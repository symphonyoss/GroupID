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

export const getTicket = (data) => {
  const apiUrl = `${data.entity.ticketUrl}`;
  return axios({
    method: 'get',
    url: apiUrl,
  })
  .catch(error => rejectPromise(error));
};

export const approveAttachment = (messageAttachment) => {
  const apiUrl = '/v1/makerchecker/accept';
  return axios({
    method: 'post',
    url: apiUrl,
    body: {
      message: messageAttachment,
    },
  }).catch(error => rejectPromise(error));
};

export const denyAttachment = (messageAttachment) => {
  const apiUrl = '/v1/makerchecker/deny';
  return axios({
    method: 'post',
    url: apiUrl,
    body: {
      message: messageAttachment,
    },
  }).catch(error => rejectPromise(error));
};

export const searchAttachment = (attachmentId) => {
  const apiUrl = `/v1/makerchecker/searchAttachment/${attachmentId}`;
  return axios({
    method: 'get',
    url: apiUrl,
  }).catch(error => rejectPromise(error));
};
