package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the rezerwacje database table.
 * 
 */
@Entity
@Table(name="rezerwacje")
@NamedQuery(name="Rezerwacje.findAll", query="SELECT r FROM Rezerwacje r")
public class Rezerwacje implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id_rezerwacji;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date data_odbioru;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date data_rezerwacji;

	@Column(nullable=false, length=7)
	private String nr_rezerwacji;

	@Column(length=10)
	private String status_rezerwacji;

	@Column(length=500)
	private String uwagi;

	//bi-directional many-to-one association to KopieFilmu
	@OneToMany(mappedBy="rezerwacje" , fetch= FetchType.EAGER, cascade = CascadeType.MERGE)
	private List<KopieFilmu> kopieFilmus;

	//bi-directional many-to-one association to Klienci
	@ManyToOne
	@JoinColumn(name="Id_klienta")
	private Klienci klienci;

	//bi-directional many-to-one association to Pracownicy
	@ManyToOne(cascade = {CascadeType.ALL},fetch= FetchType.EAGER)
	@JoinColumn(name="Id_prcownika")
	private Pracownicy pracownicy;

	public Rezerwacje() {
	}

	public int getId_rezerwacji() {
		return this.id_rezerwacji;
	}

	public void setId_rezerwacji(int id_rezerwacji) {
		this.id_rezerwacji = id_rezerwacji;
	}

	public Date getData_odbioru() {
		return this.data_odbioru;
	}

	public void setData_odbioru(Date data_odbioru) {
		this.data_odbioru = data_odbioru;
	}

	public Date getData_rezerwacji() {
		return this.data_rezerwacji;
	}

	public void setData_rezerwacji(Date data_rezerwacji) {
		this.data_rezerwacji = data_rezerwacji;
	}

	public String getNr_rezerwacji() {
		return this.nr_rezerwacji;
	}

	public void setNr_rezerwacji(String nr_rezerwacji) {
		this.nr_rezerwacji = nr_rezerwacji;
	}

	public String getStatus_rezerwacji() {
		return this.status_rezerwacji;
	}

	public void setStatus_rezerwacji(String status_rezerwacji) {
		this.status_rezerwacji = status_rezerwacji;
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
		kopieFilmus.setRezerwacje(this);

		return kopieFilmus;
	}

	public KopieFilmu removeKopieFilmus(KopieFilmu kopieFilmus) {
		getKopieFilmus().remove(kopieFilmus);
		kopieFilmus.setRezerwacje(null);

		return kopieFilmus;
	}

	public Klienci getKlienci() {
		return this.klienci;
	}

	public void setKlienci(Klienci klienci) {
		this.klienci = klienci;
	}

	public Pracownicy getPracownicy() {
		return this.pracownicy;
	}

	public void setPracownicy(Pracownicy pracownicy) {
		this.pracownicy = pracownicy;
	}

}