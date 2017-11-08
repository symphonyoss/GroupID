package org.symphonyoss.symphony.bots.helpdesk.service.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.symphonyoss.symphony.bots.helpdesk.service.client.TicketClient;
import org.symphonyoss.symphony.bots.helpdesk.service.config.HelpDeskServiceConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@SpringBootApplication
@EnableSwagger2
@EnableWebMvc
@EnableConfigurationProperties(HelpDeskServiceConfig.class)
@ComponentScan(basePackages = {"org.symphonyoss.symphony.bots.helpdesk.service.model", "org.symphonyoss.symphony.bots.helpdesk.service.api"})
public class SpringHelpDeskServiceInit {
  private static final Logger LOG = LoggerFactory.getLogger(SpringHelpDeskServiceInit.class);

  @Autowired
  private HelpDeskServiceConfig helpDeskServiceConfig;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringHelpDeskServiceInit.class, args);
  }

  @PostConstruct
  public void init() {
    LOG.info("Using config: " + helpDeskServiceConfig.toString());
  }
}
