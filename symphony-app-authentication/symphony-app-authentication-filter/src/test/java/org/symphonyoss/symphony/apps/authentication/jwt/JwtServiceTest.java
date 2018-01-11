package org.symphonyoss.symphony.apps.authentication.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateService;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.json.JacksonParser;
import org.symphonyoss.symphony.apps.authentication.json.JsonParserFactory;
import org.symphonyoss.symphony.apps.authentication.jwt.exception.JwtProcessingException;
import org.symphonyoss.symphony.apps.authentication.jwt.model.JwtPayload;

import java.io.IOException;
import java.security.PublicKey;

/**
 * Unit tests for {@link JwtService}
 *
 * Created by rsanchez on 10/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class JwtServiceTest {

  private static final String MOCK_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
      + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9."
      + "TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

  @Mock
  private PodCertificateService certificateService;

  @Mock
  private JsonParserFactory factory;

  @Mock
  private JwtParser parser;

  @Mock
  private PublicKey publicKey;

  @Mock
  private Jws<Claims> claimsJws;

  @InjectMocks
  private JwtService service = new JwtService(5, 1000);

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);

    doReturn(parser).when(parser).setSigningKey(publicKey);
    doReturn(new JacksonParser()).when(factory).getComponent();
  }

  @Test
  public void testGetPodCertificateFailed() throws PodCertificateException, JwtProcessingException {
    PodCertificateException ex = new PodCertificateException("Fail to read certificate");
    doThrow(ex).when(certificateService).getPodPublicKey();

    try {
      service.parseJwtPayload(MOCK_JWT);
      fail();
    } catch (JwtProcessingException e) {
      assertEquals("Fail to read certificate", e.getMessage());
      assertEquals(ex, e.getCause());
    }
  }

  @Test
  public void testInvalidSignature() throws PodCertificateException, JwtProcessingException {
    UnsupportedJwtException ex = new UnsupportedJwtException("Invalid signature");

    doReturn(publicKey).when(certificateService).getPodPublicKey();
    doThrow(ex).when(parser).parseClaimsJws(MOCK_JWT);

    try {
      service.parseJwtPayload(MOCK_JWT);
      fail();
    } catch (JwtProcessingException e) {
      assertEquals("Invalid signature", e.getMessage());
      assertEquals(ex, e.getCause());
    }
  }

  @Test
  public void testInvalidJWTPayload() throws PodCertificateException, JwtProcessingException {
    doReturn(publicKey).when(certificateService).getPodPublicKey();
    doReturn(claimsJws).when(parser).parseClaimsJws(MOCK_JWT);

    doReturn("").when(claimsJws).getBody();

    try {
      service.parseJwtPayload(MOCK_JWT);
      fail();
    } catch (JwtProcessingException e) {
      assertEquals("Invalid JWT", e.getMessage());
    }
  }

}
