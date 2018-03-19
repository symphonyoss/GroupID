Meta:

Narrative:
As a user
I want to save and recover authentication tokens
So that I can assert those actions are available and working.

Scenario: Authenticate application without appId
When I call the app authentication API without appId
Then receive a bad request error from app authentication API caused by appId missing in the body

Scenario: Authenticate application
When I call the app authentication API with valid appId
Then check the app token was created

Scenario: Validate tokens without appId
When I call the tokens validation API without appId
Then receive a bad request error from tokens validation API caused by appId missing in the body

Scenario: Validate tokens without appToken
When I call the tokens validation API without appToken
Then receive a bad request error from tokens validation API caused by appToken missing in the body

Scenario: Validate tokens without symphonyToken
When I call the tokens validation API without symphonyToken
Then receive a bad request error from tokens validation API caused by symphonyToken missing in the body

Scenario: Validate tokens with invalid appToken
When I call the tokens validation API with invalid appToken
Then receive an unauthorized error

Scenario: Validate tokens with invalid symphonyToken
When I call the tokens validation API with invalid symphonyToken
Then receive an unauthorized error

Scenario: Validate tokens with valid tokens
When I call the tokens validation API with valid tokens
Then check the tokens were validated

Scenario: Validate JWT without data
When I call the JWT validation API without data
Then receive a bad request error from JWT validation API caused by jwt missing in the body

Scenario: Validate JWT with invalid token
When I call the JWT validation API with invalid token
Then receive an unauthorized error

Scenario: Validate JWT with valid token
Given a generated JWT from user
When I call the JWT validation API with valid token
Then check the user id
