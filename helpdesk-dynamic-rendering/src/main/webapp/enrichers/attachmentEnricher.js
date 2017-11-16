import { MessageEnricherBase } from 'symphony-integration-commons';
import actionFactory from '../utils/actionFactory';
import AttachmentService from '../services/attachmentService';

const actions = require('../templates/attachmentActions.hbs');

const enricherServiceName = 'helpdesk-enricher';
const messageEvents = [
  'com.symphony.markdown',
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
    };

    const denyAttachmentAction = {
      id: 'denyAttachment',
      service: enricherServiceName,
      type: 'denyAttachment',
      label: 'Deny',
    };

    const data = actionFactory([approveAttachmentAction, denyAttachmentAction],
      enricherServiceName, entity);

    const result = {
      template: actions(),
      data,
    };

    return result;
  }
}

