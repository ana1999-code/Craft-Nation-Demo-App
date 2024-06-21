package com.example.demo.customer.dao;

import com.example.demo.customer.AbstractTestcontainersUnitTest;
import com.example.demo.customer.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.example.demo.customer.utils.TestUtils.FAKER;
import static com.example.demo.customer.utils.TestUtils.getCustomer;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerJdbcDataAccessServiceTest extends AbstractTestcontainersUnitTest {

    private CustomerJdbcDataAccessService jdbcDataAccessService;

    @BeforeEach
    void setUp() {
        jdbcDataAccessService = new CustomerJdbcDataAccessService(getJdbcTemplate());
    }

    @Test
    void findAllCustomers() {
        Customer customer = getCustomer();
        jdbcDataAccessService.save(customer);

        List<Customer> allCustomers = jdbcDataAccessService.findAllCustomers();

        assertThat(allCustomers.contains(customer)).isTrue();
    }

    @Test
    void findCustomerById() {
        Customer customer = getCustomer();
        Customer saved = jdbcDataAccessService.save(customer);

        Optional<Customer> customerById = jdbcDataAccessService.findCustomerById(saved.getId());

        assertThat(customerById).isPresent()
                .hasValueSatisfying(cust -> assertThat(cust.equals(customer)).isTrue());
    }

    @Test
    void existsByEmail() {
        Customer customer = getCustomer();
        Customer saved = jdbcDataAccessService.save(customer);

        boolean existsByEmail = jdbcDataAccessService.existsByEmail(saved.getEmail());

        assertThat(existsByEmail).isTrue();
    }

    @Test
    void existsByEmailReturnsFalseWhenDoesNotExists() {
        boolean existsByEmail = jdbcDataAccessService
                .existsByEmail(UUID.randomUUID() + FAKER.internet().safeEmailAddress());

        assertThat(existsByEmail).isFalse();
    }

    @Test
    void save() {
        Customer customer = getCustomer();
        Customer saved = jdbcDataAccessService.save(customer);

        assertThat(saved.equals(customer)).isTrue();
    }

    @Test
    void existsById() {
        Customer customer = getCustomer();
        Customer saved = jdbcDataAccessService.save(customer);

        boolean existsById = jdbcDataAccessService.existsById(saved.getId());

        assertThat(existsById).isTrue();
    }

    @Test
    void existsByIdReturnsFalseWhenDoesNotExists() {
        boolean existsById = jdbcDataAccessService
                .existsById(-1L);

        assertThat(existsById).isFalse();
    }

    @Test
    void deleteById() {
        Customer customer = getCustomer();
        Customer saved = jdbcDataAccessService.save(customer);

        jdbcDataAccessService.deleteById(saved.getId());

        boolean existsById = jdbcDataAccessService.existsById(saved.getId());

        assertThat(existsById).isFalse();
    }

    @Test
    void updateCustomer() {
        Customer customer = getCustomer();
        Customer saved = jdbcDataAccessService.save(customer);

        saved.setName(FAKER.name().fullName());
        saved.setEmail(UUID.randomUUID() + FAKER.internet().safeEmailAddress());
        saved.setAge(new Random().nextInt(16, 98));

        Customer updatedCustomer = jdbcDataAccessService.updateCustomer(saved);

        assertThat(updatedCustomer.equals(customer)).isFalse();
        assertThat(updatedCustomer.equals(saved)).isTrue();
    }
}