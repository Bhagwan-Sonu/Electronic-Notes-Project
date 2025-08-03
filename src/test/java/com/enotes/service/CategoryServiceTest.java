package com.enotes.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.enotes.dto.CategoryDto;
import com.enotes.entity.Category;
import com.enotes.exception.ExistDataException;
import com.enotes.repository.CategoryRepository;
import com.enotes.service.impl.CategoryServiceImpl;
import com.enotes.util.Validation;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryrepo;
	
	@InjectMocks
	private CategoryServiceImpl categoryService;
	
	@Mock
	private ModelMapper mapper;
	
	@Mock
	private Validation validation;
	
	private CategoryDto categoryDto = null;
	
	private Category category = null;
	
	private List<Category> categories = new ArrayList<>();
	private List<CategoryDto> categoriesDto = new ArrayList<>();
	
	@BeforeEach
	public void initialize() {
		categoryDto = CategoryDto.builder()
				.id(null)
				.name("Java notes")
				.description("This is java notes.")
				.isActive(true).build();
		
		category=Category.builder()
				.id(null)
				.name("Java Notes")
				.description("java notes")
				.isActive(true)
				.isDeleted(false)
				.build();
		
		categories.add(category);
		categoriesDto.add(categoryDto); 
	}
	
	@Test
	public void testSaveCategory() {
		//arrange
		when(categoryrepo.existsByName(categoryDto.getName())).thenReturn(false);
		when(mapper.map(categoryDto, Category.class)).thenReturn(category);
		when(categoryrepo.save(category)).thenReturn(category);
		
		//act
		Boolean saveCategory = categoryService.saveCategory(categoryDto);
		
		//assert
		assertTrue(saveCategory);
		
		//verify
		verify(validation).categoryValidation(categoryDto);
		verify(categoryrepo).existsByName(categoryDto.getName());
		verify(categoryrepo).save(category);
	}
	
	@Test
	public void testCategoryExist() {
		when(categoryrepo.existsByName(categoryDto.getName())).thenReturn(true);
		ExistDataException exception = assertThrows(ExistDataException.class, ()->{
			categoryService.saveCategory(categoryDto);
		});
		assertEquals("Category already exist", exception.getMessage());
		verify(validation).categoryValidation(categoryDto);
		verify(categoryrepo.existsByName(categoryDto.getName()));
		verify(categoryrepo,never()).save(category);
		
	}
	
	@Test
	public void testUpdateCategory() {
		
		category.setId(1);
		categoryDto.setId(1);
		
		//arrange
		when(categoryrepo.existsByName(categoryDto.getName())).thenReturn(false);
		when(mapper.map(categoryDto, Category.class)).thenReturn(category);
		when(categoryrepo.save(category)).thenReturn(category);
		
		//act
		Boolean saveCategory = categoryService.saveCategory(categoryDto);
		
		//assert
		assertTrue(saveCategory);
		
		//verify
		verify(validation).categoryValidation(categoryDto);
		verify(categoryrepo).existsByName(categoryDto.getName());
		verify(categoryrepo).save(category);
	}
	
	@Test
	public void testGetAllCategory() {
		when(categoryrepo.findByIsDeletedFalse()).thenReturn(categories);
		List<CategoryDto> allCategory = categoryService.getAllCategory();
		
		assertEquals(allCategory.size(), categories.size());
		verify(categoryrepo).findByIsDeletedFalse();
 	}
	
}
