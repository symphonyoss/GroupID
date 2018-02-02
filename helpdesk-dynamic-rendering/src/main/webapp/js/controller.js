import 'babel-polyfill';
import ClaimTicketEnricher from '../enrichers/claimTicketEnricher';
import ActionClaimTicketEnricher from '../enrichers/actionClaimTicketEnricher';
import AttachmentEnricher from '../enrichers/attachmentEnricher';

const claimTicketEnricher = new ClaimTicketEnricher();
const attachmentEnricher = new AttachmentEnricher();
const actionClaimTicketEnricher = new ActionClaimTicketEnricher();
const appId = 'helpdesk';
const controllerName = 'helpdesk:controller';

const registerApp = () => {
  claimTicketEnricher.init();
  attachmentEnricher.init();
  return SYMPHONY.application.register(appId, ['ui', 'entity', 'extended-user-service', 'error-banner'], [controllerName, claimTicketEnricher.name, attachmentEnricher.name, actionClaimTicketEnricher.name]);
};

SYMPHONY.services.register(controllerName);

SYMPHONY.remote.hello()
  .then(registerApp)
  .then(() => {
    claimTicketEnricher.register();
    attachmentEnricher.register();
    actionClaimTicketEnricher.register();
  })
  .fail(() => console.error('Fail to register helpdesk dynamic rendering'));
