package com.enotes.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.enotes.dto.EmailRequest;
import com.enotes.dto.PasswordChangeRequest;
import com.enotes.dto.PswdResetRequest;
import com.enotes.entity.User;
import com.enotes.exception.ResourceNotFoundException;
import com.enotes.repository.UserRepository;
import com.enotes.service.UserService;
import com.enotes.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public void changePassword(PasswordChangeRequest passwordRequest) {
		User logedInUser = CommonUtil.getLoggedInUser();
		if (!passwordEncoder.matches(passwordRequest.getOldPassword(), logedInUser.getPassword())) {
			throw new IllegalArgumentException("Old password is incorrect !!");
		}
		String encodePassword = passwordEncoder.encode(passwordRequest.getNewPassword());
		logedInUser.setPassword(encodePassword);
		userRepository.save(logedInUser);
	}

	@Override
	public void sendEmailPasswordReset(String email, String url) throws Exception {
		User user = userRepository.findByEmail(email);
		if (ObjectUtils.isEmpty(user)) {
			throw new ResourceNotFoundException("invalid email!");
		}
		// Generate unique password reset token
		String passwordResetToken = UUID.randomUUID().toString();
		user.getStatus().setPasswordResetToken(passwordResetToken);
		User updateUser = userRepository.save(user);

		sendEmailRequest(updateUser, url);

	}

	private void sendEmailRequest(User user, String url) throws Exception {

		String message = "Hi,<b>[[username]]</b>" + "<br><p> You have requested to reset your password</p>."
				+ "<p> Click the link below to change your password.</p>"
				+ "<p><a href=[[url]]>Change my password</a></p> "
				+ "<p> Ignore this email if you do remember your password,"
				+ "or you have not made the request.</p><br><br>" + "Thanks,<br>Enotes.com";

		message = message.replace("[[username]]", user.getFirstName());
		message = message.replace("[[url]]", url + "/api/v1/home/verify-pswd-link?uid=" + user.getId() + "&&code="
				+ user.getStatus().getPasswordResetToken());
		EmailRequest emailRequest = EmailRequest.builder().to(user.getEmail()).title("Password reset")
				.subject("Password Reset link").message(message).build();
		// send password reset email to user
		emailService.sendEmail(emailRequest);
	}

	@Override
	public void verifyPswdResetLink(Integer uid, String code) throws Exception {
		User user = userRepository.findById(uid).orElseThrow(() -> new ResourceNotFoundException("Invalid Exception"));
		verifyPasswordResetToken(user.getStatus().getPasswordResetToken(), code);
	}

	private void verifyPasswordResetToken(String existtoken, String reqToken) {
		//req token not null
		if (StringUtils.hasText(reqToken)) {
			//password already reset
			if (!StringUtils.hasText(existtoken)) {
				throw new IllegalArgumentException("Password already reset.");
			}
			//user req token changes
			if (!existtoken.equals(reqToken)) {
				throw new IllegalArgumentException("Invalid token.");
			}
		} else {
			throw new IllegalArgumentException("Invalid token.");
		}
	}

	@Override
	public void resetPassword(PswdResetRequest pswdResetRequest) throws Exception {
		User user = userRepository.findById(pswdResetRequest.getUid()).orElseThrow(() -> new ResourceNotFoundException("Invalid Exception"));
		String encodePassword = passwordEncoder.encode(pswdResetRequest.getNewPassword());
		user.setPassword(encodePassword);
		user.getStatus().setPasswordResetToken(null);
		userRepository.save(user);
	}
	
	

}
