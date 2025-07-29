package com.enotes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.enotes.endpoint.NotesEndpoint;
import com.enotes.entity.FileDetails;
import com.enotes.service.NotesService;
import com.enotes.util.CommonUtil;

import jakarta.websocket.server.PathParam;

@RestController
public class NotesController implements NotesEndpoint {

	@Autowired
	private NotesService notesService;
	
	@Override
	public ResponseEntity<?> saveNotes(String notes,MultipartFile file) throws Exception{
		Boolean saveNotes = notesService.saveNotes(notes, file);
		if(saveNotes) {
			return CommonUtil.createBuildResponseMessage("Notes saved successfully.", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not save", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public ResponseEntity<?> downloadFile(Integer id) throws Exception{
		
		FileDetails fileDetails = notesService.getFileDetails(id);
		byte [] data = notesService.downloadFile(fileDetails);
		
		HttpHeaders headers = new HttpHeaders();
		String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
		headers.setContentType(MediaType.parseMediaType(contentType));
		headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());
		
		return ResponseEntity.ok().headers(headers).body(data);
	}
	
	@Override
	public ResponseEntity<?> getAllNotes(){
		List<NotesDto> notes = notesService.getAllNotes();
		if(CollectionUtils.isEmpty(notes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> searchNotes(String key, Integer pageNo, Integer pageSize){
		NotesResponse notes = notesService.getNotesByUserSearch(pageNo, pageSize, key);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);

	}
	
	@Override
	public ResponseEntity<?> getAllNotesByUser(Integer pageNo, Integer pageSize){
		NotesResponse notes = notesService.getAllNotesByUser(pageNo, pageSize);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> deleteNotes(Integer id) throws Exception{
		notesService.softDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Deleted successfully.", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> restoreNotes(Integer id) throws Exception{
		notesService.restoreNotes(id);
		return CommonUtil.createBuildResponseMessage("Notes Restored successfully.", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> getUserRecycleBinNotes() throws Exception{
		List<NotesDto> notes = notesService.getUserRecycleBinNotes();
		if(CollectionUtils.isEmpty(notes)) {
			return CommonUtil.createBuildResponseMessage("Notes not available in recycle bin", HttpStatus.OK);
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> hardDeleteNotes(Integer id) throws Exception{
		notesService.hardDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Deleted successfully.", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> emptyUserRecycleBin(Integer id) throws Exception{
		notesService.emptyRecycleBin();
		return CommonUtil.createBuildResponseMessage("Deleted successfully.", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> favouriteNote(Integer noteId) throws Exception{
		notesService.favouriteNotes(noteId);
		return CommonUtil.createBuildResponseMessage("Favourite Note Added.", HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<?> unFavouoriteNote(Integer favnoteId) throws Exception{
		notesService.unFavouriteNotes(favnoteId);
		return CommonUtil.createBuildResponseMessage("Favourite notes removed.", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> getUserFavouriteNote() throws Exception{
		List<FavouriteNoteDto> userFavouriteNotes = notesService.getUserFavouriteNotes();
		if(CollectionUtils.isEmpty(userFavouriteNotes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(userFavouriteNotes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> copyNotes(Integer id) throws Exception{
		Boolean copyNotes = notesService.copyNotes(id);
		if(copyNotes) {
			return CommonUtil.createBuildResponseMessage("Notes copied successfully.", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not copied", HttpStatus.INTERNAL_SERVER_ERROR);

	}
}
