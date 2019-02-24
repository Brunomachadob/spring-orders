package com.brunomb.spo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping()
    public Page<User> findAll(
            @PageableDefault(size = 40) Pageable pageable
    ) {
        return repository.findAll(pageable);
    }

    @RequestMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        return repository.findById(id).get();
    }

    @RequestMapping(method = RequestMethod.POST)
    public User save(@Valid @RequestBody User user) {

        if (user.getId() == null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        return this.repository.save(user);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }
}