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
	 // Adres email osby która wysy³a maila
	 private static final String FROM = "dvd.wypozyczalnia@gmail.com";
	 // Has³o do konta osoby która wysy³a maila
	 private static final String PASSWORD = "44rr5t44rr5t";
	 // Adres email osoby do której wysy³any jest mail
	 private  String TO ="" ;
	 // Temat wiadomoœci
	 private  String SUBJECT = "";
	 // Treœæ wiadomoœci
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

	  // ustawienie debagowania, jeœli nie chcesz ogl¹daæ logów to usuñ
	  // linijkê poni¿ej lub zmieñ wartoœæ na false
	  mailSession.setDebug(false);

	  // Tworzenie wiadomoœci email
	  MimeMessage message = new MimeMessage(mailSession);
	  message.setSubject(SUBJECT);
	  message.setContent(CONTENT, "text/plain; charset=ISO-8859-2");
	  message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));

	  Transport transport = mailSession.getTransport();
	  transport.connect(HOST, PORT, FROM, PASSWORD);

	  // wys³anie wiadomoœci
	  transport.sendMessage(message, message
	    .getRecipients(Message.RecipientType.TO));
	  transport.close();
	 }
	}
