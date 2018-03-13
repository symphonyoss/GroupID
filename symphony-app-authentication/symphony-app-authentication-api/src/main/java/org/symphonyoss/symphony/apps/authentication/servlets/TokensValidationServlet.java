package org.symphonyoss.symphony.apps.authentication.servlets;

import org.symphonyoss.symphony.apps.authentication.AppAuthenticationService;
import org.symphonyoss.symphony.apps.authentication.servlets.model.ErrorResponse;
import org.symphonyoss.symphony.apps.authentication.tokens.StoreTokensProvider;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet class to receive HTTP POST requests in order to get the app and symphony tokens and
 * validate them according to the information saved in the datastore.
 * <p>
 * This servlet requires the class implementation of {@link StoreTokensProvider} to retrieve the app/symphony
 * tokens.
 * Created by rsanchez on 06/03/18.
 */
public class TokensValidationServlet extends AppBaseServlet {

  private static final String UNAUTHORIZED_PAIR = "The provided application token %s and "
      + "Symphony token %s are invalid. Please restart the authentication process";

  private AppAuthenticationService authenticationService = new AppAuthenticationService();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    AppToken reqPayload = readPostJsonObject(request, AppToken.class);

    String appId = reqPayload.getAppId();
    String appToken = reqPayload.getAppToken();
    String symphonyToken = reqPayload.getSymphonyToken();

    if (appId == null) {
      missingParameter(response, "appId");
      return;
    }

    if (appToken == null) {
      missingParameter(response, "appToken");
      return;
    }

    if (symphonyToken == null) {
      missingParameter(response, "symphonyToken");
      return;
    }

    boolean isValid = authenticationService.isValidTokenPair(appToken, symphonyToken);

    if (isValid) {
      AppToken result = new AppToken(appId, appToken, symphonyToken);
      writeJsonObject(response, result, HttpServletResponse.SC_OK);
    } else {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
      errorResponse.setMessage(String.format(UNAUTHORIZED_PAIR, appToken, symphonyToken));

      writeJsonObject(response, errorResponse, errorResponse.getCode());
    }
  }

}
