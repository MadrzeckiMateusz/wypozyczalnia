package pl.jeeweb.wypozyczalnia.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256hash {

	
	
  public static String HashText(String tekst){
	  MessageDigest md = null;
	try {
		md = MessageDigest.getInstance("SHA-256");
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		md.update(tekst.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // Change this to "UTF-16" if needed
	byte[] digest = md.digest();
	return new String(digest);
  }
}
