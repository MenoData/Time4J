/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Month.java) is part of project Time4J.
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
import net.time4j.base.GregorianMath;
import net.time4j.engine.ChronoCondition;
import net.time4j.format.CalendarText;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;

import static net.time4j.Quarter.Q1;
import static net.time4j.Quarter.Q2;
import static net.time4j.Quarter.Q3;
import static net.time4j.Quarter.Q4;
import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Enumeration of months in ISO-8601-calendar. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Aufz&auml;hlung der Monate in ISO-Systemen. </p>
 *
 * @author  Meno Hochschild
 */
public enum Month
    implements ChronoCondition<GregorianDate> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** January with the numerical ISO-value {@code 1}. */
    /*[deutsch] Januar mit dem numerischen ISO-Wert {@code 1}. */
    JANUARY,

    /** February with the numerical ISO-value {@code 2}. */
    /*[deutsch] Februar mit dem numerischen ISO-Wert {@code 2}. */
    FEBRUARY,

    /** March with the numerical ISO-value {@code 3}. */
    /*[deutsch] M&auml;rz mit dem numerischen ISO-Wert {@code 3}. */
    MARCH,

    /** April with the numerical ISO-value {@code 4}. */
    /*[deutsch] April mit dem numerischen ISO-Wert {@code 4}. */
    APRIL,

    /** May with the numerical ISO-value {@code 5}. */
    /*[deutsch] Mai mit dem numerischen ISO-Wert {@code 5}. */
    MAY,

    /** June with the numerical ISO-value {@code 6}. */
    /*[deutsch] Juni mit dem numerischen ISO-Wert {@code 6}. */
    JUNE,

    /** July with the numerical ISO-value {@code 7}. */
    /*[deutsch] Juli mit dem numerischen ISO-Wert {@code 7}. */
    JULY,

    /** August with the numerical ISO-value {@code 8}. */
    /*[deutsch] August mit dem numerischen ISO-Wert {@code 8}. */
    AUGUST,

    /** September with the numerical ISO-value {@code 9}. */
    /*[deutsch] September mit dem numerischen ISO-Wert {@code 9}. */
    SEPTEMBER,

    /** October with the numerical ISO-value {@code 10}. */
    /*[deutsch] Oktober mit dem numerischen ISO-Wert {@code 10}. */
    OCTOBER,

    /** November with the numerical ISO-value {@code 11}. */
    /*[deutsch] November mit dem numerischen ISO-Wert {@code 11}. */
    NOVEMBER,

    /** December with the numerical ISO-value {@code 12}. */
    /*[deutsch] Dezember mit dem numerischen ISO-Wert {@code 12}. */
    DECEMBER;

    private static final Month[] ENUMS = Month.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets the enum-constant which corresponds to the given numerical
     * value. </p>
     *
     * @param   month   gregorian month in the range [1-12]
     * @return  month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    /*[deutsch]
     * <p>Liefert die zum kalendarischen Integer-Wert passende
     * Enum-Konstante. </p>
     *
     * @param   month   gregorian month in the range [1-12]
     * @return  month of year as enum
     * @throws  IllegalArgumentException if given argument is out of range
     */
    public static Month valueOf(int month) {

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
     * <p>Calculates the corresponding quarter of year. </p>
     *
     * @return  quarter of year
     */
    /*[deutsch]
     * <p>Ermittelt das zugeh&ouml;rige Quartal. </p>
     *
     * @return  quarter of year
     */
    public Quarter getQuarterOfYear() {

        switch (this) {
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                return Q1;
            case APRIL:
            case MAY:
            case JUNE:
                return Q2;
            case JULY:
            case AUGUST:
            case SEPTEMBER:
                return Q3;
            default:
                return Q4;
        }

    }

    /**
     * <p>Gets the first month of given quarter of year. </p>
     *
     * @param   quarterOfYear   quarter of year (Q1-Q4)
     * @return  first month in given quarteryear
     */
    /*[deutsch]
     * <p>Liefert den ersten Monat des angegebenen Quartals. </p>
     *
     * @param   quarterOfYear   quarter of year (Q1-Q4)
     * @return  first month in given quarteryear
     */
    public static Month atStartOfQuarterYear(Quarter quarterOfYear) {

        switch (quarterOfYear) {
            case Q1:
                return Month.JANUARY;
            case Q2:
                return Month.APRIL;
            case Q3:
                return Month.JULY;
            default:
                return Month.OCTOBER;
        }

    }

    /**
     * <p>Gets the last month of given quarter of year. </p>
     *
     * @param   quarterOfYear   quarter of year (Q1-Q4)
     * @return  last month in given quarteryear
     */
    /*[deutsch]
     * <p>Liefert den letzten Monat des angegebenen Quartals. </p>
     *
     * @param   quarterOfYear   quarter of year (Q1-Q4)
     * @return  last month in given quarteryear
     */
    public static Month atEndOfQuarterYear(Quarter quarterOfYear) {

        switch (quarterOfYear) {
            case Q1:
                return Month.MARCH;
            case Q2:
                return Month.JUNE;
            case Q3:
                return Month.SEPTEMBER;
            default:
                return Month.DECEMBER;
        }

    }

    /**
     * <p>Calculates the maximum length of this month in days dependent on
     * given year (taking into account leap years). </p>
     *
     * @param   year    proleptic iso year
     * @return  length of month in days
     */
    /*[deutsch]
     * <p>Ermittelt die maximale L&auml;nge des Monats in Tagen abh&auml;ngig
     * vom angegebenen Jahr (mit Beachtung der Schaltjahre). </p>
     *
     * @param   year    proleptic iso year
     * @return  length of month in days
     */
    public int getLength(int year) {

        return GregorianMath.getLengthOfMonth(year, this.getValue());

    }

    /**
     * <p>Rolls to the next month. </p>
     *
     * <p>The result is January if applied on December. </p>
     *
     * @return  next month (rolling at December)
     */
    /*[deutsch]
     * <p>Ermittelt den n&auml;chsten Monat. </p>
     *
     * <p>Auf den Dezember angewandt ist das Ergebnis der Januar. </p>
     *
     * @return  next month (rolling at december)
     */
    public Month next() {

        return this.roll(1);

    }

    /**
     * <p>Rolls to the previous month. </p>
     *
     * <p>The result is December if applied on January. </p>
     *
     * @return  previous month (rolling at January)
     */
    /*[deutsch]
     * <p>Ermittelt den vorherigen Monat. </p>
     *
     * <p>Auf den Januar angewandt ist das Ergebnis der Dezember. </p>
     *
     * @return  previous month (rolling at january)
     */
    public Month previous() {

        return this.roll(-1);

    }

    /**
     * <p>Rolls this month by given amount of months. </p>
     *
     * @param   months      count of months (maybe negative)
     * @return  result of rolling operation
     */
    /*[deutsch]
     * <p>Rollt um die angegebene Anzahl von Monaten vor oder zur&uuml;ck. </p>
     *
     * @param   months      count of months (maybe negative)
     * @return  result of rolling operation
     */
    public Month roll(int months) {

        return Month.valueOf((this.ordinal() + (months % 12 + 12)) % 12 + 1);

    }

    /**
     * <p>Equivalent to the expression
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}.
     * </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    /*[deutsch]
     * <p>Entspricht dem Ausdruck
     * {@code getDisplayName(locale, TextWidth.WIDE, OutputContext.FORMAT)}.
     * </p>
     *
     * @param   locale      language setting
     * @return  descriptive text (long form, never {@code null})
     * @see     #getDisplayName(Locale, TextWidth, OutputContext)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(
            locale, TextWidth.WIDE, OutputContext.FORMAT);

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

        CalendarText names =
            CalendarText.getInstance(ISO_CALENDAR_TYPE, locale);
        return names.getStdMonths(width, context).print(this);

    }

    @Override
    public boolean test(GregorianDate context) {

        return (context.getMonth() == this.getValue());

    }

}
