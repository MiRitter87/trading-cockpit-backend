package backend.controller;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * Tests the MailController.
 * 
 * @author Michael
 */
public class MailControllerTest {
	//@Test
	/**
	 * Explorative test to send an E-Mail.
	 */
	public void testSendMail() {
		Properties properties = this.getProperties();
		Session session = this.getSession(properties, "sending_mail_account_user", "sending_mail_account_password");
		
		try {
			this.sendMail(session, "mail_address_sender", "mail_address_receiver", "Hello World", "Hello World!");
		} catch (AddressException e) {
			fail(e.getMessage());
		} catch (MessagingException e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Initializes the properties for Mail transfer.
	 * @return
	 */
	private Properties getProperties() {
		Properties properties = new Properties();
		
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "mail.gmx.net");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.ssl.trust", "mail.gmx.net");
		
		return properties;
	}
	
	
	/**
	 * Gets the Session for Mail transfer.
	 * 
	 * @param properties The configuration properties.
	 * @param username The user name of the sending mail account.
	 * @param password The password of the sending mail account.
	 * @return The Session.
	 */
	private Session getSession(final Properties properties, final String username, final String password) {
		Session session = Session.getInstance(properties, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, password);
		    }
		});
		
		return session;
	}
	
	
	/**
	 * Sends an E-Mail.
	 * 
	 * @param session The Session for mail transfer.
	 * @param mailAddressFrom The mail address of the sender.
	 * @param mailAddressTo The mail address of the receiver.
	 * @param subject The subject of the mail.
	 * @param body The body text of the mail.
	 */
	private void sendMail(final Session session, final String mailAddressFrom, final String mailAddressTo, 
			final String subject, final String body) throws AddressException, MessagingException {
		
		Message message = new MimeMessage(session);
		
		message.setFrom(new InternetAddress(mailAddressFrom));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailAddressTo));
		message.setSubject(subject);
	
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(body, "text/html; charset=utf-8");
	
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
	
		message.setContent(multipart);
	
		Transport.send(message);
	}
}
