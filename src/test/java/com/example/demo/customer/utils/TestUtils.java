package com.example.demo.customer.utils;

import com.example.demo.customer.entity.Customer;
import com.github.javafaker.Faker;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class TestUtils {


    public static final Faker FAKER = new Faker();

    @NotNull
    public static Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(UUID.randomUUID() + FAKER.internet().safeEmailAddress());
        customer.setAge(new Random().nextInt(16, 80));
        return customer;
    }
}
