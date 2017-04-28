/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IndianMonth.java) is part of project Time4J.
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
 * <p>The Indian national calendar defines 12 indian months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
/*[deutsch]
 * <p>Der indische Nationalkalender definiert 12 indische Monate. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
public enum IndianMonth
    implements ChronoCondition<IndianCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The 1st month of Indian national calendar.
     *
     * <p>In normal years, it has 30 days and starts on 22nd of March. But in leap years,
     * it has 31 days and starts on 21th of March. </p>
     */
    /*[deutsch]
     * Der erste Monat des indischen Nationalkalenders.
     *
     * <p>In normalen Jahren hat er 30 Tage und beginnt am 22. M&auml;rz. Aber in Schaltjahren
     * hat er 31 Tage und beginnt am 21. M&auml;rz. </p>
     */
    CHAITRA,

    /**
     * The 2nd month of Indian national calendar with 31 days (starts on 21th of April).
     */
    /*[deutsch]
     * Der zweite Monat des indischen Nationalkalenders mit 31 Tagen.
     *
     * <p>Er beginnt immer am 21. April. </p>
     */
    VAISHAKHA,

    /**
     * The 3rd month of Indian national calendar with 31 days (starts on 22nd of May).
     */
    /*[deutsch]
     * Der dritte Monat des indischen Nationalkalenders mit 31 Tagen.
     *
     * <p>Er beginnt immer am 22. Mai. </p>
     */
    JYESHTHA,

    /**
     * The 4th month of Indian national calendar with 31 days (starts on 22nd of June).
     */
    /*[deutsch]
     * Der vierte Monat des indischen Nationalkalenders mit 31 Tagen.
     *
     * <p>Er beginnt immer am 22. Juni. </p>
     */
    ASHADHA,

    /**
     * The 5th month of Indian national calendar with 31 days (starts on 23rd of July).
     */
    /*[deutsch]
     * Der f&uuml;nfte Monat des indischen Nationalkalenders mit 31 Tagen.
     *
     * <p>Er beginnt immer am 23. Juli. </p>
     */
    SHRAVANA,

    /**
     * The 6th month of Indian national calendar with 31 days (starts on 23rd of August).
     */
    /*[deutsch]
     * Der sechste Monat des indischen Nationalkalenders mit 31 Tagen.
     *
     * <p>Er beginnt immer am 23. August. </p>
     */
    BHAADRA,

    /**
     * The 7th month of Indian national calendar with 30 days (starts on 23rd of September).
     */
    /*[deutsch]
     * Der siebente Monat des indischen Nationalkalenders mit 30 Tagen.
     *
     * <p>Er beginnt immer am 23. September. </p>
     */
    ASHWIN,

    /**
     * The 8th month of Indian national calendar with 30 days (starts on 23rd of October).
     */
    /*[deutsch]
     * Der achte Monat des indischen Nationalkalenders mit 30 Tagen.
     *
     * <p>Er beginnt immer am 23. Oktober. </p>
     */
    KARTIKA,

    /**
     * The 9th month of Indian national calendar with 30 days (starts on 22nd of November).
     */
    /*[deutsch]
     * Der neunte Monat des indischen Nationalkalenders mit 30 Tagen.
     *
     * <p>Er beginnt immer am 22. November. </p>
     */
    AGRAHAYANA,

    /**
     * The 10th month of Indian national calendar with 30 days (starts on 22nd of December).
     */
    /*[deutsch]
     * Der zehnte Monat des indischen Nationalkalenders mit 30 Tagen.
     *
     * <p>Er beginnt immer am 22. Dezember. </p>
     */
    PAUSHA,

    /**
     * The 11th month of Indian national calendar with 30 days (starts on 21th of January).
     */
    /*[deutsch]
     * Der elfte Monat des indischen Nationalkalenders mit 30 Tagen.
     *
     * <p>Er beginnt immer am 21. Januar. </p>
     */
    MAGHA,

    /**
     * The 12th month of Indian national calendar with 30 days (starts on 20th of February).
     */
    /*[deutsch]
     * Der zw&ouml;lfte Monat des indischen Nationalkalenders mit 30 Tagen.
     *
     * <p>Er beginnt immer am 20. Februar. </p>
     */
    PHALGUNA;

    private static final IndianMonth[] ENUMS = IndianMonth.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value. </p>
     *
     * @param   month   Indian month in the range [1-12]
     * @return  Indian month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   Indian month in the range [1-12]
     * @return  Indian month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static IndianMonth valueOf(int month) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return ENUMS[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of month in the range [1-12]
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of month in the range [1-12]
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
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT);

    }

    /**
     * <p>Gets the description text dependent on the locale and style parameters. </p>
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
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        CalendarText names = CalendarText.getInstance("indian", locale);
        return names.getStdMonths(width, context).print(this);

    }

    @Override
    public boolean test(IndianCalendar context) {

        return (context.getMonth() == this);

    }

}
