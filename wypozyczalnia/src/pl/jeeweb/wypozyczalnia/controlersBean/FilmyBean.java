package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;

@ManagedBean(name = "FilmyBean")
@RequestScoped
public class FilmyBean implements Serializable {
	
	@ManagedProperty("#{param.id_filmu}")
	private int id;
	
	private Filmy filmSzczegoly;
	
	public FilmyBean() {

	}

	public List<Filmy> getListaFilmy() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List resultList = em.createNamedQuery("Filmy.findAll").getResultList();
		List<Filmy> list = resultList;
		em.close();
		return list;
	}
	public List<Filmy> getTop5filmy() {

		EntityManager em = DBManager.getManager().createEntityManager();

		List<Filmy> top5ListaFilmy = (List<Filmy>) em.createQuery(
				"Select f from Filmy f where Data_dodania like '2014-12-%'")
				.getResultList();

		return top5ListaFilmy;

	}
	
	@PostConstruct
	public void getFilmById() {
		if(id!=0) {
		EntityManager em = DBManager.getManager().createEntityManager();
		filmSzczegoly = em.find(Filmy.class, id);
		em.close();
		filmSzczegoly.getKlasyfikacjaGatunkus().size();
		}
		System.out.print("nicd");
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

}
