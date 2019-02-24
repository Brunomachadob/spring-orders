package com.brunomb.spo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Configuration
@Getter
public class ApplicationConfig {
    @Autowired
    private JWT jwt;

    @Autowired
    private Auth auth;

    @Value("${app.test}")
    private boolean test;

    @Configuration
    @Getter
    public static class Auth {
        @NotNull
        @Value("${app.auth.sign-up-url}")
        private String signUpUrl;

        @NotNull
        @Value("${app.auth.login-url}")
        private String loginUrl;
    }

    @Component
    @Getter
    public static class JWT {
        @NotNull
        @Value("${app.jwt.key}")
        private String key;

        @NotNull
        @Value("${app.jwt.expiration}")
        private long expiration;

        @NotNull
        @Value("${app.jwt.token-prefix}")
        private String tokenPrefix;

        @NotNull
        @Value("${app.jwt.header}")
        private String header;
    }
}
