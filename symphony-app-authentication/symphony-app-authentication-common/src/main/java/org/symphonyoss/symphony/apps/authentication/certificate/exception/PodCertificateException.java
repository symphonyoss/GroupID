package org.symphonyoss.symphony.apps.authentication.certificate.exception;

/**
 * Exception to report failures to retrieve POD certificate
 *
 * Created by rsanchez on 09/01/18.
 */
public class PodCertificateException extends Exception {

  public PodCertificateException(String message) {
    super(message);
  }

  public PodCertificateException(String message, Throwable cause) {
    super(message, cause);
  }
}
