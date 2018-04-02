import 'babel-polyfill';
import { bootstrapService } from 'symphony-app-authentication-fe';
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
  let config;
  config.appId = appId;
  config.dependencies = ['ui', 'entity', 'extended-user-service', 'error-banner'];
  config.exportedDependencies = [controllerName, claimTicketEnricher.name,
    attachmentEnricher.name, actionClaimTicketEnricher.name, actionAttachmentEnricher.name];
  config.baseAuthenticationUrl = 'localhost:8080';

  bootstrapService.initApp(config);
};

const initApp = () => {
  SYMPHONY.services.register(controllerName);

  initEnrichers();

  initAuthentication();

  registerEnrichers();
};

initApp();
