import axios from 'axios';

export const calimTicket = () => {
    const apiUrl = ``;
  
    const params = {
      url,
    };
  
    return axios.get(apiUrl, {
      params,
    }).catch(error => rejectPromise(error));
  };