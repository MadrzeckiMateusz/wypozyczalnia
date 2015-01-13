/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.catalina.connector.Request;
import org.primefaces.context.RequestContext;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmuPK;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "SzczegolyRezerwacji")
@ViewScoped
public class SzczegolyRezerwacji implements Serializable {

	private Rezerwacje rezerwacja;
	private List<Filmy> selectedFilmy;
	JasperPrint jasperPrint;

	public SzczegolyRezerwacji() {

	}

	@PostConstruct
	public void initBean() {

		int id_rezerwacji = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("rez"));
		this.rezerwacja = findRezeracja(id_rezerwacji);

		if (rezerwacja.getKlienci().getAktywowany().equals("NIE")) {

			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('DialogDaneKlient').show();");
		}
	}

	public void dodajFilmy() {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();

		if (!selectedFilmy.isEmpty()) {
			for (Filmy film : selectedFilmy) {
				Filmy film1 = (Filmy) em.createNamedQuery("Filmy.findById")
						.setParameter("idFilmu", film.getId_filmu())
						.getSingleResult();
				for (KopieFilmu kopia : film1.getKopieFilmus()) {
					if (kopia.getRezerwacje() == null) {
						kopia.setRezerwacje(rezerwacja);
						this.rezerwacja.getKopieFilmus().add(kopia);
						break;
					}
				}
			}
			em.merge(this.rezerwacja);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Filmy dodane", 1);
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ filmu", 3);
		}
		em.getTransaction().commit();
		em.close();
	}

	public void usunFilm(int id) {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		Filmy film = (Filmy) em.createNamedQuery("Filmy.findById")
				.setParameter("idFilmu", id).getSingleResult();

		for (KopieFilmu kopiefilmu : film.getKopieFilmus()) {
			if (kopiefilmu.getRezerwacje() != null) {
				if (kopiefilmu.getRezerwacje().getId_rezerwacji() == this.rezerwacja
						.getId_rezerwacji()) {

					kopiefilmu.setRezerwacje(null);

					List<KopieFilmu> KopieZapis = new ArrayList<>();
					for (KopieFilmu kopiareze : this.rezerwacja
							.getKopieFilmus()) {
						if (!kopiareze.getId().equals(kopiefilmu.getId())) {
							KopieZapis.add(kopiareze);
						}

					}
					this.rezerwacja.setKopieFilmus(KopieZapis);
					em.merge(kopiefilmu);
					em.merge(this.rezerwacja);
				}
			}
		}

		em.getTransaction().commit();
		em.close();

		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Filmy usuniêty", 1);

	}

	private List<Rezerwacje> rezerwacjaFilmy() {
		List<Rezerwacje> Rezerwacja = new ArrayList<>();
		List<Filmy> filmyrez = new ArrayList<>();
		for (KopieFilmu kopia : this.rezerwacja.getKopieFilmus()) {
			Filmy film = kopia.getFilmy();
			film.setGatunek_string(klasyfikacjaGatunkuToString(film));
			film.setNumerKopiifilmu(film.getNr_filmu() + "/"
					+ kopia.getId().getId_kopii());
			filmyrez.add(film);
		}
		this.rezerwacja.setFilmyTran(filmyrez);
		Rezerwacja.add(this.rezerwacja);
		return Rezerwacja;
	}

	private void init() throws JRException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
				rezerwacjaFilmy());
		HttpServletRequest req = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		ServletContext con = req.getServletContext();
		InputStream resourc = con
				.getResourceAsStream("/WEB-INF/reports/rezerwacja1.jasper");
		Map<String, Object> parametry = new HashMap<>();
		jasperPrint = JasperFillManager.fillReport(resourc, parametry,
				beanCollectionDataSource);
	}

	public void PDF() throws JRException, IOException {
		init();
		HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		httpServletResponse.addHeader("Content-disposition",
				"attachment; filename=rezerwacja_"+this.rezerwacja.getNr_rezerwacji()+".pdf");
		ServletOutputStream servletOutputStream = httpServletResponse
				.getOutputStream();
		removeBlankPage(jasperPrint.getPages());
		JasperExportManager.exportReportToPdfStream(jasperPrint,
				servletOutputStream);
		FacesContext.getCurrentInstance().responseComplete();
	}

	private Rezerwacje findRezeracja(int id) {
		EntityManager em = DBManager.getManager().createEntityManager();
		Rezerwacje _rezerwacja = (Rezerwacje) em
				.createNamedQuery("Rezerwacje.findById")
				.setParameter("id_rezerwacji", id).getSingleResult();
		em.close();
		return _rezerwacja;
	}

	public String getNumerkopiiFlmu(KopieFilmu kopiaFilmu) {
		KopieFilmuPK kopiaPk = kopiaFilmu.getId();
		return kopiaFilmu.getFilmy().getNr_filmu() + "/"
				+ kopiaPk.getId_kopii();

	}

	public String klasyfikacjaGatunkuToString(Filmy film) {
		String gatunek = "";
		for (KlasyfikacjaGatunku gatunekFilmu : film.getKlasyfikacjaGatunkus()) {
			gatunek = gatunek + gatunekFilmu.getGatunek() + ", ";
		}

		return gatunek;
	}

	private void removeBlankPage(List<JRPrintPage> pages) {

		for (Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) {
			JRPrintPage page = i.next();
			if (page.getElements().size() == 0)
				i.remove();
		}
	}

	public void przekierowaniePotwierdz() {

		try {
			// if (rezerwacja == (null)) {
			// DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
			// "globalmessage", "B³¹d!!!", 3);
			//
			// } else {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/editKlient.xhtml?id_klient="
									+ rezerwacja.getKlienci().getId_klienta()
									+ "&target="
									+ FacesContext.getCurrentInstance()
											.getViewRoot().getViewId()
									+ "?rez="
									+ this.rezerwacja.getId_rezerwacji());
			// }
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "B³¹d!!!", 3);
		}
	}

	public Rezerwacje getRezerwacja() {
		return rezerwacja;
	}

	public void setRezerwacja(Rezerwacje rezerwacja) {
		this.rezerwacja = rezerwacja;
	}

	public List<Filmy> getSelectedFilmy() {
		return selectedFilmy;
	}

	public void setSelectedFilmy(List<Filmy> selectedFilmy) {
		this.selectedFilmy = selectedFilmy;
	}

}
