package org.symphonyoss.symphony.bots.helpdesk.bot.it;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.x509.X509V1CertificateGenerator;

import java.io.File;
import java.io.FileInputStream;
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

public class CertificateUtils {

  private static final String ALGORITHM = "RSA";

  private static final String PROVIDER = "BC";

  private static final String PKCS_12 = "PKCS12";

  private static final String X_509 = "X.509";

  private static final int KEYSIZE = 2048;

  private static final String SIGNATURE_ALGORITHM = "SHA1WithRSAEncryption";

  private static final String WHITE_SPACES = " ";

  private static String PASSWD = "changeit";

  private static TestContext testContext = TestContext.getInstance();

  public static void main(String[] args) {
    testContext.setCertsDir("/tmp/certs");
    File directory = new File(testContext.getCertsDir());
    if (!directory.exists()) {
      directory.mkdirs();
    }

    createCertificateP12("/opt/test/root-key.pem", "/opt/test/root-cert.pem", "Test- 2");
  }

  public static void createCertificateP12(String caKeyPath, String caCertPath, String userName) {
    URI certsDir = new File(testContext.getCertsDir()).toURI();

    Security.addProvider(new BouncyCastleProvider());

    try {
      KeyPair keys = generateKeys();

      File privateKeyFile = new File(caKeyPath);
      PrivateKey privateKey = getPrivateKey(privateKeyFile);

      File publicKeyFile = new File(caCertPath);
      PublicKey publicKey = getPublicKey(publicKeyFile.getPath());

      Certificate[] certChain = createCertificate(publicKey, privateKey, userName);

      URI certificateP12 = certsDir.resolve(userName.replace(WHITE_SPACES, "") + ".p12");
      writeKeystore(certificateP12, PASSWD, certChain, userName, keys);
    } catch (Exception e) {
      throw new IllegalStateException("Couldn't create certificate.", e);
    }
  }

  private static KeyPair generateKeys() throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
    generator.initialize(KEYSIZE);

    return generator.generateKeyPair();
  }

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

  private static PublicKey getPublicKey(String filename) throws Exception {
    File file = new File(filename);
    FileInputStream is = new FileInputStream(file);

    CertificateFactory certificateFactory = CertificateFactory.getInstance(X_509);
    X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(is);

    return certificate.getPublicKey();
  }

  private static Certificate[] createCertificate(PublicKey publicKey, PrivateKey privateKey, String userName) throws Exception {
    X509V1CertificateGenerator v1CertGen = new X509V1CertificateGenerator();

    String issuer = "CN=" + userName + ", C=US, O=Symphony Communications LLC, OU=NOT FOR PRODUCTION USE";
    String subject = "CN=" + userName + ", C=US, O=Symphony Communications LLC, OU=NOT FOR PRODUCTION USE";

    v1CertGen.setSerialNumber(BigInteger.valueOf(1));
    v1CertGen.setIssuerDN(new X509Principal(issuer));
    v1CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
    v1CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)));
    v1CertGen.setSubjectDN(new X509Principal(subject));
    v1CertGen.setPublicKey(publicKey);
    v1CertGen.setSignatureAlgorithm(SIGNATURE_ALGORITHM);

    X509Certificate cert = v1CertGen.generate(privateKey);

    cert.checkValidity(new Date());

    cert.verify(publicKey);

    Certificate[] chain = new Certificate[1];

    chain[0] = cert;

    return chain;
  }

  private static void writeKeystore(URI keyStoreFile, String keyStorePassword, Certificate[] chain,
      String userRef, KeyPair keys) throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException,
      IOException, CertificateException {
    KeyStore store = KeyStore.getInstance(PKCS_12, PROVIDER);
    store.load(null, null);
    store.setKeyEntry(userRef, keys.getPrivate(), null, chain);

    try (FileOutputStream fOut = new FileOutputStream(keyStoreFile.getPath())) {
      store.store(fOut, keyStorePassword.toCharArray());
    }
  }
}