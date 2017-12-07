import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import { errorTypes } from '../utils/errorTypes';
import { componentTypes } from '../utils/componentTypes';
import messageByCode from '../errorMessages/messageByCode';
import messages from '../errorMessages/messages';

export default class AttachmentService {
  constructor(serviceName) {
    this.serviceName = serviceName;
    this.errorBanner = SYMPHONY.services.subscribe('error-banner');
  }

  approve(message) {
    return approveAttachment(message)
      .catch((error) => {
        const messageText = error.code ? messageByCode[error.code]
          : messages.PERFORM_ACTION_ERROR;
        this.errorBanner.setChatBanner(message.streamId, componentTypes.CHAT,
          messageText, errorTypes.ERROR);
      });
  }

  deny(message) {
    return denyAttachment(message)
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
