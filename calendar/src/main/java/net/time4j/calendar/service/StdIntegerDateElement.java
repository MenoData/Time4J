/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StdIntegerDateElement.java) is part of project Time4J.
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

package net.time4j.calendar.service;

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;
import net.time4j.format.NumericalElement;


/**
 * <p>General integer-based date element. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Allgemeines Integer-basiertes Datumselement. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public class StdIntegerDateElement<T extends ChronoEntity<T>>
    extends StdDateElement<Integer, T>
    implements NumericalElement<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4975173343610190782L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int min;
    private transient final int max;
    private transient final ChronoOperator<T> decrementor;
    private transient final ChronoOperator<T> incrementor;

    //~ Konstruktoren -----------------------------------------------------

    public StdIntegerDateElement(
        String name,
        Class<T> chrono,
        int min,
        int max,
        char symbol
    ) {
        super(name, chrono, symbol, true);

        this.min = min;
        this.max = max;
        this.decrementor = null;
        this.incrementor = null;

    }

    public StdIntegerDateElement(
        String name,
        Class<T> chrono,
        int min,
        int max,
        char symbol,
        ChronoOperator<T> decrementor,
        ChronoOperator<T> incrementor
    ) {
        super(name, chrono, symbol, false);

        this.min = min;
        this.max = max;
        this.decrementor = decrementor;
        this.incrementor = incrementor;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Integer> getType() {

        return Integer.class;

    }

    @Override
    public Integer getDefaultMinimum() {

        return Integer.valueOf(this.min);

    }

    @Override
    public Integer getDefaultMaximum() {

        return Integer.valueOf(this.max);

    }

    @Override
    public int numerical(Integer value) {

        return value.intValue();

    }

    @Override
    public ChronoOperator<T> decremented() {

        if (this.decrementor != null) {
            return this.decrementor;
        }

        return super.decremented();

    }

    @Override
    public ChronoOperator<T> incremented() {

        if (this.incrementor != null) {
            return this.incrementor;
        }

        return super.incremented();

    }

}
