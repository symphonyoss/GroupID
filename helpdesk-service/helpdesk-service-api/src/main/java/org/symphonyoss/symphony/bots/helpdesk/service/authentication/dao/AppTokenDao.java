package org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao;

import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;

/**
 * DAO for application tokens.
 *
 * Created by rsanchez on 12/03/18.
 */
public interface AppTokenDao {

  /**
   * Save application token into database.
   *
   * @param appToken Application token
   * @return success response with created/updated application token.
   */
  HelpDeskAppToken saveAppToken(HelpDeskAppToken appToken);

  /**
   * Gets an application token from database.
   *
   * @param appToken application token.
   * @return success response with retrieved application token object.
   */
  HelpDeskAppToken getAppToken(String appToken);

}
