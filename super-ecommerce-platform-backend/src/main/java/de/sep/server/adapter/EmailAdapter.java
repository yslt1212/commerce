package de.sep.server.adapter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class EmailAdapter {

    Properties properties;
    PasswordAuthentication passwordAuthentication= new PasswordAuthentication("d710f92e0cd0b5","9f4a9d1893531f");

    public EmailAdapter() {
        properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.host", "smtp.mailtrap.io");
        properties.put("mail.smtp.port", "2525");
        properties.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");

    }

    public void sendMail(String subject, String content,String address){
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return passwordAuthentication;
            }
        });
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(address)
            );
            message.setSubject(subject);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content,"text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("YOU'VE GOT MAIL !!!!!!");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("YOUR MAIL CRASH LANDED !!!!!!");
        }
    }
}
