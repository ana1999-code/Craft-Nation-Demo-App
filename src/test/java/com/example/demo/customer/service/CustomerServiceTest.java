package com.example.demo.customer.service;

import com.example.demo.customer.dao.CustomerDao;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.model.CustomerRegistrationRequest;
import com.example.demo.customer.model.CustomerUpdateRequest;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.RequestValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.example.demo.customer.service.ValidationUtils.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.example.demo.customer.service.ValidationUtils.EMAIL_DUPLICATE_ERROR;
import static com.example.demo.customer.service.ValidationUtils.NO_CHANGES_FOUND;
import static com.example.demo.customer.utils.TestUtils.FAKER;
import static com.example.demo.customer.utils.TestUtils.getCustomer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerDao customerDao;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private final Customer customer = getCustomer();

    @BeforeEach
    void setUp() {
        customer.setId(1L);
    }

    @Test
    void findAllCustomers() {
        when(customerDao.findAllCustomers())
                .thenReturn(List.of(customer));

        List<Customer> allCustomers = customerService.findAllCustomers();

        assertThat(allCustomers).contains(customer);
        verify(customerDao).findAllCustomers();
    }

    @Test
    void findCustomerById() {
        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));

        Customer customerById = customerService.findCustomerById(customer.getId());

        assertThat(customerById).isEqualTo(customer);
        verify(customerDao).findCustomerById(anyLong());
    }

    @Test
    void findCustomerByIdThrowsWhenDoesNotExists() {
        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> customerService.findCustomerById(customer.getId()));

        assertThat(notFoundException.getMessage())
                .isEqualTo(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(customer.getId()));
        verify(customerDao).findCustomerById(anyLong());
    }

    @Test
    void saveCustomer() {
        when(customerDao.save(any(Customer.class)))
                .thenReturn(customer);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest();
        customerRegistrationRequest.setName(customer.getName());
        customerRegistrationRequest.setEmail(customer.getEmail());
        customerRegistrationRequest.setAge(customer.getAge());

        Customer savedCustomer = customerService.saveCustomer(customerRegistrationRequest);

        assertThat(savedCustomer).isEqualTo(customer);
        verify(customerDao).save(any(Customer.class));
    }

    @Test
    void saveCustomerThrowsWhenDuplicatedEmail() {
        when(customerDao.existsByEmail(anyString()))
                .thenReturn(true);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest();
        customerRegistrationRequest.setName(customer.getName());
        customerRegistrationRequest.setEmail(customer.getEmail());
        customerRegistrationRequest.setAge(customer.getAge());

        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                () -> customerService.saveCustomer(customerRegistrationRequest));

        assertThat(duplicateResourceException.getMessage())
                .isEqualTo(EMAIL_DUPLICATE_ERROR.formatted(customer.getEmail()));
        verify(customerDao).existsByEmail(anyString());
        verify(customerDao, never()).save(any());
    }

    @Test
    void deleteCustomer() {
        when(customerDao.existsById(anyLong()))
                .thenReturn(true);

        assertDoesNotThrow(() -> customerService.deleteCustomer(customer.getId()));

        verify(customerDao).existsById(anyLong());
        verify(customerDao).deleteById(anyLong());
    }

    @Test
    void deleteCustomerThrowsWhenDoesNotFound() {
        when(customerDao.existsById(anyLong()))
                .thenReturn(false);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> customerService.deleteCustomer(customer.getId()));

        assertThat(notFoundException.getMessage())
                .isEqualTo(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(customer.getId()));

        verify(customerDao).existsById(anyLong());
        verify(customerDao, never()).deleteById(anyLong());
    }

    @Test
    void updateCustomer() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(FAKER.name().fullName());
        customerUpdateRequest.setEmail(FAKER.internet().safeEmailAddress());
        customerUpdateRequest.setAge(new Random().nextInt(16, 99));

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));
        when(customerDao.updateCustomer(any()))
                .thenReturn(new Customer(1L,
                        customerUpdateRequest.getName(),
                        customerUpdateRequest.getEmail(),
                        customerUpdateRequest.getAge()));

        Customer updatedCustomer = customerService.updateCustomer(customer.getId(), customerUpdateRequest);
        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertAll(
                () -> assertThat(updatedCustomer.getName()).isEqualTo(customerArgumentCaptorValue.getName()),
                () -> assertThat(updatedCustomer.getEmail()).isEqualTo(customerArgumentCaptorValue.getEmail()),
                () -> assertThat(updatedCustomer.getAge()).isEqualTo(customerArgumentCaptorValue.getAge())
        );
    }

    @Test
    void updateCustomerName() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(FAKER.name().fullName());
        customerUpdateRequest.setEmail(null);
        customerUpdateRequest.setAge(null);

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));
        when(customerDao.updateCustomer(any()))
                .thenReturn(new Customer(1L,
                        customerUpdateRequest.getName(),
                        customer.getEmail(),
                        customer.getAge()));

        Customer updatedCustomer = customerService.updateCustomer(customer.getId(), customerUpdateRequest);

        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertAll(
                () -> assertThat(updatedCustomer.getName()).isEqualTo(customerArgumentCaptorValue.getName()),
                () -> assertThat(updatedCustomer.getEmail()).isEqualTo(customerArgumentCaptorValue.getEmail()),
                () -> assertThat(updatedCustomer.getAge()).isEqualTo(customerArgumentCaptorValue.getAge())
        );
    }

    @Test
    void updateCustomerEmail() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(null);
        customerUpdateRequest.setEmail(FAKER.internet().safeEmailAddress());
        customerUpdateRequest.setAge(null);

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));
        when(customerDao.updateCustomer(any()))
                .thenReturn(new Customer(1L,
                        customer.getName(),
                        customerUpdateRequest.getEmail(),
                        customer.getAge()));

        Customer updatedCustomer = customerService.updateCustomer(customer.getId(), customerUpdateRequest);

        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertAll(
                () -> assertThat(updatedCustomer.getName()).isEqualTo(customerArgumentCaptorValue.getName()),
                () -> assertThat(updatedCustomer.getEmail()).isEqualTo(customerArgumentCaptorValue.getEmail()),
                () -> assertThat(updatedCustomer.getAge()).isEqualTo(customerArgumentCaptorValue.getAge())
        );
    }

    @Test
    void updateCustomerAge() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(null);
        customerUpdateRequest.setEmail(null);
        customerUpdateRequest.setAge(new Random().nextInt(16, 99));

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));
        when(customerDao.updateCustomer(any()))
                .thenReturn(new Customer(1L,
                        customer.getName(),
                        customer.getEmail(),
                        customerUpdateRequest.getAge()));

        Customer updatedCustomer = customerService.updateCustomer(customer.getId(), customerUpdateRequest);

        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertAll(
                () -> assertThat(updatedCustomer.getName()).isEqualTo(customerArgumentCaptorValue.getName()),
                () -> assertThat(updatedCustomer.getEmail()).isEqualTo(customerArgumentCaptorValue.getEmail()),
                () -> assertThat(updatedCustomer.getAge()).isEqualTo(customerArgumentCaptorValue.getAge())
        );
    }

    @Test
    void updateCustomerThrowsWhenDoesNotFound() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(FAKER.name().fullName());
        customerUpdateRequest.setEmail(FAKER.internet().safeEmailAddress());
        customerUpdateRequest.setAge(new Random().nextInt(16, 99));

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> customerService.updateCustomer(customer.getId(), customerUpdateRequest));

        assertThat(notFoundException.getMessage())
                .isEqualTo(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(customer.getId()));

        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomerThrowsWhenNoChanges() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(customer.getName());
        customerUpdateRequest.setEmail(customer.getEmail());
        customerUpdateRequest.setAge(customer.getAge());
        ;

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));

        RequestValidationException requestValidationException = assertThrows(RequestValidationException.class,
                () -> customerService.updateCustomer(customer.getId(), customerUpdateRequest));

        assertThat(requestValidationException.getMessage())
                .isEqualTo(NO_CHANGES_FOUND);

        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomerThrowsWhenDuplicateEmail() {
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setName(FAKER.name().fullName());
        customerUpdateRequest.setEmail(FAKER.internet().safeEmailAddress());
        customerUpdateRequest.setAge(new Random().nextInt(16, 99));

        when(customerDao.findCustomerById(anyLong()))
                .thenReturn(Optional.of(customer));
        when(customerDao.existsByEmail(anyString()))
                .thenReturn(true);

        DuplicateResourceException duplicateResourceException = assertThrows(DuplicateResourceException.class,
                () -> customerService.updateCustomer(customer.getId(), customerUpdateRequest));

        assertThat(duplicateResourceException.getMessage())
                .isEqualTo(EMAIL_DUPLICATE_ERROR.formatted(customerUpdateRequest.getEmail()));

        verify(customerDao).findCustomerById(anyLong());
        verify(customerDao).existsByEmail(anyString());
        verify(customerDao, never()).updateCustomer(any());
    }
}