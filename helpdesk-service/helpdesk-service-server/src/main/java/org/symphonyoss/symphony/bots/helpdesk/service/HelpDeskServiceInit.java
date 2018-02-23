package org.symphonyoss.symphony.bots.helpdesk.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main class to execute HelpDesk Service. This application provides a set of API's to manage
 * members of groups, tickets, and maker/checker information.
 * <p>
 * This application requires a datastore to persist these records. In production, the app connects to
 * a mongo database to make it. In local environment, you can disable this option and runs the
 * app saving all the information in-memory. To enable it you must set program arguments '--env=dev'
 * <p>
 * For this reason we must to exclude the Spring auto-configuration related to MongoDB.
 * Otherwise, the application always will try to connect a mongo database.
 * <p>
 * Created by rsanchez on 13/11/17.
 */
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@EnableSwagger2
@EnableWebMvc
public class HelpDeskServiceInit {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(HelpDeskServiceInit.class, args);
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
