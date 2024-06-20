package com.example.demo.customer.controller;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.model.CustomerRegistrationRequest;
import com.example.demo.customer.model.CustomerUpdateRequest;
import com.example.demo.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id")Integer id){
        Customer customer = customerService.findCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<Customer> registerCustomer(@RequestBody @Valid CustomerRegistrationRequest customer){
        Customer savedCustomer = customerService.saveCustomer(customer);
        return ResponseEntity.created(URI.create(savedCustomer.getId().toString()))
                .body(savedCustomer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCustomer(@PathVariable("id") Integer id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") Integer id,
            @RequestBody CustomerUpdateRequest customer){
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(updatedCustomer);
    }
}
