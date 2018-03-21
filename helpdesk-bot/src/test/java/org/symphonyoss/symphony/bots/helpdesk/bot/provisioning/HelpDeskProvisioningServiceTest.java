package org.symphonyoss.symphony.bots.helpdesk.bot.provisioning;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskPublicApiClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.ProvisioningConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.CertificateUtils;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;

import java.security.cert.X509Certificate;

/**
 * Created by campidelli on 19/03/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpDeskProvisioningServiceTest {

  private static final String CERTS_PATH = "/certs/";
  private static final String CA_CERT_FILENAME = "root-cert.pem";
  private static final String CA_CERT_PATH = CERTS_PATH + CA_CERT_FILENAME;
  private static final String CA_KEY_FILENAME = "root-key.pem";
  private static final String CA_KEY_PATH = CERTS_PATH + CA_KEY_FILENAME;

  private static final String USERNAME = "username";
  private static final String CERT_PEM = "pem";
  private static final String SALT_VALUE = "s41t";
  private static final String PASSWORD = "password";
  private static final String SALTED_PASSWORD = "tEyB5Q9vrMGqNpBYaQ7gDsRGTShrOWfGbCU8qwTnzHg=";
  private static final String SKEY_VALUE = "123456";

  @Mock
  ProvisioningConfig config;

  @Mock
  HelpDeskPublicApiClient publicApiClient;

  @Mock
  CertificateUtils certificateUtils;

  @Mock
  X509Certificate certificate;

  @Mock
  CompanyCert companyCert;

  @InjectMocks
  private HelpDeskProvisioningService service = new HelpDeskProvisioningService();

  @Test
  public void testExecute() {
    doReturn(true).when(config).isExecute();
    doReturn(true).when(config).isGenerateCACert();
    doReturn(true).when(config).isGenerateServiceAccountP12();
    doReturn(USERNAME).when(config).getServiceAccountUserName();
    doReturn(USERNAME).when(config).getUserName();
    doReturn(PASSWORD).when(config).getUserPassword();

    doReturn(certificate).when(certificateUtils).createSelfSignedRootCertificate();
    doReturn(CERT_PEM).when(certificateUtils).getPemAsString(certificate);
    doReturn(CA_CERT_PATH).when(certificateUtils).getSelfSignedRootCertificatePath();
    doReturn(companyCert).when(certificateUtils).buildCompanyCertificate(CA_CERT_FILENAME,
        CERT_PEM, CompanyCertStatus.TypeEnum.TRUSTED);

    doReturn(SALT_VALUE).when(publicApiClient).getSalt(USERNAME);
    doReturn(SKEY_VALUE).when(publicApiClient).login(USERNAME, SALTED_PASSWORD);

    doReturn(CA_CERT_PATH).when(certificateUtils).getSelfSignedRootCertificatePath();
    doReturn(CA_KEY_PATH).when(certificateUtils).getSelfSignedRootKeyPath();

    service.execute();

    verify(certificateUtils, times(1)).buildCompanyCertificate(CA_CERT_FILENAME,
        CERT_PEM, CompanyCertStatus.TypeEnum.TRUSTED);
    verify(publicApiClient, times(1)).login(USERNAME, SALTED_PASSWORD);
    verify(publicApiClient, times(1)).createCompanyCert(SKEY_VALUE, companyCert);
    verify(certificateUtils, times(1)).createUserCertificate(CA_KEY_PATH, CA_CERT_PATH, USERNAME);
  }
}
