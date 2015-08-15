/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarDays.java) is part of project Time4J.
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

package net.time4j.engine;

import net.time4j.base.MathUtils;

import java.io.Serializable;


/**
 * <p>Represents a count of calendar days. </p>
 *
 * @author  Meno Hochschild
 * @since   3.4/4.3
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Anzahl von Kalendertagen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.4/4.3
 */
public final class CalendarDays
    implements Comparable<CalendarDays>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Represents zero calendar days.
     */
    /*[deutsch]
     * Repr&auml;sentiert null Kalendertage.
     */
    public static final CalendarDays ZERO = new CalendarDays(0);

    /**
     * Represents exactly one calendar day.
     */
    /*[deutsch]
     * Repr&auml;sentiert genau einen Kalendertag.
     */
    public static final CalendarDays ONE = new CalendarDays(1);

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  count of days
     */
    /*[deutsch]
     * @serial  Anzahl der Kalendertage
     */
    private final long days;

    //~ Konstruktoren -----------------------------------------------------

    private CalendarDays(long days) {
        super();

        this.days = days;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Wraps given count of calendar days. </p>
     *
     * @param   days    count of calendar days
     * @return  new or cached instance of {@code CalendarDays}
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Kapselt die angegebenen Kalendertage als Objekt. </p>
     *
     * @param   days    count of calendar days
     * @return  new or cached instance of {@code CalendarDays}
     * @since   3.4/4.3
     */
    public static CalendarDays of(long days) {

        return ((days == 0) ? CalendarDays.ZERO : ((days == 1) ? CalendarDays.ONE : new CalendarDays(days)));

    }

    /**
     * <p>Yields the calendar days as primitive. </p>
     *
     * @return  count of calendar days, maybe zero or negative
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Liefert die Kalendertage als Java-Primitive. </p>
     *
     * @return  count of calendar days, maybe zero or negative
     * @since   3.4/4.3
     */
    public long getAmount() {

        return this.days;

    }

    /**
     * <p>Is the count of calendar days equal to zero? </p>
     *
     * @return  boolean
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Ist die Anzahl der Kalendertage {@code 0}? </p>
     *
     * @return  boolean
     * @since   3.4/4.3
     */
    public boolean isZero() {

        return (this.days == 0);

    }

    /**
     * <p>Is the count of calendar days smaller than zero? </p>
     *
     * @return  boolean
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Ist die Anzahl der Kalendertage negativ? </p>
     *
     * @return  boolean
     * @since   3.4/4.3
     */
    public boolean isNegative() {

        return (this.days < 0);

    }

    /**
     * <p>Calculates the delta of calendar days between given calendar variants. </p>
     *
     * @param   start   first calendar variant (inclusive)
     * @param   end     second calendar variant (exclusive)
     * @return  count of calendar days between start and end
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Berechnet die Tagesdifferenz zwischen den angegebenen Kalendervarianten. </p>
     *
     * @param   start   first calendar variant (inclusive)
     * @param   end     second calendar variant (exclusive)
     * @return  count of calendar days between start and end
     * @since   3.4/4.3
     */
    public static CalendarDays between(
        CalendarVariant<?> start,
        CalendarVariant<?> end
    ){

        long t1 = CalendarVariant.utcDays(start);
        long t2 = CalendarVariant.utcDays(end);
        return CalendarDays.of(MathUtils.safeSubtract(t2, t1));

    }

    /**
     * <p>Calculates the delta of calendar days between given calendrical time points. </p>
     *
     * @param   start   first calendrical time point (inclusive)
     * @param   end     second calendrical time point (exclusive)
     * @return  count of calendar days between start and end
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Berechnet die Tagesdifferenz zwischen den angegebenen Kalenderzeitpunkten. </p>
     *
     * @param   start   first calendrical time point (inclusive)
     * @param   end     second calendrical time point (exclusive)
     * @return  count of calendar days between start and end
     * @since   3.4/4.3
     */
    public static CalendarDays between(
        Calendrical<?, ?> start,
        Calendrical<?, ?> end
    ){

        long t1 = start.getEpochDays();
        long t2 = end.getEpochDays();
        return CalendarDays.of(MathUtils.safeSubtract(t2, t1));

    }

    /**
     * <p>Yields the absolute value of the represented calendar days. </p>
     *
     * @return  non-negative count of calendar days
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Liefert den absoluten Betrag der repr&auml;sentierten Kalendertage. </p>
     *
     * @return  non-negative count of calendar days
     * @since   3.4/4.3
     */
    public CalendarDays abs() {

        return ((this.days < 0) ? CalendarDays.of(MathUtils.safeNegate(this.days)) : this);

    }

    /**
     * <p>Yields the sum of the represented calendar days of this instance and given argument. </p>
     *
     * @param   other   calendar days to be added
     * @return  sum of calendar days
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Liefert die Summe der Kalendertage dieser Instanz und des angegebenen Arguments. </p>
     *
     * @param   other   calendar days to be added
     * @return  sum of calendar days
     * @since   3.4/4.3
     */
    public CalendarDays plus(CalendarDays other) {

        return CalendarDays.of(MathUtils.safeAdd(this.days, other.days));

    }

    /**
     * <p>Yields the delta of the represented calendar days of this instance and given argument. </p>
     *
     * @param   other   calendar days to be subtracted
     * @return  delta of calendar days
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Liefert die Differenz der Kalendertage dieser Instanz und des angegebenen Arguments. </p>
     *
     * @param   other   calendar days to be subtracted
     * @return  delta of calendar days
     * @since   3.4/4.3
     */
    public CalendarDays minus(CalendarDays other) {

        return CalendarDays.of(MathUtils.safeSubtract(this.days, other.days));

    }

    @Override
    public int compareTo(CalendarDays other) {

        return ((this.days < other.days) ? -1 : ((this.days > other.days) ? 1 : 0));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CalendarDays) {
            return (this.days == ((CalendarDays) obj).days);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (int) (this.days ^ (this.days >>> 32));

    }

    /**
     * <p>Returns an ISO-8601-like duration representation in format &quot;[-]P{n}D&quot;. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert eine ISO-8601-&auml;hnliche Darstellung im Format &quot;[-]P{n}D&quot;. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        if (this.days < 0) {
            sb.append('-');
        }
        sb.append('P');
        sb.append(Math.abs(this.days));
        sb.append('D');
        return sb.toString();

    }

}
