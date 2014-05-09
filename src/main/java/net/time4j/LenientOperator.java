/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LenientOperator.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j;

import net.time4j.engine.ChronoOperator;


/**
 * <p>Spezial-Operator f&uuml;r das nachsichtige Setzen von Werten auf einem
 * {@code PlainTimestamp}. </p>
 *
 * @author  Meno Hochschild
 */
final class LenientOperator
    implements ChronoOperator<PlainTimestamp> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoOperator<PlainTimestamp> delegate;
    private final long value;

    //~ Konstruktoren -----------------------------------------------------

    LenientOperator(
        ChronoOperator<PlainTimestamp> delegate,
        Object value
    ) {
        super();

        this.delegate = delegate;
        this.value = Number.class.cast(value).longValue();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public PlainTimestamp apply(PlainTimestamp entity) {

        return this.delegate.apply(entity);

    }

    /**
     * <p>Liefert den Wert, der nachsichtig neu gesetzt werden soll. </p>
     *
     * @return  new value which shall be set in lenient mode
     */
    long getValue() {

        return this.value;

    }

}
