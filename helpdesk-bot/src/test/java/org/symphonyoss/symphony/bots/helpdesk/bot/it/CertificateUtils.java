package org.symphonyoss.symphony.bots.helpdesk.bot.it;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
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

  public static void createCertificateP12(String caKeyPath, String caCertPath, String userName) {
    URI certsDir = new File(testContext.getCertsDir()).toURI();

    Security.addProvider(new BouncyCastleProvider());

    try {
      KeyPair keys = generateKeys();

      File privateKeyFile = new File(caKeyPath);
      PrivateKey privateKey = getPrivateKey(privateKeyFile);

      File publicKeyFile = new File(caCertPath);
      PublicKey publicKey = getPublicKey(publicKeyFile.getPath());

      Certificate[] certChain = createCertificate2(publicKey, privateKey, userName);

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

  private static Certificate[] createCertificate2(PublicKey publicKey, PrivateKey privateKey, String userName)
      throws IOException, OperatorCreationException, CertificateException, NoSuchProviderException,
      NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    X500Name issuer = new X500Name("CN=" + userName + ", C=US, O=Symphony Communications LLC, OU=NOT FOR PRODUCTION USE");
    X500Name subject = new X500Name("CN=" + userName + ", C=US, O=Symphony Communications LLC, OU=NOT FOR PRODUCTION USE");

    ByteArrayInputStream bIn = new ByteArrayInputStream(publicKey.getEncoded());
    SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo((ASN1Sequence)new ASN1InputStream(bIn).readObject());

    X509v1CertificateBuilder x509v1CertificateBuilder =
        new X509v1CertificateBuilder(issuer,
            BigInteger.valueOf(1),
            new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30),
            new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)),
            subject,
            publicKeyInfo);

    AsymmetricKeyParameter privateKeyAsymKeyParam = PrivateKeyFactory.createKey(privateKey.getEncoded());
    DefaultDigestAlgorithmIdentifierFinder digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
    AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(SIGNATURE_ALGORITHM);
    AlgorithmIdentifier digAlgId = digAlgFinder.find(sigAlgId);
    ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(privateKeyAsymKeyParam);

    X509CertificateHolder certificateHolder = x509v1CertificateBuilder.build(sigGen);

    JcaX509CertificateConverter converter = new JcaX509CertificateConverter();

    X509Certificate cert = converter.getCertificate(certificateHolder);

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