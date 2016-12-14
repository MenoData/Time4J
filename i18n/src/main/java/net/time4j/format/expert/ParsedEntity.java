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
    protected Chronology<T> getChronology() {

        throw new UnsupportedOperationException(
            "Parsed values do not have any chronology.");

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

    // called by format processors
    abstract void put(ChronoElement<?> element, int v);

    // called by format processors
    abstract void put(ChronoElement<?> element, Object v);

}
