package org.symphonyoss.symphony.bots.helpdesk.bot.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.symphonyoss.symphony.bots.helpdesk.bot.HelpDeskBot;
import org.symphonyoss.symphony.bots.helpdesk.bot.bootstrap.HelpDeskBootstrap;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by nick.tarsillo on 11/6/17.
 */
@SpringBootApplication(scanBasePackages = { "org.symphonyoss.symphony.bots.helpdesk.bot",
    "org.symphonyoss.symphony.bots.helpdesk.messageproxy" })
@EnableSwagger2
@EnableWebMvc
public class SpringHelpDeskBotInit {

  public static void main(String[] args) throws Exception {
    SpringApplication application = new SpringApplication(SpringHelpDeskBotInit.class);
    application.addListeners(new HelpDeskBootstrap());

    application.run(args);
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
