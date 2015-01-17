/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;

/**
 * @author Mateusz
 *
 */
public abstract class Converters {

	public static String klasyfikacjaGatunkuToString(Filmy film) {
		String gatunek = "";
		for (KlasyfikacjaGatunku gatunekFilmu : film.getKlasyfikacjaGatunkus()) {
			gatunek = gatunek + gatunekFilmu.getGatunek() + ", ";
		}

		return gatunek;
	}
}
