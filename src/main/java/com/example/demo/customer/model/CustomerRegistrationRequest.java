package com.example.demo.customer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CustomerRegistrationRequest {

    @NotNull
    private String name;

    @Email
    private String email;

    private Integer age;
}
