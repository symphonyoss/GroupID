import axios from 'axios';

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

export const claimTicket = (data) => {
  const apiUrl = `${data.entity.claimUrl}`;
  return axios({
    method: 'post',
    url: apiUrl,
    params: {
      agentId: data.userId,
    },
  }).catch(error => rejectPromise(error));
};

export const joinConversation = (data) => {
  const apiUrl = `${data.entity.joinUrl}`;
  return axios({
    method: 'post',
    url: apiUrl,
    params: {
      agentId: data.userId,
    },
  }).catch(error => rejectPromise(error));
};

export const getTicket = (ticketUrl) => {
  const apiUrl = `${ticketUrl}`;
  return axios({
    method: 'get',
    url: apiUrl,
  })
  .catch(error => rejectPromise(error));
};

export const approveAttachment = (approveUrl, userId) => {
  const apiUrl = `${approveUrl}`;
  return axios({
    method: 'post',
    url: apiUrl,
    params: {
      userId,
    },
  }).catch(error => rejectPromise(error));
};

export const denyAttachment = (denyUrl, userId) => {
  const apiUrl = `${denyUrl}`;
  return axios({
    method: 'post',
    url: apiUrl,
    params: {
      userId,
    },
  }).catch(error => rejectPromise(error));
};

export const searchAttachment = (attachmentUrl) => {
  const apiUrl = `${attachmentUrl}`;
  return axios({
    method: 'get',
    url: apiUrl,
  }).catch(error => rejectPromise(error));
};
