package org.symphonyoss.symphony.bots.helpdesk.bot.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSessionManager;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.client.SymphonyUtilClient;
import org.symphonyoss.symphony.bots.utility.validation.ValidationUtil;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@SpringBootApplication(scanBasePackages = { "org.symphonyoss.symphony.bots.helpdesk.bot" })
@EnableSwagger2
@EnableWebMvc
@EnableConfigurationProperties(HelpDeskBotConfig.class)
public class SpringHelpDeskBotInit {

  private final HelpDeskBotConfig helpDeskBotConfig;

  private final HelpDeskBot helpDeskBot;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringHelpDeskBotInit.class, args);
  }

  public SpringHelpDeskBotInit(HelpDeskBotConfig helpDeskBotConfig, HelpDeskBot helpDeskBot) {
    this.helpDeskBotConfig = helpDeskBotConfig;
    this.helpDeskBot = helpDeskBot;

    HelpDeskBotSessionManager.setDefaultSessionManager(new HelpDeskBotSessionManager());
    HelpDeskBotSessionManager.getDefaultSessionManager().registerSession(this.helpDeskBotConfig.getGroupId(),
        this.helpDeskBot.getHelpDeskBotSession());
  }

  @Bean(name = "membershipClient")
  @Description("A membership client")
  public MembershipClient getMembershipClient() {
    return new MembershipClient(helpDeskBotConfig.getGroupId(),
        helpDeskBotConfig.getMemberServiceUrl());
  }

  @Bean(name = "ticketClient")
  @Description("A ticket client")
  public TicketClient getTicketClient() {
    return new TicketClient(helpDeskBotConfig.getGroupId(), helpDeskBotConfig.getTicketServiceUrl());
  }

  @Bean(name = "utilityClient")
  @Description("A utility client")
  public SymphonyUtilClient getUtilClient() {
    return new SymphonyUtilClient(helpDeskBot.getHelpDeskBotSession().getSymphonyClient());
  }

  @Bean(name = "validationUtil")
  @Description("A validation utility")
  public ValidationUtil getValidationUtil() {
    return new ValidationUtil();
  }

}
