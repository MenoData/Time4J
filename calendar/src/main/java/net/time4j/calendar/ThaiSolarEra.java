/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ThaiSolarEra.java) is part of project Time4J.
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

import net.time4j.engine.CalendarEra;
import net.time4j.format.CalendarText;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Thai-Solar calendar supports two eras related to either the Rattanakosin kingdom (historic)
 * or to the date of death of Buddha (used today). </p>
 *
 * @author  Meno Hochschild
 * @since   3.19/4.15
 */
/*[deutsch]
 * <p>Der Thai-Solar-Kalender unterst&uuml;tzt zwei &Auml;ras, die sich entweder auf das historische
 * Rattanakosin-K&ouml;nigreich oder auf das Datum des Todestags von Buddha (heute gebr&auml;uchlich)
 * beziehen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.19/4.15
 */
public enum ThaiSolarEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The era started on 6th of April in 1782 (used until the calendar reform of king Rama VI in year 1912).
     */
    /*[deutsch]
     * Die &Auml;ra begann am sechsten April 1782 (verwendet bis zur Kalenderreform von K&ouml;nig Rama VI
     * im Jahre 1912).
     */
    RATTANAKOSIN,

    /**
     * Users add 543 years to the gregorian AD-year in order to get the buddhist year counting.
     */
    /*[deutsch]
     * Anwender addieren 543 Jahre zum gregorianischen Jahr, um die buddhistische Jahresz&auml;hlung zu erhalten.
     */
    BUDDHIST;

    //~ Methoden ----------------------------------------------------------

    @Override
    public int getValue() {

        return this.ordinal();

    }

    /**
     * <p>Equivalent to the expression {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     * @since   3.19/4.15
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE);

    }

    /**
     * <p>Gets the description text dependent on the locale and style
     * parameters. </p>
     *
     * <p>The second argument controls the width of description. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
     * @since   3.19/4.15
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        CalendarText names = CalendarText.getInstance("buddhist", locale);
        return names.getEras(width).print(this);

    }

}
