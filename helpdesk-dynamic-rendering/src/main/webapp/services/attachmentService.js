import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import errorTypes from '../utils/errorTypes';
import componentTypes from '../utils/componentTypes';
import messages from '../errorMessages/messages';

export default class AttachmentService {
  constructor(serviceName) {
    this.serviceName = serviceName;
    this.errorBanner = SYMPHONY.services.subscribe('error-banner');
  }

  approve(message) {
    return approveAttachment(message)
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

  deny(message) {
    return denyAttachment(message)
      .catch((error) => {
        errorCode = parseInt(error.message, 10);
        const messageText = error.message ? messageByCode[errorCode] : messages.GENERIC_ERROR;
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
