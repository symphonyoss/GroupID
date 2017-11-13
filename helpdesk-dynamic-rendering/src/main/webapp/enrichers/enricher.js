import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import ClaimTicketService from '../services/claimTicketService';

const actions = require('../templates/actions.hbs');

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket',
];

const entityRegistry = SYMPHONY.services.subscribe('entity');

export default class HelpDeskBotEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);

    const claimTicketService = new ClaimTicketService(enricherServiceName);

    this.services = {
      claimTicketService,
    };
  }

  enrich(type, entity) {
    this.services.claimTicketService.getTicket(entity.ticketNumber).then((rsp) => {
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: entity.ticketNumber,
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
    this.services.claimTicketService.claim(data).then((rsp) => {
      const claimTicketAction = {
        id: 'claimTicket',
        service: enricherServiceName,
        type: 'claimTicket',
        label: 'Claim',
        enricherInstanceId: rsp.ticketNumber,
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

