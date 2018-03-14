package org.symphonyoss.symphony.bots.helpdesk.bot.provisioning;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.serialNumber;

import com.symphony.security.exceptions.SymphonyEncryptionException;
import com.symphony.security.exceptions.SymphonyInputException;
import com.gs.ti.wpt.lc.security.cryptolib.PBKDF;

import com.google.common.io.BaseEncoding;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.symphonyoss.symphony.bots.helpdesk.bot.client.HelpDeskPublicApiClient;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertAttributes;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;
import org.symphonyoss.symphony.pod.model.CompanyCertType;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

/**
 * Performs the provisioning process of a bot. It consists of:
 *
 * - Generate a self-signed certificate and submit it to a POD;
 *
 * Created by campidelli on 3/9/18.
 */
@Service
public class HelpDeskProvisioningService {

  @Autowired
  HelpDeskPublicApiClient client;

  private X509Certificate generateSelfSignedCertificate() {
    try {
      // Generate key pair
      Security.addProvider(new BouncyCastleProvider());
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
      keyPairGenerator.initialize(1024, new SecureRandom());
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      // Generate certificate
      X500Name subjectDN = new X500Name("CN=Symphony");
      BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
      Date validityStartDate = new Date(System.currentTimeMillis() - 100000);
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.YEAR, 10);
      Date validityEndDate = new Date(calendar.getTime().getTime());
      SubjectPublicKeyInfo subPubKeyInfo =
          SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

      X509v1CertificateBuilder builder =
          new X509v1CertificateBuilder(subjectDN, serialNumber, validityStartDate,
              validityEndDate, subjectDN, subPubKeyInfo);

      AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(
          "SHA256WithRSAEncryption");
      AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

      ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
          .build(PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded()));
      X509CertificateHolder holder = builder.build(signer);

      X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(holder);

      // Save certificate and private key
      FileWriter fileWriter = new FileWriter("/home/campidelli/projects/symphonyoss/GroupID/helpdesk-bot/target/root-cert.pem");
      JcaPEMWriter pemWriter = new JcaPEMWriter(fileWriter);
      pemWriter.writeObject(certificate);
      pemWriter.flush();

      fileWriter = new FileWriter("/home/campidelli/projects/symphonyoss/GroupID/helpdesk-bot/target/root-key.pem");
      pemWriter = new JcaPEMWriter(fileWriter);
      pemWriter.writeObject(keyPair.getPrivate());
      pemWriter.flush();

      return certificate;

    } catch (Exception e) {
      throw new RuntimeException("Error creating X509v1Certificate.", e);
    }
  }

  public void login() {

    try {
      String userName = "rsanchez";
      String salt = client.getSalt(userName);
      String password = "Symphony!123456";

      byte[] saltedPasswordBytes = generatePasswordSalt(password, salt);
      String saltedPassword = BaseEncoding.base64().encode(saltedPasswordBytes);

      String sessionToken = client.login(userName, saltedPassword);

      X509Certificate x509Certificate = generateSelfSignedCertificate();

     // CompanyCert companyCert = buildCompanyCertificate("campidelli.pem", x509Certificate.get)

      //client.createCompanyCert(sessionToken, )

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private byte[] generatePasswordSalt(String password, String saltStr)
      throws SymphonyEncryptionException, SymphonyInputException {
    return PBKDF.PBKDF2_SHA256(password.getBytes(), Base64.decode(saltStr.getBytes()), 10000);
  }

  /**
   * Builds a company certificate object.
   *
   * @param certName Certificate name
   * @param pem An X509 certificate in PEM format
   * @param type Certificate type
   * @return Company certificate object
   */
  private CompanyCert buildCompanyCertificate(String certName, String pem,
      CompanyCertStatus.TypeEnum type) {
    CompanyCertAttributes attributes = new CompanyCertAttributes();
    attributes.setName(certName);

    CompanyCertType certType = new CompanyCertType();
    certType.setType(CompanyCertType.TypeEnum.USER);
    attributes.setType(certType);

    CompanyCertStatus status = new CompanyCertStatus();
    status.setType(type);
    attributes.setStatus(status);

    CompanyCert companyCert = new CompanyCert();
    companyCert.setPem(pem);
    companyCert.setAttributes(attributes);
    return companyCert;
  }
}
