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

export const getTicket = (ticketId) => {
  const apiUrl = `/v1/ticket/${ticketId}/get`;
  return axios({
    method: 'get',
    url: apiUrl,
  }).catch(error => rejectPromise(error));
};

export const acceptAttachment = (message) => {
  const apiUrl = `/v1/makerchecker/accept`;
  return axios({
    method: 'post',
    url: apiUrl,
    body: {
      message: message
    },
  }).catch(error => rejectPromise(error));
};

export const denyAttachment = (message) => {
  const apiUrl = `/v1/makerchecker/deny`;
  return axios({
    method: 'post',
    url: apiUrl,
    body: {
      message: message
    },
  }).catch(error => rejectPromise(error));
};
