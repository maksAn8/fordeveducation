Feature: Create user

  @userTests @createUser
  Scenario: Create user
    When I register new user in the store
    Then I check user email