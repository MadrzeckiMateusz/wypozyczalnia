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
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
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
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		String rola = (String) session.getAttribute("role-name");
		List<Rezerwacje> list = null;
		EntityManager em = DBManager.getManager().createEntityManager();
		if (rola.equals("admin")) {
			list = em.createNamedQuery("Rezerwacje.findAll").getResultList();
		} else {
			list = em.createNamedQuery("Rezerwacje.findDifferentStatus")
					.setParameter("status", "Zrealizowana").getResultList();
		}
		em.close();
		return list;

	}

	public List<Rezerwacje> getklientbyid() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		int user_id = (int) session.getAttribute("user_id");
		List lista;
		EntityManager em = DBManager.getManager().createEntityManager();
		klient = (Klienci) em
				.createQuery("Select k from Klienci k where k.id_klienta=:id")
				.setParameter("id", user_id).getSingleResult();
		klient.getRezerwacjes().size();
		List<Rezerwacje> rezerwacje = klient.getRezerwacjes();
		em.close();

		return rezerwacje;

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
					"globalmessage", "Nie wybra³eœ rezerwacji", 3);
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
