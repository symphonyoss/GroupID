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

package org.symphonyoss.symphony.bots.utility.template;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick.tarsillo on 7/5/17.
 *
 * Data to generate template from.
 */
public class TemplateData {
  protected interface TemplateEnums {
    String getReplacement();
  }

  private Map<String, String> replacementHash = new HashMap<>();

  public void addData(String replace, String replacement){
    replacementHash.put(replace, replacement);
  }

  protected void addField(String replace){
    replacementHash.put(replace, null);
  }

  protected void putFields(TemplateEnums[] templateEnums) {
    for(TemplateEnums replacementEnums : templateEnums) {
      addField(replacementEnums.getReplacement());
    }
  }

  public Map<String, String> getReplacementHash() {
    return replacementHash;
  }
}
