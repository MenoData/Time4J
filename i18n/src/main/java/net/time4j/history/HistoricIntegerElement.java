/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistoricIntegerElement.java) is part of project Time4J.
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
import net.time4j.format.internal.DualFormatElement;
import net.time4j.history.internal.StdHistoricalElement;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;

import static net.time4j.history.YearDefinition.DUAL_DATING;


/**
 * <p>Allgemeines verstellbares chronologisches Element auf Integer-Basis. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class HistoricIntegerElement
    extends StdHistoricalElement
    implements DualFormatElement {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final int YEAR_OF_ERA_INDEX = 2;
    static final int MONTH_INDEX = 3;
    static final int DAY_OF_MONTH_INDEX = 4;
    static final int DAY_OF_YEAR_INDEX = 5;
    static final int YEAR_AFTER_INDEX = 6;
    static final int YEAR_BEFORE_INDEX = 7;
    static final int CENTURY_INDEX = 8;

    private static final long serialVersionUID = -6283098762945747308L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  associated chronological history
     */
    private final ChronoHistory history;

    private transient final int index;

    //~ Konstruktoren -----------------------------------------------------

    HistoricIntegerElement(
        char symbol,
        int defaultMin,
        int defaultMax,
        ChronoHistory history,
        int index
    ) {
        super(toName(index), symbol, defaultMin, defaultMax);

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
        char zeroChar = (
            attributes.contains(Attributes.ZERO_DIGIT)
            ? attributes.get(Attributes.ZERO_DIGIT).charValue()
            : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
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
                int yearOfEra = date.getYearOfEra();
                String text = null;
                if (!NewYearStrategy.DEFAULT.equals(nys)) {
                    int yearOfDisplay = date.getYearOfEra(nys);
                    if (yearOfDisplay != yearOfEra) {
                        if (attributes.get(ChronoHistory.YEAR_DEFINITION, DUAL_DATING) == DUAL_DATING) {
                            text = this.dual(numsys, zeroChar, yearOfDisplay, yearOfEra, minDigits);
                        } else {
                            yearOfEra = yearOfDisplay;
                        }
                    }
                }
                if (text == null) { // no dual format
                    if (numsys.isDecimal()) {
                        text = pad(numsys.toNumeral(yearOfEra), minDigits, zeroChar);
                    } else {
                        text = numsys.toNumeral(yearOfEra);
                    }
                }
                if (numsys.isDecimal()) {
                    char defaultZeroChar = numsys.getDigits().charAt(0);
                    if (zeroChar != defaultZeroChar) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0, n = text.length(); i < n; i++) {
                            char c = text.charAt(i);
                            if (numsys.contains(c)) {
                                int diff = zeroChar - defaultZeroChar;
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
            default:
                throw new ChronoException("Not printable as text: " + this.name());
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
        } else if (
            (this.index == YEAR_AFTER_INDEX)
            || (this.index == YEAR_BEFORE_INDEX)
            || (this.index == CENTURY_INDEX)
        ) {
            throw new ChronoException("Not parseable as text element: " + this.name());
        }

        NumberSystem numsys = attributes.get(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC);
        char zeroChar = (
            attributes.contains(Attributes.ZERO_DIGIT)
                ? attributes.get(Attributes.ZERO_DIGIT).charValue()
                : (numsys.isDecimal() ? numsys.getDigits().charAt(0) : '0'));
        Leniency leniency = (numsys.isDecimal() ? Leniency.SMART : attributes.get(Attributes.LENIENCY, Leniency.SMART));
        int start = status.getIndex();
        int pos = start;
        int value = parseNum(numsys, zeroChar, text, pos, status, leniency);
        pos = status.getIndex();

        if ( // dual date check
            (this.index == YEAR_OF_ERA_INDEX)
            && (pos > start)
            && (!NewYearStrategy.DEFAULT.equals(this.history.getNewYearStrategy()))
            && (pos < text.length())
            && (text.charAt(pos) == '/')
            && (attributes.get(ChronoHistory.YEAR_DEFINITION, DUAL_DATING) == DUAL_DATING)
        ) {
            int slash = pos;
            int yoe = parseNum(numsys, zeroChar, text, pos + 1, status, leniency);
            int test = status.getIndex();
            if (test == pos + 1) { // we will now stop consuming more chars and ignore yoe-part
                status.setIndex(pos);
            } else {
                pos = test;
                int yod = value;
                int maxDeviation = (
                    (this.history.getNewYearStrategy().rule(HistoricEra.AD, yod) == NewYearRule.CALCULUS_PISANUS)
                    ? 2 : 1);
                int ancient = this.getAncientYear(yod, yoe, maxDeviation);
                if (numsys.isDecimal() && (ancient != Integer.MAX_VALUE)) {
                    value = ancient;
                    if (parsedResult != null) {
                        parsedResult.with(StdHistoricalElement.YEAR_OF_DISPLAY, yod);
                    }
                } else if (Math.abs(yoe - yod) <= maxDeviation) { // plausibility check
                    value = yoe;
                    if (parsedResult != null) {
                        parsedResult.with(StdHistoricalElement.YEAR_OF_DISPLAY, yod);
                    }
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
        int yearOfEra,
        int maxDeviation
    ) {

        if ((yearOfEra >= 0) && (yearOfEra < 100) && (yearOfDisplay >= 100)) {
            int factor = ((yearOfEra < 10) ? 10 : 100);

            if (Math.abs(yearOfEra - MathUtils.floorModulo(yearOfDisplay, factor)) <= maxDeviation) {
                return MathUtils.floorDivide(yearOfDisplay, factor) * factor + yearOfEra;
            }
        }

        return Integer.MAX_VALUE;

    }

    private String dual(
        NumberSystem numsys,
        char zeroChar,
        int yearOfDisplay,
        int yearOfEra,
        int minDigits
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append(numsys.toNumeral(yearOfDisplay));
        sb.append('/');

        if (
            numsys.isDecimal()
            && (yearOfEra >= 100)
            && (MathUtils.floorDivide(yearOfDisplay, 100) == MathUtils.floorDivide(yearOfEra, 100))
        ) {
            int yoe2 = MathUtils.floorModulo(yearOfEra, 100);
            if (yoe2 < 10) {
                sb.append(zeroChar);
            }
            sb.append(numsys.toNumeral(yoe2));
        } else {
            sb.append(numsys.toNumeral(yearOfEra));
        }

        if (numsys.isDecimal()) {
            return pad(sb.toString(), minDigits, zeroChar);
        } else {
            return sb.toString();
        }

    }

    private static String pad(
        String digits,
        int min,
        char zeroChar
    ) {

        int len = digits.length();

        if (min <= len) {
            return digits; // optimization
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = min - len; i < n; i++) {
            sb.append(zeroChar);
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
        char zeroChar,
        CharSequence text,
        int offset,
        ParsePosition status,
        Leniency leniency
    ) {

        int value = 0;
        int pos = offset;

        if (numsys.isDecimal()) {
            boolean negative = false;

            if ((numsys == NumberSystem.ARABIC) && (text.charAt(pos) == '-')) {
                negative = true;
                pos++;
            }

            char defaultZeroChar = (leniency.isStrict() ? '\u0000' : numsys.getDigits().charAt(0));

            for (int i = pos, n = Math.min(pos + 9, text.length()); i < n; i++) {
                int digit = text.charAt(i) - zeroChar;
                if ((digit >= 0) && (digit <= 9)) {
                    value = value * 10 + digit;
                    pos++;
                } else if ((defaultZeroChar != '\u0000') && (zeroChar != defaultZeroChar)) { // smart or lax mode
                    digit = text.charAt(i) - defaultZeroChar;
                    if ((digit >= 0) && (digit <= 9)) {
                        zeroChar = defaultZeroChar;
                        value = value * 10 + digit;
                        pos++;
                    } else {
                        break;
                    }
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

    private static String toName(int index) {

        switch (index) {
            case YEAR_OF_ERA_INDEX:
                return "YEAR_OF_ERA";
            case MONTH_INDEX:
                return "HISTORIC_MONTH";
            case DAY_OF_MONTH_INDEX:
                return "HISTORIC_DAY_OF_MONTH";
            case DAY_OF_YEAR_INDEX:
                return "HISTORIC_DAY_OF_YEAR";
            case YEAR_AFTER_INDEX:
                return "YEAR_AFTER";
            case YEAR_BEFORE_INDEX:
                return "YEAR_BEFORE";
            case CENTURY_INDEX:
                return "CENTURY_OF_ERA";
            default:
                throw new UnsupportedOperationException("Unknown element index: " + index);
        }

    }

    private Object readResolve() throws ObjectStreamException {

        String n = this.name();

        if (n.equals("YEAR_OF_ERA")) {
            return this.history.yearOfEra();
        } else if (n.equals("HISTORIC_MONTH")) {
            return this.history.month();
        } else if (n.equals("HISTORIC_DAY_OF_MONTH")) {
            return this.history.dayOfMonth();
        } else if (n.equals("HISTORIC_DAY_OF_YEAR")) {
            return this.history.dayOfYear();
        } else if (n.equals("YEAR_AFTER")) {
            return this.history.yearOfEra(YearDefinition.AFTER_NEW_YEAR);
        } else if (n.equals("YEAR_BEFORE")) {
            return this.history.yearOfEra(YearDefinition.BEFORE_NEW_YEAR);
        } else if (n.equals("CENTURY_OF_ERA")) {
            return this.history.centuryOfEra();
        } else {
            throw new InvalidObjectException("Unknown element: " + n);
        }

    }

    @Override
    public int numerical(Integer value) {

        return value.intValue();

    }

    @Override
    public int parseToInt(
        Integer value,
        ChronoDisplay context,
        AttributeQuery attributes
    ) {

        return value.intValue();

    }

    @Override
    public boolean parseFromInt(ChronoEntity<?> entity, int value) {

        entity.with(this, Integer.valueOf(value));
        return true;

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
                int value;

                switch (this.index) {
                    case YEAR_OF_ERA_INDEX:
                        value = date.getYearOfEra();
                        break;
                    case MONTH_INDEX:
                        value = date.getMonth();
                        break;
                    case DAY_OF_MONTH_INDEX:
                        value = date.getDayOfMonth();
                        break;
                    case DAY_OF_YEAR_INDEX:
                        long utc = iso.getDaysSinceEpochUTC();
                        int yoe = date.getYearOfEra(this.history.getNewYearStrategy());
                        HistoricDate newYear = this.history.getBeginOfYear(date.getEra(), yoe);
                        value = (int) (utc - this.history.convert(newYear).getDaysSinceEpochUTC() + 1);
                        break;
                    case YEAR_AFTER_INDEX:
                    case YEAR_BEFORE_INDEX:
                        value = date.getYearOfEra(this.history.getNewYearStrategy());
                        break;
                    case CENTURY_INDEX:
                        value = ((date.getYearOfEra() - 1) / 100) + 1;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown element index: " + this.index);
                }
                return Integer.valueOf(value);
            } catch (IllegalArgumentException iae) {
                throw new ChronoException(iae.getMessage(), iae);
            }

        }

        @Override
        public Integer getMinimum(C context) {

            try {
                HistoricDate current = this.history.convert(context.get(PlainDate.COMPONENT));

                if (
                    (this.index == YEAR_OF_ERA_INDEX)
                    || (this.index == YEAR_AFTER_INDEX)
                    || (this.index == YEAR_BEFORE_INDEX)
                    || (this.index == CENTURY_INDEX)
                ) {
                    if ((current.getEra() == HistoricEra.BYZANTINE) && (current.getMonth() >= 9)) {
                        return Integer.valueOf(0);
                    } else {
                        return Integer.valueOf(1);
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
                    case YEAR_AFTER_INDEX:
                    case YEAR_BEFORE_INDEX:
                    case CENTURY_INDEX:
                        if (current.getEra() == HistoricEra.BC) {
                            max = this.history.convert(PlainDate.axis().getMinimum()).getYearOfEra();
                        } else {
                            max = this.history.convert(PlainDate.axis().getMaximum()).getYearOfEra();
                        }
                        if (this.index == CENTURY_INDEX) {
                            max = ((max - 1) / 100) + 1;
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

            if (value == null) {
                throw new IllegalArgumentException("Missing historic element value.");
            }

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
            YearDefinition yd = YearDefinition.DUAL_DATING;
            NewYearStrategy nys = this.history.getNewYearStrategy();

            HistoricDate result;

            switch (this.index) {
                case YEAR_AFTER_INDEX:
                case YEAR_BEFORE_INDEX:
                    yd = (
                        (this.index == YEAR_AFTER_INDEX)
                            ? YearDefinition.AFTER_NEW_YEAR
                            : YearDefinition.BEFORE_NEW_YEAR);
                    // fall-through
                case YEAR_OF_ERA_INDEX:
                    result = HistoricDate.of(hd.getEra(), value, hd.getMonth(), hd.getDayOfMonth(), yd, nys);
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
                case CENTURY_INDEX:
                    int y2 = (hd.getYearOfEra() % 100);
                    int yearOfEra = ((value - 1) * 100) + ((y2 == 0) ? 100 : y2);
                    result = HistoricDate.of(hd.getEra(), yearOfEra, hd.getMonth(), hd.getDayOfMonth(), yd, nys);
                    result = this.history.adjustDayOfMonth(result);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

            return result;

        }

    }

}
