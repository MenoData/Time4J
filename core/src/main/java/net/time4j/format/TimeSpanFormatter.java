/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeSpanFormatter.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.TimeSpan;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * <p>Represents a non-localized and user-defined format for timespans based on a
 * pattern containing some standard symbols and literals. </p>
 *
 * @param   <U> generic type of time units
 * @param   <S> generic type of supported timespan
 * @since   3.26/4.22
 */
/*[deutsch]
 * <p>Nicht-lokalisiertes benutzerdefiniertes Zeitspannenformat, das auf
 * Symbolmustern beruht. </p>
 *
 * @param   <U> generic type of time units
 * @param   <S> generic type of supported timespan
 * @since   3.26/4.22
 */
public abstract class TimeSpanFormatter<U, S extends TimeSpan<U>> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Object SIGN_KEY = new Object();

    //~ Instanzvariablen --------------------------------------------------

    private final Class<U> type;
    private final List<FormatItem<U>> items;
    private final String pattern;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard constructor for subclasses. </p>
     *
     * @param   type        reified unit type
     * @param   pattern     format pattern
     * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
     */
    /*[deutsch]
     * <p>Standardkonstruktor f&uuml;r Subklassen. </p>
     *
     * @param   type        reified unit type
     * @param   pattern     format pattern
     * @throws  IllegalArgumentException in any case of pattern inconsistencies or failures
     */
    protected TimeSpanFormatter(
        Class<U> type,
        String pattern
    ) {
        super();

        if (type == null) {
            throw new NullPointerException("Missing unit type.");
        }

        int n = pattern.length();
        List<List<FormatItem<U>>> stack = new ArrayList<List<FormatItem<U>>>();
        stack.add(new ArrayList<FormatItem<U>>());
        int digits = 0;

        for (int i = 0; i < n; i++) {
            char c = pattern.charAt(i);

            if (c == '#') {
                digits++;
            } else if (isSymbol(c)) {
                int start = i++;
                while ((i < n) && pattern.charAt(i) == c) {
                    i++;
                }
                this.addSymbol(c, i - start, digits, stack);
                digits = 0;
                i--;
            } else if (digits > 0) {
                throw new IllegalArgumentException("Char # must be followed by unit symbol.");
            } else if (c == '\'') { // Literalsektion
                int start = i++;
                while (i < n) {
                    if (pattern.charAt(i) == '\'') {
                        if ((i + 1 < n) && (pattern.charAt(i + 1) == '\'')) {
                            i++;
                        } else {
                            break;
                        }
                    }
                    i++;
                }
                if (i >= n) {
                    throw new IllegalArgumentException(
                        "String literal in pattern not closed: " + pattern);
                }
                if (start + 1 == i) {
                    this.addLiteral('\'', stack);
                } else {
                    String s = pattern.substring(start + 1, i);
                    this.addLiteral(s.replace("''", "'"), stack);
                }
            } else if (c == '[') {
                startOptionalSection(stack);
            } else if (c == ']') {
                endOptionalSection(stack);
            } else if (c == '.') {
                lastOn(stack).add(new SeparatorItem<U>('.', ','));
            } else if (c == ',') {
                lastOn(stack).add(new SeparatorItem<U>(',', '.'));
            } else if (c == '-') {
                lastOn(stack).add(new SignItem<U>(false));
            } else if (c == '+') {
                lastOn(stack).add(new SignItem<U>(true));
            } else if (c == '{') {
                int start = ++i;
                while ((i < n) && pattern.charAt(i) != '}') {
                    i++;
                }
                this.addPluralItem(pattern.substring(start, i), stack);
            } else if (c == '|') {
                lastOn(stack).add(OrItem.<U>getInstance());
            } else {
                this.addLiteral(c, stack);
            }
        }

        if (stack.size() > 1) {
            throw new IllegalArgumentException(
                "Open square bracket without closing one.");
        } else if (stack.isEmpty()) {
            throw new IllegalArgumentException("Empty or invalid pattern.");
        }

        List<FormatItem<U>> items = stack.get(0);

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Missing format pattern.");
        } else if ((items.get(0) == OrItem.INSTANCE) || (items.get(items.size() - 1) == OrItem.INSTANCE)) {
            throw new IllegalArgumentException("Pattern must not start or end with an or-operator.");
        }

        int count = items.size();
        int reserved = items.get(count - 1).getMinWidth();

        for (int i = count - 2; i >= 0; i--) {
            FormatItem<U> item = items.get(i);
            if (item == OrItem.INSTANCE) {
                reserved = 0;
            } else {
                items.set(i, item.update(reserved));
                reserved += item.getMinWidth();
            }
        }

        this.type = type;
        this.items = Collections.unmodifiableList(items);
        this.pattern = pattern;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the underlying format pattern. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert das zugrundeliegende Formatmuster. </p>
     *
     * @return  String
     */
    public String getPattern() {

        return this.pattern;

    }

    /**
     * <p>Yields the associated reified unit type. </p>
     *
     * @return  Class
     */
    /*[deutsch]
     * <p>Liefert den zugeh&ouml;rigen Zeiteinheitstyp. </p>
     *
     * @return  Class
     */
    public Class<U> getType() {

        return this.type;

    }

    /**
     * <p>Creates a textual output of given duration. </p>
     *
     * @param   duration	duration object
     * @return  textual representation of duration
     * @throws	IllegalArgumentException if some aspects of duration
     *          prevents printing (for example too many nanoseconds)
     */
    /*[deutsch]
     * <p>Erzeugt eine textuelle Ausgabe der angegebenen Dauer. </p>
     *
     * @param   duration	duration object
     * @return  textual representation of duration
     * @throws	IllegalArgumentException if some aspects of duration
     *          prevents printing (for example too many nanoseconds)
     */
    public String format(TimeSpan<? super U> duration) {

        StringBuilder buffer = new StringBuilder();

        try {
            this.print(duration, buffer);
        } catch (IOException ex) {
            throw new AssertionError(ex); // should never happen
        }

        return buffer.toString();

    }

    /**
     * <p>Creates a textual output of given duration and writes to
     * the buffer. </p>
     *
     * @param   duration	duration object
     * @param   buffer      I/O-buffer where the result is written to
     * @throws	IllegalArgumentException if some aspects of duration
     *          prevents printing (for example too many nanoseconds)
     * @throws  IOException if writing into buffer fails
     */
    /*[deutsch]
     * <p>Erzeugt eine textuelle Ausgabe der angegebenen Dauer und
     * schreibt sie in den Puffer. </p>
     *
     * @param   duration	duration object
     * @param   buffer      I/O-buffer where the result is written to
     * @throws	IllegalArgumentException if some aspects of duration
     *          prevents printing (for example too many nanoseconds)
     * @throws  IOException if writing into buffer fails
     */
    public void print(
        TimeSpan<? super U> duration,
        Appendable buffer
    ) throws IOException {

        for (FormatItem<U> item : this.items) {
            if (item == OrItem.INSTANCE) {
                break;
            }
            item.print(duration, buffer);
        }

    }

    /**
     * <p>Equivalent to {@code parse(text, 0)}. </p>
     *
     * @param   text	custom textual representation to be parsed
     * @return  parsed duration
     * @throws ParseException (for example in case of mixed signs)
     * @see     #parse(CharSequence, int)
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code parse(text, 0)}. </p>
     *
     * @param   text	custom textual representation to be parsed
     * @return  parsed duration
     * @throws	ParseException (for example in case of mixed signs)
     * @see     #parse(CharSequence, int)
     */
    public S parse(CharSequence text) throws ParseException {

        return this.parse(text, 0);

    }

    /**
     * <p>Analyzes given text according to format pattern and parses the
     * text to a duration. </p>
     *
     * @param   text	custom textual representation to be parsed
     * @param   offset  start position for the parser
     * @return  parsed duration
     * @throws	ParseException (for example in case of mixed signs)
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Text entsprechend dem
     * voreingestellten Formatmuster als Dauer. </p>
     *
     * @param   text	custom textual representation to be parsed
     * @param   offset  start position for the parser
     * @return  parsed duration
     * @throws	ParseException (for example in case of mixed signs)
     */
    public S parse(
        CharSequence text,
        int offset
    ) throws ParseException {

        int pos = offset;
        Map<Object, Long> unitsToValues = new HashMap<Object, Long>();

        for (int i = 0, n = this.items.size(); i < n; i++) {
            FormatItem<U> item = this.items.get(i);

            if (item == OrItem.INSTANCE) {
                break;
            }

            int reply = item.parse(unitsToValues, text, pos);

            if (reply < 0) {
                int found = -1;
                for (int j = i + 1; j < n; j++) {
                    if (this.items.get(j) == OrItem.INSTANCE) {
                        found = j;
                        break;
                    }
                }
                if (found == -1) {
                    throw new ParseException("Cannot parse: " + text, ~reply);
                } else {
                    unitsToValues.clear();
                    i = found;
                }
            } else {
                pos = reply;
            }
        }

        Long sign = unitsToValues.remove(SIGN_KEY);
        boolean negative = ((sign != null) && (sign.longValue() < 0));
        Map<U, Long> map = new HashMap<U, Long>();

        for (Object key : unitsToValues.keySet()) {
            if (this.type.isInstance(key)) {
                map.put(this.type.cast(key), unitsToValues.get(key));
            } else {
                throw new ParseException(
                    "Duration type mismatched: " + unitsToValues, pos);
            }
        }

        return this.convert(map, negative);

    }

    /**
     * <p>Used during parsing. </p>
     *
     * @param   map         map containing unit-to-value-associations
     * @param   negative    sign information of parsed timespan
     * @return  resulting timespan
     */
    /*[deutsch]
     * <p>Verwendet, wenn ein Text zu einer Zeitspanne interpretiert wird. </p>
     *
     * @param   map         map containing unit-to-value-associations
     * @param   negative    sign information of parsed timespan
     * @return  resulting timespan
     */
    protected abstract S convert(Map<U, Long> map, boolean negative);

    /**
     * <p>Associates a pattern symbol with the amount in some temporal unit. </p>
     *
     * <p>The character &quot;f&quot; must be associated with the nanosecond unit. </p>
     *
     * @param   symbol      pattern symbol
     * @return  resulting time unit
     * @throws  IllegalArgumentException if the symbol is not adequate for the underlying unit type
     */
    /*[deutsch]
     * <p>Assoziiert ein Formatmustersymbol mit dem Betrag in einer bestimmten Zeiteinheit. </p>
     *
     * <p>Das Zeichen &quot;f&quot; mu&szlig; immer mit der Nanosekundeneinheit assoziiert sein. </p>
     *
     * @param   symbol      pattern symbol
     * @return  resulting time unit
     * @throws  IllegalArgumentException if the symbol is not adequate for the underlying unit type
     */
    protected abstract U getUnit(char symbol);

    private static boolean isSymbol(char c) {

        return (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')));

    }

    private void addSymbol(
        char symbol,
        int count,
        int digits,
        List<List<FormatItem<U>>> stack
    ) {

        U unit = this.getUnit(symbol);
        List<FormatItem<U>> items = stack.get(stack.size() - 1);

        if (symbol == 'f') {
            if (digits > 0) {
                throw new IllegalArgumentException("Combination of # and f-symbol not allowed.");
            } else {
                items.add(new FractionItem<U>(0, count, this.getUnit(symbol)));
            }
        } else {
            items.add(new NumberItem<U>(0, count, count + digits, unit));
        }

    }

    private void addLiteral(
        char literal,
        List<List<FormatItem<U>>> stack
    ) {

        this.addLiteral(String.valueOf(literal), stack);

    }

    private void addLiteral(
        String literal,
        List<List<FormatItem<U>>> stack
    ) {

        lastOn(stack).add(new LiteralItem<U>(literal));

    }

    private void addPluralItem(
        String pluralInfo,
        List<List<FormatItem<U>>> stack
    ) {

        String[] parts = pluralInfo.split(":");

        if ((parts.length > 9) || (parts.length < 4)) {
            throw new IllegalArgumentException(
                "Plural information has wrong format: " + pluralInfo);
        }

        U unit;

        if (parts[0].length() == 1) {
            unit = this.getUnit(parts[0].charAt(0));
        } else {
            throw new IllegalArgumentException(
                "Plural information has wrong symbol: " + pluralInfo);
        }

        String[] localInfo = parts[2].split("-|_");
        String lang = localInfo[0];
        Locale loc;

        if (localInfo.length > 1) {
            String country = localInfo[1];
            if (localInfo.length > 2) {
                String variant = localInfo[2];
                if (localInfo.length > 3) {
                    throw new IllegalArgumentException(
                        "Plural information has wrong locale: " + pluralInfo);
                } else {
                    loc = new Locale(lang, country, variant);
                }
            } else {
                loc = new Locale(lang, country);
            }
        } else {
            loc = new Locale(lang);
        }

        Map<PluralCategory, String> pluralForms = new EnumMap<PluralCategory, String>(PluralCategory.class);
        PluralRules rules = PluralRules.of(loc, NumberType.CARDINALS);

        for (int i = 3; i < parts.length; i++) {
            String[] formInfo = parts[i].split("=");
            if (formInfo.length == 2) {
                pluralForms.put(
                    PluralCategory.valueOf(formInfo[0]),
                    formInfo[1]);
            } else {
                throw new IllegalArgumentException(
                    "Plural information has wrong format: " + pluralInfo);
            }
        }

        if (pluralForms.isEmpty()) {
            throw new IllegalArgumentException(
                "Missing plural forms: " + pluralInfo);
        } else if (!pluralForms.containsKey(PluralCategory.OTHER)) {
            throw new IllegalArgumentException(
                "Missing plural category OTHER: " + pluralInfo);
        }

        lastOn(stack).add(new PluralItem<U>(unit, parts[1], rules, pluralForms));

    }

    private static <U> void startOptionalSection(List<List<FormatItem<U>>> stack) {

        stack.add(new ArrayList<FormatItem<U>>());

    }

    private static <U> void endOptionalSection(List<List<FormatItem<U>>> stack) {

        int last = stack.size() - 1;

        if (last < 1) {
            throw new IllegalArgumentException(
                "Closing square bracket without open one.");
        }

        List<FormatItem<U>> items = stack.remove(last);
        stack.get(last - 1).add(new OptionalSectionItem<U>(items));

    }

    private static <U> List<FormatItem<U>> lastOn(List<List<FormatItem<U>>> stack) {

        return stack.get(stack.size() - 1);

    }

    //~ Innere Klassen ----------------------------------------------------

    private abstract static class FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final int reserved;

        //~ Konstruktoren -------------------------------------------------

        FormatItem(int reserved){
            super();

            this.reserved = reserved;

        }

        //~ Methoden ------------------------------------------------------

        boolean isZero(TimeSpan<? super U> duration) {
            return true;
        }

        abstract void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException;

        abstract int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int pos
        );

        int getReserved() {
            return this.reserved;
        }

        abstract int getMinWidth();

        abstract FormatItem<U> update(int reserved);

    }

    private static class NumberItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final int minWidth;
        private final int maxWidth;
        private final U unit;

        //~ Konstruktoren -------------------------------------------------

        private NumberItem(
            int reserved,
            int minWidth,
            int maxWidth,
            U unit
        ) {
            super(reserved);

            if (minWidth < 1 || minWidth > 18) {
                throw new IllegalArgumentException("Min width out of bounds: " + minWidth);
            } else if (maxWidth < minWidth) {
                throw new IllegalArgumentException("Max width smaller than min width.");
            } else if (maxWidth > 18) {
                throw new IllegalArgumentException("Max width out of bounds: " + maxWidth);
            } else if (unit == null) {
                throw new NullPointerException("Missing unit.");
            }

            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
            this.unit = unit;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            String num = String.valueOf(duration.getPartialAmount(this.unit));

            if (num.length() > this.maxWidth) {
                throw new IllegalArgumentException("Too many digits for: " + this.unit + " [" + duration + "]");
            }

            for (int i = this.minWidth - num.length(); i > 0; i--) {
                buffer.append('0');
            }

            buffer.append(num);

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            long total = 0;
            int pos = start;

            for (int i = start, n = text.length() - this.getReserved(); i < n; i++) {
                char c = text.charAt(i);
                if ((c >= '0') && (c <= '9')) {
                    if (i - start >= this.maxWidth) {
                        break;
                    }
                    int digit = (c - '0');
                    total = total * 10 + digit;
                    pos++;
                } else {
                    break;
                }
            }

            if (pos == start) {
                return ~start; // digits expected
            }

            Long value = Long.valueOf(total);
            Object old = unitsToValues.put(this.unit, value);

            if ((old == null) || old.equals(value)) {
                return pos;
            } else {
                return ~start; // ambivalent parsing
            }

        }

        @Override
        int getMinWidth() {

            return this.minWidth;

        }

        @Override
        FormatItem<U> update(int reserved) {

            return new NumberItem<U>(reserved, this.minWidth, this.maxWidth, this.unit);

        }

        @Override
        boolean isZero(TimeSpan<? super U> duration) {

            return (this.getAmount(duration) == 0);

        }

        long getAmount(TimeSpan<? super U> duration) {

            return duration.getPartialAmount(this.unit);

        }

        U getUnit() {

            return this.unit;

        }

    }

    private static class FractionItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final int width;
        private final U nanosecond;

        //~ Konstruktoren -------------------------------------------------

        private FractionItem(
            int reserved,
            int width,
            U nanosecond
        ) {
            super(reserved);

            if (width < 1 || width > 9) {
                throw new IllegalArgumentException(
                    "Fraction width out of bounds: " + width);
            }

            this.width = width;
            this.nanosecond = nanosecond;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            String num = String.valueOf(duration.getPartialAmount(this.nanosecond));
            int len = num.length();

            if (len > 9) {
                throw new IllegalArgumentException(
                    "Too many nanoseconds, consider normalization: " + duration);
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 9 - len; i++) {
                sb.append('0');
            }

            sb.append(num);
            buffer.append(sb.toString().substring(0, this.width));

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            StringBuilder fraction = new StringBuilder();
            int pos = start;

            for (
                int i = start, n = Math.min(text.length() - this.getReserved(), start + this.width);
                i < n;
                i++
            ) {
                char c = text.charAt(i);
                if ((c >= '0') && (c <= '9')) {
                    fraction.append(c);
                    pos++;
                } else {
                    break;
                }
            }

            if (pos == start) {
                return ~start; // digits expected
            }

            for (int i = 0, n = pos - start; i < 9 - n; i++) {
                fraction.append('0');
            }

            Long value = Long.valueOf(Long.parseLong(fraction.toString()));
            Object old = unitsToValues.put(this.nanosecond, value);

            if ((old == null) || old.equals(value)) {
                return pos;
            } else {
                return ~start; // ambivalent parsing
            }

        }

        @Override
        int getMinWidth() {

            return this.width;

        }

        @Override
        FormatItem<U> update(int reserved) {

            return new FractionItem<U>(reserved, this.width, this.nanosecond);

        }

        @Override
        boolean isZero(TimeSpan<? super U> duration) {

            return (duration.getPartialAmount(this.nanosecond) == 0);

        }

    }

    private static class PluralItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final NumberItem<U> numItem;
        private final FormatItem<U> sepItem;
        private final PluralRules rules;
        private final Map<PluralCategory, String> pluralForms;
        private final int minWidth;

        //~ Konstruktoren -------------------------------------------------

        private PluralItem(
            U unit,
            String separator,
            PluralRules rules,
            Map<PluralCategory, String> pluralForms
        ) {
            super(0);

            this.numItem = new NumberItem<U>(0, 1, 18, unit);
            this.sepItem = new LiteralItem<U>(separator, true);
            this.rules = rules;
            this.pluralForms = pluralForms;

            int width = Integer.MAX_VALUE;

            for (String s : pluralForms.values()) {
                if (s.length() < width) {
                    width = s.length();
                }
            }

            this.minWidth = width;

        }

        private PluralItem(
            int reserved,
            NumberItem<U> numItem,
            FormatItem<U> sepItem,
            PluralRules rules,
            Map<PluralCategory, String> pluralForms,
            int minWidth
        ) {
            super(reserved);

            this.numItem = numItem;
            this.sepItem = sepItem;
            this.rules = rules;
            this.pluralForms = pluralForms;
            this.minWidth = minWidth;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            this.numItem.print(duration, buffer);
            this.sepItem.print(duration, buffer);
            PluralCategory category = this.rules.getCategory(this.numItem.getAmount(duration));
            buffer.append(this.pluralForms.get(category));

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int pos
        ) {

            int start = pos;
            pos = this.numItem.parse(unitsToValues, text, pos);

            if (pos < 0) {
                return pos;
            }

            pos = this.sepItem.parse(unitsToValues, text, pos);

            if (pos < 0) {
                return pos;
            }

            long value = unitsToValues.get(this.numItem.getUnit()).longValue();
            String s = this.pluralForms.get(this.rules.getCategory(value));
            int n = s.length();

            if (pos + n > text.length() - this.getReserved()) {
                return ~start;
            }

            for (int i = 0; i < n; i++) {
                if (s.charAt(i) != text.charAt(pos + i)) {
                    return ~start;
                }
            }

            return pos + n;

        }

        @Override
        int getMinWidth() {

            return this.minWidth;

        }

        @Override
        FormatItem<U> update(int reserved) {

            return new PluralItem<U>(
                reserved, this.numItem, this.sepItem, this.rules, this.pluralForms, this.minWidth);

        }

        @Override
        boolean isZero(TimeSpan<? super U> duration) {

            return this.numItem.isZero(duration);

        }

    }

    private static class SeparatorItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final char separator;
        private final char alt;

        //~ Konstruktoren -------------------------------------------------

        private SeparatorItem(
            char separator,
            char alt
        ) {
            this(0, separator, alt);

        }

        private SeparatorItem(
            int reserved,
            char separator,
            char alt
        ) {
            super(reserved);

            this.separator = separator;
            this.alt = alt;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            buffer.append(this.separator);

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            if (start >= text.length() - this.getReserved()) {
                return ~start; // end of text
            }

            char c = text.charAt(start);

            if ((c != this.separator) && (c != this.alt)) {
                return ~start; // decimal separator expected
            }

            return start + 1;

        }

        @Override
        int getMinWidth() {

            return 1;

        }

        @Override
        FormatItem<U> update(int reserved) {

            return new SeparatorItem<U>(reserved, this.separator, this.alt);

        }

    }

    private static class OrItem<U>
        extends FormatItem<U> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final OrItem INSTANCE = new OrItem();

        //~ Konstruktoren -------------------------------------------------

        private OrItem() {
            super(0);

        }

        //~ Methoden ------------------------------------------------------

        @SuppressWarnings("unchecked")
        static <U> FormatItem<U> getInstance() {

            return INSTANCE;

        }

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            // no-op

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            return start;

        }

        @Override
        int getMinWidth() {

            return 0;

        }

        @Override
        FormatItem<U> update(int reserved) {

            return this;

        }

    }

    private static class LiteralItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final String literal;

        //~ Konstruktoren -------------------------------------------------

        private LiteralItem(String literal) {
            this(literal, false);

        }

        private LiteralItem(
            String literal,
            boolean withEmpty
        ) {
            super(0);

            if (!withEmpty && literal.isEmpty()) {
                throw new IllegalArgumentException("Literal is empty.");
            }

            this.literal = literal;

        }

        private LiteralItem(
            int reserved,
            String literal
        ) {
            super(reserved);

            this.literal = literal;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            buffer.append(this.literal);

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            int end = start + this.literal.length();

            if (end > text.length() - this.getReserved()) {
                return ~start; // end of line
            }

            for (int i = start; i < end; i++) {
                if (text.charAt(i) != this.literal.charAt(i - start)) {
                    return ~start; // literal expected
                }
            }

            return end;

        }

        @Override
        int getMinWidth() {

            return this.literal.length();

        }

        @Override
        FormatItem<U> update(int reserved) {

            return new LiteralItem<U>(reserved, this.literal);

        }

    }

    private static class SignItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final boolean always;

        //~ Konstruktoren -------------------------------------------------

        private SignItem(boolean always) {
            super(0);

            this.always = always;

        }

        private SignItem(
            int reserved,
            boolean always
        ) {
            super(reserved);

            this.always = always;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            if (this.always) {
                buffer.append(duration.isNegative() ? '-' : '+');
            } else if (duration.isNegative()) {
                buffer.append('-');
            }

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            if (start >= text.length() - this.getReserved()) {
                if (this.always) {
                    return ~start; // sign expected
                } else {
                    Long old = unitsToValues.put(SIGN_KEY, Long.valueOf(1));
                    if ((old != null) && (old.longValue() != 1)) {
                        return ~start; // mixed signs
                    }
                    return start;
                }
            }

            char c = text.charAt(start);
            Long sign = Long.valueOf(1);
            int ret = start;

            if (this.always) {
                if (c == '+') {
                    ret = start + 1;
                } else if (c == '-') {
                    sign = Long.valueOf(-1);
                    ret = start + 1;
                } else {
                    return ~start; // sign expected
                }
            } else {
                if (c == '+') {
                    return ~start; // positive sign not allowed
                } else if (c == '-') {
                    sign = Long.valueOf(-1);
                    ret = start + 1;
                }
            }

            Long old = unitsToValues.put(SIGN_KEY, sign);

            if ((old != null) && (old.longValue() != sign.longValue())) {
                return ~start; // mixed signs
            }

            return ret;

        }

        @Override
        int getMinWidth() {

            return (this.always ? 1 : 0);

        }

        @Override
        FormatItem<U> update(int reserved) {

            return new SignItem<U>(reserved, this.always);

        }

    }

    private static class OptionalSectionItem<U>
        extends FormatItem<U> {

        //~ Instanzvariablen ----------------------------------------------

        private final List<FormatItem<U>> items;

        //~ Konstruktoren -------------------------------------------------

        private OptionalSectionItem(List<FormatItem<U>> items) {
            super(0);

            if (items.isEmpty()) {
                throw new IllegalArgumentException(
                    "Optional section is empty.");
            } else if ((items.get(0) == OrItem.INSTANCE) || (items.get(items.size() - 1) == OrItem.INSTANCE)) {
                throw new IllegalArgumentException(
                    "Optional section must not start or end with an or-operator.");
            }

            this.items = Collections.unmodifiableList(items);

        }

        //~ Methoden ------------------------------------------------------

        @Override
        void print(
            TimeSpan<? super U> duration,
            Appendable buffer
        ) throws IOException {

            if (!this.isZero(duration)) {
                for (FormatItem<U> item : this.items) {
                    if (item == OrItem.INSTANCE) {
                        break;
                    }
                    item.print(duration, buffer);
                }
            }

        }

        @Override
        int parse(
            Map<Object, Long> unitsToValues,
            CharSequence text,
            int start
        ) {

            int pos = start;
            Map<Object, Long> store = new HashMap<Object, Long>();

            for (int i = 0, n = this.items.size(); i < n; i++) {
                FormatItem<U> item = this.items.get(i);

                if (item == OrItem.INSTANCE) {
                    break;
                }

                int reply = item.parse(store, text, pos);

                if (reply < 0) {
                    int found = -1;
                    for (int j = i + 1; j < n; j++) {
                        if (this.items.get(j) == OrItem.INSTANCE) {
                            found = j;
                            break;
                        }
                    }
                    if (found == -1) {
                        return start;
                    } else {
                        store.clear();
                        i = found;
                    }
                } else {
                    pos = reply;
                }
            }

            unitsToValues.putAll(store);
            return pos;

        }

        @Override
        int getMinWidth() {

            return 0;

        }

        @Override
        FormatItem<U> update(int reserved) {

            List<FormatItem<U>> tmp = new ArrayList<FormatItem<U>>(this.items);
            int n = tmp.size();

            for (int i = n - 1; i >= 0; i--) {
                FormatItem<U> item = tmp.get(i);
                tmp.set(i, item.update(reserved));
                reserved += item.getMinWidth();
            }

            return new OptionalSectionItem<U>(tmp);

        }

        @Override
        boolean isZero(TimeSpan<? super U> duration) {

            for (FormatItem<U> item : this.items) {
                if (!item.isZero(duration)) {
                    return false;
                }
            }

            return true;

        }

    }

}
