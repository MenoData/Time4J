/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EthiopianEra.java) is part of project Time4J.
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
 * <p>The Ethiopian calendar supports two eras. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Der &auml;tiopische Kalender unterst&uuml;tzt zwei &Auml;ras. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum EthiopianEra
    implements CalendarEra {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The era &quot;Anno Mundi&quot; (English - &quot;Year of the World&quot;) in the version
     * of Panodoros starts at BC-5493-08-29 (Julian calendar).
     */
    /*[deutsch]
     * Die &Auml;ra &quot;Anno Mundi&quot; (Sch&ouml;pfungsdatum) in der Version
     * von Panodoros beginnt zum Datum BC-5493-08-29 (julianischer Kalender).
     */
    AMETE_ALEM,

    /**
     * This era (English - &quot;Year of Grace&quot;) is used for current years {@code >= 1} and
     * starts at AD-8-08-29 (Julian calendar).
     */
    /*[deutsch]
     * Diese &Auml;ra (Jahr der Barmherzigkeit) wird f&uuml;r aktuelle Jahre {@code >= 1} gebraucht und
     * beginnt zum Datum AD-8-08-29 (julianischer Kalender).
     */
    AMETE_MIHRET;

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
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth)
     * @since   3.11/4.8
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
     * @since   3.11/4.8
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
     * @since   3.11/4.8
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        CalendarText names = CalendarText.getInstance("ethiopic", locale);
        return names.getEras(width).print(this);

    }

}
