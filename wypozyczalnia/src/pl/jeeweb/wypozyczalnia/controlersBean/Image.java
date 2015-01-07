package pl.jeeweb.wypozyczalnia.controlersBean;

import java.io.ByteArrayInputStream;

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

    private StreamedContent image;

    @ManagedProperty("#{param.id}")
    private int id;

    

    @PostConstruct
    public void init() {
        if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            image = new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
        
    		EntityManager em = DBManager.getManager().createEntityManager();
    		Filmy film_by_id = em.find(Filmy.class, id);
    		
            image = new DefaultStreamedContent(new ByteArrayInputStream(film_by_id.getPlakat()));
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public StreamedContent getImage() {
        return image;
    }

}