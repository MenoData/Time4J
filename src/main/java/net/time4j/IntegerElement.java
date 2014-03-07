/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IntegerElement.java) is part of project Time4J.
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
 * <p>Allgemeines verstellbares chronologisches Element auf Integer-Basis. </p>
 *
 * @param       <T> generic target type for a {@code ChronoOperator}
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class IntegerElement<T extends ChronoEntity<T>>
    extends AbstractValueElement<Integer, T>
    implements ProportionalElement<Integer, T>,
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
    /** Element-Index */
    static final int YEAR = 14;
    /** Element-Index */
    static final int MONTH = 15;
    /** Element-Index */
    static final int DAY_OF_MONTH = 16;
    /** Element-Index */
    static final int DAY_OF_YEAR = 17;
    /** Element-Index */
    static final int DAY_OF_QUARTER = 18;

    private static final int DATE_KIND = 0;
    private static final int TIME_KIND = 1;
    private static final int CLOCK_KIND = 2;
    private static final long serialVersionUID = -1337148214680014674L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int index;
    private transient final Integer defaultMin;
    private transient final Integer defaultMax;
    private transient final int kind;
    private transient final char symbol;

    //~ Konstruktoren -----------------------------------------------------

    private IntegerElement(
        String name,
        int index,
        Integer defaultMin,
        Integer defaultMax,
        int kind,
        char symbol
    ) {
        super(name);

        this.index = index;
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
        this.kind = kind;
        this.symbol = symbol;

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

        return (this.kind == DATE_KIND);

    }

    @Override
    public boolean isTimeElement() {

        return !this.isDateElement();

    }

    @Override
    public int numerical(Integer value) {

        return value.intValue();

    }

    @Override
    public ChronoFunction<ChronoEntity<?>, BigDecimal> ratio() {

        boolean closedRange;

        switch (this.index) {
            case ISO_HOUR:
            case MINUTE_OF_DAY:
            case SECOND_OF_DAY:
            case MILLI_OF_DAY:
                closedRange = true;
                break;
            default:
                closedRange = false;
        }

        return new ProportionalFunction(this, closedRange);

    }

    @Override
    public ChronoOperator<T> roundedUp(int stepwidth) {

        return new IntRoundingOperator<T>(
            this,
            true,
            stepwidth
        );

    }

    @Override
    public ChronoOperator<T> roundedDown(int stepwidth) {

        return new IntRoundingOperator<T>(
            this,
            false,
            stepwidth
        );

    }

    @Override
    protected Integer getRollMax() {

        switch (this.index) {
            case ISO_HOUR:
            case MINUTE_OF_DAY:
            case SECOND_OF_DAY:
            case MILLI_OF_DAY:
                return this.defaultMax;
            default:
                return null;
        }

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
     * <p>Erzeugt ein neues Datumselement. </p>
     *
     * @param   name        name of element
     * @param   index       index of element
     * @param   dmin        default minimum
     * @param   dmax        default maximum
     * @param   symbol      format symbol
     * @return  new element instance
     */
    static <T extends ChronoEntity<T>> IntegerElement<T> createDateElement(
        String name,
        int index,
        int dmin,
        int dmax,
        char symbol
    ) {

        return new IntegerElement<T>(
            name,
            index,
            Integer.valueOf(dmin),
            Integer.valueOf(dmax),
            DATE_KIND,
            symbol
        );

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
    static <T extends ChronoEntity<T>> IntegerElement<T> createTimeElement(
        String name,
        int index,
        int dmin,
        int dmax,
        char symbol
    ) {

        return new IntegerElement<T>(
            name,
            index,
            Integer.valueOf(dmin),
            Integer.valueOf(dmax),
            TIME_KIND,
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
    static <T extends ChronoEntity<T>> IntegerElement<T> createClockElement(
        String name,
        boolean has24Hours
    ) {

        return new IntegerElement<T>(
            name,
            (has24Hours ? CLOCK_HOUR_OF_DAY : CLOCK_HOUR_OF_AMPM),
            Integer.valueOf(1),
            Integer.valueOf(has24Hours ? 24 : 12),
            CLOCK_KIND,
            (has24Hours ? 'k' : 'h')
        );

    }

    private Object readResolve() throws ObjectStreamException {

        Object element;

        if (this.isDateElement()) {
            element = PlainDate.lookupElement(this.name());
        } else {
            element = PlainTime.lookupElement(this.name());
        }

        if (element == null) {
            throw new InvalidObjectException(this.name());
        } else {
            return element;
        }

    }

}
