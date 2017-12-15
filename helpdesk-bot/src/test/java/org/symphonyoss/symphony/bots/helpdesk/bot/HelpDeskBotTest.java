package org.symphonyoss.symphony.bots.helpdesk.bot;

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
import org.symphonyoss.client.exceptions.InitException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.client.MembershipClient;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymUser;

/**
 * Created by rsanchez on 14/12/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskBotTest {

  private static final String MOCK_GROUP_ID = "mockgroupid";

  private static final long MOCK_USERID = 123456;

  @Mock
  private HelpDeskBotConfig configuration;

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private MembershipClient membershipClient;

  @Mock
  private UsersClient usersClient;

  private HelpDeskBot helpDeskBot;

  @Before
  public void init() throws UsersClientException {
    this.helpDeskBot = new HelpDeskBot(configuration, symphonyClient, membershipClient);

    SymUser user = new SymUser();
    user.setId(MOCK_USERID);

    doReturn(user).when(symphonyClient).getLocalUser();

    doReturn(MOCK_GROUP_ID).when(configuration).getGroupId();
  }

  @Test
  public void testMissingGroupId() throws InitException {
    doReturn(null).when(configuration).getGroupId();

    try {
      this.helpDeskBot.validateGroupId();
      fail();
    } catch (IllegalStateException e) {
      assertEquals("GroupId were not provided", e.getMessage());
    }
  }

  @Test
  public void testCreateMembership() throws InitException, UsersClientException {
    this.helpDeskBot.registerDefaultAgent();
    verify(membershipClient, times(1)).newMembership(MOCK_USERID, MembershipClient.MembershipType.AGENT);
  }

  @Test
  public void testUpdateMembership() throws InitException, UsersClientException {
    Membership membership = new Membership();

    doReturn(membership).when(membershipClient).getMembership(MOCK_USERID);

    this.helpDeskBot.registerDefaultAgent();

    verify(membershipClient, times(1)).updateMembership(membership);
  }
}
