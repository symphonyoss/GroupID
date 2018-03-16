package org.symphonyoss.symphony.apps.authentication.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.jwt.JwtService;
import org.symphonyoss.symphony.apps.authentication.jwt.exception.JwtProcessingException;
import org.symphonyoss.symphony.apps.authentication.jwt.model.JwtPayload;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link JwtValidationServlet}
 *
 * Created by rsanchez on 07/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtValidationServletTest {

  private static final String MOCK_JWT_PAYLOAD = "{ \"jwt\": \"json.web.token\" }";

  private static final String MOCK_JWT = "json.web.token";

  private static final String MOCK_USER_ID = "userId";

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private JwtService service;

  @Mock
  private BufferedReader reader;

  @Mock
  private PrintWriter writer;

  @InjectMocks
  private JwtValidationServlet servlet;

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
  public void testMissingJwt() throws IOException, ServletException {
    doReturn("{}").doReturn(null).when(reader).readLine();

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage("Missing the required parameter jwt");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testInvalidJwt() throws IOException, ServletException, JwtProcessingException {
    JwtProcessingException exception = new JwtProcessingException("invalid", new RuntimeException());

    doReturn(MOCK_JWT_PAYLOAD).doReturn(null).when(reader).readLine();
    doThrow(exception).when(service).parseJwtPayload(MOCK_JWT);

    servlet.doPost(request, response);

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
    errorResponse.setMessage(
        "The provided JWT token 'json.web.token' is invalid and therefore unauthorized. More "
            + "information: invalid.");

    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String expected = parser.writeToString(errorResponse);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(writer, times(1)).write(expected);
    verify(response, times(1)).addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  @Test
  public void testJwt() throws IOException, ServletException, JwtProcessingException {
    JwtPayload payload = new JwtPayload();
    payload.setUserId(MOCK_USER_ID);

    doReturn(MOCK_JWT_PAYLOAD).doReturn(null).when(reader).readLine();
    doReturn(payload).when(service).parseJwtPayload(MOCK_JWT);

    servlet.doPost(request, response);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    verify(writer, times(1)).write(MOCK_USER_ID);
  }

}
