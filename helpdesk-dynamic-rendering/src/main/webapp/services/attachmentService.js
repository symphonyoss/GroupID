import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import errorTypes from '../utils/errorTypes';
import componentTypes from '../utils/componentTypes';
<<<<<<< HEAD
import messageByCode from '../errorMessages/messageByCode';
=======
>>>>>>> 5af3fd0b5be79f1a0abf5a1914c52c88d380189d
import messages from '../errorMessages/messages';

export default class AttachmentService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  approve(message) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;
    return approveAttachment(message)
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
    return denyAttachment(message)
      .catch((error) => {
        errorCode = parseInt(error.message, 10);
<<<<<<< HEAD
        const messageText = error.message ? messageByCode[errorCode]
          : messages.GENERIC_ERROR;
=======
        const messageText = error.message ? messageByCode[errorCode] : messages.GENERIC_ERROR;
>>>>>>> 5af3fd0b5be79f1a0abf5a1914c52c88d380189d
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
