/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HebrewMonth.java) is part of project Time4J.
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

import net.time4j.engine.AttributeKey;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;


/**
 * <p>The Hebrew calendar defines 13 Hebrew months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.37/4.32
 */
/*[deutsch]
 * <p>Der hebr&auml;ische Kalender definiert 13 hebr&auml;ische Monate. </p>
 *
 * @author  Meno Hochschild
 * @since   3.37/4.32
 */
public enum HebrewMonth
    implements ChronoCondition<HebrewCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * The 1st month of Hebrew calendar with 30 days.
     */
    /*[deutsch]
     * Der erste Monat des hebr&auml;ischen Kalenders mit 30 Tagen.
     */
    TISHRI,

    /**
     * The 2nd month of Hebrew calendar with 29 or 30 days.
     */
    /*[deutsch]
     * Der zweite Monat des hebr&auml;ischen Kalenders mit 29 oder 30 Tagen.
     */
    HESHVAN,

    /**
     * The 3rd month of Hebrew calendar with 29 or 30 days.
     */
    /*[deutsch]
     * Der dritte Monat des hebr&auml;ischen Kalenders mit 29 oder 30 Tagen.
     */
    KISLEV,

    /**
     * The 4th month of Hebrew calendar with 29 days.
     */
    /*[deutsch]
     * Der vierte Monat des hebr&auml;ischen Kalenders mit 29 Tagen.
     */
    TEVET,

    /**
     * The 5th month of Hebrew calendar with 30 days.
     */
    /*[deutsch]
     * Der f&uuml;nfte Monat des hebr&auml;ischen Kalenders mit 30 Tagen.
     */
    SHEVAT,

    /**
     * The 6th month of Hebrew calendar with 30 days (leap month).
     *
     * <p>Note: This month only occurs in leap years. </p>
     */
    /*[deutsch]
     * Der sechste Monat des hebr&auml;ischen Kalenders mit 30 Tagen (Schaltmonat).
     *
     * <p>Hinweis: Dieser Monat kommt nur in Schaltjahren vor. </p>
     */
    ADAR_I,

    /**
     * The 7th month of Hebrew calendar with 29 days.
     *
     * <p>Note: This month is just called &quot;ADAR&quot; and is effectively the sixth month
     * if it is not in a leap year. </p>
     */
    /*[deutsch]
     * Der siebente Monat des hebr&auml;ischen Kalenders mit 29 Tagen.
     *
     * <p>Hinweis: Dieser Monat wird einfach &quot;ADAR&quot; genannt und ist effektiv der sechste
     * Monat, wenn er nicht in einem Schaltjahr liegt. </p>
     */
    ADAR_II,

    /**
     * The 8th month of Hebrew calendar with 30 days (7th month in non-leap-years).
     */
    /*[deutsch]
     * Der achte Monat des hebr&auml;ischen Kalenders mit 30 Tagen (siebenter Monat in Normaljahren).
     */
    NISAN,

    /**
     * The 9th month of Hebrew calendar with 29 days (8th month in non-leap-years).
     */
    /*[deutsch]
     * Der neunte Monat des hebr&auml;ischen Kalenders mit 29 Tagen (achter Monat in Normaljahren).
     */
    IYAR,

    /**
     * The 10th month of Hebrew calendar with 30 days (9th month in non-leap-years).
     */
    /*[deutsch]
     * Der zehnte Monat des hebr&auml;ischen Kalenders mit 30 Tagen (neunter Monat in Normaljahren).
     */
    SIVAN,

    /**
     * The 11th month of Hebrew calendar with 29 days (10th month in non-leap-years).
     */
    /*[deutsch]
     * Der elfte Monat des hebr&auml;ischen Kalenders mit 29 Tagen (zehnter Monat in Normaljahren).
     */
    TAMUZ,

    /**
     * The 12th month of Hebrew calendar with 30 days (11th month in non-leap-years).
     */
    /*[deutsch]
     * Der zw&ouml;lfte Monat des hebr&auml;ischen Kalenders mit 30 Tagen (elfter Monat in Normaljahren).
     */
    AV,

    /**
     * The 13th month of Hebrew calendar with 29 days (12th month in non-leap-years).
     */
    /*[deutsch]
     * Der dreizehnte Monat des hebr&auml;ischen Kalenders mit 29 Tagen (zw&ouml;lfter Monat in Normaljahren).
     */
    ELUL;

    private static final HebrewMonth[] ENUMS = HebrewMonth.values(); // Cache
    private static final AttributeKey<Order> ATTRIBUTE = Attributes.createKey("HEBREW_MONTH_ORDER", Order.class);

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value (in standard civil order). </p>
     *
     * @param   month       civil number of month in the range [1-13]
     * @param   leapYear    called in a leap year context?
     * @return  hebrew month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @see     net.time4j.calendar.HebrewMonth.Order#CIVIL
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante in der Standardz&auml;hlweise. </p>
     *
     * @param   month       civil number of month in the range [1-13]
     * @param   leapYear    called in a leap year context?
     * @return  hebrew month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @see     net.time4j.calendar.HebrewMonth.Order#CIVIL
     */
    public static HebrewMonth valueOfCivil(
        int month,
        boolean leapYear
    ) {

        if ((month < 1) || (month > 13) || (!leapYear && (month == 13))) {
            throw new IllegalArgumentException("Hebrew month out of range: " + month);
        }

        if (!leapYear && (month >= 6)) {
            return ENUMS[month];
        } else {
            return ENUMS[month - 1];
        }

    }

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value (in biblical order). </p>
     *
     * @param   month       biblical number of month in the range [1-13]
     * @param   leapYear    called in a leap year context?
     * @return  hebrew month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @see     net.time4j.calendar.HebrewMonth.Order#BIBLICAL
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante in der biblischen Ordnung. </p>
     *
     * @param   month       biblical number of month in the range [1-13]
     * @param   leapYear    called in a leap year context?
     * @return  hebrew month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @see     net.time4j.calendar.HebrewMonth.Order#BIBLICAL
     */
    public static HebrewMonth valueOfBiblical(
        int month,
        boolean leapYear
    ) {

        if ((month < 1) || (month > 13) || (!leapYear && (month == 13))) {
            throw new IllegalArgumentException("Hebrew month out of range: " + month);
        }

        int m = month + 7;

        if (m > 13) {
            m -= 13;
        }

        if (!leapYear && (month == 12)) {
            return ADAR_II;
        }

        return ENUMS[m - 1];

    }

    /**
     * <p>Gets the corresponding numerical value in usual civil order. </p>
     *
     * <p>The first month is TISHRI. All months starting with ADAR-II or later
     * decrement the numerical value by 1 if this method is called for a normal year. </p>
     *
     * @param   leapYear    called in a leap year context?
     * @return  civil number of month in the range [1-13]
     * @see     net.time4j.calendar.HebrewMonth.Order#CIVIL
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert
     * in der zivilen Standardz&auml;hlweise. </p>
     *
     * <p>Der erste Monat ist TISHRI. Alle Monate ab ADAR-II vermindern ihren numerischen Wert um 1,
     * wenn ein Normaljahr vorliegt. </p>
     *
     * @param   leapYear    called in a leap year context?
     * @return  civil number of month in the range [1-13]
     * @see     net.time4j.calendar.HebrewMonth.Order#CIVIL
     */
    public int getCivilValue(boolean leapYear) {

        int m = (this.ordinal() + 1);

        if (!leapYear && (m >= 7)) {
            m--;
        }

        return m;

    }

    /**
     * <p>Gets the corresponding numerical value in biblical order. </p>
     *
     * <p>The first month in biblical order (Leviticus 23:5) is NISAN.
     * The last month ADAR-II will have the number 12 in normal years and 13 in leap years. </p>
     *
     * @param   leapYear    called in a leap year context?
     * @return  biblical number of month in the range [1-13]
     * @see     net.time4j.calendar.HebrewMonth.Order#BIBLICAL
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert entsprechend
     * der in der Bibel angegebenen Ordnung. </p>
     *
     * <p>Der erste Monat in der Bibelkonvention (Leviticus 23:5) ist NISAN.
     * Der letzte Monat ADAR-II hat die Nummer 12 in Normaljahren und 13 in Schaltjahren. </p>
     *
     * @param   leapYear    called in a leap year context?
     * @return  biblical number of month in the range [1-13]
     * @see     net.time4j.calendar.HebrewMonth.Order#BIBLICAL
     */
    public int getBiblicalValue(boolean leapYear) {

        int m = (this.ordinal() + 7);

        if (m > 13) {
            m -= 13;
        }

        if (!leapYear && (m == 13)) {
            m = 12;
        }

        return m;

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT, leapYear)}. </p>
     *
     * @param   locale      language setting
     * @param   leapYear    called in a leap year context?
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext, boolean)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT, leapYear)}. </p>
     *
     * @param   locale      language setting
     * @param   leapYear    called in a leap year context?
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext, boolean)
     */
    public String getDisplayName(
        Locale locale,
        boolean leapYear
    ) {

        return this.getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT, leapYear);

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
     * @param   leapYear    called in a leap year context?
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
     * @param   leapYear    called in a leap year context?
     * @return  descriptive text for given locale and style (never {@code null})
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width,
        OutputContext context,
        boolean leapYear
    ) {

        CalendarText names = CalendarText.getInstance("hebrew", locale);

        if (leapYear && (this == ADAR_II)) {
            return names.getLeapMonths(width, context).print(this);
        } else {
            return names.getStdMonths(width, context).print(this);
        }

    }

    @Override
    public boolean test(HebrewCalendar context) {

        return (context.getMonth() == this);

    }

    /**
     * <p>Format attribute which expects months to be numerical with a specific order. </p>
     *
     * @return  format attribute key
     */
    /*[deutsch]
     * <p>Formatattribut, das Monate in einem numerischen Format mit einer spezifischen Z&auml;hlweise
     * erwartet oder festlegt. </p>
     *
     * @return  format attribute key
     */
    public static AttributeKey<Order> order() {

        return ATTRIBUTE;

    }

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical value (in enum-order). </p>
     *
     * @param   month   simple enum month number in the range [1-13]
     * @return  hebrew month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @see     #getValue()
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende Enum-Konstante. </p>
     *
     * @param   month   simple enum month number in the range [1-13]
     * @return  hebrew month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     * @see     #getValue()
     */
    static HebrewMonth valueOf(int month) {

        if ((month < 1) || (month > 13)) {
            throw new IllegalArgumentException("Hebrew month out of range: " + month);
        }

        return ENUMS[month - 1];

    }

    /**
     * <p>Gets the corresponding numerical value in simple enum order. </p>
     *
     * @return  simple enum number of month in the range [1-13]
     * @see     #getCivilValue(boolean)
     * @see     #getBiblicalValue(boolean)
     */
    /*[deutsch]
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert
     * in vereinfachter Enum-Reihenfolge. </p>
     *
     * @return  simple enum number of month in the range [1-13]
     * @see     #getCivilValue(boolean)
     * @see     #getBiblicalValue(boolean)
     */
    int getValue() {

        return (this.ordinal() + 1);

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Determines which order should be applied on Hebrew months. </p>
     *
     * @author  Meno Hochschild
     */
    /*[deutsch]
     * <p>Legt fest, welche Reihenfolge auf die hebr&auml;ischen Monate angewandt werden soll. </p>
     *
     * @author  Meno Hochschild
     */
    public static enum Order {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * <p>The first month of this standard numbering is TISHRI. </p>
         *
         * <p>If no leap month exists then the numbers of following months will be reduced by one. </p>
         */
        /*[deutsch]
         * <p>Der erste Monat dieser Standardz&auml;weise ist TISHRI. </p>
         *
         * <p>Falls kein Schaltmonat existiert, werden die Nummern der Folgemonate um 1 reduziert. </p>
         */
        CIVIL,

        /**
         * <p>The first month of the biblical numbering is NISAN. </p>
         *
         * <p>If no leap month exists then the numbers of following months will be reduced by one. </p>
         *
         * @since   3.38/4.33
         */
        /*[deutsch]
         * <p>Der erste Monat in der Bibelz&auml;hlweise ist NISAN. </p>
         *
         * <p>Falls kein Schaltmonat existiert, werden die Nummern der Folgemonate um 1 reduziert. </p>
         *
         * @since   3.38/4.33
         */
        BIBLICAL,

        /**
         * <p>This technical numbering is based on the ordinal number of enum, incremented by one. </p>
         */
        /*[deutsch]
         * <p>Diese technische Z&auml;hlweise beruht auf der um 1 erh&ouml;hten Ordinalnummer des Enums. </p>
         */
        ENUM,

        /**
         * <p>The first month of the biblical numbering is NISAN. </p>
         *
         * <p>If no leap month exists then the numbers of following months will be reduced by one. </p>
         *
         * @deprecated  Use {@link #BIBLICAL}
         */
        /*[deutsch]
         * <p>Der erste Monat in der Bibelz&auml;hlweise ist NISAN. </p>
         *
         * <p>Falls kein Schaltmonat existiert, werden die Nummern der Folgemonate um 1 reduziert. </p>
         *
         * @deprecated  Use {@link #BIBLICAL}
         */
        @Deprecated
        BIBILICAL

    }

}
