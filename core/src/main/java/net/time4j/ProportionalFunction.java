/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ProportionalFunction.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * <p>Ermittelt eine Verh&auml;ltniszahl. </p>
 *
 * @author      Meno Hochschild
 */
final class ProportionalFunction
    implements ChronoFunction<ChronoEntity<?>, BigDecimal> {

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<? extends Number> element;
    private final boolean extendedRange;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Erzeugt eine neue Abfrage. </p>
     *
     * @param   element         element this query is related to
     * @param   extendedRange   is the range extended due to T24:00?
     */
    ProportionalFunction(
        ChronoElement<? extends Number> element,
        boolean extendedRange
    ) {
        super();

        this.element = element;
        this.extendedRange = extendedRange;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public BigDecimal apply(ChronoEntity<?> context) {

        long value = context.get(this.element).longValue();
        long min = context.getMinimum(this.element).longValue();
        long max = context.getMaximum(this.element).longValue();

        if (value > max) {
            value = max; // Schutz gegen Anomalien
        }

        if (value == min) {
            return BigDecimal.ZERO;
        }

        if (
            this.extendedRange
            && (context instanceof PlainTime)
            && !PlainTime.class.cast(context).hasReducedRange(this.element)
        ) {
            if (value == max) {
                return BigDecimal.ONE;
            }
            max--;
        }

        BigDecimal count = new BigDecimal(value - min).setScale(15);
        BigDecimal divisor = new BigDecimal(max - min + 1);

        return count.divide(divisor, RoundingMode.HALF_UP).stripTrailingZeros();

    }

}
