package org.getalp.blexisma.utils;
/*
 * @(#)ConceptualVector.java
 * Created on 10 Aug 2006
 * 
 * Copyright (c) 2006 Unit Terjemahan Melalui Komputer (UTMK)
 * Universiti Sains Malaysia, Pulau Pinang.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of 
 * Unit Terjemahan Melalui Komputer. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms
 * of the license agreement agreed you entered into with UTMK.
 */ 

import java.util.Calendar;

/**
 * This class provide some usefull utils for time.
 * 
 * @author didier
 * 
 */
public class TimeUtils {
	/**
	 * the number of milliseconds in a day
	 */
	public static final long millisInADay = 86400000l;

	/**
	 * the number of milliseconds in a minute
	 */
	public static final long millisInAMin = 60000l;

	/**
	 * the number of milliseconds in an hour
	 */
	public static final long millisInAnHour = 3600000l;

	/**
	 * The constant value for French
	 */
	public static final int FRENCH = 0;

	/**
	 * The constant value for English
	 */
	public static final int ENGLISH = 1;

	/**
	 * The constant value for Malay
	 */
	public static final int MALAY = 2;

	/**
	 * Returns a String which is an helpful represent of time for human. Instead
	 * of a double it gives how many days, hours, minutes, seconds and
	 * millisecond it correspond. You can choose the language to display
	 * according constants defined in the class.
	 * <P>
	 * Examples :
	 * </P>
	 * <P>
	 * TimeUtils.formatTime(2222222222l, TimeUtils.FRENCH) gives 25 jours 17
	 * heures 17 minutes 2 secondes 222 millisecondes
	 * </P>
	 * <P>
	 * TimeUtils.formatTime(2222222222l, TimeUtils.ENGLISH) gives 25 days 17
	 * hours 17 minutes 2 seconds 222 milliseconds
	 * </P>
	 * 
	 * @see #FRENCH
	 * @see #ENGLISH
	 * @see #MALAY
	 * 
	 * @param millisTime
	 *            the time to display
	 * @param language
	 *            the language in which the time is displayed
	 * @return a representation of the time helpful for human
	 */
	public static String formatTime(double millisTime, int language) {

		return formatTime((long) millisTime, language);
	}

	/**
	 * Returns a String which is an helpful represent of time for human.
	 * 
	 * @param millisTime
	 *            the time to display
	 * @param language
	 *            the language in which the time is displayed
	 * @return a representation of the time helpful for human
	 * @see #formatTime(double millisTime, int language)
	 */
	public static String formatTime(long millisTime, int language) {

		long reste = millisTime;
		int days, hours, minutes, seconds, milliseconds;

		days = (int) (reste / millisInADay);
		reste = reste % (long) millisInADay;

		hours = (int) (reste / millisInAnHour);
		reste = reste % (long) millisInAnHour;

		minutes = (int) (reste / millisInAMin);
		reste = reste % (long) millisInAMin;

		seconds = (int) (reste / 1000);
		milliseconds = (int) (reste % 1000);

		return format(days, hours, minutes, seconds, milliseconds, language);
	}

	/**
	 * Returns a String which is an helpful represent of time for human.
	 * 
	 * @param millisTime
	 *            the time to display
	 * @return a representation of the time helpful for human in English
	 * @see #formatTime(double millisTime, int language)
	 */
	public static String formatTime(double millisTime) {

		return formatTime((long) millisTime, FRENCH);
	}

	/**
	 * Returns a String which is an helpful represent of time for human.
	 * 
	 * @param millisTime
	 *            the time to display
	 * @return a representation of the time helpful for human in English
	 * @see #formatTime(double millisTime, int language)
	 */
	public static String formatTime(long millisTime) {

		return formatTime(millisTime, ENGLISH);
	}

	private static String format(int days, int hours, int minutes, int seconds,
			int milliseconds, int language) {

		StringBuffer res = new StringBuffer(200);

		switch (language) {

		case (ENGLISH): {
			if (days != 0)
				res.append(days + " days ");
			if (hours != 0)
				res.append(hours + " hours ");
			if (minutes != 0)
				res.append(minutes + " minutes ");
			if (seconds != 0)
				res.append(seconds + " seconds ");
			if (milliseconds != 0)
				res.append(milliseconds + " milliseconds");
			break;
		}
		case (MALAY): {
			if (days != 0)
				res.append(days + " hari ");
			if (hours != 0)
				res.append(hours + " jam ");
			if (minutes != 0)
				res.append(minutes + " minit ");
			if (seconds != 0)
				res.append(seconds + " saat ");
			if (milliseconds != 0)
				res.append(milliseconds + " milisaat");
			break;
		}
		default: {
			if (days != 0)
				res.append(days + " jours ");
			if (hours != 0)
				res.append(hours + " heures ");
			if (minutes != 0)
				res.append(minutes + " minutes ");
			if (seconds != 0)
				res.append(seconds + " secondes ");
			if (milliseconds != 0)
				res.append(milliseconds + " millisecondes");
			break;
		}
		}
		return res.toString();
	}

	public static long howManyMillisTo(int hour, int min, int s) {

		Calendar now = Calendar.getInstance();
		long nowmillis = now.get(Calendar.ZONE_OFFSET) + now.get(Calendar.HOUR)
				* 3600000 + now.get(Calendar.MINUTE) * 60000
				+ now.get(Calendar.SECOND) * 1000;
		long tomillis = hour * 3600000 + min * 60000 + s * 1000;

		return tomillis - nowmillis;
	}
}
