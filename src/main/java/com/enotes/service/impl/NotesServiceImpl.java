package com.enotes.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.enotes.dto.NotesDto;
import com.enotes.dto.NotesDto.CategoryDto;
import com.enotes.entity.Notes;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.repository.CategoryRepository;
import com.enotes.repository.NotesRepository;
import com.enotes.service.NotesService;

@Service
public class NotesServiceImpl implements NotesService {

	@Autowired
	private NotesRepository notesRepo;

	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private CategoryRepository categoryRepo;

	@Override
	public Boolean saveNotes(NotesDto notesDto) throws Exception {
		// validation category
		checkCategoryExist(notesDto.getCategory());
		
		Notes notes = mapper.map(notesDto, Notes.class);
		Notes saveNotes = notesRepo.save(notes);
		if (!ObjectUtils.isEmpty(saveNotes)) {
			return true;
		}
		return false;
	}

	private void checkCategoryExist(CategoryDto category) throws Exception {

		categoryRepo.findById(category.getId())
		.orElseThrow(()-> new ResourceNotFoundException("Category is Invalid"));
	}

	@Override
	public List<NotesDto> getAllNotes() {
		return notesRepo.findAll().stream()
				.map(note -> mapper.map(note, NotesDto.class)).toList();
	}

}
