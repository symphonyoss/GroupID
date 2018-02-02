import 'babel-polyfill';
import ClaimTicketEnricher from '../enrichers/claimTicketEnricher';
import AttachmentEnricher from '../enrichers/attachmentEnricher';
import ActionAttachmentEnricher from '../enrichers/actionAttachmentEnricher';

const claimTicketEnricher = new ClaimTicketEnricher();
const attachmentEnricher = new AttachmentEnricher();
const actionAttachmentEnricher = new ActionAttachmentEnricher();
const appId = 'helpdesk';
const controllerName = 'helpdesk:controller';

const registerApp = () => {
  claimTicketEnricher.init();
  attachmentEnricher.init();
  actionAttachmentEnricher.init();
  return SYMPHONY.application.register(appId, ['ui', 'entity', 'extended-user-service', 'error-banner'], [controllerName, claimTicketEnricher.name, attachmentEnricher.name, actionAttachmentEnricher.name]);
};

SYMPHONY.services.register(controllerName);

SYMPHONY.remote.hello()
  .then(registerApp)
  .then(() => {
    claimTicketEnricher.register();
    attachmentEnricher.register();
    actionAttachmentEnricher.register();
  })
  .fail(() => console.error('Fail to register helpdesk dynamic rendering'));
