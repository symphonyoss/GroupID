import { MessageEnricherBase } from 'symphony-integration-commons';
import { getUserId, getRooms } from '../utils/userUtils';
import actionFactory from '../utils/actionFactory';

const actions = require('../templates/claimTicketActions.hbs');
const base64 = require('base64-url');

const enricherServiceName = 'helpdesk-action-claim-enricher';
const updatedEnricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.ticket.accept',
];

export default class ActionClaimTicketEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);
  }

  enrich(type, entity) {
    const claimedTicket = entity.state === 'UNRESOLVED' || entity.state === 'RESOLVED';

    if (claimedTicket) {
      getUserId().then((userId) => {
        const joinConversationAction = {
          id: 'joinConversation',
          service: updatedEnricherServiceName,
          type: 'joinConversation',
          label: 'Join the conversation',
          enricherInstanceId: entity.ticketId,
          show: entity.state === 'UNSERVICED',
          userName: entity.agent.displayName,
          streamId: entity.streamId,
          userId,
        };

        getRooms().then((userRooms) => {
          let inRoom = false;
          userRooms.forEach((room) => {
            if (base64.escape(room.threadId) === entity.streamId) {
              inRoom = true;
            }
          });

          const data = actionFactory([joinConversationAction], updatedEnricherServiceName, entity);
          const entityRegistry = SYMPHONY.services.subscribe('entity');
          const displayName = entity.agent !== null ? entity.agent.displayName : '';
          const template = actions({ showClaim: false,
            resolved: entity.state === 'RESOLVED',
            userName: displayName,
            inRoom,
          });

          entityRegistry.updateEnricher(entity.ticketId, template, data);
        });
      });
    }
  }

  action() {
  }
}
