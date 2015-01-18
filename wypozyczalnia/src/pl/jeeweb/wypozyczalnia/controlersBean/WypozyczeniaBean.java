/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.Wypozyczenia;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "WypozyczeniaBean")
@RequestScoped
public class WypozyczeniaBean implements Serializable {
	private List<Wypozyczenia> filteredWypozyczenia;
	private Wypozyczenia zaznaczoneWypozyczenie;

	public WypozyczeniaBean() {

	}

	@PostConstruct
	public void initBean() {
		this.filteredWypozyczenia = null;
	}

	public List<Wypozyczenia> getAllWypozyczenia() {
		List<Wypozyczenia> wypozyczenia = null;
		EntityManager em = DBManager.getManager().createEntityManager();
		wypozyczenia = em.createNamedQuery("Wypozyczenia.findAll")
				.getResultList();

		return wypozyczenia;

	}
	public void przekierowanieszczegoly() {
		if (zaznaczoneWypozyczenie != null) {
			try {

				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/szczegolyWypozyczenia.xhtml?wypo="
										+ this.zaznaczoneWypozyczenie
												.getId_wypozyczenia());

			} catch (IOException e) {
				System.out.print(this.zaznaczoneWypozyczenie.getId_wypozyczenia());

			}
		}else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Nie wybra³eœ rezerwacji", 3);
		}
	}

	public String ImieNazwiskoKlient(Klienci _klient) {
		return _klient.getImie() + " " + _klient.getNazwisko();
	}

	public List<Wypozyczenia> getFilteredWypozyczenia() {
		return filteredWypozyczenia;
	}

	public void setFilteredWypozyczenia(List<Wypozyczenia> filteredWypozyczenia) {
		this.filteredWypozyczenia = filteredWypozyczenia;
	}

	public Wypozyczenia getZaznaczoneWypozyczenie() {
		return zaznaczoneWypozyczenie;
	}

	public void setZaznaczoneWypozyczenie(Wypozyczenia zaznaczoneWypozyczenie) {
		this.zaznaczoneWypozyczenie = zaznaczoneWypozyczenie;
	}

}
