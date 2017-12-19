import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import errorTypes from '../utils/errorTypes';
import componentTypes from '../utils/componentTypes';
import { getMessageByCode } from '../errorMessages/messageByCode';

function createMessageAttachment(message) {
  const messageAttachment = {
    streamId: message.entity.streamId !== undefined ? message.entity.streamId : null,
    proxyToStreamIds: message.entity.proxyToStreamIds !== undefined ?
      message.entity.proxyToStreamIds : null,
    timeStamp: message.entity.timestamp !== undefined ? message.entity.timestamp : null,
    messageId: message.entity.messageId !== undefined ? message.entity.messageId : null,
    groupId: message.entity.groupId !== undefined ? message.entity.groupId : null,
    userId: message.userId !== undefined ? message.userId : null,
    attachmentId: message.entity.attachmentId !== undefined ? message.entity.attachmentId : null,
  };

  return messageAttachment;
}

export default class AttachmentService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  approve(message) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;
    return approveAttachment(message.approveUrl, createMessageAttachment(message))
      .catch((error) => {
        errorCode = parseInt(error.message, 10);
        const messageText = getMessageByCode(errorCode);
        errorMessageService.setChatBanner(message.streamId, componentTypes.CHAT,
          messageText, errorTypes.ERROR);
      });
  }

  deny(message) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;

    return denyAttachment(message.denyUrl, createMessageAttachment(message))
    .catch((error) => {
      errorCode = parseInt(error.message, 10);
      const messageText = getMessageByCode(errorCode);
      errorMessageService.setChatBanner(message.streamId, componentTypes.CHAT,
        messageText, errorTypes.ERROR);
    });
  }

  search(attachmentUrl) {
    return searchAttachment(attachmentUrl);
  }

}
