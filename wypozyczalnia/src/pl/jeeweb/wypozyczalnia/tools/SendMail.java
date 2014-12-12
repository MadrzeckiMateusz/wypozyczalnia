package pl.jeeweb.wypozyczalnia.tools;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

	 private static final String HOST = "smtp.gmail.com";
	 private static final int PORT = 465;
	 // Adres email osby kt�ra wysy�a maila
	 private static final String FROM = "dvd.wypozyczalnia@gmail.com";
	 // Has�o do konta osoby kt�ra wysy�a maila
	 private static final String PASSWORD = "44rr5t44rr5t";
	 // Adres email osoby do kt�rej wysy�any jest mail
	 private  String TO ="" ;
	 // Temat wiadomo�ci
	 private  String SUBJECT = "";
	 // Tre�� wiadomo�ci
	 private  String CONTENT = "";
	 
	 public SendMail(String to, String subject, String content){
		 this.TO = to;
		 this.SUBJECT = subject;
		 this.CONTENT = content;
	 }
	

	 public void send() throws MessagingException {

	  Properties props = new Properties();
	  props.put("mail.transport.protocol", "smtps");
	  props.put("mail.smtps.auth", "true");

	  // Inicjalizacja sesji
	  Session mailSession = Session.getDefaultInstance(props);

	  // ustawienie debagowania, je�li nie chcesz ogl�da� log�w to usu�
	  // linijk� poni�ej lub zmie� warto�� na false
	  mailSession.setDebug(false);

	  // Tworzenie wiadomo�ci email
	  MimeMessage message = new MimeMessage(mailSession);
	  message.setSubject(SUBJECT);
	  message.setContent(CONTENT, "text/plain; charset=ISO-8859-2");
	  message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));

	  Transport transport = mailSession.getTransport();
	  transport.connect(HOST, PORT, FROM, PASSWORD);

	  // wys�anie wiadomo�ci
	  transport.sendMessage(message, message
	    .getRecipients(Message.RecipientType.TO));
	  transport.close();
	 }
	}
