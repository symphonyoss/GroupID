Meta:

Narrative:
As a user
I want to save and recover ticket data
So that I can assert those actions are available and working.

Scenario: Create a ticket without id
When I call the create ticket API without id
Then receive a bad request error from ticket API caused by id missing in the body

Scenario: Create a ticket without group id
When I call the create ticket API without group id
Then receive a bad request error from ticket API caused by groupId missing in the body

Scenario: Create a ticket without state
When I call the create ticket API without state
Then receive a bad request error from ticket API caused by state missing in the body

Scenario: Create a ticket
When I call the create ticket API
Then check that ticket exists

Scenario: Create a ticket duplicate
When I call the create ticket API for ticket duplicated
Then receive a bad request error caused by ticket already exists

Scenario: Retrieve a ticket
When I call the get ticket API
Then check that ticket exists

Scenario: Retrieve an unexistent ticket
When I call the get ticket API for unexistent ticket
Then receive a no content response from ticket API

Scenario: Get a ticket for unexistent path
When I call the get ticket API for unexistent path
Then receive a not found error from ticket API

Scenario: Search a ticket without groupId
When I call the search ticket API without groupId
Then receive a bad request error from ticket API caused by groupId missing in the parameters

Scenario: Search a ticket by groupId
When I call the search ticket API with groupId
Then check that ticket was found

Scenario: Search a ticket by clientStreamId
When I call the search ticket API with clientStreamId
Then check that ticket was found

Scenario: Search a ticket by serviceStreamId
When I call the search ticket API with serviceStreamId
Then check that ticket was found

Scenario: Search a ticket
When I call the search ticket API
Then check that ticket was found

Scenario: Search a ticket with invalid groupId
When I call the search ticket API with invalid groupId
Then check that ticket was not found

Scenario: Search a ticket with invalid clientStreamId
When I call the search ticket API with invalid clientStreamId
Then check that ticket was not found

Scenario: Search a ticket with invalid serviceStreamId
When I call the search ticket API with invalid serviceStreamId
Then check that ticket was not found

Scenario: Update ticket
When call the update ticket API
Then check that ticket was updated

Scenario: Try to update the ticket and receive an error
When call the update ticket API with invalid path
Then receive a not found error from ticket API

Scenario: Try to update an unexistent ticket
When call the update ticket API for an unexistent ticket
Then receive a bad request error caused by ticket not found

Scenario: Try to delete an unexistent ticket
When call the delete ticket API for an unexistent ticket
Then receive successfull message even there is no ticket

Scenario: Try to delete a ticket with wrong parameters
When call the delete ticket API with wrong parameters
Then receive a not found error from ticket API

Scenario: Delete a ticket
When call the delete ticket API
And I call the get ticket API
Then check that ticket no longer exists