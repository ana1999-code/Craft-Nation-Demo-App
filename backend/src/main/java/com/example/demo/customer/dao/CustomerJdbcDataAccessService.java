package com.example.demo.customer.dao;

import com.example.demo.customer.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("jdbc")
@RequiredArgsConstructor
public class CustomerJdbcDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Customer> findAllCustomers() {
        String sql = "SELECT * FROM CUSTOMER";
        RowMapper<Customer> rowMapper = new BeanPropertyRowMapper<>(Customer.class);

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Customer> findCustomerById(Long id) {
        String sql = "SELECT * FROM CUSTOMER WHERE ID = ?";
        RowMapper<Customer> rowMapper = new BeanPropertyRowMapper<>(Customer.class);

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT * FROM CUSTOMER WHERE EMAIL = ?";
        RowMapper<Customer> rowMapper = new BeanPropertyRowMapper<>(Customer.class);

        return !jdbcTemplate.query(sql, rowMapper, email).isEmpty();
    }

    @Override
    public Customer save(Customer newCustomer) {
        String sql = "INSERT INTO CUSTOMER(name, email, age)" +
                     "VALUES(?, ? ,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Customer addedCustomer = new Customer();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, newCustomer.getName());
            preparedStatement.setString(2, newCustomer.getEmail());
            preparedStatement.setInt(3, newCustomer.getAge());

            return preparedStatement;
        }, keyHolder);

        addedCustomer.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
        addedCustomer.setName((String) keyHolder.getKeys().get("name"));
        addedCustomer.setEmail((String) keyHolder.getKeys().get("email"));
        addedCustomer.setAge((Integer) keyHolder.getKeys().get("age"));

        return addedCustomer;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT * FROM CUSTOMER WHERE ID = ?";
        RowMapper<Customer> rowMapper = new BeanPropertyRowMapper<>(Customer.class);

        return !jdbcTemplate.query(sql, rowMapper, id).isEmpty();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM CUSTOMER WHERE ID = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if (customer.getName() != null) {
            String sql = "UPDATE CUSTOMER SET NAME = ? WHERE ID = ?";
            jdbcTemplate.update(sql, customer.getName(), customer.getId());
        }

        if (customer.getEmail() != null) {
            String sql = "UPDATE CUSTOMER SET EMAIL = ? WHERE ID = ?";
            jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
        }

        if (customer.getAge() != null) {
            String sql = "UPDATE CUSTOMER SET AGE = ? WHERE ID = ?";
            jdbcTemplate.update(sql, customer.getAge(), customer.getId());
        }

        return customer;
    }
}
