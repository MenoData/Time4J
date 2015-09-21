/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PersianMonth.java) is part of project Time4J.
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

import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Persian calendar defines 12 persian months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.9/4.6
 */
/*[deutsch]
 * <p>Der persische Kalender definiert 12 persische Monate. </p>
 *
 * @author  Meno Hochschild
 * @since   3.9/4.6
 */
public enum PersianMonth
    implements ChronoCondition<PersianCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The 1st month of Persian Calendar with 31 days.
     */
    /*[deutsch]
     * Der erste Monat des persischen Kalenders mit 31 Tagen.
     */
    FARVARDIN,

    /**
     * The 2nd month of Persian Calendar with 31 days.
     */
    /*[deutsch]
     * Der zweite Monat des persischen Kalenders mit 31 Tagen.
     */
    ORDIBEHESHT,

    /**
     * The 3rd month of Persian Calendar with 31 days.
     */
    /*[deutsch]
     * Der dritte Monat des persischen Kalenders mit 31 Tagen.
     */
    KHORDAD,

    /**
     * The 4th month of Persian Calendar with 31 days.
     */
    /*[deutsch]
     * Der vierte Monat des persischen Kalenders mit 31 Tagen.
     */
    TIR,

    /**
     * The 5th month of Persian Calendar with 31 days.
     */
    /*[deutsch]
     * Der f&uuml;nfte Monat des persischen Kalenders mit 31 Tagen.
     */
    MORDAD,

    /**
     * The 6th month of Persian Calendar with 31 days.
     */
    /*[deutsch]
     * Der sechste Monat des persischen Kalenders mit 31 Tagen.
     */
    SHAHRIVAR,

    /**
     * The 7th month of Persian Calendar with 30 days.
     */
    /*[deutsch]
     * Der siebente Monat des persischen Kalenders mit 30 Tagen.
     */
    MEHR,

    /**
     * The 8th month of Persian Calendar with 30 days.
     */
    /*[deutsch]
     * Der achte Monat des persischen Kalenders mit 30 Tagen.
     */
    ABAN,

    /**
     * The 9th month of Persian Calendar with 30 days.
     */
    /*[deutsch]
     * Der neunte Monat des persischen Kalenders mit 30 Tagen.
     */
    AZAR,

    /**
     * The 10th month of Persian Calendar with 30 days.
     */
    /*[deutsch]
     * Der zehnte Monat des persischen Kalenders mit 30 Tagen.
     */
    DEY,

    /**
     * The 11th month of Persian Calendar with 30 days.
     */
    /*[deutsch]
     * Der elfte Monat des persischen Kalenders mit 30 Tagen.
     */
    BAHMAN,

    /**
     * The 12th month of Persian Calendar with 29 or 30 days (if in leap year).
     */
    /*[deutsch]
     * Der zw&ouml;lfte Monat des persischen Kalenders mit 29 oder 30 Tagen (wenn im Schaltjahr).
     */
    ESFAND;

    private static final PersianMonth[] ENUMS = PersianMonth.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   month   persian month in the range [1-12]
     * @return  persian month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   persian month in the range [1-12]
     * @return  persian month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.9/4.6
     */
    public static PersianMonth valueOf(int month) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return ENUMS[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of month in the range [1-12]
     * @since   3.9/4.6
     */
    /**
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of month in the range [1-12]
     * @since   3.9/4.6
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.9/4.6
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale and style
     * parameters. </p>
     *
     * <p>The second argument controls the width of description while the
     * third argument is only relevant for languages which make a difference
     * between stand-alone forms and embedded text forms (does not matter in
     * English). </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     * @since   3.9/4.6
     */
    /*[deutsch]
     * <p>Liefert den sprachabh&auml;ngigen Beschreibungstext. </p>
     *
     * <p>&Uuml;ber das zweite Argument kann gesteuert werden, ob eine kurze
     * oder eine lange Form des Beschreibungstexts ausgegeben werden soll. Das
     * ist besonders sinnvoll in Benutzeroberfl&auml;chen, wo zwischen der
     * Beschriftung und der detaillierten Erl&auml;uterung einer graphischen
     * Komponente unterschieden wird. Das dritte Argument ist in Sprachen von
     * Belang, die verschiedene grammatikalische Formen f&uuml;r die Ausgabe
     * als alleinstehend oder eingebettet in formatierten Text kennen. </p>
     *
     * @param   locale      language setting
     * @param   width       text width
     * @param   context     output context
     * @return  descriptive text for given locale and style (never {@code null})
     * @since   3.9/4.6
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        CalendarText names = CalendarText.getInstance("persian", locale);
        return names.getStdMonths(width, context).print(this);

    }

    @Override
    public boolean test(PersianCalendar context) {

        return (context.getMonth() == this);

    }

}
