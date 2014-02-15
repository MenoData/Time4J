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

import net.time4j.engine.ChronoElement;
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

    private final ChronoElement<Integer> element;
    private final boolean up;
    private final double stepwidth;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor. </p>
     *
     * @param   element     referencing element
     * @param   up          {@code true} if ceiling mode else {@code false}
     * @param   stepwidth   controls limits of rounding
     */
    IntRoundingOperator(
        ChronoElement<Integer> element,
        boolean up,
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

        if (this.up) {
             nv = Math.ceil(value / this.stepwidth) * this.stepwidth;
        } else {
             nv = Math.floor(value / this.stepwidth) * this.stepwidth;
        }

        Integer num = Integer.valueOf((int) nv);

        int min = entity.getMinimum(this.element).intValue();

        if (min > num.intValue()) {
            return entity.with(this.element, entity.getMinimum(this.element));
        }

        int max = entity.getMaximum(this.element).intValue();

        if (max < num.intValue()) {
            return entity.with(this.element, entity.getMaximum(this.element));
        }

        return entity.with(this.element, num);

    }

}
