import messages from './messages';

export default {
  400: messages.INVALID_REQUEST_ERROR,
  401: messages.UNAUTHORIZED_ERROR,
  404: messages.PERFORM_ACTION_ERROR,
  500: messages.GENERIC_ERROR,
};
