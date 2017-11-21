import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import AttachmentService from '../services/attachmentService';

const actions = require('../templates/attachmentActions.hbs');

const enricherServiceName = 'helpdesk-attachment-enricher';
const messageEvents = [
  'org.symphonyoss.helpdesk.makerCheckerMessage',
];

export default class AttachmentEnricher extends MessageEnricherBase {
  constructor() {
    super(enricherServiceName, messageEvents);

    const attachmentService = new AttachmentService(enricherServiceName);

    this.services = {
      attachmentService,
    };
  }

  enrich(type, entity) {
    const approveAttachmentAction = {
      id: 'approveAttachment',
      service: enricherServiceName,
      type: 'approveAttachment',
      label: 'Approve',
      enricherInstanceId: entity.attachmentId,
    };

    const denyAttachmentAction = {
      id: 'denyAttachment',
      service: enricherServiceName,
      type: 'denyAttachment',
      label: 'Deny',
      enricherInstanceId: entity.attachmentId,
    };

    const data = actionFactory([approveAttachmentAction, denyAttachmentAction],
      enricherServiceName, entity);

    const result = {
      template: actions({ showButtons: true }),
      data,
      enricherInstanceId: entity.attachmentId,
    };

    return result;
  }

  action(data) {
    const entityRegistry = SYMPHONY.services.subscribe('entity');
    const dataUpdate = actionFactory([], enricherServiceName, data.entity);
    const template = actions({ showButtons: false });

    if (data.type === 'approveAttachment') {
      this.services.attachmentService.approve(data.entity).then(() => {
        entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
      });
    }

    if (data.type === 'denyAttachment') {
      this.services.attachmentService.deny(data.entity).then(() => {
        entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
      });
    }
  }
}

