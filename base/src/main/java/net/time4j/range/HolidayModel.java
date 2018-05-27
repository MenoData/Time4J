/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HolidayModel.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.IsoDateUnit;
import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoOperator;

import java.util.Locale;
import java.util.function.Predicate;


/**
 * <p>Represents a rule how to find a non-business day. </p>
 *
 * @author  Meno Hochschild
 * @see     DateInterval#streamExcluding(Predicate)
 * @since   4.24
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Regel, wie ein arbeitsfreier Tag gefunden werden kann. </p>
 *
 * @author  Meno Hochschild
 * @see     DateInterval#streamExcluding(Predicate)
 * @since   4.24
 */
@FunctionalInterface
public interface HolidayModel
    extends ChronoCondition<PlainDate> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Queries if given date is a non-business day. </p>
     *
     * @param   date    the calendar date to be queried
     * @return  {@code true} if the date is a non-business day (holiday or weekend) else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt, ob das angegebene Datum auf einen Feiertag oder ein Wochenende f&auml;llt. </p>
     *
     * @param   date    the calendar date to be queried
     * @return  {@code true} if the date is a non-business day (holiday or weekend) else {@code false}
     */
    @Override
    boolean test(PlainDate date);

    /**
     * <p>Queries if given date is a business day. </p>
     *
     * <p>Equivalent to {@link #test(PlainDate) !test(date)}. </p>
     *
     * @param   date    the calendar date to be queried
     * @return  {@code true} if the date is a business day else {@code false} (holiday or weekend)
     */
    /*[deutsch]
     * <p>Ermittelt, ob das angegebene Datum auf einen Arbeitstag f&auml;llt. </p>
     *
     * <p>&Auml;quivalent zu {@link #test(PlainDate) !test(date)}. </p>
     *
     * @param   date    the calendar date to be queried
     * @return  {@code true} if the date is a business day else {@code false} (holiday or weekend)
     */
    default boolean isBusinessDay(PlainDate date) {
        return !this.test(date);
    }

    /**
     * <p>Determines Saturday and Sunday as non-business days. </p>
     *
     * @return  HolidayModel
     */
    /*[deutsch]
     * <p>Bestimmt Samstag oder Sonntag als arbeitsfreie Tage. </p>
     *
     * @return  HolidayModel
     */
    static HolidayModel ofSaturdayOrSunday() {
        return date -> {
            Weekday wd = date.getDayOfWeek();
            return ((wd == Weekday.SATURDAY) || (wd == Weekday.SUNDAY));
        };
    }

    /**
     * <p>Determines a country-specific weekend as non-business days. </p>
     *
     * @param   country     locale with country information
     * @return  HolidayModel
     * @see     Weekmodel#weekend()
     */
    /*[deutsch]
     * <p>Bestimmt ein l&auml;nderspezifisches Wochenende als arbeitsfreie Tage. </p>
     *
     * @param   country     locale with country information
     * @return  HolidayModel
     * @see     Weekmodel#weekend()
     */
    static HolidayModel ofWeekend(Locale country) {
        return date -> date.matches(Weekmodel.of(country).weekend());
    }

    /**
     * <p>Determines the next business day. </p>
     *
     * <p>Example skipping a weekend: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2017, 2, 3); // Friday
     *     date = date.with(HolidayModel.ofSaturdayOrSunday().nextBusinessDay());
     *     System.out.println(date); // 2017-02-06 (Monday)
     * </pre>
     *
     * @return  new date operator
     * @see     #nextOrSameBusinessDay()
     * @see     #previousOrSameBusinessDay()
     * @see     #previousBusinessDay()
     */
    /*[deutsch]
     * <p>Bestimmt den n&auml;chsten Arbeitstag. </p>
     *
     * <p>Beispiel, das zeigt, wie ein Wochenende ausgelassen wird: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2017, 2, 3); // Freitag
     *     date = date.with(HolidayModel.ofSaturdayOrSunday().nextBusinessDay());
     *     System.out.println(date); // 2017-02-06 (Montag)
     * </pre>
     *
     * @return  new date operator
     * @see     #nextOrSameBusinessDay()
     * @see     #previousBusinessDay()
     * @see     #previousOrSameBusinessDay()
     */
    default ChronoOperator<PlainDate> nextBusinessDay() {
        return date -> {
            while (HolidayModel.this.test(date = date.plus(1, CalendarUnit.DAYS))) {
                // skip and loop
            }
            return date;
        };
    }

    /**
     * <p>Determines the same or next business day. </p>
     *
     * @return  new date operator
     * @see     #nextBusinessDay()
     * @see     #previousBusinessDay()
     * @see     #previousOrSameBusinessDay()
     */
    /*[deutsch]
     * <p>Bestimmt den gleichen oder n&auml;chsten Arbeitstag. </p>
     *
     * @return  new date operator
     * @see     #nextBusinessDay()
     * @see     #previousBusinessDay()
     * @see     #previousOrSameBusinessDay()
     */
    default ChronoOperator<PlainDate> nextOrSameBusinessDay() {
        return date -> {
            while (HolidayModel.this.test(date)) {
                date = date.plus(1, CalendarUnit.DAYS);
            }
            return date;
        };
    }

    /**
     * <p>Determines the previous business day. </p>
     *
     * @return  new date operator
     * @see     #nextBusinessDay()
     * @see     #nextOrSameBusinessDay()
     * @see     #previousOrSameBusinessDay()
     */
    /*[deutsch]
     * <p>Bestimmt den vorherigen Arbeitstag. </p>
     *
     * @return  new date operator
     * @see     #nextBusinessDay()
     * @see     #nextOrSameBusinessDay()
     * @see     #previousOrSameBusinessDay()
     */
    default ChronoOperator<PlainDate> previousBusinessDay() {
        return date -> {
            while (HolidayModel.this.test(date = date.minus(1, CalendarUnit.DAYS))) {
                // skip and loop
            }
            return date;
        };
    }

    /**
     * <p>Determines the same or previous business day. </p>
     *
     * @return  new date operator
     * @see     #nextBusinessDay()
     * @see     #nextOrSameBusinessDay()
     * @see     #previousBusinessDay()
     */
    /*[deutsch]
     * <p>Bestimmt den gleichen oder vorherigen Arbeitstag. </p>
     *
     * @return  new date operator
     * @see     #nextBusinessDay()
     * @see     #nextOrSameBusinessDay()
     * @see     #previousBusinessDay()
     */
    default ChronoOperator<PlainDate> previousOrSameBusinessDay() {
        return date -> {
            while (HolidayModel.this.test(date)) {
                date = date.minus(1, CalendarUnit.DAYS);
            }
            return date;
        };
    }

    /**
     * <p>Queries the first business day in an arbitrary date interval. </p>
     *
     * @return  ChronoFunction which can either yield {@code null} if the first business day does not exist
     *          or throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     * @see     #lastBusinessDay()
     */
    /*[deutsch]
     * <p>Ermittelt den ersten Arbeitstag in einem Datumsintervall. </p>
     *
     * @return  ChronoFunction which can either yield {@code null} if the first business day does not exist
     *          or throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     * @see     #lastBusinessDay()
     */
    default ChronoFunction<ChronoInterval<PlainDate>, PlainDate> firstBusinessDay() {
        return interval -> {
            if (!interval.isFinite()) {
                throw new ChronoException("Cannot query infinite intervals.");
            }
            PlainDate start = interval.getStart().getTemporal();
            PlainDate end = interval.getEnd().getTemporal();
            while (HolidayModel.this.test(start)) {
                start = start.plus(1, CalendarUnit.DAYS);
                if (start.isAfter(end)) {
                    return null;
                }
            }
            return start;
        };
    }

    /**
     * <p>Queries the last business day in an arbitrary date interval. </p>
     *
     * <p>Following example shows the last Friday in a month: </p>
     *
     * <pre>
     *     PlainDate date = CalendarMonth.of(2017, 4).get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
     *     System.out.println(date); // 2017-04-28 (Friday)
     * </pre>
     *
     * @return  ChronoFunction which can either yield {@code null} if the last business day does not exist
     *          or throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     * @see     #firstBusinessDay()
     */
    /*[deutsch]
     * <p>Ermittelt den letzten Arbeitstag in einem Datumsintervall. </p>
     *
     * <p>Folgendes Beispiel zeigt den letzten Freitag in einem Monat: </p>
     *
     * <pre>
     *     PlainDate date = CalendarMonth.of(2017, 4).get(HolidayModel.ofSaturdayOrSunday().lastBusinessDay());
     *     System.out.println(date); // 2017-04-28 (Freitag)
     * </pre>
     *
     * @return  ChronoFunction which can either yield {@code null} if the last business day does not exist
     *          or throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     * @see     #firstBusinessDay()
     */
    default ChronoFunction<ChronoInterval<PlainDate>, PlainDate> lastBusinessDay() {
        return interval -> {
            if (!interval.isFinite()) {
                throw new ChronoException("Cannot query infinite intervals.");
            }
            PlainDate start = interval.getStart().getTemporal();
            PlainDate end = interval.getEnd().getTemporal();
            while (HolidayModel.this.test(end)) {
                end = end.minus(1, CalendarUnit.DAYS);
                if (start.isAfter(end)) {
                    return null;
                }
            }
            return end;
        };
    }

    /**
     * <p>Queries the count of business days in an arbitrary date interval. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *     int count = CalendarMonth.of(2017, 1).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()); // 22
     * </pre>
     *
     * @return  ChronoFunction counting business days which throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der Arbeitstage in einem Datumsintervall. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *     int count = CalendarMonth.of(2017, 1).get(HolidayModel.ofSaturdayOrSunday().countOfBusinessDays()); // 22
     * </pre>
     *
     * @return  ChronoFunction counting business days which throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     */
    default ChronoFunction<ChronoInterval<PlainDate>, Integer> countOfBusinessDays() {
        return interval -> {
            if (!interval.isFinite()) {
                throw new ChronoException("Cannot query infinite intervals.");
            } else if (interval.isEmpty()) {
                return Integer.valueOf(0);
            }
            DateInterval di = DateInterval.from(interval);
            PlainDate start = di.getClosedFiniteStart();
            PlainDate end = di.getClosedFiniteEnd();
            long count = 0;
            while (!start.isAfter(end)) {
                if (!HolidayModel.this.test(start)) {
                    count++;
                }
                start = start.plus(1, CalendarUnit.DAYS);
            }
            if (count > Integer.MAX_VALUE) {
                throw new ArithmeticException("Numerical overflow.");
            }
            return Integer.valueOf((int) count);
        };
    }

    /**
     * <p>Queries the count of non-business days in an arbitrary date interval. </p>
     *
     * @return  ChronoFunction counting free days which throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     */
    /*[deutsch]
     * <p>Ermittelt die Anzahl der arbeitsfreien Tage in einem Datumsintervall. </p>
     *
     * @return  ChronoFunction counting free days which throws an exception if applied on an infinite interval
     * @see     IsoInterval#get(ChronoFunction)
     */
    default ChronoFunction<ChronoInterval<PlainDate>, Integer> countOfHolidays() {
        return interval -> {
            if (!interval.isFinite()) {
                throw new ChronoException("Cannot query infinite intervals.");
            } else if (interval.isEmpty()) {
                return Integer.valueOf(0);
            }
            DateInterval di = DateInterval.from(interval);
            PlainDate start = di.getClosedFiniteStart();
            PlainDate end = di.getClosedFiniteEnd();
            long count = 0;
            while (!start.isAfter(end)) {
                if (HolidayModel.this.test(start)) {
                    count++;
                }
                start = start.plus(1, CalendarUnit.DAYS);
            }
            if (count > Integer.MAX_VALUE) {
                throw new ArithmeticException("Numerical overflow.");
            }
            return Integer.valueOf((int) count);
        };
    }

    /**
     * <p>Queries if a given date is the first business day of month. </p>
     *
     * @return  ChronoCondition
     * @see     net.time4j.engine.ChronoEntity#matches(ChronoCondition)
     * @see     #lastBusinessDayInMonth()
     */
    /*[deutsch]
     * <p>Ermittelt, ob ein Datum der erste Arbeitstag des Monats ist. </p>
     *
     * @return  ChronoCondition
     * @see     net.time4j.engine.ChronoEntity#matches(ChronoCondition)
     * @see     #lastBusinessDayInMonth()
     */
    default ChronoCondition<PlainDate> firstBusinessDayInMonth() {
        return date -> !test(date) && (date.with(previousBusinessDay()).getMonth() != date.getMonth());
    }

    /**
     * <p>Queries if a given date is the last business day of month. </p>
     *
     * <p>Example: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2017, 4, 28);
     *     System.out.println(date.matches(HolidayModel.ofSaturdayOrSunday().lastBusinessDayInMonth()));
     *     // true
     * </pre>
     *
     * @return  ChronoCondition
     * @see     net.time4j.engine.ChronoEntity#matches(ChronoCondition)
     * @see     #firstBusinessDayInMonth()
     */
    /*[deutsch]
     * <p>Ermittelt, ob ein Datum der letzte Arbeitstag des Monats ist. </p>
     *
     * <p>Beispiel: </p>
     *
     * <pre>
     *     PlainDate date = PlainDate.of(2017, 4, 28);
     *     System.out.println(date.matches(HolidayModel.ofSaturdayOrSunday().lastBusinessDayInMonth()));
     *     // true
     * </pre>
     *
     * @return  ChronoCondition
     * @see     net.time4j.engine.ChronoEntity#matches(ChronoCondition)
     * @see     #firstBusinessDayInMonth()
     */
    default ChronoCondition<PlainDate> lastBusinessDayInMonth() {
        return date -> !test(date) && (date.with(nextBusinessDay()).getMonth() != date.getMonth());
    }

    /**
     * <p>Creates a special time unit for the counting of business days. </p>
     *
     * <p>Only applicable on the types {@code PlainDate} and {@code PlainTimestamp}. </p>
     *
     * @return  IsoDateUnit
     */
    /*[deutsch]
     * <p>Erzeugt eine Spezialzeiteinheit f&uuml;r das Z&auml;hlen von Arbeitstagen. </p>
     *
     * <p>Nur auf die Typen {@code PlainDate} und {@code PlainTimestamp} anwendbar. </p>
     *
     * @return  IsoDateUnit
     */
    default IsoDateUnit businessDays() {
        return new BusinessDayUnit(this);
    }

}
