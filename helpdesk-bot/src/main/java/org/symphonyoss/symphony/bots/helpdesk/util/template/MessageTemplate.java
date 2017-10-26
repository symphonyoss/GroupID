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

package org.symphonyoss.symphony.bots.helpdesk.util.template;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/4/17.
 *
 * Template for replacing symphony attributes using symphony data
 */
public class MessageTemplate {

  private String template;

  public MessageTemplate(String template){
    this.template = template;
  }

  public String buildFromData(TemplateData templateData) {
    String message = template;
    for(String replace: templateData.getReplacementHash().keySet()){
      message = replace(message, replace, templateData.getReplacementHash().get(replace));
    }

    return message;
  }

  /**
   * Replaces all occurrences of sequence with given replacement.
   * Replace sequence will be surrounded in {}. Example: {REPLACE_THIS}
   *
   * To use logic templates, add a # in front of replace sequence and close with a second
   * replacement sequence with a / added in front of it.
   * Example: {#REPLACE_THIS} Foo bar {/REPLACE_THIS}
   * If replacement is null for replacement sequence "REPLACE_THIS", "Foo bar" will be removed from the template.
   *
   * @param template the template to perform the replacement on
   * @param replace the sequence to replace
   * @param replacement the replacement string
   * @return the new template
   */
  private String replace(String template, String replace, String replacement){
    String doReplacement = template;
    if(replacement != null){
      doReplacement = doReplacement.replace("{" + replace + "}", replacement);
      doReplacement = doReplacement.replace("{#" + replace + "}", "");
      doReplacement = doReplacement.replace("{/" + replace + "}", "");
    } else {
      String[] logicalSplit = doReplacement.split("\\{#" + replace + "\\}");
      Set<String> parseLogic = new LinkedHashSet<>();
      for(String line : logicalSplit) {
        if(!line.contains("{/" + replace + "}")) {
          parseLogic.add(line);
        } else {
          String[] splitGarbage = line.split("\\{/" + replace + "\\}");
          if(splitGarbage.length > 1) {
            parseLogic.add(splitGarbage[1]);
          }
        }
      }

      doReplacement = String.join("", (String[]) parseLogic.toArray(new String[parseLogic.size()]));
    }

    return doReplacement.trim();
  }
}
