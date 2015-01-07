package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;

@ManagedBean(name = "LogingBean")
@SessionScoped
public class LogingBean {
	private String username;
	private String password;
	private Klienci klient = new Klienci();
	private boolean islogin = false;
	private boolean adminRole = false; 

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
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		try {
			request.getSession();
			request.login(this.username, this.password);
			addLoginKlientToSession(request);
			islogin= true;

		} catch (ServletException e) {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "B³êdny login lub has³o", 2);
			System.out.print(e);
		}

	}

	private void addLoginKlientToSession(HttpServletRequest request) {
		EntityManager em = DBManager.getManager().createEntityManager();
		List lista = em.createNativeQuery(
				"Select * from klienci  where e_mail like '" + this.username
						+ "'", Klienci.class).getResultList();
		em.close();
		if (lista.size() != 0) {
			klient = (Klienci) lista.get(0);
		}
		Map<String, Object> sessiondate = new HashMap<>();
		sessiondate.put("user_id", this.klient.getId_klienta());
		

		
		FacesContext.getCurrentInstance().getExternalContext()
				.setSessionMaxInactiveInterval(30 * 60);
		try {
			if(request.isUserInRole("klient")) {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Zalogowano pomyœlnie", 1);
			FacesContext.getCurrentInstance().getExternalContext().getFlash()
					.setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/wypozyczalnia/katalogFilmow.xhtml");
			sessiondate.put("role-name", "klient");}
			else if (request.isUserInRole("pracownik")) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Zalogowano pomyœlnie", 1);
				FacesContext.getCurrentInstance().getExternalContext().getFlash()
						.setKeepMessages(true);
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("/wypozyczalnia/Zarzadzanie/listafilmow.xhtml");
				sessiondate.put("role-name", "pracownik");
			}
			else if ( request.isUserInRole("admin")) {
				adminRole = true;
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Zalogowano pomyœlnie", 1);
				FacesContext.getCurrentInstance().getExternalContext().getFlash()
						.setKeepMessages(true);
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect("/wypozyczalnia/Zarzadzanie/zarzadzaj.xhtml");
				sessiondate.put("role-name", "admin");
				
			}
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
			.putAll(sessiondate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isAdminRole() {
		return adminRole;
	}
	public boolean isUserLoged() {
		Map<String, Object> mapasesji = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap();
		
		if (!mapasesji.containsKey("user_id"))
			return false;
		else
			return true;
	}

	public String LogOut() {
		islogin = false;
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		try {
			request.logout();
		} catch (ServletException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();
		klient = new Klienci();
		try {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Wylogowano pomyœlnie", 1);
			FacesContext.getCurrentInstance().getExternalContext().getFlash()
					.setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/wypozyczalnia/katalogFilmow.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String email) {
		username = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String haslo) {
		password = haslo;
	}

	public Klienci getKlient() {
		return klient;
	}

	public void setKlient(Klienci klient) {
		this.klient = klient;
	}

	public boolean isIslogin() {
		return islogin;
	}

	public void setIslogin(boolean islogin) {
		this.islogin = islogin;
	}
}
