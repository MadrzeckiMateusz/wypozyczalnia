package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "GatunkiEditBean")
@ViewScoped
public class GatunkiEditBean implements Serializable {

	private KlasyfikacjaGatunku gatunek;
	private int id;

	@PostConstruct
	public void loadSelected() {
		id = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("id_gatunku"));
		if (id != 0) {
			EntityManager em = DBManager.getManager().createEntityManager();
			this.gatunek = (KlasyfikacjaGatunku) em
					.createNamedQuery("KlasyfikacjaGatunku.getById")
					.setParameter("id", id).getSingleResult();
			em.close();
		}
	}

	public String saveEditedGatunek() {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		em.merge(this.gatunek);
		em.getTransaction().commit();
		em.close();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
				"globalmessage", "Gatunek zapisany pomyœlnie", 1);
		return null;
	}

	public KlasyfikacjaGatunku getGatunek() {
		return gatunek;
	}

	public void setGatunek(KlasyfikacjaGatunku gatunek) {
		this.gatunek = gatunek;
	}

}
