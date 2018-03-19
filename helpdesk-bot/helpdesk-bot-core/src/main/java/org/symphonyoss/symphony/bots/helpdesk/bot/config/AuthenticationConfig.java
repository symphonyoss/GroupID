package org.symphonyoss.symphony.bots.helpdesk.bot.config;

/**
 * Created by robson on 20/11/17.
 */
public class AuthenticationConfig {

  private String truststoreFile;

  private String truststorePassword;

  private String truststoreData;

  private String keystoreFile;

  private String keystorePassword;

  private String keystoreData;

  public String getTruststoreFile() {
    return truststoreFile;
  }

  public void setTruststoreFile(String truststoreFile) {
    this.truststoreFile = truststoreFile;
  }

  public String getTruststorePassword() {
    return truststorePassword;
  }

  public void setTruststorePassword(String truststorePassword) {
    this.truststorePassword = truststorePassword;
  }

  public String getTruststoreData() {
    return truststoreData;
  }

  public void setTruststoreData(String truststoreData) {
    this.truststoreData = truststoreData;
  }

  public String getKeystoreFile() {
    return keystoreFile;
  }

  public void setKeystoreFile(String keystoreFile) {
    this.keystoreFile = keystoreFile;
  }

  public String getKeystorePassword() {
    return keystorePassword;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public String getKeystoreData() {
    return keystoreData;
  }

  public void setKeystoreData(String keystoreData) {
    this.keystoreData = keystoreData;
  }
}
