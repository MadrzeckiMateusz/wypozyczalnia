/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Role;
import pl.jeeweb.wypozyczalnia.entity.User;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "PracownikEditBean")
@ViewScoped
public class PracownikEditBean implements Serializable {
 	private Pracownicy pracownik;
 	private Role rola;
 	private User user;
 	
 	@PostConstruct
 	public void initBean() {

		int id_pracownika = Integer.valueOf(FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("id_prac"));	
		EntityManager em = DBManager.getManager().createEntityManager();
		this.pracownik = (Pracownicy) em.createNamedQuery("Pracownicy.findByid").setParameter("id", id_pracownika).getSingleResult();
		this.rola = (Role) em.createNamedQuery("Role.findByusername").setParameter("username",this.pracownik.getE_mail()).getSingleResult();
		this.user = (User) em.createNamedQuery("User.findByEmail").setParameter("email", this.pracownik.getE_mail()).getSingleResult();
		em.close();
	}

 	public void updatePracownik() {
		EntityManager em = DBManager.getManager().createEntityManager();
		try {
			em.getTransaction().begin();
			 em.merge(this.pracownik);
			 em.merge(this.rola);
			 em.merge(this.user);
			 em.getTransaction().commit();
			 DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Dane zaktualizowane", 1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			em.getTransaction().rollback();
		}finally {
			em.close();
		}
		

 	}
	/**
	 * @return the pracownik
	 */
	public Pracownicy getPracownik() {
		return pracownik;
	}

	/**
	 * @param pracownik the pracownik to set
	 */
	public void setPracownik(Pracownicy pracownik) {
		this.pracownik = pracownik;
	}
 	
}
