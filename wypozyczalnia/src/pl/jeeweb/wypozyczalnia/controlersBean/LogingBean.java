package pl.jeeweb.wypozyczalnia.controlersBean;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;

@ManagedBean(name = "LogingBean")
@RequestScoped
public class LogingBean {
	private String Email;
	private String Haslo;
	private Klienci klient = new Klienci();
	private klientLoginBean klBean;
	private boolean islogin=false;

	public void readCookieMessage() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		Cookie cok[] = request.getCookies();
		for (Cookie c : cok) {
			if (c.getName().equals(new String("message"))) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", c.getValue(), 3);
			}
		}
	}

	public void LogIn(ActionEvent ae) {
		EntityManager em = DBManager.getManager().createEntityManager();
		List lista = em
				.createNativeQuery(
						"Select * from klienci  where e_mail like '"
								+ this.Email + "'", Klienci.class)
				.getResultList();
		em.close();
		if (lista.size() != 0) {
			klient = (Klienci) lista.get(0);

			String hashweb = SHA256hash.HashText(this.Haslo);
//			System.out.print(hashweb + "\n \n" + "baza ");
//			System.out.print(klient.getHaslo());
//
			if (hashweb.equals(klient.getHaslo())) {

				klBean = new klientLoginBean();
				klBean.setKlient(klient);
				klBean.LogIn();

			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "B³êdny login lub has³o", 2);
				klient = new Klienci();
			}
		}

		else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "B³êdny e-mail. Zarejestruj siê!", 2);
			klient = new Klienci();
		}

	}

	public boolean isUserLoged() {
		Map<String, Object> mapasesji = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		
		if (!mapasesji.containsKey("user_id"))
			return false;
		else
			return true;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getHaslo() {
		return Haslo;
	}

	public void setHaslo(String haslo) {
		Haslo = haslo;
	}

	public Klienci getKlient() {
		return klient;
	}

	public void setKlient(Klienci klient) {
		this.klient = klient;
	}
}
