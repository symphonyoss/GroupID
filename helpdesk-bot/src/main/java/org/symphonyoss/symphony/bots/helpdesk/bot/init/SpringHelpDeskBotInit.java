package org.symphonyoss.symphony.bots.helpdesk.bot.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.session.HelpDeskBotSessionManager;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.bots.utility.validation.SymphonyValidationUtil;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@SpringBootApplication(scanBasePackages = { "org.symphonyoss.symphony.bots.helpdesk.bot" })
@EnableSwagger2
@EnableWebMvc
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
    return new MembershipClient(helpDeskBotConfig.getGroupId(), helpDeskBotConfig.getHelpDeskServiceUrl());
  }

  @Bean(name = "ticketClient")
  @Description("A ticket client")
  public TicketClient getTicketClient() {
    return new TicketClient(helpDeskBotConfig.getGroupId(), helpDeskBotConfig.getHelpDeskServiceUrl());
  }

  @Bean(name = "makercheckerClient")
  @Description("A makerchecker client")
  public MakercheckerClient getMakercheckerClient() {
    return new MakercheckerClient(helpDeskBotConfig.getGroupId(), helpDeskBotConfig.getHelpDeskServiceUrl());
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
