package org.symphonyoss.symphony.apps.authentication.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;

/**
 * Spring Configuration to create JSON parser.
 *
 * Created by rsanchez on 10/01/18.
 */
@Configuration
@ConditionalOnProperty(name = "app-authentication.enabled", havingValue = "true")
public class JsonParserConfiguration {

  @Bean
  @ConditionalOnMissingBean(JsonParser.class)
  public JsonParser jsonParser() {
    return new JacksonParser();
  }

}
