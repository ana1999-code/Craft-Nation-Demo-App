package com.example.demo.customer.dao;

import com.example.demo.customer.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> findAllCustomers();

    Optional<Customer> findCustomerById(Integer id);

    boolean existsByEmail(String email);

    Customer save(Customer newCustomer);

    boolean existsById(Integer id);

    void deleteById(Integer id);
}