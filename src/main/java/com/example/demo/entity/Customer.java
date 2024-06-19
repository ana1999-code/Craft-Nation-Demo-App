package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Customer {

    @Id
    @SequenceGenerator(name = "customer_id_seq",
    sequenceName = "customer_id_seq",
    allocationSize = 1)
    private Integer id;

    @NotNull
    private String name;

    @Email
    private String email;

    private Integer age;
}
