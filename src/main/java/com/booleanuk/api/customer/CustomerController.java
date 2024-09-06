package com.booleanuk.api.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

    private CustomerRepository customerRepository;

    public CustomerController() throws SQLException {
        this.customerRepository = new CustomerRepository();
    }

    @GetMapping
    public List<Customer> getAll() throws SQLException {
        return customerRepository.getAll();
    }

    @GetMapping("{id}")
    public Customer getOne(@PathVariable int id) throws SQLException {
        Customer customer = this.customerRepository.getOne(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nody with that id here");
        }
        return customer;
    }

}
