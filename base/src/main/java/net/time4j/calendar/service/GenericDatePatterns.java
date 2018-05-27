/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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


import net.time4j.engine.DisplayStyle;
import net.time4j.format.CalendarText;
import net.time4j.format.DisplayMode;

import java.util.Locale;
import java.util.ResourceBundle;


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
     * @since   3.10/4.7
     */
    public static String get(
        String calendarType,
        DisplayStyle style,
        Locale locale
    ) {

        DisplayMode mode = DisplayMode.ofStyle(style.getStyleValue());

        if (calendarType.equals(CalendarText.ISO_CALENDAR_TYPE)) {
            return CalendarText.patternForDate(mode, locale);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        sb.append(Character.toLowerCase(mode.name().charAt(0)));
        sb.append(')');
        String key = sb.toString();

        ResourceBundle rb = GenericTextProviderSPI.getBundle(calendarType, locale);

        if (!rb.containsKey(key)) {
            rb = GenericTextProviderSPI.getBundle("generic", locale);
        }

        return rb.getString(key);

    }

}
