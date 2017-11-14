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

  getTicket(ticketId) {
    return getTicket(ticketId)
      .catch((error) => {
        switch (error.message) {
          // TODO APP-1477 To map all errors from API
          case '': {
            break;
          }
          default: {
            // TODO APP-1477
            break;
          }
        }
      });
  }
}
