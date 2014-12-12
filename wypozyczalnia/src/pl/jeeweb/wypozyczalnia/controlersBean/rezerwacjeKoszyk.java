package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "koszyk")
@SessionScoped
public class rezerwacjeKoszyk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Filmy> rezerwowaneFilmy = new ArrayList<>();
	private Rezerwacje rezerwacja = new Rezerwacje();
	private Filmy filmDoKoszyka = new Filmy();
	

	public rezerwacjeKoszyk() {
	}

	public void dodajDoKoszyka(int id_filmu) {

		EntityManager em = DBManager.getManager().createEntityManager();
		filmDoKoszyka = em.find(Filmy.class, id_filmu);
		filmDoKoszyka.getKopieFilmus().size();// Lazy init
		rezerwowaneFilmy.add(filmDoKoszyka);
		em.close();
		filmDoKoszyka = new Filmy(); 
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Film dodano do koszyka !!!", 1);

	}

	public String usunFilm(Filmy film) {
		rezerwowaneFilmy.remove(film);
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"Globalmessage", "Film usuniêty z koszyka !!!", 1);
		return "";
	}

	@SuppressWarnings("unchecked")
	public String setNumerRezerwacji() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List<Integer> id = em.createNativeQuery(
				"Select id_rezerwacji from rezerwacje").getResultList();
		em.close();
		List<Integer> id_int = new ArrayList<Integer>();
		Integer i;
		for (Integer k : id) {
			id_int.add(new Integer(k));
		}
		if (id_int.size() == 0)
			i = 0;
		else
			i = Collections.max(id_int);

		i += 1;
		String numerKlienta = "";
		if (i < 10)
			numerKlienta = "RE/000" + i.toString();
		if (i >= 10 && i < 100)
			numerKlienta = "RE/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerKlienta = "RE/0" + i.toString();
		if (i >= 1000)
			numerKlienta = "RE/" + i.toString();
		return numerKlienta;
	}

	public String dokonajRezerwacji() {
		if (rezerwowaneFilmy.isEmpty()) {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"Globalmessage", "Koszyk jest pusty !!!", 3);
		} else {
			HttpSession session = (HttpSession) FacesContext
					.getCurrentInstance().getExternalContext().getSession(true);
			int user_id = (int) session.getAttribute("user_id");
			EntityManager em = DBManager.getManager().createEntityManager();
			this.rezerwacja.setKlienci(em.find(Klienci.class, user_id));
			this.rezerwacja.setNr_rezerwacji(setNumerRezerwacji());
			this.rezerwacja.setData_rezerwacji(currentDate());
			this.rezerwacja.setData_odbioru(nextDate(3));
			this.rezerwacja.setStatus_rezerwacji("W REALIZACJI");
			
			em.getTransaction().begin();
			em.persist(rezerwacja);
			em.getTransaction().commit();
			oznaczKopieFilmu(em);

			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"Globalmessage", "Filmy zarezerwowane!!!", 1);

		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public void oznaczKopieFilmu(EntityManager em1) {
		EntityManager em = em1;
		List<Integer> maxid = em.createNativeQuery(
				"Select Id_rezerwacji from rezerwacje").getResultList();
		this.rezerwacja = new Rezerwacje();
		int maxID = Collections.max(maxid);
		this.rezerwacja = em.find(Rezerwacje.class, maxID);

		for (Filmy film : rezerwowaneFilmy) {
			
			for (KopieFilmu kf : film.getKopieFilmus()) {
				if (kf.getRezerwacje() == null) {
					kf.setRezerwacje(rezerwacja);
					em.getTransaction().begin();
					em.merge(kf);
					em.getTransaction().commit();
					break;
				}
			}
		}
		this.rezerwowaneFilmy = new ArrayList<>();
		em.close();
		this.rezerwacja = new Rezerwacje();
	}

	public Date nextDate(int days) {
		final long ONE_DAY_MILLIS = 86400 * 1000;
		Date now = new Date();
		Date date = new Date(now.getTime() + (3 * ONE_DAY_MILLIS));

		return date;
	}

	public Date currentDate() {
		Date date = new Date();
		return date;
	}

	public List<Filmy> getRezerwowaneFilmy() {
		return rezerwowaneFilmy;
	}

	public void setRezerwowaneFilmy(List<Filmy> rezerwowaneFilmy) {
		this.rezerwowaneFilmy = rezerwowaneFilmy;
	}

	

}
