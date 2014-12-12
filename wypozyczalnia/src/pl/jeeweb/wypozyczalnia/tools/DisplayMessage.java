package pl.jeeweb.wypozyczalnia.tools;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class DisplayMessage {
	/***
	 * 
	 * @param context
	 * @param forM
	 * @param message
	 * @param type
	 *            1-info, 2-warn,3-error
	 */
	public static void InfoMessage(FacesContext context, String forM,
			String message, int type) {
		switch (type) {
		case 1:
			context.addMessage(forM, new FacesMessage(
					FacesMessage.SEVERITY_INFO, message, " "));
			break;
		case 2:
			context.addMessage(forM, new FacesMessage(
					FacesMessage.SEVERITY_WARN, message, " "));
			break;
		case 3:
			context.addMessage(forM, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, message, " "));
			break;
		case 4:
			context.addMessage(forM, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "icon"));
		default:
			break;
		}

	}
}
