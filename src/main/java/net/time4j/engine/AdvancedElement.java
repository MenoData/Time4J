/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AdvancedElement.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.engine;


/**
 * <p>Standardimplementierung eines chronologischen Elements mit verschiedenen
 * element-bezogenen Manipulationsmethoden. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 */
public abstract class AdvancedElement<V extends Comparable<V>>
    extends BasicElement<V> {

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor f&uuml;r Subklassen, die eine so erzeugte Instanz
     * in der Regel statischen Konstanten zuweisen und damit Singletons
     * erzeugen. </p>
     *
     * @param   name            name of element
     * @throws  IllegalArgumentException if the name is empty or if it only
     *          contains <i>white space</i> (spaces, tabs etc.)
     * @see     ChronoElement#name()
     */
    protected AdvancedElement(String name) {
        super(name);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Vergleicht die Werte dieses Elements auf Basis ihrer
     * nat&uuml;rlichen Ordnung. </p>
     *
     * @throws  ChronoException if this element is not registered in any entity
     *          and/or if no element rule exists to extract the element value
     */
    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementminimum setzt. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> min() {

        return StdOperator.createMinimizer(this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementmaximum setzt. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> max() {

        return StdOperator.createMaximizer(this);

    }

    /**
     * <p>Liefert ein Objekt, das eine Entit&auml;t abrundet, indem alle
     * Kindselemente dieses Elements auf ihr Minimum gesetzt werden. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> floor() {

        return StdOperator.createFloor(this);

    }

    /**
     * <p>Liefert ein Objekt, das eine Entit&auml;t aufrundet, indem alle
     * Kindselemente dieses Elements auf ihr Maximum gesetzt werden. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> ceiling() {

        return StdOperator.createCeiling(this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert im
     * Nachsichtigkeitsmodus gesetzt wird. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @param   value   new element value
     * @return  operator
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> lenient(
        V value
    ) {

        return StdOperator.createLenientSetter(this, value);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den vorherigen Wert bekommt. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @return  decrementing operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> previous() {

        return StdOperator.createPrevious(this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den n&auml;chsten Wert bekommt. </p>
     *
     * @param   <T> generic type of target entity the operator is applied to
     * @return  incrementing operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    public <T extends ChronoEntity<T>> ChronoOperator<T> next() {

        return StdOperator.createNext(this);

    }

    /**
     * <p>Rollt das angegebene chronologische Element um eine Anzahl von
     * Werten innerhalb seines Wertbereichs zwischen Minimum und Maximum
     * (Odometer-Prinzip). </p>
     *
     * <p>&Auml;hnlich wie die Addition von Zeiteinheiten zu einem Zeitpunkt,
     * aber mit dem Unterschied, da&szlig; erstens die Methode auf Elementen
     * statt Zeiteinheiten definiert ist, zweitens mit gr&ouml;&szlig;eren
     * Zeiteinheiten verkn&uuml;pfte Elemente nicht beeinflusst werden
     * und drittens das Rollverhalten im Gegensatz zur Addition von den
     * Elementwertgrenzen abh&auml;ngt. </p>
     *
     * <p>Das Rollmaximum ist optional, sollte aber dann abweichend vom
     * kontextabh&auml;ngigen Maximum angegeben werden, wenn sonst andere
     * gr&ouml;&szlig;ere Elemente sich durch das Rollen zu &auml;ndern
     * drohen. </p>
     *
     * @param   <V> generic type of element value
     * @param   <T> generic type of target entity the operator is applied to
     * @param   element     chronological element to be rolled
     * @param   units       count of base units to be added, rolling when
     *                      element minimum (or maximum) has been reached
     * @param   rollMax     maximum limit of rolling (optional)
     * @return  rolling operator
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    public static <V, T extends ChronoEntity<T>> ChronoOperator<T> roll(
        ChronoElement<V> element,
        long units,
        V rollMax // optional
    ) {

        return new RollingOperator<V, T>(element, units, rollMax);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class RollingOperator<V, T extends ChronoEntity<T>>
        implements ChronoOperator<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final ChronoElement<V> element;
        private final long amount;
        private final V rollMax;

        //~ Konstruktoren -------------------------------------------------

        RollingOperator(
            ChronoElement<V> element,
            long amount,
            V rollMax // optional
        ) {
            super();

            this.element = element;
            this.amount = amount;
            this.rollMax = rollMax;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T apply(T entity) {

            T result = null;

            if (entity instanceof TimePoint) {
                TimePoint<?, ?> tp = TimePoint.class.cast(entity);
                Object ret = roll(tp, this.element, this.amount, this.rollMax);
                result = entity.getChronology().getChronoType().cast(ret);
            }

            if (result == null) {
                throw new ChronoException(
                    "No base unit defined for: " + this.element.name());
            } else {
                return result;
            }

        }

        // wildcard capture
        private static <V, U, T extends TimePoint<U, T>> T roll(
            TimePoint<U, T> entity,
            ChronoElement<V> element,
            long amount,
            V rollMax
        ) {

            T context = entity.getContext();
            TimeAxis<U, T> axis = entity.getChronology();
            U unit = axis.getBaseUnit(element);

            if (unit == null) {
                return null;
            } else if (amount == 0) {
                return context;
            }

            long value = amount;
            UnitRule<T> unitRule = axis.getRule(unit);
            ElementRule<T, V> elementRule = axis.getRule(element);

            T min = elementRule.withValue(
                context,
                elementRule.getMinimum(context),
                element.isLenient()
            );

            T max = elementRule.withValue(
                context,
                ((rollMax == null)
                    ? elementRule.getMaximum(context)
                    : rollMax),
                element.isLenient()
            );

            // Sonderfall zeitlich absteigende Reihenfolge (z.B. year-of-era)
            if (min.compareTo(max) > 0) {
                T temp = min;
                min = max;
                max = temp;
            }

            if (value < 0) {
                long diff = unitRule.between(min, context);
                if (value + diff < 0) {
                    context = max;
                    value = (value + diff + 1);
                    if (value != 0) {
                        long size = unitRule.between(min, max) + 1;
                        value = (value % size);
                        if (value == 1 - size) {
                            return min;
                        }
                    }
                } else if (value + diff == 0) {
                    return min;
                }
            } else {
                long diff = unitRule.between(context, max);
                if (value > diff) {
                    context = min;
                    value = (value - diff - 1);
                    if (value != 0) {
                        long size = unitRule.between(min, max) + 1;
                        value = (value % size);
                        if (value == size - 1) {
                            return max;
                        }
                    }
                } else if (value == diff) {
                    return max;
                }
            }

            return (
                (value == 0)
                ? context
                : unitRule.addTo(context, value)
            );

        }

    }

}
