package org.symphonyoss.symphony.apps.authentication.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.symphonyoss.symphony.apps.authentication.servlets.AppBaseServlet.APPLICATION_JSON;
import static org.symphonyoss.symphony.apps.authentication.servlets.AppBaseServlet.CONTENT_TYPE;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.AppAuthenticationException;
import org.symphonyoss.symphony.apps.authentication.AppAuthenticationService;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link TokensValidationServlet}
 *
 * Created by rsanchez on 07/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class TokensValidationServletTest {

  private static final String APP_ID = "APP_ID";

  private static final String APP_TOKEN = "ABCD";

  private static final String SYMPHONY_TOKEN = "EFGH";

  private static final String MOCK_TOKENS = "{ \"appId\": \"APP_ID\", \"appToken\": \"ABCD\", \""
      + "symphonyToken\": \"EFGH\" }";

  private static final String MISSING_APP_TOKEN = "{ \"appId\": \"APP_ID\" }";

  private static final String MISSING_SYMPHONY_TOKEN = "{ \"appId\": \"APP_ID\", \"appToken\": \"ABCD\" }";

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private BufferedReader reader;

  @Mock
  private PrintWriter writer;

  @Mock
  private AppAuthenticationService authenticationService;

  @InjectMocks
  private TokensValidationServlet servlet;

  @BeforeClass
  public static void setup() {
    JsonParserFactory.getInstance().setComponent(new JacksonParser());
  }

  @Before
  public void init() throws IOException {
    doReturn(reader).when(request).getReader();
    doReturn(writer).when(response).getWriter();
  }

  @Test
  public void testMissingAppId() throws IOException, ServletException {
    doReturn("{}").doReturn(null).when(reader).readLine();

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage("Missing the required parameter appId");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testMissingAppToken() throws IOException, ServletException {
    doReturn(MISSING_APP_TOKEN).doReturn(null).when(reader).readLine();

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage("Missing the required parameter appToken");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testMissingSymphonyToken() throws IOException, ServletException {
    doReturn(MISSING_SYMPHONY_TOKEN).doReturn(null).when(reader).readLine();

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage("Missing the required parameter symphonyToken");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testInvalidTokens()
      throws IOException, ServletException, AppAuthenticationException {
    doReturn(MOCK_TOKENS).doReturn(null).when(reader).readLine();
    doReturn(false).when(authenticationService).isValidTokenPair(APP_TOKEN, SYMPHONY_TOKEN);

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
    errorResponse.setMessage("The provided application token ABCD and Symphony token EFGH are invalid."
        + " Please restart the authentication process");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testValidTokens()
      throws IOException, ServletException, AppAuthenticationException {
    doReturn(MOCK_TOKENS).doReturn(null).when(reader).readLine();
    doReturn(true).when(authenticationService).isValidTokenPair(APP_TOKEN, SYMPHONY_TOKEN);

    servlet.doPost(request, response);

    AppToken tokens = new AppToken(APP_ID, APP_TOKEN, SYMPHONY_TOKEN);

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(tokens);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }
}
