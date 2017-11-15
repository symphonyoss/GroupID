<h3>LOCAL DEPLOYMENT STEPS</h3>
<h4>Using Sample Files: </h4>
   1. Change the "keyStoreFile" property in [application.properties](./src/main/resources/application.properties) to the 
   full file path of [server.keystore](./src/main/resources/sample/server.keystore) the  file in the sample resources.
   2. Change the "trustStoreFile" property in [application.properties](./src/main/resources/application.properties) to the 
   full file path of [server.truststore](./src/main/resources/sample/server.truststore) the  file in the sample resources.
   3. Change the "databaseUrl" property in [helpdeskservice.yaml](./src/main/resources/helpdeskservice.yaml) to the url
   of your MySQL database.
   4. Change the "databaseUser" property in [helpdeskservice.yaml](./src/main/resources/helpdeskservice.yaml) to the username
   of your MySQL database user.
   5. Change the "databasePassword" property in [helpdeskservice.yaml](./src/main/resources/helpdeskservice.yaml) to the password
   for your MySQL database user.
   6. Run [SpringHelpDeskServiceInit.java](./src/main/java/org/symphonyoss/symphony/bots/helpdesk/service/init/SpringHelpDeskServiceInit.java)
   with system property "app.home" set to the path of the [resources folder](./src/main/resources).
   7. Check that the bot server is running by reaching: https://localhost.symphony.com:8442/helpdesk/service/v1/healthcheck