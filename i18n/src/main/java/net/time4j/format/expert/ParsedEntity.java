/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ParsedEntity.java) is part of project Time4J.
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
import net.time4j.engine.Chronology;
import net.time4j.tz.TZID;

import java.util.Set;


/**
 * <p>Definiert eine aktualisierbare Wertquelle mit einem oder mehreren chronologischen Elementen,
 * denen ein beliebiger Wert ohne weitere Validierung zugeordnet sind. </p>
 *
 * @author  Meno Hochschild
 * @since   3.26/4.22
 */
abstract class ParsedEntity<T extends ParsedEntity<T>>
    extends ChronoEntity<T> {

    //~ Methoden ----------------------------------------------------------

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

    @SuppressWarnings("unchecked")
    @Override
    public <V> T with(
        ChronoElement<V> element,
        V value // optional
    ) {

        this.put(element, value);
        return (T) this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public T with(
        ChronoElement<Integer> element,
        int value
    ) {

        this.put(element, value);
        return (T) this;

    }

    @Override
    public <V> V getMinimum(ChronoElement<V> element) {

        return element.getDefaultMinimum();

    }

    @Override
    public <V> V getMaximum(ChronoElement<V> element) {

        return element.getDefaultMaximum();

    }

    @Override
    public final boolean hasTimezone() {

        return (
            this.contains(TimezoneElement.TIMEZONE_ID)
            || this.contains(TimezoneElement.TIMEZONE_OFFSET)
        );

    }

    @Override
    public final TZID getTimezone() {

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

    /**
     * <p>Vergleichsmethode. </p>
     */
    @Override
    public final boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ParsedEntity) {
            ParsedEntity<?> that = (ParsedEntity<?>) obj;
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
            Object o1 = this.getResult();
            Object o2 = that.getResult();
            if (o1 == null) {
                return (o2 == null);
            } else {
                return o1.equals(o2);
            }
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public final int hashCode() {

        int hash = this.getRegisteredElements().hashCode();
        Object result = this.getResult();
        if (result != null) {
            hash += 31 * result.hashCode();
        }
        return hash;

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
        Object result = this.getResult();
        if (result != null) {
            sb.append(">>>result=");
            sb.append(result);
        }
        return sb.toString();

    }

    @Override
    protected final Chronology<T> getChronology() {

        throw new UnsupportedOperationException(
            "Parsed values do not have any chronology.");

    }

    // called by format processors
    abstract void put(ChronoElement<?> element, int v);

    // called by format processors
    abstract void put(ChronoElement<?> element, Object v);

    // concerns processors which do the result evaluation themselves
    abstract void setResult(Object entity);

    // concerns processors which do the result evaluation themselves
    abstract <E> E getResult();

}
