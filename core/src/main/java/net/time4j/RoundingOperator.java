/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RoundingOperator.java) is part of project Time4J.
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


/**
 * <p>Rundungsoperator. </p>
 *
 * @author      Meno Hochschild
 */
final class RoundingOperator<T extends ChronoEntity<T>>
    implements ChronoOperator<T> {

    //~ Instanzvariablen --------------------------------------------------

    private final ProportionalElement<?, T> element;
    private final Boolean up;
    private final double stepwidth;
    private final boolean longBased;

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
    RoundingOperator(
        final ProportionalElement<?, T> element,
        Boolean up,
        int stepwidth
    ) {
        super();

        this.element = element;
        this.up = up;
        this.stepwidth = stepwidth;
        this.longBased = element.getType().equals(Long.class);

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

        Number num;

        if (this.longBased) {
            num = Long.valueOf((long) nv);
        } else {
            num = Integer.valueOf((int) nv);
        }

        return entity.with(lenient(this.element, num));

    }

    private static <V extends Number, T extends ChronoEntity<T>>
    ChronoOperator<T> lenient(
        ProportionalElement<V, T> element,
        Number num
    ) {

        return element.setLenient(element.getType().cast(num));

    }

}
