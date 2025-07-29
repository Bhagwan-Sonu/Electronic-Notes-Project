package com.enotes.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enotes.dto.PasswordChangeRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "All the User APIs")
@RequestMapping("/api/v1/user")
public interface UserEndpoint {

	@Operation(summary = "Get User profile", tags = {"Todo"}, description = "Get User profile")
	@GetMapping("/profile")
	public ResponseEntity<?> getProfile();
	
	@Operation(summary = "User password change", tags = {"Todo"}, description = "Get User password change")
	@PostMapping("/change-pswd")
	public ResponseEntity<?> changepassword(@RequestBody PasswordChangeRequest passwordRequest);
}
