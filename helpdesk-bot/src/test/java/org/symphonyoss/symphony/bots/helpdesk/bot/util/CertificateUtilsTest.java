package org.symphonyoss.symphony.bots.helpdesk.bot.util;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;

import java.io.File;
import java.net.URI;
import java.security.cert.X509Certificate;

/**
 * Created by campidelli on 19/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class CertificateUtilsTest {

  private static final String CERTS_DIR_PROPERTY = "CERTS_DIR";

  private static final String CA_CERT_FILENAME = "root-cert.pem";
  private static final String CA_KEY_FILENAME = "root-key.pem";

  private static final String USERNAME = "username";
  private static final String P12_FILENAME = USERNAME + ".p12";
  private static final String P12_PASSWORD = "changeit";

  private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
  private static final String END_CERTIFICATE = "-----END CERTIFICATE-----" + System.lineSeparator();

  private File certsDir;
  private File caCertFile;
  private File caKeyFile;
  private File p12File;

  @InjectMocks
  private CertificateUtils utils;

  @Before
  public void init() throws Exception {
    utils = new CertificateUtils(null);

    certsDir = new File(System.getProperty(CERTS_DIR_PROPERTY));
    caCertFile = new File(certsDir.getPath() + File.separator + CA_CERT_FILENAME);
    caKeyFile = new File(certsDir.getPath() + File.separator + CA_KEY_FILENAME);
    p12File = new File(certsDir.getPath() + File.separator + P12_FILENAME);
  }

  @After
  public void end() {
    caCertFile.delete();
    caKeyFile.delete();
    p12File.delete();
  }

  @Test
  public void testCreateSelfSignedRootCertificate() {
    assertFalse(caCertFile.exists());
    assertFalse(caKeyFile.exists());

    utils.createSelfSignedRootCertificate();

    assertTrue(caCertFile.exists());
    assertTrue(caKeyFile.exists());
  }

  @Test
  public void testCreateUserCertificate() {
    utils.createSelfSignedRootCertificate();
    assertTrue(caCertFile.exists());
    assertTrue(caKeyFile.exists());

    utils.createUserCertificate(caKeyFile.getPath(), caCertFile.getPath(), P12_PASSWORD, USERNAME,
        p12File.toURI(), P12_PASSWORD);
    assertTrue(p12File.exists());
  }

  @Test
  public void testGetSelfSignedRootCertificatePath() {
    utils.createSelfSignedRootCertificate();
    assertTrue(caCertFile.exists());

    String caCertFilePath = utils.getSelfSignedRootCertificatePath();
    assertEquals(caCertFile.getPath(), caCertFilePath);
  }

  @Test
  public void testGetSelfSignedRootKeyPath() {
    utils.createSelfSignedRootCertificate();
    assertTrue(caKeyFile.exists());

    String caKeyFilePath = utils.getSelfSignedRootKeyPath();
    assertEquals(caKeyFile.getPath(), caKeyFilePath);
  }

  @Test
  public void testGetPemAsString() {
    X509Certificate cert = utils.createSelfSignedRootCertificate();
    assertTrue(caCertFile.exists());

    String pem = utils.getPemAsString(cert);
    assertTrue(pem.startsWith(BEGIN_CERTIFICATE));
    assertTrue(pem.endsWith(END_CERTIFICATE));
  }

  @Test
  public void testBuildCompanyCertificate() {
    X509Certificate cert = utils.createSelfSignedRootCertificate();
    assertTrue(caCertFile.exists());

    String pem = utils.getPemAsString(cert);
    CompanyCertStatus.TypeEnum trusted = CompanyCertStatus.TypeEnum.TRUSTED;
    CompanyCert companyCert = utils.buildCompanyCertificate(CA_CERT_FILENAME, pem, trusted);

    assertNotNull(companyCert);
    assertEquals(pem, companyCert.getPem());
    assertNotNull(companyCert.getAttributes());
    assertEquals(CA_CERT_FILENAME, companyCert.getAttributes().getName());
    assertNotNull(companyCert.getAttributes().getStatus());
    assertEquals(trusted, companyCert.getAttributes().getStatus().getType());
  }
}
