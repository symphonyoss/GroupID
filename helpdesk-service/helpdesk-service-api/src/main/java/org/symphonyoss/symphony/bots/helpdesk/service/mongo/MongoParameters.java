package org.symphonyoss.symphony.bots.helpdesk.service.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by rsanchez on 22/11/17.
 */
@ConfigurationProperties(prefix = "mongo")
public class MongoParameters {

  private String host;

  private Integer port;

  private String database;

  private int socketTimeout;

  private int connectTimeout;

  private int waitTimeout;

  private String readPreference;

  private String writeConcern;

  private boolean ignoreSSL;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public int getSocketTimeout() {
    return socketTimeout;
  }

  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getWaitTimeout() {
    return waitTimeout;
  }

  public void setWaitTimeout(int waitTimeout) {
    this.waitTimeout = waitTimeout;
  }

  public String getReadPreference() {
    return readPreference;
  }

  public void setReadPreference(String readPreference) {
    this.readPreference = readPreference;
  }

  public String getWriteConcern() {
    return writeConcern;
  }

  public void setWriteConcern(String writeConcern) {
    this.writeConcern = writeConcern;
  }

  public boolean isIgnoreSSL() {
    return ignoreSSL;
  }

  public void setIgnoreSSL(boolean ignoreSSL) {
    this.ignoreSSL = ignoreSSL;
  }
}
