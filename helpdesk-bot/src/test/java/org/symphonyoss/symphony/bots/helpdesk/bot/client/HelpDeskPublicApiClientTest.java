package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.doReturn;

import jdk.nashorn.internal.runtime.CodeStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.HelpDeskBotConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.CertificateUtils;
import org.symphonyoss.symphony.bots.utility.config.ServiceInfo;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertDetail;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;

/**
 * Created by campidelli on 19/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskPublicApiClientTest {

  private static final String POD_PROTOCOL = "https://";
  private static final String POD_HOST = "www.symphony.com";
  private static final Integer POD_PORT = 443;
  private static final String ENDPOINT_PREFIX = POD_PROTOCOL + POD_HOST + ":" + POD_PORT;

  private static final String USER = "username";
  private static final String PASSWORD = "password";
  private static final String INVALID_JSON = "<xml>";

  private static final String GET_SALT_ENDPOINT = ENDPOINT_PREFIX + "/login/salt?userName=" + USER;
  private static final String LOGIN_ENDPOINT = ENDPOINT_PREFIX + "/login/username_password";
  private static final String CREATE_CERT_ENDPOINT = ENDPOINT_PREFIX + "/pod/v2/companycert/create";

  private static final String SALT_VALUE = "s41t";
  private static final String SALT_JSON = "{ \"salt\": \"" + SALT_VALUE + "\" }";

  private static final String SKEY = "skey";
  private static final String SKEY_VALUE = "123456";

  private static final String CERT_NAME = "name";
  private static final String CERT_PEM = "pem";

  @Mock
  private HelpDeskBotConfig config;

  @Mock
  private RestTemplate restTemplate;

  @Spy
  private CertificateUtils certificateUtils = new CertificateUtils();

  @InjectMocks
  private HelpDeskPublicApiClient client = new HelpDeskPublicApiClient();

  private ServiceInfo pod = new ServiceInfo();

  @Before
  public void init() {
    pod.setHost(POD_HOST);
    pod.setPort(POD_PORT);

    doReturn(pod).when(config).getPod();
  }

  @Test
  public void testGetSalt() {
    ResponseEntity<String> response = new ResponseEntity<>(SALT_JSON, HttpStatus.OK);
    doReturn(response).when(restTemplate).getForEntity(GET_SALT_ENDPOINT, String.class);

    String value = client.getSalt(USER);

    assertEquals(SALT_VALUE, value);
  }

  @Test (expected = IllegalStateException.class)
  public void testGetSaltException() {
    ResponseEntity<String> response = new ResponseEntity<>(INVALID_JSON, HttpStatus.OK);
    doReturn(response).when(restTemplate).getForEntity(GET_SALT_ENDPOINT, String.class);

    client.getSalt(USER);

    fail();
  }

  @Test
  public void testLogin() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, SKEY + "=" + SKEY_VALUE);
    ResponseEntity<String> response = new ResponseEntity<>(headers, HttpStatus.OK);

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("userName", USER);
    formData.add("hPassword", PASSWORD);

    doReturn(response).when(restTemplate).postForEntity(LOGIN_ENDPOINT, formData, String.class);

    String value = client.login(USER, PASSWORD);

    assertEquals(SKEY_VALUE, value);
  }

  @Test
  public void testCreateCompanyCert() {
    CompanyCert cert = certificateUtils.buildCompanyCertificate(CERT_NAME, CERT_PEM,
        CompanyCertStatus.TypeEnum.TRUSTED);

    CompanyCertDetail certDetail = new CompanyCertDetail();
    certDetail.setCompanyCertAttributes(cert.getAttributes());

    ResponseEntity<CompanyCertDetail> response = new ResponseEntity<>(certDetail, HttpStatus.OK);

    HttpHeaders headers = new HttpHeaders();
    headers.set("sessionToken", SKEY_VALUE);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CompanyCert> entity = new HttpEntity<CompanyCert>(cert, headers);

    doReturn(response).when(restTemplate).postForEntity(CREATE_CERT_ENDPOINT, entity,
        CompanyCertDetail.class);

    CompanyCertDetail value = client.createCompanyCert(SKEY_VALUE, cert);

    assertEquals(certDetail, value);
  }

  @Test (expected = IllegalStateException.class)
  public void testCreateCompanyCertSessionTokenException() {
    CompanyCert cert = new CompanyCert();
    client.createCompanyCert(null, cert);

    fail();
  }

  @Test (expected = IllegalStateException.class)
  public void testCreateCompanyCertCertificateException() {
    client.createCompanyCert(SKEY_VALUE, null);

    fail();
  }
}
