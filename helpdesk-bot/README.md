<h3>LOCAL DEPLOYMENT STEPS</h3>
<h4>Using Sample Files: </h4>
   1. Change the "keyStoreFile" property in [application.properties](./src/main/resources/application.properties) to the 
   full file path of [server.keystore](./src/main/resources/sample/server.keystore) the  file in the sample resources.
   2. Change the "trustStoreFile" property in [application.properties](./src/main/resources/application.properties) to the 
   full file path of [server.truststore](./src/main/resources/sample/server.truststore) the  file in the sample resources.
   3. Change the "keyStoreFile" property in [helpdeskbot.yaml](./src/main/resources/helpdeskbot.yaml) to the 
   full file path of the [bot.user1.p12](./src/main/resources/sample/bot.user1.p12) file in the sample resources.
   4. Change the "trustStoreFile" property in [helpdeskbot.yaml](./src/main/resources/helpdeskbot.yaml) to the 
   full file path of the [auth.truststore](./src/main/resources/sample/auth.truststore) file in the sample resources.
   5. Change the "defaultAgentEmail" property in [helpdeskbot.yaml](./src/main/resources/helpdeskbot.yaml) to a user
    in which should be promoted to an agent by default.
   6. Change the "agentStreamId" property in [helpdeskbot.yaml](./src/main/resources/helpdeskbot.yaml) to the stream Id of
    the agent claim ticket room. The bot must be in the defined stream.
   7. Run [SpringHelpDeskBotInit.java](./src/main/java/org/symphonyoss/symphony/bots/helpdesk/init/SpringHelpDeskBotInit.java)
   with system property "app.home" set to the path of the [resources folder](./src/main/resources).
   8. Check that the bot server is running by reaching: https://localhost.symphony.com:8443/helpdesk/v1/Test/healthcheck