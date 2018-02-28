package org.symphonyoss.symphony.bots.utility.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.symphonyoss.client.model.NodeTypes;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SymMessageUtil {

  /**
   * Check if a SymMessage is a chime
   * @param message SymMessage with the message contents
   * @return True if message is a chime, false otherwise
   */
  public static boolean isChime(SymMessage message) {
    Element elementMessageML = null;

    if (message.getMessage() != null) {
      Document doc = Jsoup.parse(message.getMessage());

      elementMessageML = doc.select("messageML").first();

      if (elementMessageML == null) {
        elementMessageML = doc.select("div").first();
      }

      if (elementMessageML != null) {
        elementMessageML = doc.select("audio").first();
      }
    }

    return elementMessageML != null;
  }

  /**
   * Check if a SymMessage contains an attachment
   * @param message SymMessage with the message contents
   * @return True if message contains an attachment, false otherwise
   */
  public static boolean hasAttachment(SymMessage message) {
    List<SymAttachmentInfo> attachments = message.getAttachments();
    return attachments != null && !attachments.isEmpty();
  }

  /**
   * Check if a SymMessage contains a table
   * @param message SymMessage with the message contents
   * @return True if message contains a table, false otherwise
   */
  public static boolean hasTable(SymMessage message) {
    Element elementMessageML = null;

    if (message.getMessage() != null) {
      Document doc = Jsoup.parse(message.getMessage());

      elementMessageML = doc.select("messageML").first();

      if (elementMessageML == null) {
        elementMessageML = doc.select("div").first();
      }

      if (elementMessageML != null) {
        elementMessageML = doc.select("table").first();
      }
    }

    return elementMessageML != null;
  }

  /**
   * Parse a message into valid MessageML format
   * @param message SymphonyAiMessage with the message contents
   * @return StringBuilder with the output message in MessageML format
   */
  public static String parseMessage(SymMessage message) {
    Element elementMessageML;
    StringBuilder textDoc = new StringBuilder("");

    Document doc = Jsoup.parse(message.getMessage());

    doc.select("errors").remove();
    elementMessageML = doc.select("messageML").first();
    if (elementMessageML == null) {
      elementMessageML = doc.select("div").first();
    }

    if (elementMessageML != null) {
      elementMessageML.childNodes().forEach(node -> {
        if (node.toString().equalsIgnoreCase("<br>")) {
          textDoc.append("<br/>");
        } else {
          textDoc.append(node.toString());
        }
      });
    }

    return placeEmojis(textDoc.toString());
  }

  /**
   * Parse the received PresentationML formatted message into a valid MessageML fragment that
   * will compose the "Question" field of the ticket
   * @param message The message to be processed
   * @return String containing the valid MessageML fragment
   */
  public static String parseTicketMessage(SymMessage message) throws IOException {
    Element elementMessageML;
    StringBuilder textDoc = new StringBuilder("");

    Document doc = Jsoup.parse(message.getMessage());

    doc.select("errors").remove();
    elementMessageML = doc.select("messageML").first();
    if (elementMessageML == null) {
      elementMessageML = doc.select("div").first();
    }

    if (elementMessageML != null) {
      for (Node node : elementMessageML.childNodes()) {
        if (node.nodeName().equalsIgnoreCase("span")) {
          if (node.attributes().get("class").equalsIgnoreCase("entity")) {
            String value = node.childNodes().get(0).toString();
            String statement = "";
            if (value.startsWith("#")) {
              statement =
                  "<" + NodeTypes.HASHTAG.toString() + " tag=\"" + value.substring(1) + "\" />";
            } else if (value.startsWith("$")) {
              statement =
                  "<" + NodeTypes.CASHTAG.toString() + " tag=\"" + value.substring(1) + "\" />";
            } else if (value.startsWith("@")) {
              LinkedHashMap result = (LinkedHashMap)
                  new ObjectMapper().readValue(message.getEntityData(), HashMap.class)
                      .get(node.attributes().get("data-entity-id"));
              ArrayList<LinkedHashMap> ids = (ArrayList) result.get("id");
              for (LinkedHashMap id : ids) {
                statement +=
                    "<" + NodeTypes.MENTION.toString() + " uid=\"" + id.get("value").toString()
                        + "\" />";
              }
            }
            textDoc.append(statement);
          }
        } else if (node.nodeName().equalsIgnoreCase("<br>")) {
          textDoc.append("<br/>");
        } else {
          textDoc.append(node.toString());
        }
      }
    }

    return placeEmojis(textDoc.toString());
  }

  /**
   * Verifies a string changing any emoji shortcodes with the correct MessageML emoji tags
   * @param message String with   the possible emojis
   * @return String with any emojis processed into MessageML tags
   */
  private static String placeEmojis(String message) {
    StringBuilder result = new StringBuilder();
    StringBuilder partial = new StringBuilder();
    boolean possileEmoji = false;
    for (Character c : message.toCharArray()) {
      if (possileEmoji) {
        partial.append(c);
        if (c == ':') {
          partial.setLength(partial.length()-1);
          String shortCode = partial.toString();
          shortCode = shortCode.substring(1);
          result.append("<emoji shortcode=\"" + shortCode + "\" />");
          possileEmoji = false;
        } else if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_' && c != '-' && c != '+') {
          possileEmoji = false;
          result.append(partial);
        }
      } else {
        if (c == ':') {
          possileEmoji = true;
          partial.setLength(0);
          partial.append(c);
        } else {
          result.append(c);
        }
      }
    }
    return result.toString();
  }
}
