/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
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


/**
 * Small utility class for localization purposes.
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
/*[deutsch]
 * Kleine Hilfsklasse f&uuml;r Lokalisierungszwecke.
 *
 * @author  Meno Hochschild
 * @since   4.0
 */
public class FormatUtils {

    //~ Konstruktoren -----------------------------------------------------

    private FormatUtils() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * Strips off any timezone symbols in clock time patterns.
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

}
