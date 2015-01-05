package pl.jeeweb.wypozyczalnia.tools;


public class SHA256hash {

	
	
  public static String HashText(String tekst){
	
	return  org.apache.commons.codec.digest.DigestUtils.sha256Hex(tekst).toString();
  }
}
