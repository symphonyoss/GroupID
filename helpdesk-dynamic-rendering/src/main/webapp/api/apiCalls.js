import axios from 'axios';

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

const extendedUserService = SYMPHONY.services.subscribe('extended-user-service');

export const claimTicket = (data) => {
  const ticketId = data.entity.id;
  const apiUrl = `${data.entity.url}/${ticketId}`;
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      userId: extendedUserService.getUserId,
    },
  }).catch(error => rejectPromise(error));
};
