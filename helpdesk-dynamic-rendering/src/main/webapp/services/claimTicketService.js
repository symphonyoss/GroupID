import { claimTicket } from '../api/apiCalls';

export default class ClaimTicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    claimTicket(data)
      .then(() => {
        // TODO APP-1349
      })
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
}
