/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Evangelist.java) is part of project Time4J.
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

import net.time4j.format.CalendarText;

import java.util.Locale;


/**
 * <p>Enumeration of the four evangelists of the bible, used in some calendars with historic or religious context. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Aufz&auml;hlung der vier Evangelien der Bibel, verwendet im historischen oder religi&ouml;sen Kontext. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum Evangelist {

    //~ Statische Felder/Initialisierungen --------------------------------

    MATTHEW,

    MARK,

    LUKE,

    JOHN;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines a translation of this instance with fallback to English. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (never {@code null})
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert eine &Uuml;bersetzung oder Englisch, wenn nicht vorhanden. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (never {@code null})
     * @since   3.11/4.8
     */
    public String getDisplayName(Locale locale) {

        CalendarText names = CalendarText.getInstance("generic", locale);
        return names.getTextForms("EV", Evangelist.class).print(this);

    }

}
