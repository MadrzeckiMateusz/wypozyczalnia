package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;

@ManagedBean(name = "FilmDetailsBean")
@ViewScoped
public class FilmDetailsBean implements Serializable {

	private int id;

	private Filmy filmSzczegoly = new Filmy();
	private List<Filmy> listszczegolyfilm = new ArrayList<>();
	private List<Filmy> filmyWedlugGatunkow = new ArrayList<>();
	private List<Filmy> filmyProponowane = new ArrayList<>();

	private String gatunek = " ";

	@PostConstruct
	public void getFilmById() {
		id = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("id_filmu"));
		if (id != 0) {
			EntityManager em = DBManager.getManager().createEntityManager();
			filmSzczegoly = em.find(Filmy.class, id);
			em.close();
			klasyfikacjaGatunkuToString();
			znajdzFilmyPoGatunkach();
			if (isListaProponowanychFilmow()) {
				this.setFilmyProponowane(losujFilmy(this.filmyWedlugGatunkow));
				this.listszczegolyfilm.add(filmSzczegoly);
			}
		}

	}

	public boolean isListaProponowanychFilmow() {
		return !this.filmyWedlugGatunkow.isEmpty();
	}

	private void znajdzFilmyPoGatunkach() {
		this.filmyWedlugGatunkow = filmyWedlugGatunkow(filmSzczegoly
				.getKlasyfikacjaGatunkus());
	}

	private List<Filmy> filmyWedlugGatunkow(List<KlasyfikacjaGatunku> gatunki) {
		List<Filmy> podobnePropozycje = new ArrayList<>();
		for (KlasyfikacjaGatunku gatunek : gatunki) {
			EntityManager em = DBManager.getManager().createEntityManager();
			List<KlasyfikacjaGatunku> filmyZgatunku = em
					.createNamedQuery("KlasyfikacjaGatunku.getbygatunek")
					.setParameter("gatunek", gatunek.getGatunek())
					.getResultList();
			em.close();
			for (KlasyfikacjaGatunku gatunekfilmu : filmyZgatunku) {
				for (Filmy filmPodobne : gatunekfilmu.getFilmies()) {
					if (filmSzczegoly.compareTo(filmPodobne) == -1
							&& !podobnePropozycje.contains(filmPodobne)) {
						podobnePropozycje.add(filmPodobne);
					}
				}
			}
		}
		return podobnePropozycje;
	}

	private void klasyfikacjaGatunkuToString() {

		for (KlasyfikacjaGatunku gatunekFilmu : this.filmSzczegoly
				.getKlasyfikacjaGatunkus()) {
			this.gatunek = this.gatunek + " " + gatunekFilmu.getGatunek();
		}
	}

	private List<Filmy> losujFilmy(List<Filmy> listafilmow) {
		List<Filmy> filmyDolosowania = new ArrayList<>(listafilmow);
		Random r = new Random();
		List<Filmy> filmyWylosowane = new ArrayList<>();
		for (int i = 0; i <= 3; i++) {
			Filmy film = filmyDolosowania
					.get(r.nextInt(filmyDolosowania.size()));
			if (film.compareTo(filmSzczegoly) == -1
					&& !filmyWylosowane.contains(film)) {
				filmyWylosowane.add(film);
			} else {

			}
		}
		return filmyWylosowane;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Filmy getFilmSzczegoly() {
		return filmSzczegoly;
	}

	public void setFilmSzczegoly(Filmy filmSzczegoly) {
		this.filmSzczegoly = filmSzczegoly;
	}

	public String getGatunek() {
		return gatunek;
	}

	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}

	public List<Filmy> getFilmyProponowane() {
		return filmyProponowane;
	}

	public void setFilmyProponowane(List<Filmy> filmyProponowane) {
		this.filmyProponowane = filmyProponowane;
	}

	public List<Filmy> getListszczegolyfilm() {
		return listszczegolyfilm;
	}

	public void setListszczegolyfilm(List<Filmy> listszczegolyfilm) {
		this.listszczegolyfilm = listszczegolyfilm;
	}
}
