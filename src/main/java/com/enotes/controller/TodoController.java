package com.enotes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enotes.dto.TodoDto;
import com.enotes.service.TodoService;
import com.enotes.util.CommonUtil;

@RestController
@RequestMapping("api/v1/todo")
public class TodoController {

	@Autowired
	private TodoService todoService;
	
	@PostMapping("/")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> saveTodo(@RequestBody TodoDto todoDto) throws Exception{
		Boolean saveTodo = todoService.saveTodo(todoDto);
		if(saveTodo) {
			return CommonUtil.createBuildResponseMessage("To do saved successfully,", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Todo not saved", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getTodoById(@PathVariable Integer id) throws Exception{
		TodoDto todoDto= todoService.getTodoById(id);
		return CommonUtil.createBuildResponse(todoDto, HttpStatus.OK);
	}
	
	@GetMapping("/list")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getAllTodoByUser(){
		List<TodoDto> todo = todoService.getTodoByUser();
		if(CollectionUtils.isEmpty(todo)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(todo, HttpStatus.OK);
	}
	
	
	
	
}
