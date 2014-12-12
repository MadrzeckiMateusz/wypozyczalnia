package pl.jeeweb.wypozyczalnia.tools;


import org.apache.commons.lang3.RandomStringUtils;

public class RandomAlphaNum {
	 private RandomStringUtils utils;
	 private String randompassword;
	 public RandomAlphaNum(){};
	 public String RandomPass(){
		 utils = new RandomStringUtils();
		 
		return randompassword = RandomStringUtils.random(10, true, true);
	 }

	public String getRandompassword() {
		return randompassword;
	}

	public void setRandompassword(String randompassword) {
		this.randompassword = randompassword;
	}
}
