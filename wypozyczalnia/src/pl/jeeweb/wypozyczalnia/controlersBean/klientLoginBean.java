package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "LoginBean")
@SessionScoped
public class klientLoginBean implements Serializable {
	private Klienci klient;
	private boolean islogged = false;

	public klientLoginBean() {
	}

	public void LogIn() {

		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put("user_id", this.klient.getId_klienta());
		FacesContext.getCurrentInstance().getExternalContext()
				.setSessionMaxInactiveInterval(10 * 60);
		try {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Zalogowano pomyœlnie", 1);
			FacesContext.getCurrentInstance().getExternalContext().getFlash()
					.setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/wypozyczalnia/katalogFilmow.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String LogOut() {
		islogged = false;

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
		FacesContext.getCurrentInstance().getExternalContext()
				.invalidateSession();
		klient = new Klienci();
		return "";
	}

	// ----------------------------------------------------
	// Getery i setery

	public boolean isIslogged() {
		return islogged;
	}

	public void setIslogged(boolean islogged) {
		this.islogged = islogged;
	}

	public Klienci getKlient() {
		return klient;
	}

	public void setKlient(Klienci klient) {
		this.klient = klient;
	}

}
