package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KlasyfikacjaGatunku;
import pl.jeeweb.wypozyczalnia.tools.DisplayMessage;

@ManagedBean(name = "gatunkiBean")
@RequestScoped
public class GatunkiBean implements Serializable {
	private List<KlasyfikacjaGatunku> filteredGatunki = new ArrayList<>();
	private KlasyfikacjaGatunku selectedGatunek = null;

	public GatunkiBean() {

	}

	public List<KlasyfikacjaGatunku> getloadAllGatunki() {
		EntityManager em = DBManager.getManager().createEntityManager();

		List<KlasyfikacjaGatunku> listaAllGatunek = em.createNamedQuery(
				"KlasyfikacjaGatunku.findAll").getResultList();
		em.close();

		return listaAllGatunek;
	}
	
	public void przekierowanieEdycjaGatunku() {
		try {
			if(selectedGatunek == (null)) 
			{
				DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Nie wybra³es Gatunku!!!", 3);

			}
				else {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/editGatunek.xhtml?id_gatunku="
									+ selectedGatunek.getId_klasyfikacji());
			}
		} catch (IOException e) {
//			System.out.print("ssadsdassd");
			DisplayMessage.InfoMessage(FacesContext.getCurrentInstance(), "globalmessage", "Nie wybra³es Gatunku!!!", 3);
		}
	}
	
	public void przekierowanieDodajGatunek() {
		try {
			
				
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							"/wypozyczalnia/Zarzadzanie/addGatunek.xhtml");
			
		} catch (IOException e) {
			System.out.print("ssadsdassd");
			
		}
	}

	public List<KlasyfikacjaGatunku> getFilteredGatunki() {
		return filteredGatunki;
	}

	public void setFilteredGatunki(List<KlasyfikacjaGatunku> filteredGatunki) {
		this.filteredGatunki = filteredGatunki;
	}

	public KlasyfikacjaGatunku getSelectedGatunek() {
		return selectedGatunek;
	}

	public void setSelectedGatunek(KlasyfikacjaGatunku selectedGatunek) {
		this.selectedGatunek = selectedGatunek;
	}

}
