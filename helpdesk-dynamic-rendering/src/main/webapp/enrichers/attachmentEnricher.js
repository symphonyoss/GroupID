import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import AttachmentService from '../services/attachmentService';
import { getUserId } from '../utils/userUtils';

const actions = require('../templates/attachmentActions.hbs');

const enricherServiceName = 'helpdesk-attachment-enricher';
const messageEvents = [
  'com.symphony.bots.helpdesk.event.makerchecker',
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
    let attachment;
    return this.services.attachmentService.search(entity.attachmentUrl).then((rsp) => {
      attachment = rsp;
      return getUserId();
    }).then(userId => this.showAttachmentsRender(entity, attachment, userId));
  }

  showAttachmentsRender(entity, rsp, userId) {
    const show = userId === entity.makerId;
    const approveAttachmentAction = {
      id: 'approveAttachment',
      service: enricherServiceName,
      type: 'approveAttachment',
      label: 'Approve',
      enricherInstanceId: entity.attachmentId,
      streamId: entity.streamId,
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
    const canPerformActions = rsp.state !== 'Approved' && rsp.state !== 'Denied';
    const displayName = rsp.user.displayName ? rsp.user.displayName : '';
    const result = {
      template: actions({ showActions: canPerformActions,
        showButtons: !show,
        body: 'You cannot approve a message you authored, please invite a checker',
        isApproved: rsp.state === 'Approved',
        userName: displayName }),
      data,
      enricherInstanceId: entity.attachmentId,
    };
    return result;
  }

  action(data) {
    const entityRegistry = SYMPHONY.services.subscribe('entity');
    const dataUpdate = actionFactory([], enricherServiceName, data.entity);

    if (data.type === 'approveAttachment') {
      this.services.attachmentService.approve(data.entity).then((rsp) => {
        const displayName = rsp.message.makerCheckerMessageDetail.user.displayName;
        const templateApproved = actions({ showActions: false,
          isApproved: true,
          userName: displayName });

        entityRegistry.updateEnricher(data.enricherInstanceId, templateApproved, dataUpdate);
      });
    }

    if (data.type === 'denyAttachment') {
      this.services.attachmentService.deny(data.entity).then((rsp) => {
        const displayName = rsp.message.makerCheckerMessageDetail.user.displayName;
        const templateDeny = actions({ showActions: false,
          isApproved: false,
          userName: displayName });

        entityRegistry.updateEnricher(data.enricherInstanceId, templateDeny, dataUpdate);
      });
    }
  }
}
