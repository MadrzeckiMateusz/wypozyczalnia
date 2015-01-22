/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Role;
import pl.jeeweb.wypozyczalnia.entity.User;
import pl.jeeweb.wypozyczalnia.tools.DateTools;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "PracownikAddBean")
public class PracownikAddNewBean implements Serializable {

	private Pracownicy pracownik = new Pracownicy();
	private String nowehaslo;
	private String nowehaslo1;
	private String role;
	private Map<String, String> roles;

	@PostConstruct
	public void initBean() {
		roles = new HashMap<String, String>();
		roles.put("Administrator", "admin");
		roles.put("Pracownik", "pracownik");

		EntityManager em = DBManager.getManager().createEntityManager();
		int maxid = (int) em.createNamedQuery("Pracownicy.getMaxId")
				.getSingleResult();

		this.pracownik.setNr_pracownika(setNumerPracownika(maxid));
		this.pracownik.setData_zatrudnienia(DateTools.currentDate());
		em.close();
	}

	public void savePracownik() {
		EntityManager em = DBManager.getManager().createEntityManager();

		if (this.nowehaslo.equals(this.nowehaslo1)) {
			try {
				em.getTransaction().begin();
				this.pracownik.setHaslo(SHA256hash.HashText(this.nowehaslo));
				Pracownicy pracownik_merge = em.merge(this.pracownik);
				User user = new User();
				user.setPassword(pracownik_merge.getHaslo());
				user.setUsername(pracownik_merge.getE_mail());
				em.persist(user);
				Role userRole = new Role();

				userRole.setRolename(role);
				userRole.setUsername(pracownik_merge.getE_mail());

				em.persist(userRole);
				em.getTransaction().commit();
			} catch (Exception e) {
				em.getTransaction().rollback();
			}
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Pracownik doday pomyœlnie", 1);
			FacesContext.getCurrentInstance().getExternalContext().getFlash()
					.setKeepMessages(true);
			try {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/Admin/listaPracownicy.xhtml");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Has³a nie s¹ identyczn", 3);
		}
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

	/**
	 * @return the pracownik
	 */
	public Pracownicy getPracownik() {
		return pracownik;
	}

	/**
	 * @param pracownik
	 *            the pracownik to set
	 */
	public void setPracownik(Pracownicy pracownik) {
		this.pracownik = pracownik;
	}

	/**
	 * @return the nowehaslo
	 */
	public String getNowehaslo() {
		return nowehaslo;
	}

	/**
	 * @param nowehaslo
	 *            the nowehaslo to set
	 */
	public void setNowehaslo(String nowehaslo) {
		this.nowehaslo = nowehaslo;
	}

	/**
	 * @return the nowehaslo1
	 */
	public String getNowehaslo1() {
		return nowehaslo1;
	}

	/**
	 * @param nowehaslo1
	 *            the nowehaslo1 to set
	 */
	public void setNowehaslo1(String nowehaslo1) {
		this.nowehaslo1 = nowehaslo1;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the roles
	 */
	public Map<String, String> getRoles() {
		return roles;
	}

}
