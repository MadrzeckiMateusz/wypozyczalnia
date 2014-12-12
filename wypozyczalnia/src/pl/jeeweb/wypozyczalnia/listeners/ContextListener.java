package pl.jeeweb.wypozyczalnia.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import pl.jeeweb.wypozyczalnia.config.DBManager;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		DBManager.getManager().createEntityManagerFactory();

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		 DBManager.getManager().closeEntityManagerFactory();

	}

}
