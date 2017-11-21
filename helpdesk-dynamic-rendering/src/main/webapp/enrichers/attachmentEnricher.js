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
    this.services.attachmentService.searchAttachment(entity).then((rsp) => {
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
      const displayName = rsp.user.displayName ? rsp.user.displayName : '';

      const result = {
        template: actions({ stateAttachment: rsp.state, userName: displayName }),
        data,
        enricherInstanceId: entity.attachmentId,
      };

      return result;
    });
  }

  action(data) {
    const entityRegistry = SYMPHONY.services.subscribe('entity');
    const dataUpdate = actionFactory([], enricherServiceName, data.entity);

    if (data.type === 'approveAttachment') {
      this.services.attachmentService.approve(data.entity).then((rsp) => {
        const displayName = rsp.message.makerCheckerMessageDetail.user.displayName;
        const template = actions({ stateAttachment: 'Approved', userName: displayName });

        entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
      });
    }

    if (data.type === 'denyAttachment') {
      this.services.attachmentService.deny(data.entity).then((rsp) => {
        const displayName = rsp.message.makerCheckerMessageDetail.user.displayName;
        const template = actions({ stateAttachment: 'Denied', userName: displayName });

        entityRegistry.updateEnricher(data.enricherInstanceId, template, dataUpdate);
      });
    }
  }
}

