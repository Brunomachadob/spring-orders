package com.brunomb.spo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@TestConfiguration
public class UserDetailsMock {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User basicUser = new User("user", bCryptPasswordEncoder.encode("password"), emptyList());

        return new InMemoryUserDetailsManager(asList(
                basicUser
        ));
    }
}
