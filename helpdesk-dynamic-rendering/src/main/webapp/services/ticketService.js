import { claimTicket, getTicket } from '../api/apiCalls';

export default class TicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    return claimTicket(data)
      .catch((error) => {
        switch (error.message) {
          case '400': {
            // TODO APP-1455
            break;
          }
          case '401': {
            // TODO APP-1455
            break;
          }
          case '404': {
            // TODO APP-1455
            break;
          }
          default: {
            // TODO APP-1455
            break;
          }
        }
      });
  }

  getTicket(ticketUrl) {
    return getTicket(ticketUrl);
  }
}
