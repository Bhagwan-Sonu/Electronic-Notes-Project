package com.enotes.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.enotes.dto.CategoryDto;

import jakarta.validation.ValidationException;

@Component
public class Validation {

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
}
