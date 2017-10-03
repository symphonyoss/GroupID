/*
 * Copyright 2017 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.symphony.helpdesk.service.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class ServiceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(ServiceConfig.class);
  private static final Set<EnvironmentConfigProperty> PROPERTY_SET = new HashSet<>();

  public final static String DATABASE_DRIVER_ENV = "DATABASE_DRIVER";
  public final static String DATABASE_URL_ENV = "DATABASE_URL";
  public final static String DATABASE_USER_ENV = "DATABASE_USER";
  public final static String DATABASE_PASSWORD_ENV = "DATABASE_PASSWORD";
  public final static String MEMBERSHIP_TABLE_NAME_ENV = "MEMBERSHIP_TABLE_NAME";
  public final static String TICKET_TABLE_NAME_ENV = "TICKET_TABLE_NAME";

  //_____________________________Properties_____________________________//
  /**
   * Config
   */
  public final static String CONFIG_DIR = "service.config.dir";
  public final static String CONFIG_FILE = "service.properties";

  /**
   * Database
   */
  public static final String DATABASE_DRIVER = "service.database.driver";
  public static final String DATABASE_URL = "service.db.url";

  /**
   * Passwords
   */
  public static final String DATABASE_USER = "service.database.user";
  public static final String DATABASE_PASSWORD = "service.database.password";

  /**
   * Names
   */
  public static final String MEMBERSHIP_TABLE_NAME = "membership.table.name";
  public static final String TICKET_TABLE_NAME = "ticket.table.name";

  static {
    PROPERTY_SET.add(new EnvironmentConfigProperty(DATABASE_DRIVER_ENV, DATABASE_DRIVER));
    PROPERTY_SET.add(new EnvironmentConfigProperty(DATABASE_PASSWORD_ENV, DATABASE_PASSWORD));
    PROPERTY_SET.add(new EnvironmentConfigProperty(DATABASE_URL_ENV, DATABASE_URL));
    PROPERTY_SET.add(new EnvironmentConfigProperty(DATABASE_USER_ENV, DATABASE_USER));
    PROPERTY_SET.add(new EnvironmentConfigProperty(MEMBERSHIP_TABLE_NAME_ENV, MEMBERSHIP_TABLE_NAME));
    PROPERTY_SET.add(new EnvironmentConfigProperty(TICKET_TABLE_NAME_ENV, TICKET_TABLE_NAME));
  }

  /**
   * Init properties and envs
   */
  public static void init(){
    String configDir = null;
    String propFile = null;

    Configuration c = null;
    try {
      if (configDir == null) {
        configDir = System.getProperty(CONFIG_DIR);
        if (configDir == null) {
          configDir = "com/symphony/adminbot/quickstart";
        }
      }

      if (propFile == null) {
        propFile = CONFIG_FILE;
      }
      propFile = configDir + "/" + propFile;

      LOG.info("Using bot.properties file location: {}", propFile);

      c = new PropertiesConfiguration(propFile);

      for(EnvironmentConfigProperty property: PROPERTY_SET){
        property.initProperty(c);
      }
    } catch (ConfigurationException e) {
      LOG.error("Configuration init exception: ", e);
    }
  }

  /**
   * If env exists, use env, otherwise use default config property
   */
  static class EnvironmentConfigProperty {
    private String envName;
    private String propertyName;

    EnvironmentConfigProperty(String envName, String propertyName){
      this.envName = envName;
      this.propertyName = propertyName;
    }

    void initProperty(Configuration configuration){
      if (System.getProperty(propertyName) == null) {
        if (System.getenv(envName) != null) {
          System.setProperty(propertyName, System.getenv(envName));
        } else {
          System.setProperty(propertyName, configuration.getString(propertyName));
        }
      }
    }
  }

}
