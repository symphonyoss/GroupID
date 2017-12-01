package org.symphonyoss.symphony.bots.helpdesk.bot.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.bot.authentication.HelpDeskAuthenticationService;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSessionManager;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@SpringBootApplication(scanBasePackages = { "org.symphonyoss.symphony.bots.helpdesk.bot",
    "org.symphonyoss.symphony.bots.helpdesk.messageproxy" })
@EnableSwagger2
@EnableWebMvc
public class SpringHelpDeskBotInit {

  private final HelpDeskBotConfig configuration;

  private final HelpDeskBot helpDeskBot;

  private final HelpDeskAuthenticationService authenticationService;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringHelpDeskBotInit.class, args);
  }

  public SpringHelpDeskBotInit(HelpDeskBotConfig configuration, HelpDeskBot helpDeskBot,
      HelpDeskAuthenticationService authenticationService) {
    this.configuration = configuration;
    this.helpDeskBot = helpDeskBot;
    this.authenticationService = authenticationService;

    HelpDeskBotSessionManager.setDefaultSessionManager(new HelpDeskBotSessionManager());
    HelpDeskBotSessionManager.getDefaultSessionManager().registerSession(this.configuration.getGroupId(),
        this.helpDeskBot.getHelpDeskBotSession());
  }

  @Bean(name = "membershipClient")
  @Description("A membership client")
  public MembershipClient getMembershipClient() {
    return new MembershipClient(configuration.getGroupId(), configuration.getHelpDeskServiceUrl());
  }

  @Bean(name = "ticketClient")
  @Description("A ticket client")
  public TicketClient getTicketClient() {
    return new TicketClient(configuration.getGroupId(), configuration.getHelpDeskServiceUrl());
  }

  @Bean(name = "symphonyClient")
  @Description("A ticket client")
  public SymphonyClient getSymphonyClient() throws InitException {
    SymAuth symAuth = authenticationService.authenticate();

    SymphonyClient symClient = new SymphonyBasicClient();
    symClient.init(symAuth, configuration.getEmail(), configuration.getAgentUrl(), configuration.getPodUrl());

    return symClient;
  }

  @Bean(name = "validationUtil")
  @Description("A validation utility")
  public SymphonyValidationUtil getValidationUtil() {
    return new SymphonyValidationUtil(helpDeskBot.getHelpDeskBotSession().getSymphonyClient());
  }

  /**
   * Configure CORS for the web resources accessed from other domains
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
      }
    };
  }
}
