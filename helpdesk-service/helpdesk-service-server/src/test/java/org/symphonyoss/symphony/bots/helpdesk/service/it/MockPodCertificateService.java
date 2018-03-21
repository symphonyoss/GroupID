package org.symphonyoss.symphony.bots.helpdesk.service.it;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProvider;
import org.symphonyoss.symphony.apps.authentication.keystore.KeystoreProviderFactory;
import org.symphonyoss.symphony.apps.authentication.keystore.model.KeystoreSettings;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Created by rsanchez on 15/03/18.
 */
public class MockPodCertificateService {

  private static final String DEFAULT_KEYSTORE_PASS = "changeit";

  private static final String ISSUER_NAME = "Symphony Communication Services LLC.";

  private static final long JWT_TTL = TimeUnit.MINUTES.toMillis(60);

  private final KeystoreProviderFactory keystoreProviderFactory = KeystoreProviderFactory.getInstance();

  private PrivateKey privateKey;

  private void readKeyPair() {
    KeystoreProvider keystoreProvider = keystoreProviderFactory.getComponent();
    
    KeystoreSettings appKeystore = keystoreProvider.getApplicationKeystore(StringUtils.EMPTY);
    KeyStore keyStore = appKeystore.getData();

    try {
      String alias = keyStore.aliases().nextElement();
      this.privateKey = (PrivateKey) keyStore.getKey(alias, DEFAULT_KEYSTORE_PASS.toCharArray());

    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      throw new IllegalStateException("Cannot read private key", e);
    }
  }

  public String generateUserJWT(String appId, Long userId) {
    if (privateKey == null) {
      readKeyPair();
    }

    return Jwts.builder()
        .setHeaderParam(JwsHeader.TYPE, JwsHeader.JWT_TYPE)
        .claim(Claims.ISSUER, ISSUER_NAME)
        .claim(Claims.SUBJECT, userId)
        .claim(Claims.AUDIENCE, appId)
        .claim(Claims.EXPIRATION, (System.currentTimeMillis() + JWT_TTL) / 1000)  // in second
        .signWith(SignatureAlgorithm.RS512, privateKey)
        .compact();
  }

  public X509Certificate getPublicCertificate() throws CertificateException {
    KeystoreProvider keystoreProvider = keystoreProviderFactory.getComponent();

    KeystoreSettings appKeystore = keystoreProvider.getApplicationKeystore(StringUtils.EMPTY);
    KeyStore keyStore = appKeystore.getData();

    try {
      String alias = keyStore.aliases().nextElement();
      return (X509Certificate) keyStore.getCertificate(alias);
    } catch (KeyStoreException e) {
      throw new IllegalStateException("Cannot read X.509 certificate", e);
    }
  }

}
