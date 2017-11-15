package org.symphonyoss.symphony.bots.ai;

import org.symphonyoss.symphony.bots.ai.impl.SymphonyAi;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionContext;
import org.symphonyoss.symphony.bots.ai.model.AiSessionKey;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of the Symphony Ai, that supports help desk functions.
 */
public class HelpDeskAi extends SymphonyAi {
  private HelpDeskAiSession helpDeskAiSession;

  public HelpDeskAi(HelpDeskAiSession helpDeskAiSession) {
    super(helpDeskAiSession.getSymphonyClient(), helpDeskAiSession.getHelpDeskAiConfig().isSuggestCommands());

    this.helpDeskAiSession = helpDeskAiSession;
  }

      @Override
  public AiSessionContext newAiSessionContext(AiSessionKey aiSessionKey) {
    SymphonyAiSessionContext symphonyAiSessionContext = (SymphonyAiSessionContext) super.newAiSessionContext(aiSessionKey);
    HelpDeskAiSessionContext sessionContext = new HelpDeskAiSessionContext();
    sessionContext.setSymphonyAiChatListener(symphonyAiSessionContext.getSymphonyAiChatListener());
    sessionContext.setSymphonyAiMessageListener(symphonyAiSessionContext.getSymphonyAiMessageListener());
    sessionContext.setHelpDeskAiSession(helpDeskAiSession);
    sessionContext.setAiSessionKey(aiSessionKey);

    return sessionContext;
  }
}
