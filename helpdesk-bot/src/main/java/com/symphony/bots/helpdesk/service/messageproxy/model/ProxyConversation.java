package com.symphony.bots.helpdesk.service.messageproxy.model;

import com.symphony.bots.ai.AiResponder;
import com.symphony.bots.ai.AiResponseIdentifier;
import com.symphony.bots.ai.impl.AiResponseIdentifierImpl;
import com.symphony.bots.ai.impl.SymphonyAiMessage;
import com.symphony.bots.ai.model.AiConversation;
import com.symphony.bots.ai.model.AiMessage;
import com.symphony.bots.ai.model.AiResponse;
import com.symphony.bots.helpdesk.service.makerchecker.MakerCheckerService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of an AI conversation that proxys and validates messages.
 */
public class ProxyConversation extends AiConversation {
  private Set<AiResponseIdentifier> proxyToIds = new HashSet<>();
  private MakerCheckerService makerCheckerService;

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
    if(makerCheckerService.allChecksPass(symphonyAiMessage)) {
      AiResponse aiResponse = new AiResponse(symphonyAiMessage, proxyToIds);
      responder.addResponse(aiSessionContext, aiResponse);
      responder.respond(aiSessionContext);
    } else {
      SymphonyAiMessage validationMessage = makerCheckerService.getMakerCheckerMessage(symphonyAiMessage);
      Set<AiResponseIdentifier> aiResponseIdentifiers = new HashSet<>();
      aiResponseIdentifiers.add(new AiResponseIdentifierImpl(symphonyAiMessage.getFromUserId()));
      AiResponse aiResponse = new AiResponse(validationMessage, aiResponseIdentifiers);
      responder.addResponse(aiSessionContext, aiResponse);
      responder.respond(aiSessionContext);
    }
  }

  /**
   * Add a new ID to proxy to.
   * @param streamId the stream to proxy to.
   */
  public void addProxyId(String streamId) {
    proxyToIds.add(new AiResponseIdentifierImpl(streamId));
  }
}
