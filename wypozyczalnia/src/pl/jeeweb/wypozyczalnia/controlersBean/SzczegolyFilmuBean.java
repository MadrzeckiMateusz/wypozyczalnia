package pl.jeeweb.wypozyczalnia.controlersBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;

@ManagedBean(name = "SzczegolyFilmu")
@RequestScoped
public class SzczegolyFilmuBean {
	
	@ManagedProperty("#{param.id_filmu}")
	private int id;

	private Filmy filmSzczegoly = new Filmy();

	private List<Filmy> proponowaneFilmy = new ArrayList<>();

	private String gatunek = " ";

	@PostConstruct
	public void getFilmById() {
		if (id != 0) {
			EntityManager em = DBManager.getManager().createEntityManager();
			filmSzczegoly = em.find(Filmy.class, id);
			em.close();
			klasyfikacjaGatunkuToString();
			findProponowaneFilmy();
		}
		System.out.print("huj ");
	}

	private void findProponowaneFilmy() {
		List<Filmy> filmy = filmyWedlugGatunkow(filmSzczegoly
				.getKlasyfikacjaGatunkus());
		this.proponowaneFilmy = losujFilmy(filmy);

	}

	public boolean isProponowanyFilm() {
		return !this.proponowaneFilmy.isEmpty();
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
			Filmy film = filmyDolosowania.get(r.nextInt(filmyDolosowania.size()));
			if (film.compareTo(filmSzczegoly) == -1
					&& !filmyWylosowane.contains(film)) {
				filmyWylosowane.add(film);
			} else {
//				if (filmyDolosowania.size()  3) {
//					i--;
//				}
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

	public List<Filmy> getProponowaneFilmy() {
		return proponowaneFilmy;
	}

	public void setProponowaneFilmy(List<Filmy> proponowaneFilmy) {
		this.proponowaneFilmy = proponowaneFilmy;
	}

	public String getGatunek() {
		return gatunek;
	}

	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}
}
