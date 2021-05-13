Feature: Add new pet to the store

  @petsTests @addNewPet
  Scenario: Add new pet to the store
    When I add new to the store
    Then I check the status of added pet