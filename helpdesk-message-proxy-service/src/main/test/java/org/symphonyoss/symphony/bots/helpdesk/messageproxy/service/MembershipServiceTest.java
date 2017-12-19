package org.symphonyoss.symphony.bots.helpdesk.messageproxy;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient.MembershipType.AGENT;
import static org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient.MembershipType.CLIENT;

/**
 * Created by alexandre-silva-daitan on 19/12/17
 */

@RunWith(MockitoJUnitRunner.class)
public class MembershipServiceTest {

  @Mock
  private MembershipClient membershipClient;

  private static final String STREAM_ID = "STREAM_ID";

  @Test
  public void createMembershipClient() {
    MembershipService membershipService = new MembershipService(membershipClient);
    SymMessage message = new SymMessage();
    message.setStreamId(STREAM_ID);

    MembershipType type type = new MembershipType(CLIENT);

    when(symMessage.getFromUserId()).thenReturn(STREAM_ID);
    Long userId = symMessage.getFromUserId();

    when(getMembership(userId)).thenReturn(null);
    Membership membership = getMembership(userId);

    when(membership == null).thenReturn(true);
    if(membership == null) {
      membership = createMembership(userId, type);
    }

    verify(membershipService, times(1)).createMembership(userId, CLIENT);
  }

  @Test
  public void createMembershipAgent() {
    MembershipService membershipService = new MembershipService(membershipClient);
    SymMessage message = new SymMessage();
    message.setStreamId(STREAM_ID);

    MembershipType type type = new MembershipType(AGENT);

    when(symMessage.getFromUserId()).thenReturn(STREAM_ID);
    Long userId = symMessage.getFromUserId();

    when(getMembership(userId)).thenReturn(null);
    Membership membership = getMembership(userId);

    when(membership == null).thenReturn(false);
    when(AGENT.equals(type) && !type.toString().equals(membership.getType())).thenReturn(true);

    if(membership == null) {
      membership = createMembership(userId, type);
    } else if(AGENT.equals(type) && !type.toString().equals(membership.getType())) {
      membership.setType(AGENT.getType());
      membership = updateMembership(membership);
    }

    verify(membershipService, times(1)).createMembership(userId, AGENT);
  }

}
