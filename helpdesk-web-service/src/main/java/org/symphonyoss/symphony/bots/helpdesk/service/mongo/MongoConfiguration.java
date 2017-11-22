package org.symphonyoss.symphony.bots.helpdesk.service.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by rsanchez on 22/11/17.
 */
@Configuration
@EnableConfigurationProperties({ MongoParameters.class })
public class MongoConfiguration extends AbstractMongoConfiguration {

  private MongoClient mongoClient;

  private final MongoParameters mongoParameters;

  public MongoConfiguration(MongoParameters mongoParameters) {
    this.mongoParameters = mongoParameters;
  }

  @Override
  protected String getDatabaseName() {
    return mongoParameters.getDatabase();
  }

  @Override
  public Mongo mongo() throws Exception {
    if (this.mongoClient != null) {
      return this.mongoClient;
    }

    MongoClientOptions.Builder builder = new MongoClientOptions.Builder()
        .socketFactory(SSLSocketFactory.getDefault());

    MongoClientOptions options = builder.socketTimeout(mongoParameters.getSocketTimeout())
        .connectTimeout(mongoParameters.getConnectTimeout())
        .readPreference(ReadPreference.valueOf(mongoParameters.getReadPreference()))
        .writeConcern(WriteConcern.valueOf(mongoParameters.getWriteConcern()))
        .maxWaitTime(mongoParameters.getWaitTimeout())
        .build();

    String host = mongoParameters.getHost();
    int port = mongoParameters.getPort();

    this.mongoClient = new MongoClient(new ServerAddress(host, port), options);

    return mongoClient;
  }

  @Bean
  public MongoDbFactory mongoDbFactory() throws Exception {
    MongoClient mongo = (MongoClient) mongo();
    return new SimpleMongoDbFactory(mongo, getDatabaseName());
  }

  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
    MongoTemplate template = new MongoTemplate(mongoDbFactory());
    return template;
  }

  @Bean
  public DB db(MongoTemplate mongoTemplate) {
    return mongoTemplate.getDb();
  }

}
