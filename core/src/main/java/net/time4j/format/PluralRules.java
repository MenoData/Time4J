/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PluralRules.java) is part of project Time4J.
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

package net.time4j.format;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.time4j.format.PluralCategory.FEW;
import static net.time4j.format.PluralCategory.MANY;
import static net.time4j.format.PluralCategory.ONE;
import static net.time4j.format.PluralCategory.OTHER;
import static net.time4j.format.PluralCategory.TWO;
import static net.time4j.format.PluralCategory.ZERO;


/**
 * <p>Helps to determine the plural category for a given number of units. </p>
 *
 * <p>The predefined rules for any given language are based on
 * CLDR-version 25 but can be overridden if necessary. The source data
 * of the underlying algorithms to determine the plural category can be
 * found in CLDR-repository-file &quot;core.zip&quot; along the path
 * &quot;common/supplemental/plurals.xml&quot;. </p>
 *
 * @author  Meno Hochschild
 * @spec    All concrete classes must be immutable.
 */
/*[deutsch]
 * <p>Hilfsklasse zur Bestimmung der Pluralkategorie f&uuml;r eine gegebene
 * Sprache und eine entsprechende Anzahl von Zeiteinheiten. </p>
 *
 * <p>Die vordefinierten Regeln f&uuml;r irgendeine Sprache basieren auf
 * der CLDR-Version 25, k&ouml;nnen bei Bedarf aber &uuml;berschrieben
 * werden. Die Quelldaten der zugrundeliegenden Algorithmen, die die
 * Pluralkategorie zu bestimmen helfen, k&ouml;nnen im CLDR-Repositorium
 * &quot;core.zip&quot; und dem Pfad &quot;common/supplemental/plurals.xml&quot;
 * gefunden werden. </p>
 *
 * @author  Meno Hochschild
 * @spec    All concrete classes must be immutable.
 */
public abstract class PluralRules {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<String, PluralRules> LANGUAGE_MAP =
        new ConcurrentHashMap<String, PluralRules>(140);
    private static final PluralRules STD_RULES = new CLDR(0);

    static {
        Map<String, PluralRules> map = new HashMap<String, PluralRules>();
        fill(map, "bm bo dz id ig ii in ja jbo jv jw kde kea km ko lkt", -1);
        fill(map, "lo ms my nqo root sah ses sg th to vi wo yo zh", -1);
        fill(map, "am bn fa gu hi kn mr zu", 1);
        fill(map, "ff fr hy kab", 1);
        fill(map, "si", 1);
        fill(map, "ak bh guw ln mg nso pa ti wa", 1);
        fill(map, "tzm", 2);
        fill(map, "is", 3);
        fill(map, "mk", 4);
        fill(map, "fil tl", 5);
        fill(map, "lv prg", 6);
        fill(map, "lag ksh", 7);
        fill(map, "iu kw naq se sma smi smj smn sms", 8);
        fill(map, "shi", 9);
        fill(map, "mo ro", 10);
        fill(map, "bs hr sh sr", 11);
        fill(map, "gd", 12);
        fill(map, "sl", 13);
        fill(map, "he iw", 14);
        fill(map, "cs sk", 15);
        fill(map, "pl", 16);
        fill(map, "be", 17);
        fill(map, "lt", 18);
        fill(map, "mt", 19);
        fill(map, "ru uk", 17);
        fill(map, "br", 20);
        fill(map, "ga", 21);
        fill(map, "gv", 22);
        fill(map, "ar", 23);
        fill(map, "cy", 24);
        LANGUAGE_MAP.putAll(map);
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the localized plural rules for given language. </p>
     *
     * @param   lang    language which specifies the suitable plural rules
     * @return  localized plural rules
     */
    /*[deutsch]
     * <p>Ermittelt die Pluralregeln f&uuml;r die angegebene Sprache. </p>
     *
     * @param   lang    language which specifies the suitable plural rules
     * @return  localized plural rules
     */
    public static PluralRules of(Locale lang) {

        PluralRules rules = LANGUAGE_MAP.get(lang.getLanguage());

        if (rules == null) {
            return STD_RULES;
        }

        return rules;

    }

    /**
     * <p>Registers given plural rules for a language, possibly overriding
     * CLDR-default setting. </p>
     *
     * @param   lang    language which the rules shall be assigned to
     * @param   rules   localized plural rules
     */
    /*[deutsch]
     * <p>Registriert die angegebenen Pluralregeln f&uuml;r eine Sprache,
     * wobei die CLDR-Vorgabe &uuml;berschrieben werden kann. </p>
     *
     * @param   lang    language which the rules shall be assigned to
     * @param   rules   localized plural rules
     */
    public static void register(
        Locale lang,
        PluralRules rules
    ) {

        if (rules == null) {
            throw new NullPointerException("Missing plural rules.");
        }

        LANGUAGE_MAP.put(lang.getLanguage(), rules);

    }

    /**
     * <p>Determines the plural category for given number of units. </p>
     *
     * @param   count   integral number of units
     * @return  plural category, never {@code null}
     */
    /*[deutsch]
     * <p>Bestimmt die Pluralkategorie f&uuml;r die angegebene Anzahl von
     * Zeiteinheiten. </p>
     *
     * @param   count   integral number of units
     * @return  plural category, never {@code null}
     */
    public abstract PluralCategory getCategory(long count);

    private static void fill(
        Map<String, PluralRules> map,
        String languages,
        int id
    ) {

        for (String language : languages.split(" ")) {
            map.put(language, new CLDR(id));
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class CLDR
        extends PluralRules {

        //~ Instanzvariablen ----------------------------------------------

        private final int id;

        //~ Konstruktoren -------------------------------------------------

        private CLDR(int id) {
            super();

            this.id = id;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public PluralCategory getCategory(long n) {

            long mod10 = -1;
            long mod100 = -1;

            switch (this.id) {
                case 0: // STD_RULES
                    return (n == 1 ? ONE : OTHER);
                case 1: // französisch (fr)
                    return ((n == 0) || (n == 1) ? ONE : OTHER);
                case 2: // Tamazight, Central Atlas (tzm)
                    if (n == 0 || n == 1 || (n >= 11 && n <= 99)) {
                        return ONE;
                    }
                    return OTHER;
                case 3: // isländisch (is)
                    if (((n % 10) == 1) && ((n % 100) != 11)) {
                        return ONE;
                    }
                    return OTHER;
                case 4: // mazedonisch (mk)
                    return ((n % 10) == 1 ? ONE : OTHER);
                case 5: // tagalog - Philippinen (fil)
                    if (n >= 1 && n <= 3) {
                        return ONE;
                    }
                    mod10 = n % 10;
                    if (mod10 == 4 || mod10 == 6 || mod10 == 9) {
                        return OTHER;
                    } else {
                        return ONE;
                    }
                case 6: // lettisch (lv)
                    if (n % 10 == 0) {
                        return ZERO;
                    }
                    mod100 = n % 100;
                    if (mod100 >= 11 && mod100 <= 19) {
                        return ZERO;
                    } else if (((n % 10) == 1) && (mod100 != 11)) {
                        return ONE;
                    }
                    return OTHER;
                case 7: // langi (lag)
                    if (n == 0) {
                        return ZERO;
                    } else if (n == 1) {
                        return ONE;
                    }
                    return OTHER;
                case 8: // Inuktitut (iu)
                    if (n == 1) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    }
                    return OTHER;
                case 9: // tachelhit (shi)
                    if (n == 0 || n == 1) {
                        return ONE;
                    } else if (n >= 2 && n <= 10) {
                        return FEW;
                    }
                    return OTHER;
                case 10: // moldawisch (mo | ro)
                    if (n == 1) {
                        return ONE;
                    } else if (n == 0) {
                        return FEW;
                    }
                    mod100 = n % 100;
                    if (mod100 >= 1 && mod100 <= 19 && n != 1) {
                        return FEW;
                    }
                    return OTHER;
                case 11: // bosnisch (bs)
                    mod100 = n % 100;
                    if (((n % 10) == 1) && (mod100 != 11)) {
                        return ONE;
                    } else if (
                        n % 10 >= 2 && n % 10 <= 4
                        && mod100 != 12 && mod100 != 13 && mod100 != 14
                    ) {
                        return FEW;
                    }
                    return OTHER;
                case 12: // schottisch (gd)
                    if (n == 1 || n == 11) {
                        return ONE;
                    } else if (n == 2 || n == 12) {
                        return TWO;
                    } else if (
                        (n >= 3 && n <= 10)
                        || (n >= 13 && n <= 19)
                    ) {
                        return FEW;
                    }
                    return OTHER;
                case 13: // slowenisch (sl)
                    mod100 = n % 100;
                    if (mod100 == 1) {
                        return ONE;
                    } else if (mod100 == 2) {
                        return TWO;
                    } else if (mod100 == 3 || mod100 == 4) {
                        return FEW;
                    }
                    return OTHER;
                case 14: // hebräisch (he iw)
                    if (n == 1) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    } else if (n >= 11 && (n % 10 == 0)) {
                        return MANY;
                    }
                    return OTHER;
                case 15: // tschechisch (cs)
                    if (n == 1) {
                        return ONE;
                    } else if (n >= 2 && n <= 4) {
                        return FEW;
                    }
                    return OTHER;
                case 16: // polnisch (pl)
                    if (n == 1) {
                        return ONE;
                    }
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (
                        mod10 >= 2 && mod10 <= 4
                        && mod100 != 12 && mod100 != 13 && mod100 != 14
                    ) {
                        return FEW;
                    } else if (
                        (n != 1 && mod10 >= 0 && mod10 <= 1)
                        || (mod10 >= 5 && mod10 <= 9)
                        || (mod100 >= 12 && mod100 <= 14)
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 17: // belorussisch (be)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (mod10 == 1 && mod100 != 11) {
                        return ONE;
                    } else if (
                        mod10 >= 2 && mod10 <= 4
                        && mod100 != 12 && mod100 != 13 && mod100 != 14
                    ) {
                        return FEW;
                    } else if (
                        (mod10 == 0)
                        || (mod10 >= 5 && mod10 <= 9)
                        || (mod100 >= 11 && mod100 <= 14)
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 18: // litauisch (lt)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (mod10 == 1 && !(mod100 >= 11 && mod100 <= 19)) {
                        return ONE;
                    } else if (
                        mod10 >= 2 && mod10 <= 9
                        && !(mod100 >= 11 && mod100 <= 19)
                    ) {
                        return FEW;
                    }
                    return OTHER;
                case 19: // maltesisch (mt)
                    if (n == 1) {
                        return ONE;
                    }
                    mod100 = n % 100;
                    if (n == 0 || (mod100 >= 2 && mod100 <= 10)) {
                        return FEW;
                    } else if (mod100 >= 11 && mod100 <= 19) {
                        return MANY;
                    }
                    return OTHER;
                case 20: // bretonisch (br)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (
                        mod10 == 1
                        && mod100 != 11 && mod100 != 71 && mod100 != 91
                    ) {
                        return ONE;
                    } else if (
                        mod10 == 2
                        && mod100 != 12 && mod100 != 72 && mod100 != 92
                    ) {
                        return TWO;
                    } else if (
                        (mod10 == 3 || mod10 == 4 || mod10 == 9)
                        && !(mod100 >= 10 && mod100 <= 19)
                        && !(mod100 >= 70 && mod100 <= 79)
                        && !(mod100 >= 90 && mod100 <= 99)
                    ) {
                        return FEW;
                    } else if (n != 0 && ((n % 1000000) == 0)) {
                        return MANY;
                    }
                    return OTHER;
                case 21: // irisch (ga)
                    if (n == 1) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    } else if (n >= 3 && n <= 6) {
                        return FEW;
                    } else if (n >= 7 && n <= 10) {
                        return MANY;
                    }
                    return OTHER;
                case 22: // Manx - Isle Of Man (gv)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (mod10 == 1) {
                        return ONE;
                    } else if (mod10 == 2) {
                        return TWO;
                    } else if (
                        mod100 == 0 || mod100 == 20 || mod100 == 40
                        || mod100 == 60 || mod100 == 80
                    ) {
                        return FEW;
                    }
                    return OTHER;
                case 23: // arabisch (ar)
                    if (n == 0) {
                        return ZERO;
                    } else if (n == 1) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    }
                    mod100 = n % 100;
                    if (mod100 >= 3 && mod100 <= 10) {
                        return FEW;
                    } else if (mod100 >= 11 && mod100 <= 99) {
                        return MANY;
                    }
                    return OTHER;
                case 24: // walisisch (cy)
                    if (n == 0) {
                        return ZERO;
                    } else if (n == 1) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    } else if (n == 3) {
                        return FEW;
                    } else if (n == 6) {
                        return MANY;
                    }
                    return OTHER;
                default: // chinesisch (zh)
                    return OTHER;
            }

        }

    }

}
