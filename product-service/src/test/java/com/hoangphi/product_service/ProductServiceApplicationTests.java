package com.hoangphi.product_service;

import com.hoangphi.product_service.dto.ProductRequest;
import com.hoangphi.product_service.dto.ProductResponse;
import com.hoangphi.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;



	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString=objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
    		.contentType(MediaType.APPLICATION_JSON)
    		.content(productRequestString))
    		.andExpect(status().isCreated())
    		.andReturn();

        Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetProductById() throws Exception {
    // Create a product and get its ID
    ProductRequest productRequest = getProductRequest();
    String productRequestString = objectMapper.writeValueAsString(productRequest);
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
            .contentType(MediaType.APPLICATION_JSON)
            .content(productRequestString))
            .andExpect(status().isCreated())
			.andReturn();

    String responseString = result.getResponse().getContentAsString();
    ProductResponse productResponse = objectMapper.readValue(responseString, ProductResponse.class);
    String productId = productResponse.getId();

    // Use the productId to perform the GET request
    mockMvc.perform(MockMvcRequestBuilders.get("/api/product/" + productId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(productId))
            .andExpect(jsonPath("$.name").value(productRequest.getName()))
            .andExpect(jsonPath("$.description").value(productRequest.getDescription()))
            .andExpect(jsonPath("$.price").value(productRequest.getPrice()));
}

	private ProductRequest getProductRequest(){
		return ProductRequest.builder()
				.name("Iphone 12")
				.description("New Iphone")
				.price(BigDecimal.valueOf(1200))
				.build();
	}


}
