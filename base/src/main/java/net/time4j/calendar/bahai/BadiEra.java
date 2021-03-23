/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BadiEra.java) is part of project Time4J.
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

package net.time4j.calendar.bahai;

import net.time4j.engine.CalendarEra;
import net.time4j.format.CalendarText;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Badi calendar only supports one single era which is related
 * to the gregorian date of 21st of March in year 1844. </p>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
/*[deutsch]
 * <p>Der Badi-Kalender unterst&uuml;tzt nur eine einzige &Auml;ra, die sich auf
 * das gregorianische Datum 1844-03-21 bezieht. </p>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
public enum BadiEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton instance.
     */
    /*[deutsch]
     * Singleton-Instanz.
     */
    BAHAI;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and width (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and width (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        return accessor(locale, width).print(this);

    }

    // also called by era element
    static TextAccessor accessor(
        Locale locale,
        TextWidth width
    ) {

        String variant;

        switch (width) {
            case WIDE:
                variant = "w";
                break;
            case ABBREVIATED:
            case SHORT:
                variant = "a";
                break;
            case NARROW:
                variant = "n";
                break;
            default:
                throw new UnsupportedOperationException(width.name());
        }

        CalendarText ct = CalendarText.getInstance("bahai", locale);
        return ct.getTextForms("E", BadiEra.class, variant);

    }

}
