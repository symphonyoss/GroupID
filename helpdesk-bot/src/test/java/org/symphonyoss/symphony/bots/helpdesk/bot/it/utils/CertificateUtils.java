package org.symphonyoss.symphony.bots.helpdesk.bot.it.utils;

import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.bots.helpdesk.bot.it.TestContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
public class CertificateUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtils.class);

  private static final String CERTS_DIR = "certs";

  private static final String ALGORITHM = "RSA";

  private static final String X_509 = "X.509";

  private static final String PROVIDER = "BC";

  private static final int KEYSIZE = 2048;

  private static final String WHITE_SPACES = " ";

  private static final String PASSWD = "changeit";

  private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

  private static final String PKCS_12 = "PKCS12";

  private static final TestContext CONTEXT = TestContext.getInstance();

  /**
   * Creates certificate directory and set it into the test context.
   */
  public static void createCertsDir() {
    String certsDir = System.getProperty("java.io.tmpdir") + File.separator + CERTS_DIR;

    File directory = new File(certsDir);

    if (!directory.exists()) {
      LOGGER.info("Creating certificate directory: " + directory.getAbsolutePath());
      directory.mkdirs();
    } else {
      LOGGER.info("Certificate directory already exists");
    }

    CONTEXT.setCertsDir(certsDir);

    System.setProperty("CERTS_DIR", certsDir);
  }

  /**
   * Creates p12 certificate file. This method generates a key pair, creates a certificate
   * and save it on the filesystem.
   *
   * @param caKeyPath Path to CA private key
   * @param caCertPath Path to CA certificate
   * @param userName user name
   */
  public static void createUserCertificate(String caKeyPath, String caCertPath, String userName) {
    URI certsDir = new File(CONTEXT.getCertsDir()).toURI();

    Security.addProvider(new BouncyCastleProvider());

    try {
      KeyPair keys = generateKeys();

      File caKeyFile = new File(caKeyPath);
      PrivateKey caKey = getPrivateKey(caKeyFile);

      File caCertFile = new File(caCertPath);
      X509Certificate caCert = getX509Certificate(caCertFile);

      X509Certificate userCert = createUserCert(caCert, caKey, keys.getPublic(), userName);

      Certificate[] chain = new Certificate[] { userCert, caCert };

      URI certificateP12 = certsDir.resolve(userName.replace(WHITE_SPACES, "") + ".p12");
      writeKeystore(certificateP12, PASSWD, chain, userName, keys);
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't create certificate.", e);
    }
  }

  /**
   * Generate the public/private key pair
   * @return Public/private key pair
   */
  private static KeyPair generateKeys() throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
    generator.initialize(KEYSIZE);

    return generator.generateKeyPair();
  }

  /**
   * Convert the File object into PrivateKey object.
   * @param privateKey private key file
   * @return PrivateKey object
   */
  private static PrivateKey getPrivateKey(File privateKey)
      throws IOException, PKCSException, OperatorCreationException {
    FileReader fileReader = new FileReader(privateKey);
    PEMParser parser = new PEMParser(fileReader);
    PKCS8EncryptedPrivateKeyInfo pair = (PKCS8EncryptedPrivateKeyInfo) parser.readObject();
    JceOpenSSLPKCS8DecryptorProviderBuilder jce = new JceOpenSSLPKCS8DecryptorProviderBuilder();
    InputDecryptorProvider decryptorProvider = jce.build(PASSWD.toCharArray());
    JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();

    return jcaPEMKeyConverter.getPrivateKey(pair.decryptPrivateKeyInfo(decryptorProvider));
  }

  /**
   * Convert the File object into X509Certificate object.
   * @param certFile Certificate file
   * @return X509Certificate object
   */
  private static X509Certificate getX509Certificate(File certFile)
      throws FileNotFoundException, CertificateException {
    FileInputStream is = new FileInputStream(certFile);

    CertificateFactory certificateFactory = CertificateFactory.getInstance(X_509);
    return (X509Certificate) certificateFactory.generateCertificate(is);
  }

  private static X509Certificate createUserCert(X509Certificate caCert, PrivateKey caKey,
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
            new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30),
            new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)),
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
  private static void writeKeystore(URI certificateDir, String keyStorePassword, Certificate[] chain,
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

}
