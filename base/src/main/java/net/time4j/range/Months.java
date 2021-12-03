/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Months.java) is part of project Time4J.
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
import net.time4j.PlainDate;
import net.time4j.base.MathUtils;
import net.time4j.engine.TimePoint;

import java.text.ParseException;


/**
 * <p>Represents a time span in gregorian months. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Zeitspanne in gregorianischen Monaten. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public final class Months
    extends SingleUnitTimeSpan<CalendarUnit, Months> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Constant for zero gregorian months. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r null gregorianische Monate. </p>
     */
    public static final Months ZERO = new Months(0);

    /**
     * <p>Constant for exactly one gregorian month. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r genau einen gregorianischen Monat. </p>
     */
    public static final Months ONE = new Months(1);

    private static final long serialVersionUID = 6367060429891625338L;

    //~ Konstruktoren -----------------------------------------------------

    private Months(int amount) {
        super(amount, CalendarUnit.MONTHS);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a time span in given gregorian months. </p>
     *
     * @param   months      count of gregorian months, maybe negative
     * @return  time span in months
     * @see     CalendarUnit#MONTHS
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine Zeitspanne in den angegebenen gregorianischen Monaten. </p>
     *
     * @param   months      count of gregorian months, maybe negative
     * @return  time span in months
     * @see     CalendarUnit#MONTHS
     */
    public static Months of(int months) {

        return ((months == 0) ? ZERO : (months == 1) ? ONE : new Months(months));

    }

    /**
     * <p>Determines the temporal distance between given dates/time-points in gregorian months. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of month difference
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Bestimmt die gregorianische Monatsdifferenz zwischen den angegebenen Zeitpunkten. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of month difference
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public static <T extends TimePoint<? super CalendarUnit, T>> Months between(T t1, T t2) {

        long delta = CalendarUnit.MONTHS.between(t1, t2);
        return Months.of(MathUtils.safeCast(delta));

    }

    /**
     * <p>Determines the difference in months between given calendar months. </p>
     *
     * @param   m1  first calendar month
     * @param   m2  second calendar month
     * @return  month difference
     */
    /*[deutsch]
     * <p>Bestimmt die Monatsdifferenz zwischen den angegebenen Kalendermonaten. </p>
     *
     * @param   m1  first calendar month
     * @param   m2  second calendar month
     * @return  month difference
     */
    public static Months between(CalendarMonth m1, CalendarMonth m2) {

        PlainDate d1 = m1.atDayOfMonth(1);
        PlainDate d2 = m2.atDayOfMonth(1);
        return Months.between(d1, d2);

    }

    /**
     * <p>Parses the canonical ISO-8601-format &quot;PnM&quot; with possible preceding minus-char. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert das kanonische ISO-8601-Format &quot;PnM&quot; mit optionalem vorangehenden Minus-Zeichen. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    public static Months parsePeriod(String period) throws ParseException {

        int amount = SingleUnitTimeSpan.parsePeriod(period, 'M');
        return Months.of(amount);

    }

    @Override
    Months with(int amount) {

        return Months.of(amount);

    }

    @Override
    Months self() {

        return this;

    }

}
