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
      enricherInstanceId: 12345,
      showClaim: true,
    };

    const data = actionFactory([claimTicketAction], enricherServiceName, entity);

    const result = {
      template: actions({ showClaim: data.claimTicket.data.showClaim }),
      data,
    };

    return result;
  }

  action(data) {
    this.services.claimTicketService.claim(data);

    const entityRegistry = SYMPHONY.services.subscribe('entity');

    const claimTicketAction = {
      id: 'claimTicket',
      service: enricherServiceName,
      type: 'claimTicket',
      label: 'Claim',
      enricherInstanceId: '12345',
      showClaim: false,
      userName: 'Cassiano',
    };

    const dataUpdate = actionFactory([claimTicketAction], enricherServiceName, data.entity);
    const template = actions({ showClaim: dataUpdate.claimTicket.data.showClaim,
      userName: dataUpdate.claimTicket.data.userName });

    entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
  }
}

