/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DaylightSavingRule.java) is part of project Time4J.
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

import net.time4j.ClockUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Weekday;

import java.io.Serializable;


/**
 * <p>Defines a yearly pattern when and how there is a switch from winter
 to summer time and vice versa. </p>
 *
 * <p>This rule describes when such a switch happens. It also determines
 * the DST-offset. For every rule instance, a {@code ZonalTransition} can
 * be created just by indicating the appropriate year and standard offset.
 * The change from winter to summer time and back is usually expressed
 * by two rule instances. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  exclude
 * @concurrency <immutable>
 */
/*[deutsch]
 * <p>Definiert ein j&auml;hrliches Muster, wann und wie im Jahr eine Umstellung
 * von Winter- zu Sommerzeit oder zur&uuml;ck stattfindet. </p>
 *
 * <p>Dieses Muster beschreibt zum einen, wie ein solcher Umstellungszeitpunkt
 * festgelegt werden kann. Au&szlig;erdem wird ein DST-Offset festgelegt.
 * Somit kann nur mit der zus&auml;tzlichen Angabe eines Standard-Offsets pro
 * Jahr genau eine {@code ZonalTransition} erzeugt werden. Der Wechsel von
 * Winter- zu Sommerzeit und zur&uuml;ck wird im allgemeinen durch zwei
 * Instanzen dieser Klasse ausgedr&uuml;ckt. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  exclude
 * @concurrency <immutable>
 */
public class DaylightSavingRule
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 6813874976190920796L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final PlainTime timeOfDay;
    private transient final OffsetIndicator indicator;
    private transient final int savings;

    //~ Konstruktoren -----------------------------------------------------

    DaylightSavingRule(
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super();

        check(timeOfDay, indicator, savings);

        this.timeOfDay = timeOfDay.with(PlainTime.PRECISION, ClockUnit.SECONDS);
        this.indicator = indicator;
        this.savings = savings;

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
     * @throws  IllegalArgumentException if the last argument is negative or
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
     * @throws  IllegalArgumentException if the last argument is negative or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    public static DaylightSavingRule ofFixedDay(
        Month month,
        int dayOfMonth,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new FixedDayPattern(
            month, dayOfMonth, timeOfDay, indicator, savings);

    }

    /**
     * <p>Creates a rule for the last day of week in given month. </p>
     *
     * @param   month       calendar month
     * @param   dayOfWeek   last day of week
     * @param   timeOfDay   clock time when time switch happens
     * @param   indicator   offset indicator
     * @param   savings     fixed DST-offset in seconds
     * @return  new daylight saving rule
     * @throws  IllegalArgumentException if the last argument is negative
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
     * @throws  IllegalArgumentException if the last argument is negative
     * @since   2.2
     */
    public static DaylightSavingRule ofLastWeekday(
        Month month,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new LastDayOfWeekPattern(
            month, dayOfWeek, timeOfDay, indicator, savings);

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
     * @throws  IllegalArgumentException if the last argument is negative or
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
     * @throws  IllegalArgumentException if the last argument is negative or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    public static DaylightSavingRule ofWeekdayAfterDate(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new DayOfWeekInMonthPattern(
            month, dayOfMonth, dayOfWeek, timeOfDay, indicator, savings, true);

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
     * @throws  IllegalArgumentException if the last argument is negative or
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
     * @throws  IllegalArgumentException if the last argument is negative or
     *          if the day of month is not valid in context of given month
     * @since   2.2
     */
    public static DaylightSavingRule ofWeekdayBeforeDate(
        Month month,
        int dayOfMonth,
        Weekday dayOfWeek,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        return new DayOfWeekInMonthPattern(
            month, dayOfMonth, dayOfWeek, timeOfDay, indicator, savings, false);

    }

    /**
     * <p>Determines the date of time switch dependent on given year. </p>
     *
     * <p>The result must be interpreted by mean of {@link #getIndicator()}
     * in order to calculate the UTC date. </p>
     *
     * @param   year    reference year when a time switch happens
     * @return  calendar date of time switch
     * @throws  IllegalArgumentException if given year does not fit to this rule
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert das Datum der Zeitumstellung in Abh&auml;ngigkeit
     * vom angegebenen Jahr. </p>
     *
     * <p>Das Ergebnis ist mittels {@link #getIndicator()} geeignet
     * zu interpretieren, um das UTC-Datum zu bestimmen. </p>
     *
     * @param   year    Bezugsjahr, in dem eine Winter- oder
     *                  Sommerzeitumstellung stattfindet
     * @return  Datum der Umstellung
     * @throws  IllegalArgumentException wenn das Jahr nicht passt
     * @since   2.2
     */
    public PlainDate getDate(int year) {

        // must be overridden by subclasses - see java.util.concurrent.TimeUnit
        throw new AbstractMethodError();

    }

    /**
     * <p>Determines the clock time when the switch from winter time to
     * summer time happens or vice versa. </p>
     *
     * <p>The result must be interpreted by mean of {@link #getIndicator()}
     * in order to calculate the UTC time. </p>
     *
     * @return  clock time of time switch in second precision
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert die Uhrzeit der Zeitumstellung. </p>
     *
     * <p>Das Ergebnis ist mittels {@link #getIndicator()} geeignet
     * zu interpretieren, um die UTC-Zeit zu bestimmen. </p>
     *
     * @return  Uhrzeit der Umstellung in second precision
     * @since   2.2
     */
    public PlainTime getTimeOfDay() {

        return this.timeOfDay;

    }

    /**
     * <p>Yields the offset indicator which must be consulted when interpreting
     * the date and time of time switch in terms of UTC. </p>
     *
     * @return  OffsetIndicator
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert den Offset-Indikator, der zur Interpretation des Datums
     * und der Uhrzeit der Zeitumstellung im UTC-Kontext dient. </p>
     *
     * @return  OffsetIndicator
     * @since   2.2
     */
    public OffsetIndicator getIndicator() {

        return this.indicator;

    }

    /**
     * <p>Yields the dayight saving amount after the time switch
     * in seconds. </p>
     *
     * @return  DST-Offset in seconds (without standard offset)
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert den DST-Offset nach der Umstellung in Sekunden. </p>
     *
     * @return  reiner DST-Offset in Sekunden (ohne Standard-Offset)
     * @since   2.2
     */
    public int getSavings() {

        return this.savings;

    }

    // benutzt in der Serialisierung
    int getType() {

        return 0; // default value for unknown type

    }

    // für Subklassen
    boolean isEqual(DaylightSavingRule rule) {

        return (
            this.timeOfDay.equals(rule.timeOfDay)
            && (this.indicator == rule.indicator)
            && (this.savings == rule.savings));

    }

    private static void check(
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {

        if (timeOfDay == null) {
            throw new NullPointerException("Missing time of day.");
        } else if (indicator == null) {
            throw new NullPointerException("Missing offset indicator.");
        } else if (savings < 0) {
            throw new IllegalArgumentException(
                "Negative daylight saving offset: " + savings);
        }

    }

}
