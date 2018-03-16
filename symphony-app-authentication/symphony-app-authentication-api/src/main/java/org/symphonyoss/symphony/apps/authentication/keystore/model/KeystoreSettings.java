package org.symphonyoss.symphony.apps.authentication.keystore.model;

import java.security.KeyStore;
import java.util.Objects;

/**
 * Holds the keystore data and password.
 *
 * Created by rsanchez on 06/03/18.
 */
public class KeystoreSettings {

  private KeyStore data;

  private String password;

  public KeystoreSettings() {}

  public KeystoreSettings(KeyStore data, String password) {
    this.data = data;
    this.password = password;
  }

  public KeyStore getData() {
    return data;
  }

  public void setData(KeyStore data) {
    this.data = data;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }

    KeystoreSettings that = (KeystoreSettings) o;

    return Objects.equals(data, that.data) &&
        Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, password);
  }
}
