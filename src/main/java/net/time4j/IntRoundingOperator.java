/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntRoundingOperator.java) is part of project Time4J.
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

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoOperator;


/**
 * <p>Rundungsoperator. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class IntRoundingOperator<T extends ChronoEntity<T>>
    implements ChronoOperator<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final ProportionalElement<Integer, T> element;
    private final Boolean up;
    private final double stepwidth;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor. </p>
     *
     * @param   element     referencing element
     * @param   up          {@code Boolean.TRUE} if ceiling mode,
     *                      {@code null} if half rounding
     *                      {@code Boolean.FALSE} if floor mode
     * @param   stepwidth   controls limits of rounding
     */
    IntRoundingOperator(
        ProportionalElement<Integer, T> element,
        Boolean up,
        int stepwidth
    ) {
        super();

        this.element = element;
        this.up = up;
        this.stepwidth = stepwidth;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public T apply(T entity) {

        double value = entity.get(this.element).doubleValue();
        double nv;

        if (this.up == null) {
            double high = Math.ceil(value / this.stepwidth) * this.stepwidth;
            double low = Math.floor(value / this.stepwidth) * this.stepwidth;
            nv = ((value - low < high - value) ? low : high);
        } else if (this.up.booleanValue()) {
            nv = Math.ceil(value / this.stepwidth) * this.stepwidth;
        } else {
            nv = Math.floor(value / this.stepwidth) * this.stepwidth;
        }

        Integer num = Integer.valueOf((int) nv);
        return entity.with(this.element.setLenient(num));

    }

}
