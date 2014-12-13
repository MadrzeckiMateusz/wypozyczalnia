package pl.jeeweb.wypozyczalnia.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the klasyfikacja_gatunku database table.
 * 
 */
@Entity
@Table(name="klasyfikacja_gatunku")
@NamedQueries({
	@NamedQuery(name="KlasyfikacjaGatunku.findAll", query="SELECT k FROM KlasyfikacjaGatunku k"),
	@NamedQuery(name="KlasyfikacjaGatunku.getbygatunek",query="SELECT k FROM KlasyfikacjaGatunku k where k.gatunek = :gatunek")})
public class KlasyfikacjaGatunku implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id_klasyfikacji;

	@Column(length=50)
	private String gatunek;

	//bi-directional many-to-many association to Filmy
	@ManyToMany(mappedBy="klasyfikacjaGatunkus" , fetch= FetchType.EAGER)
	private List<Filmy> filmies;

	public KlasyfikacjaGatunku() {
	}

	public int getId_klasyfikacji() {
		return this.id_klasyfikacji;
	}

	public void setId_klasyfikacji(int id_klasyfikacji) {
		this.id_klasyfikacji = id_klasyfikacji;
	}

	public String getGatunek() {
		return this.gatunek;
	}

	public void setGatunek(String gatunek) {
		this.gatunek = gatunek;
	}

	public List<Filmy> getFilmies() {
		return this.filmies;
	}

	public void setFilmies(List<Filmy> filmies) {
		this.filmies = filmies;
	}

}