package com.brunomb.spo.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
@EnableSpringDataWebSupport
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CustomerRepository repository;

    @Test
    public void shouldReturnAllCustomers() throws Exception {
        Pageable pageable = Mockito.any(Pageable.class);
        List<Customer> result = Arrays.asList(new Customer("John", "Doe"));

        when(repository.findAll(pageable))
                .thenReturn(new PageImpl(result));

        this.mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content[0].firstName").value("John"));
    }

    @Test
    public void shouldReturnCustomerById() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Customer("John", "Doe")));

        this.mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("firstName").value("John"))
                .andExpect(jsonPath("lastName").value("Doe"));
    }

    @Test
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
    public void shouldDeleteACustomer() throws Exception {
        CustomerRepository spy = Mockito.spy(repository);
        Mockito.doNothing().when(spy).deleteById(1L);

        this.mockMvc.perform(delete("/customer/1"))
                .andExpect(status().isOk());
    }
}