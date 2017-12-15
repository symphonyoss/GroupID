import messages from './messages';

const messageByCode = {
  400: messages.INVALID_REQUEST_ERROR,
  401: messages.UNAUTHORIZED_ERROR,
  404: messages.PERFORM_ACTION_ERROR,
  500: messages.GENERIC_ERROR,
};

export const getMessageByCode = (errorCode) => {
  const message = messageByCode[errorCode];

  if (message === undefined) {
    return messages.GENERIC_ERROR;
  }

  return message;
};
