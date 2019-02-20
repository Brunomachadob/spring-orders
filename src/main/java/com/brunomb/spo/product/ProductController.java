package com.brunomb.spo.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @RequestMapping()
    public Page<Product> findAll(
            @PageableDefault(size = 40) Pageable pageable
    ) {
        return repository.findAll(pageable);
    }

    @RequestMapping("/{id}")
    public Product findById(@PathVariable("id") Long id) {
        return repository.findById(id).get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Product save(@Valid @RequestBody Product c) {
        return this.repository.save(c);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
}