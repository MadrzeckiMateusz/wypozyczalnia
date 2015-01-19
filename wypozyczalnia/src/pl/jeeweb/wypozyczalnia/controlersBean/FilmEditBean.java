package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.parser.Entity;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "FilmEditBean")
@ViewScoped
public class FilmEditBean implements Serializable {

	private Filmy film;
	private int id;

	private List<KlasyfikacjaGatunku> Gatunki;
	private List<String> GatunkiFilm = new ArrayList<>();
	private UploadedFile uploadedFile;

	@PostConstruct
	public void getFilmById() {
		
		id = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("id_filmu"));
		if (id != 0) {
			EntityManager em = DBManager.getManager().createEntityManager();
			film = em.find(Filmy.class, id);
			film.getKlasyfikacjaGatunkus().size();

			for (KlasyfikacjaGatunku gatunekFilmu : this.film
					.getKlasyfikacjaGatunkus()) {
				GatunkiFilm.add(Integer.toString(gatunekFilmu
						.getId_klasyfikacji()));
			}

			klasyfikacjaGatunkuAll();
			em.close();

		}
	}

	private void klasyfikacjaGatunkuAll() {
		EntityManager em = DBManager.getManager().createEntityManager();
		this.Gatunki = em.createNamedQuery("KlasyfikacjaGatunku.findAll")
				.getResultList();
		em.close();

	}

	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
		byte[] file = new byte[uploadedFile.getContents().length];
		file = uploadedFile.getContents();
		film.setPlakat(file);
		System.out.println("Powodzenie" + event.getFile().getFileName()
				+ " zosta³ za³adowny.");
		FacesMessage message = new FacesMessage("Powodzenie", event.getFile()
				.getFileName() + " zosta³ za³adowny.");
		FacesContext.getCurrentInstance().addMessage("globalmessage", message);
		RequestContext.getCurrentInstance().update("image1");
	}

	private void ustawZaznaczonegatunki() {
		this.film.getKlasyfikacjaGatunkus().clear();
		EntityManager em = DBManager.getManager().createEntityManager();
		List<KlasyfikacjaGatunku> listaKlasyfikacja = null;
		for (int i = 0; i < GatunkiFilm.size(); i++) {
			int id_kla = Integer.parseInt(GatunkiFilm.get(i));
			KlasyfikacjaGatunku klasyf = (KlasyfikacjaGatunku) em
					.createNamedQuery("KlasyfikacjaGatunku.getById")
					.setParameter("id", id_kla).getSingleResult();
			this.film.getKlasyfikacjaGatunkus().add(klasyf);
		}

		em.close();
	}

	public String zapiszZmiany() {
		ustawZaznaczonegatunki();
		System.out.println("wywo³any");
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		em.merge(this.film);
		em.getTransaction().commit();
		em.close();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Film zapisany pomyœlnie", 1);
		
		return null;
	}

	public Filmy getFilm() {
		return film;
	}

	public void setFilm(Filmy film) {
		this.film = film;
	}

	public List<KlasyfikacjaGatunku> getGatunki() {
		return Gatunki;
	}

	public List<String> getGatunkiFilm() {
		return GatunkiFilm;
	}

	public void setGatunki(List<KlasyfikacjaGatunku> gatunki) {
		Gatunki = gatunki;
	}

	public void setGatunkiFilm(List<String> gatunkiFilm) {
		GatunkiFilm = gatunkiFilm;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

}
