package org.symphonyoss.symphony.bots.helpdesk.makerchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerMessage;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.MakerCheckerServiceSession;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.check.Checker;
import org.symphonyoss.symphony.bots.helpdesk.makerchecker.model.template
    .MakerCheckerEntityTemplateData;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client.MakercheckerClient;
import org.symphonyoss.symphony.bots.utility.template.MessageTemplate;
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
  private Set<Object> flaggedData = new HashSet<>();

  private final MakercheckerClient makercheckerClient;

  private final MakerCheckerServiceSession session;

  public MakerCheckerService(MakercheckerClient client, MakerCheckerServiceSession session) {
    this.makercheckerClient = client;
    this.session = session;
  }

  /**
   * Add a check to the maker checker service.
   * @param checker the check to add.
   */
  public void addCheck(Checker checker) {
    checkerSet.add(checker);
    checker.setSession(session);
  }

  /**
   * Validates that all checks pass.
   * @param symMessage the message to validate.
   * @return if all the checks passed.
   */
  public boolean allChecksPass(SymMessage symMessage) {
    flaggedData = new HashSet<>();
    for(Checker checker: checkerSet) {
      Set<Object> flagged = checker.check(symMessage);
      if(flagged != null && !flagged.isEmpty()) {
        flaggedData.add(flagged);
      }
    }

    return flaggedData.isEmpty();
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
      List<SymMessage> symMessageList =
          session.getSymphonyClient().getMessagesClient().getMessagesFromStream(
              stream, Long.parseLong(makerCheckerMessage.getTimeStamp()) - 1, 0, 10);

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
    String groupId = session.getMakerCheckerServiceConfig().getGroupId();
    Set<SymMessage> makerCheckerMessages = new HashSet<>();
    for(Checker checker: checkerSet) {
      Set<Object> flagged = checker.check(symMessage);
      if(flagged != null && !flagged.isEmpty()) {
        Set<SymMessage> symMessages = checker.buildSymCheckerMessages(symMessage);
        for (SymMessage checkerMessage : symMessages) {
          MessageTemplate entityTemplate = new MessageTemplate(checkerMessage.getEntityData());
          checkerMessage.setEntityData(entityTemplate.buildFromData(
              new MakerCheckerEntityTemplateData(groupId, symMessage, proxyToIds)));
          checkerMessage.setStreamId(symMessage.getStreamId());
          checkerMessage.setFromUserId(symMessage.getFromUserId());
          makerCheckerMessages.add(checkerMessage);
        }
      }
    }

    makerCheckerMessages.addAll(getMessagesFromUnflaggedData(symMessage, proxyToIds));
    return makerCheckerMessages;
  }

  private Set<SymMessage> getMessagesFromUnflaggedData(SymMessage symMessage, Set<String> proxyToIds) {
    boolean messageContainsData = false;
    Set<SymMessage> symMessages = new HashSet<>();
    if(messageContainsData) {
      for (String stream: proxyToIds) {
        SymMessage unflaggedData = new SymMessage();
        if (!flaggedData.contains(symMessage.getMessage())) {
          unflaggedData.setMessage(symMessage.getMessage());
          messageContainsData = true;
        }
        if (!flaggedData.contains(symMessage.getEntityData())) {
          unflaggedData.setEntityData(symMessage.getEntityData());
          messageContainsData = true;
        }
        if(!flaggedData.contains(symMessage.getAttachments())) {
          unflaggedData.setAttachments(symMessage.getAttachments());
          messageContainsData = true;
        }
        unflaggedData.setStreamId(stream);
        unflaggedData.setFromUserId(symMessage.getFromUserId());

        if(messageContainsData) {
          symMessages.add(unflaggedData);
        }
      }
    }

    return symMessages;
  }

  public void createMakerchecker(String id, Long makerId, String streamId) {
    this.makercheckerClient.createMakerchecker(id, makerId, streamId);
  }

}
