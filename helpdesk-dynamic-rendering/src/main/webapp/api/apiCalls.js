import axios from 'axios';
import { getIntegrationBaseUrl } from 'symphony-integration-commons';

const baseUrl = getIntegrationBaseUrl();

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

export const claimTicket = (data) => {
  const id = data.entity.id;
  const apiUrl = `${baseUrl}/v1/ticket/${id}`;
  return axios({
    method: 'post',
    url: apiUrl,
    params: {
      url,
    },
    data: {
      body: '',
    },
  }).catch(error => rejectPromise(error));
};
