import { approveAttachment, denyAttachment, searchAttachment } from '../api/apiCalls';
import { errorTypes } from '../utils/errorTypes';
import { componentTypes } from '../utils/componentTypes';

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
