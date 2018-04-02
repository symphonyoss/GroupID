import axios from 'axios';
import UserService from 'symphony-app-authentication-fe';

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

const userService = new UserService();

export const claimTicket = (data) => {
  const apiUrl = `${data.entity.claimUrl}`;
  const jwt = userService.getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer, ${jwt}`,
    },
    params: {
      agentId: data.userId,
    },
  }).catch(error => rejectPromise(error));
};

export const joinConversation = (data) => {
  const apiUrl = `${data.entity.joinUrl}`;
  const jwt = userService.getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer, ${jwt}`,
    },
    params: {
      agentId: data.userId,
    },
  }).catch(error => rejectPromise(error));
};

export const getTicket = (ticketUrl) => {
  const apiUrl = `${ticketUrl}`;
  const jwt = userService.getUserJWT();
  return axios({
    method: 'get',
    url: apiUrl,
    headers: {
      Authorization: `Bearer, ${jwt}`,
    },
  })
  .catch(error => rejectPromise(error));
};

export const approveAttachment = (approveUrl, userId) => {
  const apiUrl = `${approveUrl}`;
  const jwt = userService.getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer, ${jwt}`,
    },
    params: {
      userId,
    },
  }).catch(error => rejectPromise(error));
};

export const denyAttachment = (denyUrl, userId) => {
  const apiUrl = `${denyUrl}`;
  const jwt = userService.getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer, ${jwt}`,
    },
    params: {
      userId,
    },
  }).catch(error => rejectPromise(error));
};

export const searchAttachment = (attachmentUrl) => {
  const apiUrl = `${attachmentUrl}`;
  const jwt = userService.getUserJWT();
  return axios({
    method: 'get',
    url: apiUrl,
    headers: {
      Authorization: `Bearer, ${jwt}`,
    },
  }).catch(error => rejectPromise(error));
};
