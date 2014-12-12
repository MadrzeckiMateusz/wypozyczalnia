package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the klasyfikator database table.
 * 
 */
@Embeddable
public class KlasyfikatorPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=false, updatable=false, unique=true, nullable=false)
	private int id_filmu;

	@Column(insertable=false, updatable=false, unique=true, nullable=false)
	private int id_klasyfikacji;

	public KlasyfikatorPK() {
	}
	public int getId_filmu() {
		return this.id_filmu;
	}
	public void setId_filmu(int id_filmu) {
		this.id_filmu = id_filmu;
	}
	public int getId_klasyfikacji() {
		return this.id_klasyfikacji;
	}
	public void setId_klasyfikacji(int id_klasyfikacji) {
		this.id_klasyfikacji = id_klasyfikacji;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof KlasyfikatorPK)) {
			return false;
		}
		KlasyfikatorPK castOther = (KlasyfikatorPK)other;
		return 
			(this.id_filmu == castOther.id_filmu)
			&& (this.id_klasyfikacji == castOther.id_klasyfikacji);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.id_filmu;
		hash = hash * prime + this.id_klasyfikacji;
		
		return hash;
	}
}