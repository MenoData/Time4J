/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AbstractMetric.java) is part of project Time4J.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>Represents a metric suitable for the default algorithm of Time4J. </p>
 *
 * <p>If the starting time point is after the end time point then a
 * duration computed with this metric will be negative. In this case
 * the metric defined here will first toggle the time points to be
 * compared and then compare all elements in the order of ascending
 * precision. Elements which differ less than a full unit will cause
 * an amount of {@code 0} in related duration item. Convertible units
 * will be consolidated in one step. Finally the representation of
 * the duration will be normalized such that small units will be
 * converted to larger units if possible. </p>
 *
 * <p>This metric can be changed to a reversible one by calling {@code reversible()}
 * (third invariance in {@code AbstractDuration}. </p>
 *
 * @param   <U> generic type of time unit ({@code ChronoUnit})
 * @param   <P> generic type of duration result
 * @author  Meno Hochschild
 * @see     AbstractDuration
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Metrik passend zum Standardalgorithmus
 * von Time4J. </p>
 *
 * <p>Eine mit Hilfe der Metrik berechnete Zeitspanne ist negativ, wenn
 * der Start nach dem Endzeitpunkt liegt. Im Fall der negativen Zeitspanne
 * werden die zu vergleichenden Zeitpunkte zuerst vertauscht und dann ihre
 * Elemente miteinander verglichen, wieder in der Reihenfolge aufsteigender
 * Genauigkeit. Elemente, die sich um weniger als eine volle Zeiteinheit
 * unterscheiden, gelten als gleich. Konvertierbare Zeiteinheiten werden
 * dabei in einem Schritt zusammengefasst. Am Ende wird die Darstellung
 * in der Regel normalisiert, also kleine Zeiteinheiten so weit wie
 * m&ouml;glich in gro&szlig;e Einheiten umgerechnet. </p>
 *
 * <p>Diese Metrik kann mittels Aufruf von {@code reversible()} umkehrbar gemacht werden
 * (dritte Invarianzbedingung in {@code AbstractDuration}. </p>
 *
 * @param   <U> generic type of time unit ({@code ChronoUnit})
 * @param   <P> generic type of duration result
 * @author  Meno Hochschild
 * @see     AbstractDuration
 */
public abstract class AbstractMetric<U extends ChronoUnit, P extends AbstractDuration<U>>
    implements TimeMetric<U, P>, Comparator<U> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;
    private static final double LENGTH_OF_FORTNIGHT = 86400.0 * 14;

    //~ Instanzvariablen --------------------------------------------------

    private final List<U> sortedUnits;
    private final boolean normalizing;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new default metric with given array of time units. </p>
     *
     * <p>The given time units can be in any arbitrary order, but internally
     * the will be automatically sorted by their default estimated length. </p>
     *
     * @param   normalizing     Is normalizing required that is shall amounts
     *                          in small units be converted to bigger units?
     * @param   units           time units to be used for calculating time span
     * @throws  IllegalArgumentException if any time unit is given more than
     *          once or if there is no time unit at all
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Standardmetrik mit einem Array von
     * Zeiteinheiten. </p>
     *
     * <p>Die Zeiteinheiten k&ouml;nnen in beliebiger Reihenfolge
     * angegeben werden, aber intern werden sie &uuml;ber ihre
     * Standardl&auml;nge automatisch sortiert. </p>
     *
     * @param   normalizing     Is normalizing required that is shall amounts
     *                          in small units be converted to bigger units?
     * @param   units           time units to be used for calculating time span
     * @throws  IllegalArgumentException if any time unit is given more than
     *          once or if there is no time unit at all
     */
    @SafeVarargs
    protected AbstractMetric(
        boolean normalizing,
        U... units
    ) {
        this(Arrays.asList(units), normalizing);
    }

    /**
     * <p>Creates a new default metric with given collection of time units. </p>
     *
     * <p>The given time units can be in any arbitrary order, but internally
     * the will be automatically sorted by their default estimated length. </p>
     *
     * @param   normalizing     Is normalizing required that is shall amounts
     *                          in small units be converted to bigger units?
     * @param   units           time units to be used for calculating time span
     * @throws  IllegalArgumentException if any time unit is given more than once or if there is no time unit at all
     * @since   5.6
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Standardmetrik mit einer {@code Collection} von Zeiteinheiten. </p>
     *
     * <p>Die Zeiteinheiten k&ouml;nnen in beliebiger Reihenfolge
     * angegeben werden, aber intern werden sie &uuml;ber ihre
     * Standardl&auml;nge automatisch sortiert. </p>
     *
     * @param   normalizing     Is normalizing required that is shall amounts
     *                          in small units be converted to bigger units?
     * @param   units           time units to be used for calculating time span
     * @throws  IllegalArgumentException if any time unit is given more than once or if there is no time unit at all
     * @since   5.6
     */
    protected AbstractMetric(
        boolean normalizing,
        Collection<? extends U> units
    ) {
        this(new ArrayList<>(units), normalizing);
    }

    private AbstractMetric(
        List<U> list,
        boolean normalizing
    ) {
        super();

        if (list.isEmpty()) {
            throw new IllegalArgumentException("Missing units.");
        }

        list.sort(this);

        for (int i = 0, n = list.size(); i < n; i++) {
            U unit = list.get(i);

            for (int j = i + 1; j < n; j++) {
                if (unit.equals(list.get(j))) {
                    throw new IllegalArgumentException("Duplicate unit: " + unit);
                }
            }
        }

        this.sortedUnits = Collections.unmodifiableList(list);
        this.normalizing = normalizing;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Compares time units by their length in descending order. </p>
     *
     * @param   u1  first time unit
     * @param   u2  second time unit
     * @return  negative, zero or positive if u1 is greater, equal to
     *          or smaller than u2
     */
    /*[deutsch]
     * <p>Vergleicht Zeiteinheiten absteigend nach ihrer L&auml;nge. </p>
     *
     * @param   u1  first time unit
     * @param   u2  second time unit
     * @return  negative, zero or positive if u1 is greater, equal to
     *          or smaller than u2
     */
    @Override
    public int compare(U u1, U u2) {

        return Double.compare(u2.getLength(), u1.getLength()); // descending

    }

    @Override
    public <T extends TimePoint<? super U, T>> P between(
        T start,
        T end
    ) {

        return this.between(start, end, -1);

    }

    @SuppressWarnings("unchecked")
    private <T extends TimePoint<? super U, T>> P between(
        T start,
        T end,
        int monthIndex
    ) {

        if (end.equals(start)) {
            return this.createEmptyTimeSpan();
        }

        T t1 = start;
        T t2 = end;
        boolean negative = false;

        // Lage von Start und Ende bestimmen
        if (t1.compareTo(t2) > 0) {
            T temp = t1;
            t1 = end;
            t2 = temp;
            negative = true;
        }

        List<TimeSpan.Item<U>> resultList = new ArrayList<>(10);
        TimeAxis<? super U, T> engine = start.getChronology();
        int index = 0;
        int endIndex = this.sortedUnits.size();

        while (index < endIndex) {

            // Aktuelle Zeiteinheit bestimmen
            U unit = this.sortedUnits.get(index);

            if (
                (this.getLength(engine, unit) < 1.0)
                && (index < endIndex - 1)
            ) {
                // Millis oder Mikros vor Nanos nicht berechnen (maximal eine fraktionale Einheit)
            } else {
                // konvertierbare Einheiten zusammenfassen
                int k = index + 1;
                long factor = 1;

                while (k < endIndex) {
                    U nextUnit = this.sortedUnits.get(k);
                    factor *= this.getFactor(engine, unit, nextUnit);
                    if (
                        (factor < MIO)
                        && engine.isConvertible(unit, nextUnit)
                    ) {
                        unit = nextUnit;
                    } else {
                        break;
                    }
                    k++;
                }
                index = k - 1;

                // Differenz in einer Einheit berechnen
                long amount = t1.until(t2, unit);

                if (amount < 0) {
                    throw new IllegalStateException(
                        "Implementation error: "
                            + "Cannot compute timespan "
                            + "due to illegal negative timespan amounts.");
                }

                // Dauerkomponente erzeugen, mit Monatsendekorrektur ggf. Betrag verkleinern
                while (amount > 0) {
                    T temp = t1.plus(amount, unit);
                    if (
                        (index > monthIndex)
                        || (index == endIndex - 1)
                        || temp.minus(amount, unit).equals(t1)
                    ) {
                        t1 = temp;
                        resultList.add(this.resolve(TimeSpan.Item.of(amount, unit)));
                        break;
                    } else {
                        amount--; // avoid possible end-of-month-correction
                    }
                }
            }
            index++;
        }

        if (this.normalizing) {
            this.normalize(engine, this.sortedUnits, resultList);
        }

        return this.createTimeSpan(resultList, negative);

    }

    @Override
    public TimeMetric<U, P> reversible() {

        return new ReversalMetric<>(this);

    }

    /**
     * <p>Creates an empty time span. </p>
     *
     * @return  empty time span without any time units
     * @see     TimeSpan#isEmpty()
     */
    /*[deutsch]
     * <p>Erzeugt eine leere Zeitspanne. </p>
     *
     * @return  empty time span without any time units
     * @see     TimeSpan#isEmpty()
     */
    protected abstract P createEmptyTimeSpan();

    /**
     * <p>Creates a time span with the given units and amounts. </p>
     *
     * @param   items       elements of time span
     * @param   negative    sign of time span
     * @return  new time span
     */
    /*[deutsch]
     * <p>Erzeugt eine Zeitspanne mit den angegebenen Einheiten und
     * Betr&auml;gen. </p>
     *
     * @param   items       elements of time span
     * @param   negative    sign of time span
     * @return  new time span
     */
    protected abstract P createTimeSpan(
        List<TimeSpan.Item<U>> items,
        boolean negative
    );

    /**
     * <p>Hook for adjustments like resolving millis or micros to nanos. </p>
     *
     * @param   item        item to be adjusted
     * @return  adjusted item (usually the same as the argument)
     * @since   3.21/4.17
     */
    /*[deutsch]
     * <p>Einsprungpunkt f&uuml;r Anpassungen wie die Aufl&ouml;sung von Milli- und Mikrosekunden zu Nanosekunden. </p>
     *
     * @param   item        item to be adjusted
     * @return  adjusted item (usually the same as the argument)
     * @since   3.21/4.17
     */
    protected TimeSpan.Item<U> resolve(TimeSpan.Item<U> item) {

        return item;

    }

    @SuppressWarnings("unchecked")
    private <T extends TimePoint<? super U, T>> void normalize(
        TimeAxis<? super U, T> engine,
        List<U> sortedUnits,
        List<TimeSpan.Item<U>> resultList
    ) {

        Comparator<? super U> comparator = engine.unitComparator();

        for (int i = sortedUnits.size() - 1; i >= 0; i--) {
            if (i > 0) {
                U currentUnit = sortedUnits.get(i);
                U nextUnit = sortedUnits.get(i - 1);
                long factor = this.getFactor(engine, nextUnit, currentUnit);
                if (
                    (factor < MIO)
                    && engine.isConvertible(nextUnit, currentUnit)
                ) {
                    TimeSpan.Item<U> currentItem =
                        getItem(resultList, currentUnit);
                    if (currentItem != null) {
                        long currentValue = currentItem.getAmount();
                        long overflow = currentValue / factor;
                        if (overflow > 0) {
                            long a = currentValue % factor;
                            if (a == 0) {
                                removeItem(resultList, currentUnit);
                            } else {
                                putItem(resultList, comparator, a, currentUnit);
                            }
                            TimeSpan.Item<U> nextItem =
                                getItem(resultList, nextUnit);
                            if (nextItem == null) {
                                putItem(resultList, comparator, overflow, nextUnit);
                            } else {
                                putItem(
                                    resultList,
                                    comparator,
                                    Math.addExact(nextItem.getAmount(), overflow),
                                    nextUnit
                                );
                            }
                        }
                    }
                }
            }
        }

    }

    private static <U> TimeSpan.Item<U> getItem(
        List<TimeSpan.Item<U>> items,
        U unit
    ) {

        for (int i = 0, n = items.size(); i < n; i++) {
            TimeSpan.Item<U> item = items.get(i);
            if (item.getUnit().equals(unit)) {
                return item;
            }
        }

        return null;

    }

    private static <U> void putItem(
        List<TimeSpan.Item<U>> items,
        Comparator<? super U> comparator,
        long amount,
        U unit
    ) {

        TimeSpan.Item<U> item = TimeSpan.Item.of(amount, unit);
        int insert = 0;

        for (int i = 0, n = items.size(); i < n; i++) {
            U u = items.get(i).getUnit();

            if (u.equals(unit)) {
                items.set(i, item);
                return;
            } else if (
                (insert == i)
                && (comparator.compare(u, unit) < 0)
            ) {
                insert++;
            }
        }

        items.add(insert, item);

    }

    private static <U> void removeItem(
        List<TimeSpan.Item<U>> items,
        U unit
    ) {

        for (int i = 0, n = items.size(); i < n; i++) {
            if (items.get(i).getUnit().equals(unit)) {
                items.remove(i);
                return;
            }
        }

    }

    private <T extends TimePoint<? super U, T>> long getFactor(
        TimeAxis<? super U, T> engine,
        U unit1,
        U unit2
    ) {

        double d1 = this.getLength(engine, unit1);
        double d2 = this.getLength(engine, unit2);
        return Math.round(d1 / d2);

    }

    @SuppressWarnings("unchecked")
    private <T extends TimePoint<? super U, T>> double getLength(
        TimeAxis<? super U, T> engine,
        U unit
    ) {

        return engine.getLength(unit);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class ReversalMetric<U extends ChronoUnit, P extends AbstractDuration<U>>
        implements TimeMetric<U, P> {

        //~ Instanzvariablen ----------------------------------------------

        private final AbstractMetric<U, P> delegate;
        private final int monthIndex;

        //~ Konstruktoren -------------------------------------------------

        private ReversalMetric(AbstractMetric<U, P> delegate) {
            super();

            this.delegate = delegate;
            int mi = -1;

            for (int i = this.delegate.sortedUnits.size() - 1; i >= 0; i--) {
                U unit = this.delegate.sortedUnits.get(i);

                if (Double.compare(unit.getLength(), LENGTH_OF_FORTNIGHT) > 0) {
                    mi = i;
                    break;
                }
            }

            this.monthIndex = mi;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public <T extends TimePoint<? super U, T>> P between(T start, T end) {
            return this.delegate.between(start, end, this.monthIndex);
        }

        @Override
        public TimeMetric<U, P> reversible() {
            return this;
        }
    }

}
