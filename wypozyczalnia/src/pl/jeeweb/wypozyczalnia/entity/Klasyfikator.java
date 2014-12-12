package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the klasyfikator database table.
 * 
 */
@Entity
@Table(name="klasyfikator")
@NamedQuery(name="Klasyfikator.findAll", query="SELECT k FROM Klasyfikator k")
public class Klasyfikator implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KlasyfikatorPK id;

	public Klasyfikator() {
	}

	public KlasyfikatorPK getId() {
		return this.id;
	}

	public void setId(KlasyfikatorPK id) {
		this.id = id;
	}

}