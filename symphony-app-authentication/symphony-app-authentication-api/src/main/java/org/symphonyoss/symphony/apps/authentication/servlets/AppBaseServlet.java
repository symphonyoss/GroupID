package org.symphonyoss.symphony.apps.authentication.servlets;

import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract servlet class that contains useful methods to be reused for other servlets.
 * <p>
 * Created by rsanchez on 06/03/18.
 */
public abstract class AppBaseServlet extends HttpServlet {

  private static final String MISSING_PARAMETER = "Missing the required parameter %s";

  public static final String CONTENT_TYPE = "Content-Type";

  public static final String APPLICATION_JSON = "application/json";

  /**
   * Read HTTP request payload.
   *
   * @param request HTTP request
   * @return HTTP request payload converted to string.
   * @throws IOException Failure to read HTTP request payload
   */
  protected String readPostBody(HttpServletRequest request) throws IOException {
    StringBuilder buffer = new StringBuilder();

    try (BufferedReader reader = request.getReader()) {
      String line;

      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
    }

    return buffer.toString();
  }

  /**
   * Read HTTP request payload that contains JSON object.
   *
   * @param request HTTP request
   * @param clazz Class to be deserialized
   * @return HTTP request payload converted to object.
   * @throws IOException Failure to read HTTP request payload or to deserialize JSON document
   */
  protected <T> T readPostJsonObject(HttpServletRequest request, Class<T> clazz) throws IOException {
    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String body = readPostBody(request);

    return parser.writeToObject(body, clazz);
  }

  /**
   * Write HTTP response (200 OK).
   *
   * @param response HTTP response
   * @param payload HTTP response payload
   * @param status HTTP status
   * @throws IOException Failure to write HTTP response
   */
  protected void writeResponse(HttpServletResponse response, String payload, int status) throws IOException {
    response.setStatus(status);
    response.getWriter().write(payload);
  }

  /**
   * Write HTTP response (200 OK) that contains JSON object.
   *
   * @param response HTTP response
   * @param payload HTTP response payload
   * @param status HTTP status
   * @throws IOException Failure to write HTTP response or to serialize JSON document
   */
  protected void writeJsonObject(HttpServletResponse response, Object payload, int status) throws IOException {
    JsonParser parser = JsonParserFactory.getInstance().getComponent();
    String content = parser.writeToString(payload);

    writeResponse(response, content, status);

    response.addHeader(CONTENT_TYPE, APPLICATION_JSON);
  }

  /**
   * Write HTTP response (400 Bad Request) due to missing required parameter.
   *
   * @param response HTTP response
   * @param param Parameter name
   * @throws IOException Failure to write HTTP response or to serialize JSON document
   */
  protected void missingParameter(HttpServletResponse response, String param) throws IOException {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
    errorResponse.setMessage(String.format(MISSING_PARAMETER, param));

    writeJsonObject(response, errorResponse, errorResponse.getCode());
  }
}
