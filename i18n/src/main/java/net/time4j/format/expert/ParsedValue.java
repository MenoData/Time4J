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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * <p>Definiert eine aktualisierbare Wertquelle mit normalerweise nur einem chronologischen Element,
 * dem ein beliebiger Wert ohne weitere Validierung zugeordnet sind. </p>
 *
 * @author  Meno Hochschild
 * @since   3.26/4.22
 */
class ParsedValue
    extends ParsedEntity<ParsedValue> {

    //~ Instanzvariablen --------------------------------------------------

    private ChronoElement<?> key;
    private Object value;

    // other element-value-pairs which don't exist by default but can be used in extensions or mergers
    private Map<ChronoElement<?>, Object> map = null;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard-Konstruktor.
     */
    ParsedValue() {
        super();

        this.key = null;
        this.value = null;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(ChronoElement<?> element) {

        if (element == null) {
            return false;
        }

        boolean found = element.equals(this.key);

        if (!found && (this.map != null)) {
            found = this.map.containsKey(element);
        }

        return found;

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        if (element.equals(this.key)) {
            return element.getType().cast(this.value);
        }

        Map<ChronoElement<?>, Object> m = this.map;

        if ((m != null) && m.containsKey(element)) {
            return element.getType().cast(m.get(element));
        }

        throw new ChronoException("No value found for: " + element.name());

    }

    @Override
    public int getInt(ChronoElement<Integer> element) {

        if (element.equals(this.key)) {
            return element.getType().cast(this.value).intValue();
        }

        Map<ChronoElement<?>, Object> m = this.map;

        if ((m != null) && m.containsKey(element)) {
            return element.getType().cast(m.get(element)).intValue();
        }

        return Integer.MIN_VALUE;

    }

    @Override
    public Set<ChronoElement<?>> getRegisteredElements() {

        Set<ChronoElement<?>> set = new HashSet<>();
        if (this.key != null) {
            set.add(this.key);
        }
        if (this.map != null) {
            set.addAll(this.map.keySet());
        }
        return Collections.unmodifiableSet(set);

    }

    // called by format processors
    void put(ChronoElement<?> element, int v) {

        if ((this.key == null) || element.equals(this.key)) {
            this.key = element;
            this.value = Integer.valueOf(v);
            return;
        }

        Map<ChronoElement<?>, Object> m = this.map;
        if (m == null) {
            m = new HashMap<>();
            this.map = m;
        }
        Object newValue = Integer.valueOf(v);
        m.put(element, newValue);

    }

    // called by format processors
    void put(ChronoElement<?> element, Object v) {

        if (v == null) { // removal
            if (element.equals(this.key)) {
                this.key = null;
                this.value = null;
            } else if (this.map != null) {
                this.map.remove(element);
                if (this.map.isEmpty()) {
                    this.map = null;
                }
            }
        } else if ((this.key == null) || element.equals(this.key)) {
            this.key = element;
            this.value = element.getType().cast(v);
        } else {
            Map<ChronoElement<?>, Object> m = this.map;
            if (m == null) {
                m = new HashMap<>();
                this.map = m;
            }
            m.put(element, v);
        }

    }

    // called in context of erraneous or-block
    void reset() {

        this.key = null;
        this.value = null;
        this.map = null;

    }

}
