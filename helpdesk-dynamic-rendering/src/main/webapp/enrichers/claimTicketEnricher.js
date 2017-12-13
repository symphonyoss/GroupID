import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import TicketService from '../services/ticketService';

const actions = require('../templates/claimTicketActions.hbs');
const error = require('../templates/error.hbs');

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket',
];

function renderErrorMessage(entity, messageError) {
  const data = actionFactory([], enricherServiceName, entity);

  const result = {
    template: error({ message: messageError }),
    data,
    enricherInstanceId: entity.ticketId,
  };

  return result;
}

export default class ClaimTicketEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);

    const ticketService = new TicketService(enricherServiceName);

    this.services = {
      ticketService,
    };
  }

  enrich(type, entity) {
    if (entity.ticketUrl === undefined) {
      return renderErrorMessage(entity, 'Cannot retrieve ticket state.');
    }

    return this.services.ticketService.getTicket(entity.ticketUrl).then((rsp) => {
      if (rsp.code === '204') {
        return renderErrorMessage(entity, 'Ticket not found.');
      }

      const displayName = rsp.data.agent && rsp.data.agent.displayName ? rsp.data.agent.displayName : '';
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: entity.ticketId,
        show: rsp.data.state === 'UNSERVICED',
        userName: displayName,
        streamId: entity.streamId,
      };

      const data = actionFactory([claimTicketAction], enricherServiceName, entity);

      const result = {
        template: actions({ showClaim: data.claimTicket.data.show,
          userName: data.claimTicket.data.userName }),
        data,
        enricherInstanceId: entity.ticketId,
      };

      return result;
    }).catch(() => renderErrorMessage(entity, 'Cannot retrieve ticket state.'));
  }

  action(data) {
    this.services.ticketService.claim(data).then((rsp) => {
      const entityRegistry = SYMPHONY.services.subscribe('entity');
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: rsp.ticketId,
        show: rsp.data.state === 'UNSERVICED',
        userName: rsp.data.user.displayName,
      };

      const dataUpdate = actionFactory([claimTicketAction], enricherServiceName, data.entity);
      const template = actions({ showClaim: dataUpdate.claimTicket.data.showClaim,
        userName: dataUpdate.claimTicket.data.userName });

      entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
    });
  }
}
