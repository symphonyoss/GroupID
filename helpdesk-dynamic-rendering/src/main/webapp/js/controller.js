import 'babel-polyfill';
import { getParameterByName } from '../utils/urlUtils';
import ClaimTicketEnricher from '../enrichers/claimTicketEnricher';
import ActionClaimTicketEnricher from '../enrichers/actionClaimTicketEnricher';
import AttachmentEnricher from '../enrichers/attachmentEnricher';
import ActionAttachmentEnricher from '../enrichers/actionAttachmentEnricher';

const claimTicketEnricher = new ClaimTicketEnricher();
const attachmentEnricher = new AttachmentEnricher();
const actionClaimTicketEnricher = new ActionClaimTicketEnricher();
const actionAttachmentEnricher = new ActionAttachmentEnricher();

const appName = getParameterByName('id');
const appId = appName || 'helpdesk';
const controllerName = `${appId}:controller`;

const registerApp = () => {
  claimTicketEnricher.init();
  attachmentEnricher.init();
  actionClaimTicketEnricher.init();
  actionAttachmentEnricher.init();

  return SYMPHONY.application.register(appId, ['ui', 'entity', 'extended-user-service', 'error-banner'], [controllerName, claimTicketEnricher.name, attachmentEnricher.name, actionClaimTicketEnricher.name, actionAttachmentEnricher.name]);
};

SYMPHONY.services.register(controllerName);

SYMPHONY.remote.hello()
  .then(registerApp)
  .then(() => {
    claimTicketEnricher.register();
    attachmentEnricher.register();
    actionClaimTicketEnricher.register();
    actionAttachmentEnricher.register();
  })
  .fail(() => console.error('Fail to register helpdesk dynamic rendering'));
