package org.symphonyoss.symphony.bots.helpdesk.service.membership.client;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

@RunWith(MockitoJUnitRunner.class)
public class MembershipClientTest {

  private static final Long USER_ID = 123L;
  private static final Long NOVO_ID = 345L;
  private static final String GROUP_ID = "GROUP_ID";

  private static final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  @Mock
  private ApiClient apiClient;

  private MembershipClient membershipClient;

  @Before
  public void setUp() throws Exception {
    Configuration.setDefaultApiClient(apiClient);
    membershipClient = new MembershipClient(GROUP_ID, TICKET_SERVICE_URL);
  }

  @Test
  public void getMembership() throws ApiException {

    doReturn(GROUP_ID).when(apiClient).escapeString(GROUP_ID);
    doReturn(USER_ID.toString()).when(apiClient).escapeString(USER_ID.toString());

    doReturn(getMembership("agent")).when(apiClient)
        .invokeAPI(eq("/v1/membership/GROUP_ID/123"), eq("GET"), any(), any(), any(), any(), any(),
            any(), any(), any());

    Membership membership = membershipClient.getMembership(USER_ID);

    assertNotNull(membership);
    assertEquals(getMembership("agent"), membership);
  }

  @Test
  public void getMembershipWithError() throws ApiException {

    try {
      membershipClient.getMembership(null);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Failed to get membership: " + null, e.getMessage());
    }

  }

  @Test
  public void newMembership() throws ApiException {
    doReturn(getMembership("client")).when(apiClient)
        .invokeAPI(eq("/v1/membership"), eq("POST"), any(), any(), any(), any(), any(), any(),
            any(), any());

    Membership membership =
        membershipClient.newMembership(USER_ID, MembershipClient.MembershipType.CLIENT);

    assertEquals(membership, getMembership("client"));
  }

  @Test
  public void newMembershipFail() throws ApiException {
    doThrow(ApiException.class).when(apiClient)
        .invokeAPI(eq("/v1/membership"), eq("POST"), any(), any(), any(), any(), any(), any(),
            any(), any());

    try {
      membershipClient.newMembership(USER_ID, MembershipClient.MembershipType.CLIENT);
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Failed to create new membership for user: " + USER_ID,
          e.getMessage());
    }

  }

  @Test
  public void updateMembership() throws ApiException {

    doReturn(GROUP_ID).when(apiClient).escapeString(GROUP_ID);
    doReturn(NOVO_ID.toString()).when(apiClient).escapeString(NOVO_ID.toString());

    Membership membership = getMembership("agent");
    membership.setId(NOVO_ID);

    doReturn(membership).when(apiClient)
        .invokeAPI(eq("/v1/membership/GROUP_ID/345"), eq("PUT"), any(), any(), any(), any(), any(),
            any(), any(), any());
    Membership updatedMembership = membershipClient.updateMembership(membership);

    assertNotNull(updatedMembership);
    assertNotEquals(getMembership("agent").getId(), updatedMembership.getId());
  }

  @Test
  public void updateMembershipWithError() throws ApiException {

    try {
      membershipClient.updateMembership(new Membership());
      fail();
    } catch (HelpDeskApiException e) {
      assertEquals("Could not update membership", e.getMessage());
    }
  }

  private Membership getMembership(String type) {
    Membership membership = new Membership();
    if (type.equals("agent")) {
      membership.setType(MembershipClient.MembershipType.AGENT.getType());
    } else {
      membership.setType(MembershipClient.MembershipType.CLIENT.getType());
    }
    membership.setGroupId(GROUP_ID);
    membership.setId(USER_ID);

    return membership;
  }
}