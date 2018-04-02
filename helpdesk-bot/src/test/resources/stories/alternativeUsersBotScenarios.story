Meta:

Narrative:
As a user
I want to validate user manage two tickets in same time
So that I can assert those actions are available and working.

Scenario: Agent claims a ticket about one user
Given a new user account helpdesk_client1 with roles INDIVIDUAL
And a certificate for helpdesk_client1 user
When helpdesk_client1 user authenticates using a certificate
And helpdesk_client1 sends an initial personal question to the bot
Then bot can verify a new ticket was created in the queue room with personal question
When agent1 agent authenticates using a certificate
And agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the personal history conversation in the ticket room
And helpdesk_client1 can verify the ticket claimed message in the client room

Scenario: Agent claims a ticket about other user
Given a new user account helpdesk_client2 with roles INDIVIDUAL
And a certificate for helpdesk_client2 user
When helpdesk_client2 user authenticates using a certificate
And helpdesk_client2 sends an initial help question to the bot
Then bot can verify a new ticket was created in the queue room with help question
And helpdesk_client2 can verify the ticket successfully created message in the client room
When agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the help history conversation in the ticket room
And helpdesk_client2 can verify the ticket claimed message in the client room

Scenario: Answer the second client
When agent1 agent authenticates using a certificate
And agent1 answer the client question
Then helpdesk_client2 can verify the agent answer in the client room

Scenario: Answer the first client
When agent1 answer the first client question
Then helpdesk_client1 can verify the agent answer your question in the client room

Scenario: Close the last ticket
When agent1 user sends a message to close the ticket (PresentationML 2.0 format)
Then bot can verify there are no agents in the ticket room
And helpdesk_client2 can verify the ticket closed message in the client room

Scenario: Close the first ticket
When agent1 user sends a message to close the other ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room
