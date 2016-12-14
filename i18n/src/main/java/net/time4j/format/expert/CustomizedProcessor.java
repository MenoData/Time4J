/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CustomizedProcessor.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.Moment;
import net.time4j.ZonalDateTime;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.Chronology;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * <p>Repr&auml;sentiert eine benutzerdefinierte Formatroutine. </p>
 *
 * @param   <V> generic type of element values
 * @author  Meno Hochschild
 * @since   3.0
 */
final class CustomizedProcessor<V>
    implements FormatProcessor<V> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ChronoFunction<ChronoDisplay, Void> NO_RESULT =
        new ChronoFunction<ChronoDisplay, Void>() {
            @Override
            public Void apply(ChronoDisplay context) {
                return null;
            }
        };

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<V> element;
    private final ChronoPrinter<V> printer;
    private final ChronoParser<V> parser;
    private final boolean passThroughZDT;

    // quick path optimization
    private boolean optPrinter;
    private boolean optParser;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue benutzerdefinierte Formatverarbeitung. </p>
     *
     * @param   element     chronological element to be formatted
     * @param   printer     helper object for text output
     * @param   parser      helper object for parsing
     */
    CustomizedProcessor(
        ChronoElement<V> element,
        ChronoPrinter<V> printer,
        ChronoParser<V> parser
    ) {
        this(element, printer, parser, false, false);

    }

    private CustomizedProcessor(
        ChronoElement<V> element,
        ChronoPrinter<V> printer,
        ChronoParser<V> parser,
        boolean optPrinter,
        boolean optParser
    ) {
        super();

        if (element == null) {
            throw new NullPointerException("Missing element.");
        } else if (printer == null) {
            throw new NullPointerException("Missing printer.");
        } else if (parser == null) {
            throw new NullPointerException("Missing parser.");
        }

        this.element = element;
        this.printer = printer;
        this.parser = parser;

        this.passThroughZDT = ((printer instanceof ChronoFormatter) && (element.getType() == Moment.class));

        this.optPrinter = optPrinter;
        this.optParser = optParser;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions, // optional
        boolean quickPath
    ) throws IOException {

        if (quickPath && this.optPrinter) {
            attributes = ChronoFormatter.class.cast(this.printer).getAttributes();
        }

        // special optimization avoiding double conversion from Moment to ZonalDateTime
        if (this.passThroughZDT && (formattable instanceof ZonalDateTime) && (positions == null)) {
            ChronoFormatter<?> cf = (ChronoFormatter<?>) this.printer;
            cf.print(formattable, buffer, attributes, false);
            return;
        }

        V value = formattable.get(this.element);
        StringBuilder collector = new StringBuilder();

        if (
            (buffer instanceof CharSequence)
            && (positions != null)
        ) {
            int offset = ((CharSequence) buffer).length();

            if (this.printer instanceof ChronoFormatter) {
                ChronoFormatter<?> cf = ChronoFormatter.class.cast(this.printer);
                Set<ElementPosition> result = print(cf, value, collector, attributes);
                Set<ElementPosition> set = new LinkedHashSet<ElementPosition>();
                for (ElementPosition ep : result) {
                    set.add(
                        new ElementPosition(
                            ep.getElement(),
                            offset + ep.getStartIndex(),
                            offset + ep.getEndIndex()));
                }
                positions.addAll(set);
            } else {
                this.printer.print(value, collector, attributes, NO_RESULT);

                positions.add(
                    new ElementPosition(
                        this.element,
                        offset,
                        offset + collector.length()));
            }
        } else {
            this.printer.print(value, collector, attributes, NO_RESULT);
        }

        buffer.append(collector);

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int offset = status.getPosition();

        try {
            if (quickPath && this.optParser) {
                attributes = ChronoFormatter.class.cast(this.parser).getAttributes();
            }

            V value = this.parser.parse(text, status, attributes);

            if (value == null) {
                status.setError(offset, status.getErrorMessage());
            } else {
                ChronoEntity<?> raw = status.getRawValues();
                for (ChronoElement<?> e : raw.getRegisteredElements()) {
                    if (e.getType() == Integer.class){
                        @SuppressWarnings("unchecked") // avoids autoboxing
                        ChronoElement<Integer> ie = (ChronoElement<Integer>) e;
                        parsedResult.put(ie, raw.getInt(ie));
                    } else {
                        parsedResult.put(e, raw.get(e));
                    }
                }
                parsedResult.put(this.element, value);
            }
        } catch (IndexOutOfBoundsException ex) {
            status.setError(offset, ex.getMessage());
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof CustomizedProcessor) {
            CustomizedProcessor<?> that = (CustomizedProcessor) obj;

            return (
                (this.element.equals(that.element))
                && this.printer.equals(that.printer)
                && this.parser.equals(that.parser)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (
            7 * this.element.hashCode()
            + 31 * this.printer.hashCode()
            + 37 * this.parser.hashCode()
        );

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        sb.append("[element=");
        sb.append(this.element.name());
        sb.append(", printer=");
        sb.append(this.printer);
        sb.append(", parser=");
        sb.append(this.parser);
        sb.append(']');
        return sb.toString();

    }

    @Override
    public ChronoElement<V> getElement() {

        return this.element;

    }

    @Override
    public FormatProcessor<V> withElement(ChronoElement<V> element) {

        if (this.element == element) {
            return this;
        }

        return new CustomizedProcessor<V>(element, this.printer, this.parser);

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    @Override
    @SuppressWarnings("unchecked")
    public FormatProcessor<V> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        if (attributes instanceof AttributeSet) { // should always happen, see implementation of FormatStep
            ChronoPrinter<V> effectivePrinter = this.printer;
            ChronoParser<V> effectiveParser = this.parser;
            boolean effectiveOptPrinter = false;
            boolean effectiveOptParser = false;
            Map<ChronoElement<?>, Object> defaultMap = formatter.getDefaults();
            AttributeSet outer = (AttributeSet) attributes;

            if (this.printer instanceof ChronoFormatter) {
                ChronoFormatter<?> qPrinter = ChronoFormatter.class.cast(this.printer);
                effectivePrinter = (ChronoPrinter<V>) qPrinter.with(adjust(defaultMap, qPrinter), outer);
                effectiveOptPrinter = true;
            }

            if (this.parser instanceof ChronoFormatter) {
                ChronoFormatter<?> qParser = ChronoFormatter.class.cast(this.parser);
                effectiveParser = (ChronoParser<V>) qParser.with(adjust(defaultMap, qParser), outer);
                effectiveOptParser = true;
            }

            return new CustomizedProcessor<V>(
                this.element, effectivePrinter, effectiveParser, effectiveOptPrinter, effectiveOptParser);
        } else if (this.optPrinter || this.optParser) {
            return new CustomizedProcessor<V>(this.element, this.printer, this.parser);
        } else {
            return this;
        }

    }

    private static Map<ChronoElement<?>, Object> adjust(
        Map<ChronoElement<?>, Object> outerDefaults,
        ChronoFormatter<?> inner
    ) {

        Chronology<?> chrono = inner.getChronology();
        Map<ChronoElement<?>, Object> result = new HashMap<ChronoElement<?>, Object>();

        for (ChronoElement<?> element : outerDefaults.keySet()) {
            if (chrono.isSupported(element)) {
                result.put(element, outerDefaults.get(element));
            }
        }

        return result;

    }

    // wildcard capture
    private static <T> Set<ElementPosition> print(
        ChronoFormatter<T> formatter,
        Object value,
        StringBuilder collector,
        AttributeQuery attributes
    ) throws IOException {

        return formatter.print(
            formatter.getChronology().getChronoType().cast(value),
            collector,
            attributes);

    }

}

