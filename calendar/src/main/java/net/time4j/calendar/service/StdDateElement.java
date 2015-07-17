/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdDateElement.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.calendar.StdCalendarElement;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;
import net.time4j.engine.Chronology;
import net.time4j.engine.StdOperator;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;


/**
 * <p>General date element. </p>
 *
 * <p>All operator methods are based on {@link StdOperator}. Concrete calendars will
 * just instantiate a subclass once and then assign the instance to a static constant
 * and finally register a rule for this element in a given chronology. Applications
 * will usually not directly use this class. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Allgemeines Datumselement. </p>
 *
 * <p>Alle Operator-Methoden basieren auf {@link StdOperator}. Konkrete Kalender werden
 * einfach eine Subklasse einmalig instanzieren und dann die Instanz einer statischen
 * Konstanten zuweisen und schlie&szlig;lich eine Regel f&uuml;r dieses Element in
 * einer vorgegebenen Chronologie registrieren. Anwendungen werden normalerweise diese
 * Klasse nicht direkt verwenden. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public abstract class StdDateElement<V extends Comparable<V>, T extends ChronoEntity<T>>
    extends BasicElement<V>
    implements StdCalendarElement<V, T> {

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological type which has registered this element
     */
    /*[deutsch]
     * @serial  assoziierter chronologischer Typ, der dieses Element registriert hat
     */
    private final Class<T> chrono;

    private transient final char symbol;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>For subclasses. </p>
     *
     * @param   name        element name
     * @param   chrono      chronological type which registers this element
     */
    /*[deutsch]
     * <p>F&uuml;r Subklassen. </p>
     *
     * @param   name        element name
     * @param   chrono      chronological type which registers this element
     */
    public StdDateElement(
        String name,
        Class<T> chrono
    ) {
        this(name, chrono, '\u0000');

    }

    /**
     * <p>For subclasses. </p>
     *
     * @param   name        element name
     * @param   chrono      chronological type which registers this element
     * @param   symbol      format pattern symbol
     */
    /*[deutsch]
     * <p>F&uuml;r Subklassen. </p>
     *
     * @param   name        element name
     * @param   chrono      chronological type which registers this element
     * @param   symbol      format pattern symbol
     */
    public StdDateElement(
        String name,
        Class<T> chrono,
        char symbol
    ) {
        super(name);

        this.chrono = chrono;
        this.symbol = symbol;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public char getSymbol() {

        return this.symbol;

    }

    @Override
    public boolean isDateElement() {

        return true;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public ChronoOperator<T> minimized() {

        return StdOperator.minimized(this);

    }

    @Override
    public ChronoOperator<T> maximized() {

        return StdOperator.maximized(this);

    }

    @Override
    public ChronoOperator<T> decremented() {

        return StdOperator.decremented(this);

    }

    @Override
    public ChronoOperator<T> incremented() {

        return StdOperator.incremented(this);

    }

    @Override
    public ChronoOperator<T> atFloor() {

        return StdOperator.atFloor(this);

    }

    @Override
    public ChronoOperator<T> atCeiling() {

        return StdOperator.atCeiling(this);

    }

    /**
     * @serialData  preserves singleton semantic
     * @return      resolved singleton
     * @throws      ObjectStreamException if resolving fails
     */
    protected Object readResolve() throws ObjectStreamException {

        String comp = this.name();

        for (ChronoElement<?> element : Chronology.lookup(this.chrono).getRegisteredElements()) {
            if (element.name().equals(comp)) {
                return element;
            }
        }

        throw new InvalidObjectException(comp);

    }

}
