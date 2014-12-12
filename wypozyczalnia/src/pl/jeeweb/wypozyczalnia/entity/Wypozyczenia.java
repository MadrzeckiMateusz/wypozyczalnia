package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the wypozyczenia database table.
 * 
 */
@Entity
@Table(name="wypozyczenia")
@NamedQuery(name="Wypozyczenia.findAll", query="SELECT w FROM Wypozyczenia w")
public class Wypozyczenia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id_wypozyczenia;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date data_wypozyczenia;

	@Temporal(TemporalType.DATE)
	private Date data_zwrotu;

	@Column(nullable=false, length=7)
	private String nr_wypozyczenia;

	private float odsetki;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date termin_zwrotu;

	@Column(length=500)
	private String uwagi;

	//bi-directional many-to-one association to KopieFilmu
	@OneToMany(mappedBy="wypozyczenia" , fetch= FetchType.EAGER)
	private List<KopieFilmu> kopieFilmus;

	//bi-directional many-to-one association to Pracownicy
	@ManyToOne
	@JoinColumn(name="Id_prcownika" )
	private Pracownicy pracownicy;

	//bi-directional many-to-one association to Klienci
	@ManyToOne
	@JoinColumn(name="Id_klienta")
	private Klienci klienci;

	public Wypozyczenia() {
	}

	public int getId_wypozyczenia() {
		return this.id_wypozyczenia;
	}

	public void setId_wypozyczenia(int id_wypozyczenia) {
		this.id_wypozyczenia = id_wypozyczenia;
	}

	public Date getData_wypozyczenia() {
		return this.data_wypozyczenia;
	}

	public void setData_wypozyczenia(Date data_wypozyczenia) {
		this.data_wypozyczenia = data_wypozyczenia;
	}

	public Date getData_zwrotu() {
		return this.data_zwrotu;
	}

	public void setData_zwrotu(Date data_zwrotu) {
		this.data_zwrotu = data_zwrotu;
	}

	public String getNr_wypozyczenia() {
		return this.nr_wypozyczenia;
	}

	public void setNr_wypozyczenia(String nr_wypozyczenia) {
		this.nr_wypozyczenia = nr_wypozyczenia;
	}

	public float getOdsetki() {
		return this.odsetki;
	}

	public void setOdsetki(float odsetki) {
		this.odsetki = odsetki;
	}

	public Date getTermin_zwrotu() {
		return this.termin_zwrotu;
	}

	public void setTermin_zwrotu(Date termin_zwrotu) {
		this.termin_zwrotu = termin_zwrotu;
	}

	public String getUwagi() {
		return this.uwagi;
	}

	public void setUwagi(String uwagi) {
		this.uwagi = uwagi;
	}

	public List<KopieFilmu> getKopieFilmus() {
		return this.kopieFilmus;
	}

	public void setKopieFilmus(List<KopieFilmu> kopieFilmus) {
		this.kopieFilmus = kopieFilmus;
	}

	public KopieFilmu addKopieFilmus(KopieFilmu kopieFilmus) {
		getKopieFilmus().add(kopieFilmus);
		kopieFilmus.setWypozyczenia(this);

		return kopieFilmus;
	}

	public KopieFilmu removeKopieFilmus(KopieFilmu kopieFilmus) {
		getKopieFilmus().remove(kopieFilmus);
		kopieFilmus.setWypozyczenia(null);

		return kopieFilmus;
	}

	public Pracownicy getPracownicy() {
		return this.pracownicy;
	}

	public void setPracownicy(Pracownicy pracownicy) {
		this.pracownicy = pracownicy;
	}

	public Klienci getKlienci() {
		return this.klienci;
	}

	public void setKlienci(Klienci klienci) {
		this.klienci = klienci;
	}

}