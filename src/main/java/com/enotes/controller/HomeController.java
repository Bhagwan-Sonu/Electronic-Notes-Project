package com.enotes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enotes.dto.PswdResetRequest;
import com.enotes.endpoint.HomeEndpoint;
import com.enotes.service.HomeService;
import com.enotes.service.UserService;
import com.enotes.util.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HomeController implements HomeEndpoint{

	Logger log = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	private UserService userService;
	
	@Override
	public ResponseEntity<?> verifyUserAccount(Integer uid, String code) throws Exception{
		log.info("HomeController: verifyUserAccount : Execution start");
		Boolean verifyAccount = homeService.verifyAccount(uid, code);
		if(verifyAccount) {
			return CommonUtil.createBuildResponseMessage("Account verification successful.", HttpStatus.OK);
		}
		log.info("HomeController: verifyUserAccount : Execution end");
		return CommonUtil.createErrorResponseMessage("Invalid Verification user not verified.", HttpStatus.BAD_REQUEST);
	}
	
	@Override
	public ResponseEntity<?> sendEmailForPasswordReset(String email, HttpServletRequest request) throws Exception{
		String url = CommonUtil.getUrl(request);
		userService.sendEmailPasswordReset(email, url);
		return CommonUtil.createBuildResponseMessage("Email send successfully! check email for password reset. ", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> verifyPasswordResetLink(Integer uid, String code) throws Exception{
		userService.verifyPswdResetLink(uid, code);
		return CommonUtil.createBuildResponseMessage("Verification successful.", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> resetPassword(PswdResetRequest pswdResetRequest) throws Exception{
		userService.resetPassword(pswdResetRequest);
		return CommonUtil.createBuildResponseMessage("Password reset succefully.", HttpStatus.OK);
	}
}
