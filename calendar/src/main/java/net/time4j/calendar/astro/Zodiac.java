/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Zodiac.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.calendar.astro;

import net.time4j.Moment;
import net.time4j.engine.ChronoCondition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;


/**
 * <p>Enumeration of the thirteen astronomical zodiac definitions. </p>
 *
 * <p>See also <a href="https://en.wikipedia.org/wiki/Zodiac">Wikipedia</a>. The boundaries
 * of the associated <a href="https://www.iau.org/public/themes/constellations/">constellations</a>
 * have been defined by IAU (International Astronomical Union). </p>
 *
 * <p><strong>Attention</strong>: Users are required to make a strict difference between constellations
 * and the twelve astrological (horoscopic) zodiacs. Latter type is also supported by specialized methods. </p>
 *
 * @author 	Meno Hochschild
 * @since 	4.37
 */
/*[deutsch]
 * <p>Aufz&auml;hlung der dreizehn astronomischen Tierkreissternbilder. </p>
 *
 * <p>Siehe auch <a href="https://en.wikipedia.org/wiki/Zodiac">Wikipedia</a>. Die Grenzen
 * der damit verbundenen <a href="https://www.iau.org/public/themes/constellations/">Konstellationen</a>
 * sind von der IAU (Internationale Astronomische Union) definiert worden. </p>
 *
 * <p><strong>Achtung</strong>: Anwender m&uuml;ssen im Gebrauch streng zwischen Sternbildern und
 * astrologischen (horoskopischen) Tierkreiszeichen unterscheiden. Letztere werden &uuml;ber spezielle
 * Methoden ebenfalls unterst&uuml;tzt. </p>
 *
 * @author 	Meno Hochschild
 * @since 	4.37
 */
public enum Zodiac {

	//~ Statische Felder/Initialisierungen --------------------------------

	ARIES('\u2648', 26.766, 11.048), // longitude => 28.8

	TAURUS('\u2649', 51.113, 18.648), // longitude => 53.5

	GEMINI('\u264A', 90.218, 23.439), // longitude => 90.2

	CANCER('\u264B', 120.198, 20.542), // longitude => 118.1

	LEO('\u264C', 140.637, 15.375), // longitude => 138.2

	VIRGO('\u264D', 174.400, 2.423), // longitude => 173.9

	LIBRA('\u264E', 215.634, -14.176), // longitude => 218.0

	SCORPIUS('\u264F', 238.861, -20.359), // longitude => 241.0

	OPHIUCHUS('\u26CE', 245.915, -21.594), // longitude => 247.7

	SAGITTARIUS('\u2650', 265.968, -23.388), // longitude => 266.3

	CAPRICORNUS('\u2651', 301.869, -20.214), // longitude => 299.7

	AQUARIUS('\u2652', 329.790, -12.306), // longitude => 327.6

	PISCES('\u2653', 352.284, -3.331); // longitude => 351.6

	private static final Map<String, String[]> LANG_TO_NAMES;

	static {
		Map<String, String[]> lang2names = new HashMap<String, String[]>();
		lang2names.put(
			"",
			new String[]
				{"Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra",
					"Scorpius", "Ophiuchus", "Sagittarius", "Capricornus", "Aquarius", "Pisces"});
		lang2names.put(
			"da",
			new String[]
				{"Vædderen", "Tyren", "Tvillingerne", "Krebsen", "Løven", "Jomfruen", "Vægten",
					"Skorpionen", "Slangebæreren", "Skytten", "Stenbukken", "Vandmanden", "Fiskene"});
		lang2names.put(
			"de",
			new String[]
				{"Widder", "Stier", "Zwillinge", "Krebs", "Löwe", "Jungfrau", "Waage",
					"Skorpion", "Schlangenträger", "Schütze", "Steinbock", "Wassermann", "Fische"});
		lang2names.put(
			"en",
			new String[]
				{"Ram", "Bull", "Twins", "Crab", "Lion", "Maiden", "Scales",
					"Scorpion", "Serpent-bearer", "Archer", "Capricorn", "Water-bearer", "Fish"});
		lang2names.put(
			"es",
			new String[]
				{"Aries", "Tauro", "Géminis", "Cáncer", "Leo", "Virgo", "Libra",
					"Escorpio", "Ofiuco", "Sagitario", "Capricornio", "Acuario", "Piscis"});
		lang2names.put(
			"fr",
			new String[]
				{"Bélier", "Taureau", "Gémeaux", "Cancer", "Lion", "Vierge", "Balance",
					"Scorpion", "Serpentaire", "Sagittaire", "Capricorne", "Verseau", "Poissons"});
		lang2names.put(
			"it",
			new String[]
				{"Ariete", "Toro", "Gemelli", "Cancro", "Leone", "Vergine", "Bilancia",
					"Scorpione", "Ofiuco", "Sagittario", "Capricorno", "Acquario", "Pesci"});
		lang2names.put(
			"nl",
			new String[]
				{"Ram", "Stier", "Tweelingen", "Kreeft", "Leeuw", "Maagd", "Weegschaal",
					"Schorpioen", "Slangendrager", "Schutter", "Steenbok", "Waterman", "Vissen"});
		lang2names.put(
			"ru",
			new String[]
				{"Овен", "Телец", "Близнецы", "Рак", "Лев", "Дева", "Весы",
					"Скорпион", "Змееносец", "Стрелец", "Козерог", "Водолей", "Рыбы"});
		lang2names.put(
			"tr",
			new String[]
				{"Koç", "Boğa", "İkizler", "Yengeç", "Aslan", "Başak", "Terazi",
					"Akrep", "Ophiuchus", "Yay", "Oğlak", "Kova", "Balık"});
		LANG_TO_NAMES = Collections.unmodifiableMap(lang2names);
	}

	private static final double MEAN_TROPICAL_YEAR = 365.242189;
	private static final double MEAN_SYNODIC_MONTH = 29.530588861;

	//~ Instanzvariablen --------------------------------------------------

	private transient final char symbol;
	private transient final EquatorialCoordinates entry;

	//~ Konstruktoren -----------------------------------------------------

	private Zodiac(
		char symbol,
		double ra,
		double dec
	) {
		this.symbol = symbol;
		this.entry = new SkyPosition(ra, dec); // using J2000
	}

	//~ Methoden ----------------------------------------------------------

	/**
	 * <p>Determines the zodiac constellation passed by the sun at given moment. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac constellation
	 */
	/*[deutsch]
	 * <p>Bestimmt das Tierkreissternbild, das von der Sonne zum angegebenen Zeitpunkt durchschritten wird. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac constellation
	 */
	public static Zodiac constellationPassedBySun(Moment moment) {

		return Zodiac.of('S', moment, false);

	}

	/**
	 * <p>Determines the zodiac constellation passed by the moon at given moment. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac constellation
	 */
	/*[deutsch]
	 * <p>Bestimmt das Tierkreissternbild, das vom Mond zum angegebenen Zeitpunkt durchschritten wird. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac constellation
	 */
	public static Zodiac constellationPassedByMoon(Moment moment) {

		return Zodiac.of('L', moment, false);

	}

	/**
	 * <p>Determines the zodiac sign passed by the sun at given moment. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac sign
	 */
	/*[deutsch]
	 * <p>Bestimmt das Tierkreiszeichen, das von der Sonne zum angegebenen Zeitpunkt durchschritten wird. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac sign
	 */
	public static Zodiac signPassedBySun(Moment moment) {

		return Zodiac.of('S', moment, true);

	}

	/**
	 * <p>Determines the zodiac sign passed by the moon at given moment. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac sign
	 */
	/*[deutsch]
	 * <p>Bestimmt das Tierkreiszeichen, das vom Mond zum angegebenen Zeitpunkt durchschritten wird. </p>
	 *
	 * @param 	moment		the moment when any zodiac is passed
	 * @return	Zodiac sign
	 */
	public static Zodiac signPassedByMoon(Moment moment) {

		return Zodiac.of('L', moment, true);

	}

	/**
	 * <p>Obtains the associated symbol character. </p>
	 *
	 * @return char
	 */
	/*[deutsch]
	 * <p>Liefert das assozierte Symbolzeichen. </p>
	 *
	 * @return	char
	 */
	public char getSymbol() {

		return this.symbol;

	}

	/**
	 * <p>Obtains a localized name. </p>
	 *
	 * @param locale language
	 * @return localized name or the Latin name if given language is not supported
	 */
	/*[deutsch]
	 * <p>Liefert einen sprachabh&auml;ngigen Namen. </p>
	 *
	 * @param 	locale		language
	 * @return	localized name or the Latin name if given language is not supported
	 */
	public String getDisplayName(Locale locale) {

		String[] names = LANG_TO_NAMES.get(locale.getLanguage());

		if (names == null) {
			names = LANG_TO_NAMES.get("");
		}

		return names[this.ordinal()];

	}

	/**
	 * <p>Obtains the zodiac which happens previous before this zodiac. </p>
	 *
	 * @return	previous zodiac
	 */
	/*[deutsch]
	 * <p>Liefert das Tierkreissternbild, das von der Sonne direkt
	 * vor diesem Tierkreissternbild erreicht wird. </p>
	 *
	 * @return	previous zodiac
	 */
	public Zodiac previous() {

		return Zodiac.values()[(this.ordinal() + 12) % 13];

	}

	/**
	 * <p>Obtains the zodiac which happens next after this zodiac. </p>
	 *
	 * @return	next zodiac
	 */
	/*[deutsch]
	 * <p>Liefert das Tierkreissternbild, das von der Sonne am n&auml;chsten
	 * nach diesem Tierkreissternbild erreicht wird. </p>
	 *
	 * @return	next zodiac
	 */
	public Zodiac next() {

		return Zodiac.values()[(this.ordinal() + 1) % 13];

	}

	private static Zodiac of(
		char body,
		Moment moment,
		boolean horoscope
	) {
		Moment time = moment.with(Moment.PRECISION, TimeUnit.MINUTES);
		double jde = JulianDay.ofEphemerisTime(time).getValue();
		double lng = (body == 'S') ? getSolarLongitude(jde) : getLunarLongitude(jde);

		double start;
		double end;

		for (Zodiac zodiac : Zodiac.values()) {
			Zodiac next = zodiac.next();

			if (horoscope) {
				if (zodiac == OPHIUCHUS) {
					continue;
				} else if (next == OPHIUCHUS) {
					next = SAGITTARIUS;
				}
				int offset1 = (zodiac.compareTo(OPHIUCHUS) < 0) ? 0 : -1;
				int offset2 = (next.compareTo(OPHIUCHUS) < 0) ? 0 : -1;
				start = (zodiac.ordinal() + offset1) * 30;
				end = (next.ordinal() + offset2) * 30;
			} else {
				start = toEclipticAngle(time, zodiac.entry.getRightAscension(), zodiac.entry.getDeclination());
				end = toEclipticAngle(time, next.entry.getRightAscension(), next.entry.getDeclination());
			}

			if (end < start) {
				end += 360;
				if (lng < 180) {
					lng += 360;
				}
			}

			if ((lng >= start) && (lng < end)) {
				return zodiac;
			}
		}

		throw new NoSuchElementException("Unable to determine zodiac."); // should never happen
	}

	private static double getSolarLongitude(double jde) {
		return StdSolarCalculator.TIME4J.getFeature(jde, "solar-longitude");
	}

	private static double getLunarLongitude(double jde) {
		return MoonPosition.lunarLongitude(jde);
	}

	private static double toEclipticAngle(
		Moment moment,
		double raJ2000,
		double decJ2000
	) {
		// approximation, see also Meeus (p. 92) about obliquity
		double jct = JulianDay.ofSimplifiedTime(moment).getCenturyJ2000();
		double meanObliquity = Math.toRadians(StdSolarCalculator.meanObliquity(jct));

		// apply precession (Meeus 21.3 + 21.4, verified with example 21.b)
		double eta = (2306.2181 + (0.30188 + 0.017998 * jct) * jct) * jct / 3600;
		double zeta = (2306.2181 + (1.09468 + 0.018203 * jct) * jct) * jct / 3600;
		double theta = (2004.3109 - (0.42665 + 0.041833 * jct) * jct) * jct / 3600;

		double aeRad = Math.toRadians(raJ2000 + eta);
		double cosAE = Math.cos(aeRad);
		double cosTheta = Math.cos(Math.toRadians(theta));
		double sinTheta = Math.sin(Math.toRadians(theta));
		double cosD0 = Math.cos(Math.toRadians(decJ2000));
		double sinD0 = Math.sin(Math.toRadians(decJ2000));

		double a = cosD0 * Math.sin(aeRad);
		double b = cosTheta * cosD0 * cosAE - sinTheta * sinD0;
		double c = sinTheta * cosD0 * cosAE + cosTheta * sinD0;

		double ra = Math.toRadians(Math.toDegrees(Math.atan2(a, b)) + zeta); // in rad
		double dec = Math.asin(c); // in rad

		// transformation to ecliptic longitude (degrees in range 0-360)
		double lng =
			Math.toDegrees(
				Math.atan2(
					Math.sin(ra) * Math.cos(meanObliquity) + Math.tan(dec) * Math.sin(meanObliquity),
					Math.cos(ra))
			);
		if (lng < 0.0) {
			lng += 360;
		}
		return lng;
	}

	//~ Innere Klassen ----------------------------------------------------

	/**
	 * <p>Represents the event when the sun or moon enters or exits a zodiac. </p>
	 *
	 * @author 	Meno Hochschild
	 * @see     SunPosition#inSignOf(Zodiac)
	 * @see     MoonPosition#inSignOf(Zodiac)
	 * @see     SunPosition#inConstellationOf(Zodiac)
	 * @see     MoonPosition#inConstellationOf(Zodiac)
	 * @since	4.37
	 */
	/*[deutsch]
	 * <p>Stellt das Ereignis dar, wenn die Sonne oder der Mond ein Tierkreissternbild
	 * oder -zeichen betreten oder verlassen. </p>
	 *
	 * @author 	Meno Hochschild
	 * @see     SunPosition#inSignOf(Zodiac)
	 * @see     MoonPosition#inSignOf(Zodiac)
	 * @see     SunPosition#inConstellationOf(Zodiac)
	 * @see     MoonPosition#inConstellationOf(Zodiac)
	 * @since	4.37
	 */
	public static class Event
		implements ChronoCondition<Moment> {

		//~ Instanzvariablen ----------------------------------------------

		private final char body;
		private final Zodiac zodiac;
		private final boolean horoscope;

		//~ Konstruktoren -------------------------------------------------

		private Event(
			char body,
			Zodiac zodiac,
			boolean horoscope
		) {
			super();

			if ((body != 'S') && (body != 'L')) {
				throw new IllegalArgumentException("Unsupported celestial body: " + body);
			} else if (zodiac == null) {
				throw new IllegalArgumentException("Celestial coordinates must be finite.");
			} else if (horoscope && (zodiac == OPHIUCHUS)) {
				throw new IllegalArgumentException("Ophiuchus is not an astrological zodiac sign.");
			}

			this.body = body;
			this.zodiac = zodiac;
			this.horoscope = horoscope;
		}

		//~ Methoden ------------------------------------------------------

		/**
		 * <p>Calculates the moment when the celestial body enters the associated zodiac. </p>
		 *
		 * <p>The accuracy is limited to roughly minute precision. </p>
		 *
		 * @param 	start		the moment when to start the search
		 * @return	moment of this event at or after given start
		 * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
		 */
		/*[deutsch]
		 * <p>Berechnet den Moment, wann der Himmelsk&ouml;rper das verkn&uuml;pfte Tierkreissymbol erreicht. </p>
		 *
		 * <p>Die Rechengenauigkeit ist ungef&auml;hr auf eine Minute beschr&auml;nkt. </p>
		 *
		 * @param 	start		the moment when to start the search
		 * @return	moment of this event at or after given start
		 * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
		 */
		public Moment atMomentOfEntry(Moment start) {

			Moment estimate = this.atTime(start, false, true);
			return this.atTime(estimate, false, false); // two-step-approximation

		}

		/**
		 * <p>Calculates the moment when the celestial body leaves the associated zodiac. </p>
		 *
		 * <p>The accuracy is limited to roughly minute precision. </p>
		 *
		 * @param 	start		the moment when to start the search
		 * @return	moment of this event at or after given start
		 * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
		 */
		/*[deutsch]
		 * <p>Berechnet den Moment, wann der Himmelsk&ouml;rper das verkn&uuml;pfte Tierkreissymbol verl&auml;sst. </p>
		 *
		 * <p>Die Rechengenauigkeit ist ungef&auml;hr auf eine Minute beschr&auml;nkt. </p>
		 *
		 * @param 	start		the moment when to start the search
		 * @return	moment of this event at or after given start
		 * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
		 */
		public Moment atMomentOfExit(Moment start) {

			Moment estimate = this.atTime(start, true, true);
			return this.atTime(estimate, true, false); // two-step-approximation

		}

		/**
		 * <p>Tests if this event happens at given moment. </p>
		 *
		 * <p>Example of usage: </p>
		 *
		 * <pre>
		 *     Moment moment = PlainTimestamp.of(2000, 4, 18, 13, 16).atUTC();
		 *     System.out.println(moment.matches(SunPosition.inConstellationOf(Zodiac.ARIES))); // true
		 * </pre>
		 *
		 * <p>Note: Due to precessional effects, Aries is nowadays passed by sun in April and not around
		 * vernal equinox as 2000 years ago. </p>
		 *
		 * @param 	moment		the moment to be tested
		 * @return	boolean
		 */
		/*[deutsch]
		 * <p>Testet, ob dieses Ereignis zum angegebenen Moment auftritt. </p>
		 *
		 * <p>Anwendungsbeispiel: </p>
		 *
		 * <pre>
		 *     Moment moment = PlainTimestamp.of(2000, 4, 18, 13, 16).atUTC();
		 *     System.out.println(moment.matches(SunPosition.inConstellationOf(Zodiac.ARIES))); // true
		 * </pre>
		 *
		 * <p>Hinweis: Wegen des Effekts der Pr&auml;zession passiert die Sonne heutzutage das Sternbild Aries
		 * im April statt um den Fr&uuml;hlingszeitpunkt (wie 2000 Jahre fr&uuml;her). </p>
		 *
		 * @param 	moment		the moment to be tested
		 * @return	boolean
		 */
		@Override
		public boolean test(Moment moment) {

			Moment time = moment.with(Moment.PRECISION, TimeUnit.MINUTES);
			double jde = JulianDay.ofEphemerisTime(time).getValue();
			double lng = (this.body == 'S') ? getSolarLongitude(jde) : getLunarLongitude(jde);

			double start;
			double end;

			if (this.horoscope) {
				start = this.getHoroscopeLongitude(false);
				end = this.getHoroscopeLongitude(true);
			} else {
				Zodiac z1 = this.zodiac;
				Zodiac z2 = z1.next();
				start = toEclipticAngle(time, z1.entry.getRightAscension(), z1.entry.getDeclination());
				end = toEclipticAngle(time, z2.entry.getRightAscension(), z2.entry.getDeclination());
			}

			if (end < start) {
				end += 360;
				if (lng < 180) {
					lng += 360;
				}
			}

			return (lng >= start) && (lng < end);

		}

		static Event ofSign(
			char body,
			Zodiac zodiac
		) {
			return new Event(body, zodiac, true);
		}

		static Event ofConstellation(
			char body,
			Zodiac zodiac
		) {
			return new Event(body, zodiac, false);
		}

		private Zodiac getZodiac(boolean exiting) {
			Zodiac z = this.zodiac;
			if (exiting) {
				z = z.next();
			}
			if (this.horoscope && (z == OPHIUCHUS)) {
				z = SAGITTARIUS;
			}
			return z;
		}

		private int getHoroscopeLongitude(boolean exiting) {
			Zodiac z = this.getZodiac(exiting);
			int offset = (z.compareTo(OPHIUCHUS) < 0) ? 0 : -1;
			return (z.ordinal() + offset) * 30;
		}

		private Moment atTime(
			Moment moment,
			boolean exiting,
			boolean after
		) {
			final double angle;

			if (this.horoscope) {
				if (after) {
					angle = this.getHoroscopeLongitude(exiting);
				} else {
					return moment; // no precession => reduce to one-step-calculation
				}
			} else {
				Zodiac z = this.getZodiac(exiting);
				angle = toEclipticAngle(moment, z.entry.getRightAscension(), z.entry.getDeclination());
			}

			double jd0 = JulianDay.ofEphemerisTime(moment).getValue();
			double estimate = jd0;

			if (this.body == 'S') {
				double delta = angle - getSolarLongitude(jd0);
				if (after) {
					delta = modulo360(delta);
				}
				estimate += (delta * MEAN_TROPICAL_YEAR / 360.0);
			} else {
				double delta = angle - getLunarLongitude(jd0);
				if (after) {
					delta = modulo360(delta);
				}
				estimate += (delta * MEAN_SYNODIC_MONTH / 360.0);
			}

			double low = Math.max(jd0, estimate - 5);
			double high = estimate + 5;

			while (true) {
				double x = (low + high) / 2;

				if (high - low < 0.0001) { // < 9 seconds
					return JulianDay.ofEphemerisTime(x).toMoment().with(Moment.PRECISION, TimeUnit.SECONDS);
				}

				double delta = ((this.body == 'S') ? getSolarLongitude(x) : getLunarLongitude(x)) - angle;

				if (modulo360(delta) < 180.0) {
					high = x;
				} else {
					low = x;
				}
			}
		}

		private static double modulo360(double angle) {
			return angle - 360.0 * Math.floor(angle / 360.0); // always >= 0.0
		}

	}

}