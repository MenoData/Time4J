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


import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

	ARIES('\u2648'),

	TAURUS('\u2649'),

	GEMINI('\u264A'),

	CANCER('\u264B'),

	LEO('\u264C'),

	VIRGO('\u264D'),

	LIBRA('\u264E'),

	SCORPIUS('\u264F'),

	OPHIUCHUS('\u26CE'),

	SAGITTARIUS('\u2650'),

	CAPRICORNUS('\u2651'),

	AQUARIUS('\u2652'),

	PISCES('\u2653');

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

	//~ Instanzvariablen --------------------------------------------------

	private transient final char symbol;

	//~ Konstruktoren -----------------------------------------------------

	private Zodiac(char symbol) {
		this.symbol = symbol;
	}

	//~ Methoden ----------------------------------------------------------

	/**
	 * <p>Obtains the associated symbol character. </p>
	 *
	 * @return	char
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
	 * @param 	locale		language
	 * @return	localized name or the Latin name if given language is not supported
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

}
