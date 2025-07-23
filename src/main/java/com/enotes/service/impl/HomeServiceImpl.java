package com.enotes.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.enotes.entity.AccountStatus;
import com.enotes.entity.User;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.exception.SuccessException;
import com.enotes.repository.UserRepository;
import com.enotes.service.HomeService;

@Component
public class HomeServiceImpl implements HomeService{

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public Boolean verifyAccount(Integer userId, String verificationCode) throws Exception {
		User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Invalid User"));
		
		if(user.getStatus().getVarificationCode()==null) {
			throw new SuccessException("Account already verified.");
		}
		
		if(user.getStatus().getVarificationCode().equals(verificationCode)) {
			AccountStatus status = user.getStatus();
			status.setIsActive(true);
			status.setVarificationCode(null);
			userRepo.save(user);
			return true;
		}
		return false;
	}

	
}
