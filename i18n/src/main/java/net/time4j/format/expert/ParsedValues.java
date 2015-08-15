/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import java.io.Serializable;
import java.util.Map;


/**
 * <p>Definiert eine aktualisierbare Wertquelle mit chronologischen Elementen,
 * denen beliebige Werte ohne weitere Validierung zugeordnet sind. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
class ParsedValues
    extends ChronoEntity<ParsedValues>
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 6400471321200669320L;

    //~ Instanzvariablen --------------------------------------------------

    private final NonAmbivalentMap map;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Leere Menge.
     */
    ParsedValues() {
        super();

        this.map = new NonAmbivalentMap();

    }

    /**
     * <p>Standard-Konstruktor. </p>
     *
     * @param   map     zu umh&uuml;llende Map
     */
    ParsedValues(NonAmbivalentMap map) {
        super();

        map.remove(null);
        this.map = map;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(ChronoElement<?> element) {

        return this.map.containsKey(element);

    }

    @Override
    public <V> V get(ChronoElement<V> element) {

        V value = element.getType().cast(this.map.get(element));

        if (value == null) {
            throw new ChronoException("No value found for: " + element.name());
        } else {
            return value;
        }

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

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        } else if (value == null) {
            this.map.remove(element);
        } else {
            this.map.put(element, value);
        }

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
            return this.map.equals(that.map);
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        return this.map.hashCode();

    }

    /**
     * <p>Gibt den internen Zustand in String-Form aus. </p>
     */
    @Override
    public String toString() {

        boolean first = true;
        StringBuilder sb = new StringBuilder(128);
        sb.append('{');

        for (ChronoElement<?> key : this.map.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(key.name());
            sb.append('=');
            sb.append(this.map.get(key));
        }

        sb.append('}');
        return sb.toString();

    }

    @Override
    protected Chronology<ParsedValues> getChronology() {

        throw new UnsupportedOperationException(
            "Parsed values do not have any chronology.");

    }

    @Override
    public boolean hasTimezone() {

        return this.map.containsKey(TimezoneElement.TIMEZONE_ID);

    }

    @Override
    public TZID getTimezone() {

        Object tz = this.map.get(TimezoneElement.TIMEZONE_ID);

        if (tz instanceof TZID) {
            return TZID.class.cast(tz);
        } else {
            return super.getTimezone(); // throws exception
        }

    }

    /**
     * <p>Liefert alle enthaltenen Elemente. </p>
     *
     * @return  internal {@code Map}
     */
    Map<ChronoElement<?>, Object> toMap() {

        return this.map;

    }

    /**
     * <p>Schaltet die Pr&uuml;fung von ambivalenten Werten ab. </p>
     */
    void setNoAmbivalentCheck() {

        this.map.setChecking(false);

    }

}
