/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriAlgorithm.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.base.MathUtils;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.VariantSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * <p>Describes islamic calendar variants based on simplistic deterministic algorithms. </p>
 *
 * <p>Most algorithms uses a leap year pattern within a 30-year-cycle. All years have 12 months where
 * the month lengths are alternately 30 or 29 with the exception of last month which has 30 days in
 * leap years else 29 days. The supported range in islamic years is 1-1600. </p>
 *
 * <p>Note that all these algorithms have <strong>approximated</strong> nature only. There are deviations
 * from sighting-based variants especially in short term. However, main advantage of algorithm-based variants
 * is the fact that they can be applied into far past or future. Keep in mind that sighting-based calendars
 * have a much more constrained valid range. For more background see
 * <a href="http://www.staff.science.uu.nl/~gent0113/islam/islam_tabcal_variants.htm">The Arithmetical
 * or Tabular Islamic Calendar</a>. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.6/4.4
 */
/*[deutsch]
 * <p>Beschreibt islamische Kalendervarianten basierend auf vereinfachenden deterministischen Algorithmen. </p>
 *
 * <p>Die meisten Algorithmen verwenden ein Schaltjahresmuster innerhalb eines 30-Jahres-Zyklus. Jedes Jahr hat
 * 12 Monate, die abwechselnd 30 oder 29 Tage lang sind. Als Ausnahme hat der letzte Monat in Schaltjahren
 * 30 Tage. Der unterst&uuml;tzte Wertbereich ist in islamischen Jahren 1-1600. </p>
 *
 * <p>Zu beachten: All diese Algorithmen haben nur <strong>N&auml;herungscharakter</strong>. Abweichungen von
 * sichtbasierten Varianten sind besonders kurzfristig m&ouml;glich. Allerdings besteht der Hauptvorteil der
 * Algorithmen darin, da&szlig; sie auf Zeiten weit in der Vergangenheit oder Zukunft angewandt werden
 * k&ouml;nnen, w&auml;hrend sichtbasierte Varianten stark in ihrem G&uuml;ltigkeitsbereich eingeschr&auml;nkt
 * sind. Mehr Hintergrundinformationen siehe
 * <a href="http://www.staff.science.uu.nl/~gent0113/islam/islam_tabcal_variants.htm">The Arithmetical
 * or Tabular Islamic Calendar</a>. </p>
 *
 * @author 	Meno Hochschild
 * @since 	3.6/4.4
 */
public enum HijriAlgorithm
	implements VariantSource {

	//~ Statische Felder/Initialisierungen --------------------------------

	/**
	 * Uses the leap year pattern {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29} with civil (Friday) epoch.
	 *
	 * Variant name: &quot;islamic-eastc&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29} mit der Freitagsepoche.
	 *
	 * Variantenname: &quot;islamic-eastc&quot;
	 */
	EAST_ISLAMIC_CIVIL("islamic-eastc", new int[] {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29}, true),

	/**
	 * Uses the leap year pattern {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29} with astronomical (Thursday) epoch.
	 *
	 * Variant name: &quot;islamic-easta&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29} mit der Donnerstagsepoche.
	 *
	 * Variantenname: &quot;islamic-easta&quot;
	 */
	EAST_ISLAMIC_ASTRO("islamic-easta", new int[] {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29}, false),

	/**
	 * Uses the leap year pattern {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29} with civil (Friday) epoch.
	 *
	 * Variant name: &quot;islamic-civil&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29} mit der Freitagsepoche.
	 *
	 * Variantenname: &quot;islamic-civil&quot;
	 */
	WEST_ISLAMIC_CIVIL("islamic-civil", new int[] {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29}, true),

	/**
	 * Uses the leap year pattern {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29} with astronomical (Thursday) epoch.
	 *
	 * Variant name: &quot;islamic-tbla&quot;. This variant is equivalent to Microsoft Hijri (Kuwaiti) calendar.
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29} mit der Donnerstagsepoche.
	 *
	 * Variantenname: &quot;islamic-tbla&quot; Diese Variante ist &auml;quivalent zum
	 * Hijri-Kalender (Kuwaiti) von Microsoft.
	 */
	WEST_ISLAMIC_ASTRO("islamic-tbla", new int[] {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29}, false),

	/**
	 * Uses the leap year pattern {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29} with civil (Friday) epoch.
	 *
	 * Variant name: &quot;islamic-fatimidc&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29} mit der Freitagsepoche.
	 *
	 * Variantenname: &quot;islamic-fatimidc&quot;
	 */
	FATIMID_CIVIL("islamic-fatimidc", new int[] {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29}, true),

	/**
	 * Uses the leap year pattern {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29} with astronomical (Thursday) epoch.
	 *
	 * Variant name: &quot;islamic-fatimida&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29} mit der Donnerstagsepoche.
	 *
	 * Variantenname: &quot;islamic-fatimida&quot;
	 */
	FATIMID_ASTRO("islamic-fatimida", new int[] {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29}, false),

	/**
	 * Uses the leap year pattern {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30} with civil (Friday) epoch.
	 *
	 * Variant name: &quot;islamic-habashalhasibc&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30} mit der Freitagsepoche.
	 *
	 * Variantenname: &quot;islamic-habashalhasibc&quot;
	 */
	HABASH_AL_HASIB_CIVIL("islamic-habashalhasibc", new int[] {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30}, true),

	/**
	 * Uses the leap year pattern {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30} with astronomical (Thursday) epoch.
	 *
	 * Variant name: &quot;islamic-habashalhasiba&quot;
	 */
	/*[deutsch]
	 * Verwendet das Schaltjahrmuster {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30} mit der Donnerstagsepoche.
	 *
	 * Variantenname: &quot;islamic-habashalhasiba&quot;
	 */
	HABASH_AL_HASIB_ASTRO("islamic-habashalhasiba", new int[] {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30}, false);
	
	private static final long LENGTH_OF_30_YEAR_CYCLE; 
	private static final long START_622_07_15;
	private static final long START_622_07_16;
	private static final int MAX_YEAR;
	private static final long ASTRO_1600_12_29;
	private static final long CIVIL_1600_12_29;
	
	static {
		LENGTH_OF_30_YEAR_CYCLE = 30 * 354 + 11;
		// HistoricDate date = HistoricDate.of(HistoricEra.AD, 622, 7, 15); // Thursday epoch
		START_622_07_15 = -492879; // ChronoHistory.PROLEPTIC_JULIAN.convert(date).get(EpochDays.UTC);
		START_622_07_16 = START_622_07_15 + 1;
		MAX_YEAR = 1600;
		ASTRO_1600_12_29 = 74106;
		CIVIL_1600_12_29 = ASTRO_1600_12_29 + 1;
	}

	//~ Instanzvariablen --------------------------------------------------

	private final transient Transformer calsys;

	//~ Konstruktoren -------------------------------------------------

	private HijriAlgorithm(
		String variant,
		int[] intercalaries, 
		boolean civil
	) {
		this.calsys = new Transformer(variant, intercalaries, civil, 0);

	}

	//~ Methoden ------------------------------------------------------

	@Override
	public String getVariant() {

		return this.calsys.variant;

	}

	// yields the calculation engine
	EraYearMonthDaySystem<HijriCalendar> getCalendarSystem(int adjustment) {

		if (adjustment == 0) {
			return this.calsys;
		}

		HijriAdjustment ha = HijriAdjustment.of(this.getVariant(), adjustment);
		return new Transformer(ha.getVariant(), this.calsys.intercalaries, this.calsys.civil, adjustment);

	}

	//~ Innere Klassen ----------------------------------------------------

	private static class Transformer
		implements EraYearMonthDaySystem<HijriCalendar> {

		//~ Instanzvariablen ----------------------------------------------

		private final String variant;
		private final int[] intercalaries;
		private final boolean civil;
		private final int adjustment;

		//~ Konstruktoren -------------------------------------------------

		Transformer(
			String variant,
			int[] intercalaries,
			boolean civil,
			int adjustment
		) {
			super();

			this.variant = variant;
			this.intercalaries = intercalaries;
			this.civil = civil;
			this.adjustment = adjustment;

		}

		//~ Methoden ------------------------------------------------------

		@Override
		public boolean isValid(
			CalendarEra era,
			int hyear,
			int hmonth,
			int hdom
		) {

			return (
				(era == HijriEra.ANNO_HEGIRAE)
				&& (hyear >= 1)
				&& (hyear <= MAX_YEAR)
				&& (hmonth >= 1)
				&& (hmonth <= 12)
				&& (hdom >= 1)
				&& (hdom <= this.getLengthOfMonth(era, hyear, hmonth))
			);

		}

		@Override
		public int getLengthOfMonth(
			CalendarEra era,
			int hyear,
			int hmonth
		) {

			if (era != HijriEra.ANNO_HEGIRAE) {
				throw new IllegalArgumentException("Wrong era: " + era);
			} else if (hyear < 1 || hyear > MAX_YEAR || hmonth < 1 || hmonth > 12) {
				throw new IllegalArgumentException("Out of bounds: " + hyear + "/" + hmonth);
			}

			if (hmonth == 12) {
				int y = ((hyear - 1) % 30) + 1;
				return ((Arrays.binarySearch(this.intercalaries, y) >= 0) ? 30 : 29);
			}

			return ((hmonth % 2 == 1) ? 30 : 29);

		}

		@Override
		public int getLengthOfYear(
			CalendarEra era,
			int hyear
		) {

			if (era != HijriEra.ANNO_HEGIRAE) {
				throw new IllegalArgumentException("Wrong era: " + era);
			}

			if ((hyear < 1) || (hyear > MAX_YEAR)) {
				throw new IllegalArgumentException("Out of bounds: yearOfEra=" + hyear);
			}

			int y = ((hyear - 1) % 30) + 1;
			return ((Arrays.binarySearch(this.intercalaries, y) >= 0) ? 355 : 354);

		}

		@Override
		public HijriCalendar transform(long utcDays) {

			long realDays = MathUtils.safeAdd(utcDays, this.adjustment);
			long start = (this.civil ? START_622_07_16 : START_622_07_15);

			if ((realDays < start) || (realDays > (this.civil ? CIVIL_1600_12_29 : ASTRO_1600_12_29))) {
				throw new IllegalArgumentException("Out of supported range: " + utcDays);
			}

			long days = MathUtils.safeSubtract(realDays, start);

			int hyear = 1;
			int hmonth = 1;
			int hdom = 1;

			hyear += MathUtils.safeCast((days / LENGTH_OF_30_YEAR_CYCLE) * 30);
			int delta = (int) (days % LENGTH_OF_30_YEAR_CYCLE);

			for (int i = 1; i < 30; i++) {
				int ylen = 354;
				if (Arrays.binarySearch(this.intercalaries, i) >= 0) {
					ylen++;
				}
				if (delta > ylen) {
					delta -= ylen;
					hyear++;
				} else {
					break;
				}
			}

			for (int i = 1; i < 12; i++) {
				int mlen = 30;
				if ((i % 2) == 0) {
					mlen = 29;
				}
				if (delta > mlen) {
					delta -= mlen;
					hmonth++;
				} else {
					break;
				}
			}

			hdom += delta;
			int test;

			if (hmonth == 12) {
				int y = ((hyear - 1) % 30) + 1;
				test = ((Arrays.binarySearch(this.intercalaries, y) >= 0) ? 30 : 29);
			} else {
				test = ((hmonth % 2 == 1) ? 30 : 29);
			}

			if (hdom > test) {
				hdom = 1;
				hmonth++;

				if (hmonth > 12) {
					hmonth = 1;
					hyear++;
				}
			}

			return HijriCalendar.of(this.variant, hyear, hmonth, hdom);

		}

		@Override
		public long transform(HijriCalendar date) {

			int hyear = date.getYear();
			int hmonth = date.getMonth().getValue();
			int hdom = date.getDayOfMonth();

			if (hyear < 1 || hyear > MAX_YEAR || hmonth < 1 || hmonth > 12 || hdom < 1 || hdom > 30) {
				throw new IllegalArgumentException("Out of supported range: " + date);
			}

			long days = ((hyear - 1) / 30) * LENGTH_OF_30_YEAR_CYCLE;
			int y = ((hyear - 1) % 30) + 1;

			for (int i = 1; i < y; i++) {
				if (Arrays.binarySearch(this.intercalaries, i) >= 0) {
					days += 355;
				} else {
					days += 354;
				}
			}

			for (int i = 1; i < hmonth; i++) {
				if ((i % 2) == 0) {
					days += 29;
				} else {
					days += 30;
				}
			}

			if (hdom == 30) {
				if (
					(hmonth == 12 && Arrays.binarySearch(this.intercalaries, y) < 0)
					|| ((hmonth != 12) && (hmonth % 2) == 0)
				) {
					throw new IllegalArgumentException("Invalid day-of-month: " + date);
				}
			}

			days += hdom;
			return MathUtils.safeSubtract(this.getMinimumSinceUTC() + days - 1, this.adjustment);

		}

		@Override
		public long getMinimumSinceUTC() {

			return MathUtils.safeSubtract(this.civil ? START_622_07_16 : START_622_07_15, this.adjustment);

		}

		@Override
		public long getMaximumSinceUTC() {

			return MathUtils.safeSubtract(this.civil ? CIVIL_1600_12_29 : ASTRO_1600_12_29, this.adjustment);

		}

		@Override
		public List<CalendarEra> getEras() {

			CalendarEra era = HijriEra.ANNO_HEGIRAE;
			return Collections.singletonList(era);

		}

	}

}
