package com.example.demo.customer.controller;

import com.example.demo.customer.AbstractTestcontainersUnitTest;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.model.CustomerRegistrationRequest;
import com.example.demo.customer.model.CustomerUpdateRequest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static com.example.demo.customer.service.ValidationUtils.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.example.demo.customer.utils.TestUtils.FAKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = NONE)
@CucumberContextConfiguration
class CustomerIntegrationTest extends AbstractTestcontainersUnitTest {

    public static final String URI = "/api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;

    private CustomerRegistrationRequest registrationRequest;

    private CustomerUpdateRequest customerUpdateRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setName(FAKER.name().fullName());
        registrationRequest.setEmail(FAKER.internet().safeEmailAddress());
        registrationRequest.setAge(new Random().nextInt(16, 99));

        customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(FAKER.name().fullName());
        customerUpdateRequest.setEmail(FAKER.internet().safeEmailAddress());
        customerUpdateRequest.setAge(new Random().nextInt(16, 99));
    }

    @Test
    void getAllCustomers() {
        Customer customer = new Customer();
        customer.setName(registrationRequest.getName());
        customer.setEmail(registrationRequest.getEmail());
        customer.setAge(registrationRequest.getAge());

        webTestClient.post()
                .uri(URI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(customers).contains(customer);
    }

    @Test
    void getCustomerById() throws JsonProcessingException {
        Customer customer = new Customer();
        customer.setName(registrationRequest.getName());
        customer.setEmail(registrationRequest.getEmail());
        customer.setAge(registrationRequest.getAge());

        webTestClient.post()
                .uri(URI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Customer addedCustomer = customers.stream()
                .filter(cust -> cust.getEmail().equals(registrationRequest.getEmail()))
                .findFirst()
                .orElseThrow();

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(addedCustomer);

        webTestClient.get()
                .uri(URI + "/{id}", addedCustomer.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedJson);
    }

    @Test
    void registerCustomer() {
        webTestClient.post()
                .uri(URI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody().jsonPath("name").isEqualTo(registrationRequest.getName())
                .jsonPath("email").isEqualTo(registrationRequest.getEmail())
                .jsonPath("age").isEqualTo(registrationRequest.getAge())
                .jsonPath("id").isNotEmpty();
    }

    @Test
    void deleteCustomer() {
        webTestClient.post()
                .uri(URI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        long id = customers.stream()
                .filter(customer -> customer.getEmail().equals(registrationRequest.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri(URI + "/{id}", id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient.get()
                .uri(URI + "/{id}", id)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("message")
                .isEqualTo(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(id));
    }

    @Test
    void updateCustomer() throws JsonProcessingException {
        Customer customer = new Customer();
        customer.setName(registrationRequest.getName());
        customer.setEmail(registrationRequest.getEmail());
        customer.setAge(registrationRequest.getAge());

        webTestClient.post()
                .uri(URI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        List<Customer> customers = webTestClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Long id = customers.stream()
                .filter(cust -> cust.getEmail().equals(registrationRequest.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer(id, customerUpdateRequest.getName(), customerUpdateRequest.getEmail(), customerUpdateRequest.getAge());

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(updatedCustomer);

        webTestClient.put()
                .uri(URI + "/{id}", id)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(expectedJson);
    }
}