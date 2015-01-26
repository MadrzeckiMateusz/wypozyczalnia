/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

/**
 * @author Mateusz
 *
 */
public class StatystykaFilmuModel {

	
	private String numer;
	private String tytul;
	private int iloscWypozyczen;
	private int iloscRezerwacji;
	/**
	 * @param numer
	 * @param tytul
	 * @param iloscWypozyczen
	 * @param iloscRezerwacji
	 */
	public StatystykaFilmuModel(String numer, String tytul,
			int iloscWypozyczen, int iloscRezerwacji) {
		super();
		this.numer = numer;
		this.tytul = tytul;
		this.iloscWypozyczen = iloscWypozyczen;
		this.iloscRezerwacji = iloscRezerwacji;
	}
	/**
	 * @return the numer
	 */
	public void incrementIloscWypozyczen() {
		this.iloscWypozyczen++;
	}
	public String getNumer() {
		return numer;
	}
	/**
	 * @param numer the numer to set
	 */
	public void setNumer(String numer) {
		this.numer = numer;
	}
	/**
	 * @return the tytul
	 */
	public String getTytul() {
		return tytul;
	}
	/**
	 * @param tytul the tytul to set
	 */
	public void setTytul(String tytul) {
		this.tytul = tytul;
	}
	/**
	 * @return the iloscWypozyczen
	 */
	public int getIloscWypozyczen() {
		return iloscWypozyczen;
	}
	/**
	 * @param iloscWypozyczen the iloscWypozyczen to set
	 */
	public void setIloscWypozyczen(int iloscWypozyczen) {
		this.iloscWypozyczen = iloscWypozyczen;
	}
	/**
	 * @return the iloscRezerwacji
	 */
	public int getIloscRezerwacji() {
		return iloscRezerwacji;
	}
	/**
	 * @param iloscRezerwacji the iloscRezerwacji to set
	 */
	public void setIloscRezerwacji(int iloscRezerwacji) {
		this.iloscRezerwacji = iloscRezerwacji;
	}
	
	
}
