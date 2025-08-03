package com.enotes.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.enotes.dto.CategoryDto;
import com.enotes.dto.LoginRequest;
import com.enotes.entity.Category;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	private CategoryDto categoryDto = null;

	private Category category = null;
	
	private final String token = null;

	@BeforeEach
	public void initialize() {
		categoryDto = CategoryDto.builder().id(null).name("Java notes").description("This is java notes.")
				.isActive(true).build();

		category = Category.builder().id(null).name("Java Notes").description("java notes").isActive(true)
				.isDeleted(false).build();

	}
	
	@Test
	public void testSaveCategory() throws JsonProcessingException, Exception {
		String token =generateToken("sonubsacexyz@gmail.com","123");
		mockMvc.perform(post("/api/v1/category/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryDto))
				.header("Authorization", token))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.message").value("saved successfully."))
		.andExpect(jsonPath("$.status").value("success"));
	}
	
	public String generateToken(String email, String password) throws JsonProcessingException, UnsupportedEncodingException, Exception {
		LoginRequest login = new LoginRequest();
		login.setEmail(email);
		login.setPassword(password);
		
		String response = mockMvc.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(login))
				).andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		JsonNode root = objectMapper.readTree(response);
		String token = root.path("data").path("token").asText();
		return "Bearer "+token;
	}
	
	
	
}
