Meta:

Narrative:
As a user
I want to interact with bot user to manage tickets
So that I can assert those actions are available and working.

Scenario: Create a conversation with bot
Given a new user account helpdesk_client1 with roles INDIVIDUAL
And a certificate for helpdesk_client1 user
When helpdesk_client1 user authenticates using a certificate
And helpdesk_client1 sends an initial question to the bot
Then bot can verify a new ticket was created in the queue room
And helpdesk_client1 can verify the ticket successfully created message in the client room

Scenario: Claim ticket
When agent1 agent authenticates using a certificate
And agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the history conversation in the ticket room
And helpdesk_client1 can verify the ticket claimed message in the client room

Scenario: Answer questions
When agent1 agent authenticates using a certificate
And agent1 answer the client question
Then helpdesk_client1 can verify the agent answer in the client room

Scenario: Join conversation
When agent2 agent authenticates using a certificate
And agent2 user join the conversation
Then bot can verify the agent2 user was added to the ticket room
And agent2 user can see all the history conversation in the ticket room

Scenario: Close conversation
When agent1 agent authenticates using a certificate
And agent1 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room
