package com.example.demo.customer.repository;

import com.example.demo.customer.dao.AbstractTestcontainersUnitTest;
import com.example.demo.customer.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(UUID.randomUUID() + FAKER.internet().safeEmailAddress());
        customer.setAge(new Random().nextInt(16, 80));
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