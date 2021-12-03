/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Weeks.java) is part of project Time4J.
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
import net.time4j.Weekday;
import net.time4j.base.MathUtils;
import net.time4j.engine.TimePoint;

import java.text.ParseException;


/**
 * <p>Represents a time span in 7-day weeks. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Zeitspanne in 7-Tage-Wochen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.21/4.17
 */
public final class Weeks
    extends SingleUnitTimeSpan<CalendarUnit, Weeks> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Constant for zero weeks. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r null Wochen. </p>
     */
    public static final Weeks ZERO = new Weeks(0);

    /**
     * <p>Constant for exactly one week. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r genau eine Woche. </p>
     */
    public static final Weeks ONE = new Weeks(1);

    private static final long serialVersionUID = 78946640166405240L;

    //~ Konstruktoren -----------------------------------------------------

    private Weeks(int amount) {
        super(amount, CalendarUnit.WEEKS);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains a time span in given weeks. </p>
     *
     * @param   weeks       count of weeks, maybe negative
     * @return  time span in weeks
     * @see     CalendarUnit#WEEKS
     */
    /*[deutsch]
     * <p>Erh&auml;lt eine Zeitspanne in den angegebenen Wochen. </p>
     *
     * @param   weeks       count of weeks, maybe negative
     * @return  time span in weeks
     * @see     CalendarUnit#WEEKS
     */
    public static Weeks of(int weeks) {

        return ((weeks == 0) ? ZERO : (weeks == 1) ? ONE : new Weeks(weeks));

    }

    /**
     * <p>Determines the temporal distance between given dates/time-points in weeks. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of week difference
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    /*[deutsch]
     * <p>Bestimmt die gregorianische Wochendifferenz zwischen den angegebenen Zeitpunkten. </p>
     *
     * @param   <T> generic type of time-points
     * @param   t1      first time-point
     * @param   t2      second time-point
     * @return  result of week difference
     * @see     net.time4j.PlainDate
     * @see     net.time4j.PlainTimestamp
     */
    public static <T extends TimePoint<? super CalendarUnit, T>> Weeks between(T t1, T t2) {

        long delta = CalendarUnit.WEEKS.between(t1, t2);
        return Weeks.of(MathUtils.safeCast(delta));

    }

    /**
     * <p>Determines the difference in weeks between given quarter years. </p>
     *
     * @param   w1  first calendar week
     * @param   w2  second calendar week
     * @return  difference in weeks
     */
    /*[deutsch]
     * <p>Bestimmt die Differenz zwischen den angegebenen Kalenderwochen. </p>
     *
     * @param   w1  first calendar week
     * @param   w2  second calendar week
     * @return  difference in weeks
     */
    public static Weeks between(CalendarWeek w1, CalendarWeek w2) {

        PlainDate d1 = w1.at(Weekday.MONDAY);
        PlainDate d2 = w2.at(Weekday.MONDAY);
        return Weeks.between(d1, d2);

    }

    /**
     * <p>Parses the canonical ISO-8601-format &quot;PnW&quot; with possible preceding minus-char. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert das kanonische ISO-8601-Format &quot;PnW&quot; mit optionalem vorangehenden Minus-Zeichen. </p>
     *
     * @param   period      the formatted string to be parsed
     * @return  parsed instance
     * @throws  ParseException if given argument cannot be parsed
     */
    public static Weeks parsePeriod(String period) throws ParseException {

        int amount = SingleUnitTimeSpan.parsePeriod(period, 'W');
        return Weeks.of(amount);

    }

    @Override
    Weeks with(int amount) {

        return Weeks.of(amount);

    }

    @Override
    Weeks self() {

        return this;

    }

}
