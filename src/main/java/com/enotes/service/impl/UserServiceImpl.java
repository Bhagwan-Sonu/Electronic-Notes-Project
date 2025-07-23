package com.enotes.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.enotes.dto.EmailRequest;
import com.enotes.dto.UserDto;
import com.enotes.entity.Role;
import com.enotes.entity.User;
import com.enotes.repository.RoleRepository;
import com.enotes.repository.UserRepository;
import com.enotes.service.UserService;
import com.enotes.util.Validation;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private Validation validation;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private EmailService emailService;
	
	@Override
	public Boolean register(UserDto userDto) throws Exception {

		validation.userValidation(userDto);
		User user = mapper.map(userDto, User.class);
		setRole(userDto, user);
		User saveUser = userRepo.save(user);
		if(!ObjectUtils.isEmpty(saveUser)) {
			
			//send email
			emailSend(saveUser);
			
			return true;
		}
		return false;
	}

	private void emailSend(User saveUser) throws Exception {
		
		String message = "Hi,<b>"+saveUser.getFirstName()+"</b>"
				+"<br> Your accunt register successfully.<br>"
				+"<br> Click the below link to verify your account."
				+"<a href='#'>Click Here</a> <br><br>"
				+"Thanks,<br>Enotes.com";
		
		EmailRequest emailRequest = EmailRequest.builder()
			.to(saveUser.getEmail())
			.title("Account creating confirmation")
			.subject("Account created success")
			.message(message)
			.build();
		emailService.sendEmail(emailRequest);
	}

	private void setRole(UserDto userDto, User user) {

		List<Integer> reqRoleId = userDto.getRoles().stream().map(r -> r.getId()).toList();
		List<Role> roles = roleRepo.findAllById(reqRoleId);
		user.setRole(roles);
	}

}
