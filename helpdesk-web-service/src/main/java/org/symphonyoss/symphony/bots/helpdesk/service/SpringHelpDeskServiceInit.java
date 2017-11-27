package org.symphonyoss.symphony.bots.helpdesk.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by nick.tarsillo on 9/25/17.
 */
@EnableSwagger2
@EnableWebMvc
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class SpringHelpDeskServiceInit {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringHelpDeskServiceInit.class, args);
  }

}
