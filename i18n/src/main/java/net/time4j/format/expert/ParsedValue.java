/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ParsedValue.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * <p>Definiert eine aktualisierbare Wertquelle mit nur einem Ergebniswert. </p>
 *
 * @author  Meno Hochschild
 * @since   3.26/4.22
 */
class ParsedValue
    extends ParsedEntity<ParsedValue> {

    //~ Instanzvariablen --------------------------------------------------

    private Object result;

    // other element-value-pairs which don't exist by default but can be used in extensions or mergers
    private Map<ChronoElement<?>, Object> map = null;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard-Konstruktor.
     */
    ParsedValue() {
        super();

        this.result = null;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(ChronoElement<?> element) {

        if ((element != null) && (this.map != null)) {
            return this.map.containsKey(element);
        }

        return false;

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        if (element == null) {
            throw new NullPointerException();
        }

        Map<ChronoElement<?>, Object> m = this.map;

        if ((m != null) && m.containsKey(element)) {
            return element.getType().cast(m.get(element));
        }

        throw new ChronoException("No value found for: " + element.name());

    }

    @Override
    public int getInt(ChronoElement<Integer> element) {

        if (element == null) {
            throw new NullPointerException();
        }

        Map<ChronoElement<?>, Object> m = this.map;

        if ((m != null) && m.containsKey(element)) {
            return element.getType().cast(m.get(element)).intValue();
        }

        return Integer.MIN_VALUE;

    }

    @Override
    public Set<ChronoElement<?>> getRegisteredElements() {

        if (this.map == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(this.map.keySet());

    }

    // called by format processors
    void put(ChronoElement<?> element, int v) {

        if (element == null) {
            throw new NullPointerException();
        }

        Map<ChronoElement<?>, Object> m = this.map;
        if (m == null) {
            m = new HashMap<ChronoElement<?>, Object>();
            this.map = m;
        }
        m.put(element, Integer.valueOf(v));

    }

    // called by format processors
    void put(ChronoElement<?> element, Object v) {

        if (element == null) {
            throw new NullPointerException();
        }

        if (v == null) { // removal
            if (this.map != null) {
                this.map.remove(element);
                if (this.map.isEmpty()) {
                    this.map = null;
                }
            }
        } else {
            Map<ChronoElement<?>, Object> m = this.map;
            if (m == null) {
                m = new HashMap<ChronoElement<?>, Object>();
                this.map = m;
            }
            m.put(element, v);
        }

    }

    @Override
    void setResult(Object entity) {

        this.result = entity;

    }

    @SuppressWarnings("unchecked")
    @Override
    <E> E getResult() {

        return (E) this.result;

    }

}
