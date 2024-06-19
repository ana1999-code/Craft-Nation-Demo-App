package com.example.demo.service;

import com.example.demo.dao.CustomerDao;
import com.example.demo.entity.Customer;
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
}
