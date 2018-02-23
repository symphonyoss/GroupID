Meta:

Narrative:
As a user
I want to interact with bot user to manage tickets
So that I can assert those actions are available and working.

Scenario: Create a conversation with bot
Given a new user account helpdesk_client1 with roles INDIVIDUAL
And a certificate for helpdesk_client1 user
When helpdesk_client1 user authenticates using a certificate
Then helpdesk_client1 sends an initial question to the bot