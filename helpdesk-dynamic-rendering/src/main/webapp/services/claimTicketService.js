import { getIntegrationBaseUrl } from 'symphony-integration-commons';
import { claimTicket } from '../api/apiCalls';

export default class ClaimTicketService {
  constructor(serviceName) {
    this.serviceName = serviceName;
  }

  claim(data) {
    claimTicket(data)
      .then(() => {
      })
      .catch((error) => {
        switch (error.message) {
          case '400': {
            break;
          }
          case '401': {
            break;
          }
          case '404': {
            break;
          }
          default: {
            break;
          }
        }
      });
  }
}
