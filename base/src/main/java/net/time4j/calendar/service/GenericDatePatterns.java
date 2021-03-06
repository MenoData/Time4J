/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GenericDatePatterns.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.format.CalendarText;
import net.time4j.format.internal.PropertyBundle;

import java.time.format.FormatStyle;
import java.util.Locale;


/**
 * <p>Represents a low-level access to generic non-iso date patterns. </p>
 *
 * @author  Meno Hochschild
 * @since   3.10/4.7
 */
public final class GenericDatePatterns {

    //~ Konstruktoren -----------------------------------------------------

    private GenericDatePatterns() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines a suitable date pattern. </p>
     *
     * @param   calendarType    general calendar type
     * @param   style           format style
     * @param   locale          desired language and/or country
     * @return  localized date pattern
     * @throws  UnsupportedOperationException if given style is not supported
     * @since   5.8
     */
    public static String get(
        String calendarType,
        FormatStyle style,
        Locale locale
    ) {

        if (calendarType.equals(CalendarText.ISO_CALENDAR_TYPE)) {
            return CalendarText.patternForDate(style, locale);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        sb.append(Character.toLowerCase(style.name().charAt(0)));
        sb.append(')');
        String key = sb.toString();

        PropertyBundle rb = GenericTextProviderSPI.getBundle(calendarType, locale);

        if (!rb.containsKey(key)) {
            rb = GenericTextProviderSPI.getBundle("generic", locale);
        }

        return rb.getString(key);

    }

}
