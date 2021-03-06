/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.parser.Entity;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.context.RequestContext;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.entity.Klienci;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmuPK;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.entity.Wypozyczenia;
import pl.jeeweb.wypozyczalnia.tools.DateTools;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SendMail;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "WypozyczeniaDetailsBean")
@ViewScoped
public class WypozyczeniaDetailsBean implements Serializable {

	public WypozyczeniaDetailsBean() {

	}

	private Wypozyczenia wypozyczenie;
	private List<Filmy> selectedFilmy;
	JasperPrint jasperPrint;
	private Pracownicy pracownik;
	private KopieFilmu slelectedkopia = null;
	private Wypozyczenia selectedwypozyczenie;

	@PostConstruct
	public void initBean() {
		this.selectedwypozyczenie = null;
		int id_wypozyczenia = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("wypo"));
		this.wypozyczenie = findWypozyczenie(id_wypozyczenia).get(0);

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		int id = (int) session.getAttribute("pracownik_id");
		EntityManager em = DBManager.getManager().createEntityManager();
		this.pracownik = (Pracownicy) em
				.createNamedQuery("Pracownicy.findByid").setParameter("id", id)
				.getSingleResult();
		em.close();
		sprawdzKlienta();

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
					if (kopia.getRezerwacje() == null
							&& kopia.getWypozyczenia() == null) {
						kopia.setWypozyczenia(wypozyczenie);
						this.wypozyczenie.getKopieFilmus().add(kopia);
						break;
					}
				}
			}
			Wypozyczenia wypo = em.merge(this.wypozyczenie);
			this.wypozyczenie.setHistoria_wypo(ustawhistorie(wypo));
			em.merge(this.wypozyczenie);
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Filmy dodane", 1);
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra�e� filmu", 3);
		}
		em.getTransaction().commit();
		em.close();
	}
	
	public List<Filmy> getFilmyArchiwum(Wypozyczenia wypozyczenie) {
		List<Filmy> filmyarchiwum = new ArrayList<>();
		String[] idfilm = { "0" };
		if (wypozyczenie != null) {
			if (wypozyczenie.getHistoria_wypo() != null) {
				idfilm = wypozyczenie.getHistoria_wypo().split(
						";");

				EntityManager em = DBManager.getManager().createEntityManager();

				for (int i = 0; i < idfilm.length; i++) {
					Filmy film = (Filmy) em
							.createNamedQuery("Filmy.findById")
							.setParameter("idFilmu",
									Integer.parseInt(idfilm[i]))
							.getSingleResult();
					filmyarchiwum.add(film);
				}
			}

		}
		return filmyarchiwum;
	}


	private String ustawhistorie(Wypozyczenia wypozyczenia) {
		String historia = "";
		for (Filmy film : wypozyczenia.getFilmyTran()) {
			historia = historia + film.getId_filmu() + ";";
		}
		return historia;
	}

	public void usunFilm(KopieFilmu kopia) {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		kopia.setRezerwacje(null);
		this.wypozyczenie.getKopieFilmus().remove(kopia);
		em.merge(kopia);
		Wypozyczenia wypo = em.merge(this.wypozyczenie);

		this.wypozyczenie.setHistoria_wypo(ustawhistorie(wypo));
		em.merge(this.wypozyczenie);
		em.getTransaction().commit();
		em.close();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Filmy usuni�ty", 1);

	}

	private List<Wypozyczenia> rezerwacjaFilmy() {
		List<Wypozyczenia> wypozyczenia = new ArrayList<>();
		List<Filmy> filmyrez = new ArrayList<>();
		for (KopieFilmu kopia : this.wypozyczenie.getKopieFilmus()) {
			Filmy film = kopia.getFilmy();
			film.setGatunek_string(klasyfikacjaGatunkuToString(film));
			film.setNumerKopiifilmu(film.getNr_filmu() + "/"
					+ kopia.getId().getId_kopii());
			filmyrez.add(film);
		}
		this.wypozyczenie.setFilmyTran(filmyrez);
		wypozyczenia.add(this.wypozyczenie);
		return wypozyczenia;
	}

	private void sprawdzKlienta() {
		if (wypozyczenie.getKlienci().getAktywowany().equals("NIE")) {

			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('DialogDaneKlient').show();");
		}
	}

	public void wyslijPowiadomienieWypozyczenia() {
		Klienci klient = this.wypozyczenie.getKlienci();
		SendMail mail = new SendMail(
				klient.getE_mail(),
				"Wpozyczenie numer " + this.wypozyczenie.getNr_wypozyczenia(),
				"Witaj, "
						+ klient.getImie()+ "!!! \n"
						+ " Wypozyczenie zosta�o zrealizowane.\nDzi�kujemy za skorzystanie z naszych us�ug. Termin zwrotu: "
						+ this.wypozyczenie.getTermin_zwrotu().toString()+"\n"
								+"---------------------------------------------------------------------------------------- \n"
								+ "www.wypozyczalniadvd.com.pl \n"
								+ "tel.555 555 555 \n"
								+ "dvd.wpozyczalnia@gmail.com");
		try {
			mail.send();
		} catch (MessagingException e) {
			System.out.print("B�ad wysy�ania maila");
			e.printStackTrace();
		}
	}

	private void init() throws JRException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
				rezerwacjaFilmy());
		HttpServletRequest req = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		ServletContext con = req.getServletContext();
		InputStream resourc = con
				.getResourceAsStream("/WEB-INF/reports/wypozyczenie.jasper");
		Map<String, Object> parametry = new HashMap<>();
		jasperPrint = JasperFillManager.fillReport(resourc, parametry,
				beanCollectionDataSource);
	}

	public void PDF() throws JRException, IOException {
		init();
		HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		httpServletResponse.addHeader(
				"Content-disposition",
				"attachment; filename=wypozyczenie_"
						+ this.wypozyczenie.getNr_wypozyczenia() + ".pdf");
		ServletOutputStream servletOutputStream = httpServletResponse
				.getOutputStream();
		removeBlankPage(jasperPrint.getPages());
		JasperExportManager.exportReportToPdfStream(jasperPrint,
				servletOutputStream);
		FacesContext.getCurrentInstance().responseComplete();

	}

	private List<Wypozyczenia> findWypozyczenie(int id) {

		EntityManager em = DBManager.getManager().createEntityManager();
		List<Wypozyczenia> _wypozyczenie = (List<Wypozyczenia>) em
				.createNamedQuery("Wypozyczenia.findById")
				.setParameter("id", id).getResultList();
		em.close();
		return _wypozyczenie;
	}

	public void zwrotfilmow() {
		EntityManager em = DBManager.getManager().createEntityManager();
		try {
			em.getTransaction().begin();
			for (KopieFilmu kopiafilmu : this.wypozyczenie.getKopieFilmus()) {
				kopiafilmu.setWypozyczenia(null);
				em.merge(kopiafilmu);
			}
			
			this.wypozyczenie.setData_zwrotu(DateTools.currentDate());
			if (this.wypozyczenie.getTermin_zwrotu().after(DateTools.currentDate())
					|| this.wypozyczenie.getTermin_zwrotu().equals(
							DateTools.currentDate())) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Film zwr�cony w terminie", 1);

			}
			else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Film zwr�cony po terminie", 3);
				long diff = DateTools.currentDate().getTime() - this.wypozyczenie.getTermin_zwrotu().getTime() ;
				long diffdays = diff /(24*60*60*1000);
				if(diffdays>0) {
				this.wypozyczenie.setOdsetki(this.wypozyczenie.getKopieFilmus().size()*5*diffdays);
				}
			}
			this.wypozyczenie.setHistoria_wypo(ustawhistorie(this.wypozyczenie));
			this.wypozyczenie.setKopieFilmus(null);
			em.merge(this.wypozyczenie);
			em.getTransaction().commit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			em.getTransaction().rollback();
			e.printStackTrace();
		}
		em.close();
	
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
			// "globalmessage", "B��d!!!", 3);
			//
			// } else {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/editKlient.xhtml?id_klient="
									+ wypozyczenie.getKlienci().getId_klienta()
									+ "&target="
									+ FacesContext.getCurrentInstance()
											.getViewRoot().getViewId()
									+ "?wypo="
									+ this.wypozyczenie.getId_wypozyczenia());
			// }
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "B��d!!!", 3);
		}
	}

	public void przekierowanieSzczegoly() {
		try {
			if (this.slelectedkopia != null) {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/szczegolyFilmu.xhtml?id_filmu="
										+ this.slelectedkopia.getFilmy()
												.getId_filmu());
			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie wybrale� filmu", 3);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Wypozyczenia getWypozyczenie() {
		return wypozyczenie;
	}

	public void setWypozyczenie(Wypozyczenia wypozyczenie) {
		this.wypozyczenie = wypozyczenie;
	}

	public List<Filmy> getSelectedFilmy() {
		return selectedFilmy;
	}

	public void setSelectedFilmy(List<Filmy> selectedFilmy) {
		this.selectedFilmy = selectedFilmy;
	}

	public KopieFilmu getSlelectedkopia() {
		return slelectedkopia;
	}

	public void setSlelectedkopia(KopieFilmu slelectedkopia) {
		this.slelectedkopia = slelectedkopia;
	}

	/**
	 * @return the selectedwypozyczenie
	 */
	public Wypozyczenia getSelectedwypozyczenie() {
		return selectedwypozyczenie;
	}

	/**
	 * @param selectedwypozyczenie the selectedwypozyczenie to set
	 */
	public void setSelectedwypozyczenie(Wypozyczenia selectedwypozyczenie) {
		this.selectedwypozyczenie = selectedwypozyczenie;
	}
}
