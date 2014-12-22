package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;


@ManagedBean(name = "RezerwacjeKlientaBean")
@RequestScoped
public class RezerwacjeKlientaBean implements Serializable{
	
	private Rezerwacje rezerwacje = new Rezerwacje();
	private List<Rezerwacje> rezerwacjebyid ;//= new ArrayList<>();
	private Rezerwacje zaznaczonaRezerwacja ;// = new Rezerwacje();
	private Klienci klient = new Klienci();
	public RezerwacjeKlientaBean(){
		this.rezerwacje = new Rezerwacje();
		this.rezerwacjebyid = new ArrayList<>();
		this.zaznaczonaRezerwacja = new Rezerwacje();
		
	}
	
	public  List<Rezerwacje> getklientbyid(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		int user_id = (int) session.getAttribute("user_id");
		List lista;
		EntityManager em = DBManager.getManager().createEntityManager();
		klient = (Klienci) em.createQuery("Select k from Klienci k where k.id_klienta=:id").setParameter("id", user_id).getSingleResult();		
        List<Rezerwacje> rezerwacje = klient.getRezerwacjes();
		em.close();
		 

		 return klient.getRezerwacjes();
		 
	}

	public Rezerwacje getZaznaczonaRezerwacja() {
		return zaznaczonaRezerwacja;
	}

	public void setZaznaczonaRezerwacja(Rezerwacje zaznaczonaRezerwacja) {
		this.zaznaczonaRezerwacja = zaznaczonaRezerwacja;
	}


	
}
