import MessageEnricher from './messageEnricher';
import actionFactory from '../utils/actionFactory';
import TicketService from '../services/ticketService';
import { getUserId, getRooms } from '../utils/userUtils';
import { renderErrorMessage } from '../utils/errorMessage';

const actions = require('../templates/claimTicketActions.hbs');
const base64 = require('base64-url');

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket',
];

function TicketException(messageException) {
  this.messageException = messageException;
  this.name = 'ticketException';
}

export default class ClaimTicketEnricher extends MessageEnricher {
  constructor(application) {
    super(enricherServiceName, messageEvents, application);

    const ticketService = new TicketService(enricherServiceName);

    this.services = {
      ticketService,
    };
  }

  enrich(type, entity) {
    if (entity.ticketUrl === undefined) {
      return renderErrorMessage(entity, 'Cannot retrieve ticket state.', enricherServiceName);
    }

    let ticket;

    return this.services.ticketService.getTicket(entity.ticketUrl).then((rsp) => {
      if (rsp.code === '204') {
        throw new TicketException('Ticket not found.');
      }

      ticket = rsp.data;
      return Promise.all([getUserId(), getRooms()]);
    }).then(userInfo =>
      this.showTicketRenderer(entity, ticket, userInfo[0], userInfo[1])
    ).catch((e) => {
      if (e.messageException === undefined) {
        return renderErrorMessage(entity, 'Cannot retrieve ticket state.', enricherServiceName);
      }

      return renderErrorMessage(entity, e.messageException, enricherServiceName);
    });
  }

  showTicketRenderer(entity, ticket, userId, userRooms) {
    const agent = ticket.agent;
    const displayName = agent && agent.displayName ? agent.displayName : '';

    const claimTicketAction = {
      id: 'claimTicket',
      service: enricherServiceName,
      type: 'claimTicket',
      label: 'Claim',
      enricherInstanceId: entity.ticketId,
      show: ticket.state === 'UNSERVICED',
      userName: displayName,
      streamId: entity.streamId,
      userId,
    };

    const actionObjs = [claimTicketAction];

    const joinConversationAction = {
      id: 'joinConversation',
      service: enricherServiceName,
      type: 'joinConversation',
      label: 'Join the conversation',
      enricherInstanceId: entity.ticketId,
      show: ticket.state === 'UNRESOLVED',
      userName: displayName,
      streamId: entity.streamId,
      userId,
    };

    actionObjs.push(joinConversationAction);

    let isTicketRoomMember = false;
    userRooms.forEach((room) => {
      if (base64.escape(room.threadId) === ticket.serviceStreamId) {
        isTicketRoomMember = true;
      }
    });

    const data = actionFactory(actionObjs, enricherServiceName, entity);

    const result = {
      template: actions({ showClaim: data.claimTicket.data.show,
        userName: data.claimTicket.data.userName,
        resolved: ticket.state === 'RESOLVED',
        isTicketRoomMember,
      }),
      data,
      enricherInstanceId: entity.ticketId,
    };

    return result;
  }

  action(data) {
    if (data.type === 'claimTicket') {
      this.claim(data);
    } else if (data.type === 'joinConversation') {
      this.join(data);
    }
  }

  claim(data) {
    this.services.ticketService.claim(data).then((rsp) => {
      const entityRegistry = SYMPHONY.services.subscribe('entity');
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: rsp.data.ticketId,
        show: rsp.data.state === 'UNSERVICED',
        userName: rsp.data.user.displayName,
      };

      const dataUpdate = actionFactory([claimTicketAction], enricherServiceName, data.entity);
      const template = actions({ showClaim: dataUpdate.claimTicket.data.show,
        resolved: rsp.data.state === 'RESOLVED',
        userName: dataUpdate.claimTicket.data.userName,
        isTicketRoomMember: true });

      entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
    });
  }

  join(data) {
    this.services.ticketService.join(data).then((rsp) => {
      const entityRegistry = SYMPHONY.services.subscribe('entity');
      const joinConversationAction = {
        id: 'joinConversation',
        service: enricherServiceName,
        type: 'joinConversation',
        label: 'Join the conversation',
        enricherInstanceId: rsp.data.ticketId,
        show: rsp.data.state === 'UNRESOLVED',
        userName: rsp.data.user.displayName,
      };

      const dataUpdate = actionFactory([joinConversationAction], enricherServiceName, data.entity);
      const template = actions({ showClaim: false,
        resolved: rsp.data.state === 'RESOLVED',
        userName: dataUpdate.joinConversation.data.userName,
        isTicketRoomMember: true });

      entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
    });
  }
}
