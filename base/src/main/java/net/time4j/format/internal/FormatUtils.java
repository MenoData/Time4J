/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormatUtils.java) is part of project Time4J.
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

package net.time4j.format.internal;

import java.util.Locale;


/**
 * Small utility class for localization purposes.
 *
 * @author  Meno Hochschild
 * @since   3.37/4.32
 */
/*[deutsch]
 * Kleine Hilfsklasse f&uuml;r Lokalisierungszwecke.
 *
 * @author  Meno Hochschild
 * @since   3.37/4.32
 */
public class FormatUtils {

    //~ Konstruktoren -----------------------------------------------------

    private FormatUtils() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Extracts the region or country information from given locale. </p>
     *
     * @param   locale      localization info
     * @return  usually an ISO-3166-2 region/country code in upper case or an empty string
     */
    /*[deutsch]
     * <p>Ermittelt die Region- oder Landesangabe aus der angegebenen Lokalisierungsinformation. </p>
     *
     * @param   locale      localization info
     * @return  usually an ISO-3166-2 region/country code in upper case or an empty string
     */
    public static String getRegion(Locale locale) {

        String region = locale.getUnicodeLocaleType("rg");

        if ((region != null) && (region.length() == 6)) {
            String upper = region.toUpperCase(Locale.US);
            if (upper.endsWith("ZZZZ")) {
                return upper.substring(0, 2);
            }
        }

        return locale.getCountry();

    }

    /**
     * <p>Determines if given locale indicates using the default week model of a calendar. </p>
     *
     * @param   locale      localization info
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ermittelt, ob die angegebene Lokalisierungsinformation die Verwendung des standardm&auml;&szlig;igen
     * Wochenmodells eines Kalenders anzeigt oder nicht. </p>
     *
     * @param   locale      localization info
     * @return  boolean
     */
    public static boolean useDefaultWeekmodel(Locale locale) {

        String fw = locale.getUnicodeLocaleType("fw");

        if ((fw != null) && (getWeekdayISO(fw) != 0)) {
            return false;
        }

        return getRegion(locale).isEmpty();

    }

    /**
     * Strips off any timezone symbols in clock time patterns.
     *
     * @param   pattern     the CLDR pattern to be processed
     * @return  possibly changed pattern
     */
    public static String removeZones(String pattern) {

        boolean literal = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = pattern.length(); i < n; i++) {
            char c = pattern.charAt(i);

            if (c == '\'') {
                if (i + 1 < n && pattern.charAt(i + 1) == '\'') {
                    sb.append(c);
                    i++;
                } else {
                    literal = !literal;
                }
                sb.append(c);
            } else if (literal) {
                sb.append(c);
            } else if (c != 'z' && c != 'Z' && c != 'v' && c != 'V' && c != 'x' && c != 'X') {
                sb.append(c);
            }
        }

        for (int j = 0; j < sb.length(); j++) {
            char c = sb.charAt(j);

            if (c == ' ' && j + 1 < sb.length() && sb.charAt(j + 1) == ' ') {
                sb.deleteCharAt(j);
                j--;
            } else if (c == '[' || c == ']' || c == '(' || c == ')') { // check locales es, fa, ps, uz
                sb.deleteCharAt(j);
                j--;
            }
        }

        String result = sb.toString().trim();

        if (result.endsWith(" '")) { // special case for de, fr_BE
            result = result.substring(0, result.length() - 2) + "'";
        } else if (result.endsWith(",")) { // special case for hy
            result = result.substring(0, result.length() - 1);
        }

        return result;

    }

    /**
     * Replaces any occurence of given old char by new char in clock time patterns.
     *
     * @param   pattern     the CLDR pattern to be processed
     * @param   oldChar     the old char to be replaced
     * @param   newChar     the replacement for the old char
     * @return  possibly changed pattern
     * @since   5.8
     */
    public static String replaceSymbol(
        String pattern,
        char oldChar,
        char newChar
    ) {

        boolean literal = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = pattern.length(); i < n; i++) {
            char c = pattern.charAt(i);

            if (c == '\'') {
                if (i + 1 < n && pattern.charAt(i + 1) == '\'') {
                    sb.append(c);
                    i++;
                } else {
                    literal = !literal;
                }
                sb.append(c);
            } else if (literal) {
                sb.append(c);
            } else if (c == oldChar) {
                sb.append(newChar);
            } else {
                sb.append(c);
            }
        }

        return sb.toString().trim();

    }

    private static int getWeekdayISO(String weekday) {

        switch (weekday) {
            case "mon":
                return 1;
            case "tue":
                return 2;
            case "wed":
                return 3;
            case "thu":
                return 4;
            case "fri":
                return 5;
            case "sat":
                return 6;
            case "sun":
                return 7;
            default:
                return 0;
        }

    }

}
