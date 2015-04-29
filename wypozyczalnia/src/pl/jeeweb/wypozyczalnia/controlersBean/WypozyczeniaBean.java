/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
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
		this.zaznaczoneWypozyczenie = null;
		this.filteredWypozyczenia = null;
	}

	public List<Wypozyczenia> getAllWypozyczenia() {
		List<Wypozyczenia> wypozyczenia = null;
		List<Wypozyczenia> aktualne = new ArrayList<>();
		EntityManager em = DBManager.getManager().createEntityManager();
		wypozyczenia = em.createNamedQuery("Wypozyczenia.findAll")
				.getResultList();
		for (Wypozyczenia wypozyczenie : wypozyczenia) {
			if (wypozyczenie.getData_zwrotu() == null) {
				aktualne.add(wypozyczenie);
			}
		}
		return aktualne;

	}

	public List<Wypozyczenia> getZwroconeWypozyczenia() {
		List<Wypozyczenia> wypozyczenia = null;
		List<Wypozyczenia> zwrocone = new ArrayList<>();
		EntityManager em = DBManager.getManager().createEntityManager();
		wypozyczenia = em.createNamedQuery("Wypozyczenia.findAll")
				.getResultList();
		for (Wypozyczenia wypozyczenie : wypozyczenia) {
			if (wypozyczenie.getData_zwrotu() != null) {
				zwrocone.add(wypozyczenie);

			}
		}
		return zwrocone;

	}

	public List<Filmy> getFilmyArchiwum() {
		List<Filmy> filmyarchiwum = new ArrayList<>();
		String[] idfilm = { "0" };
		if (this.zaznaczoneWypozyczenie != null) {
			if (this.zaznaczoneWypozyczenie.getHistoria_wypo() != null) {
				idfilm = this.zaznaczoneWypozyczenie.getHistoria_wypo().split(
						";");

				EntityManager em = DBManager.getManager().createEntityManager();

				for (int i = 0; i < idfilm.length; i++) {
					Filmy film = (Filmy) em
							.createNamedQuery("Filmy.findById")
							.setParameter("idFilmu",
									Integer.parseInt(idfilm[i]))
							.getSingleResult();
					filmyarchiwum.add(film);
				}
			}

		}
		return filmyarchiwum;
	}

	public List<Wypozyczenia> getklientbyid(String status) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		List<Wypozyczenia> wypozyczenia;
		List<Wypozyczenia> zrealizowaneWypozyczenia = new ArrayList<>();
		List<Wypozyczenia> aktualneWypozyczenia = new ArrayList<>();
		int user_id = (int) session.getAttribute("user_id");

		EntityManager em = DBManager.getManager().createEntityManager();
		Klienci klient = (Klienci) em
				.createQuery("Select k from Klienci k where k.id_klienta=:id")
				.setParameter("id", user_id).getSingleResult();
		klient.getWypozyczenias().size();
		wypozyczenia = klient.getWypozyczenias();
		em.close();
		for (Wypozyczenia wypozyczenie : wypozyczenia) {
			if (wypozyczenie.getData_zwrotu() != null) {
				zrealizowaneWypozyczenia.add(wypozyczenie);
			} else {
				aktualneWypozyczenia.add(wypozyczenie);
			}

		}
		if (status.equals("Zwrocone")) {
			return zrealizowaneWypozyczenia;
		} else {
			return aktualneWypozyczenia;
		}

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
				System.out.print(this.zaznaczoneWypozyczenie
						.getId_wypozyczenia());

			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ rezerwacji", 3);
		}
	}

	public void przekierowanieszczegolyHistoria() {
		if (zaznaczoneWypozyczenie != null) {
			try {

				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/Admin/wypozyczenieHistoriaSzczegoly.xhtml?wypo="
										+ this.zaznaczoneWypozyczenie
												.getId_wypozyczenia());

			} catch (IOException e) {
				System.out.print(this.zaznaczoneWypozyczenie
						.getId_wypozyczenia());

			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ rezerwacji", 3);
		}
	}

	public void przekierowanieDodajWypozyczenie() {
		try {

			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/wypozyczenieNowe.xhtml");

		} catch (IOException e) {
			System.out.print("ssadsdassd");

		}
	}

	public boolean isAdmin() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		String rola = (String) session.getAttribute("role-name");

		if (rola.equals("admin")) {
			return true;
		} else
			return false;

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
