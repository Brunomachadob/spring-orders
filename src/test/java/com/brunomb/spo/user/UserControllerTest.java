package com.brunomb.spo.user;

import com.brunomb.spo.AbstractTest;
import com.brunomb.spo.customer.Customer;
import com.brunomb.spo.customer.CustomerController;
import com.brunomb.spo.customer.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTest extends AbstractTest {

    @MockBean
    private UserRepository repository;

    @Test
    @WithMockUser
    public void shouldReturnAllCustomers() throws Exception {
        Pageable pageable = Mockito.any(Pageable.class);
        List<User> result = Arrays.asList(new User("user", "pass"));

        when(repository.findAll(pageable))
                .thenReturn(new PageImpl(result));

        this.mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content[0].username").value("user"));
    }

    @Test
    @WithMockUser
    public void shouldReturnCustomerById() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new User("user", "pass")));

        this.mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("user"))
                .andExpect(jsonPath("password").value("pass"));
    }

    @Test
    @WithMockUser
    public void shouldSaveACustomer() throws Exception {
        User savedUser = new User("user", "pass");
        savedUser.setId(1L);
        when(repository.save(Mockito.any(User.class))).thenReturn(savedUser);

        User reqCustomer = new User("user", "pass");

        this.mockMvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(reqCustomer))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("username").value("user"))
                .andExpect(jsonPath("password").value("pass"));
    }

    @Test
    @WithMockUser
    public void shouldFailToSaveACustomerWithoutName() throws Exception {
        User reqCustomer = new User("", "");

        String message = this.mockMvc.perform(
                post("/user")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(reqCustomer))
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();

        assertThat(message, containsString("username"));
        assertThat(message, containsString("password"));
    }

    @Test
    @WithMockUser
    public void shouldDeleteACustomer() throws Exception {
        UserRepository spy = Mockito.spy(repository);
        Mockito.doNothing().when(spy).deleteById(1L);

        this.mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk());
    }
}