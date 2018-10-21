/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianTimezoneRule.java) is part of project Time4J.
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

package net.time4j.tz.model;

import net.time4j.CalendarUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.base.GregorianDate;
import net.time4j.base.GregorianMath;
import net.time4j.format.CalendarText;
import net.time4j.format.CalendarType;

import java.io.Serializable;


/**
 * <p>Represents a standard daylight saving rule following the gregorian
 * calendar as used in IANA-TZDB. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Standardregel f&uuml;r Zeitumstellungen im
 * gregorianischen Kalender wie in der IANA-Zeitzonendatenbank benutzt. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
@CalendarType("iso8601")
public class GregorianTimezoneRule
    extends DaylightSavingRule
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte month;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Constructor for subclasses only. </p>
     *
     * @param   month       gregorian month
     * @param   timeOfDay   time of day in seconds after midnight
     * @param   indicator   offset indicator
     * @param   savings     daylight saving amount
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruktor nur f&uuml;r Subklassen. </p>
     *
     * @param   month       gregorian month
     * @param   timeOfDay   time of day in seconds after midnight
     * @param   indicator   offset indicator
     * @param   savings     daylight saving amount
     * @since   5.0
     */
    protected GregorianTimezoneRule(
        Month month,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(timeOfDay, indicator, savings);

        this.month = (byte) month.getValue();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a rule for a fixed day in given month. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  day of month (1 - 31)
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r einen festen Tag im angegebenen
     * Monat. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  day of month (1 - 31)
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    public static GregorianTimezoneRule ofFixedDay(
        Month month,
        int dayOfMonth,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return ofFixedDay(month, dayOfMonth, timeOfDay.getInt(PlainTime.SECOND_OF_DAY), indicator, savings);

    }

    /**
     * <p>Creates a rule for a fixed day in given month. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  day of month (1 - 31)
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r einen festen Tag im angegebenen
     * Monat. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  day of month (1 - 31)
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   5.0
     */
    public static GregorianTimezoneRule ofFixedDay(
        Month month,
        int dayOfMonth,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new FixedDayPattern(month, dayOfMonth, timeOfDay, indicator, savings);

    }

    /**
     * <p>Creates a rule for the last day of week in given month. </p>
     *
     * @param   month       calendar month
     * @param   dayOfWeek   last day of week
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range
     * @since   2.2
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r den letzten Wochentag im angegebenen
     * Monat. </p>
     *
     * @param   month       calendar month
     * @param   dayOfWeek   last day of week
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range
     * @since   2.2
     */
    public static GregorianTimezoneRule ofLastWeekday(
        Month month,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return ofLastWeekday(month, dayOfWeek, timeOfDay.getInt(PlainTime.SECOND_OF_DAY), indicator, savings);

    }

    /**
     * <p>Creates a rule for the last day of week in given month. </p>
     *
     * @param   month       calendar month
     * @param   dayOfWeek   last day of week
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r den letzten Wochentag im angegebenen
     * Monat. </p>
     *
     * @param   month       calendar month
     * @param   dayOfWeek   last day of week
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range
     * @since   5.0
     */
    public static GregorianTimezoneRule ofLastWeekday(
        Month month,
        Weekday dayOfWeek,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new LastWeekdayPattern(month, dayOfWeek, timeOfDay, indicator, savings);

    }

    /**
     * <p>Creates a rule for a day of week after the given reference date. </p>
     *
     * <p>Example =&gt; You have to set for the second Sunday in April:
     * {@code month=APRIL, dayOfMonth=8, dayOfWeek=SUNDAY}. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r einen Wochentag nach einem
     * festen Monatstag im angegebenen Monat. </p>
     *
     * <p>Beispiel =&gt; F&uuml;r den zweiten Sonntag im April sei zu setzen:
     * {@code month=APRIL, dayOfMonth=8, dayOfWeek=SUNDAY}. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    public static GregorianTimezoneRule ofWeekdayAfterDate(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return ofWeekdayAfterDate(
            month, dayOfMonth, dayOfWeek, timeOfDay.getInt(PlainTime.SECOND_OF_DAY), indicator, savings);

    }

    /**
     * <p>Creates a rule for a day of week after the given reference date. </p>
     *
     * <p>Example =&gt; You have to set for the second Sunday in April:
     * {@code month=APRIL, dayOfMonth=8, dayOfWeek=SUNDAY}. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r einen Wochentag nach einem
     * festen Monatstag im angegebenen Monat. </p>
     *
     * <p>Beispiel =&gt; F&uuml;r den zweiten Sonntag im April sei zu setzen:
     * {@code month=APRIL, dayOfMonth=8, dayOfWeek=SUNDAY}. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   5.0
     */
    public static GregorianTimezoneRule ofWeekdayAfterDate(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new DayOfWeekInMonthPattern(month, dayOfMonth, dayOfWeek, timeOfDay, indicator, savings, true);

    }

    /**
     * <p>Creates a rule for a day of week before the given reference date. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r einen Wochentag vor einem
     * festen Monatstag im angegebenen Monat. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    public static GregorianTimezoneRule ofWeekdayBeforeDate(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return ofWeekdayBeforeDate(
            month, dayOfMonth, dayOfWeek, timeOfDay.getInt(PlainTime.SECOND_OF_DAY), indicator, savings);

    }

    /**
     * <p>Creates a rule for a day of week before the given reference date. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   5.0
     */
    /*[deutsch]
     * <p>Konstruiert ein Muster f&uuml;r einen Wochentag vor einem
     * festen Monatstag im angegebenen Monat. </p>
     *
     * @param   month       calendar month
     * @param   dayOfMonth  reference day of month (1 - 31)
     * @param   dayOfWeek   day of week when time switch happens
     * @param   timeOfDay   clock time in seconds after midnight when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is out of range or
     *          if the day of month is not valid in context of given month
     * @since   5.0
     */
    public static GregorianTimezoneRule ofWeekdayBeforeDate(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        int timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new DayOfWeekInMonthPattern(month, dayOfMonth, dayOfWeek, timeOfDay, indicator, savings, false);

    }

    @Override
    public final PlainDate getDate(int year) {

        return this.getDate0(year).plus(this.getDayOverflow(), CalendarUnit.DAYS);

    }

    /**
     * <p>Yields the gregorian month of time switch. </p>
     *
     * @return  month
     * @since   3.1
     */
    /*[deutsch]
     * <p>Liefert den gregorianischen Monat der Zeitumstellung. </p>
     *
     * @return  month
     * @since   3.1
     */
    public Month getMonth() {

        return Month.valueOf(this.month);

    }

    // Must be overridden by subclasses.
    protected PlainDate getDate0(int year) {

        throw new AbstractMethodError("Implemented by subclasses.");

    }

    @Override
    protected int toCalendarYear(long mjd) {

        return GregorianMath.readYear(GregorianMath.toPackedDate(mjd));

    }

    @Override
    protected int toCalendarYear(GregorianDate date) {

        return date.getYear();

    }

    @Override
    protected String getCalendarType() {

        return CalendarText.ISO_CALENDAR_TYPE;

    }

    // für Subklassen
    protected boolean isEqual(GregorianTimezoneRule rule) {

        return (
            this.getTimeOfDay().equals(rule.getTimeOfDay())
            && (this.getDayOverflow() == rule.getDayOverflow())
            && (this.getIndicator() == rule.getIndicator())
            && (this.getSavings() == rule.getSavings())
            && (this.month == rule.month));

    }

    /**
     * <p>Benutzt unter anderem in der Serialisierung. </p>
     *
     * @return  byte
     */
    byte getMonthValue() {

        return this.month;

    }

}
