/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduEra.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.engine.CalendarEra;
import net.time4j.format.CalendarText;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Hindu calendar supports several eras in different regions of Indian subcontinent. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Der Hindu-Kalender unterst&uuml;tzt mehrere &Auml;ras in verschiedenen Regionen des
 * indischen Subkontinents. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public enum HinduEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The onset of this ancient era (iron age) is 3179 years before Saka.
     */
    /*[deutsch]
     * Der Beginn dieser historischen &Auml;ra (eisernes Zeitalter) liegt 3179 Jahre vor Saka.
     */
    KALI_YUGA,

    /**
     * The onset of this era mainly used in Nepal is 955 years before Saka.
     */
    /*[deutsch]
     * Der Beginn dieser vorwiegend in Nepal verwendeten &Auml;ra liegt 955 Jahre vor Saka.
     */
    NEPALESE,

    /**
     * The onset of this era mainly used in Kerala (part of Malayalam calendar) is 900 years before Saka.
     */
    /*[deutsch]
     * Der Beginn dieser vorwiegend in Kerala verwendeten &Auml;ra (Bestandteil des Malayalam-Kalenders)
     * liegt 900 Jahre vor Saka.
     */
    KOLLAM,

    /**
     * The onset of this era mainly used in Northern India is 135 years before Saka.
     */
    /*[deutsch]
     * Der Beginn dieser vorwiegend in Nordindien verwendeten &Auml;ra liegt 135 Jahre vor Saka.
     */
    VIKRAMA,

    /**
     * The onset of this era is in gregorian year +78.
     */
    /*[deutsch]
     * Der Beginn dieser &Auml;ra liegt im gregorianischen Jahr +78.
     */
    SAKA,

    /**
     * The onset of this era mainly used in West Bengal is 515 years after Saka.
     */
    /*[deutsch]
     * Der Beginn dieser vorwiegend in Westbengalen verwendeten &Auml;ra liegt 515 Jahre nach Saka.
     */
    BENGAL;

    private static final int[] SAKA_OFFSETS = {3179, 955, 900, 135, 0, -515};

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Equivalent to the expression {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     */
    public String getDisplayName(Locale locale) {
        return this.getDisplayName(locale, TextWidth.WIDE);
    }

    /**
     * <p>Gets the description text dependent on the locale and style parameters. </p>
     *
     * <p>The second argument controls the width of description. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @return  descriptive text for given locale and style (never {@code null})
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
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {
        CalendarText names = CalendarText.getInstance("hindu", locale);
        return names.getEras(width).print(this);
    }

    /**
     * <p>Scales given year of era to another year related to this era. </p>
     *
     * @param   era         era reference of given year
     * @param   yearOfEra   year reckoned in given era
     * @return  year related to this era
     * @throws  IllegalArgumentException if numerical overflow occurs
     */
    /*[deutsch]
     * <p>Skaliert das angegebene Jahr der &Auml;ra zu einem anderen Jahreswert bezogen auf diese &Auml;ra. </p>
     *
     * @param   era         era reference of given year
     * @param   yearOfEra   year reckoned in given era
     * @return  year related to this era
     * @throws  IllegalArgumentException if numerical overflow occurs
     */
    public int yearOfEra(
        HinduEra era,
        int yearOfEra
    ) {
        try {
            return Math.subtractExact(
                Math.addExact(yearOfEra, SAKA_OFFSETS[this.ordinal()]),
                SAKA_OFFSETS[era.ordinal()]);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Out of range: " + yearOfEra);
        }
    }

}
