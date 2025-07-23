package com.enotes.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.enotes.dto.CategoryDto;
import com.enotes.dto.TodoDto;
import com.enotes.dto.TodoDto.StatusDto;
import com.enotes.dto.UserDto;
import com.enotes.entity.Role;
import com.enotes.enums.TodoStatus;
import com.enotes.exception.ExistDataException;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.repository.RoleRepository;
import com.enotes.repository.UserRepository;
import com.mysql.cj.util.StringUtils;

import jakarta.validation.ValidationException;

@Component
public class Validation {
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	UserRepository userRepo;
	
	public void categoryValidation(CategoryDto categoryDto) {
		
		Map<String, Object> error = new LinkedHashMap<>();
		
		if(ObjectUtils.isEmpty(categoryDto)) {
			throw new IllegalArgumentException("Category object should not be null or empty");
		}else {
			
			// Validation name field
			if(ObjectUtils.isEmpty(categoryDto.getName())) {
				error.put("name", "name field is empty or null");
			}else {
				if(categoryDto.getName().length()<3) {
					error.put("name", "name length must be 3");
				}
				if(categoryDto.getName().length()>100) {
					error.put("name", "name length less than 10");
				}
			}
		}
		//Validation description
		if(ObjectUtils.isEmpty(categoryDto.getDescription())) {
			error.put("description", "description field is empty or null");
		}
		
		//Validation isActive
		if(ObjectUtils.isEmpty(categoryDto.getIsActive())) {
			error.put("isActive", "isActive field is empty or null");
		}else {
			if(categoryDto.getIsActive()!=Boolean.TRUE.booleanValue()
					&& categoryDto.getIsActive()!=Boolean.FALSE.booleanValue()) {
				error.put("isActive", "isActive field has invalid value");

			}
		}
		
		if(!error.isEmpty()) {
			throw new com.enotes.exception.ValidationException(error);
		}
	}
	
	public void todoValidation(TodoDto todo) throws Exception {
		
		StatusDto reqStatus = todo.getStatus();
		Boolean statusFound = false;
		for(TodoStatus st : TodoStatus.values()) {
			if(st.getId().equals(reqStatus.getId())) {
				statusFound = true;
			}
		}
		if(!statusFound) {
			throw new ResourceNotFoundException("Invalid status");
		}
	}
	
	public void userValidation(UserDto userDto) {
		
		if(!org.springframework.util.StringUtils.hasText(userDto.getFirstName())) {
			throw new IllegalArgumentException("First name is invalid.");
		}
		if(!org.springframework.util.StringUtils.hasText(userDto.getLastName())) {
			throw new IllegalArgumentException("Last name is invalid.");
		}
		if(!org.springframework.util.StringUtils.hasText(userDto.getEmail())
				|| !userDto.getEmail().matches(Constants.EMAIL_REGEX)) {
			throw new IllegalArgumentException("Email is invalid.");
		}else {
			// validate email exists
			Boolean existsByEmail = userRepo.existsByEmail(userDto.getEmail());
			if(existsByEmail) {
				throw new ExistDataException("Email already exist in the database.");
			}
		}
		if(!org.springframework.util.StringUtils.hasText(userDto.getMobNo())
				|| !userDto.getMobNo().matches(Constants.Mobile_No)) {
			throw new IllegalArgumentException("Mobile Number is invalid.");
		}
		
		if(CollectionUtils.isEmpty(userDto.getRoles())) {
			throw new IllegalArgumentException("Role is Invalid");
		}else {
			List<Integer> rolesIds = roleRepo.findAll()
					.stream().map(r -> r.getId()).toList();
			
			List<Integer> invalidRequestRoleId = userDto.getRoles().stream()
					.map(r->r.getId())
					.filter(roleId -> rolesIds.contains(roleId)).toList();
			
			if(CollectionUtils.isEmpty(invalidRequestRoleId)) {
				throw new IllegalArgumentException("Role is invalid"+invalidRequestRoleId);
			}
		}
	}
	
	
	
}
