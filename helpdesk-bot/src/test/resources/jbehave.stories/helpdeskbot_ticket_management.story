Meta: @helpdeskbot

Narrative:
As a user
I want to perform a ticket management with bot
So that I can achieve a business goal

Scenario: Create a chat (ticket) with helpdeskbot
Given a new user helpdesk_super_user in a private pod with roles INDIVIDUAL,SUPER_ADMINISTRATOR
!--And a certificate for API user helpdesk_super_user
!--When API user helpdesk_super_user authenticates using a certificate
When user helpdesk_super_user logs in
And set bot user helpdeskbot
And user helpdesk_super_user creates an IM with helpdeskbot
And helpdeskbot create a ticket to help the client helpdesk_super_user




