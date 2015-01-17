/**
 * 
 */
package pl.jeeweb.wypozyczalnia.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mateusz
 *
 */
public abstract class DateTools {
	
	public static Date currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();

		return date;
	}

	public static Date nextDate(int days) {
		final long ONE_DAY_MILLIS = 86400 * 1000;
		Date now = new Date();
		Date date = new Date(now.getTime() + (3 * ONE_DAY_MILLIS));

		return date;
	}

}
