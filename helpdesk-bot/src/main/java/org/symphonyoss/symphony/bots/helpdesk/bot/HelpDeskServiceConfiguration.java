package org.symphonyoss.symphony.bots.helpdesk.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.ai.HelpDeskAi;
import org.symphonyoss.symphony.bots.ai.HelpDeskAiSession;
import org.symphonyoss.symphony.bots.ai.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskSymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.listener.AutoConnectionAcceptListener;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check.AgentExternalCheck;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;

/**
 * Created by rsanchez on 04/12/17.
 */
@Configuration
public class HelpDeskServiceConfiguration {

  @Bean(name = "membershipClient")
  public MembershipClient getMembershipClient(HelpDeskBotConfig configuration) {
    return new MembershipClient(configuration.getGroupId(), configuration.getHelpDeskServiceUrl());
  }

  @Bean(name = "ticketClient")
  public TicketClient getTicketClient(HelpDeskBotConfig configuration) {
    return new TicketClient(configuration.getGroupId(), configuration.getHelpDeskServiceUrl());
  }

  @Bean(name = "makercheckerClient")
  public MakercheckerClient getMakercheckerClient(HelpDeskBotConfig configuration) {
    return new MakercheckerClient(configuration.getGroupId(), configuration.getHelpDeskServiceUrl());
  }

  @Bean(name = "symphonyClient")
  public SymphonyClient getSymphonyClient(HelpDeskBotConfig configuration,
      HelpDeskAuthenticationService authenticationService) throws InitException {
    SymAuth symAuth = authenticationService.authenticate();

    SymphonyClient symClient = new HelpDeskSymphonyClient();
    symClient.init(symAuth, configuration.getEmail(), configuration.getAgentUrl(), configuration.getPodUrl());

    return symClient;
  }

  @Bean(name = "helpdeskAi")
  public HelpDeskAi initHelpDeskAi(HelpDeskBotConfig configuration, MembershipClient membershipClient,
      TicketClient ticketClient, SymphonyClient symphonyClient) {
    HelpDeskAiSession helpDeskAiSession = new HelpDeskAiSession();
    helpDeskAiSession.setMembershipClient(membershipClient);
    helpDeskAiSession.setTicketClient(ticketClient);
    helpDeskAiSession.setSymphonyClient(symphonyClient);

    HelpDeskAiConfig helpDeskAiConfig = new HelpDeskAiConfig();
    helpDeskAiConfig.setGroupId(configuration.getGroupId());
    helpDeskAiConfig.setAgentStreamId(configuration.getAgentStreamId());
    helpDeskAiConfig.setCloseTicketSuccessResponse(configuration.getCloseTicketSuccessResponse());
    helpDeskAiConfig.setAddMemberAgentSuccessResponse(configuration.getAddMemberAgentSuccessResponse());
    helpDeskAiConfig.setAddMemberClientSuccessResponse(configuration.getAddMemberClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketAgentSuccessResponse(configuration.getAcceptTicketAgentSuccessResponse());
    helpDeskAiConfig.setAcceptTicketClientSuccessResponse(configuration.getAcceptTicketClientSuccessResponse());
    helpDeskAiConfig.setAcceptTicketCommand(configuration.getAcceptTicketCommand());
    helpDeskAiConfig.setCloseTicketCommand(configuration.getCloseTicketCommand());
    helpDeskAiConfig.setAddMemberCommand(configuration.getAddMemberCommand());
    helpDeskAiConfig.setDefaultPrefix(configuration.getAiDefaultPrefix());
    helpDeskAiConfig.setAgentServiceRoomPrefix(configuration.getAiServicePrefix());

    helpDeskAiSession.setHelpDeskAiConfig(helpDeskAiConfig);

    HelpDeskAi helpDeskAi = new HelpDeskAi(helpDeskAiSession);

    return helpDeskAi;
  }

  @Bean(name = "agentMakerCheckerService")
  public MakerCheckerService getAgentMakerCheckerService(HelpDeskBotConfig configuration,
      SymphonyClient symphonyClient, TicketClient ticketClient,
      MakercheckerClient makercheckerClient) {
    MakerCheckerService agentMakerCheckerService =
        new MakerCheckerService(makercheckerClient, symphonyClient);

    AgentExternalCheck agentExternalCheck =
        new AgentExternalCheck(configuration.getHelpDeskBotUrl(),
            configuration.getHelpDeskServiceUrl(), configuration.getGroupId(), ticketClient);

    agentMakerCheckerService.addCheck(agentExternalCheck);

    return  agentMakerCheckerService;
  }

  @Bean(name = "clientMakerCheckerService")
  public MakerCheckerService getClientMakerCheckerService(SymphonyClient symphonyClient,
      MakercheckerClient makercheckerClient) {
    return new MakerCheckerService(makercheckerClient, symphonyClient);
  }

  @Bean(name = "autoAcceptConnectionListener")
  public AutoConnectionAcceptListener getAutoAcceptConnectionListener(SymphonyClient symphonyClient) {
    AutoConnectionAcceptListener connectionListener =
        new AutoConnectionAcceptListener(symphonyClient.getConnectionsClient());
    symphonyClient.getMessageService().addConnectionsEventListener(connectionListener);

    return connectionListener;
  }

  @Bean(name = "validationUtil")
  public SymphonyValidationUtil getValidationUtil(SymphonyClient symphonyClient) {
    return new SymphonyValidationUtil(symphonyClient);
  }

}
