Meta:

Narrative:
As a user
I want to perform an action
So that I can achieve a business goal

Scenario: Create an agent membership without id
When I call the create membership API for agent without id
Then receive a bad request error

Scenario: Create an agent membership without gourp id
When I call the create membership API for agent without group id
Then receive a bad request error

Scenario: Create an agent membership without type
When I call the create membership API for agent without type
Then receive a bad request error

Scenario: Create a client membership
When I call the create membership API for client
Then check that membership client was created/founded

Scenario: Create an agent membership
When I call the create membership API for agent
Then check that membership agents was created/founded

Scenario: Retrieve an agent membership
When I call the search membership API for agent
Then check that membership agents was created/founded

Scenario: Retrieve a client membership
When I call the search membership API for client
Then check that membership client was created/founded

Scenario: Search an unexistent membership
When I call the search membership API for unexistent client
Then receive a no content response

Scenario: Search a membership for unexistent path
When I call the search membership API for unexistent path
Then receive a not found error

Scenario: Update a membership agent
When call the update membership API for agent
Then check that agent was updated



