package com.example.demo;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository){
		return args -> {
			Faker faker = new Faker();

			Customer customer = new Customer();
			customer.setName(faker.name().fullName());
			customer.setEmail(faker.internet().safeEmailAddress());
			customer.setAge(new Random().nextInt(16, 99));

			customerRepository.save(customer);
		};
	}
}
