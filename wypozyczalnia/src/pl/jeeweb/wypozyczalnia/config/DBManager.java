package pl.jeeweb.wypozyczalnia.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBManager {
	private static DBManager instance;
	private EntityManagerFactory emf;

	private DBManager() {
	}

	public synchronized static DBManager getManager() {
		if (instance == null)
			instance = new DBManager();
		return instance;
	}

	public EntityManagerFactory createEntityManagerFactory() {
		if (emf == null)
			emf = Persistence.createEntityManagerFactory("Wypozyczalnia");
		return emf;
	}

	public EntityManager createEntityManager() {
		return this.createEntityManagerFactory().createEntityManager();
	}

	public void closeEntityManagerFactory() {
		if (emf != null) {
			emf.close();
			emf = null;
		}

	}
	public void refresh() {
		if(emf != null) {
			emf.close();
			emf = Persistence.createEntityManagerFactory("Wypozyczalnia");
		}
	}
}
