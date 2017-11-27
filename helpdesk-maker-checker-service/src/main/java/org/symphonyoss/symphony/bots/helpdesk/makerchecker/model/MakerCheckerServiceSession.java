package org.symphonyoss.symphony.bots.helpdesk.makerchecker.model;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.config.MakerCheckerServiceConfig;

/**
 * Created by nick.tarsillo on 11/7/17.
 */
public class MakerCheckerServiceSession {
  private MakerCheckerServiceConfig makerCheckerServiceConfig;
  private SymphonyClient symphonyClient;

  public SymphonyClient getSymphonyClient() {
    return symphonyClient;
  }

  public void setSymphonyClient(SymphonyClient symphonyClient) {
    this.symphonyClient = symphonyClient;
  }

  public MakerCheckerServiceConfig getMakerCheckerServiceConfig() {
    return makerCheckerServiceConfig;
  }

  public void setMakerCheckerServiceConfig(
      MakerCheckerServiceConfig makerCheckerServiceConfig) {
    this.makerCheckerServiceConfig = makerCheckerServiceConfig;
  }
}
