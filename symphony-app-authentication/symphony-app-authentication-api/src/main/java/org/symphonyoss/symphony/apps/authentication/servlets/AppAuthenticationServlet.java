package org.symphonyoss.symphony.apps.authentication.servlets;

import org.symphonyoss.symphony.apps.authentication.AppAuthenticationException;
import org.symphonyoss.symphony.apps.authentication.AppAuthenticationService;
import org.symphonyoss.symphony.apps.authentication.servlets.model.AppInfo;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet class to receive HTTP POST requests in order to get the required information (appId and podId)
 * and perform the app authentication in the cloud.
 * <p>
 * This servlet requires the class implementation to get POD and Session auth base URL's, to retrieve
 * app keystore, and to store the app/symphony tokens
 * <p>
 * Created by rsanchez on 06/03/18.
 */
public class AppAuthenticationServlet extends AppBaseServlet {

  private static final String UNAUTHORIZED_MESSAGE = "The provided POD ID %s is not the same "
      + "used by this application.";

  private AppAuthenticationService authenticationService = new AppAuthenticationService();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    AppInfo reqPayload = readPostJsonObject(request, AppInfo.class);
    String appId = reqPayload.getAppId();

    if (appId == null) {
      missingParameter(response, "appId");
      return;
    }

    try {
      AppToken appToken = authenticationService.authenticate(appId);
      writeJsonObject(response, appToken, HttpServletResponse.SC_OK);
    } catch (AppAuthenticationException e) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      errorResponse.setMessage(e.getMessage());

      writeJsonObject(response, errorResponse, errorResponse.getCode());
    }

  }

}
