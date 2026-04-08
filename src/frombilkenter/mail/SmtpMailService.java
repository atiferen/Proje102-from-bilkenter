package frombilkenter.mail;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
public class SmtpMailService {
    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private final String fromEmail;
    private final boolean authEnabled;
    private final boolean startTlsEnabled;
    private final boolean sslEnabled;

    public SmtpMailService(String host, String port, String username, String password, String fromEmail,
                           boolean authEnabled, boolean startTlsEnabled, boolean sslEnabled) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.fromEmail = fromEmail;
        this.authEnabled = authEnabled;
        this.startTlsEnabled = startTlsEnabled;
        this.sslEnabled = sslEnabled;
    }

    public static SmtpMailService fromEnvironment() {
        return new SmtpMailService(
            getenv("FROM_BILKENTER_SMTP_HOST"),
            getenv("FROM_BILKENTER_SMTP_PORT", "587"),
            getenv("FROM_BILKENTER_SMTP_USERNAME"),
            getenv("FROM_BILKENTER_SMTP_PASSWORD"),
            getenv("FROM_BILKENTER_SMTP_FROM"),
            Boolean.parseBoolean(getenv("FROM_BILKENTER_SMTP_AUTH", "true")),
            Boolean.parseBoolean(getenv("FROM_BILKENTER_SMTP_STARTTLS", "true")),
            Boolean.parseBoolean(getenv("FROM_BILKENTER_SMTP_SSL", "false"))
        );
    }

    public boolean isConfigured() {
        return notBlank(host) && notBlank(port) && notBlank(fromEmail)
            && (!authEnabled || (notBlank(username) && notBlank(password)));
    }

    public void sendVerificationCode(String recipient, String code, String subjectPrefix) throws MessagingException {
        if (!isConfigured()) {
            throw new MessagingException("SMTP settings are missing.");
        }
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", String.valueOf(authEnabled));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnabled));
        properties.put("mail.smtp.ssl.enable", String.valueOf(sslEnabled));

        Session session;
        if (authEnabled) {
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(properties);
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subjectPrefix + " Verification Code");
        message.setText("""
            Hello,

            Your From Bilkenter verification code is: %s

            If you did not request this code, you can ignore this e-mail.

            From Bilkenter
            """.formatted(code));
        Transport.send(message);
    }

    private static String getenv(String key) {
        return System.getenv(key);
    }

    private static String getenv(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
