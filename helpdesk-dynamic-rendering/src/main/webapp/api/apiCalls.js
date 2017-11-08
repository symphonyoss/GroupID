import axios from 'axios';

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

export const getUserId = () => {
  const extendedUserService = SYMPHONY.services.subscribe('extended-user-service');
  return extendedUserService.getUserId();
};

export const claimTicket = (data) => {
  const apiUrl = `${data.entity.url}`;
  return getUserId().then((userId) => {
    const idUser = userId;
    return axios({
      method: 'post',
      url: apiUrl,
      headers: {
        agentId: idUser,
      },
    });
  }).catch(error => rejectPromise(error));
};
