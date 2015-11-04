/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (EthiopianMonth.java) is part of project Time4J.
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
 * <p>The Ethiopian calendar defines 13 months like the Coptic calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
/*[deutsch]
 * <p>Der &auml;thiopische Kalender definiert wie der koptische Kalender 13 Monate. </p>
 *
 * @author  Meno Hochschild
 * @since   3.11/4.8
 */
public enum EthiopianMonth
    implements ChronoCondition<EthiopianCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The 1st month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der erste Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    MESKEREM,

    /**
     * The 2nd month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der zweite Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    TEKEMT,

    /**
     * The 3rd month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der dritte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    HEDAR,

    /**
     * The 4th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der vierte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    TAHSAS,

    /**
     * The 5th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der f&uuml;nfte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    TER,

    /**
     * The 6th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der sechste Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    YEKATIT,

    /**
     * The 7th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der siebente Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    MEGABIT,

    /**
     * The 8th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der achte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    MIAZIA,

    /**
     * The 9th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der neunte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    GENBOT,

    /**
     * The 10th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der zehnte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    SENE,

    /**
     * The 11th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der elfte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    HAMLE,

    /**
     * The 12th month of Ethiopian calendar with 30 days.
     */
    /*[deutsch]
     * Der zw&ouml;lfte Monat des &auml;thiopischen Kalenders mit 30 Tagen.
     */
    NEHASSE,

    /**
     * The 13th month of Ethiopian calendar with 5 or 6 days (if in leap year).
     */
    /*[deutsch]
     * Der dreizehnte Monat des &auml;thiopischen Kalenders mit 5 oder 6 Tagen (wenn im Schaltjahr).
     */
    PAGUMEN;

    private static final EthiopianMonth[] ENUMS = EthiopianMonth.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   month   Ethiopian month in the range [1-13]
     * @return  Ethiopian month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   Ethiopian month in the range [1-13]
     * @return  Ethiopian month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.11/4.8
     */
    public static EthiopianMonth valueOf(int month) {

        if ((month < 1) || (month > 13)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return ENUMS[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of month in the range [1-13]
     * @since   3.11/4.8
     */
    /**
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of month in the range [1-13]
     * @since   3.11/4.8
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
     * @since   3.11/4.8
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.11/4.8
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
     * @since   3.11/4.8
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
     * @since   3.11/4.8
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        CalendarText names = CalendarText.getInstance("ethiopic", locale);
        return names.getStdMonths(width, context).print(this);

    }

    @Override
    public boolean test(EthiopianCalendar context) {

        return (context.getMonth() == this);

    }

}
