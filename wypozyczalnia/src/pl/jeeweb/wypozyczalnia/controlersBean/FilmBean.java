package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.LazyFilmDataModel;

@ManagedBean(name = "FilmBean")
@ViewScoped
public class FilmBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Filmy> filteredFilmy = new ArrayList<>();
	private Filmy selectedFilm = null;
	private LazyDataModel<Filmy> lazyModel;
	private List<Filmy> allFilmy;

	public FilmBean() {

	}
	
	@PostConstruct
	private void init() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List resultList = em.createNamedQuery("Filmy.findAll").getResultList();
		allFilmy = resultList;
		em.close();
		for(Filmy film : allFilmy) {
			film.setGatunek_string(klasyfikacjaGatunkuToString(film));
		}
		lazyModel = new LazyFilmDataModel(allFilmy);
	}
	public List<Filmy> getListaFilmy() {
		
		
		return allFilmy;
	}

	public void onRowSelect(SelectEvent event) {
		przekierowanieSzczegoly(this.selectedFilm.getId_filmu());

	}

	public List<Filmy> getTop5filmy() {
		
		EntityManager em = DBManager.getManager().createEntityManager();

		List<Filmy> top5ListaFilmy = (List<Filmy>) em.createQuery(
				"Select f from Filmy f where Data_dodania like '2015-01-%'")
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
		filmId = (Filmy) em.createNamedQuery("Filmy.findById").setParameter("idFilmu", film.getId_filmu()).getSingleResult();
		// Filmy filmId = (Filmy)
		// em.createNamedQuery("Filmy.findById").setParameter("idFilmu",
		// film.getId_filmu()).getSingleResult();
		
		String size = Integer.toString(filmId.getKopieFilmus().size());
		
		int licznik = 0;
		for (KopieFilmu kopia : filmId.getKopieFilmus()) {
			if (kopia.getRezerwacje() == null
					&& kopia.getWypozyczenia() == null) {
				licznik++;
			}
		}
		em.close();
		return licznik+"/"+size;

	}

	public void przekierowanieEdycjaFilmu() {
		try {
			if (selectedFilm == (null)) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie wybra³es filmu!!!", 3);

			} else {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/editFilmy.xhtml?id_filmu="
										+ selectedFilm.getId_filmu());
			}
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³es filmu!!!", 3);
		}
	}

	public void przekierowanieDodajFilm() {
		try {

			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("/wypozyczalnia/Zarzadzanie/addFilm.xhtml");

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

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<Filmy> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<Filmy> lazyModel) {
		this.lazyModel = lazyModel;
	}

}
