package com.brunomb.spo.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @RequestMapping()
    public Page<Customer> findAll(
            @PageableDefault(size = 40) Pageable pageable
    ) {
        return repository.findAll(pageable);
    }

    @RequestMapping("/{id}")
    public Customer findById(@PathVariable("id") Long id) {
        return repository.findById(id).get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Customer save(@Valid @RequestBody Customer c) {
        return this.repository.save(c);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
}