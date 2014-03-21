/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LongElement.java) is part of project Time4J.
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
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoOperator;
import net.time4j.format.NumericalElement;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;


/**
 * <p>Repr&auml;sentiert ein Uhrzeitelement vom long-Typ. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class LongElement
    extends AbstractValueElement<Long, PlainTime>
    implements ProportionalElement<Long, PlainTime>,
               NumericalElement<Long> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 5930990958663061693L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final Long defaultMin;
    private transient final Long defaultMax;
    private transient final char symbol;

    //~ Konstruktoren -----------------------------------------------------

    private LongElement(
        String name,
        long defaultMin,
        long defaultMax,
        char symbol
    ) {
        super(name);

        this.defaultMin = Long.valueOf(defaultMin);
        this.defaultMax = Long.valueOf(defaultMax);
        this.symbol = symbol;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Long> getType() {

        return Long.class;

    }

    @Override
    public char getSymbol() {

        return this.symbol;

    }

    @Override
    public Long getDefaultMinimum() {

        return this.defaultMin;

    }

    @Override
    public Long getDefaultMaximum() {

        return this.defaultMax;

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return true;

    }

    @Override
    public int numerical(Long value) {

        long v = value.longValue();

        if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
            return value.intValue();
        } else {
            throw new ArithmeticException("Numerical overflow: " + value);
        }

    }

    @Override
    public ChronoFunction<ChronoEntity<?>, BigDecimal> ratio() {

        return new ProportionalFunction(this, true);

    }


    @Override
    public ChronoOperator<PlainTime> roundedUp(int stepwidth) {

        return new LongRoundingOperator(
            this,
            Boolean.TRUE,
            stepwidth
        );

    }

    @Override
    public ChronoOperator<PlainTime> roundedHalf(int stepwidth) {

        return new LongRoundingOperator(
            this,
            null,
            stepwidth
        );

    }

    @Override
    public ChronoOperator<PlainTime> roundedDown(int stepwidth) {

        return new LongRoundingOperator(
            this,
            Boolean.FALSE,
            stepwidth
        );

    }

    @Override
    public ChronoOperator<PlainTime> rolledBy(long units) {

        return new OperatorDelegate<Long, PlainTime>(
            this,
            units,
            this.defaultMax);

    }

    /**
     * <p>Erzeugt ein neues Uhrzeitelement ohne Formatsymbol. </p>
     *
     * @param   name        name of element
     * @param   defaultMin  default minimum
     * @param   defaultMax  default maximum
     */
    static LongElement create(
        String name,
        long defaultMin,
        long defaultMax
    ) {

        return new LongElement(name, defaultMin, defaultMax, '\u0000');

    }

    private Object readResolve() throws ObjectStreamException {

        Object element = PlainTime.lookupElement(this.name());

        if (element == null) {
            throw new InvalidObjectException(this.name());
        } else {
            return element;
        }

    }

}
