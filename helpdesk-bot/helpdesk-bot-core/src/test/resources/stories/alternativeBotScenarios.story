Meta:

Narrative:
As a user
I want to validate alternative ticktes scenarios
So that I can assert those actions are available and working.

Scenario: Send a message and none agent claim the ticket
Given a new user account helpdesk_client1 with roles INDIVIDUAL
And a certificate for helpdesk_client1 user
When helpdesk_client1 user authenticates using a certificate
And helpdesk_client1 sends an initial personal question to the bot
Then bot can verify a new ticket was created in the queue room with personal question
And helpdesk_client1 can verify the ticket successfully created message in the client room
And bot can verify a new idle message was created in the queue room

Scenario: Close an idle ticket
When agent1 agent authenticates using a certificate
And agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
And agent1 user can see all the personal history conversation in the ticket room
And helpdesk_client1 can verify the ticket claimed message in the client room
When agent1 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room

Scenario: Agent claim ticket then left to this room
When helpdesk_client1 sends an initial personal question to the bot
Then bot can verify a new ticket was created in the queue room with personal question
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
And agent2 user can see all the personal history conversation in the ticket room
When agent2 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room

Scenario: Agent claims a ticket
When helpdesk_client1 sends an initial personal question to the bot
Then bot can verify a new ticket was created in the queue room with personal question
And helpdesk_client1 can verify the ticket successfully created message in the client room
When agent2 agent authenticates using a certificate
And agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room

Scenario: Another agent joins the ticket room
When agent2 agent authenticates using a certificate
And agent2 user join the conversation
Then bot can verify the agent2 user was added to the ticket room

Scenario: Agent leaves the room and the ticket can not be closed
When agent1 agent authenticates using a certificate
Then agent1 leaves the ticket room
And bot can verify only user agent2 is in the ticket room
And bot can verify that ticket still claimed by agent1

Scenario: Remaining agent closes the ticket
When agent2 agent authenticates using a certificate
Then agent2 user can see all the personal history conversation in the ticket room
When agent2 user sends a message to close the ticket
Then bot can verify there are no agents in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room