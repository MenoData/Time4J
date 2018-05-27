/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
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
    /**
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
