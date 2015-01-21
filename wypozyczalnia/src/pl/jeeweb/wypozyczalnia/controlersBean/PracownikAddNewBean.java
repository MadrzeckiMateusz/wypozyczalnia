/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;

/**
 * @author Mateusz
 *
 */
@ManagedBean(name="PracownikAddBean")
public class PracownikAddNewBean implements Serializable {
	
	private Pracownicy pracownik = new Pracownicy();
	
	
	@PostConstruct
	public void initBean() {
		EntityManager em = DBManager.getManager().createEntityManager();
		int maxid = (int) em.createNamedQuery("Pracownicy.getMaxId").getSingleResult();
		
		this.pracownik.setNr_pracownika(setNumerPracownika(maxid));
	}
	private String setNumerPracownika(int id) {
		Integer i = id;
		i += 1;
		String numerKlienta = "";
		if (i < 10)
			numerKlienta = "PR/000" + i.toString();
		if (i >= 10 && i < 100)
			numerKlienta = "PR/00" + i.toString();
		if (i >= 100 && i < 1000)
			numerKlienta = "PR/0" + i.toString();
		if (i >= 1000)
			numerKlienta = "PR/" + i.toString();
		return numerKlienta;
	}

}
