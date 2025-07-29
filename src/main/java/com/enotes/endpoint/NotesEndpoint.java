package com.enotes.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.enotes.dto.NotesDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.enotes.util.Constants.ROLE_ADMIN;
import static com.enotes.util.Constants.ROLE_ADMIN_USER;
import static com.enotes.util.Constants.ROLE_USER;
import static com.enotes.util.Constants.DEFAULT_PAGE_NO;
import static com.enotes.util.Constants.DEFAULT_PAGE_SIZE;

@Tag(name = "Notes", description = "All the Notes APIs")
@RequestMapping("/api/v1/notes")
public interface NotesEndpoint {

	@Operation(summary = "Save notes", tags = {"Notes", "USER"}, description = "Save notes")
	@PostMapping(value="/save", consumes = "multipart/form-data")
	@PreAuthorize(ROLE_ADMIN_USER)
	public ResponseEntity<?> saveNotes(@RequestParam
			@Parameter(description = "Json string notes", required = true,
			content = @Content(schema = @Schema(implementation = NotesDto.class))) String notes, 
			@RequestParam(required = false)  MultipartFile file) throws Exception;
	
	@Operation(summary = "Download notes", tags = {"Notes", "USER"}, description = "Download notes by Id")
	@GetMapping("/download/{id}")
	@PreAuthorize(ROLE_ADMIN_USER)
	public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception;
	
	@Operation(summary = "Get All Notes", tags = {"Notes"}, description = "Get All Notes")
	@GetMapping("/")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> getAllNotes();
	
	@Operation(summary = "Search Notes", tags = {"Notes"}, description = "Search Notes")
	@GetMapping("/search")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> searchNotes(@RequestParam(name = "key", defaultValue = "") String key,
			@RequestParam(name="pageNo",defaultValue = DEFAULT_PAGE_NO) Integer pageNo,
			@RequestParam(name="pageSize",defaultValue = DEFAULT_PAGE_SIZE)Integer pageSize);
	
	@Operation(summary = "Get notes by user", tags = {"Notes"}, description = "Get notes by user")
	@GetMapping("/user-notes")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> getAllNotesByUser(@RequestParam(name="pageNo",defaultValue = DEFAULT_PAGE_NO) Integer pageNo,
			@RequestParam(name="pageSize",defaultValue = DEFAULT_PAGE_SIZE)Integer pageSize);
	
	@Operation(summary = "Delete Notes", tags = {"Notes", "USER"}, description = "Delete Notes by ID")
	@GetMapping("/delete/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception;
	
	@Operation(summary = "Restore Notes", tags = {"Notes", "USER"}, description = "Restore Notes")
	@GetMapping("/restore/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception;
	
	@Operation(summary = "Get Notes from Recycle Bin", tags = {"Notes", "USER"}, description = "Get Notes from Recycle Bin")
	@GetMapping("/recycle-bin")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getUserRecycleBinNotes() throws Exception;
	
	@Operation(summary = "User Verification", tags = {"Notes", "USER"}, description = "Registered User verification")
	@DeleteMapping("/delete/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception;
	
	@Operation(summary = "Delete note", tags = {"Notes", "USER"}, description = "Delete note by user")
	@DeleteMapping("/delete")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> emptyUserRecycleBin(@PathVariable Integer id) throws Exception;
	
	@Operation(summary = "Favourite Note by ID", tags = {"Notes", "USER"}, description = "Favourite Note by ID")
	@GetMapping("/fav/{noteId}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId) throws Exception;
	
	@Operation(summary = "UN-Favourite Note", tags = {"Notes", "USER"}, description = "UN-Favourite Note")
	@DeleteMapping("/un-fav/{favnoteId}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> unFavouoriteNote(@PathVariable Integer favnoteId) throws Exception;
	
	@Operation(summary = "Favourite Note", tags = {"Notes", "USER"}, description = "Registered User verification")
	@GetMapping("/favNote")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getUserFavouriteNote() throws Exception;
	
	@Operation(summary = "Copy Notes", tags = {"Notes", "USER"}, description = "Copy Notes")
	@GetMapping("/copy/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> copyNotes(@PathVariable Integer id) throws Exception;
}
