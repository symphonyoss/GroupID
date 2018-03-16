package org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.AppTokenDao;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.exception
    .RetrieveTokensException;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.exception.SaveTokensException;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;
import org.symphonyoss.symphony.bots.helpdesk.service.mongo.MongoCondition;

/**
 * DAO component responsible for managing app tokens into MongoDB. This class
 * depends of {@link MongoCondition} to be created.
 * <p>
 * This component is being lazily initialized to ensure the database connection only happens when
 * it receives the first request to get data.
 * <p>
 * Created by rsanchez on 12/03/18.
 */
@Component
@Conditional(MongoCondition.class)
@Lazy
public class MongoAppTokenDao implements AppTokenDao {

  private static final String COLLECTION_NAME = "helpdeskapptokens";

  private final MongoTemplate mongoTemplate;

  public MongoAppTokenDao(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public HelpDeskAppToken saveAppToken(HelpDeskAppToken appToken) {
    try {
      this.mongoTemplate.save(appToken, COLLECTION_NAME);
      return appToken;
    } catch (Exception e) {
      throw new SaveTokensException(appToken.getId(), e);
    }
  }

  @Override
  public HelpDeskAppToken getAppToken(String appToken) {
    try {
      Criteria criteria = where("appToken").is(appToken);
      return mongoTemplate.findOne(query(criteria), HelpDeskAppToken.class, COLLECTION_NAME);
    } catch (Exception e) {
      throw new RetrieveTokensException(appToken, e);
    }
  }

}
