package com.enotes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enotes.dto.CategoryDto;
import com.enotes.dto.CategoryResponse;
import com.enotes.entity.Category;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.service.CategoryService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;

	@PostMapping("/save")
	public ResponseEntity<?> saveCategory(@RequestBody CategoryDto categoryDto	){
		Boolean saveCategory = categoryService.saveCategory(categoryDto);
		if(saveCategory) {
			return new ResponseEntity<>("saved successfully", HttpStatus.CREATED);
		}else {
			return new ResponseEntity<>("Not saved successfully", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/category")
	public ResponseEntity<?> getAllCategory(){
		 List<CategoryDto> allCategory = categoryService.getAllCategory();
		if(CollectionUtils.isEmpty(allCategory)) {
			return ResponseEntity.noContent().build();
		}else {
			return new ResponseEntity<>(allCategory, HttpStatus.OK);
		}
	}
	
	@GetMapping("/active-category")
	public ResponseEntity<?> getActiveCategory(){
		 List<CategoryResponse> activeCategory = categoryService.getActiveCategory();
		if(CollectionUtils.isEmpty(activeCategory)) {
			return ResponseEntity.noContent().build();
		}else {
			return new ResponseEntity<>(activeCategory, HttpStatus.OK);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getCategoryDetailsById(@PathVariable Integer id) throws Exception{
//		try {
//			CategoryDto categoryDto = categoryService.getCategoryById(id);
//			if(ObjectUtils.isEmpty(categoryDto)) {
//				return new ResponseEntity<>("Category not found By Id", HttpStatus.NOT_FOUND);
//			}else {
//				return new ResponseEntity<>(categoryDto, HttpStatus.OK);
//			}
//		} catch (ResourceNotFoundException e) {
//			log.error("Controller :: getCategoryDetailsById ::", e.getMessage());
//			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
//		} catch (Exception e) {
//			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		
		CategoryDto categoryDto = categoryService.getCategoryById(id); // Exception call handler
		if(ObjectUtils.isEmpty(categoryDto)) {
			return new ResponseEntity<>("Internal Server Error", HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(categoryDto, HttpStatus.OK);
		}
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id){
		Boolean deleted = categoryService.deleteCategoryById(id);
		if(deleted) {
			return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Category not found.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
