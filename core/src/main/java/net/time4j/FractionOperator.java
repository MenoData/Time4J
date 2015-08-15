/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FractionOperator.java) is part of project Time4J.
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

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;

import static net.time4j.PlainTime.NANO_OF_SECOND;


/**
 * <p>Spezialoperator f&uuml;r Sekundenbruchteile. </p>
 *
 * @param       <T> generic target type of this operator
 * @author      Meno Hochschild
 */
final class FractionOperator<T extends ChronoEntity<T>>
    implements ChronoOperator<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int KILO = 1000;
    private static final int MIO = 1000000;

    //~ Instanzvariablen --------------------------------------------------

    private final char fraction;
    private final boolean up;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * @param   fraction    count of fractional digits (als char = 3, 6, 9)
     * @param   up          {@code true} if ceiling else {@code false}
     */
    FractionOperator(
        char fraction,
        boolean up
    ) {
        super();

        this.fraction = fraction;
        this.up = up;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public T apply(T entity) {

        if (this.fraction == '9') {
            return entity;
        }

        int nano = entity.get(NANO_OF_SECOND);
        int max = entity.getMaximum(NANO_OF_SECOND);

        switch (this.fraction) {
            case '3':
                nano = (nano / MIO) * MIO + (this.up ? 999999 : 0);
                return entity.with(NANO_OF_SECOND, Math.min(max, nano));
            case '6':
                nano = (nano / KILO) * KILO + (this.up ? 999 : 0);
                return entity.with(NANO_OF_SECOND, Math.min(max, nano));
            default:
                throw new UnsupportedOperationException(
                    "Unknown: " + this.fraction);
        }

    }

}
