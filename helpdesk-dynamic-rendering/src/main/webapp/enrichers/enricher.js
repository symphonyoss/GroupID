import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import ClaimTicketService from '../services/claimTicketService';

const actions = require('../templates/actions.hbs');

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket',
];

export default class HelpDeskBotEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);

    const claimTicketService = new ClaimTicketService(enricherServiceName);

    this.services = {
      claimTicketService,
    };
  }

  enrich(type, entity) {
    const claimTicketAction = {
      id: 'claimTicket',
      service: enricherServiceName,
      type: 'claimTicket',
      label: 'Claim',
    };

    const data = actionFactory([claimTicketAction], enricherServiceName, entity);

    const result = {
      template: actions(),
      data,
    };

    return result;
  }

  action(data) {
    this.services.claimTicketService.claim(data);
  }
}

