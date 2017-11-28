package org.symphonyoss.symphony.bots.helpdesk.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.symphonyoss.symphony.bots.helpdesk.service.exception.BadRequestException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Error;

/**
 * Global exception handler for web resources.
 *
 * Created by rsanchez on 27/11/2017.
 */
@ControllerAdvice
public class ApiExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

  /**
   * Handle {@link MissingServletRequestParameterException} exception.
   * @param ex Exception object
   * @return HTTP 400 (Bad Request)
   */
  @ResponseBody
  @ExceptionHandler({ MissingServletRequestParameterException.class, BadRequestException.class})
  public ResponseEntity<Error> handleMissingRequiredParameterException(Exception ex) {
    Error response = new Error().code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handle {@link HttpMediaTypeNotAcceptableException} exception.
   * @param ex Exception object
   * @return HTTP 406 (Not acceptable)
   */
  @ResponseBody
  @ExceptionHandler({ HttpMediaTypeNotAcceptableException.class})
  public ResponseEntity<Error> handleMediaTypeNotAcceptableException(Exception ex) {
    Error response = new Error().code(HttpStatus.NOT_ACCEPTABLE.value()).message(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
  }

  /**
   * Handle {@link HttpMediaTypeNotSupportedException} exception.
   * @param ex Exception object
   * @return HTTP 415 (Unsupported Media Type)
   */
  @ResponseBody
  @ExceptionHandler({ HttpMediaTypeNotSupportedException.class})
  public ResponseEntity<Error> handleUnsupportedMediaTypeException(Exception ex) {
    Error response = new Error().code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).message(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
  }

  /**
   * Handle {@link HttpMediaTypeNotSupportedException} exception.
   * @param ex Exception object
   * @return HTTP 405 (Method Not allowed)
   */
  @ResponseBody
  @ExceptionHandler({ HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<Error> handleMethodNotSupportedException(Exception ex) {
    Error response = new Error().code(HttpStatus.METHOD_NOT_ALLOWED.value()).message(ex.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
  }

  /**
   * Handle {@link Exception} exception.
   * @param ex Exception object
   * @return HTTP 500 (Internal Server Error)
   */
  @ResponseBody
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> handleForbiddenException(Exception ex) {
    String message = ex.getMessage();
    LOGGER.error(message);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    Error response = new Error().code(status.value()).message(message);
    return ResponseEntity.status(status).body(response);
  }

}
