package org.symphonyoss.symphony.bots.helpdesk.service.authentication;

import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.AppTokenDao;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;

/**
 * Implementation class to retrieve and store the application and symphony tokens from/to Mongo DB.
 * <p>
 * Created by rsanchez on 12/03/18.
 */
@Component
public class HelpDeskStoreTokensProvider implements StoreTokensProvider {

  private final AppTokenDao dao;

  public HelpDeskStoreTokensProvider(AppTokenDao dao) {
    this.dao = dao;
  }

  @Override
  public void saveAppAuthenticationToken(AppToken appToken) {
    HelpDeskAppToken token = new HelpDeskAppToken(appToken);
    dao.saveAppToken(token);
  }

  @Override
  public AppToken getAppAuthenticationToken(String appToken) {
    HelpDeskAppToken result = dao.getAppToken(appToken);

    if ((result != null) && (System.currentTimeMillis() < result.getExpiresAt())) {
      return result.toAppToken();
    }

    return null;
  }

}
