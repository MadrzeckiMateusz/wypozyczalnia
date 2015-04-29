/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

/**
 * @author Mateusz
 *
 */
public class statystykiRezerwacjiModel {
		
	
	private int liczbaogolem;
	private int liczbazrealizowanych;
	private int liczbaWobsludze;
	/**
	 * @param liczbaogolem
	 * @param liczbazrealizowanych
	 * @param liczbaWobsludze
	 */
	public statystykiRezerwacjiModel(int liczbaogolem,
			int liczbazrealizowanych, int liczbaWobsludze) {
	
		this.liczbaogolem = liczbaogolem;
		this.liczbazrealizowanych = liczbazrealizowanych;
		this.liczbaWobsludze = liczbaWobsludze;
	}
	/**
	 * @return the liczbaogolem
	 */
	public int getLiczbaogolem() {
		return liczbaogolem;
	}
	/**
	 * @param liczbaogolem the liczbaogolem to set
	 */
	public void setLiczbaogolem(int liczbaogolem) {
		this.liczbaogolem = liczbaogolem;
	}
	/**
	 * @return the liczbazrealizowanych
	 */
	public int getLiczbazrealizowanych() {
		return liczbazrealizowanych;
	}
	/**
	 * @param liczbazrealizowanych the liczbazrealizowanych to set
	 */
	public void setLiczbazrealizowanych(int liczbazrealizowanych) {
		this.liczbazrealizowanych = liczbazrealizowanych;
	}
	/**
	 * @return the liczbaWobsludze
	 */
	public int getLiczbaWobsludze() {
		return liczbaWobsludze;
	}
	/**
	 * @param liczbaWobsludze the liczbaWobsludze to set
	 */
	public void setLiczbaWobsludze(int liczbaWobsludze) {
		this.liczbaWobsludze = liczbaWobsludze;
	}
}
