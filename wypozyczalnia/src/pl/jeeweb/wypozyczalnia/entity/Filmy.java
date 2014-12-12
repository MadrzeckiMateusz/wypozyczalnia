package pl.jeeweb.wypozyczalnia.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.primefaces.model.DefaultStreamedContent;


/**
 * The persistent class for the filmy database table.
 * 
 */
@Entity
@Table(name="filmy")
@NamedQuery(name="Filmy.findAll", query="SELECT f FROM Filmy f")
public class Filmy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id_filmu;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date data_dodania;

	@Temporal(TemporalType.DATE)
	private Date data_usuniecia;

	@Column(nullable=false, length=7)
	private String nr_filmu;

	@Column(length=1024)
	private String opis;

	@Lob
	private byte[] plakat;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date premiera;

	@Column(length=100)
	private String produkcja;

	@Column(nullable=false, length=100)
	private String rezyseria;

	@Column(length=100)
	private String scenariusz;

	@Column(nullable=false, length=100)
	private String tytul;

	//bi-directional many-to-many association to KlasyfikacjaGatunku
	@ManyToMany
	@JoinTable(
		name="klasyfikator"
		, joinColumns={
			@JoinColumn(name="Id_filmu", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="Id_klasyfikacji", nullable=false)
			}
		)
	private List<KlasyfikacjaGatunku> klasyfikacjaGatunkus;

	//bi-directional many-to-one association to KopieFilmu
	@OneToMany(mappedBy="filmy", fetch=FetchType.LAZY)
	private List<KopieFilmu> kopieFilmus;
	
	public Filmy() {
	}

	public int getId_filmu() {
		return this.id_filmu;
	}

	public void setId_filmu(int id_filmu) {
		this.id_filmu = id_filmu;
	}

	public Date getData_dodania() {
		return this.data_dodania;
	}

	public void setData_dodania(Date data_dodania) {
		this.data_dodania = data_dodania;
	}

	public Date getData_usuniecia() {
		return this.data_usuniecia;
	}

	public void setData_usuniecia(Date data_usuniecia) {
		this.data_usuniecia = data_usuniecia;
	}

	public String getNr_filmu() {
		return this.nr_filmu;
	}

	public void setNr_filmu(String nr_filmu) {
		this.nr_filmu = nr_filmu;
	}

	public String getOpis() {
		return this.opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public byte[] getPlakat() {
		return this.plakat;
	}

	public void setPlakat(byte[] plakat) {
		this.plakat = plakat;
	}
	public Date getPremiera() {
		return this.premiera;
	}

	public void setPremiera(Date premiera) {
		this.premiera = premiera;
	}

	public String getProdukcja() {
		return this.produkcja;
	}

	public void setProdukcja(String produkcja) {
		this.produkcja = produkcja;
	}

	public String getRezyseria() {
		return this.rezyseria;
	}

	public void setRezyseria(String rezyseria) {
		this.rezyseria = rezyseria;
	}

	public String getScenariusz() {
		return this.scenariusz;
	}

	public void setScenariusz(String scenariusz) {
		this.scenariusz = scenariusz;
	}

	public String getTytul() {
		return this.tytul;
	}

	public void setTytul(String tytul) {
		this.tytul = tytul;
	}

	public List<KlasyfikacjaGatunku> getKlasyfikacjaGatunkus() {
		return this.klasyfikacjaGatunkus;
	}

	public void setKlasyfikacjaGatunkus(List<KlasyfikacjaGatunku> klasyfikacjaGatunkus) {
		this.klasyfikacjaGatunkus = klasyfikacjaGatunkus;
	}

	public List<KopieFilmu> getKopieFilmus() {
		return this.kopieFilmus;
	}

	public void setKopieFilmus(List<KopieFilmu> kopieFilmus) {
		this.kopieFilmus = kopieFilmus;
	}

	public KopieFilmu addKopieFilmus(KopieFilmu kopieFilmus) {
		getKopieFilmus().add(kopieFilmus);
		kopieFilmus.setFilmy(this);

		return kopieFilmus;
	}

	public KopieFilmu removeKopieFilmus(KopieFilmu kopieFilmus) {
		getKopieFilmus().remove(kopieFilmus);
		kopieFilmus.setFilmy(null);

		return kopieFilmus;
	}
	

}