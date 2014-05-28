/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Quarter.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.GregorianDate;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Repr&auml;sentiert ein Quartal (meist eines Jahres). </p>
 *
 * @author  Meno Hochschild
 */
public enum Quarter
    implements ChronoCondition<GregorianDate> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Erstes Quartal mit dem numerischen Wert {@code 1}. </p>
     */
    Q1,

    /**
     * <p>Zweites Quartal mit dem numerischen Wert {@code 2}. </p>
     */
    Q2,

    /**
     * <p>Drittes Quartal mit dem numerischen Wert {@code 3}. </p>
     */
    Q3,

    /**
     * <p>Letztes Quartal mit dem numerischen Wert {@code 4}. </p>
     */
    Q4;

    private static final Quarter[] ENUMS = Quarter.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert die zum kalendarischen Integer-Wert passende
     * Enum-Konstante. </p>
     *
     * @param   quarter     value in the range [1-4]
     * @return  enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static Quarter valueOf(int quarter) {

        if ((quarter < 1) || (quarter > 4)) {
            throw new IllegalArgumentException("Out of range: " + quarter);
        }

        return ENUMS[quarter - 1];

    }

    /**
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  int (Q1 = 1, Q2 = 2, Q3 = 3, Q4 = 4)
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Ermittelt das n&auml;chste Quartal. </p>
     *
     * <p>Auf {@code Q4} angewandt ist das Ergebnis {@code Q1}. </p>
     *
     * @return  next quarter rolling at last quarter
     */
    public Quarter next() {

        return this.roll(1);

    }

    /**
     * <p>Ermittelt das vorherige Quartal. </p>
     *
     * <p>Auf {@code Q1} angewandt ist das Ergebnis {@code Q4}. </p>
     *
     * @return  previous quarter rolling at first quarter
     */
    public Quarter previous() {

        return this.roll(-1);

    }

    /**
     * <p>Rollt um die angegebene Anzahl von Quartalen vor oder
     * zur&uuml;ck. </p>
     *
     * @param   quarters    count of quarteryears (maybe negative)
     * @return  result of rolling operation
     */
    public Quarter roll(int quarters) {

        return Quarter.valueOf(
            (this.ordinal() + (quarters % 4 + 4)) % 4 + 1);

    }

    /**
     * <p>Liefert eine Beschreibung in der angegebenen Sprache in Langform
     * und entspricht {@code getDisplayName(locale, true)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, boolean)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, true);

    }

    /**
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. </p>
     *
     * @param   locale      language setting
     * @param   longText    {@code true} if the long form is required else
     *                      {@code false} for the short form
     * @return  short or long descriptive text (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        boolean longText
    ) {

        CalendarText names =
            CalendarText.getInstance(ISO_CALENDAR_TYPE, locale);
        TextWidth tw = (longText ? TextWidth.WIDE : TextWidth.ABBREVIATED);
        return names.getQuarters(tw, OutputContext.FORMAT).print(this);

    }

    @Override
    public boolean test(GregorianDate context) {

        int month = context.getMonth();
        return (this.getValue() == ((month - 1) / 3) + 1);

    }

}
