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

  @Value("${provisioning.generate-ca}")
  private boolean generateCACert;

  @Value("${provisioning.service-account.name}")
  private String serviceAccountUserName;

  @Value("${provisioning.service-account.generate-p12}")
  private boolean generateServiceAccountP12;

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

  public String getServiceAccountUserName() {
    return serviceAccountUserName;
  }

  public void setServiceAccountUserName(String serviceAccountUserName) {
    this.serviceAccountUserName = serviceAccountUserName;
  }

  public boolean isGenerateServiceAccountP12() {
    return generateServiceAccountP12;
  }

  public void setGenerateServiceAccountP12(boolean generateServiceAccountP12) {
    this.generateServiceAccountP12 = generateServiceAccountP12;
  }
}
