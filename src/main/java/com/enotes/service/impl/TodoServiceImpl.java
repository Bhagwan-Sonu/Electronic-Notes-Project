package com.enotes.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.enotes.dto.TodoDto;
import com.enotes.dto.TodoDto.StatusDto;
import com.enotes.entity.Todo;
import com.enotes.enums.TodoStatus;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.repository.TodoRepository;
import com.enotes.service.TodoService;
import com.enotes.util.Validation;

@Service
public class TodoServiceImpl implements TodoService {

	@Autowired
	private TodoRepository todoRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private Validation validation;
	
	@Override
	public Boolean saveTodo(TodoDto todoDto) throws Exception {
		
		//validate Todo status
		validation.todoValidation(todoDto);
		
		Todo todo = mapper.map(todoDto, Todo.class);
		todo.setStatusId(todoDto.getStatus().getId());
		Todo saveTodo = todoRepo.save(todo);
		if(!ObjectUtils.isEmpty(saveTodo)) {
			return true;
		}
		return false;
	}

	@Override
	public TodoDto getTodoById(Integer id) throws Exception {
		Todo todo = todoRepo.findById(id)
				.orElseThrow(() ->new ResourceNotFoundException("Todo Not Found! Id invalid"));
		TodoDto todoDto = mapper.map(todo, TodoDto.class);
		setStatus(todoDto, todo);
		return todoDto;
	}
	
	private void setStatus(TodoDto todoDto, Todo todo) {

		for(TodoStatus st : TodoStatus.values()) {
			if(st.getId().equals(todo.getStatusId())) {
				StatusDto statusDto = StatusDto.builder()
						.id(st.getId())
						.name(st.name())
						.build();
				todoDto.setStatus(statusDto);
			}
		}
	}

	@Override
	public List<TodoDto> getTodoByUser() {
		Integer userId =1;
		List<Todo> todos = todoRepo.findByCreatedBy(userId);
		return todos.stream().map(td -> mapper.map(td, TodoDto.class)).toList();
	}

	
	
}
