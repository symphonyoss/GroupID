import { claimTicket, getTicket } from '../api/apiCalls';

export default class TicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    return claimTicket(data);
  }

  getTicket(ticketUrl) {
    return getTicket(ticketUrl);
  }
}
