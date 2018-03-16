package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HelpDeskPublicApiClient {

  @Autowired
  HelpDeskBotConfig config;

  private static final String GET_SALT_ENDPOINT = "login/salt?userName=%s";

  private static final String LOGIN_ENDPOINT = "login/username_password";

  private static final String CREATE_CERTIFICATE_ENDPOINT = "pod/v2/companycert/create";

  private static final String SESSION_KEY = "skey";

  private RestTemplate restTemplate = new RestTemplate();

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
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = null;

    try {
      node = mapper.readTree(json);
    } catch (IOException e) {
      String msg = String.format("[%s] is not a valid JSON.", json);
      throw new IllegalStateException(msg, e);
    }

    return node.get("salt").asText();
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

    ResponseEntity<String> response = restTemplate.postForEntity(endpoint, formData, String.class);
    List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
    String cookie = cookies.stream().filter(c -> c.startsWith(SESSION_KEY)).findFirst().get();

    HttpCookie sKeyCookie = HttpCookie.parse(cookie).get(0);

    return sKeyCookie.getValue();
  }

  public CompanyCertDetail createCompanyCert(String sessionToken, CompanyCert certificate){

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
