package potatoes.server.service;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import potatoes.server.dto.SendMailRequest;
import potatoes.server.error.exception.MailSendFailed;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MailSender {

	private final JavaMailSender javaMailSender;

	@Transactional
	public void sendVerificationMail(SendMailRequest request, String verifyNumber) {
		try {
			MimeMessage mimeMessage = createMailForm(request.email(), verifyNumber);
			sendMail(mimeMessage);
		} catch (MessagingException e) {
			throw new MailSendFailed();
		}
	}

	private MimeMessage createMailForm(String email, String verifyNumber) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("we-go 인증번호 확인 메일입니다.");
		String content = createMailContent(verifyNumber);
		mimeMessageHelper.setText(content, true);

		return mimeMessage;
	}

	private void sendMail(MimeMessage mimeMessage) {
		try {
			javaMailSender.send(mimeMessage);
		} catch (MailSendException e) {
			throw new MailSendFailed();
		}
	}

	private String createMailContent(String verifyNumber) {
		return String.format("""
			<!DOCTYPE html>
			<html xmlns:th="http://www.thymeleaf.org">
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>We-Go 인증번호</title>
			</head>
			<body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;">
			    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 40px; border-radius: 10px; margin-top: 40px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
			        <div style="text-align: center; margin-bottom: 30px;">
			            <h1 style="color: #2C3E50; margin: 0; font-size: 28px;">We-Go</h1>
			        </div>
			       \s
			        <div style="margin-bottom: 30px; text-align: center;">
			            <h2 style="color: #2C3E50; font-size: 24px; margin-bottom: 20px;">이메일 인증번호</h2>
			            <p style="color: #666666; font-size: 16px; line-height: 1.5; margin-bottom: 30px;">
			                안녕하세요!<br>
			                We-Go 서비스 이메일 인증을 위한 인증번호입니다.
			            </p>
			           \s
			            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;">
			                <h3 style="color: #2C3E50; font-size: 32px; margin: 0; letter-spacing: 5px;">
			                    인증번호: %s
			                </h3>
			            </div>
			           \s
			            <p style="color: #999999; font-size: 14px; margin-top: 30px;">
			                인증번호는 3분간 유효합니다.<br>
			                본 메일은 발신전용이므로 회신이 불가합니다.
			            </p>
			        </div>
			       \s
			        <div style="text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eeeeee;">
			            <p style="color: #999999; font-size: 12px; margin: 0;">
			                © 2024 We-Go. All rights reserved.
			            </p>
			        </div>
			    </div>
			</body>
			</html>
			""", verifyNumber);
	}
}
