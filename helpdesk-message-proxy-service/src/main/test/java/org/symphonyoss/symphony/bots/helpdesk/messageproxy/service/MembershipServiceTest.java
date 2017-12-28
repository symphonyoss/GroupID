package org.symphonyoss.symphony.bots.helpdesk.messageproxy.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.model.SymMessage;

/**
 * Created by alexandre-silva-daitan on 19/12/17
 */

@RunWith(MockitoJUnitRunner.class)
public class MembershipServiceTest {

  @Mock
  private MembershipClient membershipClient;

  private MembershipService membershipService;

  private static final String STREAM_ID = "STREAM_ID";

  private static final Long AGENT_ID = 012345L;

  private static final Long CLIENT_ID = 678900L;

  private static final String GROUP_ID = "GROUP_ID";

  @Before
  public void initMocks() {
    membershipService =
        new MembershipService(membershipClient);
  }

  @Test
  public void getMembershipNull() {
    SymMessage symMessage = new SymMessage();
    symMessage.setStreamId(STREAM_ID);

    doReturn(null).when(membershipClient).getMembership(AGENT_ID);

    Membership membership = membershipService.getMembership(AGENT_ID);

    verify(membershipClient, times(1)).getMembership(AGENT_ID);
    assertEquals(null,membership);

  }

  @Test
  public void createMembershipClient() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(CLIENT_ID);
    Membership client = getMembershipClient();

    doReturn(null).when(membershipClient).getMembership(CLIENT_ID);

    doReturn(client).when(membershipClient).newMembership(CLIENT_ID,
        MembershipClient.MembershipType.CLIENT);

    Membership actual = membershipService.updateMembership(symMessage, MembershipClient.MembershipType.CLIENT);

    verify(membershipClient, times(1)).newMembership(CLIENT_ID, MembershipClient.MembershipType.CLIENT);

    assertEquals(client, actual);

  }

  @Test
  public void createMembershipAgent() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(AGENT_ID);

    Membership agent = getMembershipAgent();

    doReturn(null).when(membershipClient).getMembership(AGENT_ID);

    doReturn(agent).when(membershipClient).newMembership(AGENT_ID,
        MembershipClient.MembershipType.AGENT);

    Membership actual = membershipService.updateMembership(symMessage, MembershipClient.MembershipType.AGENT);

    verify(membershipClient, times(1)).newMembership(AGENT_ID, MembershipClient.MembershipType.AGENT);

    assertEquals(agent, actual);
  }

  @Test
  public void updateMembership() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(CLIENT_ID);
    Membership client = getMembershipClient();
    Membership agent = getMembershipClientUpdated();

    doReturn(client).when(membershipClient).getMembership(CLIENT_ID);

    doReturn(agent).when(membershipClient).updateMembership(client);

    Membership actual = membershipService.updateMembership(symMessage, MembershipClient.MembershipType.AGENT);

    verify(membershipClient, times(1)).updateMembership(client);

    assertEquals(agent, actual);
  }


  private Membership getMembershipAgent() {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.AGENT.getType());
    membership.setGroupId(GROUP_ID);
    membership.setId(AGENT_ID);

    return membership;
  }

  private Membership getMembershipClient() {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.CLIENT.getType());
    membership.setGroupId(GROUP_ID);
    membership.setId(CLIENT_ID);

    return membership;
  }

  private Membership getMembershipClientUpdated() {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.AGENT.getType());
    membership.setGroupId(GROUP_ID);
    membership.setId(CLIENT_ID);

    return membership;
  }
}
