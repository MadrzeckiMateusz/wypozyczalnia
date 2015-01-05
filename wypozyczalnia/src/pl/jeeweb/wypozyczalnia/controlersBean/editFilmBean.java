package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
@ManagedBean(name="editFilmBean")
@ViewScoped
public class editFilmBean implements Serializable {
	
	private Filmy film;
	private int id;
	private String gatunek;

	@PostConstruct
	public void getFilmById() {
		id = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("id_filmu"));
		if (id != 0) {
			EntityManager em = DBManager.getManager().createEntityManager();
			film = em.find(Filmy.class, id);
			em.close();
			klasyfikacjaGatunkuToString();
			
			
			}
		}
	
	private void klasyfikacjaGatunkuToString() {

		for (KlasyfikacjaGatunku gatunekFilmu : this.film
				.getKlasyfikacjaGatunkus()) {
			this.gatunek = this.gatunek + " " + gatunekFilmu.getGatunek();
		}
	}
	

	public Filmy getFilm() {
		return film;
	}

	public String getGatunek() {
		return gatunek;
	}

	public void setFilm(Filmy film) {
		this.film = film;
	}

	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}

}
