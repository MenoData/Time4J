/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Calendrical.java) is part of project Time4J.
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


/**
 * <p>Abstract base class of all plain calendar date types which are
 * convertible via their day epoch numbers. </p>
 *
 * @param   <U> generic type of time unit compatible to {@link ChronoUnit})
 * @param   <D> generic type of self reference
 * @author  Meno Hochschild
 * @serial  exclude
 */
/*[deutsch]
 * <p>Abstrakte Basisklasse aller reinen Datumstypen, die &uuml;ber ihre
 * Epochentage ineinander konvertierbar sind. </p>
 *
 * @param   <U> generic type of time unit compatible to {@link ChronoUnit})
 * @param   <D> generic type of self reference
 * @author  Meno Hochschild
 * @serial  exclude
 */
public abstract class Calendrical<U, D extends Calendrical<U, D>>
    extends TimePoint<U, D>
    implements CalendarDate {

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean isBefore(CalendarDate date) {

        return (this.compareByTime(date) < 0);

    }

    @Override
    public boolean isAfter(CalendarDate date) {

        return (this.compareByTime(date) > 0);

    }

    @Override
    public boolean isSimultaneous(CalendarDate date) {

        return ((this == date) || (this.compareByTime(date) == 0));

    }

    /**
     * <p>Defines a total respective natural order. </p>
     *
     * <p>This implementation first evaluates the temporal position on the
     * common timeline, that is the epoch day numbers. Only date objects
     * of the same calendrical type are comparable. The order is consistent
     * with {@code equals()} as long as subclasses don't define further
     * state attributes. If objects of different calendrical type are to be
     * compared on the timeline only applications can either use an
     * {@code EpochDays}-instance as {@code Comparator} or use one of
     * the {@code Temporal}-methods {@code isAfter()}, {@code isBefore()}
     * and {@code isSimultaneous()}. </p>
     *
     * @throws  ClassCastException if there are different date types
     * @see     EpochDays#compare(ChronoDisplay, ChronoDisplay)
     * @see     #isBefore(CalendarDate)
     * @see     #isAfter(CalendarDate)
     */
    /*[deutsch]
     * <p>Definiert eine totale respektive eine nat&uuml;rliche Ordnung. </p>
     *
     * <p>Diese Implementierung wertet die zeitliche Position auf dem
     * gemeinsamen Zeitstrahl aus, also die Epochentage. Nur Datumsobjekte
     * gleichen Kalendertyps k&ouml;nnen miteinander verglichen werden.
     * Die Sortierung ist daher konsistent mit {@code equals()}, solange
     * Subklassen nicht weitere Zustandsattribute definieren. Sollen garantiert
     * Datumsobjekte verschiedenen Typs nur zeitlich verglichen werden, kann
     * entweder eine {@code EpochDays}-Instanz als {@code Comparator}
     * oder eine der {@code Temporal}-Methoden {@code isAfter()},
     * {@code isBefore()} und {@code isSimultaneous()} verwendet werden. </p>
     *
     * @throws  ClassCastException if there are different date types
     * @see     EpochDays#compare(ChronoDisplay, ChronoDisplay)
     * @see     #isBefore(CalendarDate)
     * @see     #isAfter(CalendarDate)
     */
    @Override
    public int compareTo(D date) {

        Class<?> t1 = this.getChronology().getChronoType();
        Class<?> t2 = date.getChronology().getChronoType();

        if (t1 != t2) {
            throw new ClassCastException(
                "Cannot compare different types of dates, "
                + "use instance of EpochDays as comparator instead.");
        }

        return this.compareByTime(date);

    }

    /**
     * <p>Based on the epoch day number and the calendar system. </p>
     *
     * <p>In other words: Two date object are equal if they have the
     * same temporal position on the local timeline and have the same
     * calendrical type. Subclasses which define further state attributes
     * must override this method. </p>
     *
     * <p>If an only temporal comparison is required then the method
     * {@link #isSimultaneous(CalendarDate)} is to be used. </p>
     *
     * @see     Chronology#getChronoType()
     */
    /*[deutsch]
     * <p>Basiert auf den Epochentagen und dem Kalendersystem. </p>
     *
     * <p>Mit anderen Worten: Zwei Datumsobjekte sind genau dann gleich, wenn
     * sie zeitlich gleich UND vom selben Kalendertyp sind. Subklassen, die
     * weitere Zustandsattribute definieren, m&uuml;ssen diese Methode
     * geeignet &uuml;berschreiben. </p>
     *
     * <p>Soll ein rein zeitlicher Vergleich sichergestellt sein, dann
     * ist stattdessen die Methode {@link #isSimultaneous(CalendarDate)}
     * zu verwenden. </p>
     *
     * @see     Chronology#getChronoType()
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof Calendrical) {
            Calendrical<?, ?> that = (Calendrical) obj;
            Class<?> t1 = this.getChronology().getChronoType();
            Class<?> t2 = that.getChronology().getChronoType();
            return (
                (t1 == t2)
                && (this.getDaysSinceEpochUTC() == that.getDaysSinceEpochUTC())
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Based on the epoch day number. </p>
     */
    /*[deutsch]
     * <p>Basiert auf den Epochentagen. </p>
     */
    @Override
    public int hashCode() {

        long days = this.getDaysSinceEpochUTC();
        return (int) (days ^ (days >>> 32));

    }

    /**
     * <p>Adds given calendar days to this instance. </p>
     *
     * @param   days    calendar days to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Addiert die angegebenen Kalendertage zu dieser Instanz. </p>
     *
     * @param   days    calendar days to be added
     * @return  result of addition
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    public D plus(CalendarDays days) {

        long result = Math.addExact(this.getDaysSinceEpochUTC(), days.getAmount());

        try {
            return this.getChronology().getCalendarSystem().transform(result);
        } catch (IllegalArgumentException iae) {
            ArithmeticException ex = new ArithmeticException("Out of range: " + result);
            ex.initCause(iae);
            throw ex;
        }

    }

    /**
     * <p>Subtracts given calendar days from this instance. </p>
     *
     * @param   days    calendar days to be subtracted
     * @return  result of subtraction
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    /*[deutsch]
     * <p>Subtrahiert die angegebenen Kalendertage von dieser Instanz. </p>
     *
     * @param   days    calendar days to be subtracted
     * @return  result of subtraction
     * @throws  ArithmeticException in case of numerical overflow
     * @since   3.4/4.3
     */
    public D minus(CalendarDays days) {

        return this.plus(CalendarDays.of(Math.negateExact(days.getAmount())));

    }

    @Override
    public long getDaysSinceEpochUTC() {

        return this.getChronology().getCalendarSystem().transform(this.getContext());

    }

    /**
     * <p>Definiert eine rein zeitliche Ordnung. </p>
     *
     * <p>Diese Implementierung wertet die zeitliche Position auf dem
     * gemeinsamen Zeitstrahl aus, also die Epochentage. </p>
     *
     * @param   date    another date to be compared with
     * @return  negative, zero or positive integer if this instance is earlier,
     *          simultaneous or later than given date
     */
    protected int compareByTime(CalendarDate date) {

        long d1 = this.getDaysSinceEpochUTC();
        long d2 = date.getDaysSinceEpochUTC();

        return ((d1 < d2) ? -1 : ((d1 == d2) ? 0 : 1));

    }

}
