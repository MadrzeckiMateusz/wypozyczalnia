package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;


@ManagedBean(name="GatunkiAddNewBean")
@SessionScoped
public class GatunkiAddNewBean implements Serializable {
	
	private KlasyfikacjaGatunku gatunek;
	
	public GatunkiAddNewBean() {
		
	}
	@PostConstruct
	public void initBean() {
		this.gatunek = new KlasyfikacjaGatunku();
		
	}

	public void saveGatunek() {
		EntityManager em = DBManager.getManager().createEntityManager();
		em.getTransaction().begin();
		em.persist(this.gatunek);
		em.getTransaction().commit();
		em.close();
		initBean();
		DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Gatunek zosta³ dodany pomyœlnie", 1);
	
	}
	public KlasyfikacjaGatunku getGatunek() {
		return gatunek;
	}

	public void setGatunek(KlasyfikacjaGatunku gatunek) {
		this.gatunek = gatunek;
	}
}
