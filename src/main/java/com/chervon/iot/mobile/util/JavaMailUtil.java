package com.chervon.iot.mobile.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Shayne on 2017/8/1.
 */
@Component
public class JavaMailUtil {
    private @Value("${fromEmailAccount}") String fromEmailAccount;
	private @Value("${fromEmailPassword}") String fromEmailPassword;

	// 邮箱服务器地址为: 类似smtp.163.com
	private @Value("${fromEmailSMTPHost}") String fromEmailSMTPHost;

	private @Value("${mail.transport.protocol}") String mail_transport_protocol;

	private @Value("${mail.smtp.auth}") String mail_smtp_auth;

	private @Value("${mail.smtp.starttls.enable}") String mail_smtp_starttls_enable;

	private Properties props;

	public JavaMailUtil() {	}

	public void sendEmail(String content, String toMailAccount, String revicerName) throws Exception {
		// 参数配置
		props = new Properties();
		// 使用的协议（JavaMail规范要求）
		props.setProperty("mail.transport.protocol", mail_transport_protocol);
		// 发件人的邮箱的 SMTP
		props.setProperty("mail.smtp.host", fromEmailSMTPHost);
		// 需要请求认证
		props.setProperty("mail.smtp.auth", mail_smtp_auth);
		//开启ttls
		props.setProperty("mail.smtp.starttls.enable", mail_smtp_starttls_enable);

		Session session = Session.getDefaultInstance(props);
		// 设置为debug模式, 可以查看详细的发送 log
		session.setDebug(true);

		// 3. 创建一封邮件
		MimeMessage message = createMimeMessage(session, fromEmailAccount, toMailAccount, content, revicerName);

		// 4. 根据 Session 获取邮件传输对象
		Transport transport = session.getTransport();

		// 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
		transport.connect(fromEmailAccount, fromEmailPassword);

		// 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人,
		// 抄送人, 密送人
		transport.sendMessage(message, message.getAllRecipients());

		// 7. 关闭连接
		transport.close();
	}

	/**
	 * 创建一封只包含文本的简单邮件
	 *
	 * @param session
	 *            和服务器交互的会话
	 * @param sendMail
	 *            发件人邮箱
	 * @param receiveMail
	 *            收件人邮箱
	 * @return
	 * @throws Exception
	 */
	private MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String content, String revicerName) throws Exception {
		// 1. 创建一封邮件
		MimeMessage message = new MimeMessage(session);

		// 2. From: 发件人
		message.setFrom(new InternetAddress(sendMail, "泉峰IOT", "UTF-8"));

		// 3. To: 收件人（可以增加多个收件人、抄送、密送）
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, revicerName, "UTF-8"));

		// 4. Subject: 邮件主题
		message.setSubject("邮箱认证", "UTF-8");

		// 5. Content: 邮件正文（可以使用html标签）
		message.setContent(content, "text/html;charset=UTF-8");

		// 6. 设置发件时间
		message.setSentDate(new Date());

		// 7. 保存设置
		message.saveChanges();

		return message;
	}
}