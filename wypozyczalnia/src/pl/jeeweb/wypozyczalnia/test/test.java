package pl.jeeweb.wypozyczalnia.test;


import java.util.List;

import pl.jeeweb.wypozyczalnia.controlersBean.RezerwacjeKlientaBean;
import pl.jeeweb.wypozyczalnia.entity.Filmy;
import pl.jeeweb.wypozyczalnia.entity.KopieFilmu;
import pl.jeeweb.wypozyczalnia.entity.Rezerwacje;
import pl.jeeweb.wypozyczalnia.tools.SHA256hash;


public abstract class test {

	public static void main(String[] args) {
		
		
//		RezerwacjeKlientaBean  rezerwacja = new RezerwacjeKlientaBean();
//		List<Rezerwacje> reser = rezerwacja.getklientbyid();
//		
//		List <KopieFilmu> sdd= reser.get(0).getKopieFilmus();
//		Filmy film = sdd.get(0).getFilmy();
//		System.out.print("ASdasd");
		System.out.print(SHA256hash.HashText("123"));
	}

}
