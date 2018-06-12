package fr.ign.cogit.geoxygene.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.threeten.extra.Interval;

/**
 * Some methods on Java date and time objects
 * 
 * @author QTTruong
 *
 */
public class DateTime {
	/**
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return the number of weeks between beginDate and endDate
	 * @throws ParseException
	 */
	public static int getNbOfWeeks(String beginDate, String endDate, String dateFormat) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat(dateFormat); // "yyyy-MM-dd"

		Date beginD = formatDate.parse(beginDate);
		Date endD = formatDate.parse(endDate);

		// Instant t1 = beginD.toInstant();
		// Instant t2 = endD.toInstant();
		// LocalDateTime startLocalDate = LocalDateTime.ofInstant(t1,
		// ZoneId.systemDefault());
		// LocalDateTime endLocalDate = LocalDateTime.ofInstant(t2,
		// ZoneId.systemDefault());

		Calendar cl1 = Calendar.getInstance();
		cl1.setTime(beginD);
		// System.out.println("Week year t1 :" +
		// cl1.get(Calendar.WEEK_OF_YEAR));

		Calendar cl2 = Calendar.getInstance();
		cl2.setTime(endD);
		// System.out.println("Week year t2 :" +
		// cl2.get(Calendar.WEEK_OF_YEAR));
		// System.out.println("Différence " + (cl2.get(Calendar.WEEK_OF_YEAR) -
		// cl1.get(Calendar.WEEK_OF_YEAR)));

		return cl2.get(Calendar.WEEK_OF_YEAR) - cl1.get(Calendar.WEEK_OF_YEAR);

	}

	public static int getYear(String date, String dateFormat) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat(dateFormat); // "yyyy-MM-dd"
																	// or
																	// "yyyy-MM-dd'T'HH:mm:ss'Z'"

		Date d = formatDate.parse(date);
		Calendar cl1 = Calendar.getInstance();
		cl1.setTime(d);
		return cl1.get(Calendar.YEAR);
	}

	public static int getWeekOfYear(String date, String dateFormat) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat(dateFormat); // "yyyy-MM-dd"
																	// or
																	// "yyyy-MM-dd'T'HH:mm:ss'Z'"
		Date d = formatDate.parse(date);
		Calendar cl = Calendar.getInstance();
		cl.setTime(d);
		return cl.get(Calendar.WEEK_OF_YEAR);

	}

	public static int getNbWeeksInYear(String date, String dateFormat) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat(dateFormat); // "yyyy-MM-dd"
																	// or
																	// "yyyy-MM-dd'T'HH:mm:ss'Z'"
		Date d = formatDate.parse(date);
		Calendar cl = Calendar.getInstance();
		cl.setTime(d);
		return cl.getWeeksInWeekYear();

	}

	public static int getWeeksBetween(String d1, String d2, String dateFormat) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat(dateFormat);
		Date a = formatDate.parse(d1);
		Date b = formatDate.parse(d2);
		if (b.before(a)) {
			return -getWeeksBetween(d2, d1, dateFormat);
		}
		a = resetTime(d1, dateFormat);
		b = resetTime(d2, dateFormat);

		Calendar cal = new GregorianCalendar();
		cal.setTime(a);
		int weeks = 0;
		while (cal.getTime().before(b)) {
			// add another week
			cal.add(Calendar.WEEK_OF_YEAR, 1);
			weeks++;
		}
		return weeks;
	}

	public static Date resetTime(String date, String dateFormat) throws ParseException {
		DateFormat formatDate = new SimpleDateFormat(dateFormat);
		Date d = formatDate.parse(date);
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Buffer temporel : dilate/érode l'intervalle en avançant la borne
	 * inférieure et en reculant la borne supérieure. Pour une érosion, il faut
	 * que l'offset soit négatif
	 * 
	 * @param initial
	 * @param offset
	 * @param t
	 */
	public static void buffer(Interval interval, long offset, TemporalUnit t) {
		System.out.println(
				" Interval initial : " + interval.getStart().toString() + " - " + interval.getEnd().toString());
		interval.getStart().minus(offset, t);
		interval.getEnd().plus(offset, t);
		System.out.println(
				" Interval + buffer : " + interval.getStart().toString() + " - " + interval.getEnd().toString());
	}

	/**
	 * Union des intervalles qui se chevauchent
	 * 
	 * @param intervalList
	 * @return a new list of intervals
	 */
	public static List<Interval> unionConnectedInterval(List<Interval> intervalList) {
		List<Interval> unionList = new ArrayList<Interval>();
		int next = 1;

		for (int i = 0; i < intervalList.size(); i += next) {
			Interval currentInterval = intervalList.get(i);
			next = 1;

			if (i + next >= intervalList.size()) {
				// Cas où on arrive au bout de l'intervalle
				unionList.add(currentInterval);
				break;
			}

			Interval nextInterval = intervalList.get(i + next);
			Interval union = currentInterval;

			while (currentInterval.isConnected(nextInterval) && (i + next) < intervalList.size()) {
				// Fait l'union avec l'intervalle suivant tant qu'ils se
				// chevauchent et que la fin de la liste n'est pas atteinte
				union = currentInterval.union(nextInterval);
				next++;
				if (i + next >= intervalList.size())
					break;
				nextInterval = intervalList.get(i + next);
			}

			unionList.add(union);
		}

		return unionList;
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(getNbOfWeeks("2018-06-01", "2018-06-02", "yyyy-MM-dd"));
		System.out.print(getWeeksBetween("2018-06-01", "2018-06-02", "yyyy-MM-dd") + "\n");
		Integer a = 1;
		Integer b = a;
		System.out.println("a = " + a + " - b = " + b);
		b = b + 1;
		System.out.println("a = " + a + " - b = " + b);

		// System.out.println(getYear("1987-06-21", "yyyy-MM-dd"));
		// System.out.println(getWeekOfYear("1987-06-21", "yyyy-MM-dd"));
		// System.out.println(getWeekOfYear("1987-06-21T18:46:19Z",
		// "yyyy-MM-dd'T'HH:mm:ss'Z'"));
		//
		// System.out.println(getNbWeeksInYear("1987-06-21T18:46:19Z",
		// "yyyy-MM-dd'T'HH:mm:ss'Z'"));

	}

}
