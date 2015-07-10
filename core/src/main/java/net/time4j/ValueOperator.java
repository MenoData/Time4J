/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ValueOperator.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoOperator;


/**
 * <p>Spezial-Operator f&uuml;r das Setzen von Werten. </p>
 *
 * @author  Meno Hochschild
 * @param   <T> generic target type (usually {@code PlainTimestamp})
 */
final class ValueOperator<T>
    implements ChronoOperator<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoOperator<T> delegate;
    private final Object value;

    //~ Konstruktoren -----------------------------------------------------

    private ValueOperator(
        ChronoOperator<T> delegate,
        Object value
    ) {
        super();

        this.delegate = delegate;
        this.value = value;

    }

    //~ Methoden ----------------------------------------------------------

    static <T> ValueOperator of(
        ChronoOperator<T> delegate,
        Object value
    ) {

        return new ValueOperator<T>(delegate, value);

    }

    @Override
    public T apply(T entity) {

        return this.delegate.apply(entity);

    }

    /**
     * <p>Liefert den Wert, der neu gesetzt werden soll. </p>
     *
     * @return  new value which shall be set in standard or lenient mode
     */
    Object getValue() {

        return this.value;

    }

}
