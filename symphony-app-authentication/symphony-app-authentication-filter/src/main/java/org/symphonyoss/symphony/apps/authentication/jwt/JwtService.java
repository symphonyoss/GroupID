package org.symphonyoss.symphony.apps.authentication.jwt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.symphonyoss.symphony.apps.authentication.certificate.PodCertificateClient;
import org.symphonyoss.symphony.apps.authentication.certificate.exception.PodCertificateException;
import org.symphonyoss.symphony.apps.authentication.jwt.exception.JwtProcessingException;
import org.symphonyoss.symphony.apps.authentication.json.JsonParser;
import org.symphonyoss.symphony.apps.authentication.jwt.model.JwtPayload;

import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Component responsible for handling JWT stuff.
 *
 * Created by rsanchez on 09/01/18.
 */
public class JwtService {

  private static final String CERT_KEY = "certificate";

  private LoadingCache<String, PublicKey> podPublicSignatureVerifierCache;

  private LoadingCache<String, JwtPayload> jwtPayloadCache;

  private final Integer cacheTimeoutInMinutes;

  private final Integer maxCacheSize;

  private final PodCertificateClient certificateClient;

  public JwtService(Integer cacheTimeoutInMinutes, Integer maxCacheSize, Integer connectTimeout,
      Integer readTimeout) {
    this.cacheTimeoutInMinutes = cacheTimeoutInMinutes;
    this.maxCacheSize = maxCacheSize;
    this.certificateClient = new PodCertificateClient(connectTimeout, readTimeout);

    initializePodCertificateCache();
    initializeJwtPayloadCache();
  }

  /**
   * Initializes the local cache for public pod certificates.
   */
  private void initializePodCertificateCache() {
    podPublicSignatureVerifierCache = CacheBuilder.newBuilder()
        .expireAfterWrite(cacheTimeoutInMinutes, TimeUnit.MINUTES)
        .build(new CacheLoader<String, PublicKey>() {
          @Override
          public PublicKey load(String key) throws PodCertificateException {
            return certificateClient.getPodPublicKey();
          }
        });
  }

  private void initializeJwtPayloadCache() {
    jwtPayloadCache = CacheBuilder.newBuilder()
        .maximumSize(maxCacheSize)
        .build(new CacheLoader<String, JwtPayload>() {
          @Override
          public JwtPayload load(String jwt) throws JwtProcessingException {
            return loadJwtPayload(jwt);
          }
        });
  }

  /**
   * Get JWT payload in the local cache based on encoded JWT or load it.
   *
   * @param jwt Encoded JWT
   * @return JWT payload
   */
  public JwtPayload parseJwtPayload(String jwt) throws JwtProcessingException {
    try {
      return jwtPayloadCache.get(jwt);
    } catch (ExecutionException e) {
      JwtProcessingException cause = (JwtProcessingException) e.getCause();
      throw cause;
    }
  }

  /**
   * Validate if the provided JWT is valid by checking its signer and then return its payload.
   *
   * @param jwt Json Web Token containing the user/app authentication data.
   * @return jwt payload parsed
   */
  private JwtPayload loadJwtPayload(String jwt) throws JwtProcessingException {
    try {
      PublicKey rsaVerifier = getPublicKey();
      Jws<Claims> jws = getClaims(jwt, rsaVerifier);
      return getPayload(jws);
    } catch (PodCertificateException e) {
      throw new JwtProcessingException(e.getMessage(), e);
    }
  }

  /**
   * Get public key to check JWT signature. This public key is retrieved by the POD public certificate.
   *
   * @return Public key
   */
  private PublicKey getPublicKey() throws PodCertificateException {
    try {
      return podPublicSignatureVerifierCache.get(CERT_KEY);
    } catch (ExecutionException e) {
      PodCertificateException cause = (PodCertificateException) e.getCause();
      throw cause;
    }
  }

  /**
   * Check JWT signature and get claims.
   *
   * @param jwt JSON Web Token
   * @param rsaVerifier Public key to check JWT signature
   * @return JWT Claims
   */
  private Jws<Claims> getClaims(String jwt, PublicKey rsaVerifier) throws JwtProcessingException {
    try {
      return Jwts.parser().setSigningKey(rsaVerifier).parseClaimsJws(jwt);
    } catch (JwtException e) {
      throw new JwtProcessingException(e.getMessage(), e);
    }
  }

  /**
   * Serialize JWT claims into {@link JwtPayload}.
   *
   * @param jwt JWT object
   * @return JWT payload
   */
  private JwtPayload getPayload(Jws<Claims> jwt) throws JwtProcessingException {
    try {
      String json = JsonParser.writeToString(jwt.getBody());
      return JsonParser.writeToObject(json, JwtPayload.class);
    } catch (IOException e) {
      throw new JwtProcessingException("Invalid JWT", e);
    }
  }

}
