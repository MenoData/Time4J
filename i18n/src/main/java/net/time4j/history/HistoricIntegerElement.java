/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.base.GregorianDate;
import net.time4j.base.MathUtils;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.BasicElement;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.OutputContext;
import net.time4j.format.TextAccessor;
import net.time4j.format.TextWidth;
import net.time4j.history.internal.HistorizedElement;
import net.time4j.history.internal.StdHistoricalElement;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;


/**
 * <p>Allgemeines verstellbares chronologisches Element auf Integer-Basis. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class HistoricIntegerElement
    extends StdHistoricalElement
    implements HistorizedElement {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final int YEAR_OF_ERA_INDEX = 2;
    static final int MONTH_INDEX = 3;
    static final int DAY_OF_MONTH_INDEX = 4;
    static final int DAY_OF_YEAR_INDEX = 5;

    private static final long serialVersionUID = -6283098762945747308L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological history
     */
    private final ChronoHistory history;

    private transient final int index;

    //~ Konstruktoren -----------------------------------------------------

    HistoricIntegerElement(
        String name,
        char symbol,
        int defaultMin,
        int defaultMax,
        ChronoHistory history,
        int index
    ) {
        super(name, symbol, defaultMin, defaultMax);

        this.history = history;
        this.index = index;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes
    ) throws IOException {

        NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
        char zeroChar = attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue();
        this.print(context, buffer, attributes, numsys, zeroChar, 1, 9);

    }

    @Override
    public void print(
        ChronoDisplay context,
        Appendable buffer,
        AttributeQuery attributes,
        NumberSystem numsys,
        char zeroChar,
        int minDigits,
        int maxDigits
    ) throws IOException {

        if (this.index == DAY_OF_YEAR_INDEX) {
            buffer.append(String.valueOf(context.get(this.history.dayOfYear())));
            return;
        }

        HistoricDate date;

        if (context instanceof GregorianDate) {
            date = this.history.convert(PlainDate.from((GregorianDate) context));
        } else {
            date = context.get(this.history.date());
        }

        switch (this.index) {
            case YEAR_OF_ERA_INDEX:
                NewYearStrategy nys = this.history.getNewYearStrategy();
                HistoricEra era = date.getEra();
                int yearOfEra = date.getYearOfEra();
                String text = null;
                if (
                    !NewYearStrategy.DEFAULT.equals(nys)
                    && ((era.annoDomini(yearOfEra) >= 8) || (era.compareTo(HistoricEra.AD) > 0))
                ) {
                    int yearOfDisplay = date.getYearOfEra(nys);
                    if (yearOfDisplay != yearOfEra) { // dual dating
                        text = this.dual(numsys, yearOfDisplay, yearOfEra, minDigits);
                    }
                }
                if (text == null) { // standard case
                    if (numsys == NumberSystem.ARABIC) {
                        text = this.format(Integer.toString(yearOfEra), minDigits);
                    } else {
                        text = numsys.toNumeral(yearOfEra);
                    }
                }
                if (numsys == NumberSystem.ARABIC) {
                    if (zeroChar != '0') {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0, n = text.length(); i < n; i++) {
                            char c = text.charAt(i);
                            if (c >= '0' && c <= '9') {
                                int diff = zeroChar - '0';
                                sb.append((char) (c + diff));
                            } else {
                                sb.append(c); // - (minus) or / (slash)
                            }
                        }
                        text = sb.toString();
                    }
                    this.checkLength(text, maxDigits);
                }
                buffer.append(text);
                break;
            case MONTH_INDEX:
                OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
                buffer.append(this.monthAccessor(attributes, oc).print(Month.valueOf(date.getMonth())));
                break;
            case DAY_OF_MONTH_INDEX:
                buffer.append(String.valueOf(date.getDayOfMonth()));
                break;
        }

    }

    @Override
    public Integer parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes
    ) {

        return this.parse(text, status, attributes, null);

    }

    @Override
    public Integer parse(
        CharSequence text,
        ParsePosition status,
        AttributeQuery attributes,
        ChronoEntity<?> parsedResult
    ) {

        if (this.index == MONTH_INDEX) {
            int index = status.getIndex();
            OutputContext oc = attributes.get(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
            Month month = this.monthAccessor(attributes, oc).parse(text, status, Month.class, attributes);
            if ((month == null) && attributes.get(Attributes.PARSE_MULTIPLE_CONTEXT, Boolean.TRUE)) {
                status.setErrorIndex(-1);
                status.setIndex(index);
                oc = ((oc == OutputContext.FORMAT) ? OutputContext.STANDALONE : OutputContext.FORMAT);
                month = this.monthAccessor(attributes, oc).parse(text, status, Month.class, attributes);
            }
            if (month == null) {
                return null;
            } else {
                return Integer.valueOf(month.getValue());
            }
        }

        NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
        char zero = attributes.get(Attributes.ZERO_DIGIT, Character.valueOf('0')).charValue();
        Leniency leniency = (
            (numsys == NumberSystem.ARABIC)
                ? null // not used
                : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        int start = status.getIndex();
        int pos = start;
        int value = parseNum(numsys, text, pos, status, zero, leniency);
        pos = status.getIndex();

        if (
            (this.index == YEAR_OF_ERA_INDEX)
            && (pos > start)
            && (!NewYearStrategy.DEFAULT.equals(this.history.getNewYearStrategy()))
            && (pos < text.length())
            && (text.charAt(pos) == '/')
        ) {
            int slash = pos;
            int yoe = parseNum(numsys, text, pos + 1, status, zero, leniency);
            int test = status.getIndex();
            if (test == pos + 1) { // we will now stop consuming more chars and ignore yoe-part
                status.setIndex(pos);
            } else {
                pos = test;
                int yod = value;
                int ancient = this.getAncientYear(yod, yoe);
                if ((numsys == NumberSystem.ARABIC) && (ancient != Integer.MAX_VALUE)) {
                    value = ancient;
                    if (parsedResult != null) {
                        parsedResult.with(StdHistoricalElement.YEAR_OF_DISPLAY, yod);
                    }
                } else if (Math.abs(yoe - yod) <= 1) { // check for plausibility
                    value = yoe;
                } else { // now we have something else - let the formatter process the rest
                    value = yod;
                    pos = slash;
                    status.setIndex(pos);
                }
            }
        }

        if (pos == start) {
            status.setErrorIndex(start);
            return null;
        } else {
            return Integer.valueOf(value);
        }

    }

    @Override
    protected <T extends ChronoEntity<T>> ElementRule<T, Integer> derive(Chronology<T> chronology) {

        if (chronology.isRegistered(PlainDate.COMPONENT)) {
            return new Rule<T>(this.index, this.history);
        }

        return null;

    }

    @Override
    protected boolean isSingleton() {

        return false;

    }

    @Override
    protected boolean doEquals(BasicElement<?> obj) {

        return this.history.equals(((HistoricIntegerElement) obj).history);

    }

    private int getAncientYear(
        int yearOfDisplay,
        int yearOfEra
    ) {

        if ((yearOfEra < 0) || (yearOfEra >= 100) || (yearOfDisplay < 100)) {
            return Integer.MAX_VALUE;
        }

        if (
            (this.history.getEraPreference() != EraPreference.DEFAULT)
            || (yearOfDisplay < this.history.getGregorianCutOverDate().getYear()) // estimate
        ) {
            int factor = ((yearOfEra < 10) ? 10 : 100);

            if (Math.abs(yearOfEra - MathUtils.floorModulo(yearOfDisplay, factor)) <= 1) {
                return MathUtils.floorDivide(yearOfDisplay, factor) * factor + yearOfEra;
            }
        }

        return Integer.MAX_VALUE;

    }

    private String dual(
        NumberSystem numsys,
        int yearOfDisplay,
        int yearOfEra,
        int minDigits
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append(numsys.toNumeral(yearOfDisplay));
        sb.append('/');

        if (
            (numsys == NumberSystem.ARABIC)
            && (yearOfEra >= 100)
            && (MathUtils.floorDivide(yearOfDisplay, 100) == MathUtils.floorDivide(yearOfEra, 100))
        ) {
            int yoe2 = MathUtils.floorModulo(yearOfEra, 100);
            if (yoe2 < 10) {
                sb.append('0');
            }
            sb.append(yoe2);
        } else {
            sb.append(numsys.toNumeral(yearOfEra));
        }

        if (numsys == NumberSystem.ARABIC) {
            return this.format(sb.toString(), minDigits);
        } else {
            return sb.toString();
        }

    }

    private String format(
        String digits,
        int min
    ) {

        int len = digits.length();

        if (min <= len) {
            return digits; // optimization
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = min - len; i < n; i++) {
            sb.append('0');
        }

        sb.append(digits);
        return sb.toString();

    }

    private void checkLength(
        String digits,
        int max
    ) {

        int len = digits.length();

        if (len > max) {
            throw new IllegalArgumentException(
                "Element " + this.name()
                    + " cannot be printed as the formatted value " + digits
                    + " exceeds the maximum width of " + max + ".");
        }

    }

    private TextAccessor monthAccessor(
        AttributeQuery attributes,
        OutputContext outputContext
    ) {

        CalendarText cnames = CalendarText.getIsoInstance(attributes.get(Attributes.LANGUAGE, Locale.ROOT));
        TextWidth textWidth = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
        return cnames.getStdMonths(textWidth, outputContext);

    }

    private static int parseNum(
        NumberSystem numsys,
        CharSequence text,
        int offset,
        ParsePosition status,
        char zero,
        Leniency leniency
    ) {

        int value = 0;
        int pos = offset;

        if (numsys == NumberSystem.ARABIC) {
            boolean negative = false;

            if (text.charAt(pos) == '-') {
                negative = true;
                pos++;
            }

            for (int i = pos, n = Math.min(pos + 9, text.length()); i < n; i++) {
                int digit = text.charAt(i) - zero;
                if ((digit >= 0) && (digit <= 9)) {
                    value = value * 10 + digit;
                    pos++;
                } else {
                    break;
                }
            }

            if (negative) {
                if (pos == offset + 1) {
                    pos = offset;
                } else {
                    value = MathUtils.safeNegate(value);
                }
            }
        } else {
            int len = 0;

            for (int i = pos, n = text.length(); i < n; i++) {
                if (numsys.contains(text.charAt(i))) {
                    len++;
                } else {
                    break;
                }
            }

            if (len > 0) {
                value = numsys.toInteger(text.subSequence(pos, pos + len).toString(), leniency);
                pos += len;
            }
        }

        status.setIndex(pos);
        return value;

    }

    private Object readResolve() throws ObjectStreamException {

        String n = this.name();

        if (n.equals(this.history.yearOfEra().name())) {
            return this.history.yearOfEra();
        } else if (n.equals(this.history.month().name())) {
            return this.history.month();
        } else if (n.equals(this.history.dayOfMonth().name())) {
            return this.history.dayOfMonth();
        } else if (n.equals(this.history.dayOfYear().name())) {
            return this.history.dayOfYear();
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
                PlainDate iso = context.get(PlainDate.COMPONENT);
                HistoricDate date = this.history.convert(iso);

                switch (this.index) {
                    case YEAR_OF_ERA_INDEX:
                        return date.getYearOfEra();
                    case MONTH_INDEX:
                        return date.getMonth();
                    case DAY_OF_MONTH_INDEX:
                        return date.getDayOfMonth();
                    case DAY_OF_YEAR_INDEX:
                        long utc = iso.getDaysSinceEpochUTC();
                        int yoe = date.getYearOfEra(this.history.getNewYearStrategy());
                        HistoricDate newYear = this.history.getBeginOfYear(date.getEra(), yoe);
                        return (int) (utc - this.history.convert(newYear).getDaysSinceEpochUTC() + 1);
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
                    if (current.getEra().compareTo(HistoricEra.AD) <= 0) {
                        return Integer.valueOf(1);
                    } else {
                       return this.history.convert(PlainDate.axis().getMinimum()).getYearOfEra();
                    }
                }

                HistoricDate hd = this.adjust(context, 1);

                if (this.history.isValid(hd)) {
                    return Integer.valueOf(1);
                } else if (this.index == DAY_OF_YEAR_INDEX) {
                    throw new ChronoException("Historic New Year cannot be determined.");
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
                    case DAY_OF_YEAR_INDEX:
                        int yoe = current.getYearOfEra(this.history.getNewYearStrategy());
                        max = this.history.getLengthOfYear(current.getEra(), yoe);
                        if (max == -1) {
                            throw new ChronoException("Length of historic year undefined.");
                        }
                        return Integer.valueOf(max);
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
                case DAY_OF_YEAR_INDEX:
                    int yoe = hd.getYearOfEra(this.history.getNewYearStrategy());
                    HistoricDate newYear = this.history.getBeginOfYear(hd.getEra(), yoe);
                    int max = this.history.getLengthOfYear(hd.getEra(), yoe);
                    if (value == 1) {
                        result = newYear;
                    } else if ((value > 1) && (value <= max)) {
                        PlainDate date = this.history.convert(newYear);
                        date = date.plus(CalendarDays.of(value - 1));
                        result = this.history.convert(date);
                    } else {
                        throw new IllegalArgumentException("Out of range: " + value);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

            return result;

        }

    }

}
