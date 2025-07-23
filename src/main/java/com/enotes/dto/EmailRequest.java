package com.enotes.dto;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Service
@Builder
public class EmailRequest {

	private String to;
	
	private String subject;
	
	private String title;
	
	private String message;
	
	
}
