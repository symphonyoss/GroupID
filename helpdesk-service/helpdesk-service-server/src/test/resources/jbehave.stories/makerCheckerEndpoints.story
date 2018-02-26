Meta:

Narrative:
As a user
I want to create update and read a makerchecker
So that I can achieve a business goal

Scenario: Create a maker/checker
When call the create makerchecker API
Then receive a successful created message

Scenario: Create a maker/checker with invalid id
When call the create makerchecker API with invalid id
Then receive a created error message

Scenario: Create a maker/checker with invalid stream id
When call the create makerchecker API with invalid stream id
Then receive a created error message

Scenario: Create a maker/checker with invalid maker id
When call the create makerchecker API with invalid maker id
Then receive a created error message

Scenario: Create a maker/checker with same id
When call the create makerchecker API with same id
Then receive a created error message

Scenario: Search for a maker/checker
When call the read makerchecker API
Then receive a successful founded message

Scenario: Search for an invalid maker/checker
When call the read makerchecker API with invalid id
Then receive a founded error message

Scenario: Search for a maker/checker with invalid parameter
When call the read makerchecker API with invalid parameter
Then receive a founded error message

Scenario: try to update a maker/checker
When call the update makerchecker API and returns bad request
Then receive an updated error message

Scenario: Update a maker/checker
When call the update makerchecker API
Then receive a successful updated message


