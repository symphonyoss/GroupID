import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import errorTypes from '../utils/errorTypes';
import componentTypes from '../utils/componentTypes';
import messageByCode from '../errorMessages/messageByCode';
import messages from '../errorMessages/messages';
import { getUserId } from '../utils/userUtils';

function createMessageAttachment(message) {
  const messageAttachment = {
    streamId: message.streamId,
    proxyToStreamIds: message.proxyToStreamId,
    timeStamp: message.timestamp,
    messageId: message.messageId,
    groupId: message.groupId,
    userId: message.userId,
    attachmentId: message.attachmentId,
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
    return approveAttachment(createMessageAttachment(message))
      .catch((error) => {
        errorCode = parseInt(error.message, 10);
        const messageText = error.message ? messageByCode[errorCode]
          : messages.GENERIC_ERROR;
        errorMessageService.setChatBanner(message.streamId, componentTypes.CHAT,
          messageText, errorTypes.ERROR);
      });
  }

  deny(message) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;

    return denyAttachment(createMessageAttachment(message))
    .catch((error) => {
      errorCode = parseInt(error.message, 10);
      const messageText = error.message ? messageByCode[errorCode]
        : messages.GENERIC_ERROR;
      errorMessageService.setChatBanner(message.streamId, componentTypes.CHAT,
        messageText, errorTypes.ERROR);
    });
  }

  search(attachmentId) {
    return searchAttachment(attachmentId)
      .catch((error) => {
        switch (error.message) {
          case '': {
            break;
          }
          default: {
            break;
          }
        }
      });
  }

}
