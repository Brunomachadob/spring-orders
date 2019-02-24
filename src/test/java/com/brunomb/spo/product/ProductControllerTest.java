package com.brunomb.spo.product;

import com.brunomb.spo.AbstractTest;
import com.brunomb.spo.customer.Customer;
import com.brunomb.spo.customer.CustomerController;
import com.brunomb.spo.customer.CustomerRepository;
import com.brunomb.spo.user.UserRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
public class ProductControllerTest extends AbstractTest {
    @MockBean
    private ProductRepository repository;

    @Test
    @WithMockUser
    public void shouldReturnAllCustomers() throws Exception {
        Pageable pageable = Mockito.any(Pageable.class);
        List<Product> result = Arrays.asList(new Product("Milk", BigDecimal.ONE));

        when(repository.findAll(pageable))
                .thenReturn(new PageImpl(result));

        this.mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content[0].name").value("Milk"))
                .andExpect(jsonPath("content[0].price").value(1));
    }

    @Test
    @WithMockUser
    public void shouldReturnCustomerById() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Product("Milk", BigDecimal.ONE)));

        this.mockMvc.perform(get("/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Milk"))
                .andExpect(jsonPath("price").value(1));
    }

    @Test
    @WithMockUser
    public void shouldSaveACustomer() throws Exception {
        Product savedProduct = new Product("Milk", BigDecimal.ONE);
        savedProduct.setId(1L);
        when(repository.save(Mockito.any(Product.class))).thenReturn(savedProduct);

        Product reqProduct = new Product("Milk", BigDecimal.ONE);

        this.mockMvc.perform(
                post("/product")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(reqProduct))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Milk"))
                .andExpect(jsonPath("price").value(1));
    }

    @Test
    @WithMockUser
    public void shouldFailToSaveACustomerWithoutName() throws Exception {
        Product reqCustomer = new Product("", BigDecimal.valueOf(-1));

        String message = this.mockMvc.perform(
                post("/product")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(reqCustomer))
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResolvedException().getMessage();

        assertThat(message, containsString("name"));
        assertThat(message, containsString("price"));
    }

    @Test
    @WithMockUser
    public void shouldDeleteACustomer() throws Exception {
        ProductRepository spy = Mockito.spy(repository);
        Mockito.doNothing().when(spy).deleteById(1L);

        this.mockMvc.perform(delete("/product/1"))
                .andExpect(status().isOk());
    }
}