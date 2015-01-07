package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "FilmyBean")
@RequestScoped
public class FilmyBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Filmy> filteredFilmy = new ArrayList<>();
	private Filmy selectedFilm = null;

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
		em.close();

		return top5ListaFilmy;

	}

	public String klasyfikacjaGatunkuToString(Filmy film) {
		String gatunek = "";
		for (KlasyfikacjaGatunku gatunekFilmu : film.getKlasyfikacjaGatunkus()) {
			gatunek = gatunek + " " + gatunekFilmu.getGatunek();
		}

		return gatunek;
	}

	public String iloscKopiiFilmu(Filmy film) {
		EntityManager em = DBManager.getManager().createEntityManager();
		Filmy filmId = new Filmy();
		filmId = em.find(Filmy.class, film.getId_filmu());
		// Filmy filmId = (Filmy)
		// em.createNamedQuery("Filmy.findById").setParameter("idFilmu",
		// film.getId_filmu()).getSingleResult();
		String size = Integer.toString(filmId.getKopieFilmus().size());
		em.close();
		return size;

	}

	public void przekierowanieEdycjaFilmu() {
		try {
			if(selectedFilm == (null)) 
			{
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Nie wybra³es filmu!!!", 3);

			}
				else {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/editFilmy.xhtml?id_filmu="
									+ selectedFilm.getId_filmu());
			}
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Nie wybra³es filmu!!!", 3);
		}
	}
	public void przekierowanieDodajFilm() {
		try {
			
				
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/addFilm.xhtml");
			
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			
		}
	}
	public void przekierowanieSzczegoly(int id) {
		try {

			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/szczegolyFilmu.xhtml?id_filmu="
									+ id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Filmy> getFiletredFilmy() {
		return filteredFilmy;
	}

	public void setFiletredFilmy(List<Filmy> filtredFilmy) {
		this.filteredFilmy = filtredFilmy;
	}

	public Filmy getSelectedFilm() {
		return selectedFilm;
	}

	public void setSelectedFilm(Filmy selectedFilm) {
		this.selectedFilm = selectedFilm;
	}

}
