/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
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

import org.apache.catalina.connector.Request;
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
@ManagedBean(name = "SzczegolyRezerwacji")
@ViewScoped
public class SzczegolyRezerwacji implements Serializable {

	private Rezerwacje rezerwacja;
	private Wypozyczenia wypozyczenie = new Wypozyczenia();
	private List<Filmy> selectedFilmy;
	JasperPrint jasperPrint;
	private Pracownicy pracownik;
	private KopieFilmu slelectedFilm = null;

	public SzczegolyRezerwacji() {

	}

	@PostConstruct
	public void initBean() {

		int id_rezerwacji = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("rez"));
		this.rezerwacja = findRezeracja(id_rezerwacji);

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		int id = (int) session.getAttribute("pracownik_id");
		EntityManager em = DBManager.getManager().createEntityManager();
		this.pracownik = (Pracownicy) em
				.createNamedQuery("Pracownicy.findByid").setParameter("id", id)
				.getSingleResult();
		em.close();

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
					if (kopia.getRezerwacje() == null && kopia.getWypozyczenia() == null) {
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

	public void usunFilm(KopieFilmu kopia) {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		kopia.setRezerwacje(null);
		this.rezerwacja.getKopieFilmus().remove(kopia);
		em.merge(kopia);
		em.merge(this.rezerwacja);
		em.getTransaction().commit();
		em.close();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Filmy usuniêty", 1);

	}

	private List<Rezerwacje> wrapRezerwacjaTolist() {
		List<Rezerwacje> Rezerwacja = new ArrayList<>();
		Rezerwacja.add(this.rezerwacja);
		return Rezerwacja;
	}

	public void wypozyczFilmy() throws IOException {
		if (rezerwacja.getKlienci().getAktywowany().equals("NIE")) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('DialogDaneKlient').show();");
		}
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		this.wypozyczenie.setData_wypozyczenia(DateTools.currentDate());
		this.wypozyczenie.setTermin_zwrotu(DateTools.nextDate(2));
		this.wypozyczenie.setKlienci(this.rezerwacja.getKlienci());
		this.wypozyczenie.setKopieFilmus(this.rezerwacja.getKopieFilmus());
		this.wypozyczenie.setNr_wypozyczenia(setNumerWypozyczenia());
		this.wypozyczenie.setOdsetki(0);
		this.wypozyczenie.setPracownicy(this.pracownik);
		Wypozyczenia wypozyczenie_merge = em.merge(this.wypozyczenie);

		for (KopieFilmu kopiafilmu : this.rezerwacja.getKopieFilmus()) {
			kopiafilmu.setWypozyczenia(wypozyczenie_merge);
			 kopiafilmu.setRezerwacje(null);
			em.merge(kopiafilmu);
		}
//		this.rezerwacja.setStatus_rezerwacji("Wypo¿yczono");
		//em.remove(this.rezerwacja);
		em.getTransaction().commit();
		//em.close();
		DBManager.getManager().closeEntityManagerFactory();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Wypo¿yczenie zrealizowane wydrukuj potwierdzenie", 1);
		FacesContext.getCurrentInstance().getExternalContext().getFlash()
				.setKeepMessages(true);
		FacesContext
		.getCurrentInstance()
		.getExternalContext()
		.redirect(
				"/wypozyczalnia/Zarzadzanie/szczegolyWypozyczenia.xhtml?wypo="
						+ wypozyczenie_merge
								.getId_wypozyczenia());

	}

	public void zmienStatusRezerwacji() {

		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		;
		this.rezerwacja.setStatus_rezerwacji("Obs³u¿ona. Do odbioru");
		this.rezerwacja.setPracownicy(this.pracownik);

		wyslijPowiadomienieOdbioru();
		DisplayMessage
				.InfoMessage(
						FacesContext.getCurrentInstance(),
						"globalmessage",
						"Rezerwacja obs³u¿ona. Zmieniono status na Obs³u¿ona. Do odbioru.",
						1);
		em.merge(this.rezerwacja);

		em.getTransaction().commit();
		em.close();

	}

	public void wyslijPowiadomienieOdbioru() {
		Klienci klient = this.rezerwacja.getKlienci();
		SendMail mail = new SendMail(
				klient.getE_mail(),
				"Rezerwacja numer " + this.rezerwacja.getNr_rezerwacji(),
				"<html>Witaj "
						+ klient.getImie()
						+ " <br> Twoja rezerwacja zosta³a przygotowana do odbioru</html>");
		try {
			mail.send();
		} catch (MessagingException e) {
			System.out.print("B³ad wysy³ania maila");
			e.printStackTrace();
		}
	}

	private void init() throws JRException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
				wrapRezerwacjaTolist());
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
		httpServletResponse.addHeader(
				"Content-disposition",
				"attachment; filename=rezerwacja_"
						+ this.rezerwacja.getNr_rezerwacji() + ".pdf");
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

	public void przekierowanieSzczegoly() {
		try {
			if (this.slelectedFilm != null) {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/szczegolyFilmu.xhtml?id_filmu="
										+ this.slelectedFilm.getFilmy()
												.getId_filmu());
			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie wybraleœ filmu", 3);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String setNumerWypozyczenia() {
		EntityManager em = DBManager.getManager().createEntityManager();
		List<Integer> id = em.createNativeQuery(
				"Select id_wypozyczenia from wypozyczenia").getResultList();
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
		String numerKlienta = "";
		if (i < 10)
			numerKlienta = "W0/000" + i.toString();
		if (i >= 10 && i < 100)
			numerKlienta = "W0/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerKlienta = "W0/0" + i.toString();
		if (i >= 1000)
			numerKlienta = "W0/" + i.toString();
		return numerKlienta;
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

	public KopieFilmu getSlelectedFilm() {
		return slelectedFilm;
	}

	public void setSlelectedFilm(KopieFilmu slelectedFilm) {
		this.slelectedFilm = slelectedFilm;
	}

}
