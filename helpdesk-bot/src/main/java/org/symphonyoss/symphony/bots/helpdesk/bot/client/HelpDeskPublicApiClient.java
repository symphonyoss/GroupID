package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertDetail;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

/**
 * Provides acess to public APIs of a POD.
 * Created by campidelli on 03/12/17.
 */
@Service
@Lazy
public class HelpDeskPublicApiClient {

  private static final String GET_SALT_ENDPOINT = "login/salt?userName=%s";

  private static final String LOGIN_ENDPOINT = "login/username_password";

  private static final String CREATE_CERTIFICATE_ENDPOINT = "pod/v2/companycert/create";

  private static final String SESSION_KEY = "skey";

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  @Autowired
  private HelpDeskBotConfig config;

  @Autowired
  private RestTemplate restTemplate;

  /**
   * Gets a generated salt for a given userName.
   * @param userName User name.
   * @return Generated salt.
   */
  public String getSalt(String userName) {
    String endpoint = config.getPod().getUrl(GET_SALT_ENDPOINT);
    endpoint = String.format(endpoint, userName);

    ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
    String json = response.getBody();

    try {
      JsonNode node = JSON_MAPPER.readTree(json);
      return node.get("salt").asText();
    } catch (IOException e) {
      String msg = String.format("[%s] is not a valid JSON.", json);
      throw new IllegalStateException(msg, e);
    }
  }

  /**
   * Performs a login in a POD.
   * @param userName User name;
   * @param encryptedPassword Password (must be encrypted)
   * @return Session key.
   */
  public String login(String userName, String encryptedPassword) {
    String endpoint = config.getPod().getUrl(LOGIN_ENDPOINT);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("userName", userName);
    formData.add("hPassword", encryptedPassword);

    String sessionKey = null;

    ResponseEntity<String> response = restTemplate.postForEntity(endpoint, formData, String.class);
    if (response.getHeaders().containsKey(HttpHeaders.SET_COOKIE)) {
      List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
      String cookie = cookies.stream().filter(c -> c.startsWith(SESSION_KEY)).findFirst().get();
      HttpCookie sKeyCookie = HttpCookie.parse(cookie).get(0);
      sessionKey = sKeyCookie.getValue();
    }
    return sessionKey;
  }

  /**
   * Uploads a certificate as trusted in the configured POD.
   * @param sessionToken Session token to authorize this upload.
   * @param certificate Generated certificate.
   * @return Certificate's detail.
   */
  public CompanyCertDetail createCompanyCert(String sessionToken, CompanyCert certificate) {

    if (sessionToken == null) {
      throw new IllegalStateException("[sessionToken] must be informed.");
    }
    if (certificate == null) {
      throw new IllegalStateException("[certificate] must be informed.");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("sessionToken", sessionToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CompanyCert> entity = new HttpEntity<CompanyCert>(certificate, headers);

    String endpoint = config.getPod().getUrl(CREATE_CERTIFICATE_ENDPOINT);

    ResponseEntity<CompanyCertDetail> response = restTemplate.postForEntity(
        endpoint, entity, CompanyCertDetail.class);

    return response.getBody();
  }

}
