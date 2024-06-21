package com.example.demo.customer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CustomerUpdateRequest {

    private String name;

    private String email;

    private Integer age;
}
