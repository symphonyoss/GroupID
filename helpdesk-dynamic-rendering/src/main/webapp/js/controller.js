import 'babel-polyfill';
import HelpDeskBotEnricher from '../enrichers/enricher';

const enricher = new HelpDeskBotEnricher();
const appId = 'helpdesk';
const controllerName = 'helpdesk:controller';

const registerApp = () => {
  enricher.init();
  return SYMPHONY.application.register(appId, ['ui', 'entity'], [controllerName, enricher.name]);
};

SYMPHONY.services.register(controllerName);

SYMPHONY.remote.hello()
  .then(registerApp)
  .then(() => {
    enricher.register();
  })
  .fail(() => console.error('Fail to register helpdesk dynamic rendering'));
