Feature: User registration

  @userTests @createUser
  Scenario: User registration
    When I register new user in the store
    Then I check user email