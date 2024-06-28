package com.example.demo.customer.controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(features = "src/test/resources/features",
        glue = "com.example.demo.customer.controller.steps",
        plugin = {"pretty"})
@RunWith(Cucumber.class)
public class CustomerIntegrationTest {

}
