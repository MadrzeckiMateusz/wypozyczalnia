/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

/**
 * @author Mateusz
 * 
 */
public class StatystykiWypozyczenModel {
	private int licznikOgolnyWypozyczenia;
	private int licznikWypozyczeniaZwrocone;
	private int licznikWypozyczeniaZrealizowane;

	public StatystykiWypozyczenModel() {
		super();
	}

	/**
	 * @param licznikOgolnyWypozyczenia
	 * @param licznikWypozyczeniaZwrocone
	 * @param licznikWypozyczeniaZrealizowane
	 */
	public StatystykiWypozyczenModel(int licznikOgolnyWypozyczenia,
			int licznikWypozyczeniaZwrocone, int licznikWypozyczeniaZrealizowane) {

		this.licznikOgolnyWypozyczenia = licznikOgolnyWypozyczenia;
		this.licznikWypozyczeniaZwrocone = licznikWypozyczeniaZwrocone;
		this.licznikWypozyczeniaZrealizowane = licznikWypozyczeniaZrealizowane;
	}

	/**
	 * @return the licznikOgolnyWypozyczenia
	 */
	public int getLicznikOgolnyWypozyczenia() {
		return licznikOgolnyWypozyczenia;
	}

	/**
	 * @param licznikOgolnyWypozyczenia
	 *            the licznikOgolnyWypozyczenia to set
	 */
	public void setLicznikOgolnyWypozyczenia(int licznikOgolnyWypozyczenia) {
		this.licznikOgolnyWypozyczenia = licznikOgolnyWypozyczenia;
	}

	/**
	 * @return the licznikWypozyczeniaZwrocone
	 */
	public int getLicznikWypozyczeniaZwrocone() {
		return licznikWypozyczeniaZwrocone;
	}

	/**
	 * @param licznikWypozyczeniaZwrocone
	 *            the licznikWypozyczeniaZwrocone to set
	 */
	public void setLicznikWypozyczeniaZwrocone(int licznikWypozyczeniaZwrocone) {
		this.licznikWypozyczeniaZwrocone = licznikWypozyczeniaZwrocone;
	}

	/**
	 * @return the licznikWypozyczeniaZrealizowane
	 */
	public int getLicznikWypozyczeniaZrealizowane() {
		return licznikWypozyczeniaZrealizowane;
	}

	/**
	 * @param licznikWypozyczeniaZrealizowane
	 *            the licznikWypozyczeniaZrealizowane to set
	 */
	public void setLicznikWypozyczeniaZrealizowane(
			int licznikWypozyczeniaZrealizowane) {
		this.licznikWypozyczeniaZrealizowane = licznikWypozyczeniaZrealizowane;
	}

	/**
	 * 
	 */

}
