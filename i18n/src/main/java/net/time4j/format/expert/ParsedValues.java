/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ParsedValues.java) is part of project Time4J.
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

import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.tz.TZID;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * <p>Definiert eine aktualisierbare Wertquelle mit chronologischen Elementen,
 * denen beliebige Werte ohne weitere Validierung zugeordnet sind. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
class ParsedValues
    extends ChronoEntity<ParsedValues> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final float LOAD_FACTOR = 0.75f;
    private static final int INT_PHI = 0x9E3779B9;

    private static final Set<ChronoElement<?>> INDEXED_ELEMENTS;

    static {
        Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();
        set.add(PlainDate.YEAR);
        set.add(PlainDate.MONTH_AS_NUMBER);
        set.add(PlainDate.DAY_OF_MONTH);
        set.add(PlainTime.DIGITAL_HOUR_OF_DAY);
        set.add(PlainTime.MINUTE_OF_HOUR);
        set.add(PlainTime.SECOND_OF_MINUTE);
        set.add(PlainTime.NANO_OF_SECOND);
        INDEXED_ELEMENTS = Collections.unmodifiableSet(set);
    }

    //~ Instanzvariablen --------------------------------------------------

    // standard mode
    private Object[] keys;
    private Object[] values;

    // index mode
    private Map<ChronoElement<?>, Object> map;

    private int[] ints; // index mode => date elements (year, month, day-of-month)
    private int len; // index mode => hour-of-day
    private int mask; // index mode => minute-of-hour
    private int threshold; // index mode => second-of-minute
    private int count; // index mode => nano-of-second

    private boolean duplicateKeysAllowed = false;
    private int position = -1;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard-Konstruktor.
     *
     * @param   expectedCountOfElements     How many elements to be expected?
     * @param   indexable                   Are only indexable elements used?
     */
    ParsedValues(
        int expectedCountOfElements,
        boolean indexable
    ) {
        super();

        if (indexable) {
            this.len = Integer.MIN_VALUE;
            this.mask = Integer.MIN_VALUE;
            this.threshold = Integer.MIN_VALUE;
            this.count = Integer.MIN_VALUE;
            this.keys = null;
            this.values = null;
            this.ints = new int[3];
            for (int i = 0; i < 3; i++) {
                this.ints[i] = Integer.MIN_VALUE;
            }
        } else {
            this.len = arraySize(expectedCountOfElements);
            this.mask = this.len - 1;
            this.threshold = maxFill(this.len);
            this.keys = new Object[this.len];
            this.values = null;
            this.ints = new int[this.len];
            this.count = 0;
        }

        this.map = null;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(ChronoElement<?> element) {

        if (element == null) {
            return false;
        }

        Object[] keys = this.keys;

        if (keys == null) {
            if (element == PlainDate.YEAR) {
                return (this.ints[0] != Integer.MIN_VALUE);
            } else if (element == PlainDate.MONTH_AS_NUMBER) {
                return (this.ints[1] != Integer.MIN_VALUE);
            } else if (element == PlainDate.DAY_OF_MONTH) {
                return (this.ints[2] != Integer.MIN_VALUE);
            } else if (element == PlainTime.DIGITAL_HOUR_OF_DAY) {
                return (this.len != Integer.MIN_VALUE);
            } else if (element == PlainTime.MINUTE_OF_HOUR) {
                return (this.mask != Integer.MIN_VALUE);
            } else if (element == PlainTime.SECOND_OF_MINUTE) {
                return (this.threshold != Integer.MIN_VALUE);
            } else if (element == PlainTime.NANO_OF_SECOND) {
                return (this.count != Integer.MIN_VALUE);
            } else {
                Map<ChronoElement<?>, Object> m = this.map;
                return ((m != null) && m.containsKey(element));
            }
        }

        Object current;
        int pos;

        if (((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            return false;
        }

        if (element.equals(current)) {
            return true;
        }

        while (true) {
            if (((current = keys[pos = ((pos + 1) & this.mask)]) == null)) {
                return false;
            }
            if (element.equals(current)) {
                return true;
            }
        }

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        Class<V> type = element.getType();

        if (type == Integer.class) {
            int value = this.getInt0(element);

            if (value == Integer.MIN_VALUE) {
                throw new ChronoException("No value found for: " + element.name());
            } else {
                return type.cast(Integer.valueOf(value));
            }
        }

        Object[] keys = this.keys;

        if (keys == null) {
            Map<ChronoElement<?>, Object> m = this.map;

            if ((m != null) && m.containsKey(element)) {
                return element.getType().cast(m.get(element));
            }

            throw new ChronoException("No value found for: " + element.name());
        }

        Object current;
        int pos;

        if ((this.values == null) || ((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            throw new ChronoException("No value found for: " + element.name());
        }

        if (element.equals(current)) {
            return type.cast(this.values[pos]);
        }

        while (true) {
            if (((current = keys[pos = ((pos + 1) & this.mask)]) == null)) {
                throw new ChronoException("No value found for: " + element.name());
            }
            if (element.equals(current)) {
                return type.cast(this.values[pos]);
            }
        }

    }

    @Override
    public int getInt(ChronoElement<Integer> element) {

        return this.getInt0(element);

    }

    @Override
    public <V> boolean isValid(
        ChronoElement<V> element,
        V value // optional
    ) {

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

        return true;

    }

    @Override
    public <V> ParsedValues with(
        ChronoElement<V> element,
        V value // optional
    ) {

        this.put(element, value);
        return this;

    }

    @Override
    public ParsedValues with(
        ChronoElement<Integer> element,
        int value
    ) {

        this.put(element, value);
        return this;

    }

    @Override
    public <V> V getMinimum(ChronoElement<V> element) {

        return element.getDefaultMinimum();

    }

    @Override
    public <V> V getMaximum(ChronoElement<V> element) {

        return element.getDefaultMaximum();

    }

    /**
     * <p>Vergleichsmethode. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ParsedValues) {
            ParsedValues that = (ParsedValues) obj;
            Set<ChronoElement<?>> e1 = this.getRegisteredElements();
            Set<ChronoElement<?>> e2 = that.getRegisteredElements();
            if (e1.size() != e2.size()) {
                return false;
            }
            for (ChronoElement<?> element : e1) {
                if (!e2.contains(element) || !this.get(element).equals(that.get(element))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        if (this.keys == null) {
            return Arrays.hashCode(this.ints) + 3 * this.len + 7 * this.mask + 11 * this.threshold + 31 * this.count;
        }

        return Arrays.hashCode(this.keys);

    }

    /**
     * <p>Gibt den internen Zustand in String-Form aus. </p>
     */
    @Override
    public String toString() {

        boolean first = true;
        StringBuilder sb = new StringBuilder(128);
        sb.append('{');

        for (ChronoElement<?> element : this.getRegisteredElements()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(element.name());
            sb.append('=');
            sb.append(this.get(element));
        }

        sb.append('}');
        return sb.toString();

    }

    @Override
    public Set<ChronoElement<?>> getRegisteredElements() {

        if (this.keys == null) {
            Set<ChronoElement<?>> set = new HashSet<ChronoElement<?>>();
            if (this.ints[0] != Integer.MIN_VALUE) {
                set.add(PlainDate.YEAR);
            }
            if (this.ints[1] != Integer.MIN_VALUE) {
                set.add(PlainDate.MONTH_AS_NUMBER);
            }
            if (this.ints[2] != Integer.MIN_VALUE) {
                set.add(PlainDate.DAY_OF_MONTH);
            }
            if (this.len != Integer.MIN_VALUE) {
                set.add(PlainTime.DIGITAL_HOUR_OF_DAY);
            }
            if (this.mask != Integer.MIN_VALUE) {
                set.add(PlainTime.MINUTE_OF_HOUR);
            }
            if (this.threshold != Integer.MIN_VALUE) {
                set.add(PlainTime.SECOND_OF_MINUTE);
            }
            if (this.count != Integer.MIN_VALUE) {
                set.add(PlainTime.NANO_OF_SECOND);
            }
            if (this.map != null) {
                set.addAll(this.map.keySet());
            }
            return Collections.unmodifiableSet(set);
        }

        return new KeySet();

    }

    @Override
    protected Chronology<ParsedValues> getChronology() {

        throw new UnsupportedOperationException(
            "Parsed values do not have any chronology.");

    }

    @Override
    public boolean hasTimezone() {

        return (
            this.contains(TimezoneElement.TIMEZONE_ID)
            || this.contains(TimezoneElement.TIMEZONE_OFFSET)
        );

    }

    @Override
    public TZID getTimezone() {

        Object tz = null;

        if (this.contains(TimezoneElement.TIMEZONE_ID)) {
            tz = this.get(TimezoneElement.TIMEZONE_ID);
        } else if (this.contains(TimezoneElement.TIMEZONE_OFFSET)) {
            tz = this.get(TimezoneElement.TIMEZONE_OFFSET);
        }

        if (tz instanceof TZID) {
            return TZID.class.cast(tz);
        } else {
            return super.getTimezone(); // throws exception
        }

    }

    // used in ChronoFormatter.parseElements()
    void setPosition(int position) {

        this.position = position;

    }

    // used in ChronoFormatter.parseElements()
    int getPosition() {

        return this.position;

    }

    // disables check of ambivalent values
    void setNoAmbivalentCheck() {

        this.duplicateKeysAllowed = true;

    }

    // gets the count of stored values
    int size() {

        if (this.keys == null) {
            int total = ((this.len == Integer.MIN_VALUE) ? 0 : 1);
            if (this.mask != Integer.MIN_VALUE){
                total++;
            }
            if (this.threshold != Integer.MIN_VALUE) {
                total++;
            }
            if (this.count != Integer.MIN_VALUE) {
                total++;
            }
            for (int i = 0; i < 3; i++) {
                if (this.ints[i] != Integer.MIN_VALUE) {
                    total++;
                }
            }
            if (this.map != null) {
                total += this.map.size();
            }
            return total;
        }

        return this.count;

    }

    // used by ChronoFormatter in order to determine the indexable-flag
    static boolean isIndexed(ChronoElement<?> element) {

        return INDEXED_ELEMENTS.contains(element);

    }

    // only used in ChronoFormatter.parseElements()
    void putAll(ParsedValues other) {

        if (this.keys == null) {
            int v = other.len;
            if (v != Integer.MIN_VALUE) {
                if ((this.len == Integer.MIN_VALUE) || this.duplicateKeysAllowed || (this.len == v)) {
                    this.len = v;
                } else {
                    throw new AmbivalentValueException(PlainTime.DIGITAL_HOUR_OF_DAY);
                }
            }
            v = other.mask;
            if (v != Integer.MIN_VALUE) {
                if ((this.mask == Integer.MIN_VALUE) || this.duplicateKeysAllowed || (this.mask == v)) {
                    this.mask = v;
                } else {
                    throw new AmbivalentValueException(PlainTime.MINUTE_OF_HOUR);
                }
            }
            v = other.threshold;
            if (v != Integer.MIN_VALUE) {
                if ((this.threshold == Integer.MIN_VALUE) || this.duplicateKeysAllowed || (this.threshold == v)) {
                    this.threshold = v;
                } else {
                    throw new AmbivalentValueException(PlainTime.SECOND_OF_MINUTE);
                }
            }
            v = other.count;
            if (v != Integer.MIN_VALUE) {
                if ((this.count == Integer.MIN_VALUE) || this.duplicateKeysAllowed || (this.count == v)) {
                    this.count = v;
                } else {
                    throw new AmbivalentValueException(PlainTime.NANO_OF_SECOND);
                }
            }
            for (int i = 0; i < 3; i++) {
                v = other.ints[i];
                if (v != Integer.MIN_VALUE) {
                    if ((this.ints[i] == Integer.MIN_VALUE) || this.duplicateKeysAllowed || (this.ints[i] == v)) {
                        this.ints[i] = v;
                    } else {
                        throw new AmbivalentValueException(getIndexedElement(i));
                    }
                }
            }
            Map<ChronoElement<?>, Object> m = other.map;
            if (m != null) {
                for (ChronoElement<?> e : m.keySet()) {
                    this.put(e, m.get(e));
                }
            }
            return;
        }

        Object[] elements = other.keys;
        Object current;

        for (int i = 0; i < elements.length; i++) {
            if ((current = elements[i]) != null) {
                ChronoElement<?> element = ChronoElement.class.cast(current);
                if (element.getType() == Integer.class) {
                    this.put(element, other.ints[i]);
                } else {
                    this.put(element, other.values[i]);
                }
            }
        }

    }

    // called by format processors
    void put(ChronoElement<?> element, int v) {

        int pos;
        Object current;
        Object[] keys = this.keys;

        if (keys == null) {
            if (element == PlainDate.YEAR) {
                if (this.duplicateKeysAllowed || (this.ints[0] == Integer.MIN_VALUE) || (this.ints[0] == v)) {
                    this.ints[0] = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else if (element == PlainDate.MONTH_AS_NUMBER) {
                if (this.duplicateKeysAllowed || (this.ints[1] == Integer.MIN_VALUE) || (this.ints[1] == v)) {
                    this.ints[1] = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else if (element == PlainDate.DAY_OF_MONTH) {
                if (this.duplicateKeysAllowed || (this.ints[2] == Integer.MIN_VALUE) || (this.ints[2] == v)) {
                    this.ints[2] = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else if (element == PlainTime.DIGITAL_HOUR_OF_DAY) {
                if (this.duplicateKeysAllowed || (this.len == Integer.MIN_VALUE) || (this.len == v)) {
                    this.len = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else if (element == PlainTime.MINUTE_OF_HOUR) {
                if (this.duplicateKeysAllowed || (this.mask == Integer.MIN_VALUE) || (this.mask == v)) {
                    this.mask = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else if (element == PlainTime.SECOND_OF_MINUTE) {
                if (this.duplicateKeysAllowed || (this.threshold == Integer.MIN_VALUE) || (this.threshold == v)) {
                    this.threshold = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else if (element == PlainTime.NANO_OF_SECOND) {
                if (this.duplicateKeysAllowed || (this.count == Integer.MIN_VALUE) || (this.count == v)) {
                    this.count = v;
                } else {
                    throw new AmbivalentValueException(element);
                }
            } else {
                Map<ChronoElement<?>, Object> m = this.map;
                if (m == null) {
                    m = new HashMap<ChronoElement<?>, Object>();
                    this.map = m;
                }
                Object newValue = Integer.valueOf(v);
                if (this.duplicateKeysAllowed || !m.containsKey(element) || newValue.equals(m.get(element))) {
                    m.put(element, newValue);
                    return;
                } else {
                    throw new AmbivalentValueException(element);
                }
            }
            return;
        }

        if (!((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            if (current.equals(element)) {
                if (this.duplicateKeysAllowed || (this.ints[pos] == v)) {
                    this.ints[pos] = v;
                    return;
                } else {
                    throw new AmbivalentValueException(element);
                }
            }
            while (!((current = keys[pos = (pos + 1) & this.mask]) == null)) {
                if (current.equals(element)) {
                    if (this.duplicateKeysAllowed || (this.ints[pos] == v)) {
                        this.ints[pos] = v;
                        return;
                    } else {
                        throw new AmbivalentValueException(element);
                    }
                }
            }
        }

        keys[pos] = element;
        this.ints[pos] = v;

        if (this.count++ >= this.threshold) {
            rehash(arraySize(this.count));
        }

    }

    // called by format processors
    void put(ChronoElement<?> element, Object v) {

        if (v == null) {
            this.remove(element);
            return;
        } else if (element.getType() == Integer.class) {
            this.put(element, Integer.class.cast(v).intValue());
            return;
        }

        int pos;
        Object current;
        Object[] keys = this.keys;

        if (keys == null) {
            Map<ChronoElement<?>, Object> m = this.map;
            if (m == null) {
                m = new HashMap<ChronoElement<?>, Object>();
                this.map = m;
            }
            if (this.duplicateKeysAllowed || !m.containsKey(element) || v.equals(m.get(element))) {
                m.put(element, v);
                return;
            } else {
                throw new AmbivalentValueException(element);
            }
        }

        if (this.values == null) {
            this.values = new Object[this.len];
        }

        if (!((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            if (current.equals(element)) {
                if (this.duplicateKeysAllowed || v.equals(this.values[pos])) {
                    this.values[pos] = v;
                    return;
                } else {
                    throw new AmbivalentValueException(element);
                }
            }
            while (!((current = keys[pos = (pos + 1) & this.mask]) == null)) {
                if (current.equals(element)) {
                    if (this.duplicateKeysAllowed || v.equals(this.values[pos])) {
                        this.values[pos] = v;
                        return;
                    } else {
                        throw new AmbivalentValueException(element);
                    }
                }
            }
        }

        keys[pos] = element;
        this.values[pos] = v;

        if (this.count++ >= this.threshold) {
            this.rehash(arraySize(this.count));
        }

    }

    // called in context of erraneous or-block
    void reset() {

        if (this.keys == null) {
            this.len = Integer.MIN_VALUE;
            this.mask = Integer.MIN_VALUE;
            this.threshold = Integer.MIN_VALUE;
            this.count = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                this.ints[i] = Integer.MIN_VALUE;
            }
            this.map = null;
        } else {
            this.keys = new Object[this.keys.length];
        }

        this.count = 0;

    }

    private int getInt0(ChronoElement<?> element) {

        Object[] keys = this.keys;

        if (keys == null) {
            if (element == PlainDate.YEAR) {
                return this.ints[0];
            } else if (element == PlainDate.MONTH_AS_NUMBER) {
                return this.ints[1];
            } else if (element == PlainDate.DAY_OF_MONTH) {
                return this.ints[2];
            } else if (element == PlainTime.DIGITAL_HOUR_OF_DAY) {
                return this.len;
            } else if (element == PlainTime.MINUTE_OF_HOUR) {
                return this.mask;
            } else if (element == PlainTime.SECOND_OF_MINUTE) {
                return this.threshold;
            } else if (element == PlainTime.NANO_OF_SECOND) {
                return this.count;
            }

            Map<ChronoElement<?>, Object> m = this.map;

            if ((m != null) && m.containsKey(element)) {
                return Integer.class.cast(m.get(element)).intValue();
            }

            return Integer.MIN_VALUE;
        }

        Object current;
        int pos;

        if (((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            return Integer.MIN_VALUE;
        }

        if (element.equals(current)) {
            return this.ints[pos];
        }

        while (true) {
            if (((current = keys[pos = ((pos + 1) & this.mask)]) == null)) {
                return Integer.MIN_VALUE;
            }
            if (element.equals(current)) {
                return this.ints[pos];
            }
        }

    }

    private void remove(Object element) {

        Object[] keys = this.keys;

        if (keys == null) {
            if (element == PlainDate.YEAR) {
                this.ints[0] = Integer.MIN_VALUE;
            } else if (element == PlainDate.MONTH_AS_NUMBER) {
                this.ints[1] = Integer.MIN_VALUE;
            } else if (element == PlainDate.DAY_OF_MONTH) {
                this.ints[2] = Integer.MIN_VALUE;
            } else if (element == PlainTime.DIGITAL_HOUR_OF_DAY) {
                this.len = Integer.MIN_VALUE;
            } else if (element == PlainTime.MINUTE_OF_HOUR) {
                this.mask = Integer.MIN_VALUE;
            } else if (element == PlainTime.SECOND_OF_MINUTE) {
                this.threshold = Integer.MIN_VALUE;
            } else if (element == PlainTime.NANO_OF_SECOND) {
                this.count = Integer.MIN_VALUE;
            } else {
                Map<ChronoElement<?>, Object> m = this.map;
                if (m != null) {
                    //noinspection SuspiciousMethodCalls
                    m.remove(element);
                }
            }
            return;
        }

        Object current;
        int pos;

        if (((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            return;
        }

        if (element.equals(current)) {
            this.removeEntry(pos);
            return;
        }

        while (true) {
            if (((current = keys[pos = ((pos + 1) & this.mask)]) == null)) {
                return;
            }
            if (element.equals(current)) {
                this.removeEntry(pos);
                return;
            }
        }

    }

    private void removeEntry(int pos) {

        this.count--;
        int last, slot;
        Object current;
        Object[] keys = this.keys;

        while (true) {
            pos = ((last = pos) + 1) & this.mask;
            while (true) {
                if ((current = keys[pos]) == null) {
                    keys[last] = null;
                    return;
                }
                slot = mix(current.hashCode()) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos ) {
                    break;
                }
                pos = (pos + 1) & this.mask;
            }
            keys[last] = current;
            if (this.values != null) {
                this.values[last] = this.values[pos];
            }
            this.ints[last] = this.ints[pos];
        }

    }

    private static int arraySize(int expectedCountOfElements) {

        return Math.max(2, nextPowerOfTwo((int) Math.ceil(expectedCountOfElements / LOAD_FACTOR)));

    }

    private static int nextPowerOfTwo(int x) {

        if (x == 0) {
            return 1;
        }

        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return (x | x >> 16) + 1;

    }

    private static int maxFill(int len) {

        return Math.min((int) Math.ceil(len * LOAD_FACTOR), len - 1);

    }

    private static int mix(int x) {

        int h = x * INT_PHI;
        return h ^ (h >>> 16);

    }

    private void rehash(int newLen) {

        Object[] keys = this.keys;
        Object[] values = this.values;
        int[] ints = this.ints;
        int mask = newLen - 1;
        Object[] newKeys = new Object[newLen];
        Object[] newValues = ((values == null) ? null : new Object[newLen]);
        int[] newInts = new int[newLen];
        int i = this.len;
        int pos;
        for (int j = 0, n = this.count; j < n; j++) {
            // look for occupied position i
            while (keys[--i] == null);
            // look for next free position pos
            if (!((newKeys[pos = mix(keys[i].hashCode()) & mask]) == null)) {
                while (!((newKeys[pos = (pos + 1) & mask]) == null));
            }
            // transfer data from i to pos
            newKeys[pos] = keys[i];
            if (values != null) {
                newValues[pos] = values[i];
            }
            newInts[pos] = ints[i];
        }
        this.len = newLen;
        this.mask = mask;
        this.threshold = maxFill(newLen);
        this.keys = newKeys;
        this.values = newValues;
        this.ints = newInts;

    }

    private static ChronoElement<Integer> getIndexedElement(int index) {
        switch (index) {
            case 0:
                return PlainDate.YEAR;
            case 1:
                return PlainDate.MONTH_AS_NUMBER;
            case 2:
                return PlainDate.DAY_OF_MONTH;
            case 3:
                return PlainTime.DIGITAL_HOUR_OF_DAY;
            case 4:
                return PlainTime.MINUTE_OF_HOUR;
            case 5:
                return PlainTime.SECOND_OF_MINUTE;
            case 6:
                return PlainTime.NANO_OF_SECOND;
            default:
                throw new IllegalStateException("No element index: " + index);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private class KeyIterator
        implements Iterator<ChronoElement<?>> {

        //~ Instanzvariablen ----------------------------------------------

        int pos = ParsedValues.this.len;
        int c = ParsedValues.this.count;

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean hasNext() {
            return (this.c > 0);
        }

        @Override
        public ChronoElement<?> next() {
            if (this.c > 0) {
                Object[] keys = ParsedValues.this.keys;
                while (--this.pos >= 0) {
                    if (!(keys[this.pos] == null)) {
                        this.c--;
                        return ChronoElement.class.cast(keys[this.pos]);
                    }
                }
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    private class KeySet
        extends AbstractSet<ChronoElement<?>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Iterator<ChronoElement<?>> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return ParsedValues.this.count;
        }
    }

}
