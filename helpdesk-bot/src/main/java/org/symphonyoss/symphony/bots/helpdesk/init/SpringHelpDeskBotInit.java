package org.symphonyoss.symphony.bots.helpdesk.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.symphonyoss.symphony.bots.helpdesk.api.V1HelpDeskController;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.model.session.HelpDeskBotSessionManager;
import org.symphonyoss.symphony.bots.helpdesk.service.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Iterator;

import javax.annotation.PostConstruct;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@SpringBootApplication
@EnableSwagger2
@EnableWebMvc
@EnableConfigurationProperties(HelpDeskBotConfig.class)
@ComponentScan(basePackages = {"org.symphonyoss.symphony.bots.helpdesk.api"})
public class SpringHelpDeskBotInit {
  private static final Logger LOG = LoggerFactory.getLogger(SpringHelpDeskBotInit.class);

  @Autowired
  private HelpDeskBotConfig helpDeskBotConfig;

  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext context = SpringApplication.run(SpringHelpDeskBotInit.class, args);

    HelpDeskBotConfig helpDeskBotConfig = (HelpDeskBotConfig) context.getBean("helpDeskBotConfig");
    HelpDeskBot helpDeskBot = new HelpDeskBot(helpDeskBotConfig);
    HelpDeskBotSessionManager.setDefaultSessionManager(new HelpDeskBotSessionManager());
    HelpDeskBotSessionManager.getDefaultSessionManager().registerSession(helpDeskBotConfig.getGroupId(),
        helpDeskBot.getHelpDeskBotSession());
  }

  @PostConstruct
  private void init(){
    LOG.info("Using config: " + helpDeskBotConfig);
  }

  @Configuration
  public class SpringHelpDeskBotContext {

    @Autowired
    private HelpDeskBotConfig helpDeskBotConfig;

    @Bean(name = "helpDeskBotConfig")
    @Description("A help desk bot config")
    public HelpDeskBotConfig getHelpDeskBotConfig() {
      return new HelpDeskBotConfig();
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
  }
}
