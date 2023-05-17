package com.booleanuk.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {
    private CustomerRepository customers;

    public CustomerController() throws SQLException {
        this.customers = new CustomerRepository();

    }

    @GetMapping
    public List<Customer> getAll() throws SQLException {
        return this.customers.getAll();
    }

    @GetMapping("/{id}")
    public Customer getOne(@PathVariable (name = "id") long id) throws SQLException {
        Customer customer = this.customers.get(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        return customer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody Customer customer) throws SQLException {
        Customer theCustomer = this.customers.add(customer);
        if (theCustomer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create the specified Customer");
        }
        return theCustomer;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer update(@PathVariable (name = "id") long id, @RequestBody Customer customer) throws SQLException {
        Customer toBeUpdated = this.customers.get(id);
        if (toBeUpdated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        return this.customers.update(id, customer);
    }

    @DeleteMapping("/{id}")
    public Customer delete(@PathVariable (name = "id") long id) throws SQLException {
        Customer toBeDeleted = this.customers.get(id);
        if (toBeDeleted == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        return this.customers.delete(id);
    }
}
