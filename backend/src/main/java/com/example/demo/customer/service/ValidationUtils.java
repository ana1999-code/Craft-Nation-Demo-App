package com.example.demo.customer.service;

public interface ValidationUtils {
    String CUSTOMER_NOT_FOUND_EXCEPTION_MESSAGE = "Customer with id = [%s] not found";
    String EMAIL_DUPLICATE_ERROR = "Customer with email = [%s] already exists";
    String NO_CHANGES_FOUND = "No changes found";
}
