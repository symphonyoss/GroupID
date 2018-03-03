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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    if (elementMessageML == null) {
      return placeEmojis(textDoc.toString());
    }

    elementMessageML.childNodes().stream()
        .map(node -> {
          if (node.nodeName().equalsIgnoreCase("span")) {
            return parseSpan(node, message.getEntityData());
          } else if (node.nodeName().equalsIgnoreCase("br")) {
            return "<br/>";
          } else {
            return node.toString();
          }
        })
        .forEach(value -> textDoc.append(value));

    return placeEmojis(textDoc.toString());
  }

  /**
   * Parse Hashtag, Cashtag and Mention spans in the original PresentationML into valid MessageML
   * @param node The original PresentationML span node
   * @param entityData The message's entity data for data fetching in case of mentions
   * @return String with the parsed span
   */
  private static String parseSpan(Node node, String entityData) {
    if (node.attributes().get("class").equalsIgnoreCase("entity")) {
      String value = node.childNodes().get(0).toString();

      if (value.startsWith("#")) {
        return processHashTag(value);
      } else if (value.startsWith("$")) {
        return processCashTag(value);
      } else if (value.startsWith("@")) {
        return processMention(node, entityData, value);
      }
    }

    return node.toString();
  }

  /**
   * Process hashtag.
   *
   * @param value Hashtag value
   * @return Parsed hashtag
   */
  private static String processHashTag(String value) {
    StringBuilder parsedSpan = new StringBuilder();

    parsedSpan.append("<")
        .append(NodeTypes.HASHTAG.toString())
        .append(" tag=\"")
        .append(value.substring(1))
        .append("\" />");

    return parsedSpan.toString();
  }

  /**
   * Process cashtag.
   *
   * @param value Cashtag value
   * @return Parsed hashtag
   */
  private static String processCashTag(String value) {
    StringBuilder parsedSpan = new StringBuilder();

    parsedSpan.append("<")
        .append(NodeTypes.CASHTAG.toString())
        .append(" tag=\"")
        .append(value.substring(1))
        .append("\" />");

    return parsedSpan.toString();
  }

  /**
   * Process menition.
   *
   * @param node The original PresentationML span node
   * @param entityData The message's entity data for data fetching in case of mentions
   * @param value Mention value
   * @return Parsed hashtag
   */
  private static String processMention(Node node, String entityData, String value) {
    StringBuilder parsedSpan = new StringBuilder();

    try {
      LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>)
          new ObjectMapper().readValue(entityData, HashMap.class)
              .get(node.attributes().get("data-entity-id"));
      List<LinkedHashMap> ids = (List<LinkedHashMap>) result.get("id");
      for (LinkedHashMap id : ids) {
        parsedSpan.append("<")
            .append(NodeTypes.MENTION.toString())
            .append(" uid=\"")
            .append(id.get("value").toString())
            .append("\" />");
      }
    } catch (IOException e) {
      parsedSpan.append(value);
    }

    return parsedSpan.toString();
  }

  /**
   * Verify a string changing any emoji shortcodes with the correct MessageML emoji tags
   * @param message String with the possible emojis
   * @return String with any emojis processed into MessageML tags
   */
  private static String placeEmojis(String message) {
    StringBuffer result = new StringBuffer();
    Matcher matcher = Pattern.compile(":([a-z0-9_+-]+):").matcher(message);

    if (!matcher.find()) {
      return message;
    } else {
      do {
        matcher.appendReplacement(result, "<emoji shortcode=\"" + matcher.group(1) + "\" />");
      } while (matcher.find());
    }

    return result.toString();
  }
}
