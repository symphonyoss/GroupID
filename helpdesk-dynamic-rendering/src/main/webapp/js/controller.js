import 'babel-polyfill';

const appId = 'helpdesk';
const controllerName = 'helpdesk:controller';

const registerApp = () => SYMPHONY.application.register(appId, ['ui', 'entity'], [controllerName]);

SYMPHONY.services.register(controllerName);

SYMPHONY.remote.hello()
  .then(registerApp)
  .fail(() => console.error('Fail to register helpdesk dynamic rendering'));
