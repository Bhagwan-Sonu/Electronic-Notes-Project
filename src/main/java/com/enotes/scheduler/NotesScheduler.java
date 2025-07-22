package com.enotes.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.enotes.entity.Notes;
import com.enotes.repository.NotesRepository;

@Component
public class NotesScheduler {
	
	@Autowired
	NotesRepository notesRepo;
	
	
	@Scheduled(cron = "* * * ? * *")   //cron expression to delete
	public void deleteNotesScheduler() {

		//20-Nov -14Nov -7days
		LocalDateTime cutOffDate = LocalDateTime.now().minusDays(7);
		List<Notes> deleteNotes = notesRepo.findAllByIsDeletedAndDeletedOnBefore(true, cutOffDate);
		notesRepo.deleteAll(deleteNotes);
	}
}
