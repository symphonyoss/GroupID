package org.symphonyoss.symphony.bots.ai.conversation;

import org.apache.commons.lang3.RandomStringUtils;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.AiResponseIdentifier;
import org.symphonyoss.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import org.symphonyoss.symphony.bots.ai.impl.SymphonyAiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of an AI conversation that proxys and validates messages.
 */
public class ProxyConversation extends AiConversation {
  private static final int MAKERCHECKER_ID_LENGTH = 10;
  private Set<AiResponseIdentifier> proxyToIds = new HashSet<>();
  private ProxyIdleTimer proxyIdleTimer;
  private MakerCheckerService makerCheckerService;
  private MakercheckerClient makercheckerClient;

  public ProxyConversation(boolean allowCommands, MakerCheckerService makerCheckerService) {
    super(allowCommands);
    this.makerCheckerService = makerCheckerService;
  }

  /**
   * On conversation message, validate the message with the maker checker service.
   * If all checks pass, proxy the message.
   * If checks fail, send a validation message back to the sending user.
   * @param responder the AI responder, used to respond to users.
   * @param message the message received by the AI
   */
  @Override
  public void onMessage(AiResponder responder, AiMessage message) {
    SymphonyAiMessage symphonyAiMessage = (SymphonyAiMessage) message;
    if(makerCheckerService.allChecksPass(symphonyAiMessage.toSymMessage())) {
      AiResponse aiResponse = new AiResponse(symphonyAiMessage, proxyToIds);
      responder.addResponse(aiSessionContext, aiResponse);
      responder.respond(aiSessionContext);
    } else {
      Set<String> proxyToIds = new HashSet<>();
      for(AiResponseIdentifier aiResponseIdentifier : this.proxyToIds) {
        proxyToIds.add(aiResponseIdentifier.getResponseIdentifier());
      }

      Set<SymMessage> symMessages =
          makerCheckerService.getMakerCheckerMessages(symphonyAiMessage.toSymMessage(), proxyToIds);
      for(SymMessage symMessage: symMessages) {
        Set<AiResponseIdentifier> identifiers = new HashSet<>();
        identifiers.add(new AiResponseIdentifierImpl(symMessage.getStreamId()));
        AiResponse aiResponse = new AiResponse(new SymphonyAiMessage(symMessage), identifiers);
        responder.addResponse(aiSessionContext, aiResponse);

        String makercheckerId = RandomStringUtils.randomAlphanumeric(MAKERCHECKER_ID_LENGTH).toUpperCase();
        makerCheckerService.createMakerchecker(makercheckerId, symMessage.getFromUserId(),
            symMessage.getStreamId());
      }
      responder.respond(aiSessionContext);
    }

    if (proxyIdleTimer != null) {
      proxyIdleTimer.reset();
    }
  }

  /**
   * Add a new ID to proxy to.
   * @param streamId the stream to proxy to.
   */
  public void addProxyId(String streamId) {
    proxyToIds.add(new AiResponseIdentifierImpl(streamId));
  }

  public ProxyIdleTimer getProxyIdleTimer() {
    return proxyIdleTimer;
  }

  public void setProxyIdleTimer(ProxyIdleTimer proxyIdleTimer) {
    this.proxyIdleTimer = proxyIdleTimer;
  }
}
