package org.symphonyoss.symphony.bots.helpdesk.bot.provisioning;

import com.gs.ti.wpt.lc.security.cryptolib.PBKDF;

import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskPublicApiClient;
import org.symphonyoss.symphony.bots.helpdesk.bot.config.ProvisioningConfig;
import org.symphonyoss.symphony.bots.helpdesk.bot.util.CertificateUtils;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;

/**
 * Performs the provisioning process of a bot.
 * Created by campidelli on 3/9/18.
 */
@Service
@Lazy
public class HelpDeskProvisioningService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskProvisioningService.class);

  private static final String SELF_SIGNED_CERT_NAME = "helpdesk-root";

  private final ProvisioningConfig config;

  private final HelpDeskPublicApiClient publicApiClient;

  private final CertificateUtils certificateUtils;

  private URI keystoreFilePath;

  private String keystorePassword;

  public HelpDeskProvisioningService(ProvisioningConfig config,
      HelpDeskPublicApiClient publicApiClient, CertificateUtils certificateUtils,
      @Value("${authentication.keystore-file}") String keystoreFilePath,
      @Value("${authentication.keystore-password}") String keystorePassword)
      throws URISyntaxException {
    this.config = config;
    this.publicApiClient = publicApiClient;
    this.certificateUtils = certificateUtils;

    if (StringUtils.isNotEmpty(keystoreFilePath)) {
      this.keystoreFilePath = new URI(keystoreFilePath);
    }

    this.keystorePassword = keystorePassword;
  }

  /**
   * Executes the provisioning process:
   *  - Generates a self-signed certificate and upload it to a POD
   *  - Generates a p12 keystore file signed by the above certificate
   */
  public void execute() {
    if (!config.isExecute()) {
      LOGGER.info("Provisioning process will not be executed.");
      return;
    }
    LOGGER.info("Provisioning is being executed.");

    // Root certificate
    generateRootCertificate();

    // Service account
    generateServiceAccount();
  }

  private void generateRootCertificate() {
    if (config.isGenerateCACert()) {
      String certificatePath = certificateUtils.getSelfSignedRootCertificatePath();
      File certFile = new File(certificatePath);

      if (certFile.exists() && !config.isOverwriteCACert()) {
        LOGGER.info("Self Signed CA ROOT certificate already exists.");
        return;
      }

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
  }

  private void generateServiceAccount() {
    if (config.isGenerateServiceAccountKeystore()) {
      LOGGER.info("Generating p12 file for a service account.");

      String serviceAccountUserName = config.getServiceAccountUserName();
      String caCertPath = certificateUtils.getSelfSignedRootCertificatePath();
      String caKeyPath = certificateUtils.getSelfSignedRootKeyPath();

      if (keystoreFilePath != null) {
        File certFile = new File(keystoreFilePath.getPath());

        if (certFile.exists() && !config.isOverwriteServiceAccountKeystore()) {
          LOGGER.info("P12 file for service account already exists.");
          return;
        }

        certificateUtils.createUserCertificate(caKeyPath, caCertPath, keystorePassword,
            serviceAccountUserName, keystoreFilePath, keystorePassword);
      } else {
        certificateUtils.createUserCertificate(caKeyPath, caCertPath, serviceAccountUserName);
      }

      LOGGER.info("p12 file successfully generated.");
    }
  }

  private CompanyCert getSelfSignedCertificate() {
    X509Certificate certificate = certificateUtils.createSelfSignedRootCertificate();
    String pem = certificateUtils.getPemAsString(certificate);

    return certificateUtils.buildCompanyCertificate(SELF_SIGNED_CERT_NAME, pem,
        CompanyCertStatus.TypeEnum.TRUSTED);
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
