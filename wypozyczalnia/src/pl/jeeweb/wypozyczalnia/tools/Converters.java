/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmuPK;

/**
 * @author Mateusz
 *
 */
@ManagedBean(name= "Converters")
@RequestScoped
public class Converters implements Serializable{

	public static String klasyfikacjaGatunkuToString(Filmy film) {
		String gatunek = "";
		for (KlasyfikacjaGatunku gatunekFilmu : film.getKlasyfikacjaGatunkus()) {
			gatunek = gatunek + gatunekFilmu.getGatunek() + ", ";
		}

		return gatunek;
	}
	public static String getNumerkopiiFlmu(KopieFilmu kopiaFilmu) {
		KopieFilmuPK kopiaPk = kopiaFilmu.getId();
		return kopiaFilmu.getFilmy().getNr_filmu() + "/"
				+ kopiaPk.getId_kopii();

	}
}
