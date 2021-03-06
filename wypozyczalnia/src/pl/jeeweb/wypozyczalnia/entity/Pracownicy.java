package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the pracownicy database table.
 * 
 */
@Entity
@Table(name = "pracownicy")
@NamedQueries({
		@NamedQuery(name = "Pracownicy.findAll", query = "SELECT p FROM Pracownicy p"),
		@NamedQuery(name = "Pracownicy.findByEmail", query = "SELECT p FROM Pracownicy p where p.e_mail = :email"),
		@NamedQuery(name = "Pracownicy.findByid", query = "SELECT p FROM Pracownicy p where p.id_prcownika = :id"),
		@NamedQuery(name = "Pracownicy.getMaxId", query = "SELECT MAX(p.id_prcownika) FROM Pracownicy p") })
public class Pracownicy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	private int id_prcownika;

	

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date data_zatrudnienia;

	@Temporal(TemporalType.DATE)
	private Date data_zwolnienia;

	@Column(nullable = false, length = 50)
	private String e_mail;

	@Column(length = 64)
	private String haslo;

	@Column(nullable = false, length = 30)
	private String imie;

	@Column(nullable = false, length = 50)
	private String nazwisko;

	@Column(nullable = false, length = 7)
	private String nr_pracownika;

	@Column(nullable = false, precision = 10)
	private BigDecimal nr_telefonu;

	@Column(nullable = false, precision = 10)
	private BigDecimal pesel;
	@Column(nullable = false, length = 45)
	private String miasto;
	@Column(nullable = false, length = 45)
	private String ulica;

	@Column(nullable = false, length = 6)
	private String kod_pocztowy;

	// bi-directional many-to-one association to Rezerwacje
	@OneToMany(mappedBy = "pracownicy")
	// , fetch= FetchType.EAGER)
	private List<Rezerwacje> rezerwacjes;

	// bi-directional many-to-one association to Wypozyczenia
	@OneToMany(mappedBy = "pracownicy")
	// , fetch= FetchType.EAGER)
	private List<Wypozyczenia> wypozyczenias;

	public Pracownicy() {
	}

	public int getId_prcownika() {
		return this.id_prcownika;
	}

	public void setId_prcownika(int id_prcownika) {
		this.id_prcownika = id_prcownika;
	}


	public Date getData_zatrudnienia() {
		return this.data_zatrudnienia;
	}

	public void setData_zatrudnienia(Date data_zatrudnienia) {
		this.data_zatrudnienia = data_zatrudnienia;
	}

	public Date getData_zwolnienia() {
		return this.data_zwolnienia;
	}

	public void setData_zwolnienia(Date data_zwolnienia) {
		this.data_zwolnienia = data_zwolnienia;
	}

	public String getE_mail() {
		return this.e_mail;
	}

	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}

	public String getHaslo_dostepu() {
		return this.haslo;
	}

	public void setHaslo_dostepu(String haslo_dostepu) {
		this.haslo = haslo_dostepu;
	}

	public String getImie() {
		return this.imie;
	}

	public void setImie(String imie) {
		this.imie = imie;
	}

	public String getNazwisko() {
		return this.nazwisko;
	}

	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}

	public String getNr_pracownika() {
		return this.nr_pracownika;
	}

	public void setNr_pracownika(String nr_pracownika) {
		this.nr_pracownika = nr_pracownika;
	}

	public BigDecimal getNr_telefonu() {
		return this.nr_telefonu;
	}

	public void setNr_telefonu(BigDecimal nr_telefonu) {
		this.nr_telefonu = nr_telefonu;
	}

	public BigDecimal getPesel() {
		return this.pesel;
	}

	public void setPesel(BigDecimal pesel) {
		this.pesel = pesel;
	}

	public List<Rezerwacje> getRezerwacjes() {
		return this.rezerwacjes;
	}

	public void setRezerwacjes(List<Rezerwacje> rezerwacjes) {
		this.rezerwacjes = rezerwacjes;
	}

	public Rezerwacje addRezerwacje(Rezerwacje rezerwacje) {
		getRezerwacjes().add(rezerwacje);
		rezerwacje.setPracownicy(this);

		return rezerwacje;
	}

	public Rezerwacje removeRezerwacje(Rezerwacje rezerwacje) {
		getRezerwacjes().remove(rezerwacje);
		rezerwacje.setPracownicy(null);

		return rezerwacje;
	}

	public List<Wypozyczenia> getWypozyczenias() {
		return this.wypozyczenias;
	}

	public void setWypozyczenias(List<Wypozyczenia> wypozyczenias) {
		this.wypozyczenias = wypozyczenias;
	}

	public Wypozyczenia addWypozyczenia(Wypozyczenia wypozyczenia) {
		getWypozyczenias().add(wypozyczenia);
		wypozyczenia.setPracownicy(this);

		return wypozyczenia;
	}

	public Wypozyczenia removeWypozyczenia(Wypozyczenia wypozyczenia) {
		getWypozyczenias().remove(wypozyczenia);
		wypozyczenia.setPracownicy(null);

		return wypozyczenia;
	}

	/**
	 * @return the haslo
	 */
	public String getHaslo() {
		return haslo;
	}

	/**
	 * @param haslo the haslo to set
	 */
	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	/**
	 * @return the miasto
	 */
	public String getMiasto() {
		return miasto;
	}

	/**
	 * @param miasto the miasto to set
	 */
	public void setMiasto(String miasto) {
		this.miasto = miasto;
	}

	/**
	 * @return the ulica
	 */
	public String getUlica() {
		return ulica;
	}

	/**
	 * @param ulica the ulica to set
	 */
	public void setUlica(String ulica) {
		this.ulica = ulica;
	}

	/**
	 * @return the kod_pocztowy
	 */
	public String getKod_pocztowy() {
		return kod_pocztowy;
	}

	/**
	 * @param kod_pocztowy the kod_pocztowy to set
	 */
	public void setKod_pocztowy(String kod_pocztowy) {
		this.kod_pocztowy = kod_pocztowy;
	}

}