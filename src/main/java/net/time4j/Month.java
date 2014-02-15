/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Month.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
 * <p>Aufz&auml;hlung der Monate in ISO-Systemen. </p>
 *
 * @author  Meno Hochschild
 */
public enum Month
    implements ChronoCondition<GregorianDate> { // TODO: ChronoOperator

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Januar mit dem numerischen ISO-Wert {@code 1}. */
    JANUARY,

    /** Februar mit dem numerischen ISO-Wert {@code 2}. */
    FEBRUARY,

    /** M&auml;rz mit dem numerischen ISO-Wert {@code 3}. */
    MARCH,

    /** April mit dem numerischen ISO-Wert {@code 4}. */
    APRIL,

    /** Mai mit dem numerischen ISO-Wert {@code 5}. */
    MAY,

    /** Juni mit dem numerischen ISO-Wert {@code 6}. */
    JUNE,

    /** Juli mit dem numerischen ISO-Wert {@code 7}. */
    JULY,

    /** August mit dem numerischen ISO-Wert {@code 8}. */
    AUGUST,

    /** September mit dem numerischen ISO-Wert {@code 9}. */
    SEPTEMBER,

    /** Oktober mit dem numerischen ISO-Wert {@code 10}. */
    OCTOBER,

    /** November mit dem numerischen ISO-Wert {@code 11}. */
    NOVEMBER,

    /** Dezember mit dem numerischen ISO-Wert {@code 12}. */
    DECEMBER;

    private static final Month[] ENUMS = Month.values(); // Cache

    //~ Methoden ----------------------------------------------------------

    /**
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
     * <p>Liefert den korrespondierenden kalendarischen Integer-Wert. </p>
     *
     * @return  number of month in the range [1-12]
     */
    public int getValue() {

        return (this.ordinal() + 1);

    }

    /**
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
     * <p>Liefert den ersten Monat des angegebenen Quartals. </p>
     *
     * @return  first month in given quarteryear
     */
    public static Month atStartOfQuarter(Quarter quarterOfYear) {

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
     * <p>Liefert den letzten Monat des angegebenen Quartals. </p>
     *
     * @return  last month in given quarteryear
     */
    public static Month atEndOfQuarter(Quarter quarterOfYear) {

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
     * <p>Rollt um die angegebene Anzahl von Monaten vor oder zur&uuml;ck. </p>
     *
     * @param   months      count of months (maybe negative)
     * @return  result of rolling operation
     */
    public Month roll(int months) {

        return Month.valueOf((this.ordinal() + (months % 12 + 12)) % 12 + 1);

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
        return names.getMonths(tw, OutputContext.FORMAT, false).print(this);

    }

    @Override
    public boolean test(GregorianDate context) {

        return (context.getMonth() == this.getValue());

    }

}
