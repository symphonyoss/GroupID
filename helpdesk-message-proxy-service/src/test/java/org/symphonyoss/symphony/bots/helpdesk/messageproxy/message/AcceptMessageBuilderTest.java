package org.symphonyoss.symphony.bots.helpdesk.messageproxy.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.symphony.bots.helpdesk.service.model.UserInfo;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by crepache on 30/01/18.
 */
public class AcceptMessageBuilderTest {

  private static final Long AGENGET_ID = 23011987l;

  private static final String DISPLAY_NAME_AGENT = "Agent User";

  private static final String MOCK_TICKET = "ABCDEFG";

  private static final String EXPECTED_ENTITY = "{\"helpdesk\":{\"type\":\"com.symphony.bots"
      + ".helpdesk.event.ticket.claimed.accept\",\"version\":\"1.0\",\"ticketId\":\"ABCDEFG\","
      + "\"state\":\"UNRESOLVED\",\"agent\":{\"type\":\"com.symphony.bots.helpdesk.event.ticket"
      + ".claimed.user\",\"version\":\"1.0\",\"displayName\":\"Agent User\"}}}";

  @Test
  public void testMessage() {
    AcceptMessageBuilder acceptMessageBuilder = new AcceptMessageBuilder();
    String expectedMessage = acceptMessageBuilder.getMessageTemplate();

    SymMessage symMessage = acceptMessageBuilder.ticketState(TicketClient.TicketStateType.UNRESOLVED.getState())
        .agent(mockUserInfo())
        .ticketId(MOCK_TICKET)
        .build();

    assertEquals(expectedMessage, symMessage.getMessage());
    assertEquals(EXPECTED_ENTITY, symMessage.getEntityData());
  }


  private UserInfo mockUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(AGENGET_ID);
    userInfo.setDisplayName(DISPLAY_NAME_AGENT);

    return userInfo;
  }

}
