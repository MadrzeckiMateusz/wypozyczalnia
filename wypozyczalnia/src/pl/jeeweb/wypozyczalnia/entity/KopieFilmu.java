package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the kopie_filmu database table.
 * 
 */
@Entity
@Table(name="kopie_filmu")
@NamedQuery(name="KopieFilmu.findAll", query="SELECT k FROM KopieFilmu k")
public class KopieFilmu implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KopieFilmuPK id;

	//bi-directional many-to-one association to Filmy
	@ManyToOne
	@JoinColumn(name="Id_filmu", nullable=false, insertable=false, updatable=false)
	private Filmy filmy;

	//bi-directional many-to-one association to Wypozyczenia
	@ManyToOne
	@JoinColumn(name="Id_wypozyczenia")
	private Wypozyczenia wypozyczenia;

	//bi-directional many-to-one association to Rezerwacje
	@ManyToOne
	@JoinColumn(name="Id_rezerwacji")
	private Rezerwacje rezerwacje;

	public KopieFilmu() {
	}

	public KopieFilmuPK getId() {
		return this.id;
	}

	public void setId(KopieFilmuPK id) {
		this.id = id;
	}

	public Filmy getFilmy() {
		return this.filmy;
	}

	public void setFilmy(Filmy filmy) {
		this.filmy = filmy;
	}

	public Wypozyczenia getWypozyczenia() {
		return this.wypozyczenia;
	}

	public void setWypozyczenia(Wypozyczenia wypozyczenia) {
		this.wypozyczenia = wypozyczenia;
	}

	public Rezerwacje getRezerwacje() {
		return this.rezerwacje;
	}

	public void setRezerwacje(Rezerwacje rezerwacje) {
		this.rezerwacje = rezerwacje;
	}
	
	
}