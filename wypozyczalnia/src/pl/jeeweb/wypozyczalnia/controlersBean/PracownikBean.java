/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "PracownikBean")
@ViewScoped
public class PracownikBean implements Serializable {
	private Pracownicy pracownik;
	private List<Pracownicy> filteredPracownicy;
	private Pracownicy selectedPracownik;

	public List<Pracownicy> getAllPracownicy() {
		List<Pracownicy> allPracownicy = null;
		EntityManager em = DBManager.getManager().createEntityManager();
		allPracownicy = (List<Pracownicy>) em.createNamedQuery(
				"Pracownicy.findAll").getResultList();
		em.close();

		return allPracownicy;
	}

	/**
	 * @return the filteredPracownicy
	 */
	public List<Pracownicy> getFilteredPracownicy() {
		return filteredPracownicy;
	}

	/**
	 * @param filteredPracownicy the filteredPracownicy to set
	 */
	public void setFilteredPracownicy(List<Pracownicy> filteredPracownicy) {
		this.filteredPracownicy = filteredPracownicy;
	}

	/**
	 * @return the selectedPracownik
	 */
	public Pracownicy getSelectedPracownik() {
		return selectedPracownik;
	}

	/**
	 * @param selectedPracownik the selectedPracownik to set
	 */
	public void setSelectedPracownik(Pracownicy selectedPracownik) {
		this.selectedPracownik = selectedPracownik;
	}

}
