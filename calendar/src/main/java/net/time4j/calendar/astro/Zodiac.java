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
import net.time4j.PlainDate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Enumeration of the thirteen astronomical zodiac definitions. </p>
 *
 * <p>See also <a href="https://en.wikipedia.org/wiki/Zodiac">Wikipedia</a>. The boundaries
 * of the associated <a href="https://www.iau.org/public/themes/constellations/">constellations</a>
 * have been defined by IAU. </p>
 *
 * <p><strong>Attention</strong>: This enum does <strong>not</strong>
 * represent the twelve astrological (horoscopic) zodiacs. </p>
 *
 * @author 	Meno Hochschild
 * @since 	4.37
 */
/*[deutsch]
 * <p>Aufz&auml;hlung der dreizehn astronomischen Tierkreissternbilder. </p>
 *
 * <p>Siehe auch <a href="https://en.wikipedia.org/wiki/Zodiac">Wikipedia</a>. Die Grenzen
 * der damit verbundenen <a href="https://www.iau.org/public/themes/constellations/">Konstellationen</a>
 * sind von der IAU definiert worden. </p>
 *
 * <p><strong>Achtung</strong>: Dieses Enum stellt <strong>nicht</strong> die
 * zw&ouml;lf astrologischen (horoskopischen) Tierkreiszeichen dar. </p>
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
		Map<String, String[]> lang2names = new HashMap<>();
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

	EquatorialCoordinates getEntryAngles() {
		return this.entry;
	}

	EquatorialCoordinates getExitAngles() {
		return this.next().entry;
	}

	boolean isMatched(
		char body,
		Moment moment
	) {
		Moment time = moment.with(Moment.PRECISION, TimeUnit.MINUTES);
		double jde = JulianDay.ofEphemerisTime(time).getValue();
		double lng = (body == 'S') ? getSolarLongitude(jde) : getLunarLongitude(jde);

		EquatorialCoordinates exit = this.getExitAngles();
		double start = toEclipticAngle(time, this.entry.getRightAscension(), this.entry.getDeclination());
		double end = toEclipticAngle(time, exit.getRightAscension(), exit.getDeclination());

		if (end < start) {
			end += 360;
			if (lng < 180) {
				lng += 360;
			}
		}

		return (lng >= start) && (lng < end);
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
	 * @see 	SunPosition#atEntry(Zodiac)
	 * @see 	SunPosition#atExit(Zodiac)
	 * @see 	MoonPosition#atEntry(Zodiac)
	 * @see 	MoonPosition#atExit(Zodiac)
	 * @see 	SunPosition#atHoroscopeSign(Zodiac)
	 * @since	4.37
	 */
	/*[deutsch]
	 * <p>Stellt das Ereignis dar, wenn die Sonne oder der Mond ein Tierkreissternbild
	 * oder -zeichen betreten oder verlassen. </p>
	 *
	 * @author 	Meno Hochschild
	 * @see 	SunPosition#atEntry(Zodiac)
	 * @see 	SunPosition#atExit(Zodiac)
	 * @see 	MoonPosition#atEntry(Zodiac)
	 * @see 	MoonPosition#atExit(Zodiac)
	 * @see 	SunPosition#atHoroscopeSign(Zodiac)
	 * @since	4.37
	 */
	public static class Event {

		//~ Instanzvariablen ----------------------------------------------

		private final char body;
		private final double c1;
		private final double c2;
		private final boolean ecliptic;

		//~ Konstruktoren -------------------------------------------------

		private Event(
			char body,
			double c1,
			double c2,
			boolean ecliptic
		) {
			super();

			if ((body != 'S') && (body != 'L')) {
				throw new IllegalArgumentException("Unsupported celestial body: " + body);
			} else if (!Double.isFinite(c1) || !Double.isFinite(c2)) {
				throw new IllegalArgumentException("Celestial coordinates must be finite.");
			}

			this.body = body;
			this.c1 = c1;
			this.c2 = c2;
			this.ecliptic = ecliptic;
		}

		//~ Methoden ------------------------------------------------------

		/**
		 * <p>Calculates the moment when this event occurs in given year. </p>
		 *
		 * @param 	year	gregorian year
		 * @return	moment of this event in given year
		 * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
		 */
		/*[deutsch]
		 * <p>Berechnet den Moment, wann dieses Ereignis im angegebenen Jahr eintritt. </p>
		 *
		 * @param 	year	gregorian year
		 * @return	moment of this event in given year
		 * @throws  IllegalArgumentException if the Julian day of moment is not in supported range
		 */
		public Moment inYear(int year) {

			Moment moment = PlainDate.of(year, 1, 1).atStartOfDay().atUTC();
			double jd0 = JulianDay.ofEphemerisTime(moment).getValue();
			double estimate = jd0;
			final double angle;

			if (this.ecliptic) {
				angle = this.c1;
			} else {
				angle = toEclipticAngle(moment, this.c1, this.c2);
			}

			if (this.body == 'S') {
				estimate += modulo360(angle - getSolarLongitude(jd0)) * MEAN_TROPICAL_YEAR / 360.0;
			} else {
				estimate += modulo360(angle - getLunarLongitude(jd0)) * MEAN_SYNODIC_MONTH / 360.0;
			}

			double low = Math.max(jd0, estimate - 5);
			double high = estimate + 5;

			while (true) {
				double x = (low + high) / 2;

				if (high - low < 0.0001) { // < 9 seconds
					return JulianDay.ofEphemerisTime(x).toMoment().with(Moment.PRECISION, TimeUnit.MINUTES);
				}

				double delta = ((this.body == 'S') ? getSolarLongitude(x) : getLunarLongitude(x)) - angle;

				if (modulo360(delta) < 180.0) {
					high = x;
				} else {
					low = x;
				}
			}

		}

		static Event ofHoroscopic(double angle) {
			return new Event('S', angle, 0.0, true);
		}

		static Event ofConstellation(
			char body,
			EquatorialCoordinates angles
		) {
			return new Event(body, angles.getRightAscension(), angles.getDeclination(), false);
		}

		private static double modulo360(double angle) {
			return angle - 360.0 * Math.floor(angle / 360.0); // always >= 0.0
		}

	}

}