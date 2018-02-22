package org.symphonyoss.symphony.bots.helpdesk.service.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by rsanchez on 22/11/17.
 */
@Configuration
@EnableConfigurationProperties({ MongoParameters.class })
@Conditional(MongoCondition.class)
public class HelpDeskServiceMongoConfiguration extends AbstractMongoConfiguration {

  private MongoClient mongoClient;

  private final MongoParameters mongoParameters;

  public HelpDeskServiceMongoConfiguration(MongoParameters mongoParameters) {
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

    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
    if (!mongoParameters.isIgnoreSSL()) {
      builder = builder.socketFactory(SSLSocketFactory.getDefault());
    }

    MongoClientOptions options = builder.socketTimeout(mongoParameters.getSocketTimeout())
        .connectTimeout(mongoParameters.getConnectTimeout())
        .readPreference(ReadPreference.valueOf(mongoParameters.getReadPreference()))
        .writeConcern(WriteConcern.valueOf(mongoParameters.getWriteConcern()))
        .maxWaitTime(mongoParameters.getWaitTimeout())
        .build();

    String host = mongoParameters.getHost();

    if (host == null) {
      throw new IllegalArgumentException("Unknown mongo host");
    }

    int port = mongoParameters.getPort();

    this.mongoClient = new MongoClient(new ServerAddress(host, port), options);

    return mongoClient;
  }

  @Bean
  @ConditionalOnMissingBean
  public MongoDbFactory mongoDbFactory() throws Exception {
    MongoClient mongo = (MongoClient) mongo();
    return new SimpleMongoDbFactory(mongo, getDatabaseName());
  }

  @Bean
  @ConditionalOnMissingBean
  public MongoTemplate mongoTemplate() throws Exception {
    MappingMongoConverter converter =
        new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()),
            new MongoMappingContext());
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));

    MongoTemplate template = new MongoTemplate(mongoDbFactory(), converter);
    return template;
  }

  @Bean
  @ConditionalOnMissingBean
  public DB db(MongoTemplate mongoTemplate) {
    return mongoTemplate.getDb();
  }

  @Bean
  @ConditionalOnMissingBean
  public MappingMongoConverter mappingMongoConverter() throws Exception {
    DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(this.mongoDbFactory());
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, this.mongoMappingContext());
    converter.setCustomConversions(this.customConversions());
    return converter;
  }

}
