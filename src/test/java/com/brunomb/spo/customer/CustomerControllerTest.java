package com.brunomb.spo.customer;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.brunomb.spo.AbstractTest;
import com.brunomb.spo.Application;
import com.brunomb.spo.ApplicationConfig;
import com.brunomb.spo.UserDetailsMock;
import com.brunomb.spo.security.WebSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerController.class)
public class CustomerControllerTest extends AbstractTest {

    @MockBean
    private CustomerRepository repository;

    @Test
    @WithMockUser()
    public void shouldReturnAllCustomers() throws Exception {
        Pageable pageable = Mockito.any(Pageable.class);
        List<Customer> result = Arrays.asList(new Customer("John", "Doe"));

        when(repository.findAll(pageable))
                .thenReturn(new PageImpl(result));

        this.mockMvc.perform(get("/customer").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content[0].firstName").value("John"));
    }

    @Test
    @WithMockUser()
    public void shouldReturnCustomerById() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Customer("John", "Doe")));

        this.mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("firstName").value("John"))
                .andExpect(jsonPath("lastName").value("Doe"));
    }

    @Test
    @WithMockUser()
    public void shouldSaveACustomer() throws Exception {
        Customer savedCustomer = new Customer("John", "Doe");
        savedCustomer.setId(1L);
        when(repository.save(Mockito.any(Customer.class))).thenReturn(savedCustomer);

        Customer reqCustomer = new Customer("John", "Doe");

        this.mockMvc.perform(
                post("/customer")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(reqCustomer))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("firstName").value("John"))
                .andExpect(jsonPath("lastName").value("Doe"));
    }

    @Test
    @WithMockUser()
    public void shouldFailToSaveACustomerWithoutName() throws Exception {
        Customer reqCustomer = new Customer("", "");

        String message = this.mockMvc.perform(
                post("/customer")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(reqCustomer))
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();

        assertThat(message, containsString("firstName"));
        assertThat(message, containsString("lastName"));
    }

    @Test
    @WithMockUser()
    public void shouldDeleteACustomer() throws Exception {
        CustomerRepository spy = Mockito.spy(repository);
        Mockito.doNothing().when(spy).deleteById(1L);

        this.mockMvc.perform(delete("/customer/1"))
                .andExpect(status().isOk());
    }
}