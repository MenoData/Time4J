/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntegerTimeElement.java) is part of project Time4J.
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
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoOperator;
import net.time4j.format.NumericalElement;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;


/**
 * <p>Allgemeines verstellbares Uhrzeitelement auf Integer-Basis. </p>
 *
 * @author      Meno Hochschild
 */
final class IntegerTimeElement
    extends AbstractTimeElement<Integer>
    implements ProportionalElement<Integer, PlainTime>,
               NumericalElement<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Element-Index */
    static final int CLOCK_HOUR_OF_AMPM = 1;
    /** Element-Index */
    static final int CLOCK_HOUR_OF_DAY = 2;
    /** Element-Index */
    static final int DIGITAL_HOUR_OF_AMPM = 3;
    /** Element-Index */
    static final int DIGITAL_HOUR_OF_DAY = 4;
    /** Element-Index */
    static final int ISO_HOUR = 5;
    /** Element-Index */
    static final int MINUTE_OF_HOUR = 6;
    /** Element-Index */
    static final int MINUTE_OF_DAY = 7;
    /** Element-Index */
    static final int SECOND_OF_MINUTE = 8;
    /** Element-Index */
    static final int SECOND_OF_DAY = 9;
    /** Element-Index */
    static final int MILLI_OF_SECOND = 10;
    /** Element-Index */
    static final int MICRO_OF_SECOND = 11;
    /** Element-Index */
    static final int NANO_OF_SECOND = 12;
    /** Element-Index */
    static final int MILLI_OF_DAY = 13;

    private static final long serialVersionUID = -1337148214680014674L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int index;
    private transient final Integer defaultMin;
    private transient final Integer defaultMax;
    private transient final char symbol;
    private transient final ChronoFunction<ChronoEntity<?>, BigDecimal> rf;

    //~ Konstruktoren -----------------------------------------------------

    private IntegerTimeElement(
        String name,
        int index,
        Integer defaultMin,
        Integer defaultMax,
        char symbol
    ) {
        super(name);

        this.index = index;
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
        this.symbol = symbol;

        boolean extendedRange;

        switch (index) {
            case ISO_HOUR:
            case MINUTE_OF_DAY:
            case SECOND_OF_DAY:
            case MILLI_OF_DAY:
                extendedRange = true;
                break;
            default:
                extendedRange = false;
        }

        this.rf = new ProportionalFunction(this, extendedRange);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Integer> getType() {

        return Integer.class;

    }

    @Override
    public char getSymbol() {

        return this.symbol;

    }

    @Override
    public Integer getDefaultMinimum() {

        return this.defaultMin;

    }

    @Override
    public Integer getDefaultMaximum() {

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
    public int numerical(Integer value) {

        return value.intValue();

    }

    @Override
    public ChronoFunction<ChronoEntity<?>, BigDecimal> ratio() {

        return this.rf;

    }

    @Override
    public ChronoOperator<PlainTime> roundedUp(int stepwidth) {

        return new RoundingOperator<PlainTime>(
            this,
            Boolean.TRUE,
            stepwidth
        );

    }

    @Override
    public ChronoOperator<PlainTime> roundedHalf(int stepwidth) {

        return new RoundingOperator<PlainTime>(
            this,
            null,
            stepwidth
        );

    }

    @Override
    public ChronoOperator<PlainTime> roundedDown(int stepwidth) {

        return new RoundingOperator<PlainTime>(
            this,
            Boolean.FALSE,
            stepwidth
        );

    }

    @Override
    protected boolean isSingleton() {

        return true; // exists only once per name in PlainTime

    }

    /**
     * <p>Liefert einen Zugriffsindex zur Optimierung der Elementsuche. </p>
     *
     * @return  int
     */
    int getIndex() {

        return this.index;

    }

    /**
     * <p>Erzeugt ein neues Uhrzeitelement. </p>
     *
     * @param   name        name of element
     * @param   index       index of element
     * @param   dmin        default minimum
     * @param   dmax        default maximum
     * @param   symbol      format symbol
     * @return  new element instance
     */
    static IntegerTimeElement createTimeElement(
        String name,
        int index,
        int dmin,
        int dmax,
        char symbol
    ) {

        return new IntegerTimeElement(
            name,
            index,
            Integer.valueOf(dmin),
            Integer.valueOf(dmax),
            symbol
        );

    }

    /**
     * <p>Erzeugt ein neues Uhrzeitelement auf Ziffernblattbasis. </p>
     *
     * @param   name        name of element
     * @param   has24Hours  has the element a length of 24 hours?
     * @return  new element instance
     */
    static IntegerTimeElement createClockElement(
        String name,
        boolean has24Hours
    ) {

        return new IntegerTimeElement(
            name,
            (has24Hours ? CLOCK_HOUR_OF_DAY : CLOCK_HOUR_OF_AMPM),
            Integer.valueOf(1),
            Integer.valueOf(has24Hours ? 24 : 12),
            (has24Hours ? 'k' : 'h')
        );

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
