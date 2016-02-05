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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.tz.TZID;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
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

    private static final float LOAD_FACTOR = 0.25f; // we have very small tables
    private static final int INT_PHI = 0x9E3779B9;

    //~ Instanzvariablen --------------------------------------------------

    private Object[] keys;
    private Object[] values;
    private int[] ints;

    private int mask;
    private int len;
    private int count;
    private int threshold;

    private boolean duplicateKeysAllowed = false;
    private int position = -1;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Standard-Konstruktor.
     *
     * @param   expectedCountOfElements     How many elements to be expected?
     */
    ParsedValues(int expectedCountOfElements) {
        super();

        this.len = arraySize(expectedCountOfElements);
        this.mask = this.len - 1;
        this.threshold = maxFill(this.len);
        this.keys = new Object[this.len + 1];
        this.values = new Object[this.len + 1];
        this.ints = new int[this.len + 1];
        this.count = 0;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(ChronoElement<?> element) {

        if (element == null) {
            return false;
        }

        Object[] keys = this.keys;
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
        Object current;
        int pos;

        if (((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
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
            return (
                (this.count == that.count)
                && Arrays.equals(this.keys, that.keys)
                && Arrays.equals(this.values, that.values)
                && Arrays.equals(this.ints, that.ints));
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

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

        return this.count;

    }

    // used in ChronoFormatter.parseElements()
    void putAll(ParsedValues other) {

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
    int put(ChronoElement<?> element, int v) {

        int pos;
        Object current;
        Object[] keys = this.keys;

        if (!((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            if (current.equals(element)) {
                int oldValue = this.ints[pos];
                if (this.duplicateKeysAllowed || (oldValue == v)) {
                    this.ints[pos] = v;
                    return oldValue;
                } else {
                    throw new AmbivalentValueException(element);
                }
            }
            while (!((current = keys[pos = (pos + 1) & this.mask]) == null)) {
                if (current.equals(element)) {
                    int oldValue = this.ints[pos];
                    if (this.duplicateKeysAllowed || (oldValue == v)) {
                        this.ints[pos] = v;
                        return oldValue;
                    } else {
                        throw new AmbivalentValueException(element);
                    }
                }
            }
        }

        keys[pos] = element;
        this.ints[pos] = v;

        if (this.count++ >= this.threshold) {
            rehash(arraySize(this.count + 1));
        }

        return Integer.MIN_VALUE;

    }

    // called by format processors
    Object put(ChronoElement<?> element, Object v) {

        if (v == null) {
            return this.remove(element);
        } else if (element.getType() == Integer.class) {
            return this.put(element, Integer.class.cast(v).intValue());
        }

        int pos;
        Object current;
        Object[] keys = this.keys;

        if (!((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            if (current.equals(element)) {
                Object oldValue = this.values[pos];
                if (this.duplicateKeysAllowed || v.equals(oldValue)) {
                    this.values[pos] = v;
                    return oldValue;
                } else {
                    throw new AmbivalentValueException(element);
                }
            }
            while (!((current = keys[pos = (pos + 1) & this.mask]) == null)) {
                if (current.equals(element)) {
                    Object oldValue = this.values[pos];
                    if (this.duplicateKeysAllowed || v.equals(oldValue)) {
                        this.values[pos] = v;
                        return oldValue;
                    } else {
                        throw new AmbivalentValueException(element);
                    }
                }
            }
        }

        keys[pos] = element;
        this.values[pos] = v;

        if (this.count++ >= this.threshold) {
            this.rehash(arraySize(this.count + 1));
        }

        return null;

    }

    private int getInt0(ChronoElement<?> element) {

        Object[] keys = this.keys;
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

    private Object remove(Object element) {

        Object[] keys = this.keys;
        Object current;
        int pos;

        if (((current = keys[pos = (mix(element.hashCode()) & this.mask)]) == null)) {
            return null;
        }

        if (element.equals(current)) {
            return this.removeEntry(pos);
        }

        while (true) {
            if (((current = keys[pos = ((pos + 1) & this.mask)]) == null)) {
                return null;
            }
            if (element.equals(current)) {
                return this.removeEntry(pos);
            }
        }

    }

    private Object removeEntry(int pos) {

        Object oldValue = this.values[pos];

        if (oldValue == null) {
            oldValue = Integer.valueOf(this.ints[pos]);
        }

        this.count--;
        int last, slot;
        Object current;
        Object[] keys = this.keys;

        while (true) {
            pos = ((last = pos) + 1) & this.mask;
            while (true) {
                if ((current = keys[pos]) == null) {
                    keys[last] = null;
                    return oldValue;
                }
                slot = mix(current.hashCode()) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos ) {
                    break;
                }
                pos = (pos + 1) & this.mask;
            }
            keys[last] = current;
            this.values[last] = this.values[pos];
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
        Object[] newKeys = new Object[newLen + 1];
        Object[] newValues = new Object[newLen + 1];
        int[] newInts = new int[newLen + 1];
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
            newValues[pos] = values[i];
            newInts[pos] = ints[i];
        }
        newValues[newLen] = values[this.len];
        newInts[newLen] = ints[this.len];
        this.len = newLen;
        this.mask = mask;
        this.threshold = maxFill(newLen);
        this.keys = newKeys;
        this.values = newValues;
        this.ints = newInts;

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
