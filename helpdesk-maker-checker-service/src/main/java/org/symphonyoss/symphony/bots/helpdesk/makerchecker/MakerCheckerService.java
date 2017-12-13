package org.symphonyoss.symphony.bots.helpdesk.makerchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check.Checker;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymStream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 9/26/17.
 * Used in conjunction with the Symphony AI.
 * Validates a messages, and requests validation from another user when checks fail.
 */
public class MakerCheckerService {

  private static final Logger LOG = LoggerFactory.getLogger(MakerCheckerService.class);
  private static final String MESSAGE_NOT_FOUND = "Message with id %s could not be found.";
  private static final String STREAM_NOT_FOUND= "The stream %s could not be found.";

  private Set<Checker> checkerSet = new HashSet<>();

  private final MakercheckerClient makercheckerClient;

  private final SymphonyClient symphonyClient;

  public MakerCheckerService(MakercheckerClient client, SymphonyClient symphonyClient) {
    this.makercheckerClient = client;
    this.symphonyClient = symphonyClient;
  }

  /**
   * Add a check to the maker checker service.
   * @param checker the check to add.
   */
  public void addCheck(Checker checker) {
    checkerSet.add(checker);
  }

  /**
   * Validates that all checks pass.
   * @param symMessage the message to validate.
   * @return if all the checks passed.
   */
  public boolean allChecksPass(SymMessage symMessage) {
    for(Checker checker: checkerSet) {
      Set<Object> flagged = checker.check(symMessage);
      if (flagged != null && !flagged.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  /**
   * Accept a maker checker message.
   * Find the message.
   * Send the message to client stream.
   * @param makerCheckerMessage the maker checker message
   */
  public Set<SymMessage> getAcceptMessages(MakerCheckerMessage makerCheckerMessage) {
    SymStream stream = new SymStream();
    stream.setStreamId(makerCheckerMessage.getStreamId());
    try {
      List<SymMessage> symMessageList = symphonyClient.getMessagesClient()
          .getMessagesFromStream(stream, makerCheckerMessage.getTimeStamp() - 1, 0, 10);

      SymMessage match = null;
      for(SymMessage symMessage : symMessageList) {
        if(symMessage.getId().equals(makerCheckerMessage.getMessageId())) {
          match = symMessage;
        }
      }

      if(match == null) {
        throw new BadRequestException(String.format(MESSAGE_NOT_FOUND, makerCheckerMessage.getMessageId()));
      }

      Set<SymMessage> acceptMessages = new HashSet<>();
      for(Checker checker: checkerSet) {
        if(checker.isCheckerType(makerCheckerMessage)) {
          Set<SymMessage> messages = checker.makeApprovedMessages(makerCheckerMessage, match);
          for(SymMessage symMessage: messages) {
            for(String streamId: makerCheckerMessage.getProxyToStreamIds()) {
              stream = new SymStream();
              stream.setStreamId(streamId);

              SymMessage copy = new SymMessage();
              copy.setStreamId(streamId);
              copy.setStream(stream);
              copy.setMessage(symMessage.getMessage());
              copy.setEntityData(symMessage.getEntityData());
              copy.setAttachments(symMessage.getAttachments());
              copy.setTimestamp(symMessage.getTimestamp());

              acceptMessages.add(copy);
            }
          }
        }
      }

      return acceptMessages;
    } catch (MessagesException e) {
      LOG.warn("Error accepting maker checker message: ", e);
      throw new BadRequestException(String.format(STREAM_NOT_FOUND, stream.getStreamId()));
    }
  }

  /**
   * If all checks did not pass, get a message to send back to user who sent the message.
   * This message will request validation from another user.
   * @param symMessage the message to base the maker checker message on.
   * @return the maker checker message.
   */
  public Set<SymMessage> getMakerCheckerMessages(SymMessage symMessage, Set<String> proxyToIds) {
    Set<SymMessage> makerCheckerMessages = new HashSet<>();

    for(Checker checker: checkerSet) {
      Set<Object> checkFlagged = checker.check(symMessage);

      if (checkFlagged != null && !checkFlagged.isEmpty()) {
        makerCheckerMessages.addAll(checker.buildSymCheckerMessages(symMessage, proxyToIds));
      }
    }

    return makerCheckerMessages;
  }

  public void createMakerchecker(SymMessage symMessage) {
    this.makercheckerClient.createMakerchecker(symMessage.getAttachments().get(0).getId(),
        symMessage.getFromUserId(), symMessage.getStreamId());
  }

}
