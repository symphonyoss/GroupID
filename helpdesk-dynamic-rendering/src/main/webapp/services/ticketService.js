import { claimTicket, getTicket } from '../api/apiCalls';
import errorTypes from '../utils/errorTypes';
import componentTypes from '../utils/componentTypes';
import { getMessageByCode } from '../errorMessages/messageByCode';

export default class TicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    let errorCode;
    return claimTicket(data)
    .catch((error) => {
      errorCode = parseInt(error.message, 10);
      const messageText = getMessageByCode(errorCode);
      errorMessageService.setChatBanner(data.entity.streamId, componentTypes.CHAT,
        messageText, errorTypes.ERROR);
    });
  }

  getTicket(ticketUrl) {
    return getTicket(ticketUrl);
  }
}
