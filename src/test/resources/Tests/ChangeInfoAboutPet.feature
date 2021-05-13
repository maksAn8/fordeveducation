Feature: Change info about the pet

  @petsTests @changeInfoAboutPet
  Scenario Outline: Change info about the pet
    When I add new to the store
    Then I check the status of added pet
    And I change name of the pet to "<newName>"
    Then I validate JSON Schema of received response

    Examples:
      | newName |
      | Bob1    |
      | Bob2    |
      | Bob3    |
      | Bob4    |
      | Bob5    |