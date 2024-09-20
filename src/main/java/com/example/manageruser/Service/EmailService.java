package com.example.manageruser.Service;

import com.example.manageruser.Model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "thietquang04@gmail.com";
        String senderName = "Now Feed Email Verification";
        String subject = "Vui lòng xác minh đăng ký của bạn!";
        String content = "Chào [[name]],<br>"
            + "Chào mừng bạn đên với Now Feed!<br>"
            + "Vui lòng nhấp vào liên kết dưới đây để xác minh đăng ký của bạn:<br>"
            + "<h2><a href=\"[[URL]]\" target=\"_self\">Verifi</a></h2>"
            + "Cảm ơn,<br>"
            + "Best Thing!.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
}

