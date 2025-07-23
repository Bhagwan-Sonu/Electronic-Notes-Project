package com.enotes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.enotes.dto.FavouriteNoteDto;
import com.enotes.dto.NotesDto;
import com.enotes.dto.NotesResponse;
import com.enotes.entity.FileDetails;
import com.enotes.service.NotesService;
import com.enotes.util.CommonUtil;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

	@Autowired
	private NotesService notesService;
	
	@PostMapping("/save")
	public ResponseEntity<?> saveNotes(@RequestParam String notes,
			@RequestParam(required = false) MultipartFile file) throws Exception{
		Boolean saveNotes = notesService.saveNotes(notes, file);
		if(saveNotes) {
			return CommonUtil.createBuildResponseMessage("Notes saved successfully.", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not save", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception{
		
		FileDetails fileDetails = notesService.getFileDetails(id);
		byte [] data = notesService.downloadFile(fileDetails);
		
		HttpHeaders headers = new HttpHeaders();
		String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
		headers.setContentType(MediaType.parseMediaType(contentType));
		headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());
		
		return ResponseEntity.ok().headers(headers).body(data);
	}
	
	@GetMapping("/")
	public ResponseEntity<?> getAllNotes(){
		List<NotesDto> notes = notesService.getAllNotes();
		if(CollectionUtils.isEmpty(notes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@GetMapping("/user-notes")
	public ResponseEntity<?> getAllNotesByUser(@RequestParam(name="pageNo",defaultValue = "0") Integer pageNo,
	@RequestParam(name="pageSize",defaultValue = "10")Integer pageSize){
		Integer userId = 1;
		NotesResponse notes = notesService.getAllNotesByUser(userId, pageNo, pageSize);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@GetMapping("/delete/{id}")
	public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception{
		notesService.softDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Deleted successfully.", HttpStatus.OK);
	}
	
	@GetMapping("/restore/{id}")
	public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception{
		notesService.restoreNotes(id);
		return CommonUtil.createBuildResponseMessage("Notes Restored successfully.", HttpStatus.OK);
	}
	
	@GetMapping("/recycle-bin")
	public ResponseEntity<?> getUserRecycleBinNotes() throws Exception{
		Integer id = 1;
		List<NotesDto> notes = notesService.getUserRecycleBinNotes(id);
		if(CollectionUtils.isEmpty(notes)) {
			return CommonUtil.createBuildResponseMessage("Notes not available in recycle bin", HttpStatus.OK);
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception{
		notesService.hardDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Deleted successfully.", HttpStatus.OK);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> emptyRecycleBin(@PathVariable Integer id) throws Exception{
		int userId = 2;
		notesService.emptyRecycleBin(userId);
		return CommonUtil.createBuildResponseMessage("Deleted successfully.", HttpStatus.OK);
	}
	
	@GetMapping("/fav/{noteId}")
	public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId) throws Exception{
		notesService.favouriteNotes(noteId);
		return CommonUtil.createBuildResponseMessage("Favourite Note Added.", HttpStatus.CREATED);
	}
	
	@DeleteMapping("/un-fav/{favnoteId}")
	public ResponseEntity<?> unFavouoriteNote(@PathVariable Integer favnoteId) throws Exception{
		notesService.unFavouriteNotes(favnoteId);
		return CommonUtil.createBuildResponseMessage("Favourite notes removed.", HttpStatus.OK);
	}
	
	@GetMapping("/favNote")
	public ResponseEntity<?> getUserFavouriteNote() throws Exception{
		List<FavouriteNoteDto> userFavouriteNotes = notesService.getUserFavouriteNotes();
		if(CollectionUtils.isEmpty(userFavouriteNotes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(userFavouriteNotes, HttpStatus.OK);
	}
	
	@GetMapping("/copy/{id}")
	public ResponseEntity<?> copyNotes(@PathVariable Integer id) throws Exception{
		Boolean copyNotes = notesService.copyNotes(id);
		if(copyNotes) {
			return CommonUtil.createBuildResponseMessage("Notes copied successfully.", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not copied", HttpStatus.INTERNAL_SERVER_ERROR);

	}
}
