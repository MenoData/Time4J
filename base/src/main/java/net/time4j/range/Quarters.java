/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Quarters.java) is part of project Time4J.
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
 * <p>Represents a time span in gregorian quarter years. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Zeitspanne in gregorianischen Quartalen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public final class Quarters
    extends SingleUnitTimeSpan<CalendarUnit, Quarters> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Constant for zero gregorian quarter years. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r null gregorianische Quartale. </p>
     */
    public static final Quarters ZERO = new Quarters(0);

    /**
     * <p>Constant for exactly one gregorian quarter year. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r genau ein gregorianisches Quartal. </p>
     */
    public static final Quarters ONE = new Quarters(1);

    private static final long serialVersionUID = -2100419304667904214L;

    //~ Konstruktoren -----------------------------------------------------

    private Quarters(int amount) {
        super(amount, CalendarUnit.QUARTERS);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a time span in given gregorian quarter years. </p>
     *
     * @param   quarters        count of gregorian quarter years, maybe negative
     * @return  time span in quarter years
     * @see     CalendarUnit#QUARTERS
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine Zeitspanne in den angegebenen gregorianischen Quartalen. </p>
     *
     * @param   quarters        count of gregorian quarter years, maybe negative
     * @return  time span in quarter years
     * @see     CalendarUnit#QUARTERS
     */
    public static Quarters of(int quarters) {

        return ((quarters == 0) ? ZERO : (quarters == 1) ? ONE : new Quarters(quarters));

    }

    /**
     * <p>Determines the temporal distance between given dates/time-points in gregorian quarter years. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of difference in quarter years
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Bestimmt die gregorianische Quartalsdifferenz zwischen den angegebenen Zeitpunkten. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of difference in quarter years
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public static <T extends TimePoint<? super CalendarUnit, T>> Quarters between(T t1, T t2) {

        long delta = CalendarUnit.QUARTERS.between(t1, t2);
        return Quarters.of(MathUtils.safeCast(delta));

    }

    /**
     * <p>Determines the difference in quarters between given quarter years. </p>
     *
     * @param   q1  first quarter year
     * @param   q2  second quarter year
     * @return  difference in quarter years
     */
    /*[deutsch]
     * <p>Bestimmt die Differenz zwischen den angegebenen Quartalen. </p>
     *
     * @param   q1  first quarter year
     * @param   q2  second quarter year
     * @return  difference in quarter years
     */
    public static Quarters between(CalendarQuarter q1, CalendarQuarter q2) {

        PlainDate d1 = q1.atDayOfQuarter(1);
        PlainDate d2 = q2.atDayOfQuarter(1);
        return Quarters.between(d1, d2);

    }

    /**
     * <p>Parses the canonical format &quot;PnQ&quot; with possible preceding minus-char. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert das kanonische Format &quot;PnQ&quot; mit optionalem vorangehenden Minus-Zeichen. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    public static Quarters parsePeriod(String period) throws ParseException {

        int amount = SingleUnitTimeSpan.parsePeriod(period, 'Q');
        return Quarters.of(amount);

    }

    @Override
    Quarters with(int amount) {

        return Quarters.of(amount);

    }

    @Override
    Quarters self() {

        return this;

    }

}
