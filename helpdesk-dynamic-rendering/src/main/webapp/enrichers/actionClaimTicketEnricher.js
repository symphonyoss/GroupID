import { MessageEnricherBase } from 'symphony-integration-commons';
import { getUserId, getRooms } from '../utils/userUtils';
import actionFactory from '../utils/actionFactory';
import { renderErrorMessage } from '../utils/errorMessage';

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
        const displayName = entity.agent !== null && entity.agent !== undefined ? entity.agent.displayName : '';
        const joinConversationAction = {
          id: 'joinConversation',
          service: updatedEnricherServiceName,
          type: 'joinConversation',
          label: 'Join the conversation',
          enricherInstanceId: entity.ticketId,
          show: entity.state === 'UNRESOLVED',
          userName: displayName,
          streamId: entity.streamId,
          userId,
        };
        console.log('wtf?');

        getRooms().then((userRooms) => {
          let isTicketRoomMember = false;
          userRooms.forEach((room) => {
            if (base64.escape(room.threadId) === entity.streamId) {
              isTicketRoomMember = true;
            }
          });

          const data = actionFactory([joinConversationAction], updatedEnricherServiceName, entity);
          const entityRegistry = SYMPHONY.services.subscribe('entity');
          const template = actions({ showClaim: false,
            resolved: entity.state === 'RESOLVED',
            userName: displayName,
            isTicketRoomMember,
          });

          entityRegistry.updateEnricher(entity.ticketId, template, data);
        }).catch((e) => {
          if (e.messageException === undefined) {
            return renderErrorMessage(entity, 'Could not get rooms for this user.', updatedEnricherServiceName);
          }
          return renderErrorMessage(entity, e.messageException, updatedEnricherServiceName);
        });
      }).catch((e) => {
        if (e.messageException === undefined) {
          return renderErrorMessage(entity, 'Could not find this user.', updatedEnricherServiceName);
        }
        return renderErrorMessage(entity, e.messageException, updatedEnricherServiceName);
      });
    }
  }

  action() {
  }
}
