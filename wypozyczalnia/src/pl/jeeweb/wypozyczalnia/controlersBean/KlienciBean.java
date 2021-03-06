package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionalException;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.Role;
import pl.jeeweb.wypozyczalnia.entity.User;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.RandomAlphaNum;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;
import pl.jeeweb.wypozyczalnia.tools.SendMail;

@ManagedBean(name = "KlienciBean")
@RequestScoped
public class KlienciBean {
	private Klienci klient = new Klienci();
	private User user = new User();
	private Role user_role = new Role();

	private Klienci selectedKlient = null;
	private List<Klienci> filteredKlienci = new ArrayList<>();

	public Klienci getKlient() {
		return klient;
	}

	public void setKlient(Klienci klient) {
		this.klient = klient;
	}

	@PostConstruct
	public void initBean() {
		this.klient.setNr_klienta(setNumerKlienta());
		this.klient.setData_rejestracji(currentDate());
	}

	public List<Klienci> getAllKlienci() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List<Klienci> klienci = em.createNamedQuery("Klienci.findAll")
				.getResultList();
		em.close();
		return klienci;
	}

	public String setNumerKlienta() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List<Integer> id = em.createNativeQuery(
				"Select id_klienta from klienci").getResultList();
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
			numerKlienta = "KL/000" + i.toString();
		if (i >= 10 && i < 100)
			numerKlienta = "KL/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerKlienta = "KL/0" + i.toString();
		if (i >= 1000)
			numerKlienta = "KL/" + i.toString();
		return numerKlienta;
	}

	public Date currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();

		return date;
	}

	public void save() {
		if (!ifExistEmail(this.klient.getE_mail())) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletRequest servletRequest = (HttpServletRequest) ctx
					.getExternalContext().getRequest();
			String fullURI = servletRequest.getRequestURI();
			System.out.print(fullURI);
			RandomAlphaNum rand = new RandomAlphaNum();
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();

			String tmpPassword = rand.RandomPass();
			this.klient.setHaslo(SHA256hash.HashText(tmpPassword));
			this.klient.setAktywowany("NIE");

			this.user.setUsername(this.klient.getE_mail());
			this.user.setPassword(this.klient.getHaslo());

			this.user_role.setRolename("klient");
			this.user_role.setUsername(this.klient.getE_mail());
			EntityManager em = DBManager.getManager().createEntityManager();
			try {
				em.getTransaction().begin();
				em.persist(this.klient);
				em.persist(this.user);
				em.persist(this.user_role);
				em.getTransaction().commit();
				em.close();
			} catch (TransactionalException e) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "B��d danych", 3);
			}

			try {

				new SendMail(
						this.klient.getE_mail(),
						"Rejestracja w serwisie Wypo�yczalnia DVD",
						"Witaj, "
								+ this.klient.getImie()
								+ "!!! \n"
								+ "Witaj  w serwisie Wypo�yczalniaDVD. Tw�j e-mail jest loginem. \n "
								+ "Twoje has�o to: "
								+ tmpPassword
								+ "\n"
								+ "Zmien has�o przy pierwszym logowaniu"
								+ "---------------------------------------------------------------------------------------- \n"
								+ "www.wypozyczalniadvd.com.pl \n"
								+ "tel.555 555 555 \n"
								+ "dvd.wpozyczalnia@gmail.com").send();

			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				DisplayMessage
						.InfoMessage(
								FacesContext.getCurrentInstance(),
								"text-message",
								"Uzytkownik zarejestrowany. Has�o pierwszego logowania zosta�o wys�ane w e-mailu podanym przy rejestracji",
								2);
				context.getFlash().setKeepMessages(true);
				if (fullURI.equals("/wypozyczalnia/nowyuser.xhtml")) {

					context.redirect(context.getRequestContextPath()
							+ "/zaloguj.xhtml");
				} else if (fullURI.contains("editKlient")) {
					context.redirect(context.getRequestContextPath()
							+ "/Zarzadzanie/listaKlienci.xhtml");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage",
					"Niepoprawny E-mail. E-mail istnie juz w bazie", 3);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"emailer", "b��d", 3);
		}

	}

	public void przekierowanieEdycjaKlienta() {
		try {
			if (selectedKlient == (null)) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie wybra�es klienta!!!", 3);

			} else {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/editKlient.xhtml?id_klient="
										+ selectedKlient.getId_klienta()
										+ "&target=/Zarzadzanie/listaKlienci.xhtml");
			}
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra�es klienta!!!", 3);
		}
	}

	public void przekierowanieDodajKlienta() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/wypozyczalnia/Zarzadzanie/addKlient.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean ifExistEmail(String email) {
		EntityManager em = DBManager.getManager().createEntityManager();
		List results = null;
		results = em.createNativeQuery(
				"Select * from klienci where e_mail like '" + email + "'")
				.getResultList();
		em.close();

		if (!results.isEmpty())
			return true;// istnieje
		else
			return false;// nie istnieje
	}

	public Klienci getSelectedKlient() {
		return selectedKlient;
	}

	public void setSelectedKlient(Klienci selectedKlient) {
		this.selectedKlient = selectedKlient;
	}

	public List<Klienci> getFilteredKlienci() {
		return filteredKlienci;
	}

	public void setFilteredKlienci(List<Klienci> filteredKlienci) {
		this.filteredKlienci = filteredKlienci;
	}

}
