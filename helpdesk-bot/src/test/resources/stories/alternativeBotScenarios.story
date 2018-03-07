Meta:

Narrative:
As a user
I want to validate complex bot scenarios
So that I can assert those actions are available and working.

Scenario: Send a message and none agent claim the ticket
Given a new user account helpdesk_client1 with roles INDIVIDUAL
And a certificate for helpdesk_client1 user
When helpdesk_client1 user authenticates using a certificate
And helpdesk_client1 sends an initial question to the bot
Then bot can verify a new ticket was created in the queue room
And helpdesk_client1 can verify the ticket successfully created message in the client room
And bot can verify a new idle message was created in the queue room

Scenario: Close an idle ticket
When agent1 agent authenticates using a certificate
And agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the history conversation in the ticket room
And helpdesk_client1 can verify the ticket claimed message in the client room
When agent1 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room

Scenario: Agent claim ticket then left to this room
When helpdesk_client1 sends an initial question to the bot
Then bot can verify a new ticket was created in the queue room
And helpdesk_client1 can verify the ticket successfully created message in the client room
When agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 leaves the ticket room
Then bot can verify there are no agents in the ticket room
And bot can verify a new idle message was created in the queue room

Scenario: Close another idle ticket by agent2
When agent2 agent authenticates using a certificate
And agent2 user claims the latest ticket created
Then bot can verify the agent2 user was added to the ticket room
And agent2 user can see all the history conversation in the ticket room
When agent2 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room

Scenario: Agent try to approve an approved attachment
When helpdesk_client1 sends an initial question to the bot
Then bot can verify a new ticket was created in the queue room
And helpdesk_client1 can verify the ticket successfully created message in the client room
When agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
When agent2 agent authenticates using a certificate
And agent2 user join the conversation
Then bot can verify the agent2 user was added to the ticket room
When agent3 agent authenticates using a certificate
And agent3 user join the conversation
Then bot can verify the agent3 user was added to the ticket room
When agent1 agent authenticates using a certificate
And agent1 agent sends an attachment Attachment1.jpg
When agent2 agent authenticates using a certificate
And agent2 agent approve the attachment
Then agent2 can verify the attachment Attachment1.jpg is approved
When agent3 agent authenticates using a certificate
And agent3 agent try approves the attachment
Then agent3 can verify the attachment Attachment1.jpg was approved by agent2
When agent1 agent authenticates using a certificate
And agent1 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room