package org.symphonyoss.symphony.bots.helpdesk.bot.util;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.CompanyCertAttributes;
import org.symphonyoss.symphony.pod.model.CompanyCertStatus;
import org.symphonyoss.symphony.pod.model.CompanyCertType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Utility class to deal with certificate stuff.
 *
 * Created by crepache on 01/03/18.
 */
@Component
public class CertificateUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtils.class);

  private static final String CERTS_DIR = "certs";

  private static final String ALGORITHM = "RSA";

  private static final String X_509 = "X.509";

  private static final String PROVIDER = "BC";

  private static final int KEY_SIZE = 2048;

  private static final long THIRTY_DAYS = 1000L * 60 * 60 * 24 * 30;

  private static final String WHITE_SPACES = " ";

  private static final String DEFAULT_PASSWORD = "changeit";

  private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

  private static final String PKCS_12 = "PKCS12";

  private static final TempVariablesContext CONTEXT = TempVariablesContext.getInstance();

  private static final String COUNTRY = "US";

  private static final String SUBJECT = "Symphony";

  private static final String ROOT_CERT_FILENAME = "root-cert.pem";

  private static final String ROOT_KEY_FILENAME = "root-key.pem";

  @Value("${certs.dir}")
  private String certsDir;

  @Value("${authentication.keystore-file}")
  private String p12FilePath;

  @Value("${authentication.keystore-password}")
  private String p12FilePassword;

  /**
   * Adds BouncyCastle as a Security Provider.
   * Creates certificate directory and set it into the test context.
   */
  public CertificateUtils() {
    Security.addProvider(new BouncyCastleProvider());

    if (StringUtils.isEmpty(certsDir)) {
      certsDir = System.getProperty("java.io.tmpdir") + File.separator + CERTS_DIR;
    }

    File directory = new File(certsDir);
    if (!directory.exists()) {
      LOGGER.info("Creating certificate directory: " + directory.getAbsolutePath());
      directory.mkdirs();
    } else {
      LOGGER.info("Certificate directory already exists");
    }

    System.setProperty("CERTS_DIR", certsDir);
    CONTEXT.setCertsDir(certsDir);
  }

  /**
   * Creates p12 certificate file. This method generates a key pair, creates a certificate
   * and save it on the filesystem.
   *
   * @param caKeyPath Path to CA private key
   * @param caCertPath Path to CA certificate
   * @param userName user name
   */
  public void createUserCertificate(String caKeyPath, String caCertPath, String userName) {
    try {
      KeyPair keys = generateKeys();

      File caKeyFile = new File(caKeyPath);
      PrivateKey caKey = getPrivateKey(caKeyFile);

      File caCertFile = new File(caCertPath);
      X509Certificate caCert = getX509Certificate(caCertFile);

      X509Certificate userCert = createUserCert(caCert, caKey, keys.getPublic(), userName);

      Certificate[] chain = new Certificate[] { userCert, caCert };

      URI certificateP12 = new URI(getP12FilePath(userName.replace(WHITE_SPACES, "")));
      writeKeystore(certificateP12, getP12FilePassword(), chain, userName, keys);
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't create certificate.", e);
    }
  }

  /**
   * Creates a self-signed certificate and private key.
   * @return The created X509Certificate
   */
  public X509Certificate createSelfSignedRootCertificate() {
    try {
      KeyPair keyPair = generateKeys();

      X500NameBuilder subjectBuilder = new X500NameBuilder();
      subjectBuilder.addRDN(BCStyle.C, COUNTRY);
      subjectBuilder.addRDN(BCStyle.O, SUBJECT);
      subjectBuilder.addRDN(BCStyle.CN, ROOT_CERT_FILENAME);
      X500Name name = subjectBuilder.build();

      X509v1CertificateBuilder builder = new X509v1CertificateBuilder(
          name,
          BigInteger.valueOf(System.currentTimeMillis()),
          new Date(System.currentTimeMillis() - THIRTY_DAYS),
          new Date(System.currentTimeMillis() + THIRTY_DAYS),
          name,
          SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

      AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(
          SIGNATURE_ALGORITHM);
      AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

      ContentSigner signer = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
          .build(PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded()));
      X509CertificateHolder holder = builder.build(signer);

      X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(holder);

      // Save certificate and private key files
      writePem(certificate, ROOT_CERT_FILENAME);
      writePem(keyPair.getPrivate(), ROOT_KEY_FILENAME);

      return certificate;
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't create certificate.", e);
    }
  }

  /**
   * Converts a PEM object into a String.
   * @param pem PEM object.
      */
  public String getPemAsString(Object pem) {
    try {
      StringWriter stringWriter = new StringWriter();
      JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
      pemWriter.writeObject(pem);
      pemWriter.flush();
      return stringWriter.toString();
    } catch (IOException e) {
      throw new IllegalStateException("Could not parse the certificate.", e);
    }
  }

  /**
   * @return CA Root certificate file name (path).
   */
  public String getSelfSignedRootCertificatePath() {
    URI certsDir = new File(CONTEXT.getCertsDir()).toURI();
    return certsDir.resolve(ROOT_CERT_FILENAME).getPath();
  }

  /**
   * @return CA Root key file name (path).
   */
  public String getSelfSignedRootKeyPath() {
    URI certsDir = new File(CONTEXT.getCertsDir()).toURI();
    return certsDir.resolve(ROOT_KEY_FILENAME).getPath();
  }

  /**
   * Builds a company certificate object.
   *
   * @param certName Certificate name
   * @param pem An X509 certificate in PEM format
   * @param type Certificate type
   * @return Company certificate object
   */
  public CompanyCert buildCompanyCertificate(String certName, String pem,
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

  /**
   * Generate the public/private key pair
   * @return Public/private key pair
   */
  private KeyPair generateKeys() throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
    generator.initialize(KEY_SIZE);

    return generator.generateKeyPair();
  }

  /**
   * Convert the File object into PrivateKey object.
   * @param privateKey private key file
   * @return PrivateKey object
   */
  private PrivateKey getPrivateKey(File privateKey)
      throws IOException, PKCSException, OperatorCreationException {

    FileReader fileReader = new FileReader(privateKey);
    PEMParser parser = new PEMParser(fileReader);
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

    Object keyInfo = parser.readObject();
    if (keyInfo instanceof PKCS8EncryptedPrivateKeyInfo) {
      PKCS8EncryptedPrivateKeyInfo pair = (PKCS8EncryptedPrivateKeyInfo) keyInfo;
      JceOpenSSLPKCS8DecryptorProviderBuilder jce = new JceOpenSSLPKCS8DecryptorProviderBuilder();
      InputDecryptorProvider decryptorProvider = jce.build(getP12FilePassword().toCharArray());
      return converter.getPrivateKey(pair.decryptPrivateKeyInfo(decryptorProvider));

    } else if (keyInfo instanceof PEMEncryptedKeyPair) {
      PEMDecryptorProvider decProv =
          new JcePEMDecryptorProviderBuilder().build(getP12FilePassword().toCharArray());
      KeyPair pair = converter.getKeyPair(((PEMEncryptedKeyPair) keyInfo).decryptKeyPair(decProv));
      return pair.getPrivate();
    }

    KeyPair pair = converter.getKeyPair((PEMKeyPair) keyInfo);
    return pair.getPrivate();
  }

  /**
   * Convert the File object into X509Certificate object.
   * @param certFile Certificate file
   * @return X509Certificate object
   */
  private X509Certificate getX509Certificate(File certFile)
      throws FileNotFoundException, CertificateException {
    FileInputStream is = new FileInputStream(certFile);

    CertificateFactory certificateFactory = CertificateFactory.getInstance(X_509);
    return (X509Certificate) certificateFactory.generateCertificate(is);
  }

  private X509Certificate createUserCert(X509Certificate caCert, PrivateKey caKey,
      PublicKey userKey, String userRef)
      throws OperatorCreationException, CertificateException, NoSuchAlgorithmException,
      CertIOException {
    X509CertificateHolder certHolder = new JcaX509CertificateHolder(caCert);
    X500Name caRDN = certHolder.getSubject();

    X500NameBuilder subjectBuilder = new X500NameBuilder();
    subjectBuilder.addRDN(BCStyle.C, caRDN.getRDNs(BCStyle.C)[0].getFirst().getValue());
    subjectBuilder.addRDN(BCStyle.O, caRDN.getRDNs(BCStyle.O)[0].getFirst().getValue());
    subjectBuilder.addRDN(BCStyle.OU, "NOT FOR PRODUCTION USE");
    subjectBuilder.addRDN(BCStyle.CN, userRef);

    X509v3CertificateBuilder v3Bldr =
        new JcaX509v3CertificateBuilder(caRDN,
            BigInteger.valueOf(3),
            new Date(System.currentTimeMillis() - THIRTY_DAYS),
            new Date(System.currentTimeMillis() + THIRTY_DAYS),
            subjectBuilder.build(), userKey);

    JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
    v3Bldr.addExtension(
        Extension.subjectKeyIdentifier,
        false,
        extUtils.createSubjectKeyIdentifier(userKey));
    v3Bldr.addExtension(
        Extension.authorityKeyIdentifier,
        false,
        extUtils.createAuthorityKeyIdentifier(caCert));

    X509CertificateHolder certHldr = v3Bldr.build(
        new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
            .setProvider(PROVIDER)
            .build(caKey));

    X509Certificate cert = new JcaX509CertificateConverter()
        .setProvider(PROVIDER)
        .getCertificate(certHldr);

    PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) cert;
    bagAttr.setBagAttribute(
        PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
        new DERBMPString(userRef));
    bagAttr.setBagAttribute(
        PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
        extUtils.createSubjectKeyIdentifier(userKey));

    return cert;
  }

  /**
   * Method responsible for saving p12 file in certificate directory.
   *
   * @param certificateDir path to save the certificate.
   * @param keyStorePassword certificate password
   * @param chain the certificate chain
   * @param userRef alias name of the certificate
   * @param keys private and public keys to be associated with the alias
   */
  private void writeKeystore(URI certificateDir, String keyStorePassword, Certificate[] chain,
      String userRef, KeyPair keys) throws NoSuchAlgorithmException, NoSuchProviderException,
      KeyStoreException, IOException, CertificateException {
    PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) keys.getPrivate();
    JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

    bagAttr.setBagAttribute(
        PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
        new DERBMPString(userRef));
    bagAttr.setBagAttribute(
        PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
        extUtils.createSubjectKeyIdentifier(keys.getPublic()));

    KeyStore store = KeyStore.getInstance(PKCS_12, PROVIDER);
    store.load(null, null);
    store.setKeyEntry(userRef, keys.getPrivate(), null, chain);

    try (FileOutputStream fOut = new FileOutputStream(certificateDir.getPath())) {
      store.store(fOut, keyStorePassword.toCharArray());
    }
  }

  /**
   * Writes a PEM object into a PEM file.
   * @param pem PEM object.
   * @param fileName PEM file name.
   * @throws IOException When there is a problem with files and/or directories.
   */
  private void writePem(Object pem, String fileName) throws IOException {
    URI certsDir = new File(CONTEXT.getCertsDir()).toURI();
    File rootCertFile = new File(certsDir.resolve(fileName));
    if (rootCertFile.exists()) {
      rootCertFile.delete();
    }

    FileWriter fileWriter = new FileWriter(rootCertFile);
    JcaPEMWriter pemWriter = new JcaPEMWriter(fileWriter);
    pemWriter.writeObject(pem);
    pemWriter.flush();
  }

  private String getP12FilePath(String user) {
    if (StringUtils.isEmpty(p12FilePath)) {
      URI certsDir = new File(CONTEXT.getCertsDir()).toURI();
      p12FilePath = certsDir.resolve(user + ".p12").getPath();
    }
    return p12FilePath;
  }

  private String getP12FilePassword() {
    if (StringUtils.isEmpty(p12FilePassword)) {
      p12FilePassword = DEFAULT_PASSWORD;
    }
    return p12FilePassword;
  }
}
