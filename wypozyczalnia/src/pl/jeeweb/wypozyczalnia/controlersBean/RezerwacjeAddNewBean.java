/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.parser.Entity;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.tools.DateTools;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "RezerwacjeAddNewBean")
@ViewScoped
public class RezerwacjeAddNewBean implements Serializable {

	public RezerwacjeAddNewBean() {

	}

	private Rezerwacje rezerwacja = new Rezerwacje();
	private Pracownicy pracownik = new Pracownicy();
	private List<Filmy> selectedFilmy;
	private Klienci klient;
	private List<KopieFilmu> kopiefilmu;

	@PostConstruct
	public void initBean() {
		klient = null;
		kopiefilmu = new ArrayList<>();
		EntityManager em = DBManager.getManager().createEntityManager();
		int maxID = (int) em.createNamedQuery("Rezerwacje.getMaxId")
				.getSingleResult();
		this.rezerwacja.setNr_rezerwacji(setNumerRezerwacji(maxID));
		this.rezerwacja.setData_rezerwacji(currentDate());
		this.rezerwacja.setStatus_rezerwacji("W realizacji");
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		int id = (int) session.getAttribute("pracownik_id");

		this.pracownik = (Pracownicy) em
				.createNamedQuery("Pracownicy.findByid").setParameter("id", id)
				.getSingleResult();
		this.rezerwacja.setPracownicy(pracownik);

		em.close();

	}

	public static Date currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

		Date date = new Date();

		return date;
	}

	public void dodajFilmy() {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();

		if (!selectedFilmy.isEmpty()) {
			for (Filmy film : selectedFilmy) {
				Filmy film1 = (Filmy) em.createNamedQuery("Filmy.findById")
						.setParameter("idFilmu", film.getId_filmu())
						.getSingleResult();
				for (KopieFilmu kopia : film1.getKopieFilmus()) {
					if (kopia.getRezerwacje() == null
							&& kopia.getWypozyczenia() == null) {
						// kopia.setRezerwacje(rezerwacja);
						this.kopiefilmu.add(kopia);
						break;
					}
				}
			}
			// em.merge(this.rezerwacja);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Filmy dodane", 1);
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ filmu", 3);
		}
		em.close();
	}

	public void usunFilm(KopieFilmu kopia) {

		List<KopieFilmu> kopienieusuniete = new ArrayList<>();
		for (KopieFilmu kopiaTmp : kopiefilmu) {
			if (!kopia.equals(kopiaTmp)) {
				kopienieusuniete.add(kopiaTmp);

			}

		}
		this.kopiefilmu.clear();
		this.kopiefilmu.addAll(kopienieusuniete);
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Filmy usuniêty", 1);

	}

	public void dodajKlienta() {
		if (this.klient != null) {
			this.rezerwacja.setKlienci(klient);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Klient dodany", 1);
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ kllienta", 3);
		}
	}

	public void zapiszRezerwacje() {
		
		if (!this.kopiefilmu.isEmpty()) {
			if (this.klient != null) {
				dokonajRezerwacjiZmianydoBazy();
			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie doda³eœ klienta", 3);
			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie doda³eœ filmów", 3);
		}

	}

	private void dokonajRezerwacjiZmianydoBazy() {
		if(this.rezerwacja.getData_odbioru() != null) {
			
		
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		this.rezerwacja = em.merge(this.rezerwacja);
		em.getTransaction().commit();

		for (KopieFilmu kf : this.kopiefilmu) {
			kf.setRezerwacje(this.rezerwacja);
			em.getTransaction().begin();
			em.merge(kf);
			em.getTransaction().commit();

		}

		em.close();

		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Rezerwacja dodana pomyœlnie", 1);
		FacesContext.getCurrentInstance().getExternalContext().getFlash()
		.setKeepMessages(true);
		try {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/szczegolyrezerwacji.xhtml?rez="
									+ this.rezerwacja.getId_rezerwacji());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else 
		{
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Podaj date odbioru", 3);
		}
	}

	private String setNumerRezerwacji(int id) {
		Integer i = id;
		i += 1;
		String numerKlienta = "";
		if (i < 10)
			numerKlienta = "R0/000" + i.toString();
		if (i >= 10 && i < 100)
			numerKlienta = "R0/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerKlienta = "R0/0" + i.toString();
		if (i >= 1000)
			numerKlienta = "R0/" + i.toString();
		return numerKlienta;
	}

	/**
	 * @return the rezerwacja
	 */
	public Rezerwacje getRezerwacja() {
		return rezerwacja;
	}

	/**
	 * @param rezerwacja
	 *            the rezerwacja to set
	 */
	public void setRezerwacja(Rezerwacje rezerwacja) {
		this.rezerwacja = rezerwacja;
	}

	/**
	 * @return the pracownik
	 */
	public Pracownicy getPracownik() {
		return pracownik;
	}

	/**
	 * @param pracownik
	 *            the pracownik to set
	 */
	public void setPracownik(Pracownicy pracownik) {
		this.pracownik = pracownik;
	}

	/**
	 * @return the selectedFilmy
	 */
	public List<Filmy> getSelectedFilmy() {
		return selectedFilmy;
	}

	/**
	 * @param selectedFilmy
	 *            the selectedFilmy to set
	 */
	public void setSelectedFilmy(List<Filmy> selectedFilmy) {
		this.selectedFilmy = selectedFilmy;
	}

	/**
	 * @return the klient
	 */
	public Klienci getKlient() {
		return klient;
	}

	/**
	 * @param klient
	 *            the klient to set
	 */
	public void setKlient(Klienci klient) {
		this.klient = klient;
	}

	/**
	 * @return the kopiefilmu
	 */
	public List<KopieFilmu> getKopiefilmu() {
		return kopiefilmu;
	}

	/**
	 * @param kopiefilmu
	 *            the kopiefilmu to set
	 */
	public void setKopiefilmu(List<KopieFilmu> kopiefilmu) {
		this.kopiefilmu = kopiefilmu;
	}
}
