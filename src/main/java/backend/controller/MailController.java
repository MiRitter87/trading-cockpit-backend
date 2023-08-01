package backend.controller;

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
 * Controller providing methods to send E-Mails.
 *
 * @author Michael
 */
public class MailController {
    /**
     * Property Key: SMTP Server.
     */
    protected static final String PROPERTY_MAIL_SMTP_SERVER = "mail.smtp.server";

    /**
     * Property Key: SMTP Port.
     */
    protected static final String PROPERTY_MAIL_SMTP_PORT = "mail.smtp.port";

    /**
     * Property Key: Mail sender user name.
     */
    protected static final String PROPERTY_MAIL_SENDER_USERNAME = "mail.sender.username";

    /**
     * Property Key: Mail sender password.
     */
    protected static final String PROPERTY_MAIL_SENDER_PASSWORD = "mail.sender.password";

    /**
     * The session used to send mails.
     */
    private Session session;

    /**
     * The E-Mail address from which the message is sent.
     */
    private String mailAdressFrom;

    /**
     * Initializes the MailController.
     *
     * @throws Exception Failed to initialize MailController.
     */
    public MailController() throws Exception {
        Properties properties = this.getProperties();
        this.session = this.getSession(properties);

        this.mailAdressFrom = MainController.getInstance().getConfigurationProperty(PROPERTY_MAIL_SENDER_USERNAME);
    }

    /**
     * Sends an E-Mail.
     *
     * @param mailAddressTo The mail address of the receiver.
     * @param subject       The subject of the mail.
     * @param body          The body text of the mail.
     */
    public void sendMail(final String mailAddressTo, final String subject, final String body)
            throws AddressException, MessagingException {
        Message message = new MimeMessage(this.session);

        message.setFrom(new InternetAddress(this.mailAdressFrom));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailAddressTo));
        message.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    /**
     * Initializes the properties for Mail transfer.
     * 
     * @return Configuration properties for mail transfer.
     * @throws Exception Could not read configuration properties from file.
     */
    private Properties getProperties() throws Exception {
        Properties properties = new Properties();
        String smtpServer = MainController.getInstance().getConfigurationProperty(PROPERTY_MAIL_SMTP_SERVER);
        String smtpPort = MainController.getInstance().getConfigurationProperty(PROPERTY_MAIL_SMTP_PORT);

        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpServer);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.ssl.trust", smtpServer);

        return properties;
    }

    /**
     * Gets the Session for Mail transfer.
     *
     * @param properties The configuration properties.
     * @param username   The user name of the sending mail account.
     * @param password   The password of the sending mail account.
     * @return The Session.
     * @throws Exception Could not read configuration properties from file.
     */
    private Session getSession(final Properties properties) throws Exception {
        String username = MainController.getInstance().getConfigurationProperty(PROPERTY_MAIL_SENDER_USERNAME);
        String password = MainController.getInstance().getConfigurationProperty(PROPERTY_MAIL_SENDER_PASSWORD);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        return session;
    }
}
