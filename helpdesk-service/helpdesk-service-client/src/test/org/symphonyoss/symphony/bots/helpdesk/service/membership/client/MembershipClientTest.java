package org.symphonyoss.symphony.bots.helpdesk.service.membership.client;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

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
  private final String groupId = "GROUP_ID";

  private final String TICKET_SERVICE_URL = "https://localhost/helpdesk-service";

  @Mock
  private ApiClient apiClient;

  private MembershipClient membershipClient;

  @Before
  public void setUp() throws Exception {
    Configuration.setDefaultApiClient(apiClient);
    membershipClient = new MembershipClient(groupId, TICKET_SERVICE_URL);
  }

  @Test
  public void getMembership() throws ApiException {

    doReturn(groupId).when(apiClient).escapeString(groupId);
    doReturn(USER_ID.toString()).when(apiClient).escapeString(USER_ID.toString());

    doReturn(getMembership("agent")).when(apiClient)
        .invokeAPI(any(), eq("GET"), any(), any(), any(), any(), any(), any(), any(), any());

    Membership membership = membershipClient.getMembership(USER_ID);

    assertNotNull(membership);
    assertEquals(getMembership("agent"), membership);
  }

  @Test(expected = HelpDeskApiException.class)
  public void getMembershipWithError() throws ApiException {

    membershipClient.getMembership(null);

  }

  @Test
  public void newMembership() throws ApiException {
    doReturn(getMembership("client")).when(apiClient)
        .invokeAPI(any(), eq("POST"), any(), any(), any(), any(), any(), any(), any(), any());

    Membership membership =
        membershipClient.newMembership(USER_ID, MembershipClient.MembershipType.CLIENT);

    assertEquals(membership, getMembership("client"));
  }

  @Test
  public void updateMembership() throws ApiException {

    doReturn(groupId).when(apiClient).escapeString(groupId);
    doReturn(NOVO_ID.toString()).when(apiClient).escapeString(NOVO_ID.toString());

    Membership membership = getMembership("agent");
    membership.setId(NOVO_ID);

    doReturn(membership).when(apiClient).invokeAPI(any(),eq("PUT"),any(),any(),any(),any(),any(),any(),any(),any());
    Membership updatedMembership = membershipClient.updateMembership(membership);

    assertNotNull(updatedMembership);
    assertFalse(updatedMembership.equals(getMembership("agent")));
  }

  @Test(expected = HelpDeskApiException.class)
  public void updateMembershipWithError() throws ApiException {


    Membership membership = getMembership("agent");
    membership.setId(null);

    membershipClient.updateMembership(membership);
  }

  private Membership getMembership(String type) {
    Membership membership = new Membership();
    if (type.equals("agent")) {
      membership.setType(MembershipClient.MembershipType.AGENT.getType());
    } else {
      membership.setType(MembershipClient.MembershipType.CLIENT.getType());
    }
    membership.setGroupId(groupId);
    membership.setId(USER_ID);

    return membership;
  }
}