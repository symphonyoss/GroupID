package org.symphonyoss.symphony.apps.authentication.keystore;

import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;

/**
 * Interface to retrieve the keystore used to perform authentication on the POD.
 *
 * Created by rsanchez on 06/03/18.
 */
public interface KeystoreProvider {

  /**
   * Retrieves application keystore given the application id.
   *
   * @param appId Application identifier
   * @return Application keystore
   */
  KeystoreSettings getApplicationKeystore(String appId);

}
