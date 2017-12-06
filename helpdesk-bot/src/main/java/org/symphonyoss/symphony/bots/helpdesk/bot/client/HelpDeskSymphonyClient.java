package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.client.services.RoomService;
import org.symphonyoss.symphony.clients.model.ApiVersion;

/**
 * Created by rsanchez on 05/12/17.
 */
public class HelpDeskSymphonyClient extends SymphonyBasicClient {

  private HelpDeskRoomService roomService;

  @Override
  public void init(SymAuth symAuth, String email, String agentUrl, String podUrl) throws InitException {
    if(agentUrl == null) {
      throw new InitException("Failed to provide agent URL");
    }

    if(podUrl == null) {
      throw new InitException("Failed to provide service URL");
    }

    if(symAuth != null && symAuth.getSessionToken() != null && symAuth.getKeyToken() != null) {
      SymphonyClientConfig config = new SymphonyClientConfig(false);
      config.set(SymphonyClientConfigID.AGENT_URL, agentUrl);
      config.set(SymphonyClientConfigID.POD_URL, podUrl);
      config.set(SymphonyClientConfigID.USER_EMAIL, email);

      this.roomService = new HelpDeskRoomService(this, symAuth, config);

      super.init(symAuth, config);
    } else {
      throw new InitException("Symphony Authorization is not valid. Currently not logged into "
          + "Agent, please check certificates and tokens.");
    }
  }

  @Override
  public RoomService getRoomService() {
    return this.roomService;
  }

}
