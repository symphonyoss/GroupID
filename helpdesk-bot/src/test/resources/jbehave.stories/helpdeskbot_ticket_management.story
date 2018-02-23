Meta: @helpdeskbot

Narrative:
As a user
I want to perform a ticket management with bot
So that I can achieve a business goal

!-- Create ticket scenarios

Scenario: Create a chat (ticket) with bot
When set bot user helpdeskbot
And user helpdesk_super_user creates an IM with helpdeskbot
And helpdeskbot create a ticket to help the client helpdesk_super_user
Then helpdeskbot send a successful message to helpdesk_super_user

Scenario: Try to create new ticket without close other before
Then helpdesk_super_user talk to helpdeskbot
And helpdeskbot send a message to ticket room without create new ticket
And helpdeskbot not send a successful message to helpdesk_super_user

!-- Claim ticket scenarios

Scenario: Claim a ticket
Then user agent_1 claim a ticket in the queue room
And user agent_1 join into a ticket room

Scenario: Claim a ticket and receive an error
Then user agent_1 claim a ticket in the queue room
And agent_1 receive an error message

Scenario: Try to claim a ticket that other agent was claimed before


!-- Join conversation scenarios

Scenario: Join conversation
Then user agent_2 join conversation of a claimed ticket
And user agent_2 join into a ticket room

Scenario: Try to join conversation and receive an error
Then user agent_2 join conversation of a claimed ticket
And agent_2 receive an error message


!-- Close ticket scenarios
Scenario: Close ticket
When helpdesk_super_user send a close message to helpdeskbot
Then helpdeskbot send a message to helpdesk_super_user
And helpdeskbot close the ticket room


Scenario: Try to close ticket and receive an error
When helpdesk_super_user send a close message to helpdeskbot
Then helpdesk_super_user receive an error message

!-- Idle ticket scenarios

!-- Show history scenarios









