/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriMonth.java) is part of project Time4J.
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
import net.time4j.engine.ChronoOperator;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Hijri calendar defines 12 islamic months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Der islamische Kalender definiert 12 islamische Monate. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public enum HijriMonth
    implements ChronoCondition<HijriCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    MUHARRAM,

    SAFAR,

    RABI_I,

    RABI_II,

    JUMADA_I,

    JUMADA_II,

    RAJAB,

    SHABAN,

    RAMADAN,

    SHAWWAL,

    DHU_AL_QIDAH,

    DHU_AL_HIJJAH;

    private static final HijriMonth[] ENUMS = HijriMonth.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical
     * value. </p>
     *
     * @param   month   islamic month in the range [1-12]
     * @return  islamic month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende
     * Enum-Konstante. </p>
     *
     * @param   month   islamic month in the range [1-12]
     * @return  islamic month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @since   3.5/4.3
     */
    public static HijriMonth valueOf(int month) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Out of range: " + month);
        }

        return ENUMS[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value. </p>
     *
     * @return  number of month in the range [1-12]
     * @since   3.5/4.3
     */
    /**
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of month in the range [1-12]
     * @since   3.5/4.3
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
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}. </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     * @since   3.5/4.3
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
     * @since   3.5/4.3
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
     * @since   3.5/4.3
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context
    ) {

        CalendarText names = CalendarText.getInstance("islamic", locale);
        return names.getStdMonths(width, context).print(this);

    }

    @Override
    public boolean test(HijriCalendar context) {

        return (context.getMonth() == this);

    }

    //~ Innere Klassen ----------------------------------------------------

    static class Operator
        implements ChronoOperator<HijriCalendar> {

        //~ Instanzvariablen ----------------------------------------------

        private final int steps;

        //~ Konstruktoren -------------------------------------------------

        Operator(int steps) {
            super();

            this.steps = steps;

        }

        //~ Methoden ------------------------------------------------------

        public HijriCalendar apply(HijriCalendar entity) {

            int ym = entity.getYear() * 12 + entity.getMonth().getValue() - 1;
            ym += this.steps;
            int hyear = ym / 12;
            int hmonth = ym % 12 + 1;
            int dmax = entity.getCalendarSystem().getLengthOfMonth(HijriEra.ANNO_HEGIRAE, hyear, hmonth);
            int hdom = Math.min(entity.getDayOfMonth(), dmax);

            return HijriCalendar.of(entity.getVariant(), hyear, hmonth, hdom);

        }

    }

}
