package org.symphonyoss.symphony.bots.helpdesk.bot.provisioning;

import com.gs.ti.wpt.lc.security.cryptolib.PBKDF;

import com.google.common.io.BaseEncoding;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskPublicApiClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.ProvisioningConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.CertificateUtils;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;

import java.io.File;
import java.security.cert.X509Certificate;

/**
 * Performs the provisioning process of a bot. It consists of:
 *
 * - Generate a self-signed certificate and submit it to a POD;
 *
 * Created by campidelli on 3/9/18.
 */
@Service
public class HelpDeskProvisioningService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskProvisioningService.class);

  @Autowired
  ProvisioningConfig config;

  @Autowired
  HelpDeskPublicApiClient publicApiClient;

  @Autowired
  CertificateUtils certificateUtils;

  public void execute() {
    if (!config.isExecute()) {
      LOGGER.info("Provisioning process will not be executed.");
      return;
    }
    LOGGER.info("Provisioning is being executed.");

    // Root certificate
    if (config.isGenerateCACert()) {
      LOGGER.info("Generating a Self signed CA ROOT certificate.");
      CompanyCert cert = getSelfSignedCertificate();
      if (cert != null) {
        LOGGER.info("Certificate generated! Logging in to upload the cert to a POD.");
        String sessionToken = login();
        LOGGER.info("Logged in successfully. Uploading certificate.");
        publicApiClient.createCompanyCert(sessionToken, cert);
        LOGGER.info("Certificate successfully uploaded.");
      }
    }

    // Service account
    if (config.isGenerateServiceAccountP12()) {
      LOGGER.info("Generating p12 file for a service account.");
      String serviceAccountUserName = config.getServiceAccountUserName();
      String caCertPath = certificateUtils.getSelfSignedRootCertificatePath();
      String caKeyPath = certificateUtils.getSelfSignedRootKeyPath();
      certificateUtils.createUserCertificate(caKeyPath, caCertPath, serviceAccountUserName);
      LOGGER.info("p12 file successfully generated.");
    }
  }

  private CompanyCert getSelfSignedCertificate() {
    X509Certificate certificate = certificateUtils.createSelfSignedRootCertificate();
    String pem = certificateUtils.getPemAsString(certificate);
    String fileName = certificateUtils.getSelfSignedRootCertificatePath();
    File file = new File(fileName);
    return certificateUtils.buildCompanyCertificate(
        file.getName(), pem, CompanyCertStatus.TypeEnum.TRUSTED);
  }

  private String login() {
    String userName = config.getUserName();
    String salt = publicApiClient.getSalt(userName);
    byte[] saltedPasswordBytes = generatePasswordSalt(config.getUserPassword(), salt);
    String saltedPassword = BaseEncoding.base64().encode(saltedPasswordBytes);

    String sessionToken = publicApiClient.login(userName, saltedPassword);

    return sessionToken;
  }

  private byte[] generatePasswordSalt(String password, String saltStr) {
    try {
      return PBKDF.PBKDF2_SHA256(password.getBytes(), Base64.decode(saltStr.getBytes()), 10000);
    } catch (Exception e) {
      throw new IllegalStateException("Could not generate a salted password.", e);
    }
  }
}
