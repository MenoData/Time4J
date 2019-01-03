/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BadiMonth.java) is part of project Time4J.
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

import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>Represents the months used in the Badi calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Monate, die im Badi-Kalender Verwendung fanden. </p>
 *
 * @author  Meno Hochschild
 * @since   5.3
 */
public enum BadiMonth {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The first month starting at vernal equinox in March. </p>
     */
    /*[deutsch]
     * <p>Der erste Monat beginnend zum Fr&uuml;hlingsanfang im M&auml;rz. </p>
     */
    BAHA,

    /**
     * <p>The second month. </p>
     */
    /*[deutsch]
     * <p>Der zweite Monat. </p>
     */
    JALAL,

    /**
     * <p>The third month. </p>
     */
    /*[deutsch]
     * <p>Der dritte Monat. </p>
     */
    JAMAL,

    /**
     * <p>The fourth month. </p>
     */
    /*[deutsch]
     * <p>Der vierte Monat. </p>
     */
    AZAMAT,

    /**
     * <p>The fifth month. </p>
     */
    /*[deutsch]
     * <p>Der f&uuml;nfte Monat. </p>
     */
    NUR,

    /**
     * <p>The sixth month. </p>
     */
    /*[deutsch]
     * <p>Der sechste Monat. </p>
     */
    RAHMAT,

    /**
     * <p>The seventh month. </p>
     */
    /*[deutsch]
     * <p>Der siebente Monat. </p>
     */
    KALIMAT,

    /**
     * <p>The eight month. </p>
     */
    /*[deutsch]
     * <p>Der achte Monat. </p>
     */
    KAMAL,

    /**
     * <p>The ninth month. </p>
     */
    /*[deutsch]
     * <p>Der neunte Monat. </p>
     */
    ASMA,

    /**
     * <p>The tenth month. </p>
     */
    /*[deutsch]
     * <p>Der zehnte Monat. </p>
     */
    IZZAT,

    /**
     * <p>The eleventh month. </p>
     */
    /*[deutsch]
     * <p>Der elfte Monat. </p>
     */
    MASHIYYAT,

    /**
     * <p>The twelvth month. </p>
     */
    /*[deutsch]
     * <p>Der zw&ouml;lfte Monat. </p>
     */
    ILM,

    /**
     * <p>The thirteenth month. </p>
     */
    /*[deutsch]
     * <p>Der dreizehnte Monat. </p>
     */
    QUDRAT,

    /**
     * <p>The fourteenth month. </p>
     */
    /*[deutsch]
     * <p>Der vierzehnte Monat. </p>
     */
    QAWL,

    /**
     * <p>The fifteenth month. </p>
     */
    /*[deutsch]
     * <p>Der f&uuml;nfzehnte Monat. </p>
     */
    MASAIL,

    /**
     * <p>The sixteenth month. </p>
     */
    /*[deutsch]
     * <p>Der sechzehnte Monat. </p>
     */
    SHARAF,

    /**
     * <p>The seventeenth month. </p>
     */
    /*[deutsch]
     * <p>Der siebzehnte Monat. </p>
     */
    SULTAN,

    /**
     * <p>The eighteenth month. </p>
     */
    /*[deutsch]
     * <p>Der achtzehnte Monat. </p>
     */
    MULK,

    /**
     * <p>The nineteenth month. </p>
     */
    /*[deutsch]
     * <p>Der neunzehnte Monat. </p>
     */
    ALA;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   month   badi month in the range [1-19]
     * @return  badi month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   badi month in the range [1-19]
     * @return  badi month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static BadiMonth valueOf(int month) {

        if ((month < 1) || (month > 19)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return BadiMonth.values()[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of badi month in the range [1-19]
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of badi month in the range [1-19]
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * <p>Equivalent to {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Auml;quivalent zu {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text for given locale (never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale. </p>
     *
     * <p>Note: The abbreviated style will produce numbers, and the narrow style will produce
     * one single capital letter per month. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   oc          output context
     * @return  descriptive text for given locale (never {@code null})
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>Hinweis: Abk&uuml;rzungen sind numerisch, und der NARROW-Stil wird einen einfachen Gro&szlig;buchstaben
     * pro Monat fabrizieren. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   oc          output context
     * @return  descriptive text for given locale (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext oc
    ) {

        CalendarText names = CalendarText.getInstance("extra/bahai", locale);
        return names.getStdMonths(width, oc).print(this);

    }

}
