package org.symphonyoss.symphony.bots.helpdesk.service.it;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.tokens.model.AppToken;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by rsanchez on 15/03/18.
 */
@RestController
public class MockPodRestController {

  private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";

  private static final String END_CERT = "-----END CERTIFICATE-----";

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private final MockPodCertificateService certificateService;

  private final JsonParserFactory factory = JsonParserFactory.getInstance();

  public MockPodRestController(MockPodCertificateService certificateService) {
    this.certificateService = certificateService;
  }

  @GetMapping(path = "/pod/v1/podcert", produces = { "application/json" })
  public ResponseEntity<String> getPublicCert() {
    JsonParser parser = factory.getComponent();

    try {
      X509Certificate publicCertificate = certificateService.getPublicCertificate();

      Base64.Encoder encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());

      final byte[] rawCrtText = publicCertificate.getEncoded();
      final String encodedCertText = new String(encoder.encode(rawCrtText));
      final String prettifiedCert = BEGIN_CERT + LINE_SEPARATOR + encodedCertText + LINE_SEPARATOR + END_CERT;

      PodCertificate podCertificate = new PodCertificate();
      podCertificate.setCertificate(prettifiedCert);

      return ResponseEntity.ok(parser.writeToString(podCertificate));
    } catch (CertificateException | IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping(path = "/sessionauth/v1/authenticate/extensionApp", consumes = {"application/json"},
      produces = {"text/plain"})
  public ResponseEntity<String> authenticateExtensionApp(@RequestBody String body) {
    JsonParser parser = factory.getComponent();

    try {
      AppToken appToken = parser.writeToObject(body, AppToken.class);
      appToken.setSymphonyToken(UUID.randomUUID().toString());

      return ResponseEntity.ok(parser.writeToString(appToken));
    } catch (IOException e) {
      return ResponseEntity.badRequest().build();
    }
  }

}
