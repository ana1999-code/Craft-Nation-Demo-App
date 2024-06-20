package com.example.demo.customer.service;

import com.example.demo.customer.dao.CustomerDao;
import com.example.demo.customer.entity.Customer;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.RequestValidationException;
import com.example.demo.customer.model.CustomerRegistrationRequest;
import com.example.demo.customer.model.CustomerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;

    public List<Customer> findAllCustomers() {
        return customerDao.findAllCustomers();
    }

    public Customer findCustomerById(Integer id) {
        return customerDao.findCustomerById(id)
                .orElseThrow(() -> new NotFoundException(ValidationUtils.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE
                        .formatted(id)));
    }

    public Customer saveCustomer(CustomerRegistrationRequest customer) {
        if (customerDao.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException(ValidationUtils.EMAIL_DUPLICATE_ERROR
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
            throw new NotFoundException(ValidationUtils.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE
                    .formatted(id));
        }

        customerDao.deleteById(id);
    }

    public Customer updateCustomer(Integer id, CustomerUpdateRequest customer) {
        boolean changes = false;
        Customer customerToUpdate = customerDao.findCustomerById(id)
                .orElseThrow(() -> new NotFoundException(ValidationUtils.CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE
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
                throw new DuplicateResourceException(ValidationUtils.EMAIL_DUPLICATE_ERROR
                        .formatted(customer.getEmail()));
            }
            customerToUpdate.setEmail(customer.getEmail());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException(ValidationUtils.NO_CHANGES_FOUND);
        }
        return customerDao.save(customerToUpdate);
    }
}