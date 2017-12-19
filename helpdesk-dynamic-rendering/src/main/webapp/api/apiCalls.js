import axios from 'axios';
import { getUserId } from '../utils/userUtils';

const rejectPromise = (error) => {
  const response = error.response || {};
  const status = response.status || 500;
  return Promise.reject(new Error(status));
};

export const claimTicket = (data) => {
  const apiUrl = `${data.entity.claimUrl}`;
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

export const getTicket = (ticketUrl) => {
  const apiUrl = `${ticketUrl}`;
  return axios({
    method: 'get',
    url: apiUrl,
  })
  .catch(error => rejectPromise(error));
};

export const approveAttachment = (approveUrl, messageAttachment) => {
  const apiUrl = `${approveUrl}`;
  return axios({
    method: 'post',
    url: apiUrl,
    body: {
      message: messageAttachment,
    },
  }).catch(error => rejectPromise(error));
};

export const denyAttachment = (denyUrl, messageAttachment) => {
  const apiUrl = `${denyUrl}`;
  return axios({
    method: 'post',
    url: apiUrl,
    body: {
      message: messageAttachment,
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
