/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Wypozyczenia;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "WypozyczeniaBean")
@RequestScoped
public class WypozyczeniaBean implements Serializable {
	private List<Wypozyczenia> wszytkieWypozyczenia;

	public WypozyczeniaBean() {
		
	}
	@PostConstruct
	public void initBean() {
		this.wszytkieWypozyczenia = getAllWypozyczenia();
	}

	private List<Wypozyczenia> getAllWypozyczenia() {
		List<Wypozyczenia> wypozyczenia = null;
		EntityManager em = DBManager.getManager().createEntityManager();
		wypozyczenia = em.createNamedQuery("Wypozyczenia.findAll")
				.getResultList();

		return wypozyczenia;

	}

	public List<Wypozyczenia> getWszytkieWypozyczenia() {
		return wszytkieWypozyczenia;
	}

	public void setWszytkieWypozyczenia(List<Wypozyczenia> wszytkieWypozyczenia) {
		this.wszytkieWypozyczenia = wszytkieWypozyczenia;
	}

}
