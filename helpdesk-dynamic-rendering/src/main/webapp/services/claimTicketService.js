import { claimTicket } from '../api/apiCalls';

export default class ClaimTicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    claimTicket(data)
      .then(() => {
        // TODO
      })
      .catch((error) => {
        switch (error.message) {
          case '400': {
            // TODO need a definition of error message
            break;
          }
          case '401': {
            // TODO need a definition of error message
            break;
          }
          case '404': {
            // TODO need a definition of error message
            break;
          }
          default: {
            // TODO need a definition of error message
            break;
          }
        }
      });
  }
}
