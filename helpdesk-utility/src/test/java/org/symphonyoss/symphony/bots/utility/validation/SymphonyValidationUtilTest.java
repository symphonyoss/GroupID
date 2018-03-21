package org.symphonyoss.symphony.bots.utility.validation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.StreamsClient;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.model.SymStreamAttributes;
import org.symphonyoss.symphony.clients.model.SymUser;

import javax.ws.rs.BadRequestException;

@RunWith(MockitoJUnitRunner.class)
public class SymphonyValidationUtilTest {

  private static final Long USER_ID = 123L;
  private static final String DISPLAY_NAME = "DISPLAY_NAME";
  private static final String STREAM_ID = "STREAM_ID";

  @Mock
  private SymphonyClient symphonyClient;

  @Mock
  private UsersClient usersClient;

  @Mock
  private StreamsClient streamsClient;

  private SymphonyValidationUtil symphonyValidationUtil;

  @Before
  public void setUp() throws Exception {
    symphonyValidationUtil = new SymphonyValidationUtil(symphonyClient);

  }

  @Test
  public void validateUserId() throws UsersClientException {

    doReturn(usersClient).when(symphonyClient).getUsersClient();
    doReturn(getSymUser()).when(usersClient).getUserFromId(USER_ID);
    SymUser symUser = symphonyValidationUtil.validateUserId(USER_ID);

    assertNotNull(symUser);
    assertEquals(getSymUser(),symUser);

  }

  @Test(expected = BadRequestException.class)
  public void validateUserIdThrowsException() throws UsersClientException {

    doReturn(usersClient).when(symphonyClient).getUsersClient();
    doThrow(UsersClientException.class).when(usersClient).getUserFromId(null);
    symphonyValidationUtil.validateUserId(null);

  }

  @Test
  public void validateStream() throws StreamsException {

    doReturn(streamsClient).when(symphonyClient).getStreamsClient();
    doReturn(getStream()).when(streamsClient).getStreamAttributes(STREAM_ID);
    SymStreamAttributes streamAttributes = symphonyValidationUtil.validateStream(STREAM_ID);

    assertNotNull(streamAttributes);
    assertEquals(getStream().getId(), streamAttributes.getId());

  }

  @Test(expected = BadRequestException.class)
  public void validateStreamThrowsError() throws StreamsException {
    doReturn(streamsClient).when(symphonyClient).getStreamsClient();
    doThrow(StreamsException.class).when(streamsClient).getStreamAttributes(STREAM_ID);
    symphonyValidationUtil.validateStream(STREAM_ID);

  }

  private SymUser getSymUser() {
    SymUser user = new SymUser();
    user.setId(USER_ID);
    user.setDisplayName(DISPLAY_NAME);
    return user;
  }

  private SymStreamAttributes getStream() {
    SymStreamAttributes attributes = new SymStreamAttributes();
    attributes.setId(STREAM_ID);
    return attributes;

  }
}