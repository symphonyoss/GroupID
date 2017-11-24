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

package org.symphonyoss.symphony.bots.helpdesk.bot.model.health;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 8/23/17.
 */
public class HealthcheckHelper {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private WebTarget podHealthCheckTarget;
  private WebTarget agentHealthCheckTarget;

  public HealthcheckHelper(String podUrl, String agentUrl) {
    WebTarget agentBaseTarget = ClientBuilder.newClient().target(agentUrl);
    WebTarget podBaseTarget = ClientBuilder.newClient().target(podUrl.replace("/pod", ""));

    podHealthCheckTarget = podBaseTarget.path("webcontroller/HealthCheck");
    agentHealthCheckTarget = agentBaseTarget.path("v1/HealthCheck");
  }

  private Response checkConnectivity(WebTarget target) throws HealthCheckFailedException {
    try {
      Response response = target.request().get();

      if (response.getStatus() != 200) {
        throw new HealthCheckFailedException("The request to " + target.getUri()
            + " returned status code " + response.getStatus() + "'");
      }

      return response;
    } catch (ProcessingException e) {
      throw new HealthCheckFailedException("The URI " + target.getUri()
          + " couldn't be reached because of '" + e.getMessage() + "'");
    }
  }

  public void checkPodConnectivity() throws HealthCheckFailedException {
    checkConnectivity(podHealthCheckTarget).close();
  }

  public void checkAgentConnectivity() throws HealthCheckFailedException {
    Response response = checkConnectivity(agentHealthCheckTarget);
    JsonNode node = null;

    try {
      node = MAPPER.readTree((InputStream) response.getEntity());
    } catch (IOException e) {
      throw new HealthCheckFailedException("Failed to read response entity.");
    }

    String podError = node.get("podConnectivityError") != null ? node.get("podConnectivityError").asText() : null;
    String agentError = node.get("keyManagerConnectivityError") != null ? node.get("keyManagerConnectivityError").asText() : null;

    if(StringUtils.isNotBlank(podError) && StringUtils.isNotBlank(agentError)) {
      throw new HealthCheckFailedException("The pod and the agent are currently having issues. \n"
          + "Pod health check error: " + podError + " \nAgent health check error: " + agentError);
    } else if(StringUtils.isNotBlank(podError)) {
      throw new HealthCheckFailedException("The pod is currently having issues. \n"
          + "Pod health check error: " + podError);
    } else if(StringUtils.isNotBlank(agentError)) {
      throw new HealthCheckFailedException("The agent is currently having issues. \n"
          + "Agent health check error: " + agentError);
    }

    response.close();
  }
}
