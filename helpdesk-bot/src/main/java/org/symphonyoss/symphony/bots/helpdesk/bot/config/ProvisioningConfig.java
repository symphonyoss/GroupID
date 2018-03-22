package org.symphonyoss.symphony.bots.helpdesk.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.bot.model.User;

/**
 * Holds configuration regarding the provisioning process
 * Created by campidelli on 3/16/18.
 */
@Component
@Lazy
public class ProvisioningConfig {

  @Value("${provisioning.execute}")
  private boolean execute;

  @Value("${provisioning.user.name}")
  private String userName;

  @Value("${provisioning.user.password}")
  private String userPassword;

  @Value("${provisioning.ca.generate-keystore}")
  private boolean generateCACert;

  @Value("${provisioning.ca.overwrite}")
  private boolean overwriteCACert;

  @Value("${provisioning.service-account.name}")
  private String serviceAccountUserName;

  @Value("${provisioning.service-account.generate-keystore}")
  private boolean generateServiceAccountKeystore;

  @Value("${provisioning.service-account.overwrite}")
  private boolean overwriteServiceAccountKeystore;

  public String getUserName() {
    return userName;
  }

  public boolean isExecute() {
    return execute;
  }

  public void setExecute(boolean execute) {
    this.execute = execute;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public boolean isGenerateCACert() {
    return generateCACert;
  }

  public void setGenerateCACert(boolean generateCACert) {
    this.generateCACert = generateCACert;
  }

  public boolean isOverwriteCACert() {
    return overwriteCACert;
  }

  public void setOverwriteCACert(boolean overwriteCACert) {
    this.overwriteCACert = overwriteCACert;
  }

  public String getServiceAccountUserName() {
    return serviceAccountUserName;
  }

  public void setServiceAccountUserName(String serviceAccountUserName) {
    this.serviceAccountUserName = serviceAccountUserName;
  }

  public boolean isGenerateServiceAccountKeystore() {
    return generateServiceAccountKeystore;
  }

  public void setGenerateServiceAccountKeystore(boolean generateServiceAccountKeystore) {
    this.generateServiceAccountKeystore = generateServiceAccountKeystore;
  }

  public boolean isOverwriteServiceAccountKeystore() {
    return overwriteServiceAccountKeystore;
  }

  public void setOverwriteServiceAccountKeystore(boolean overwriteServiceAccountKeystore) {
    this.overwriteServiceAccountKeystore = overwriteServiceAccountKeystore;
  }
}
