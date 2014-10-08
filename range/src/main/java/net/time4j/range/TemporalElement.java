/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TemporalElement.java) is part of project Time4J.
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

import net.time4j.engine.Calendrical;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.Temporal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Identifiziert den temporalen Wert einer Intervallgrenze. </p>
 *
 * @param   <T> generic temporal type
 * @author  Meno Hochschild
 */
final class TemporalElement<T extends Temporal<? super T>>
    implements ChronoElement<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<String, TemporalElement<?>> INSTANCES =
        new ConcurrentHashMap<String, TemporalElement<?>>();

    //~ Instanzvariablen --------------------------------------------------

    private final Class<T> type;
    private final String name;

    //~ Konstruktoren -----------------------------------------------------

    TemporalElement(
        Class<T> type,
        boolean start
    ) {
        super();

        this.type = type;
        this.name = toKey(type, start);

    }

    //~ Methoden ----------------------------------------------------------

    static <T extends Temporal<? super T>> TemporalElement<T> start(
        Class<T> type
    ) {

        return of(type, true);

    }

    static <T extends Temporal<? super T>> TemporalElement<T> end(
        Class<T> type
    ) {

        return of(type, false);

    }

    @Override
    public String name() {

        return this.name;

    }

    @Override
    public Class<T> getType() {

        return this.type;

    }

    @Override
    public char getSymbol() {

        return '\u0000';

    }

    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        T t1 = o1.get(this);
        T t2 = o2.get(this);

        if (t1.isBefore(t2)) {
            return -1;
        } else if (t1.isAfter(t2)) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public T getDefaultMinimum() {

        return null;

    }

    @Override
    public T getDefaultMaximum() {

        return null;

    }

    @Override
    public boolean isDateElement() {

        return Calendrical.class.isAssignableFrom(this.type);

    }

    @Override
    public boolean isTimeElement() {

        return !this.isDateElement();

    }

    @Override
    public boolean isLenient() {

        return false;

    }

    @Override
    public String toString() {

        return this.name;

    }

    private static String toKey(
        Class<?> type,
        boolean start
    ) {

        return (start ? "START_" : "END_") + type.getName();

    }

    @SuppressWarnings("unchecked")
    private static <T extends Temporal<? super T>> TemporalElement<T> of(
        Class<T> type,
        boolean start
    ) {

        String key = toKey(type, start);
        TemporalElement<?> instance = INSTANCES.get(key);

        if (instance == null) {
            instance = new TemporalElement<T>(type, start);
            TemporalElement<?> old = INSTANCES.putIfAbsent(key, instance);

            if (old != null) {
                instance = old;
            }
        }

        return (TemporalElement<T>) instance;

    }

}
