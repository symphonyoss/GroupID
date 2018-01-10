import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import errorTypes from '../utils/errorTypes';
import componentTypes from '../utils/componentTypes';
import { getMessageByCode } from '../errorMessages/messageByCode';

export default class AttachmentService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  approve(message) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;

    return approveAttachment(message.entity.approveUrl, message.userId)
    .catch((error) => {
      errorCode = parseInt(error.message, 10);
      const messageText = getMessageByCode(errorCode);
      errorMessageService.setChatBanner(message.entity.streamId, componentTypes.CHAT,
        messageText, errorTypes.ERROR);
    });
  }

  deny(message) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;

    return denyAttachment(message.entity.denyUrl, message.userId)
    .catch((error) => {
      errorCode = parseInt(error.message, 10);
      const messageText = getMessageByCode(errorCode);
      errorMessageService.setChatBanner(message.entity.streamId, componentTypes.CHAT,
        messageText, errorTypes.ERROR);
    });
  }

  search(attachmentUrl) {
    return searchAttachment(attachmentUrl);
  }

}
