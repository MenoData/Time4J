/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FixedCalendarTimeLine.java) is part of project Time4J.
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

import net.time4j.Quarter;
import net.time4j.base.GregorianMath;
import net.time4j.engine.TimeLine;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.function.LongFunction;


/**
 * Defines a timeline for fixed calendar intervals.
 *
 * @param   <T> generic type of underlying fixed calendar interval
 * @author  Meno Hochschild
 * @since   5.0
 */
/*[deutsch]
 * Definiert einen Zeitstrahl f&uuml;r feste Kalenderintervalle.
 *
 * @param   <T> generic type of underlying fixed calendar interval
 * @author  Meno Hochschild
 * @since   5.0
 */
final class FixedCalendarTimeLine<T extends FixedCalendarInterval<T>>
    implements TimeLine<T>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final FixedCalendarTimeLine<CalendarYear> Y =
        new FixedCalendarTimeLine<>(
            'Y',
            CalendarYear::from,
            CalendarYear.from(GregorianMath.MIN_YEAR),
            CalendarYear.from(GregorianMath.MAX_YEAR));
    private static final FixedCalendarTimeLine<CalendarQuarter> Q =
        new FixedCalendarTimeLine<>(
            'Q',
            CalendarQuarter::from,
            CalendarQuarter.of(GregorianMath.MIN_YEAR, Quarter.Q1),
            CalendarQuarter.of(GregorianMath.MAX_YEAR, Quarter.Q4));
    private static final FixedCalendarTimeLine<CalendarMonth> M =
        new FixedCalendarTimeLine<>(
            'M',
            CalendarMonth::from,
            CalendarMonth.of(GregorianMath.MIN_YEAR, 1),
            CalendarMonth.of(GregorianMath.MAX_YEAR, 12));
    private static final FixedCalendarTimeLine<CalendarWeek> W =
        new FixedCalendarTimeLine<>(
            'W',
            CalendarWeek::from,
            CalendarWeek.of(GregorianMath.MIN_YEAR, 1),
            CalendarWeek.of(GregorianMath.MAX_YEAR, 52));

    private static final long serialVersionUID = 9110377577503410192L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  denotes the type of timeline (Y, Q, M or W)
     */
    private final char type; // used in deserialization

    private transient final LongFunction<T> from;
    private transient final T min;
    private transient final T max;

    //~ Konstruktoren -----------------------------------------------------

    private FixedCalendarTimeLine(
        char type,
        LongFunction<T> from,
        T min,
        T max
    ) {
        super();

        this.type = type;
        this.from = from;
        this.min = min;
        this.max = max;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * Obtains a timeline for calendar years.
     *
     * @return  TimeLine
     */
    /*[deutsch]
     * Liefert einen Zeitstrahl f&uuml;r Kalenderjahre.
     *
     * @return  TimeLine
     */
    static FixedCalendarTimeLine<CalendarYear> forYears() {

        return Y;

    }

    /**
     * Obtains a timeline for quarter years.
     *
     * @return  TimeLine
     */
    /*[deutsch]
     * Liefert einen Zeitstrahl f&uuml;r Kalenderquartale.
     *
     * @return  TimeLine
     */
    static FixedCalendarTimeLine<CalendarQuarter> forQuarters() {

        return Q;

    }

    /**
     * Obtains a timeline for calendar months.
     *
     * @return  TimeLine
     */
    /*[deutsch]
     * Liefert einen Zeitstrahl f&uuml;r Kalendermonate.
     *
     * @return  TimeLine
     */
    static FixedCalendarTimeLine<CalendarMonth> forMonths() {

        return M;

    }

    /**
     * Obtains a timeline for calendar weeks (ISO-8601).
     *
     * @return  TimeLine
     */
    /*[deutsch]
     * Liefert einen Zeitstrahl f&uuml;r Kalenderwochen (ISO-8601).
     *
     * @return  TimeLine
     */
    static FixedCalendarTimeLine<CalendarWeek> forWeeks() {

        return W;

    }

    @Override
    public T stepForward(T timepoint) {

        if (timepoint.equals(this.max)) {
            return null;
        }

        return this.from.apply(timepoint.toProlepticNumber() + 1);

    }

    @Override
    public T stepBackwards(T timepoint) {

        if (timepoint.equals(this.min)) {
            return null;
        }

        return this.from.apply(timepoint.toProlepticNumber() - 1);

    }

    @Override
    public T getMinimum() {

        return this.min;

    }

    @Override
    public T getMaximum() {

        return this.max;

    }

    @Override
    public boolean isCalendrical() {

        return true;

    }

    @Override
    public int compare(T o1, T o2) {

        long n1 = o1.toProlepticNumber();
        long n2 = o2.toProlepticNumber();
        return ((n1 < n2) ? -1 : ((n1 > n2) ? 1 : 0));

    }

    // used in FixedCalendarPeriod
    LongFunction<T> mapper() {

        return this.from;

    }

    private Object readResolve() throws ObjectStreamException {

        switch (this.type) {
            case 'Y':
                return Y;
            case 'Q':
                return Q;
            case 'M':
                return M;
            case 'W':
                return W;
            default:
                throw new StreamCorruptedException();
        }

    }

}
