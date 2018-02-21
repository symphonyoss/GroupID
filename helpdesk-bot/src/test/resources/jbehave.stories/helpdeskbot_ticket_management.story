Meta: @helpdeskbot

Narrative:
As a user
I want to perform a ticket management with bot
So that I can achieve a business goal

!-- Create ticket scenarios

Scenario: Create a chat (ticket) with helpdeskbot
Given a new user helpdesk_super_user in a private pod with roles INDIVIDUAL,SUPER_ADMINISTRATOR
!--And a certificate for API user helpdesk_super_user
!--When API user helpdesk_super_user authenticates using a certificate
When user helpdesk_super_user logs in
And set bot user helpdeskbot
And user helpdesk_super_user creates an IM with helpdeskbot
And helpdeskbot create a ticket to help the client helpdesk_super_user
Then helpdeskbot send a successful message to helpdesk_super_user

Scenario: Try to create new ticket without close other before
Then helpdesk_super_user ask help to helpdeskbot
And helpdeskbot send a message to ticket room without create new ticket
And helpdeskbot not send a successful message to helpdesk_super_user

!-- Claim ticket scenarios

Scenario: Claim a ticket
Given a new user helpdesk_agent in a private pod with roles INDIVIDUAL
When user helpdesk_agent logs in
Then user helpdesk_agent join into a queue room
And user helpdesk_agent claim a ticket in the queue room
And user helpdesk_agent join into a ticket room

Scenario: Claim a ticket


!-- Join conversation scenarios

Scenario: Join conversation
Given a new user helpdesk_agent2 in a private pod with roles INDIVIDUAL
When user helpdesk_agent2 logs in
Then user helpdesk_agent2 join into a queue room
And user helpdesk_agent2 join conversation of a claimed ticket
And user helpdesk_agent2 join into a ticket room


!-- Close ticket scenarios
Scenario: Close ticket
When helpdesk_super_user send a close message to helpdeskbot
Then helpdeskbot send a message to helpdesk_super_user
And helpdeskbot close the ticket room










