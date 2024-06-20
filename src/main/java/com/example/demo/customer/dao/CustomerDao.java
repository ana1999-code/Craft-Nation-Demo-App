package com.example.demo.customer.dao;

import com.example.demo.customer.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> findAllCustomers();

    Optional<Customer> findCustomerById(Long id);

    boolean existsByEmail(String email);

    Customer save(Customer newCustomer);

    boolean existsById(Long id);

    void deleteById(Long id);

    Customer updateCustomer(Customer customer);
}
