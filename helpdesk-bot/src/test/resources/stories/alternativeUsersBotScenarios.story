Meta:

Narrative:
As a user
I want to validate alternative users scenarios
So that I can assert those actions are available and working.

Scenario: An agent attend two tickets
Given a new user account helpdesk_client1 with roles INDIVIDUAL
And a certificate for helpdesk_client1 user
When helpdesk_client1 user authenticates using a certificate
And helpdesk_client1 sends an initial question to the bot
Then bot can verify a new ticket was created in the queue room
And helpdesk_client1 can verify the ticket successfully created message in the client room
When agent1 agent authenticates using a certificate
And agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the history conversation in the ticket room
And helpdesk_client1 can verify the ticket claimed message in the client room
Given a new user account helpdesk_client2 with roles INDIVIDUAL
And a certificate for helpdesk_client2 user
When helpdesk_client2 user authenticates using a certificate
And helpdesk_client2 sends another initial question to the bot
Then bot can verify a new ticket was created in the queue room
And helpdesk_client2 can verify the ticket successfully created message in the client room
When agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the history conversation in the ticket room
And helpdesk_client2 can verify the ticket claimed message in the client room