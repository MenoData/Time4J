/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricalIntegerElement.java) is part of project Time4J.
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

package net.time4j.history;

import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.base.GregorianMath;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.NumericalElement;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;

import static net.time4j.format.CalendarText.ISO_CALENDAR_TYPE;


/**
 * <p>Allgemeines verstellbares chronologisches Element auf Integer-Basis. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class HistoricalIntegerElement
    extends BasicElement<Integer>
    implements NumericalElement<Integer>, TextElement<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int YEAR_OF_ERA_INDEX = 2;
    private static final int MONTH_INDEX = 3;
    private static final int DAY_OF_MONTH_INDEX = 4;

    private static final long serialVersionUID = -6283098762945747308L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological history
     */
    private final ChronoHistory history;

    private transient final int index;
    private transient final char symbol;
    private transient final Integer defaultMin;
    private transient final Integer defaultMax;

    //~ Konstruktoren -----------------------------------------------------

    private HistoricalIntegerElement(
        String name,
        char symbol,
        Integer defaultMin,
        Integer defaultMax,
        ChronoHistory history,
        int index
    ) {
        super(name);

        this.symbol = symbol;
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
        this.history = history;
        this.index = index;

    }

    //~ Methoden ----------------------------------------------------------

    // factory method
    static TextElement<Integer> forYearOfEra(ChronoHistory history) {

        return new HistoricalIntegerElement(
            "YEAR_OF_ERA",
            'y',
            Integer.valueOf(1),
            Integer.valueOf(GregorianMath.MAX_YEAR),
            history,
            YEAR_OF_ERA_INDEX
        );

    }

    // factory method
    static TextElement<Integer> forMonth(ChronoHistory history) {

        return new HistoricalIntegerElement(
            "HISTORIC_MONTH",
            'M',
            Integer.valueOf(1),
            Integer.valueOf(12),
            history,
            MONTH_INDEX
        );

    }

    // factory method
    static TextElement<Integer> forDayOfMonth(ChronoHistory history) {

        return new HistoricalIntegerElement(
            "HISTORIC_DAY_OF_MONTH",
            'd',
            Integer.valueOf(1),
            Integer.valueOf(31),
            history,
            DAY_OF_MONTH_INDEX
        );

    }

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

        return true;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public int numerical(Integer value) {

        return value.intValue();

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException {

        int v = context.get(this).intValue();

        switch (this.index) {
            case MONTH_INDEX:
                buffer.append(this.monthAccessor(attributes).print(Month.valueOf(v)));
                break;
            default:
                // numerical case
                buffer.append(String.valueOf(v));
        }

    }

    @Override
    public Integer parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        switch (this.index) {
            case MONTH_INDEX:
                Month month =
                    this.monthAccessor(attributes).parse(
                        text,
                        status,
                        Month.class,
                        attributes
                    );
                return Integer.valueOf(month.getValue());
            default:
                char zero = attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue();
                int value = 0;
                int pos = status.getIndex();
                for (int i = pos, n = text.length(); i < n; i++) {
                    int digit = text.charAt(i) - zero;
                    if ((digit >= 0) && (digit <= 9)) {
                        value = value * 10 + digit;
                        pos++;
                    } else {
                        break;
                    }
                }
                if (pos == status.getIndex()) {
                    status.setErrorIndex(pos);
                    return null;
                } else {
                    status.setIndex(pos);
                    return Integer.valueOf(value);
                }
        }

    }

    @Override
    protected <T extends ChronoEntity<T>> ElementRule<T, Integer> derive(Chronology<T> chronology) {

        if (chronology.isRegistered(PlainDate.COMPONENT)) {
            return new Rule<T>(this.index, this.history);
        }

        return null;

    }

    private TextAccessor monthAccessor(AttributeQuery attributes) {

        CalendarText cnames =
            CalendarText.getInstance(
                attributes.get(Attributes.CALENDAR_TYPE, ISO_CALENDAR_TYPE),
                attributes.get(Attributes.LANGUAGE, Locale.ROOT));

        TextWidth textWidth = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
        OutputContext outputContext = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);

        return cnames.getStdMonths(textWidth, outputContext);

    }

    private Object readResolve() throws ObjectStreamException {

        String n = this.name();

        if (n.equals(this.history.yearOfEra().name())) {
            return this.history.yearOfEra();
        } else if (n.equals(this.history.month().name())) {
            return this.history.month();
        } else if (n.equals(this.history.dayOfMonth().name())) {
            return this.history.dayOfMonth();
        } else {
            throw new InvalidObjectException("Unknown element: " + n);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class Rule<C extends ChronoEntity<C>>
        implements ElementRule<C, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;
        private final ChronoHistory history;

        //~ Konstruktoren -------------------------------------------------

        Rule(
            int index,
            ChronoHistory history
        ) {
            super();

            this.index = index;
            this.history = history;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(C context) {

            try {
                HistoricDate date = this.history.convert(context.get(PlainDate.COMPONENT));

                switch (this.index) {
                    case YEAR_OF_ERA_INDEX:
                        return date.getYearOfEra();
                    case MONTH_INDEX:
                        return date.getMonth();
                    case DAY_OF_MONTH_INDEX:
                        return date.getDayOfMonth();
                    default:
                        throw new UnsupportedOperationException("Unknown element index: " + this.index);
                }
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public Integer getMinimum(C context) {

            try {
                HistoricDate current = this.history.convert(context.get(PlainDate.COMPONENT));

                if (this.index == YEAR_OF_ERA_INDEX) {
                    return Integer.valueOf(1);
                }

                HistoricDate hd = this.adjust(context, 1);

                if (this.history.isValid(hd)) {
                    return Integer.valueOf(1);
                }


                List<CutOverEvent> events = this.history.getEvents();

                for (int i = events.size() - 1; i >= 0; i--) {
                    CutOverEvent event = events.get(i);

                    if (current.compareTo(event.dateAtCutOver) >= 0) {
                        hd = event.dateAtCutOver;
                        break;
                    }
                }

                int min = ((this.index == MONTH_INDEX) ? hd.getMonth() : hd.getDayOfMonth());
                return Integer.valueOf(min);
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public Integer getMaximum(C context) {

            try {
                HistoricDate current = this.history.convert(context.get(PlainDate.COMPONENT));
                HistoricDate hd;
                int max;

                switch (this.index) {
                    case YEAR_OF_ERA_INDEX:
                        if (current.getEra() == HistoricEra.BC) {
                            max = this.history.convert(PlainDate.axis().getMinimum()).getYearOfEra();
                        } else {
                            max = this.history.convert(PlainDate.axis().getMaximum()).getYearOfEra();
                        }
                        return Integer.valueOf(max);
                    case MONTH_INDEX:
                        max = 12;
                        hd = this.adjust(context, max);
                        break;
                    case DAY_OF_MONTH_INDEX:
                        max = this.history.getAlgorithm(current).getMaximumDayOfMonth(current);
                        hd = this.adjust(context, max);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown element index: " + this.index);
                }

                if (this.history.isValid(hd)) {
                    return Integer.valueOf(max);
                }

                List<CutOverEvent> events = this.history.getEvents();
                CutOverEvent candidate;

                for (int i = events.size() - 1; i >= 0; i--) {
                    CutOverEvent event = events.get(i);
                    candidate = event;

                    if (current.compareTo(event.dateAtCutOver) < 0) {
                        hd = candidate.dateBeforeCutOver;
                        break;
                    }
                }

                max = ((this.index == MONTH_INDEX) ? hd.getMonth() : hd.getDayOfMonth());
                return Integer.valueOf(max);
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public boolean isValid(
            C context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            try {
                HistoricDate newHD = this.adjust(context, value.intValue());
                return this.history.isValid(newHD);
            } catch (IllegalArgumentException iae) {
                return false;
            }

        }

        @Override
        public C withValue(
            C context,
            Integer value,
            boolean lenient
        ) {

            HistoricDate newHD = this.adjust(context, value.intValue());
            return context.with(PlainDate.COMPONENT, this.history.convert(newHD));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(C context) {

            throw new UnsupportedOperationException("Never called.");

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(C context) {

            throw new UnsupportedOperationException("Never called.");

        }

        private HistoricDate adjust(
            C context,
            int value
        ) {

            HistoricDate hd = this.history.convert(context.get(PlainDate.COMPONENT));
            HistoricDate result;

            switch (this.index) {
                case YEAR_OF_ERA_INDEX:
                    result = HistoricDate.of(hd.getEra(), value, hd.getMonth(), hd.getDayOfMonth());
                    result = this.history.adjustDayOfMonth(result);
                    break;
                case MONTH_INDEX:
                    result = HistoricDate.of(hd.getEra(), hd.getYearOfEra(), value, hd.getDayOfMonth());
                    result = this.history.adjustDayOfMonth(result);
                    break;
                case DAY_OF_MONTH_INDEX:
                    result = HistoricDate.of(hd.getEra(), hd.getYearOfEra(), hd.getMonth(), value);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

            return result;

        }

    }

}
