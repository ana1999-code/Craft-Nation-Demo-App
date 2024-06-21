package com.example.demo.customer.repository;

import com.example.demo.customer.dao.AbstractTestcontainersUnitTest;
import com.example.demo.customer.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static com.example.demo.customer.utils.TestUtils.FAKER;
import static com.example.demo.customer.utils.TestUtils.getCustomer;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// disable the embedded database in order to use our database
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// if not extended, will use the real database
class CustomerRepositoryTest extends AbstractTestcontainersUnitTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void existsByEmail() {
        Customer customer = getCustomer();
        Customer saved = customerRepository.save(customer);

        boolean existsByEmail = customerRepository.existsByEmail(saved.getEmail());
        assertThat(existsByEmail).isTrue();
    }

    @Test
    void existsByEmailReturnsFalseWhenDoesNotExists() {
        boolean existsByEmail = customerRepository
                .existsByEmail(UUID.randomUUID() + FAKER.internet().safeEmailAddress());

        assertThat(existsByEmail).isFalse();
    }
}