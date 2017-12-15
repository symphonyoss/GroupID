package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import org.springframework.stereotype.Service;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.MessageService;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;

/**
 * Created by rsanchez on 05/12/17.
 */
@Service
public class HelpDeskSymphonyClient extends SymphonyBasicClient {

  private final HelpDeskBotConfig configuration;

  private final HelpDeskAuthenticationService authenticationService;

  private HelpDeskRoomService roomService;

  private HelpDeskMessageService messageService;

  public HelpDeskSymphonyClient(HelpDeskAuthenticationService authenticationService,
      HelpDeskBotConfig configuration) throws InitException {
    this.authenticationService = authenticationService;
    this.configuration = configuration;
  }

  public void init() throws InitException {
    String email = configuration.getEmail();
    String agentUrl = configuration.getAgentUrl();
    String podUrl = configuration.getPodUrl();

    validateAgentUrl(agentUrl);
    validatePodUrl(podUrl);

    SymAuth symAuth = authenticationService.authenticate();
    init(symAuth, email, agentUrl, podUrl);
  }

  @Override
  public void init(SymAuth symAuth, String email, String agentUrl, String podUrl) throws InitException {
    validateAuthentication(symAuth);

    SymphonyClientConfig config = buildConfig(email, agentUrl, podUrl);

    super.init(symAuth, config);

    buildRoomService(symAuth, config);
    buildMessageService();
  }

  private void validateAgentUrl(String agentUrl) throws InitException {
    if (agentUrl == null) {
      throw new InitException("Failed to provide agent URL");
    }
  }

  private void validatePodUrl(String podUrl) throws InitException {
    if (podUrl == null) {
      throw new InitException("Failed to provide service URL");
    }
  }

  private void validateAuthentication(SymAuth symAuth) throws InitException {
    if (symAuth == null || symAuth.getSessionToken() == null || symAuth.getKeyToken() == null) {
      throw new InitException("Symphony Authorization is not valid. Currently not logged into "
          + "Agent, please check certificates and tokens.");
    }
  }

  private SymphonyClientConfig buildConfig(String email, String agentUrl, String podUrl) {
    SymphonyClientConfig config = new SymphonyClientConfig(false);
    config.set(SymphonyClientConfigID.AGENT_URL, agentUrl);
    config.set(SymphonyClientConfigID.POD_URL, podUrl);
    config.set(SymphonyClientConfigID.USER_EMAIL, email);
    config.set(SymphonyClientConfigID.DISABLE_SERVICES, Boolean.TRUE.toString());

    return config;
  }

  private void buildRoomService(SymAuth symAuth, SymphonyClientConfig config) {
    this.roomService = new HelpDeskRoomService(this, symAuth, config);
  }

  private void buildMessageService() {
    this.messageService = new HelpDeskMessageService(this);
  }

  @Override
  public RoomService getRoomService() {
    return this.roomService;
  }

  @Override
  public MessageService getMessageService() {
    return this.messageService;
  }

}
