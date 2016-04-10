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

import net.time4j.PlainDate;
import net.time4j.base.MathUtils;
import net.time4j.engine.CalendarDate;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.EpochDays;
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
     * The Rattanakosin era started counting of years on 6th of April in 1782 and was decreed by
     * king Rama V in year 1888 and used until the calendar reform of king Rama VI in year 1912.
     *
     * According to <a href="http://www.changnoi-0815.de/englisch/e_kalender.htm">the original source</a>
     * of the Wikipedia-article this era started years on first of April. Time4J always converts the
     * Rattanakosin era to Buddhist era in any thai-date-input.
     */
    /*[deutsch]
     * Die Rattanakosin-&Auml;ra begann am sechsten April 1782, wurde von K&ouml;nig Rama V im Jahre
     * 1888 proklamiert und verwendet bis zur Kalenderreform von K&ouml;nig Rama VI im Jahre 1912.
     *
     * Nach <a href="http://www.changnoi-0815.de/englisch/e_kalender.htm">der Originalquelle</a>
     * des Wikipedia-Artikels begann diese &Auml;ra Jahre am ersten April. Time4J konvertiert die
     * Rattankosin-&Auml;ra immer zur buddhistischen &Auml;ra, wenn Thai-Datumsangaben als
     * Eingabe verarbeitet werden.
     */
    RATTANAKOSIN,

    /**
     * Standard era where users add 543 years to the gregorian AD-year in order to get the buddhist year counting.
     *
     * Before year 1941, the buddhist year started on first of April, then on first of January.
     */
    /*[deutsch]
     * In dieser Standard-&Auml;ra addieren Anwender 543 Jahre zum gregorianischen Jahr, um die buddhistische
     * Jahresz&auml;hlung zu erhalten.
     *
     * Vor 1941 startete das buddhistische Jahr am ersten April, danach am ersten Januar.
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

    /**
     * <p>Determines the Thai year corresponding to given calendar date. </p>
     *
     * @param   date    calendar date of any type
     * @return  thai year in this era
     * @throws  IllegalArgumentException if the resulting year would become zero or negative
     * @since   3.19/4.15
     */
    /*[deutsch]
     * <p>Bestimmt das Thai-Jahr, das dem angegebenen Kalenderdatum entspricht. </p>
     *
     * @param   date    calendar date of any type
     * @return  thai year in this era
     * @throws  IllegalArgumentException if the resulting year would become zero or negative
     * @since   3.19/4.15
     */
    public int getYear(CalendarDate date) {

        PlainDate iso = PlainDate.of(date.getDaysSinceEpochUTC(), EpochDays.UTC);
        int year;

        if (this == RATTANAKOSIN) {
            year = iso.getYear() - 1781;
            if (iso.getMonth() < 4) {
                year--;
            }
        } else { // BUDDHIST
            year = iso.getYear() + 543;
            if ((year < 2484) && (iso.getMonth() < 4)) {
                year--;
            }
        }

        if (year < 1) {
            throw new IllegalArgumentException("Out of range: " + date);
        }

        return year;

    }

    // taking into account different new year rules of the past
    int toIsoYear(
        int thaiYear,
        int month
    ) {

        if (thaiYear < 1) {
            throw new IllegalArgumentException("Out of bounds: " + thaiYear);
        }

        int iso;

        if (this == RATTANAKOSIN) {
            iso = MathUtils.safeAdd(thaiYear, 1781); // thai-year 1 => AD 1782
            if (month < 4) {
                iso = MathUtils.safeAdd(iso, 1);
            }
        } else { // BUDDHIST
            iso = MathUtils.safeSubtract(thaiYear, 543);
            if (month < 4) {
                if (iso == 1940) {
                    throw new IllegalArgumentException("Buddhist year 2483 does not know month: " + month);
                } else if (iso < 1940) {
                    iso = MathUtils.safeAdd(iso, 1);
                }
            }
        }

        return iso;

    }

}
