/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PluralProviderSPI.java) is part of project Time4J.
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

package net.time4j.i18n;

import net.time4j.format.NumberType;
import net.time4j.format.PluralCategory;
import net.time4j.format.PluralProvider;
import net.time4j.format.PluralRules;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static net.time4j.format.PluralCategory.FEW;
import static net.time4j.format.PluralCategory.MANY;
import static net.time4j.format.PluralCategory.ONE;
import static net.time4j.format.PluralCategory.OTHER;
import static net.time4j.format.PluralCategory.TWO;
import static net.time4j.format.PluralCategory.ZERO;


/**
 * <p>{@code ServiceProvider}-implementation for accessing localized
 * plural rules. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public final class PluralProviderSPI
    implements PluralProvider {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<String, PluralRules> CARDINAL_MAP =
        new HashMap<String, PluralRules>(140);
    private static final PluralRules STD_CARDINALS = new StdCardinalRules(0);

    static {
        Map<String, PluralRules> cmap = new HashMap<String, PluralRules>();
        fillC(cmap, "bm bo dz id ig ii in ja jbo jv jw kde kea km ko lkt", -1);
        fillC(cmap, "lo ms my nqo root sah ses sg th to vi wo yo zh", -1);
        fillC(cmap, "pt_PT", 0);
        fillC(cmap, "am as bn fa gu hi kn mr zu", 1);
        fillC(cmap, "ff fr hy kab pt", 1);
        fillC(cmap, "si", 1);
        fillC(cmap, "ak bh guw ln mg nso pa ti wa", 1);
        fillC(cmap, "tzm", 2);
        fillC(cmap, "is", 3);
        fillC(cmap, "mk", 4);
        fillC(cmap, "fil tl", 5);
        fillC(cmap, "lv prg", 6);
        fillC(cmap, "lag ksh", 7);
        fillC(cmap, "iu kw naq se sma smi smj smn sms", 8);
        fillC(cmap, "shi", 9);
        fillC(cmap, "mo ro", 10);
        fillC(cmap, "bs hr sh sr", 11);
        fillC(cmap, "gd", 12);
        fillC(cmap, "sl", 13);
        fillC(cmap, "he iw", 14);
        fillC(cmap, "cs sk", 15);
        fillC(cmap, "pl", 16);
        fillC(cmap, "be", 17);
        fillC(cmap, "lt", 18);
        fillC(cmap, "mt", 19);
        fillC(cmap, "ru uk", 17);
        fillC(cmap, "br", 20);
        fillC(cmap, "ga", 21);
        fillC(cmap, "gv", 22);
        fillC(cmap, "ar", 23);
        fillC(cmap, "cy", 24);
        fillC(cmap, "dsb hsb", 25);
        CARDINAL_MAP.putAll(cmap);
    }

    private static final Map<String, PluralRules> ORDINAL_MAP =
        new HashMap<String, PluralRules>(140);
    private static final PluralRules STD_ORDINALS = new StdOrdinalRules(0);

    static {
        Map<String, PluralRules> omap = new HashMap<String, PluralRules>();
        fillO(omap, "sv", 1);
        fillO(omap, "fil fr ga hy lo mo ms ro tl vi", 2);
        fillO(omap, "hu", 3);
        fillO(omap, "ne", 4);
        fillO(omap, "kk", 5);
        fillO(omap, "it", 6);
        fillO(omap, "ka", 7);
        fillO(omap, "sq", 8);
        fillO(omap, "en", 9);
        fillO(omap, "mr", 10);
        fillO(omap, "ca", 11);
        fillO(omap, "mk", 12);
        fillO(omap, "az", 13);
        fillO(omap, "gu hi", 14);
        fillO(omap, "as bn", 15);
        fillO(omap, "cy", 16);
        fillO(omap, "be", 17);
        fillO(omap, "uk", 18);
        ORDINAL_MAP.putAll(omap);
    }

    //~ Konstruktoren -----------------------------------------------------

    /** For {@code java.util.ServiceLoader}. */
    public PluralProviderSPI() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    public PluralRules load(
        Locale locale,
        NumberType numType
    ) {

        Map<String, PluralRules> map;
        PluralRules stdRules;

        switch (numType) {
            case CARDINALS:
                map = CARDINAL_MAP;
                stdRules = STD_CARDINALS;
                break;
            case ORDINALS:
                map = ORDINAL_MAP;
                stdRules = STD_ORDINALS;
                break;
            default:
                throw new UnsupportedOperationException(numType.name());
        }

        PluralRules rules = null;

        if (!locale.getCountry().equals("")) {
            StringBuilder kb = new StringBuilder();
            kb.append(locale.getLanguage());
            kb.append('_');
            kb.append(locale.getCountry());
            rules = map.get(kb.toString());
        }

        if (rules == null) {
            rules = map.get(locale.getLanguage());
        }

        if (rules == null) {
            return stdRules;
        }

        return rules;

    }

    private static void fillC(
        Map<String, PluralRules> map,
        String languages,
        int id
    ) {

        for (String language : languages.split(" ")) {
            map.put(language, new StdCardinalRules(id));
        }

    }

    private static void fillO(
        Map<String, PluralRules> map,
        String languages,
        int id
    ) {

        for (String language : languages.split(" ")) {
            map.put(language, new StdOrdinalRules(id));
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class StdCardinalRules
        extends PluralRules {

        //~ Instanzvariablen ----------------------------------------------

        private final int id;

        //~ Konstruktoren -------------------------------------------------

        private StdCardinalRules(int id) {
            super();

            this.id = id;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public PluralCategory getCategory(long n) {

            long mod10;
            long mod100;

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
                case 25: // sorbisch (dsb/hsb)
                    mod100 = n % 100;
                    if (mod100 == 1) {
                        return ONE;
                    } else if (mod100 == 2) {
                        return TWO;
                    } else if (mod100 == 3 || mod100 == 4) {
                        return FEW;
                    }
                    return OTHER;
                default: // chinesisch (zh)
                    return OTHER;
            }

        }

        @Override
        public NumberType getNumberType() {

            return NumberType.CARDINALS;

        }

    }

    private static class StdOrdinalRules
        extends PluralRules {

        //~ Instanzvariablen ----------------------------------------------

        private final int id;

        //~ Konstruktoren -------------------------------------------------

        private StdOrdinalRules(int id) {
            super();

            this.id = id;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public PluralCategory getCategory(long n) {

            long mod10;
            long mod100;

            switch (this.id) {
                case 0: // STD_RULES
                    return OTHER;
                case 1: // schwedisch (sv)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (
                        ((mod10 == 1) || (mod10 == 2))
                        && !((mod100 == 11) || (mod100 == 12))
                    ) {
                        return ONE;
                    }
                    return OTHER;
                case 2: // französisch (fr)
                    if (n == 1) {
                        return ONE;
                    }
                    return OTHER;
                case 3: // ungarisch (hu)
                    if ((n == 1) || (n == 5)) {
                        return ONE;
                    }
                    return OTHER;
                case 4: // nepalesisch (ne)
                    if ((n >= 1) || (n <= 4)) {
                        return ONE;
                    }
                    return OTHER;
                case 5: // kasachisch (kk)
                    mod10 = n % 10;
                    if (
                        (mod10 == 6)
                        || (mod10 == 9)
                        || ((mod10 == 0) && (n != 0))
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 6: // italienisch (it)
                    if (
                        (n == 8)
                        || (n == 11)
                        || (n == 80)
                        || (n == 800)
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 7: // georgisch (ka)
                    mod100 = n % 100;
                    if (n == 1) {
                        return ONE;
                    } else if (
                        (n == 0)
                        || (mod100 >= 2 && mod100 <= 20)
                        || (mod100 == 40)
                        || (mod100 == 60)
                        || (mod100 == 80)
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 8: // albanisch (sq)
                    if (n == 1) {
                        return ONE;
                    } else if (((n % 10) == 4) && ((n % 100) != 14)) {
                        return MANY;
                    }
                    return OTHER;
                case 9: // englisch (en)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if ((mod10 == 1) && (mod100 != 11)) {
                        return ONE;
                    } else if ((mod10 == 2) && (mod100 != 12)) {
                        return TWO;
                    } else if ((mod10 == 3) && (mod100 != 13)) {
                        return FEW;
                    }
                    return OTHER;
                case 10: // Marathi - Indien (mr)
                    if (n == 1) {
                        return ONE;
                    } else if ((n == 2) || (n == 3)) {
                        return TWO;
                    } else if (n == 4) {
                        return FEW;
                    }
                    return OTHER;
                case 11: // katalanisch (ca)
                    if ((n == 1) || (n == 3)) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    } else if (n == 4) {
                        return FEW;
                    }
                    return OTHER;
                case 12: // mazedonisch (mk)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if ((mod10 == 1) && (mod100 != 11)) {
                        return ONE;
                    } else if ((mod10 == 2) && (mod100 != 12)) {
                        return TWO;
                    } else if (
                        ((mod10 == 7) || (mod10 == 8))
                        && !((mod100 == 17) || (mod100 == 18))
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 13: // aserbeidschanisch (az)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    long mod1000 = n % 1000;
                    if (
                        (mod10 == 1) || (mod10 == 2) || (mod10 == 5)
                        || (mod10 == 7) || (mod10 == 8) || (mod100 == 20)
                        || (mod100 == 50) || (mod100 == 70) || (mod100 == 80)
                    ) {
                        return ONE;
                    } else if (
                        (mod10 == 3) || (mod10 == 4)
                        || (mod1000 == 100) || (mod1000 == 200)
                        || (mod1000 == 300) || (mod1000 == 400)
                        || (mod1000 == 500) || (mod1000 == 600)
                        || (mod1000 == 700) || (mod1000 == 800)
                        || (mod1000 == 900)
                    ) {
                        return FEW;
                    } else if (
                        (n == 0) || (mod10 == 6)
                        || (mod100 == 40)|| (mod100 == 60)|| (mod100 == 90)
                    ) {
                        return MANY;
                    }
                    return OTHER;
                case 14: // hindi (hi)
                    if (n == 1) {
                        return ONE;
                    } else if ((n == 2) || (n == 3)) {
                        return TWO;
                    } else if (n == 4) {
                        return FEW;
                    } else if (n == 6) {
                        return MANY;
                    }
                    return OTHER;
                case 15: // bengalisch (bn)
                    if (
                        (n == 1)
                        || (n == 5)
                        || ((n >= 7) && (n <= 10))
                    ) {
                        return ONE;
                    } else if ((n == 2) || (n == 3)) {
                        return TWO;
                    } else if (n == 4) {
                        return FEW;
                    } else if (n == 6) {
                        return MANY;
                    }
                    return OTHER;
                case 16: // walisisch (cy)
                    if (
                        (n == 0)
                        || ((n >= 7) && (n <= 9))
                    ) {
                        return ZERO;
                    } else if (n == 1) {
                        return ONE;
                    } else if (n == 2) {
                        return TWO;
                    } else if ((n == 3) || (n == 4)) {
                        return FEW;
                    } else if ((n == 5) || (n == 6)) {
                        return MANY;
                    }
                    return OTHER;
                case 17: // weißrussisch (be)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (
                        ((mod10 == 2) || (mod10 == 3))
                        && !((mod100 == 12) || (mod100 == 13))
                    ) {
                        return FEW;
                    }
                    return OTHER;
                case 18: // ukrainisch (uk)
                    mod10 = n % 10;
                    mod100 = n % 100;
                    if (
                        (mod10 == 3)
                        && (mod100 != 13)
                    ) {
                        return FEW;
                    }
                    return OTHER;
                default: // fallback
                    return OTHER;
            }

        }

        @Override
        public NumberType getNumberType() {

            return NumberType.ORDINALS;

        }

    }

}
