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

}
