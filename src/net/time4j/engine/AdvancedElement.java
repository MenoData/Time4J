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

import java.io.Serializable;


/**
 * <p>Standardimplementierung eines chronologischen Elements mit verschiedenen
 * element-bezogenen Manipulationsmethoden. </p>
 *
 * @param   <V> generischer Elementwerttyp
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
     * @param   name            Elementname
     * @throws  IllegalArgumentException wenn der Name leer ist oder nur
     *          <i>white space</i> (Leerzeichen etc.) enth&auml;lt
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
     * @param   o1  erstes Vergleichsobjekt, das einen Elementwert liefert
     * @param   o2  zweites Vergleichsobjekt, das einen Elementwert liefert
     * @return  negativ, {@code 0} oder positiv, wenn {@code o1} einen
     *          kleineren, gleichen oder gr&ouml;&szlig;eren Wert hat
     * @throws  ChronoException wenn dieses Element nicht in einem der
     *          Argumente registriert ist und/oder keine Regel zur
     *          Wertermittlung gefunden werden kann
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
     * @param   <T> generischer Operatorzieltyp
     * @return  Operator
     */
    public <T extends ChronoEntity<T>>
    ChronoOperator<T> setToFirst() {

        return ChronoAdjuster.createMinimizer(this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t auf
     * das Elementmaximum setzt. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @return  Operator
     */
    public <T extends ChronoEntity<T>>
    ChronoOperator<T> setToLast() {

        return ChronoAdjuster.createMaximizer(this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den vorherigen Wert bekommt. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @return  Operator
     */
    public <U, T extends TimePoint<U, T>>
    ChronoOperator<T> setToPreviousValue() {

        return new Advancer<U, T>(false, this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element den n&auml;chsten Wert bekommt. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @return  Operator
     */
    public <U, T extends TimePoint<U, T>>
    ChronoOperator<T> setToNextValue() {

        return new Advancer<U, T>(true, this);

    }

    /**
     * <p>Liefert ein Objekt, das eine Entit&auml;t abrundet, indem alle
     * Kindselemente dieses Elements auf ihr Minimum gesetzt werden. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @return  Operator
     */
    public <T extends ChronoEntity<T>>
    ChronoOperator<T> setToFloor() {

        return ChronoAdjuster.createFloor(this);

    }

    /**
     * <p>Liefert ein Objekt, das eine Entit&auml;t aufrundet, indem alle
     * Kindselemente dieses Elements auf ihr Maximum gesetzt werden. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @return  Operator
     */
    public <T extends ChronoEntity<T>>
    ChronoOperator<T> setToCeiling() {

        return ChronoAdjuster.createCeiling(this);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert im
     * Nachsichtigkeitsmodus gesetzt wird. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @param   value   neuer Elementwert
     * @return  Operator
     */
    public <T extends ChronoEntity<T>>
    ChronoOperator<T> setToLenientValue(V value) {

        return ChronoAdjuster.createLenientSetter(this, value);

    }

    /**
     * <p>Liefert einen Operator, der eine beliebige Entit&auml;t so
     * anpasst, da&szlig; dieses Element auf den angegebenen Wert ohne
     * Fehlertoleranz gesetzt wird. </p>
     *
     * @param   <T> generischer Operatorzieltyp
     * @param   value   neuer Elementwert
     * @return  Operator
     */
    public <T extends ChronoEntity<T>>
    ChronoOperator<T> setToStrictValue(V value) {

        return ChronoAdjuster.createStrictSetter(this, value);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Advancer<U, T extends TimePoint<U, T>>
        implements ChronoOperator<T>, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -8302473697073025792L;

        //~ Instanzvariablen ----------------------------------------------

        private final boolean forward;
        private final ChronoElement<?> element;

        //~ Konstruktoren -------------------------------------------------

        private Advancer(
            boolean forward,
            ChronoElement<?> element
        ) {
            super();

            this.forward = forward;
            this.element = element;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T apply(T entity) {

            U unit = entity.getChronology().getBaseUnit(this.element);

            if (unit == null) {
                throw new ChronoException(
                    "No base unit defined for: " + this.element.name());
            } else if (this.forward) {
                return entity.plus(1, unit);
            } else {
                return entity.minus(1, unit);
            }

        }

    }

}
