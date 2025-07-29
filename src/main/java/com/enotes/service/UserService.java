package com.enotes.service;

import com.enotes.dto.PasswordChangeRequest;
import com.enotes.dto.PswdResetRequest;


public interface UserService {

	public void changePassword(PasswordChangeRequest passwordRequest);

	public void sendEmailPasswordReset(String email, String url) throws Exception;

	public void verifyPswdResetLink(Integer uid, String code) throws Exception;

	public void resetPassword(PswdResetRequest pswdResetRequest) throws Exception;
}
