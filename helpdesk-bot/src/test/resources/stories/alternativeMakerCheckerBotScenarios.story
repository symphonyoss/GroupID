Meta:

Narrative:
As a user
I want to perform an alternatives scenarios with makerchecker
So that I can assert those actions are available and working.

Scenario: All agents join to same ticket room
When helpdesk_client1 sends an initial personal question to the bot
Then bot can verify a new ticket was created in the queue room with personal question
And helpdesk_client1 can verify the ticket successfully created message in the client room
When agent1 user claims the latest ticket created
Then bot can verify the agent1 user was added to the ticket room
When agent2 agent authenticates using a certificate
And agent2 user join the conversation
Then bot can verify the agent2 user was added to the ticket room
When agent3 agent authenticates using a certificate
And agent3 user join the conversation
Then bot can verify the agent3 user was added to the ticket room

Scenario: Try to approve an approved attachment
When agent1 agent authenticates using a certificate
And agent1 agent sends an attachment Attachment1.jpg
When agent2 agent authenticates using a certificate
And agent2 agent approve the attachment
Then agent2 can verify the attachment Attachment1.jpg is approved
When agent3 agent authenticates using a certificate
And agent3 agent try approves the attachment
Then agent3 can verify the attachment Attachment1.jpg was approved by agent2

Scenario: Close ticket
When agent1 agent authenticates using a certificate
And agent1 user sends a message to close the ticket
Then bot can verify there are no agent in the ticket room
And helpdesk_client1 can verify the ticket closed message in the client room