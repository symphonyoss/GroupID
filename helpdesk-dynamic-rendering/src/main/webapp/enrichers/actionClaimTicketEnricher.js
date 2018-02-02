import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';

const actions = require('../templates/claimTicketActions.hbs');

const enricherServiceName = 'helpdesk-action-claim-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket.claimed',
];

export default class ActionClaimTicketEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);
  }

  enrich(type, entity) {
    const claimedTicket = entity.state === 'UNRESOLVED' || entity.state === 'RESOLVED';

    if (claimedTicket) {
      const data = actionFactory([], enricherServiceName, entity);
      const entityRegistry = SYMPHONY.services.subscribe('entity');
      const displayName = entity.agent !== null ? entity.agent.displayName : '';
      const template = actions({ showClaim: false,
        resolved: entity.state === 'RESOLVED',
        userName: displayName });

      entityRegistry.updateEnricher(entity.ticketId, template, data);
    }
  }

  action() {
  }
}
