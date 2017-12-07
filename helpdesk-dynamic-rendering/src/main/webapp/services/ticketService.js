import { claimTicket, getTicket } from '../api/apiCalls';
import { errorTypes } from '../utils/errorTypes';
import { componentTypes } from '../utils/componentTypes';

export default class TicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    const errorMessageService = SYMPHONY.services.subscribe('error-banner');
    return claimTicket(data)
      .catch((error) => {
        switch (error.message) {
          case '400': {
            errorMessageService.setChatBanner(data.streamId, componentTypes.CHAT, 'Ticket or agent could not be found.', errorTypes.ERROR);
            break;
          }
          case '401': {
            errorMessageService.setChatBanner(data.streamId, componentTypes.CHAT, 'Agent is unauthorized to claim this ticket.', errorTypes.ERROR);
            break;
          }
          case '404': {
            errorMessageService.setChatBanner(data.streamId, componentTypes.CHAT, 'Ticket not found;', errorTypes.ERROR);
            break;
          }
          default: {
            errorMessageService.setChatBanner(data.streamId, componentTypes.CHAT, 'Internal server error.', errorTypes.ERROR);
            break;
          }
        }
      });
  }

  getTicket(ticketUrl) {
    return getTicket(ticketUrl);
  }
}
