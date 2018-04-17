import 'babel-polyfill';
import { initApp } from 'symphony-app-authentication-fe';
import { getParameterByName } from '../utils/urlUtils';
import ClaimTicketEnricher from '../enrichers/claimTicketEnricher';
import ActionClaimTicketEnricher from '../enrichers/actionClaimTicketEnricher';
import AttachmentEnricher from '../enrichers/attachmentEnricher';
import ActionAttachmentEnricher from '../enrichers/actionAttachmentEnricher';

const appName = getParameterByName('id');
const authenticationURL = getParameterByName('baseAuthenticationURL');
const appId = appName || 'helpdesk';
const controllerName = `${appId}:controller`;

const claimTicketEnricher = new ClaimTicketEnricher(appId);
const attachmentEnricher = new AttachmentEnricher(appId);
const actionClaimTicketEnricher = new ActionClaimTicketEnricher(appId);
const actionAttachmentEnricher = new ActionAttachmentEnricher(appId);

const initEnrichers = () => {
  claimTicketEnricher.init();
  attachmentEnricher.init();
  actionClaimTicketEnricher.init();
  actionAttachmentEnricher.init();
};

const registerEnrichers = () => {
  claimTicketEnricher.register();
  attachmentEnricher.register();
  actionClaimTicketEnricher.register();
  actionAttachmentEnricher.register();
};

const initAuthentication = () => {
  const config = {
    appId,
    dependencies: ['ui', 'entity', 'extended-user-service', 'error-banner'],
    exportedDependencies: [controllerName, claimTicketEnricher.name, attachmentEnricher.name,
      actionClaimTicketEnricher.name, actionAttachmentEnricher.name],
    baseAuthenticationUrl: authenticationURL,
  };

  return initApp(config);
};

const initApplication = () => {
  SYMPHONY.services.register(controllerName);

  initEnrichers();

  initAuthentication()
    .then(() => registerEnrichers())
    .fail(e => console.error(e));
};

initApplication();
