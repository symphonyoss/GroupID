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
 * Spring auto-configuration class responsible for creating Mongo client according to the parameters
 * defined in the {@link MongoParameters} class.
 * <p>
 * All beans defined in this class only will be created if it doesn't exist in the Spring application context.
 * <p>
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

    System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
    System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
    System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
    System.out.println(" MONGO DB PROPERTIES: ");
    System.out.println(" host: " + host);
    System.out.println(" port: " + port);
    System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
    System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
    System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");

    this.mongoClient = new MongoClient(new ServerAddress(host, port), options);

    return mongoClient;
  }

  /**
   * Creates a new spring component of type {@link SimpleMongoDbFactory} to be used by the
   * {@link MongoTemplate}.
   *
   * @return MongoDB factory
   * @throws Exception Unexpected errors to create the database factory.
   */
  @Bean
  @ConditionalOnMissingBean
  public MongoDbFactory mongoDbFactory() throws Exception {
    MongoClient mongo = (MongoClient) mongo();
    return new SimpleMongoDbFactory(mongo, getDatabaseName());
  }

  /**
   * Creates new spring component of type a {@link MongoTemplate}. This class will be used to interact to MongoDB.
   *
   * @return Mongo template
   * @throws Exception Unexpected errors to create the mongo template.
   */
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

  /**
   * Creates a thread-safe client view of a logical database in a MongoDB cluster.
   *
   * @param mongoTemplate Mongo template
   * @return thread-safe client view of a logical database
   */
  @Bean
  @ConditionalOnMissingBean
  public DB db(MongoTemplate mongoTemplate) {
    return mongoTemplate.getDb();
  }

  /**
   * Creates a new spring component of type {@link MappingMongoConverter} using the configured
   * {@link #mongoDbFactory()} and {@link #mongoMappingContext()}.
   *
   * @return Mapping Mongo converter
   * @throws Exception Unexpected errors to create the mapping mongo converter.
   */
  @Bean
  @ConditionalOnMissingBean
  public MappingMongoConverter mappingMongoConverter() throws Exception {
    DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(this.mongoDbFactory());
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, this.mongoMappingContext());
    converter.setCustomConversions(this.customConversions());
    return converter;
  }

}
