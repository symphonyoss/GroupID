import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import TicketService from '../services/ticketService';

const actions = require('../templates/actions.hbs');

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket',
];

const entityRegistry = SYMPHONY.services.subscribe('entity');

export default class HelpDeskBotEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);

    const ticketService = new TicketService(enricherServiceName);

    this.services = {
      ticketService,
    };
  }

  enrich(type, entity) {
    this.services.ticketService.getTicket(entity.ticketId).then((rsp) => {
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: entity.ticketId,
        showClaim: rsp.ticket.state === 'UNSERVICED',
        userName: '', // TODO APP-1477.
      };

      const data = actionFactory([claimTicketAction], enricherServiceName, entity);

      const result = {
        template: actions({ showClaim: data.claimTicket.data.showClaim }),
        data,
      };

      return result;
    });
  }

  action(data) {
    this.services.ticketService.claim(data).then((rsp) => {
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: rsp.ticketId,
        showClaim: rsp.ticket.state === 'UNSERVICED',
        userName: rsp.user.displayName,
      };

      const dataUpdate = actionFactory([claimTicketAction], enricherServiceName, data.entity);
      const template = actions({ showClaim: dataUpdate.claimTicket.data.showClaim,
        userName: dataUpdate.claimTicket.data.userName });

      entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
    });
  }
}

