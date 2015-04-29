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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Wypozyczenia;
import pl.jeeweb.wypozyczalnia.tools.DateTools;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SendMail;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "WypozyczeniaAddNewBean")
@ViewScoped
public class WypozyczeniaAddNewBean implements Serializable {

	public WypozyczeniaAddNewBean() {

	}

	private Wypozyczenia wypozyczenie = new Wypozyczenia();
	private Pracownicy pracownik = new Pracownicy();
	private List<Filmy> selectedFilmy;
	private Klienci klient;
	private List<KopieFilmu> kopiefilmu;

	@PostConstruct
	public void initBean() {
		klient = null;
		kopiefilmu = new ArrayList<>();
		EntityManager em = DBManager.getManager().createEntityManager();
		int maxID = (int) em.createNamedQuery("Wypozyczenia.getMaxId")
				.getSingleResult();
		this.wypozyczenie.setNr_wypozyczenia(setNumerWypozyczenia(maxID));
		this.wypozyczenie.setData_wypozyczenia(DateTools.currentDate());
		this.wypozyczenie.setTermin_zwrotu(DateTools.nextDate(2));
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		int id = (int) session.getAttribute("pracownik_id");

		this.pracownik = (Pracownicy) em
				.createNamedQuery("Pracownicy.findByid").setParameter("id", id)
				.getSingleResult();
		this.wypozyczenie.setPracownicy(pracownik);

		em.close();

	}

	public void dodajFilmy() {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();

		if (!selectedFilmy.isEmpty()) {
			for (Filmy film : selectedFilmy) {
				Filmy film1 = (Filmy) em.createNamedQuery("Filmy.findById")
						.setParameter("idFilmu", film.getId_filmu())
						.getSingleResult();
				List<KopieFilmu> kopiaTmp = new ArrayList<>();
				for (KopieFilmu kopia : film1.getKopieFilmus()) {
					if (kopia.getRezerwacje() == null
							&& kopia.getWypozyczenia() == null) {

						kopiaTmp.add(kopia);
					}
				}
				if (kopiaTmp.size() > 0) {
					this.kopiefilmu.add(kopiaTmp.get(0));
				} else {
					DisplayMessage.InfoMessage(
							FacesContext.getCurrentInstance(), "globalmessage",
							"Brak kopii filmów!!!", 3);
				}
			}

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
			this.wypozyczenie.setKlienci(klient);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Klient dodany", 1);
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ kllienta", 3);
		}
	}

	public void zapiszWypozyczenie() {

		if (!this.kopiefilmu.isEmpty()) {
			if (this.klient != null) {
				if (wypozyczenie.getKlienci().getAktywowany().equals("NIE")) {
					RequestContext context = RequestContext
							.getCurrentInstance();
					context.execute("PF('DialogDaneKlient').show();");
				} else {
					dokonajWypozyczeniaZmianydoBazy();
				}
			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie doda³eœ klienta", 3);
			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie doda³eœ filmów", 3);
		}

	}

	private void dokonajWypozyczeniaZmianydoBazy() {
		if (this.wypozyczenie.getTermin_zwrotu() != null) {
			String historia = "";
			for (KopieFilmu kf : this.kopiefilmu) {
				historia = historia + kf.getFilmy().getId_filmu() + ";";
			}
			this.wypozyczenie.setHistoria_wypo(historia);

			EntityManager em = DBManager.getManager().createEntityManager();
			em.getTransaction().begin();
			this.wypozyczenie = em.merge(this.wypozyczenie);
			em.getTransaction().commit();

			for (KopieFilmu kf : this.kopiefilmu) {
				kf.setWypozyczenia(this.wypozyczenie);
				em.getTransaction().begin();
				em.merge(kf);
				em.getTransaction().commit();

			}

			em.close();
			wyslijPowiadomienieWypozyczenia();
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Wypo¿yczenie zrealizowane", 1);
			FacesContext.getCurrentInstance().getExternalContext().getFlash()
					.setKeepMessages(true);
			try {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/szczegolyWypozyczenia.xhtml?wypo="
										+ this.wypozyczenie
												.getId_wypozyczenia());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "podaj termin zwrotu", 3);
		}
	}

	public void wyslijPowiadomienieWypozyczenia() {

		SendMail mail = new SendMail(
				klient.getE_mail(),
				"Wpozyczenie numer " + this.wypozyczenie.getNr_wypozyczenia(),
				"Witaj, "
						+ klient.getImie()+ "!!! \n"
						+ "Wypozyczenie zosta³o zrealizowane. Dziêkujemy za skorzystanie z naszych us³ug. Termin zwrotu: " 
						+ this.wypozyczenie.getTermin_zwrotu().toString()+".\n"																
								+ "---------------------------------------------------------------------------------------- \n"
								+ "www.wypozyczalniadvd.com.pl \n"
								+ "tel.555 555 555 \n"
								+ "dvd.wpozyczalnia@gmail.com");
		try {
			mail.send();
		} catch (MessagingException e) {
			System.out.print("B³ad wysy³ania maila");
			e.printStackTrace();
		}
	}

	private String setNumerWypozyczenia(int id) {
		Integer i = id;
		i += 1;
		String numerKlienta = "";
		if (i < 10)
			numerKlienta = "W0/000" + i.toString();
		if (i >= 10 && i < 100)
			numerKlienta = "W0/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerKlienta = "W0/0" + i.toString();
		if (i >= 1000)
			numerKlienta = "W0/" + i.toString();
		return numerKlienta;
	}

	/**
	 * @return the wypozyczenie
	 */
	public Wypozyczenia getWypozyczenie() {
		return wypozyczenie;
	}

	/**
	 * @param wypozyczenie
	 *            the wypozyczenie to set
	 */
	public void setWypozyczenie(Wypozyczenia wypozyczenie) {
		this.wypozyczenie = wypozyczenie;
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
