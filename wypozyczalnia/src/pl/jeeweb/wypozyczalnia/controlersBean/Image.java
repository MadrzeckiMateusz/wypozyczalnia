package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.persistence.EntityManager;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import pl.jeeweb.wypozyczalnia.config.DBManager;
import pl.jeeweb.wypozyczalnia.entity.Filmy;

@ManagedBean
@RequestScoped
public class Image {

	private StreamedContent image = new DefaultStreamedContent();

	@ManagedProperty("#{param.id}")
	private int id = 0;

	@PostConstruct
	public void init() {
		if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so
			// that it will generate right URL.
			InputStream image_default = this.getClass().getResourceAsStream("brak_zdjecia.png");
			image = new DefaultStreamedContent(image_default,"image/jpeg");
		} else {
			// So, browser is requesting the image. Return a real
			// StreamedContent with the image bytes.
			if (id != 0) {
				EntityManager em = DBManager.getManager().createEntityManager();
				Filmy film_by_id = em.find(Filmy.class, id);

				image = new DefaultStreamedContent(new ByteArrayInputStream(
						film_by_id.getPlakat()));
			}
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public StreamedContent getImage() {
		return image;
	}

}