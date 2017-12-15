package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.SymEvent;
import org.symphonyoss.client.services.MessageService;

/**
 * Created by rsanchez on 13/12/17.
 */
public class HelpDeskMessageService extends MessageService {

  /**
   * Constructor
   * @param symClient Identifies the BOT user and exposes client APIs
   */
  public HelpDeskMessageService(SymphonyClient symClient) {
    super(symClient);
  }

  @Override
  public void onEvent(SymEvent symEvent) {
    if (symEvent.getId() == null) {
      symEvent.setId(symEvent.getType());
    }

    super.onEvent(symEvent);
  }

}
