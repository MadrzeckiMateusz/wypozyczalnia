package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the klienci database table.
 * 
 */
@Entity
@Table(name="klienci")
@NamedQuery(name="Klienci.findAll", query="SELECT k FROM Klienci k")
public class Klienci implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id_klienta;

	@Temporal(TemporalType.DATE)
	private Date data_rejestracji;

	@Temporal(TemporalType.DATE)
	private Date data_wyrejestrowania;

	private String e_mail;

	private String haslo;

	private String imie;

	private String kod_pocztowy;



	private String miasto;

	private String nazwisko;

	private String nr_klienta;

	private String nr_telefonu;

	private BigDecimal pesel;

	private String ulica;

	private String uwagi;
	private String aktywowany;

	//bi-directional many-to-one association to Rezerwacje
	@OneToMany(mappedBy="klienci" , fetch= FetchType.EAGER)
	private List<Rezerwacje> rezerwacjes;

	//bi-directional many-to-one association to Wypozyczenia
	@OneToMany(mappedBy="klienci" , fetch=FetchType.EAGER)
	private List<Wypozyczenia> wypozyczenias;

	public Klienci() {
	}

	public int getId_klienta() {
		return this.id_klienta;
	}

	public void setId_klienta(int id_klienta) {
		this.id_klienta = id_klienta;
	}

	public Date getData_rejestracji() {
		return this.data_rejestracji;
	}

	public void setData_rejestracji(Date data_rejestracji) {
		this.data_rejestracji = data_rejestracji;
	}

	public Date getData_wyrejestrowania() {
		return this.data_wyrejestrowania;
	}

	public void setData_wyrejestrowania(Date data_wyrejestrowania) {
		this.data_wyrejestrowania = data_wyrejestrowania;
	}

	public String getE_mail() {
		return this.e_mail;
	}

	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}

	public String getHaslo() {
		return this.haslo;
	}

	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	public String getImie() {
		return this.imie;
	}

	public void setImie(String imie) {
		this.imie = imie;
	}

	public String getKod_pocztowy() {
		return this.kod_pocztowy;
	}

	public void setKod_pocztowy(String kod_pocztowy) {
		this.kod_pocztowy = kod_pocztowy;
	}

	public String getMiasto() {
		return this.miasto;
	}

	public void setMiasto(String miasto) {
		this.miasto = miasto;
	}

	public String getNazwisko() {
		return this.nazwisko;
	}

	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}

	public String getNr_klienta() {
		return this.nr_klienta;
	}

	public void setNr_klienta(String nr_klienta) {
		this.nr_klienta = nr_klienta;
	}

	public String getNr_telefonu() {
		return this.nr_telefonu;
	}

	public void setNr_telefonu(String nr_telefonu) {
		this.nr_telefonu = nr_telefonu;
	}

	public BigDecimal getPesel() {
		return this.pesel;
	}

	public void setPesel(BigDecimal pesel) {
		this.pesel = pesel;
	}

	public String getUlica() {
		return this.ulica;
	}

	public void setUlica(String ulica) {
		this.ulica = ulica;
	}

	public String getAktywowany() {
		return aktywowany;
	}

	public void setAktywowany(String aktywowany) {
		this.aktywowany = aktywowany;
	}

	public String getUwagi() {
		return this.uwagi;
	}

	public void setUwagi(String uwagi) {
		this.uwagi = uwagi;
	}

	public List<Rezerwacje> getRezerwacjes() {
		return this.rezerwacjes;
	}

	public void setRezerwacjes(List<Rezerwacje> rezerwacjes) {
		this.rezerwacjes = rezerwacjes;
	}

	public Rezerwacje addRezerwacje(Rezerwacje rezerwacje) {
		getRezerwacjes().add(rezerwacje);
		rezerwacje.setKlienci(this);

		return rezerwacje;
	}

	public Rezerwacje removeRezerwacje(Rezerwacje rezerwacje) {
		getRezerwacjes().remove(rezerwacje);
		rezerwacje.setKlienci(null);

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
		wypozyczenia.setKlienci(this);

		return wypozyczenia;
	}

	public Wypozyczenia removeWypozyczenia(Wypozyczenia wypozyczenia) {
		getWypozyczenias().remove(wypozyczenia);
		wypozyczenia.setKlienci(null);

		return wypozyczenia;
	}

}