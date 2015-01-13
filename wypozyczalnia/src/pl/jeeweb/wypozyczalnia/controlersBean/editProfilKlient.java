package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaUpdate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.parser.Entity;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.User;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;

@ManagedBean(name = "editBean")
@ViewScoped
public class editProfilKlient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Klienci klient = new Klienci();
	private User user = new User();

	private Klienci selectedKlient = null;

	private String stareHaslo;
	private String noweHaslo1;
	private String noweHaslo2;
	private boolean zaladowanodoedycji = false;
	private int id = 0;
	private String target = "";

	public editProfilKlient() {

	}

	@PostConstruct
	public void Zaladujdoedycji() {

		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String, String> paramMap = ctx.getExternalContext()
				.getRequestParameterMap();
		HttpServletRequest servletRequest = (HttpServletRequest) ctx
				.getExternalContext().getRequest();
		String fullURI = servletRequest.getRequestURI();
		Iterator<String> paramIter = ctx.getExternalContext()
				.getRequestParameterNames();
		ArrayList<String> paramListNames = new ArrayList<>();
		while (paramIter.hasNext()) {
			paramListNames.add(paramIter.next());
		}
		if (fullURI.equals("/wypozyczalnia/Zarzadzanie/editKlient.xhtml")) {
			if (paramListNames.contains("id_klient")) {
				id = Integer.valueOf(paramMap.get("id_klient"));
			}
			if (paramListNames.contains("target")) {
				target = paramMap.get("target");
			}

		} else {
			HttpSession session = (HttpSession) FacesContext
					.getCurrentInstance().getExternalContext().getSession(true);
			id = (int) session.getAttribute("user_id");
		}
		EntityManager em = DBManager.getManager().createEntityManager();
		this.klient = em.find(Klienci.class, id);
		// this.user = (User) em.createNamedQuery("User.findByEmail")
		// .setParameter("email", klient.getE_mail()).getSingleResult();
		em.close();
		this.zaladowanodoedycji = true;

	}

	public String editOsobowe() {

		EntityManager em = DBManager.getManager().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.merge(this.klient);

		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Daneosobowe zapisane", 1);
		if (!target.isEmpty()) {
			this.klient.setAktywowany("TAK");
			em.merge(this.klient);

			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "To¿samoœæ klienta potwierdzona", 1);
			FacesContext.getCurrentInstance().getExternalContext().getFlash()
					.setKeepMessages(true);
			try {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("/wypozyczalnia" + target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tx.commit();
		em.close();
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
