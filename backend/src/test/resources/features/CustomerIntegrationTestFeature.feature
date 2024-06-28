Feature: Customer Management
  As a user of the customer management system
  I want to be able to add, get, delete, and update customers
  So that I can manage customer information efficiently

  Background: Customer setup
    Given Generated a random customer
    When Adding the customer

  Scenario: Add a new customer
    Then The added customer is found

  Scenario: Get all customers
    And Getting all customers
    Then The list of customers is returned
    And The list contains the added customer

  Scenario: Get customer by id
    And Getting the customer by customer id
    Then The added customer is found

  Scenario: Delete customer by id
    And Delete customer by id
    And Getting all customers
    Then The list does not contain the deleted customer

  Scenario Outline: Update customer
    And Generating customer update request
      | name   | email   | age   |
      | <name> | <email> | <age> |
    And Update customer
    Then The updated customer is found

    Examples:
      | name  | email          | age |
      | Test1 | test1@mail.com | 23  |
      | Test2 |                |     |
      |       | test@mail.com  |     |
      |       |                | 23  |
