package com.enotes.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.enotes.dto.CategoryDto;
import com.enotes.entity.Category;
import com.enotes.service.CategoryService;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

	@Mock
	private CategoryService categoryService;

	@InjectMocks
	private CategoryController categoryController;

	private CategoryDto categoryDto = null;

	private Category category = null;

	private List<Category> categories = new ArrayList<>();
	private List<CategoryDto> categoriesDto = new ArrayList<>();

	@BeforeEach
	public void initialize() {
		categoryDto = CategoryDto.builder().id(null).name("Java notes").description("This is java notes.")
				.isActive(true).build();

		category = Category.builder().id(null).name("Java Notes").description("java notes").isActive(true)
				.isDeleted(false).build();

		categories.add(category);
		categoriesDto.add(categoryDto);
	}
	
	@Test
	public void testSaveCategory() {
		when(categoryService.saveCategory(categoryDto)).thenReturn(true);
		ResponseEntity<?> response = categoryController.saveCategory(categoryDto);
		Object body = response.getBody();
		Map<String, String> json = (Map<String, String>)body;
		
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		Assertions.assertEquals(json.get("status"), "success");
	}
	
	@Test
	public void testNotSaveCategory() {
		when(categoryService.saveCategory(categoryDto)).thenReturn(false);
		ResponseEntity<?> response = categoryController.saveCategory(categoryDto);
		Object body = response.getBody();
		Map<String, String> json = (Map<String, String>)body;
		
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		Assertions.assertEquals(json.get("status"), "failed");
		Assertions.assertEquals(json.get("message"), "Category not saved.");
	}
}
