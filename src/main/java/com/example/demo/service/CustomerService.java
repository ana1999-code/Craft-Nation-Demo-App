package com.example.demo.service;

import com.example.demo.dao.CustomerDao;
import com.example.demo.entity.Customer;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.RequestValidationException;
import com.example.demo.model.CustomerRegistrationRequest;
import com.example.demo.model.CustomerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.service.ValidationUtils.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.example.demo.service.ValidationUtils.EMAIL_DUPLICATE_ERROR;
import static com.example.demo.service.ValidationUtils.NO_CHANGES_FOUND;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    public List<Customer> findAllCustomers() {
        return customerDao.findAllCustomers();
    }

    public Customer findCustomerById(Integer id) {
        return customerDao.findCustomerById(id)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE
                        .formatted(id)));
    }

    public Customer saveCustomer(CustomerRegistrationRequest customer) {
        if (customerDao.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException(EMAIL_DUPLICATE_ERROR
                    .formatted(customer.getEmail()));
        }

        Customer newCustomer = new Customer();
        newCustomer.setName(customer.getName());
        newCustomer.setEmail(customer.getEmail());

        if (customer.getAge() != null) {
            newCustomer.setAge(customer.getAge());
        }

        return customerDao.save(newCustomer);
    }

    public void deleteCustomer(Integer id) {
        if (!customerDao.existsById(id)) {
            throw new NotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE
                    .formatted(id));
        }

        customerDao.deleteById(id);
    }

    public Customer updateCustomer(Integer id, CustomerUpdateRequest customer) {
        boolean changes = false;
        Customer customerToUpdate = customerDao.findCustomerById(id)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE
                        .formatted(id)));

        if (customer.getName() != null && !customerToUpdate.getName().equals(customer.getName())) {
            customerToUpdate.setName(customer.getName());
            changes = true;
        }

        if (customer.getAge() != null && !customerToUpdate.getAge().equals(customer.getAge())) {
            customerToUpdate.setAge(customer.getAge());
            changes = true;
        }

        if (customer.getEmail() != null && !customer.getEmail().equals(customer.getEmail())) {
            if (customerDao.existsByEmail(customer.getEmail())) {
                throw new DuplicateResourceException(EMAIL_DUPLICATE_ERROR
                        .formatted(customer.getEmail()));
            }
            customerToUpdate.setEmail(customer.getEmail());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException(NO_CHANGES_FOUND);
        }
        return customerDao.save(customerToUpdate);
    }
}
