package org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.dao.memory.InMemoryAppTokenDao;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.exception
    .RetrieveTokensException;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.exception.SaveTokensException;
import org.symphonyoss.symphony.bots.helpdesk.service.authentication.model.HelpDeskAppToken;

/**
 * Unit tests for {@link MongoAppTokenDao}
 *
 * Created by rsanchez on 13/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class MongoAppTokenDaoTest {

  private static final String COLLECTION_NAME = "helpdeskapptokens";

  private static final String APP_ID = "ID";

  private static final String APP_TOKEN = "ABCD";

  @Mock
  private MongoTemplate template;

  private MongoAppTokenDao dao;

  @Before
  public void init() {
    this.dao = new MongoAppTokenDao(template);
  }

  @Test
  public void testSaveFailure() {
    AppToken token = new AppToken();
    token.setAppId(APP_ID);
    token.setAppToken(APP_TOKEN);

    HelpDeskAppToken appToken = new HelpDeskAppToken(token);

    doThrow(RuntimeException.class).when(template).save(appToken, COLLECTION_NAME);

    try {
      dao.saveAppToken(appToken);
      fail();
    } catch (SaveTokensException e) {
      assertEquals("Failed to save app token. App ID: " + APP_ID, e.getMessage());
    }
  }

  @Test
  public void testSave() {
    AppToken token = new AppToken();
    token.setAppId(APP_ID);
    token.setAppToken(APP_TOKEN);

    HelpDeskAppToken appToken = new HelpDeskAppToken(token);

    assertEquals(appToken, dao.saveAppToken(appToken));

    verify(template, times(1)).save(appToken, COLLECTION_NAME);
  }

  @Test
  public void testGetFailure() {
    Criteria criteria = where("appToken").is(APP_TOKEN);

    doThrow(RuntimeException.class).when(template)
        .findOne(query(criteria), HelpDeskAppToken.class, COLLECTION_NAME);

    try {
      dao.getAppToken(APP_TOKEN);
      fail();
    } catch (RetrieveTokensException e) {
      assertEquals("Failed to get app token. App token: " + APP_TOKEN, e.getMessage());
    }
  }

  @Test
  public void testGet() {
    AppToken token = new AppToken();
    token.setAppId(APP_ID);
    token.setAppToken(APP_TOKEN);

    HelpDeskAppToken appToken = new HelpDeskAppToken(token);

    Criteria criteria = where("appToken").is(APP_TOKEN);

    doReturn(appToken).when(template)
        .findOne(query(criteria), HelpDeskAppToken.class, COLLECTION_NAME);

    assertEquals(appToken, dao.getAppToken(APP_TOKEN));
  }
}
