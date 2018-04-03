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
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
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

  @Mock
  private SymphonyClient symphonyClient;

  private MembershipService membershipService;

  private static final String STREAM_ID = "STREAM_ID";

  private static final Long AGENT_ID = 012345L;

  private static final Long CLIENT_ID = 678900L;

  private static final String GROUP_ID = "GROUP_ID";

  private static final String JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9."
      + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9";

  @Before
  public void initMocks() {
    membershipService =
        new MembershipService(membershipClient, symphonyClient);

    Token sessionToken = new Token();
    sessionToken.setToken(JWT);

    SymAuth symAuth = new SymAuth();
    symAuth.setSessionToken(sessionToken);

    doReturn(symAuth).when(symphonyClient).getSymAuth();
  }

  @Test
  public void getMembershipNull() {
    SymMessage symMessage = new SymMessage();
    symMessage.setStreamId(STREAM_ID);

    doReturn(null).when(membershipClient).getMembership(JWT, AGENT_ID);

    Membership membership = membershipService.getMembership(AGENT_ID);

    verify(membershipClient, times(1)).getMembership(JWT, AGENT_ID);
    assertEquals(null,membership);

  }

  @Test
  public void createMembershipClient() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(CLIENT_ID);
    Membership client = getMembershipClient();

    doReturn(null).when(membershipClient).getMembership(JWT, CLIENT_ID);

    doReturn(client).when(membershipClient).newMembership(JWT, CLIENT_ID,
        MembershipClient.MembershipType.CLIENT);

    Membership actual = membershipService.updateMembership(symMessage, MembershipClient.MembershipType.CLIENT);

    verify(membershipClient, times(1)).newMembership(JWT, CLIENT_ID, MembershipClient.MembershipType.CLIENT);

    assertEquals(client, actual);

  }

  @Test
  public void createMembershipAgent() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(AGENT_ID);

    Membership agent = getMembershipAgent();

    doReturn(null).when(membershipClient).getMembership(JWT, AGENT_ID);

    doReturn(agent).when(membershipClient)
        .newMembership(JWT, AGENT_ID, MembershipClient.MembershipType.AGENT);

    Membership actual = membershipService.updateMembership(symMessage, MembershipClient.MembershipType.AGENT);

    verify(membershipClient, times(1)).newMembership(JWT, AGENT_ID, MembershipClient.MembershipType.AGENT);

    assertEquals(agent, actual);
  }

  @Test
  public void updateMembership() {
    SymMessage symMessage = new SymMessage();
    symMessage.setFromUserId(CLIENT_ID);
    Membership client = getMembershipClient();
    Membership agent = getMembershipClientUpdated();

    doReturn(client).when(membershipClient).getMembership(JWT, CLIENT_ID);

    doReturn(agent).when(membershipClient).updateMembership(JWT, client);

    Membership actual = membershipService.updateMembership(symMessage, MembershipClient.MembershipType.AGENT);

    verify(membershipClient, times(1)).updateMembership(JWT, client);

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
