/**
 * 
 */
package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Pracownicy;
import pl.jeeweb.wypozyczalnia.entity.Role;
import pl.jeeweb.wypozyczalnia.entity.User;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;

/**
 * @author Mateusz
 * 
 */
@ManagedBean(name = "PracownikBean")
@ViewScoped
public class PracownikBean implements Serializable {
	private Pracownicy pracownik;
	private List<Pracownicy> filteredPracownicy;
	private Pracownicy selectedPracownik = null;
	private String haslo;
	private String haslo1;
	private String role;

	public List<Pracownicy> getAllPracownicy() {
		List<Pracownicy> allPracownicy = null;
		EntityManager em = DBManager.getManager().createEntityManager();
		allPracownicy = (List<Pracownicy>) em.createNamedQuery(
				"Pracownicy.findAll").getResultList();
		em.close();

		return allPracownicy;
	}

	public String getRole(Pracownicy pracownik) {
		EntityManager em = DBManager.getManager().createEntityManager();
		
		String roleS = "";
		Role userRole = (Role) em
				.createNamedQuery("Role.findByusername")
				.setParameter("username",
						pracownik.getE_mail()).getSingleResult();
		return userRole.getRolename();

	}

	public void changePermission() {
		if (selectedPracownik != null) {
			EntityManager em = DBManager.getManager().createEntityManager();

			try {
				em.getTransaction().begin();

				Role userRole = (Role) em
						.createNamedQuery("Role.findByusername")
						.setParameter("username",
								this.selectedPracownik.getE_mail())
						.getSingleResult();
				userRole.setRolename(role);
				em.persist(userRole);
				em.getTransaction().commit();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				em.getTransaction().rollback();
			}
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Uprawnienia zmienione", 1);

		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ pracownika", 3);
		}
	}

	public void saveHaslo() {
		if (selectedPracownik != null) {
			EntityManager em = DBManager.getManager().createEntityManager();
			User user = (User) em.createNamedQuery("User.findByEmail").setParameter("email", this.selectedPracownik.getE_mail()).getSingleResult();
			if (this.haslo.equals(this.haslo1)) {
				try {
					em.getTransaction().begin();
					this.selectedPracownik.setHaslo(SHA256hash
							.HashText(this.haslo));
					user.setPassword(this.selectedPracownik.getHaslo());
					em.merge(this.selectedPracownik);
					em.merge(user);
					em.getTransaction().commit();
				} catch (Exception e) {
					em.getTransaction().rollback();
				}
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Has³o zmienione pomyœlnie", 1);
				this.haslo="";
				this.haslo1="";
			} else {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Has³a nie s¹ identyczne", 3);
			}
		} else {
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³eœ pracownika", 3);
		}
	}

	public void przekierowanieDodajPracownika() {
		try {

			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/Admin/addPracownik.xhtml");

		} catch (IOException e) {
			System.out.print("ssadsdassd   " + e.getMessage());

		}
	}

	public void przekierowanieEdycjaPracownika() {
		try {
			if (selectedPracownik == (null)) {
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
						"globalmessage", "Nie wybra³es Pracownika!!!", 3);

			} else {
				FacesContext
						.getCurrentInstance()
						.getExternalContext()
						.redirect(
								"/wypozyczalnia/Zarzadzanie/Admin/editPracownik.xhtml?id_prac="
										+ selectedPracownik.getId_prcownika()
										+ "&target=/Zarzadzanie/Admin/listaPracownicy.xhtml");
			}
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(),
					"globalmessage", "Nie wybra³es Pracownika!!!", 3);
		}
	}

	/**
	 * @return the filteredPracownicy
	 */
	public List<Pracownicy> getFilteredPracownicy() {
		return filteredPracownicy;
	}

	/**
	 * @param filteredPracownicy
	 *            the filteredPracownicy to set
	 */
	public void setFilteredPracownicy(List<Pracownicy> filteredPracownicy) {
		this.filteredPracownicy = filteredPracownicy;
	}

	/**
	 * @return the selectedPracownik
	 */
	public Pracownicy getSelectedPracownik() {
		return selectedPracownik;
	}

	/**
	 * @param selectedPracownik
	 *            the selectedPracownik to set
	 */
	public void setSelectedPracownik(Pracownicy selectedPracownik) {
		this.selectedPracownik = selectedPracownik;
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
	 * @return the haslo
	 */
	public String getHaslo() {
		return haslo;
	}

	/**
	 * @param haslo
	 *            the haslo to set
	 */
	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	/**
	 * @return the haslo1
	 */
	public String getHaslo1() {
		return haslo1;
	}

	/**
	 * @param haslo1
	 *            the haslo1 to set
	 */
	public void setHaslo1(String haslo1) {
		this.haslo1 = haslo1;
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

}
