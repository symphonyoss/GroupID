package makerchecker.model;

import com.symphony.bots.ai.impl.SymphonyAiMessage;
/**
 * Created by nick.tarsillo on 9/27/17.
 * Represents a check, used by the maker checker.
 */
public abstract class Checker {
  private String checkFailureMessage;

  /**
   * If check fails, verification will be required from another user.
   * @param message the message to validate
   * @return if the check passed
   */
   public abstract boolean check(SymphonyAiMessage message);

  public String getCheckFailureMessage() {
    return checkFailureMessage;
  }

  public void setCheckFailureMessage(String checkFailureMessage) {
    this.checkFailureMessage = checkFailureMessage;
  }
}
