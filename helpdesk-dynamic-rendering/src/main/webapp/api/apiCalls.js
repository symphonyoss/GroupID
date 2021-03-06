import axios from 'axios';
import { getUserJWT } from 'symphony-app-authentication-fe';

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

export const claimTicket = (data) => {
  const apiUrl = `${data.entity.claimUrl}`;
  const jwt = getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    params: {
      agentId: data.userId,
    },
  }).catch(error => rejectPromise(error));
};

export const joinConversation = (data) => {
  const apiUrl = `${data.entity.joinUrl}`;
  const jwt = getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    params: {
      agentId: data.userId,
    },
  }).catch(error => rejectPromise(error));
};

export const getTicket = (ticketUrl) => {
  const apiUrl = `${ticketUrl}`;
  const jwt = getUserJWT();
  return axios({
    method: 'get',
    url: apiUrl,
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  })
  .catch(error => rejectPromise(error));
};

export const approveAttachment = (approveUrl, userId) => {
  const apiUrl = `${approveUrl}`;
  const jwt = getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    params: {
      userId,
    },
  }).catch(error => rejectPromise(error));
};

export const denyAttachment = (denyUrl, userId) => {
  const apiUrl = `${denyUrl}`;
  const jwt = getUserJWT();
  return axios({
    method: 'post',
    url: apiUrl,
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    params: {
      userId,
    },
  }).catch(error => rejectPromise(error));
};

export const searchAttachment = (attachmentUrl) => {
  const apiUrl = `${attachmentUrl}`;
  const jwt = getUserJWT();
  return axios({
    method: 'get',
    url: apiUrl,
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  }).catch(error => rejectPromise(error));
};
