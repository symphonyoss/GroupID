Meta:

Narrative:
As a user
I want to save and recover membership data
So that I can assert those actions are available and working.

Scenario: Create an agent membership without id
When I call the create membership API for agent without id
Then receive a bad request error from membership API caused by id missing in the body

Scenario: Create an agent membership without group id
When I call the create membership API for agent without group id
Then receive a bad request error from membership API caused by groupId missing in the body

Scenario: Create an agent membership without type
When I call the create membership API for agent without type
Then receive a bad request error from membership API caused by type missing in the body

Scenario: Create a client membership
When I call the create membership API for client
Then check that client membership exists

Scenario: Create a membership duplicate
When I call the create membership API for client duplicated
Then receive a bad request error caused by membership already exists

Scenario: Create an agent membership
When I call the create membership API for agent
Then check that agent membership exists

Scenario: Retrieve an agent membership
When I call the search membership API for agent
Then check that agent membership exists

Scenario: Retrieve a client membership
When I call the search membership API for client
Then check that client membership exists

Scenario: Search an unexistent membership
When I call the search membership API for unexistent client
Then receive a no content response

Scenario: Search a membership for unexistent path
When I call the search membership API for unexistent path
Then receive a not found error

Scenario: Update a membership agent
When call the update membership API for agent
Then check that agent was updated

Scenario: Try to update a client and receive an error
When call the update membership API for client
Then receive a not found error

Scenario: Try to delete an unexistent agent
When call the delete membership API for an unexistent agent
Then receive successfull message even there is no agent

Scenario: Try to delete an agent with wrong parameters
When call the delete membership API with wrong parameters
Then receive a not found error

Scenario: Delete an agent
When call the delete membership API for agent
And I call the search membership API for agent
Then check that agent no longer exists

