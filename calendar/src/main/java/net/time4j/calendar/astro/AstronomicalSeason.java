/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AstronomicalSeason.java) is part of project Time4J.
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

import net.time4j.scale.TimeScale;


/**
 * <p>The four astronomical seasons (Spring, Summer, Autumn and Winter). </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.33/4.28
 */
/*[deutsch]
 * <p>Die vier astronomischen Jahreszeiten (Fr&uuml;hling, Sommer, Herbst und Winter). </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.33/4.28
 */
public enum AstronomicalSeason {

	//~ Statische Felder/Initialisierungen --------------------------------

	/**
	 * <p>Begin of Spring on the northern hemisphere in March (or Autumn on the southern hemisphere). </p>
	 */
	/*[deutsch]
	 * <p>Fr&uuml;hlingsanfang auf der n&oumlr;dlichen Halbkugel im M&auml;rz
	 * (oder Herbstanfang auf der s&uuml;dlichen Halbkugel). </p>
	 */
	VERNAL_EQUINOX,

	/**
	 * <p>Begin of Summer on the northern hemisphere in June (or Winter on the southern hemisphere). </p>
	 */
	/*[deutsch]
	 * <p>Sommeranfang auf der n&oumlr;dlichen Halbkugel im Juni
	 * (oder Winteranfang auf der s&uuml;dlichen Halbkugel). </p>
	 */
	SUMMER_SOLSTICE,

	/**
	 * <p>Begin of Autumn on the northern hemisphere in September (or Spring on the southern hemisphere). </p>
	 */
	/*[deutsch]
	 * <p>Herbstanfang auf der n&oumlr;dlichen Halbkugel im September
	 * (oder Fr&uuml;hlingsanfang auf der s&uuml;dlichen Halbkugel). </p>
	 */
	AUTUMNAL_EQUINOX,

	/**
	 * <p>Begin of Winter on the northern hemisphere in December (or Summer on the southern hemisphere). </p>
	 */
	/*[deutsch]
	 * <p>Winteranfang auf der n&oumlr;dlichen Halbkugel im Dezember
	 * (oder Sommeranfang auf der s&uuml;dlichen Halbkugel). </p>
	 */
	WINTER_SOLSTICE;

	//~ Methoden ----------------------------------------------------------

	/**
	 * <p>Determines the moment of this astronomical event in given year. </p>
	 *
	 * <p>The precision is for modern times (around 2000) better than a minute. The underlying astronomical
	 * calculations are based on formula given by Jean Meeus in his book &quot;Astronomical algorithms&quot;. </p>
	 *
	 * @param 	year	gregorian/julian year
	 * @return	time of this astronomical event (equinox or solstice) in given year
	 * @throws  IllegalArgumentException if {@code year < -2000}
	 */
	/*[deutsch]
	 * <p>Berechnet die Zeit, wann dieses astronomische Ereignis im angegebenen Jahr auftritt. </p>
	 *
	 * <p>Die Genauigkeit ist f&uuml;r moderne Zeiten (um das Jahr 2000 herum) besser als eine Minute.
	 * Die zugrundeliegenden astronomischen Berechnungen fu&szlig;en auf Formeln aus dem Buch
	 * &quot;Astronomical algorithms&quot; von Jean Meeus. </p>
	 *
	 * @param 	year	gregorian/julian year
	 * @return	time of this astronomical event (equinox or solstice) in given year
	 * @throws  IllegalArgumentException if {@code year < -2000}
	 */
	public Moment inYear(int year) {

		double tt = (this.jdEphemerisDays(year) - 2441317.5) * 86400.0;
		double utc;

		if (year < 1972) {
			utc = tt - TimeScale.deltaT(year, (this.ordinal() + 1) * 3);
		} else {
			utc = tt - 42.184;
		}

		long seconds = (long) Math.floor(utc);
		int nanos = (int) ((utc - seconds) * 1000000000);

		return Moment.of(seconds, nanos, TimeScale.UTC);

	}

	private double jdEphemerisDays(int year) {

		double jd0 = this.jdMean(year);
		double t = (jd0 - 2451545.0) / 36525;
		double w = 35999.373 * t - 2.47;
		double dL = 1 + 0.0334 * cos(w) + 0.0007 * cos(2 * w);
		double s = periodic24(t);
		return jd0 + ((0.00001 * s) / dL);

	}

	private double jdMean(int year) {

		if (year < 1000) { // Meeus - Astronomical Algorithms - p178, Table 27.A
			double y = year / 1000.0;

			switch (this) {

				case VERNAL_EQUINOX :
					return 1721139.29189 + 365242.13740 * y + 0.06134 * Math.pow(y, 2)
					+ 0.00111 * Math.pow(y, 3) - 0.00071 * Math.pow(y, 4);

				case SUMMER_SOLSTICE :
					return 1721233.25401 + 365241.72562 * y - 0.05323 * Math.pow(y, 2)
					+ 0.00907 * Math.pow(y, 3) + 0.00025 * Math.pow(y, 4);

				case AUTUMNAL_EQUINOX :
					return 1721325.70455 + 365242.49558 * y - 0.11677 * Math.pow(y, 2)
					- 0.00297 * Math.pow(y, 3) + 0.00074 * Math.pow(y, 4);

				case WINTER_SOLSTICE :
					return 1721414.39987 + 365242.88257 * y - 0.00769 * Math.pow(y, 2)
					- 0.00933 * Math.pow(y, 3) - 0.00006 * Math.pow(y, 4);

				default :
					throw new AssertionError(this);
			}
		} else { // Meeus - Astronomical Algorithms - p178, Table 27.B
			double y = (year - 2000) / 1000.0;

			switch (this) {

				case VERNAL_EQUINOX :
					return 2451623.80984 + 365242.37404 * y + 0.05169 * Math.pow(y, 2)
					- 0.00411 * Math.pow(y, 3) - 0.00057 * Math.pow(y, 4);

				case SUMMER_SOLSTICE :
					return 2451716.56767 + 365241.62603 * y + 0.00325 * Math.pow(y, 2)
					+ 0.00888 * Math.pow(y, 3) - 0.00030 * Math.pow(y, 4);

				case AUTUMNAL_EQUINOX :
					return 2451810.21715 + 365242.01767 * y - 0.11575 * Math.pow(y, 2)
					+ 0.00337 * Math.pow(y, 3) + 0.00078 * Math.pow(y, 4);

				case WINTER_SOLSTICE :
					return 2451900.05952 + 365242.74049 * y - 0.06223 * Math.pow(y, 2)
					- 0.00823 * Math.pow(y, 3) + 0.00032 * Math.pow(y, 4);

				default :
					throw new AssertionError(this);
			}
		}

	}

	// Meeus - Astronomical Algorithms - p179, Table 27.C
	private static final int[] A = {
		485, 203, 199, 182, 156, 136, 77, 74, 70, 58, 52, 50, 45, 44, 29, 18, 17, 16, 14, 12,
		12, 12, 9, 8
	};

	private static final double[] B = { // in degrees
		324.96, 337.23, 342.08, 27.85, 73.14, 171.52, 222.54, 296.72, 243.58, 119.81, 297.17,
		21.02, 247.54, 325.15, 60.93, 155.12, 288.79, 198.04, 199.76, 95.39, 287.11, 320.81,
		227.73, 15.45
	};

	private static final double[] C = { // in degrees
		1934.136, 32964.467, 20.186, 445267.112, 45036.886, 22518.443, 65928.934, 3034.906,
		9037.513, 33718.147, 150.678, 2281.226, 29929.562, 31555.956, 4443.417, 67555.328,
		4562.452, 62894.029, 31436.921, 14577.848, 31931.756, 34777.259, 1222.114, 16859.074
	};

	private static double periodic24(double t) {

		double s = 0;
		for (int i = 0; i < 24; i++) {
			s += A[i] * cos(B[i] + (C[i] * t));
		}
		return s;

	}

	private static double cos(double deg) {

		return Math.cos(deg * Math.PI / 180);

	}

}
