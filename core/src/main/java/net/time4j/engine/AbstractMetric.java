/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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


import net.time4j.base.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


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
 * @param   <U> generic type of time unit ({@code ChronoUnit})
 * @param   <P> generic type of duration result
 * @author  Meno Hochschild
 * @see     AbstractDuration
 */
public abstract class AbstractMetric
    <U extends ChronoUnit, P extends AbstractDuration<U>>
    implements TimeMetric<U, P>, Comparator<U> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MIO = 1000000;

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
    protected AbstractMetric(
        boolean normalizing,
        U... units
    ) {
        super();

        if (units.length == 0) {
            throw new IllegalArgumentException("Missing units.");
        }

        for (int i = 0; i < units.length - 1; i++) {
            for (int j = i + 1; j < units.length; j++) {
                if (units[i].equals(units[j])) {
                    throw new IllegalArgumentException(
                        "Duplicate unit: " + units[i]);
                }
            }
        }

        Arrays.sort(units, this);

        this.sortedUnits =
            Collections.unmodifiableList(Arrays.asList(units));
        this.normalizing = normalizing;

    }

    /**
     * <p>Creates a new default metric with given set of time units. </p>
     *
     * <p>The given time units can be in any arbitrary order, but internally
     * the will be automatically sorted by their default estimated length. </p>
     *
     * @param   normalizing     Is normalizing required that is shall amounts
     *                          in small units be converted to bigger units?
     * @param   units           time units to be used for calculating time span
     * @throws  IllegalArgumentException if there is not time unit at all
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Standardmetrik mit einem {@code Set}
     * von Zeiteinheiten. </p>
     *
     * <p>Die Zeiteinheiten k&ouml;nnen in beliebiger Reihenfolge
     * angegeben werden, aber intern werden sie &uuml;ber ihre
     * Standardl&auml;nge automatisch sortiert. </p>
     *
     * @param   normalizing     Is normalizing required that is shall amounts
     *                          in small units be converted to bigger units?
     * @param   units           time units to be used for calculating time span
     * @throws  IllegalArgumentException if there is not time unit at all
     */
    protected AbstractMetric(
        boolean normalizing,
        Set<U> units
    ) {
        super();

        if (units.isEmpty()) {
            throw new IllegalArgumentException("Missing units.");
        }

        List<U> list = new ArrayList<U>(units);
        Collections.sort(list, this);

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

        List<TimeSpan.Item<U>> resultList = new ArrayList<TimeSpan.Item<U>>(10);
        TimeAxis<? super U, T> engine = start.getChronology();
        U unit = null;
        long amount = 0;
        int index = 0;
        int endIndex = this.sortedUnits.size();

        while (index < endIndex) {

            // Nächste Subtraktion vorbereiten
            if (amount > 0) {
                t1 = t1.plus(amount, unit);
            }

            // Aktuelle Zeiteinheit bestimmen
            unit = this.sortedUnits.get(index);

            if (
                (this.getLength(engine, unit) < 1.0)
                && (index < endIndex - 1)
            ) {
                amount = 0; // Millis oder Mikros vor Nanos nicht berechnen
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
                amount = t1.until(t2, unit);
                
                if (amount > 0) {
                    resultList.add(TimeSpan.Item.of(amount, unit));
                } else if (amount < 0) {
                    throw new IllegalStateException(
                        "Implementation error: "
                        + "Cannot compute timespan "
                        + "due to illegal negative timespan amounts.");
                }
            }
            index++;
        }

        if (this.normalizing) {
            this.normalize(engine, this.sortedUnits, resultList);
        }

        return this.createTimeSpan(resultList, negative);

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

    private <T extends TimePoint<? super U, T>> void normalize(
        TimeAxis<? super U, T> engine,
        List<U> sortedUnits,
        List<TimeSpan.Item<U>> resultList
    ) {

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
                                putItem(resultList, engine, a, currentUnit);
                            }
                            TimeSpan.Item<U> nextItem =
                                getItem(resultList, nextUnit);
                            if (nextItem == null) {
                                putItem(
                                    resultList, engine, overflow, nextUnit);
                            } else {
                                putItem(
                                    resultList,
                                    engine,
                                    MathUtils.safeAdd(
                                        nextItem.getAmount(),
                                        overflow),
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

    private <T extends TimePoint<? super U, T>> double getLength(
        TimeAxis<? super U, T> engine,
        U unit
    ) {

        return engine.getLength(unit);

    }

}
