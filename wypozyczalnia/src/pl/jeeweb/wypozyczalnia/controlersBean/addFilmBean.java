package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.swing.text.html.parser.Entity;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.entity.Klasyfikator;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikatorPK;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmuPK;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "addFilmBean")
@ViewScoped
public class addFilmBean implements Serializable {

	private Filmy film;
	private List<KlasyfikacjaGatunku> Gatunki;
	private List<String> GatunkiFilm;
	private UploadedFile uploadedFile;
	private String iloscKopii;
	private String labelPlakat;

	@PostConstruct
	public void initBeanMethod() {
		klasyfikacjaGatunkuAll();
		this.film = new Filmy();
		this.film.setNr_filmu(setNumerFilmu());
		this.film.setData_dodania(currentDate());
		this.iloscKopii = "";
		this.labelPlakat = "";
		this.GatunkiFilm = new ArrayList<>();
	}

	public void dodajFilm() {

		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		Filmy film = em.merge(this.film);
		ustawZaznaczonegatunki(em, film);
		ustawKopieFilmu(em, film);
		em.getTransaction().commit();
		em.close();
		initBeanMethod();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Film dodany poprawnie", 1);
		
	}
	

	private void ustawKopieFilmu(EntityManager em, Filmy film) {

		int kopieInt = Integer.parseInt(iloscKopii);
		for (int i = 1; i <= kopieInt; i++) {
			KopieFilmu kopia = new KopieFilmu();
			KopieFilmuPK kopiefilmupk = new KopieFilmuPK();
			kopiefilmupk.setId_filmu(film.getId_filmu());
			kopiefilmupk.setId_kopii(i);
			kopia.setId(kopiefilmupk);
			em.merge(kopia);
		}

	}

	private void ustawZaznaczonegatunki(EntityManager em, Filmy film) {

		List<KlasyfikacjaGatunku> listaKlasyfikacja = null;
		for (int i = 0; i < GatunkiFilm.size(); i++) {
			int id_kla = Integer.parseInt(GatunkiFilm.get(i));
			KlasyfikatorPK kasyfikatorPk = new KlasyfikatorPK();
			kasyfikatorPk.setId_filmu(film.getId_filmu());
			kasyfikatorPk.setId_klasyfikacji(id_kla);
			Klasyfikator klasyfikator = new Klasyfikator();
			klasyfikator.setId(kasyfikatorPk);
			em.merge(klasyfikator);
		}

	}

	private void klasyfikacjaGatunkuAll() {
		EntityManager em = DBManager.getManager().createEntityManager();
		this.Gatunki = em.createNamedQuery("KlasyfikacjaGatunku.findAll")
				.getResultList();
		em.close();
	}

	public void handleFileUpload(FileUploadEvent event) {
		System.out.println("Powodzenie" + event.getFile().getFileName()
				+ " zosta³ za³adowny.");
		FacesMessage message = new FacesMessage("", event.getFile()
				.getFileName() + " zosta³ za³adowny.");
		FacesContext.getCurrentInstance().addMessage("globalmessage", message);
		uploadedFile = event.getFile();
		byte[] file = new byte[uploadedFile.getContents().length];
		file = uploadedFile.getContents();
		film.setPlakat(file);
		this.labelPlakat = uploadedFile.getFileName();
	
	}

	private Date currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();

		return date;
	}

	private String setNumerFilmu() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List<Integer> id = em.createNativeQuery(
				"Select f.id_filmu from filmy f").getResultList();
		em.close();
		List<Integer> id_int = new ArrayList<Integer>();
		Integer i;
		for (Integer k : id) {
			id_int.add(new Integer(k));
		}
		if (id_int.size() == 0)
			i = 0;
		else
			i = Collections.max(id_int);

		i += 1;
		String numerFilmu = "";
		if (i < 10)
			numerFilmu = "FM/000" + i.toString();
		if (i >= 10 && i < 100)
			numerFilmu = "FM/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerFilmu = "FM/0" + i.toString();
		if (i >= 1000)
			numerFilmu = "FM/" + i.toString();
		return numerFilmu;
	}

	public Filmy getFilm() {
		return film;
	}

	public List<KlasyfikacjaGatunku> getGatunki() {
		return Gatunki;
	}

	public List<String> getGatunkiFilm() {
		return GatunkiFilm;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setFilm(Filmy film) {
		this.film = film;
	}

	public void setGatunki(List<KlasyfikacjaGatunku> gatunki) {
		Gatunki = gatunki;
	}

	public void setGatunkiFilm(List<String> gatunkiFilm) {
		GatunkiFilm = gatunkiFilm;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getIloscKopii() {
		return iloscKopii;
	}

	public void setIloscKopii(String iloscKopii) {
		this.iloscKopii = iloscKopii;
	}

	public String getLabelPlakat() {
		return labelPlakat;
	}

	public void setLabelPlakat(String labelPlakat) {
		this.labelPlakat = labelPlakat;
	}
}
