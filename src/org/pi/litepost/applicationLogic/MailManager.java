package org.pi.litepost.applicationLogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;

import org.pi.litepost.App;
import org.pi.litepost.View;

public class MailManager extends Manager{
	
	private final Session session;
	private final String systemMailAddress;
	
	public MailManager() {
		Properties props = new Properties();
		String host = App.config.getProperty("litepost.mail.smtp.host");
		String port = App.config.getProperty("litepost.mail.smtp.port");
		systemMailAddress = App.config.getProperty("litepost.mail.systemmail");
		String useSSL = App.config.getProperty("litepost.mail.usessl");
		
		props.setProperty("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", port);
		if(useSSL.equalsIgnoreCase("true")) {
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		}
		
		session = Session.getInstance(props, null);
	}
	
	/**
	 * Sends a mail from the default system mail address
	 * 
	 * @param to the recipient
	 * @param subject the mail's subject
	 * @param template the template to be used to render the content
	 * @param data the templates data
	 * @throws MessagingException
	 */
	public void sendSystemMail(String to, String subject, String template, HashMap<String, Object> data) throws MessagingException {
		ArrayList<String> recipients = new ArrayList<>();
		recipients.add(to);
		sendMail(systemMailAddress, recipients, subject, template, data);
	}
	
	/**
	 * Sends a mail to multiple recipients from the default system mail address
	 * @param to the recipient
	 * @param subject the mail's subject
	 * @param template the template to be used to render the content
	 * @param data the templates data
	 * @throws MessagingException
	 */
	public void sendSystemMail(Collection<String> to, String subject, String template, HashMap<String, Object> data) throws MessagingException {
		sendMail(systemMailAddress, to, subject, template, data);
	}
	
	/**
	 * Sends a mail
	 * @param from the mail's sender
	 * @param to the recipient
	 * @param subject the mail's subject
	 * @param template the template to be used to render the content
	 * @param data the templates data
	 * @throws MessagingException
	 */
	public void sendMail(String from, String to, String subject, String template, HashMap<String, Object> data) throws MessagingException {
		ArrayList<String> recipients = new ArrayList<>();
		recipients.add(to);
		sendMail(from, recipients, subject, template, data);
	}
	
	/**
	 * Sends a mail to multiple recipients
	 * @param from the mail's sender
	 * @param to the recipient
	 * @param subject the mail's subject
	 * @param template the template to be used to render the content
	 * @param data the templates data
	 * @throws MessagingException
	 */
	public void sendMail(String from, Collection<String> to, String subject, String template, HashMap<String, Object> data) throws MessagingException {
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		for(String recipient : to) {
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		}
		String content = View.make(template, data);
		msg.setSubject(subject);
		msg.setContent(content, "text/html; charset=utf-8");
		String username = App.config.getProperty("litepost.mail.username");
		String password = App.config.getProperty("litepost.mail.password");
		Transport.send(msg, username, password);
	}
}
