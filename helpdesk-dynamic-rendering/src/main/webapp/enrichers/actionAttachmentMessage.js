import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';

const actions = require('../templates/attachmentActions.hbs');

const enricherServiceName = 'helpdesk-action-attachment-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.makerchecker.action.performed',
];

export default class ActionAttachmentEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);

    const actionAttachmentService = new ActionAttachmentService(enricherServiceName);

    this.services = {
      actionAttachmentService,
    };
  }

  enrich(type, entity) {
    const actionPerformed = entity.state === 'APPROVED' || entity.state === 'DENIED';
        
    if (actionPerformed) {
      const data = actionFactory([], enricherServiceName, entity);
      const entityRegistry = SYMPHONY.services.subscribe('entity');
      const displayName = entity.checker !== null ? rsp.checker.displayName : '';
      const result = {
        template: actions({ showActions: false,
        isApproved: entity.state === 'APPROVED',
        userName: displayName }),
        data,
        enricherInstanceId: entity.makerCheckerId,
      };
    
      entityRegistry.updateEnricher(data.enricherInstanceId, template, data);
    }
  }
}