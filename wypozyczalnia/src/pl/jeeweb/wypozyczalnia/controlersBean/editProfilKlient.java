package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaUpdate;
import javax.servlet.http.HttpSession;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.User;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;

@ManagedBean(name = "editBean")
@SessionScoped
public class editProfilKlient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Klienci klient = new Klienci();
	private User user = new User();

	private String stareHaslo;
	private String noweHaslo1;
	private String noweHaslo2;
	private boolean zaladowanodoedycji = false;

	public editProfilKlient() {

	}
	

	public String Zaladujdoedycji() {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		int user_id = (int) session.getAttribute("user_id");
		EntityManager em = DBManager.getManager().createEntityManager();
		this.klient = em.find(Klienci.class, user_id);
		// this.user = (User) em.createNamedQuery("User.findByEmail")
		// .setParameter("email", klient.getE_mail()).getSingleResult();
		em.close();
		this.zaladowanodoedycji = true;

		return "";
	}

	public String editOsobowe() {

		EntityManager em = DBManager.getManager().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.merge(this.klient);
		tx.commit();
		em.close();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Daneosobowe zapisane", 1);
		return null;
	}

	public String edithaslo() {

		if (!zaladowanodoedycji)
			Zaladujdoedycji();
		String hashhaslo = SHA256hash.HashText(this.stareHaslo);
		// System.out.println("starehas³o  " + hashhaslo);
		// System.out.println("has³ozbazy" + this.klient.getHaslo());
		
		if (hashhaslo.equals(this.klient.getHaslo())) {
			if (this.noweHaslo1.equals(this.noweHaslo2)) {
				klient.setHaslo(SHA256hash.HashText(this.noweHaslo1));
				user.setUsername(this.klient.getE_mail());
				user.setPassword(SHA256hash.HashText(this.noweHaslo1));
				EntityManager em = DBManager.getManager().createEntityManager();
				em.getTransaction().begin();
				em.merge(klient);
				em.merge(user);
				em.getTransaction().commit();
				em.close();

				this.noweHaslo1 = "";
				this.noweHaslo2 = "";
				this.stareHaslo = "";
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Has³o zmienione pomyœlnie", 1);
				FacesContext.getCurrentInstance().getExternalContext()
						.getFlash().setKeepMessages(true);
				try {
					FacesContext.getCurrentInstance().getExternalContext()
							.redirect("/wypozyczalnia/Users/zmianahasla.xhtml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return "";
			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nowe has³a nie s¹ identyczne", 2);

			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Twoje stare has³o nie jest poprawne", 2);
		}
		return null;

	}

	public Klienci getKlient() {
		return klient;
	}

	public void setKlient(Klienci klient) {
		this.klient = klient;
	}

	public String getStareHaslo() {
		return stareHaslo;
	}

	public void setStareHaslo(String stareHaslo) {
		this.stareHaslo = stareHaslo;
	}

	public String getNoweHaslo1() {
		return noweHaslo1;
	}

	public void setNoweHaslo1(String noweHaslo1) {
		this.noweHaslo1 = noweHaslo1;
	}

	public String getNoweHaslo2() {
		return noweHaslo2;
	}

	public void setNoweHaslo2(String noweHaslo2) {
		this.noweHaslo2 = noweHaslo2;
	}

}
