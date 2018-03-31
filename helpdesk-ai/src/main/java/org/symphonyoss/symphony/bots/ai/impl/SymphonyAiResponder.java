package org.symphonyoss.symphony.bots.ai.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.ai.AiResponder;
import org.symphonyoss.symphony.bots.ai.common.AiConstants;
import org.symphonyoss.symphony.bots.ai.model.AiCommandMenu;
import org.symphonyoss.symphony.bots.ai.model.AiResponse;
import org.symphonyoss.symphony.bots.ai.model.SymphonyAiSessionKey;
import org.symphonyoss.symphony.clients.MessagesClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

/**
 * Concrete implementation of {@link AiResponder}.
 * <p>
 * Created by nick.tarsillo on 8/21/17.
 */
public abstract class SymphonyAiResponder implements AiResponder {

  @Override
  public void respond(AiResponse... responses) {
    for (AiResponse aiResponse : responses) {
      for (String streamId : aiResponse.getRespondTo()) {
        publishMessage(streamId, aiResponse.getMessage());
      }
    }
  }

  /**
   * Send the message over Symphony's message client using MessageML.
   * @param streamId Stream identifier to where the message should be sent
   * @param symphonyAiMessage message to be sent
   */
  protected abstract void publishMessage(String streamId, SymphonyAiMessage symphonyAiMessage);

  @Override
  public void respondWithUseMenu(SymphonyAiSessionKey sessionKey, AiCommandMenu commandMenu,
      SymphonyAiMessage message) {
    String response =
        String.format(AiConstants.NOT_COMMAND, message.getAiMessage()) + "<br/><hr/><b>"
            + AiConstants.MENU_TITLE + "</b><ul><li>" + commandMenu.toString()
            .replace("\n", "</li><li>") + "</li></ul>";
    response = response.replace("<li></li>", "");

    AiResponse aiResponse = new AiResponse(new SymphonyAiMessage(response), sessionKey.getStreamId());
    respond(aiResponse);
  }

}
