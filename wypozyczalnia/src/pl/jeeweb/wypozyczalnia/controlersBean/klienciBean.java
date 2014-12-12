package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;

import org.primefaces.event.FlowEvent;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.RandomAlphaNum;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;
import pl.jeeweb.wypozyczalnia.tools.SendMail;

@ManagedBean(name = "klienciBean")
@RequestScoped
public class klienciBean {
	private Klienci klient = new Klienci();

	public Klienci getKlient() {
		return klient;
	}

	public void setKlient(Klienci klient) {
		this.klient = klient;
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

	public void save(ActionEvent ae) {
		if (!ifExistEmail(this.klient.getE_mail())) {
			RandomAlphaNum rand = new RandomAlphaNum();
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			this.klient.setNr_klienta(setNumerKlienta());
			this.klient.setData_rejestracji(currentDate());
			String tmpPassword = rand.RandomPass();
			this.klient.setHaslo(SHA256hash.HashText(tmpPassword));
			EntityManager em = DBManager.getManager().createEntityManager();
			em.getTransaction().begin();
			em.persist(this.klient);
			em.getTransaction().commit();
			em.close();

			try {
				new SendMail(
						this.klient.getE_mail(),
						"Rejestracja w serwisie Wypo¿yczalnia DVD",
						this.klient.getImie()
								+ " witaj  w serwisie Wypo¿yczalniaDVD. Twój e-mail jest loginem. \n "
								+ "Twoje has³o to: " + tmpPassword
								+ " Zmien je przy pierwszym logowaniu").send();
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				DisplayMessage
						.InfoMessage(
								FacesContext.getCurrentInstance(),
								"text-message",
								"Uzytkownik zarejestrowany. Has³o pierwszego logowania zosta³o wys³ane w e-mailu podanym przy rejestracji",
								2);
				context.getFlash().setKeepMessages(true);
				context.redirect(context.getRequestContextPath()
						+ "/zaloguj.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage",
					"Niepoprawny E-mail. E-mail istnie juz w bazie", 3);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),"emailer", "b³¹d", 3);
		}

	}

	public boolean ifExistEmail(String email) {
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

//	public String flowListener(FlowEvent event) {
//		String returned = event.getNewStep();
//		System.out.println("Flow Event Happened :: New Step :: "
//				+ event.getNewStep() + " :: Old Step :: " + event.getOldStep());
//		if (event.getNewStep().equals(new String("confirm"))) {
//			if (ifExistEmail(this.klient.getE_mail())) {
//				InfoMessage("E-mail istnieje");
//				returned = event.getOldStep();
//			} else {
//				returned = event.getNewStep();
//			}
//		}
//		return returned;
//	}

}
