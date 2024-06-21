package com.example.demo.customer.dao;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerJpaDataAccessServiceTest {

    @InjectMocks
    private CustomerJpaDataAccessService jpaDataAccessService;

    @Mock
    private CustomerRepository customerRepository;

    private final Customer customer = new Customer(1L, "John", "john@mail.com", 23);

    @Test
    void findAllCustomers() {
        when(customerRepository.findAll())
                .thenReturn(List.of(customer));

        List<Customer> allCustomers = jpaDataAccessService.findAllCustomers();

        assertThat(allCustomers.contains(customer)).isTrue();
        verify(customerRepository).findAll();
    }

    @Test
    void findCustomerById() {
        when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));

        Optional<Customer> customerById = jpaDataAccessService.findCustomerById(customer.getId());

        assertThat(customerById).isPresent()
                .hasValueSatisfying(foundCustomer -> assertThat(foundCustomer).isEqualTo(customer));
        verify(customerRepository).findById(customer.getId());
    }

    @Test
    void existsByEmail() {
        when(customerRepository.existsByEmail(customer.getEmail()))
                .thenReturn(true);

        boolean existsByEmail = jpaDataAccessService.existsByEmail(customer.getEmail());

        assertThat(existsByEmail).isTrue();
        verify(customerRepository).existsByEmail(customer.getEmail());
    }

    @Test
    void existsByEmailReturnsFalseWhenDoesNotExists() {
        when(customerRepository.existsByEmail(customer.getEmail()))
                .thenReturn(false);

        boolean existsByEmail = jpaDataAccessService.existsByEmail(customer.getEmail());

        assertThat(existsByEmail).isFalse();
        verify(customerRepository).existsByEmail(customer.getEmail());
    }

    @Test
    void save() {
        when(customerRepository.save(customer))
                .thenReturn(customer);

        Customer saved = jpaDataAccessService.save(customer);

        assertThat(saved).isEqualTo(customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void existsById() {
        when(customerRepository.existsById(customer.getId()))
                .thenReturn(true);

        boolean existsById = jpaDataAccessService.existsById(customer.getId());

        assertThat(existsById).isTrue();
        verify(customerRepository).existsById(customer.getId());
    }

    @Test
    void existsByIdReturnsFalseWhenDoesNotExists() {
        when(customerRepository.existsById(customer.getId()))
                .thenReturn(false);

        boolean existsById = jpaDataAccessService.existsById(customer.getId());

        assertThat(existsById).isFalse();
        verify(customerRepository).existsById(customer.getId());
    }

    @Test
    void deleteById() {
        Mockito.doNothing().when(customerRepository).deleteById(customer.getId());

        assertThatNoException().isThrownBy(() -> customerRepository.deleteById(customer.getId()));
        verify(customerRepository).deleteById(customer.getId());
    }

    @Test
    void updateCustomer() {
        when(customerRepository.save(customer))
                .thenReturn(customer);

        Customer updatedCustomer = jpaDataAccessService.updateCustomer(customer);

        assertThat(updatedCustomer).isEqualTo(customer);
        verify(customerRepository).save(customer);
    }
}