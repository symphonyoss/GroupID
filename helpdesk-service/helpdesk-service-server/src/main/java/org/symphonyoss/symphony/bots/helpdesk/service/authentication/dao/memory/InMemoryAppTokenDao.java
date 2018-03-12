package org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.memory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.apps.authentication.spring.tokens.LocalStoreTokensProvider;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.AppTokenDao;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.mongo.MongoAppTokenDao;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DAO component responsible for managing app tokens in-memory. This component will be
 * created only if the {@link MongoAppTokenDao} component wasn't created previously.
 * <p>
 * This class should be used only for tests purpose.
 * <p>
 * Created by rsanchez on 12/03/18.
 */
@Component
@ConditionalOnMissingBean(MongoAppTokenDao.class)
public class InMemoryAppTokenDao extends LocalStoreTokensProvider implements AppTokenDao {

  private final Map<String, HelpDeskAppToken> tokens = new ConcurrentHashMap<>();

  @Override
  public HelpDeskAppToken saveAppToken(HelpDeskAppToken appToken) {
    this.tokens.put(appToken.getAppToken(), appToken);
    return appToken;
  }

  @Override
  public HelpDeskAppToken getAppToken(String appToken) {
    return this.tokens.get(appToken);
  }

}
