import actionFactory from '../utils/actionFactory';

const error = require('../templates/error.hbs');

export const renderErrorMessage = (entity, messageError, enricherServiceName) => {
  const data = actionFactory([], enricherServiceName, entity);

  const result = {
    template: error({ message: messageError }),
    data,
    enricherInstanceId: entity.ticketId,
  };

  return result;
};
