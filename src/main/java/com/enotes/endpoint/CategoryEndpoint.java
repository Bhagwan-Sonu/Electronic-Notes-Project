package com.enotes.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static com.enotes.util.Constants.ROLE_ADMIN;
import static com.enotes.util.Constants.ROLE_ADMIN_USER;
import static com.enotes.util.Constants.ROLE_USER;


import com.enotes.dto.CategoryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category", description = "All the category operation APIs")
@RequestMapping("/api/v1/category")
public interface CategoryEndpoint {

	@Operation(summary = "Save Category", tags = {"Category"}, description = "Admin save")
	@PostMapping("/save")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> saveCategory(@RequestBody CategoryDto categoryDto	);
	
	@Operation(summary = "Get all category", tags = {"Category"}, description = "Admin get All category")
	@GetMapping("/")
	@PreAuthorize(ROLE_ADMIN_USER)
	public ResponseEntity<?> getAllCategory();
	
	@Operation(summary = "Get active category", tags = {"Category"}, description = "User get active category")
	@GetMapping("/active")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getActiveCategory();
	
	@Operation(summary = "Get category details", tags = {"Category"}, description = "Admin get category details By ID")
	@GetMapping("/{id}")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> getCategoryDetailsById(@PathVariable Integer id) throws Exception;
	
	@Operation(summary = "Delete category", tags = {"Category"}, description = "Admin delete category")
	@DeleteMapping("/{id}")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> deleteCategoryById(@PathVariable Integer id);
}
