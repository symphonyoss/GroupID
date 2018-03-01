package org.symphonyoss.symphony.bots.utility.message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.symphonyoss.symphony.clients.model.SymAttachmentInfo;
import org.symphonyoss.symphony.clients.model.SymMessage;

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
}
