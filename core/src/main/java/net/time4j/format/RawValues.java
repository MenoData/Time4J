/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RawValues.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoException;
import net.time4j.tz.TZID;


/**
 * <p>Stores any kind of raw values as {@code ChronoDisplay}. </p>
 *
 * <p><strong>Note:</strong>
 * This class is mutable and should only be used in a single thread. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Speichert einen beliebigen Satz von chronologischen Rohwerten. </p>
 *
 * <p><strong>Hinweis:</strong>
 * Diese Klasse ist <i>mutable</i> und sollte nur in einem einzigen Thread
 * verwendet werden. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
public class RawValues {

    //~ Instanzvariablen --------------------------------------------------

    private ChronoDisplay rawValues = new EmptyRawValues();

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Initially this instance has no defined raw values. </p>
     */
    /*[deutsch]
     * <p>Am Anfang hat diese Instanz keine definierten Rohwerte. </p>
     */
    public RawValues() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Consumes given chronological raw values. </p>
     *
     * @param   rawValues   raw chronological values
     * @since   3.0
     */
    /*[deutsch]
     * <p>Konsumiert die angegebenen Rohwerte. </p>
     *
     * @param   rawValues   raw chronological values
     * @since   3.0
     */
    public void accept(ChronoDisplay rawValues) {

        if (rawValues == null) {
            throw new NullPointerException("Missing raw values.");
        }

        this.rawValues = rawValues;

    }

    /**
     * <p>Yields the chronological raw values. </p>
     *
     * @return  raw chronological values, never {@code null}
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert die chronologischen Rohwerte. </p>
     *
     * @return  raw chronological values, never {@code null}
     * @since   3.0
     */
    public ChronoDisplay get() {

        return this.rawValues;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class EmptyRawValues
        implements ChronoDisplay {

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean contains(ChronoElement<?> element) {
            return false;
        }

        @Override
        public <V> V get(ChronoElement<V> element) {
            throw new ChronoException("Not supported:" + element.name());
        }

        @Override
        public int getInt(ChronoElement<Integer> element) {
            return Integer.MIN_VALUE;
        }

        @Override
        public <V> V getMinimum(ChronoElement<V> element) {
            throw new ChronoException("Not supported:" + element.name());
        }

        @Override
        public <V> V getMaximum(ChronoElement<V> element) {
            throw new ChronoException("Not supported:" + element.name());
        }

        @Override
        public boolean hasTimezone() {
            return false;
        }

        @Override
        public TZID getTimezone() {
            throw new ChronoException("Timezone does not exist.");
        }

        @Override
        public String toString() {
            return "raw-values={}";
        }

    }

}
