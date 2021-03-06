package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.entity.Wypozyczenia;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "RezerwacjeBean")
@RequestScoped
public class RezerwacjeBean implements Serializable {

	private List<Rezerwacje> filteredRezerwacje;
	private Rezerwacje zaznaczonaRezerwacja;// = new Rezerwacje();
	private Klienci klient = new Klienci();

	public RezerwacjeBean() {

	}

	@PostConstruct
	public void initBean() {
		this.zaznaczonaRezerwacja = null;
	}

	public List<Rezerwacje> getAllRezerwacje() {

		List<Rezerwacje> list = null;
		EntityManager em = DBManager.getManager().createEntityManager();

		list = em.createNamedQuery("Rezerwacje.findDifferentStatus")
				.setParameter("status", "Zrealizowana").getResultList();

		em.close();
		return list;

	}

	public List<Rezerwacje> gethistoriaRezerwacje() {

		List<Rezerwacje> list = null;
		EntityManager em = DBManager.getManager().createEntityManager();

		list = em.createNamedQuery("Rezerwacje.findDiByStatus")
				.setParameter("status", "Zrealizowana").getResultList();

		em.close();
		return list;

	}

	public List<Rezerwacje> getklientbyid(String status) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		List<Rezerwacje> rezerwacje;
		List<Rezerwacje> zrealizowaneRezerwacje = new ArrayList<>();
		List<Rezerwacje> aktualneRezrwacje = new ArrayList<>();
		int user_id = (int) session.getAttribute("user_id");
		List lista;
		EntityManager em = DBManager.getManager().createEntityManager();
		klient = (Klienci) em
				.createQuery("Select k from Klienci k where k.id_klienta=:id")
				.setParameter("id", user_id).getSingleResult();
		klient.getRezerwacjes().size();
		rezerwacje = klient.getRezerwacjes();
		em.close();
		for (Rezerwacje rezerwacja : rezerwacje) {
			if (rezerwacja.getStatus_rezerwacji().equals("Zrealizowana")) {
				zrealizowaneRezerwacje.add(rezerwacja);
			} else {
				aktualneRezrwacje.add(rezerwacja);
			}

		}
		if (status.equals("Zrealizowana")) {
			return zrealizowaneRezerwacje;
		} else {
			return aktualneRezrwacje;
		}

	}

	public List<Filmy> getFilmyArchiwum() {
		if(zaznaczonaRezerwacja != null) {
			
		List<Filmy> filmyarchiwum = new ArrayList<>();
		String[] idfilm = { "0" };
		if (this.zaznaczonaRezerwacja.getHistoria_rezer() != null) {
			idfilm = this.zaznaczonaRezerwacja.getHistoria_rezer().split(";");

			EntityManager em = DBManager.getManager().createEntityManager();

			for (int i = 0; i < idfilm.length; i++) {
				Filmy film = (Filmy) em.createNamedQuery("Filmy.findById")
						.setParameter("idFilmu", Integer.parseInt(idfilm[i]))
						.getSingleResult();
				filmyarchiwum.add(film);
			}
		}
		return filmyarchiwum;
		}
		return null;
	}

	public void przekierowanieszczegoly() {
		if (zaznaczonaRezerwacja != null) {
			try {

				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/szczegolyrezerwacji.xhtml?rez="
										+ this.zaznaczonaRezerwacja
												.getId_rezerwacji());

			} catch (IOException e) {
				System.out.print(this.zaznaczonaRezerwacja.getId_rezerwacji());

			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra�e� rezerwacji", 3);
		}
	}

	public void przekierowaniearchiwum() {
		if (zaznaczonaRezerwacja != null) {
			try {

				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/Admin/rezerwacjeHistoriaSzczegoly.xhtml?rez="
										+ this.zaznaczonaRezerwacja
												.getId_rezerwacji());

			} catch (IOException e) {
				System.out.print(this.zaznaczonaRezerwacja.getId_rezerwacji());

			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra�e� rezerwacji", 3);
		}
	}

	public void przekierowanieDodajRezerwacje() {
		try {

			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect("/wypozyczalnia/Zarzadzanie/rezerwacjeNowa.xhtml");

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

	public Rezerwacje getZaznaczonaRezerwacja() {
		return zaznaczonaRezerwacja;
	}

	public void setZaznaczonaRezerwacja(Rezerwacje zaznaczonaRezerwacja) {
		this.zaznaczonaRezerwacja = zaznaczonaRezerwacja;
	}

	public List<Rezerwacje> getFilteredRezerwacje() {
		return filteredRezerwacje;
	}

	public void setFilteredRezerwacje(List<Rezerwacje> filteredRezerwacje) {
		this.filteredRezerwacje = filteredRezerwacje;
	}

}
