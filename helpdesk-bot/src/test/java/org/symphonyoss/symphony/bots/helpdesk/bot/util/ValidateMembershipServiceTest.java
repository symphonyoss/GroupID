package org.symphonyoss.symphony.bots.helpdesk.bot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.RoomMembershipClient;
import org.symphonyoss.symphony.pod.model.MemberInfo;
import org.symphonyoss.symphony.pod.model.MembershipList;

import javax.ws.rs.BadRequestException;

/**
 * Created by robson on 21/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateMembershipServiceTest {

  private static final Long MOCK_USER_ID = 123456L;

  private static final String MOCK_AGENT_STREAM_ID = "Zs-nx3pQh3-XyKlT5B15m3___p_zHfetdA";

  private static final String JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9."
      + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9";

  @Mock
  private MembershipClient membershipClient;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private RoomMembershipClient roomMembershipClient;

  @Mock
  private HelpDeskBotConfig helpDeskBotConfig;

  private ValidateMembershipService validateMembershipService;

  @Before
  public void init() {
    doReturn(roomMembershipClient).when(symphonyClient).getRoomMembershipClient();
    doReturn(MOCK_AGENT_STREAM_ID).when(helpDeskBotConfig).getAgentStreamId();

    this.validateMembershipService =
        new ValidateMembershipService(membershipClient, symphonyClient, helpDeskBotConfig);

    Token sessionToken = new Token();
    sessionToken.setToken(JWT);

    SymAuth symAuth = new SymAuth();
    symAuth.setSessionToken(sessionToken);

    doReturn(symAuth).when(symphonyClient).getSymAuth();
  }

  @Test
  public void testUserNotFound() throws SymException {
    doReturn(new MembershipList()).when(roomMembershipClient).getRoomMembership(MOCK_AGENT_STREAM_ID);

    try {
      validateMembershipService.updateMembership(MOCK_USER_ID);
      fail();
    } catch (BadRequestException e) {
      assertEquals("User is not an agent", e.getMessage());
    }
  }

  @Test
  public void testUserIsAgent() throws SymException {
    MemberInfo memberInfo = new MemberInfo();
    memberInfo.setId(MOCK_USER_ID);

    MembershipList membershipList = new MembershipList();
    membershipList.add(memberInfo);

    doReturn(membershipList).when(roomMembershipClient).getRoomMembership(MOCK_AGENT_STREAM_ID);

    validateMembershipService.updateMembership(MOCK_USER_ID);

    verify(membershipClient, times(1)).newMembership(JWT, MOCK_USER_ID,
        MembershipClient.MembershipType.AGENT);
  }

  @Test
  public void testUserIsClient() throws SymException {
    Membership membership = new Membership();
    membership.setType(MembershipClient.MembershipType.CLIENT.getType());

    doReturn(membership).when(membershipClient).getMembership(JWT, MOCK_USER_ID);

    MemberInfo memberInfo = new MemberInfo();
    memberInfo.setId(MOCK_USER_ID);

    MembershipList membershipList = new MembershipList();
    membershipList.add(memberInfo);

    doReturn(membershipList).when(roomMembershipClient).getRoomMembership(MOCK_AGENT_STREAM_ID);

    validateMembershipService.updateMembership(MOCK_USER_ID);

    assertEquals(MembershipClient.MembershipType.AGENT.getType(), membership.getType());

    verify(membershipClient, times(1)).updateMembership(JWT, membership);
  }

}
