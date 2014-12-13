package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
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

@ManagedBean(name = "FilmyBean")
@RequestScoped
public class FilmyBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

}
