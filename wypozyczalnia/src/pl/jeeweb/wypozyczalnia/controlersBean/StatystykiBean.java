/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.entity.Wypozyczenia;
import pl.jeeweb.wypozyczalnia.tools.StatystykaFilmuModel;
import pl.jeeweb.wypozyczalnia.tools.StatystykiWypozyczenModel;
import pl.jeeweb.wypozyczalnia.tools.statystykiRezerwacjiModel;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "StatystykiBean")
@ViewScoped
public class StatystykiBean implements Serializable {

	private String selectedPracownik;
	private Pracownicy pracownik;
	private List<Pracownicy> listaPracownicy;
	private List<Filmy> listaFilmow;
	private List<Rezerwacje> listaRezerwacji;
	private List<Wypozyczenia> listaWypozyczen;
	private Date dataOd;
	private Date dataDo;
	private Map<String, String> mapaPracownicy;

	private List<StatystykiWypozyczenModel> listaStatystykiWypozyczenModels;
	private List<statystykiRezerwacjiModel> listStatystyki;
	private List<StatystykaFilmuModel> listStatystykiFilmu;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initBean() {

		this.mapaPracownicy = new HashMap<String, String>();

		EntityManager em = DBManager.getManager().createEntityManager();
		listaPracownicy = (List<Pracownicy>) em.createNamedQuery(
				"Pracownicy.findAll").getResultList();
		for (Pracownicy pracownik : listaPracownicy) {
			pracownik.getWypozyczenias().size();
			pracownik.getRezerwacjes().size();
			mapaPracownicy.put(
					pracownik.getNr_pracownika() + " "
							+ pracownik.getNazwisko() + " "
							+ pracownik.getImie(),
					Integer.toString(pracownik.getId_prcownika()));
		}

		listaFilmow = (List<Filmy>) em.createNamedQuery("Filmy.findAll")
				.getResultList();
		for (Filmy film : listaFilmow) {
			film.getKopieFilmus().size();
		}

		listaRezerwacji = (List<Rezerwacje>) em.createNamedQuery(
				"Rezerwacje.findAll").getResultList();
		for (Rezerwacje rezerwacja : listaRezerwacji) {
			rezerwacja.getKopieFilmus().size();
		}
		listaWypozyczen = (List<Wypozyczenia>) em.createNamedQuery(
				"Wypozyczenia.findAll").getResultList();
		for (Wypozyczenia wypozyczenie : listaWypozyczen) {
			wypozyczenie.getKopieFilmus().size();
		}

		em.close();

	}

	public void statystykiPracownika() {

		listStatystyki = new ArrayList<>();
		listStatystyki.clear();
		listaStatystykiWypozyczenModels = new ArrayList<>();
		listaStatystykiWypozyczenModels.clear();

		System.out.println("");
		EntityManager em = DBManager.getManager().createEntityManager();
		this.pracownik = (Pracownicy) em
				.createNamedQuery("Pracownicy.findByid")
				.setParameter("id", Integer.parseInt(selectedPracownik))
				.getSingleResult();//
		pracownik.getRezerwacjes().size();
		pracownik.getWypozyczenias().size();
		obliczRodzajWypozyczen(pracownik);
		obliczRodzajRezerwacji(pracownik);

	}

	public void styatystykiFilmow() {
		this.listStatystykiFilmu = new ArrayList<>();
		this.listStatystykiFilmu.clear();
		for (Filmy film : this.listaFilmow) {
			int licznikWyp= 0;
			int licznikRez=0;
			StatystykaFilmuModel filmstaty = new StatystykaFilmuModel(
					film.getNr_filmu(), film.getTytul(), 0, 0);

			for (Wypozyczenia wypozyczenie : this.listaWypozyczen) {
				if (wypozyczenie.getData_zwrotu() == null) {
					for (Filmy film_kopia : wypozyczenie.getFilmyTran()) {
						if (film.equals(film_kopia)) {
							licznikWyp++;
						}
					}
				} else {
					String id_filmuhist[] = wypozyczenie.getHistoria_wypo()
							.split(";");
					for (String id_filmu : id_filmuhist) {
						if (film.getId_filmu() == Integer.parseInt(id_filmu)) {
							licznikWyp++;
						}
					}
				}
			}
			
			for (Rezerwacje rezerwacja : this.listaRezerwacji) {
				if (!rezerwacja.getStatus_rezerwacji().equals("Zrealizowana")) {
					for (Filmy film_kopia : rezerwacja.getFilmyTran()) {
						if (film.equals(film_kopia)) {
							licznikRez++;
						}
					}
				} else {
					String id_filmuhist[] = rezerwacja.getHistoria_rezer()
							.split(";");
					for (String id_filmu : id_filmuhist) {
						if (film.getId_filmu() == Integer.parseInt(id_filmu)) {
							licznikRez++;
						}
					}
				}
			}
			filmstaty.setIloscWypozyczen(licznikWyp);
			filmstaty.setIloscRezerwacji(licznikRez);
			this.listStatystykiFilmu.add(filmstaty);
		}
	}

	private void obliczRodzajRezerwacji(Pracownicy pracownik) {

		int licznikOgolnyRezerwacji = 0;
		int licznikRezerwacjiObsluzonych = 0;
		int licznikRezerwacjiZrealizowanych = 0;

		for (Rezerwacje rezerwacja : pracownik.getRezerwacjes()) {
			licznikOgolnyRezerwacji++;

			switch (rezerwacja.getStatus_rezerwacji()) {
			case "Zrealizowana":
				licznikRezerwacjiZrealizowanych++;
				break;
			case "Obs³u¿ona. Do odbioru":
				licznikRezerwacjiObsluzonych++;
				break;

			default:
				break;
			}
		}
		this.listStatystyki.add(new statystykiRezerwacjiModel(
				licznikOgolnyRezerwacji, licznikRezerwacjiZrealizowanych,
				licznikRezerwacjiObsluzonych));

	}

	private void obliczRodzajWypozyczen(Pracownicy pracownik) {

		int licznikOgolnyWypozyczenia = 0;
		int licznikWypozyczeniaZrealizowane = 0;
		int licznikWypozyczeniaZwrocone = 0;

		for (Wypozyczenia wypozyczenia : pracownik.getWypozyczenias()) {
			licznikOgolnyWypozyczenia++;

			if (wypozyczenia.getData_zwrotu() != null) {
				licznikWypozyczeniaZrealizowane++;
			} else {
				licznikWypozyczeniaZwrocone++;

			}

		}
		listaStatystykiWypozyczenModels.add(new StatystykiWypozyczenModel(
				licznikOgolnyWypozyczenia, licznikWypozyczeniaZwrocone,
				licznikWypozyczeniaZrealizowane));
	}

	/**
	 * @return the selectedPracownik
	 */
	public String getSelectedPracownik() {
		return selectedPracownik;
	}

	/**
	 * @param selectedPracownik
	 *            the selectedPracownik to set
	 */
	public void setSelectedPracownik(String selectedPracownik) {
		this.selectedPracownik = selectedPracownik;
	}

	/**
	 * @return the dataOd
	 */
	public Date getDataOd() {
		return dataOd;
	}

	/**
	 * @param dataOd
	 *            the dataOd to set
	 */
	public void setDataOd(Date dataOd) {
		this.dataOd = dataOd;
	}

	/**
	 * @return the dataDo
	 */
	public Date getDataDo() {
		return dataDo;
	}

	/**
	 * @param dataDo
	 *            the dataDo to set
	 */
	public void setDataDo(Date dataDo) {
		this.dataDo = dataDo;
	}

	/**
	 * @return the mapaPracownicy
	 */
	public Map<String, String> getMapaPracownicy() {
		return mapaPracownicy;
	}

	/**
	 * @param mapaPracownicy
	 *            the mapaPracownicy to set
	 */
	public void setMapaPracownicy(Map<String, String> mapaPracownicy) {
		this.mapaPracownicy = mapaPracownicy;
	}

	public Pracownicy getPracownik() {
		return pracownik;
	}

	/**
	 * @param pracownik
	 *            the pracownik to set
	 */
	public void setPracownik(Pracownicy pracownik) {
		this.pracownik = pracownik;
	}

	/**
	 * @return the listStatystyki
	 */
	public List<statystykiRezerwacjiModel> getListStatystyki() {
		return listStatystyki;
	}

	/**
	 * @param listStatystyki
	 *            the listStatystyki to set
	 */
	public void setListStatystyki(List<statystykiRezerwacjiModel> listStatystyki) {
		this.listStatystyki = listStatystyki;
	}

	/**
	 * @return the listaStatystykiWypozyczenModels
	 */
	public List<StatystykiWypozyczenModel> getListaStatystykiWypozyczenModels() {
		return listaStatystykiWypozyczenModels;
	}

	/**
	 * @param listaStatystykiWypozyczenModels
	 *            the listaStatystykiWypozyczenModels to set
	 */
	public void setListaStatystykiWypozyczenModels(
			List<StatystykiWypozyczenModel> listaStatystykiWypozyczenModels) {
		this.listaStatystykiWypozyczenModels = listaStatystykiWypozyczenModels;
	}

	/**
	 * @return the listStatystykiFilmu
	 */
	public List<StatystykaFilmuModel> getListStatystykiFilmu() {
		return listStatystykiFilmu;
	}

	/**
	 * @param listStatystykiFilmu the listStatystykiFilmu to set
	 */
	public void setListStatystykiFilmu(
			List<StatystykaFilmuModel> listStatystykiFilmu) {
		this.listStatystykiFilmu = listStatystykiFilmu;
	}
}
