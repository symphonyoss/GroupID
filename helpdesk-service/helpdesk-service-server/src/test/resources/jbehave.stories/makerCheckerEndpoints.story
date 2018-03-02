Meta:

Narrative:
As a user
I want to save and recover maker/checker data
So that I can assert those actions are available and working.

Scenario: Create a maker/checker
When call the create makerchecker API
Then check that makerchecker exists

Scenario: Create a maker/checker with invalid id
When call the create makerchecker API with invalid id
Then receive a bad request error caused by id missing in body

Scenario: Create a maker/checker with invalid stream id
When call the create makerchecker API with invalid stream id
Then receive a bad request error caused by streamId missing in body

Scenario: Create a maker/checker with invalid maker id
When call the create makerchecker API with invalid maker id
Then receive a bad request error caused by makerId missing in body

Scenario: Create a maker/checker with same id
When call the create makerchecker API with same id
Then receive an internal server error

Scenario: Search for a maker/checker
When call the retrieve makerchecker API
Then check that makerchecker exists

Scenario: Search for an invalid maker/checker
When call the read makerchecker API with invalid id
Then receive a no content message

Scenario: Search for a maker/checker with invalid parameter
When call the read makerchecker API with invalid parameter
Then receive a method not allowed error

Scenario: try to update a maker/checker
When call the update makerchecker API and returns bad request
Then receive a bad request error caused by makerId missing in body

Scenario: Update a maker/checker
When call the update makerchecker API
Then check that makerchecker was updated
