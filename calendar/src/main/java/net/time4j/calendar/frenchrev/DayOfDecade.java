/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DayOfDecade.java) is part of project Time4J.
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

package net.time4j.calendar.frenchrev;

import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>Represents the days of decade used in the French revolutionary calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Dekadentage, die im franz&ouml;sischen Revolutionskalender Verwendung fanden. </p>
 *
 * @author  Meno Hochschild
 * @since   3.33/4.28
 */
public enum DayOfDecade {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The first day of decade. </p>
     */
    /*[deutsch]
     * <p>Der erste Dekadentag. </p>
     */
    PRIMIDI,

    /**
     * <p>The second day of decade. </p>
     */
    /*[deutsch]
     * <p>Der zweite Dekadentag. </p>
     */
    DUODI,

    /**
     * <p>The third day of decade. </p>
     */
    /*[deutsch]
     * <p>Der dritte Dekadentag. </p>
     */
    TRIDI,

    /**
     * <p>The fourth day of decade. </p>
     */
    /*[deutsch]
     * <p>Der vierte Dekadentag. </p>
     */
    QUARTIDI,

    /**
     * <p>The fifth day of decade. </p>
     */
    /*[deutsch]
     * <p>Der f&uuml;nfte Dekadentag. </p>
     */
    QUINTIDI,

    /**
     * <p>The sixth day of decade. </p>
     */
    /*[deutsch]
     * <p>Der sechste Dekadentag. </p>
     */
    SEXTIDI,

    /**
     * <p>The seventh day of decade. </p>
     */
    /*[deutsch]
     * <p>Der siebte Dekadentag. </p>
     */
    SEPTIDI,

    /**
     * <p>The eigth day of decade. </p>
     */
    /*[deutsch]
     * <p>Der achte Dekadentag. </p>
     */
    OCTIDI,

    /**
     * <p>The ninth day of decade. </p>
     */
    /*[deutsch]
     * <p>Der neunte Dekadentag. </p>
     */
    NONIDI,

    /**
     * <p>The tenth day of decade. </p>
     */
    /*[deutsch]
     * <p>Der zehnte Dekadentag. </p>
     */
    DECADI;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   dayOfDecade     french revolutionary day of decade in the range [1-10]
     * @return  day of decade as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   dayOfDecade     french revolutionary day of decade in the range [1-10]
     * @return  day of decade as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static DayOfDecade valueOf(int dayOfDecade) {

        if ((dayOfDecade < 1) || (dayOfDecade > 10)) {
            throw new IllegalArgumentException("Out of range: " + dayOfDecade);
        }

        return DayOfDecade.values()[dayOfDecade - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of day of decade in the range [1-10]
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of day of decade in the range [1-10]
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Equivalent to {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * <p>The usage of the French language is strongly recommended. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * <p>Es wird empfohlen, die franz&ouml;sische Sprache zu verwenden. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * <p>The usage of the French language is strongly recommended. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   oc          output context
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>Es wird empfohlen, die franz&ouml;sische Sprache zu verwenden. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   oc          output context
     * @return  descriptive text for given locale (never {@code null})
     * @see     Locale#FRENCH
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext oc
    ) {

        CalendarText names = CalendarText.getInstance("extra/frenchrev", locale);
        String key = ((width == TextWidth.NARROW) ? "N" : (oc == OutputContext.FORMAT ? "w" : "W"));
        return names.getTextForms().get("D(" + key + ")_" + this.getValue());

    }

}
