import 'babel-polyfill';
import ClaimTicketEnricher from '../enrichers/claimTicketEnricher';

const claimTicketEnricher = new ClaimTicketEnricher();
const appId = 'helpdesk';
const controllerName = 'helpdesk:controller';

const registerApp = () => {
  claimTicketEnricher.init();
  return SYMPHONY.application.register(appId, ['ui', 'entity', 'extended-user-service'], [controllerName, claimTicketEnricher.name]);
};

SYMPHONY.services.register(controllerName);

SYMPHONY.remote.hello()
  .then(registerApp)
  .then(() => {
    claimTicketEnricher.register();
  })
  .fail(() => console.error('Fail to register helpdesk dynamic rendering'));
