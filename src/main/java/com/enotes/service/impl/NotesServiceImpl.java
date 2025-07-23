package com.enotes.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.enotes.dto.FavouriteNoteDto;
import com.enotes.dto.NotesDto;
import com.enotes.dto.NotesDto.CategoryDto;
import com.enotes.dto.NotesDto.FilesDto;
import com.enotes.dto.NotesResponse;
import com.enotes.entity.FavouriteNote;
import com.enotes.entity.FileDetails;
import com.enotes.entity.Notes;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.repository.CategoryRepository;
import com.enotes.repository.FavouriteNoteRepository;
import com.enotes.repository.FileRepository;
import com.enotes.repository.NotesRepository;
import com.enotes.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotesServiceImpl implements NotesService {

	@Autowired
	private NotesRepository notesRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private FileRepository fileRepo;
	
	@Autowired
	private FavouriteNoteRepository favouriteNoteRepo;

	@Value("${file.upload.path}")
	private String uploadpath;

	@Override
	public Boolean saveNotes(String notes, MultipartFile file) throws Exception {

		ObjectMapper ob = new ObjectMapper();
		NotesDto notesDto = ob.readValue(notes, NotesDto.class);
		
		notesDto.setIsDeleted(false);
		notesDto.setDeletedOn(null);

		// update if id is given in request
		if (!ObjectUtils.isEmpty(notesDto)) {
			updateNotes(notesDto, file);
		}

		// validation category
		checkCategoryExist(notesDto.getCategory());

		Notes notesMap = mapper.map(notesDto, Notes.class);

		FileDetails fileDtls = saveFileDetails(file);

		if (!ObjectUtils.isEmpty(fileDtls)) {
			notesMap.setFileDetails(fileDtls);
		} else {
			if (ObjectUtils.isEmpty(notesDto)) {
				notesMap.setFileDetails(null);
			}
		}

		Notes saveNotes = notesRepo.save(notesMap);
		if (!ObjectUtils.isEmpty(saveNotes)) {
			return true;
		}
		return false;
	}

	private void updateNotes(NotesDto notesDto, MultipartFile file) throws Exception {

		Notes existNotes = notesRepo.findById(notesDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid notes id"));

		// user not choose any file at update time
		if (ObjectUtils.isEmpty(file)) {
			notesDto.setFileDetails(mapper.map(existNotes.getFileDetails(), FilesDto.class));
		}
	}

	private FileDetails saveFileDetails(MultipartFile file) throws IOException {

		if (!ObjectUtils.isEmpty(file) && !file.isEmpty()) {

			String originalFilename = file.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalFilename);

			List<String> extensioAllow = Arrays.asList("pdf", "xlxs", "jpg", "png");
			if (!extensioAllow.contains(extension)) {
				throw new IllegalArgumentException("illegel file format! upload only .pdf, .xlxs, .jpg, .png");
			}

			String randomString = UUID.randomUUID().toString();

			String uploadfileName = randomString + "." + extension;

			File saveFile = new File(uploadpath);
			if (!saveFile.exists()) {
				saveFile.mkdir();
			}
			// path : enotesapiservice/notes/java.pdf
			String storePath = uploadpath.concat(uploadfileName);

			// upload file
			long upload = Files.copy(file.getInputStream(), Paths.get(storePath));

			if (upload != 0) {
				FileDetails fileDtls = new FileDetails();
				fileDtls.setOriginalFileName(originalFilename);
				fileDtls.setDisplayFileName(getDisplayName(originalFilename));
				fileDtls.setUploadFileName(uploadfileName);
				fileDtls.setFileSize(file.getSize());
				fileDtls.setPath(storePath);
				FileDetails saveFileDtls = fileRepo.save(fileDtls);
				return saveFileDtls;
			}

		}
		return null;
	}

	private String getDisplayName(String originalFilename) {

		// dependency apache common io in pom.xml
		String extension = FilenameUtils.getExtension(originalFilename);
		String fileName = FilenameUtils.removeExtension(originalFilename);
		if (fileName.length() > 8) {
			fileName = fileName.substring(0, 7);
		}
		fileName = fileName + "." + extension;
		return fileName;
	}

	private void checkCategoryExist(CategoryDto category) throws Exception {

		categoryRepo.findById(category.getId()).orElseThrow(() -> new ResourceNotFoundException("Category is Invalid"));
	}

	@Override
	public List<NotesDto> getAllNotes() {
		return notesRepo.findAll().stream().map(note -> mapper.map(note, NotesDto.class)).toList();
	}

	@Override
	public byte[] downloadFile(FileDetails fileDetails) throws Exception {

		InputStream io = new FileInputStream(fileDetails.getPath());

		return org.springframework.util.StreamUtils.copyToByteArray(io);
	}

	@Override
	public FileDetails getFileDetails(Integer id) throws Exception {
		FileDetails fileDtls = fileRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("File not found!"));
		return fileDtls;
	}

	@Override
	public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize) {
		// 10 - 5,5 - 2 pages
		org.springframework.data.domain.Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Notes> pageNotes = notesRepo.findByCreatedByAndIsDeletedFalse(userId, pageable);

		List<NotesDto> notesDto = pageNotes.get().map(n -> mapper.map(n, NotesDto.class)).toList();
		NotesResponse notes = NotesResponse.builder().notes(notesDto).pageNo(pageNotes.getNumber())
				.pageSize(pageNotes.getSize()).totalElements(pageNotes.getTotalElements())
				.totalPages(pageNotes.getTotalPages()).isFirst(pageNotes.isFirst()).isLast(pageNotes.isLast()).build();
		return notes;
	}

	@Override
	public void softDeleteNotes(Integer id) throws Exception {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id Invalid! Not Found"));
		notes.setIsDeleted(true);
		notes.setDeletedOn(LocalDateTime.now());
		notesRepo.save(notes);
	}

	@Override
	public void restoreNotes(Integer id) throws Exception {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id Invalid! Not Found"));
		notes.setIsDeleted(false);
		notes.setDeletedOn(null);
		notesRepo.save(notes);

	}

	@Override
	public List<NotesDto> getUserRecycleBinNotes(Integer id) {
		List<Notes> recycleNotes = notesRepo.findByCreatedByAndIsDeletedTrue(id);
		List<NotesDto> noteDtoList = recycleNotes.stream().map(note ->mapper.map(note, NotesDto.class)).toList();
		return noteDtoList;
	}

	@Override
	public void hardDeleteNotes(Integer id) throws Exception {

		 Notes notes= notesRepo.findById(id)
		.orElseThrow(() -> new ResourceNotFoundException("Notes not found"));
		 
		 if(notes.getIsDeleted()) {
			 notesRepo.delete(notes);
		 }else {
			 throw new IllegalArgumentException("Sorry you cant hard delete Directly");
		 }
	}

	@Override
	public void emptyRecycleBin(int userId) {

		List<Notes> recycleNotes = notesRepo.findByCreatedByAndIsDeletedTrue(userId);
		if(!CollectionUtils.isEmpty(recycleNotes)) {
			notesRepo.deleteAll(recycleNotes);
		}
		
	}

	@Override
	public void favouriteNotes(Integer noteId) throws Exception {
		int userId = 1;
		Notes notes = notesRepo.findById(noteId)
		.orElseThrow(()-> new ResourceNotFoundException("Notes not foound"));
		FavouriteNote favouriteNote = FavouriteNote.builder()
				.note(notes)
				.userId(userId)
				.build();
				favouriteNoteRepo.save(favouriteNote);
	}

	@Override
	public void unFavouriteNotes(Integer favouriteNoteId) throws Exception {

		FavouriteNote favnote = favouriteNoteRepo.findById(favouriteNoteId)
				.orElseThrow(()-> new ResourceNotFoundException("Favouorite Notes not foound or Invalid ID"));
		favouriteNoteRepo.delete(favnote);
	}

	@Override
	public List<FavouriteNoteDto> getUserFavouriteNotes() throws Exception {
		int userId = 1;
		List<FavouriteNote> favouriteNotes = favouriteNoteRepo.findByUserId(userId);
		return favouriteNotes.stream().map(fn -> mapper.map(fn, FavouriteNoteDto.class)).toList();
	}

	@Override
	public Boolean copyNotes(Integer id) throws Exception {

		Notes notes = notesRepo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Notes id Invalid! Not found"));
		
//		Notes copyNote = new Notes();
//		copyNote.setTitle(notes.getTitle());
		
		Notes copyNote = Notes.builder()
				.title(notes.getTitle())
				.description(notes.getDescription())
				.category(notes.getCategory())
				.isDeleted(false)
				.fileDetails(null)
				.build();
		
		//TODO : Need to check user validation
				
				Notes saveCopyNote = notesRepo.save(copyNote);
				
				if(!ObjectUtils.isEmpty(saveCopyNote)) {
					return true;
				}
				return false;
	}
	
	
	
	

}
