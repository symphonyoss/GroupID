package org.symphonyoss.symphony.bots.ai.helpdesk.conversation;

import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.model.AiMessage;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiConversation;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.MakerCheckerService;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 9/28/17.
 * An extension of an AI conversation that proxys and validates messages.
 */
public class ProxyConversation extends AiConversation {

  private Set<String> proxyToIds = new HashSet<>();

  private ProxyIdleTimer proxyIdleTimer;

  private MakerCheckerService makerCheckerService;

  /**
   * Constructor for class ProxyConversation that avoids commands.
   * @param makerCheckerService the MakerCheckerService
   */
  public ProxyConversation(MakerCheckerService makerCheckerService) {
    super(false);
    this.makerCheckerService = makerCheckerService;
  }

  /**
   * Constructor for class ProxyConversation that supports commands.
   * @param aiCommandMenu Available commands
   * @param makerCheckerService the MakerCheckerService
   */
  public ProxyConversation(AiCommandMenu aiCommandMenu, MakerCheckerService makerCheckerService) {
    super(true, aiCommandMenu);
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
    SymMessage symMessage = message.toSymMessage();

    if(makerCheckerService.allChecksPass(symMessage)) {
      dispatchMessage(responder, message);
    } else {
      dispatchMakerCheckerMessage(symMessage);
    }

    if (proxyIdleTimer != null) {
      proxyIdleTimer.reset();
    }
  }

  /**
   * Build a new AI Response with the message and send it in the AI Session context
   * @param responder the AI responder, used to respond to users.
   * @param aiMessage the message to be sent by the AI
   */
  private void dispatchMessage(AiResponder responder, AiMessage aiMessage) {
    AiResponse aiResponse = new AiResponse(aiMessage, proxyToIds);
    responder.respond(aiResponse);
  }

  /**
   * Build a MakerCheckerMessage and send it via the MakerCheckerService
   * @param message the message to be sent
   */
  private void dispatchMakerCheckerMessage(SymMessage message) {
    Set<SymMessage> symMessages = makerCheckerService.getMakerCheckerMessages(message, proxyToIds);

    for(SymMessage symMessage: symMessages) {
      makerCheckerService.sendMakerCheckerMesssage(symMessage, message.getId(), proxyToIds);
    }
  }

  /**
   * Add a new ID to proxy to.
   * @param streamId the stream to proxy to.
   */
  public void addProxyId(String streamId) {
    proxyToIds.add(streamId);
  }

  public void setProxyIdleTimer(ProxyIdleTimer proxyIdleTimer) {
    this.proxyIdleTimer = proxyIdleTimer;
  }
}
