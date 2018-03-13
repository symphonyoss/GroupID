package org.symphonyoss.symphony.apps.authentication.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.certificate.model.PodCertificate;

import java.security.PublicKey;

/**
 * Unit tests for {@link PodCertificateService}
 *
 * Created by rsanchez on 10/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class PodCertificateServiceTest {

  private static final String INVALID_CERT = "invalid";

  private static final String VALID_CERT = "-----BEGIN "
      + "CERTIFICATE-----MIIEQDCCAyigAwIBAgIVAKmVBTro/GCF6u842F9q7JdYVYguMA0GCSqGSIb3DQEB"
      + "CwUAMGQxJjAkBgNVBAMMHUlzc3VpbmcgQ2VydGlmaWNhdGUgQXV0aG9yaXR5MS0w"
      + "KwYDVQQKDCRTeW1waG9ueSBDb21tdW5pY2F0aW9uIFNlcnZpY2VzIExMQy4xCzAJ"
      + "BgNVBAYTAlVTMB4XDTE3MDYyODE2MDMxMVoXDTM3MDYyODE2MDMxMVowZDEmMCQG"
      + "A1UEAwwdSXNzdWluZyBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkxLTArBgNVBAoMJFN5"
      + "bXBob255IENvbW11bmljYXRpb24gU2VydmljZXMgTExDLjELMAkGA1UEBhMCVVMw"
      + "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDnhVax5PSEeGW78l02v47sc8CzQ5IDNb3rdbg"
      + "/39ehTKQ1nxSW3AYV1UsvIGQibQZPsmCp3fLRbZv3R5TKRt5peyh2LCQOSdvqCoJj9nl27GiM7mizjmGn/76B"
      + "/sNNNcbjwr+A1Trct1HE/5bGkt17"
      + "0AomxGFxAdh8mL6dWFD8h6FqV1yPztWuL91Axgf4ARGxEClSYbZAz5XBYrW0K19n"
      + "lQJpwyDTuCRqP1DIfJgITQDtJypoc1a0h1KNfzxuAYppPbTydGbacTafKN18kz3Wlv91VxY80LRdeqM"
      + "+cuNrLA7uO0zeByYxEQk0p2qp3r3B3iSEryipa4drIvVLksNdAgMBAAGjgegwgeUwDgYDVR0PAQH"
      + "/BAQDAgGGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFESrA+Ak9Ut+Jfcc8W"
      + "+mG3RfqFXCMIGiBgNVHSMEgZowgZeAFESrA+Ak9Ut+Jfcc8W"
      + "+mG3RfqFXCoWikZjBkMSYwJAYDVQQDDB1Jc3N1aW5nIENlcnRpZmlj"
      + "YXRlIEF1dGhvcml0eTEtMCsGA1UECgwkU3ltcGhvbnkgQ29tbXVuaWNhdGlvbiBT"
      + "ZXJ2aWNlcyBMTEMuMQswCQYDVQQGEwJVU4IVAKmVBTro/GCF6u842F9q7JdYVYgu"
      + "MA0GCSqGSIb3DQEBCwUAA4IBAQC6ic1eJFuvRiHUWU48N6gynDe8Wmmzyx5zpNcg"
      + "nGXjCTqWCQymEcKVNBYEeBwFWn74ImRMxbw+n6Du3Ybbp4C1eDwbQ9xRLs4zFigsDRtb4Fr"
      + "/XMR3IyrgLdZt5+nqXmf8rBMqT5dXsiujym9di2kwbYm1kYuEZaKzrmFTy4/SmH8irbKndNFyKR"
      + "+9kU785qUXrH/ZIAQc4h5TeyyMPmrda9cftQ8GgbCO8Ejb"
      + "26x03kZ47dkiLcro82SBnNFtoD0eh7AI5qjVdFlksSduflRptrFX9JtbGNliWVNO"
      + "dZH6wQyZkOmElekcEhXdCUjyyxHjKdhIQe1HI4ObrRJsTWkf-----END CERTIFICATE-----";

  @Mock
  private PodCertificateClientFactory factory;

  @Mock
  private PodCertificateClient client;

  @InjectMocks
  private PodCertificateService service;

  @Before
  public void init() {
    doReturn(client).when(factory).getComponent();
  }

  @Test
  public void testInvalidCert() throws PodCertificateException {
    PodCertificate certificate = new PodCertificate(INVALID_CERT);
    doReturn(certificate).when(client).getPodPublicCertificate();

    try {
      service.getPodPublicKey();
      fail();
    } catch (PodCertificateException e) {
      assertEquals("Cannot retrieve public key from X.509 certificate", e.getMessage());
    }
  }

  @Test
  public void testValidCert() throws PodCertificateException {
    PodCertificate certificate = new PodCertificate(VALID_CERT);
    doReturn(certificate).when(client).getPodPublicCertificate();

    PublicKey publicKey = service.getPodPublicKey();
    assertNotNull(publicKey);
  }
}
