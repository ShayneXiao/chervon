package com.chervon.iot.mobile.util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class SendEmail{
    @Autowired
    private MailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    public void sendAttachmentsMail(String email,String url)throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);//发送者.
        message.setTo(email);//接收者.
        message.setSubject("测试邮件（邮件主题）");//邮件主题.
        message.setText(url);//邮件内容.
        mailSender.send(message);//
    }
}
