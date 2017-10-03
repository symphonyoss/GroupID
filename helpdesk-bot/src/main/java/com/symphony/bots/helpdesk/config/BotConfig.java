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

package com.symphony.bots.helpdesk.config;

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
public class BotConfig {
  private static final Logger LOG = LoggerFactory.getLogger(BotConfig.class);
  private static final Set<EnvironmentConfigProperty> PROPERTY_SET = new HashSet<>();

  /**
   * File Path
   */
  public final static String TEMPLATE_DIR_ENV = "TEMPLATE_DIR";
  public static final String SESSION_CONTEXT_DIR_ENV = "SESSION_CONTEXT_DIR";

  /**
   * File names
   */
  public final static String MAKERCHECKER_MESSAGE_TEMPLATE_NAME_ENV = "MAKERCHECKER_MESSAGE_TEMPLATE_NAME";
  public final static String MAKERCHECKER_ENTITY_TEMPLATE_NAME_ENV = "MAKERCHECKER_ENTITY_TEMPLATE_NAME";

  /**
   * URLs
   */
  public static final String MEMBER_SERVICE_URL_ENV = "MEMBER_SERVICE_URL";
  public static final String TICKET_SERVICE_URL_ENV = "TICKET_SERVICE_URL";

  //_____________________________Properties_____________________________//
  /**
   * Config
   */
  public final static String CONFIG_DIR = "service.config.dir";
  public final static String CONFIG_FILE = "service.properties";

  /**
   * File Path
   */
  public final static String TEMPLATE_DIR = "bot.template.dir";
  public static final String SESSION_CONTEXT_DIR = "bot.session.context.dir";

  /**
   * File names
   */
  public final static String MAKERCHECKER_MESSAGE_TEMPLATE_NAME = "bot.makerchecker.message.template.name";
  public final static String MAKERCHECKER_ENTITY_TEMPLATE_NAME = "bot.makerchecker.entity.template.name";

  /**
   * URLs
   */
  public static final String MEMBER_SERVICE_URL = "bot.member.service.url";
  public static final String TICKET_SERVICE_URL = "bot.ticket.service.url";

  static {
    PROPERTY_SET.add(new EnvironmentConfigProperty(TEMPLATE_DIR_ENV, TEMPLATE_DIR));
    PROPERTY_SET.add(new EnvironmentConfigProperty(MAKERCHECKER_MESSAGE_TEMPLATE_NAME_ENV,
        MAKERCHECKER_MESSAGE_TEMPLATE_NAME));
    PROPERTY_SET.add(new EnvironmentConfigProperty(MAKERCHECKER_ENTITY_TEMPLATE_NAME_ENV,
        MAKERCHECKER_ENTITY_TEMPLATE_NAME));
    PROPERTY_SET.add(new EnvironmentConfigProperty(SESSION_CONTEXT_DIR_ENV, SESSION_CONTEXT_DIR));
    PROPERTY_SET.add(new EnvironmentConfigProperty(MEMBER_SERVICE_URL_ENV, MEMBER_SERVICE_URL));
    PROPERTY_SET.add(new EnvironmentConfigProperty(TICKET_SERVICE_URL_ENV, TICKET_SERVICE_URL));
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
   * If env exists, use env, otherwise use config property
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
