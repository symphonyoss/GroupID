package org.symphonyoss.symphony.apps.authentication.servlets;

import org.symphonyoss.symphony.apps.authentication.jwt.JwtService;
import org.symphonyoss.symphony.apps.authentication.jwt.exception.JwtProcessingException;
import org.symphonyoss.symphony.apps.authentication.jwt.model.JwtPayload;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.servlets.model.JwtInfo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet class to receive HTTP POST requests in order to validate the JSON Web Token provided
 * in the request.
 * <p>
 * Created by rsanchez on 06/03/18.
 */
public class JwtValidationServlet extends AppBaseServlet {

  private static final Integer DEFAULT_CACHE_TIMEOUT = 60;

  private static final Integer DEFAULT_CACHE_SIZE = 1000;

  private static final String UNAUTHORIZED_JWT = "The provided JWT token '%s' is invalid and "
      + "therefore unauthorized. More information: %s.";

  private JwtService service = new JwtService(DEFAULT_CACHE_TIMEOUT, DEFAULT_CACHE_SIZE);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    JwtInfo reqPayload = readPostJsonObject(request, JwtInfo.class);
    String jwt = reqPayload.getJwt();

    if (jwt == null) {
      missingParameter(response, "jwt");
      return;
    }

    try {
      JwtPayload payload = service.parseJwtPayload(jwt);
      String userId = payload.getUserId();

      writeResponse(response, userId, HttpServletResponse.SC_OK);
    } catch (JwtProcessingException e) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
      errorResponse.setMessage(String.format(UNAUTHORIZED_JWT, jwt, e.getMessage()));

      writeJsonObject(response, errorResponse, errorResponse.getCode());
    }
  }

}
