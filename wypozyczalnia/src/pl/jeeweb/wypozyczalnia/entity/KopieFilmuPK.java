package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the kopie_filmu database table.
 * 
 */
@Embeddable
public class KopieFilmuPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(insertable=true, updatable=true, unique=true, nullable=false)
	private int id_filmu;

	@Column(unique=true, nullable=false)
	private int id_kopii;

	public KopieFilmuPK() {
	}
	public int getId_filmu() {
		return this.id_filmu;
	}
	public void setId_filmu(int id_filmu) {
		this.id_filmu = id_filmu;
	}
	public int getId_kopii() {
		return this.id_kopii;
	}
	public void setId_kopii(int id_kopii) {
		this.id_kopii = id_kopii;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof KopieFilmuPK)) {
			return false;
		}
		KopieFilmuPK castOther = (KopieFilmuPK)other;
		return 
			(this.id_filmu == castOther.id_filmu)
			&& (this.id_kopii == castOther.id_kopii);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.id_filmu;
		hash = hash * prime + this.id_kopii;
		
		return hash;
	}
}