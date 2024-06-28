package com.example.demo.customer.controller.steps;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.model.CustomerRegistrationRequest;
import com.example.demo.customer.model.CustomerUpdateRequest;
import com.example.demo.exception.NotFoundException;
import io.cucumber.java.After;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.demo.customer.utils.TestUtils.FAKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@CucumberContextConfiguration
public class CustomerIntegrationTestSteps {

    private CustomerRegistrationRequest registrationRequest;
    private CustomerUpdateRequest updateRequest;
    private List<Customer> customers;
    private Customer customer;

    public static final String URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;

    @DataTableType
    public CustomerUpdateRequest convertToCustomer(Map<String, String> map) {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(map.get("name"));
        customerUpdateRequest.setEmail(map.get("email"));
        if (map.get("age") != null) {
            customerUpdateRequest.setAge(Integer.valueOf(map.get("age")));
        }
        return customerUpdateRequest;
    }

    @Given("Generated a random customer")
    public void generated_a_random_customer() {
        registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setName(FAKER.name().fullName());
        registrationRequest.setEmail(FAKER.internet().safeEmailAddress());
        registrationRequest.setAge(new Random().nextInt(16, 99));
    }

    @When("Adding the customer")
    public void adding_the_customer() {
        customer = webTestClient.post()
                .uri(URI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
    }

    @When("Getting all customers")
    public void getting_all_customers() {
        customers = webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
    }

    @When("Getting the customer by customer id")
    public void getting_the_customer_by_customer_id() {
        customer = webTestClient.get()
                .uri(URI + "/{id}", customer.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
    }

    @When("Delete customer by id")
    public void delete_customer_by_id() {
        webTestClient.delete()
                .uri(URI + "/{id}", customer.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Then("The added customer is found")
    public void the_added_customer_is_found() {
        assertAll(
                () -> assertThat(customer.getId()).isNotNull(),
                () -> assertThat(customer.getName()).isEqualTo(registrationRequest.getName()),
                () -> assertThat(customer.getEmail()).isEqualTo(registrationRequest.getEmail()),
                () -> assertThat(customer.getAge()).isEqualTo(registrationRequest.getAge())
        );
    }

    @Then("The list of customers is returned")
    public void the_list_of_customers_should_be_returned() {
        assertThat(customers).isNotEmpty();
    }

    @Then("The list contains the added customer")
    public void the_list_contains_the_added_customer() {
        assertThat(customers).contains(customer);
    }

    @Then("The list does not contain the deleted customer")
    public void throw_customer_not_found() {
        assertThat(customers).doesNotContain(customer);
    }

    @When("Generating customer update request")
    public void generating_customer_update_request(CustomerUpdateRequest updateRequest) {
        this.updateRequest = updateRequest;
    }

    @When("Update customer")
    public void update_customer() {
        customer = webTestClient.put()
                .uri(URI + "/{id}", customer.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
    }

    @Then("The updated customer is found")
    public void the_updated_customer_is_found() {
        assertAll(
                () -> {
                    if (updateRequest.getName() != null) {
                        assertThat(customer.getName()).isEqualTo(updateRequest.getName());
                    }
                },
                () -> {
                    if (updateRequest.getEmail() != null) {
                        assertThat(customer.getEmail()).isEqualTo(updateRequest.getEmail());
                    }
                },
                () -> {
                    if (updateRequest.getAge() != null) {
                        assertThat(customer.getAge()).isEqualTo(updateRequest.getAge());
                    }
                }
        );
    }

    @After
    public void after() {
        try {
            webTestClient.delete()
                    .uri(URI + "/{id}", customer.getId())
                    .accept(APPLICATION_JSON)
                    .exchange();
        } catch (NotFoundException exception) {
            // ignore
        }

    }
}
