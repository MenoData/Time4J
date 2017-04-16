/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormatStep.java) is part of project Time4J.
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

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.internal.DualFormatElement;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * <p>Formatierschritt als Delegationsobjekt zum Parsen und Formatieren. </p>
 *
 * @author  Meno Hochschild
 * @since   3.15/4.12
 */
final class FormatStep {

    //~ Instanzvariablen --------------------------------------------------

    private final FormatProcessor<?> processor;
    private final int level;
    private final int section;
    private final AttributeSet sectionalAttrs;
    private final AttributeQuery fullAttrs;
    private final int reserved;
    private final int padLeft;
    private final int padRight;
    private final boolean orMarker;
    private final int lastOrBlockIndex;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruktor mit Delegationsobjekt und Attributen. </p>
     *
     * @param   processor       processor which will process all formatting work
     * @param   level           level of optional processing
     * @param   section         identifies the optional attribute section
     * @param   sectionalAttrs  sectional control attributes (optional)
     * @throws  IllegalArgumentException in case of any inconsistencies
     */
    FormatStep(
        FormatProcessor<?> processor,
        int level,
        int section,
        AttributeSet sectionalAttrs
    ) {
        this(processor, level, section, sectionalAttrs, null, 0, 0, 0, false, -1);

    }

    private FormatStep(
        FormatProcessor<?> processor,
        int level,
        int section,
        AttributeSet sectionalAttrs,
        AttributeQuery fullAttrs,
        int reserved,
        int padLeft,
        int padRight,
        boolean orMarker,
        int lastOrBlockIndex
    ) {
        super();

        if (processor == null) {
            throw new NullPointerException("Missing format processor.");
        } else if (level < 0) {
            throw new IllegalArgumentException("Invalid level: " + level);
        } else if (section < 0) {
            throw new IllegalArgumentException("Invalid section: " + section);
        } else if (reserved < 0) {
            throw new IllegalArgumentException("Reserved chars must not be negative: " + reserved);
        } else if (padLeft < 0) {
            throw new IllegalArgumentException("Invalid pad-width: " + padLeft);
        } else if (padRight < 0) {
            throw new IllegalArgumentException("Invalid pad-width: " + padRight);
        }

        this.processor = processor;
        this.level = level;
        this.section = section;
        this.sectionalAttrs = sectionalAttrs;
        this.fullAttrs = fullAttrs;
        this.reserved = reserved;
        this.padLeft = padLeft;
        this.padRight = padRight;
        this.orMarker = orMarker;
        this.lastOrBlockIndex = lastOrBlockIndex;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt eine Textausgabe und speichert sie im angegebenen Puffer. </p>
     *
     * @param   formattable     object to be formatted
     * @param   buffer          format buffer any text output will be sent to
     * @param   attributes      non-sectional control attributes
     * @param   positions       positions of elements in text (optional)
     * @param   quickPath       hint for using quick path
     * @throws  IllegalArgumentException if the object is not formattable
     * @throws  IOException if writing into buffer fails
     * @since   3.15/4.12
     */
    void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        if (!this.isPrinting(formattable)) {
            return;
        }

        AttributeQuery aq = (quickPath ? this.fullAttrs : this.getQuery(attributes));

        if (
            (this.padLeft == 0)
            && (this.padRight == 0)
        ) {
            this.processor.print(
                formattable,
                buffer,
                aq,
                positions,
                quickPath
            );

            return;
        }

        StringBuilder collector;
        int start = -1;
        int offset = -1;
        Set<ElementPosition> posBuf = null;

        if (buffer instanceof StringBuilder) {
            collector = (StringBuilder) buffer;
            start = collector.length();
        } else {
            collector = new StringBuilder();
        }

        if (
            (buffer instanceof CharSequence)
            && (positions != null)
        ) {
            offset = ((CharSequence) buffer).length();
            posBuf = new LinkedHashSet<ElementPosition>();
        }

        boolean strict = this.isStrict(aq);
        char padChar = this.getPadChar(aq);
        int len = collector.length();

        this.processor.print(
            formattable,
            collector,
            aq,
            posBuf,
            quickPath
        );

        len = collector.length() - len;
        int printed = len;

        if (this.padLeft > 0) {
            if (strict && (len > this.padLeft)) {
                throw new IllegalArgumentException(this.padExceeded());
            }

            while (printed < this.padLeft) {
                if (start == -1) {
                    buffer.append(padChar);
                } else {
                    collector.insert(start, padChar);
                }
                printed++;
            }

            if (start == -1) {
                buffer.append(collector);
            }

            if (offset != -1) {
                for (ElementPosition ep : posBuf) {
                    positions.add(
                        new ElementPosition(
                            ep.getElement(),
                            offset + ep.getStartIndex(),
                            offset + ep.getEndIndex()));
                }
            }

            if (this.padRight > 0) {
                if (strict && (len > this.padRight)) {
                    throw new IllegalArgumentException(this.padExceeded());
                }

                while (len < this.padRight) {
                    buffer.append(padChar);
                    len++;
                }
            }
        } else { // padRight > 0
            if (strict && (len > this.padRight)) {
                throw new IllegalArgumentException(this.padExceeded());
            }

            if (start == -1) {
                buffer.append(collector);
            }

            while (printed < this.padRight) {
                buffer.append(padChar);
                printed++;
            }

            if (offset != -1) {
                for (ElementPosition ep : posBuf) {
                    positions.add(
                        new ElementPosition(
                            ep.getElement(),
                            offset + ep.getStartIndex(),
                            offset + ep.getEndIndex()));
                }
            }
        }

    }

    /**
     * <p>Interpretiert den angegebenen Text. </p>
     *
     * @param   text            text to be parsed
     * @param   status          parser information (always as new instance)
     * @param   attributes      non-sectional control attributes
     * @param   parsedResult    result buffer for parsed values
     * @param   quickPath       hint for using quick path
     * @since   3.15/4.12
     */
    void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        AttributeQuery aq = (quickPath ? this.fullAttrs : this.getQuery(attributes));

        if (
            (this.padLeft == 0)
            && (this.padRight == 0)
        ) {
            // Optimierung
            this.doParse(text, status, aq, parsedResult, quickPath);
            return;
        }

        boolean strict = this.isStrict(aq);
        char padChar = this.getPadChar(aq);
        int start = status.getPosition();
        int endPos = text.length();
        int index = start;

        // linke Füllzeichen konsumieren
        while (
            (index < endPos)
            && (text.charAt(index) == padChar)
        ) {
            index++;
        }

        int leftPadCount = index - start;

        if (strict && (leftPadCount > this.padLeft)) {
            status.setError(start, this.padExceeded());
            return;
        }

        // Eigentliche Parser-Routine
        status.setPosition(index);
        this.doParse(text, status, aq, parsedResult, quickPath);

        if (status.isError()) {
            return;
        }

        index = status.getPosition();
        int width = index - start - leftPadCount;

        if (
            strict
            && (this.padLeft > 0)
            && ((width + leftPadCount) != this.padLeft)
        ) {
            status.setError(start, this.padMismatched());
            return;
        }

        // rechte Füllzeichen konsumieren
        int rightPadCount = 0;

        while (
            (index < endPos)
            && (!strict || (width + rightPadCount < this.padRight))
            && (text.charAt(index) == padChar)
        ) {
            index++;
            rightPadCount++;
        }

        if (
            strict
            && (this.padRight > 0)
            && ((width + rightPadCount) != this.padRight)
        ) {
            status.setError(index - rightPadCount, this.padMismatched());
            return;
        }

        status.setPosition(index);

    }

    /**
     * <p>Liefert die Ebene der optionalen Verarbeitung. </p>
     *
     * @return  int
     */
    int getLevel() {

        return this.level;

    }

    /**
     * <p>Identifiziert die optionale Sektion. </p>
     *
     * @return  int
     */
    int getSection() {

        return this.section;

    }

    /**
     * <p>Liegt ein fraktional oder dezimal formatiertes Element vor? </p>
     *
     * @return  boolean
     */
    boolean isDecimal() {

        return (
            (this.processor instanceof FractionProcessor)
            || (this.processor instanceof DecimalProcessor)
        );

    }

    /**
     * <p>Liegt ein numerisch formatiertes Element vor? </p>
     *
     * @return  boolean
     */
    boolean isNumerical() {

        return this.processor.isNumerical();

    }

    /**
     * <p>Ermittelt die Delegationsinstanz. </p>
     *
     * @return  delegate object for formatting work
     */
    FormatProcessor<?> getProcessor() {

        return this.processor;

    }

    /**
     * <p>Finaler Schritt nach dem <i>build</i> des Formatierers oder bei Attribut&auml;nderungen. </p>
     *
     * @param   formatter   reference to formattter holding the default global attributes
     * @return  copy of this instance maybe modified
     * @since   3.15/4.12
     */
    FormatStep quickPath(ChronoFormatter<?> formatter) {

        AttributeSet as = formatter.getAttributes0();

        if (this.sectionalAttrs != null) {
            Attributes attrs =
                new Attributes.Builder()
                    .setAll(as.getAttributes())
                    .setAll(this.sectionalAttrs.getAttributes())
                    .build();
            as = as.withAttributes(attrs);
        }

        return new FormatStep(
            this.processor.quickPath(formatter, as, this.reserved),
            this.level,
            this.section,
            this.sectionalAttrs,
            as,
            this.reserved,
            this.padLeft,
            this.padRight,
            this.orMarker,
            this.lastOrBlockIndex
        );

    }

    /**
     * <p>Aktualisiert diesen Formatierschritt. </p>
     *
     * @param   element     new element reference
     * @return  copy of this instance maybe modified
     */
    FormatStep updateElement(ChronoElement<?> element) {

        FormatProcessor<?> proc = update(this.processor, element);

        if (this.processor == proc) {
            return this;
        }

        return new FormatStep(
            proc,
            this.level,
            this.section,
            this.sectionalAttrs,
            this.fullAttrs,
            this.reserved,
            this.padLeft,
            this.padRight,
            this.orMarker,
            this.lastOrBlockIndex
        );

    }

    /**
     * <p>Rechnet die angegebene Anzahl der zu reservierenden Zeichen
     * hinzu. </p>
     *
     * @param   reserved    count of chars to be reserved
     * @return  updated format step
     */
    FormatStep reserve(int reserved) {

        return new FormatStep(
            this.processor,
            this.level,
            this.section,
            this.sectionalAttrs,
            null, // called before build of formatter
            this.reserved + reserved,
            this.padLeft,
            this.padRight,
            this.orMarker,
            this.lastOrBlockIndex
        );

    }

    /**
     * <p>Rechnet die angegebene Anzahl von F&uuml;llzeichen hinzu. </p>
     *
     * @param   padLeft     count of left-padding chars
     * @param   padRight    count of right-padding chars
     * @return  updated format step
     */
    FormatStep pad(
        int padLeft,
        int padRight
    ) {

        return new FormatStep(
            this.processor,
            this.level,
            this.section,
            this.sectionalAttrs,
            null, // called before build of formatter
            this.reserved,
            this.padLeft + padLeft,
            this.padRight + padRight,
            this.orMarker,
            this.lastOrBlockIndex
        );

    }

    /**
     * <p>Startet einen neuen oder-Block. </p>
     *
     * @return  updated format step
     * @throws  IllegalStateException if a new or-block was already started
     * @since   3.14/4.11
     */
    FormatStep startNewOrBlock() {

        if (this.orMarker) {
            throw new IllegalStateException("Cannot start or-block twice.");
        }

        return new FormatStep(
            this.processor,
            this.level,
            this.section,
            this.sectionalAttrs,
            null, // called before build of formatter
            this.reserved,
            this.padLeft,
            this.padRight,
            true,
            -1
        );

    }

    /**
     * <p>Markiert den letzten oder-Block des aktuellen Abschnitts. </p>
     *
     * @param   lastOrBlockIndex    index of last or-block in current section
     * @return  updated format step
     * @throws  IllegalStateException if called for a non-starting or-block
     * @since   3.16/4.13
     */
    FormatStep markLastOrBlock(int lastOrBlockIndex) {

        if (!this.orMarker) {
            throw new IllegalStateException("This step is not starting an or-block.");
        }

        return new FormatStep(
            this.processor,
            this.level,
            this.section,
            this.sectionalAttrs,
            this.fullAttrs,
            this.reserved,
            this.padLeft,
            this.padRight,
            true,
            lastOrBlockIndex
        );

    }

    /**
     * Wird ein neuer oder-Block gestartet?
     *
     * @return  boolean
     * @since   3.14/4.11
     */
    boolean isNewOrBlockStarted() {

        return this.orMarker;

    }

    /**
     * Ermittelt den Index des letzten Steps zum aktuellen oder-Abschnitt.
     *
     * @return  int
     * @since   3.16/4.13
     */
    int skipTrailingOrBlocks() {

        return this.lastOrBlockIndex;

    }

    /**
     * <p>Vergleicht die internen Formatverarbeitungen und die sektionalen
     * Attribute. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof FormatStep) {
            FormatStep that = (FormatStep) obj;

            return (
                this.processor.equals(that.processor)
                && (this.level == that.level)
                && (this.section == that.section)
                && isEqual(this.sectionalAttrs, that.sectionalAttrs)
                && isEqual(this.fullAttrs, that.fullAttrs)
                && (this.reserved == that.reserved)
                && (this.padLeft == that.padLeft)
                && (this.padRight == that.padRight)
                && (this.orMarker == that.orMarker)
                && (this.lastOrBlockIndex == that.lastOrBlockIndex)
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Code basierend auf dem internen Zustand. </p>
     */
    @Override
    public int hashCode() {

        return (
            7 * this.processor.hashCode()
            + 31 * (
                (this.sectionalAttrs == null)
                ? 0
                : this.sectionalAttrs.hashCode())
        );

    }

    /**
     * <p>F&uuml;r Debugging-Zwecke. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[processor=");
        sb.append(this.processor);
        sb.append(", level=");
        sb.append(this.level);
        sb.append(", section=");
        sb.append(this.section);
        if (this.sectionalAttrs != null) {
            sb.append(", attributes=");
            sb.append(this.sectionalAttrs);
        }
        sb.append(", reserved=");
        sb.append(this.reserved);
        sb.append(", pad-left=");
        sb.append(this.padLeft);
        sb.append(", pad-right=");
        sb.append(this.padRight);
        if (this.orMarker) {
            sb.append(", or-block-started");
        }
        sb.append(']');
        return sb.toString();

    }

    private AttributeQuery getQuery(AttributeQuery attributes) {

        if (this.sectionalAttrs == null) {
            return attributes; // Optimierung
        }

        return new MergedAttributes(this.sectionalAttrs, attributes);

    }

    @SuppressWarnings("unchecked")
    private static <V> FormatProcessor<V> update(
        FormatProcessor<V> fp,
        ChronoElement<?> element
    ) {

        if (fp.getElement() == null) {
            return fp;
        } else if (
            (fp.getElement().getType() != element.getType())
            && !(element instanceof DualFormatElement)
        ) {
            throw new IllegalArgumentException(
                "Cannot change element value type: " + element.name());
        }

        return fp.withElement((ChronoElement<V>) element);

    }

    private static boolean isEqual(
        Object o1, // optional
        Object o2 // optional
    ) {

        return ((o1 == null) ? (o2 == null) : o1.equals(o2));

    }

    private void doParse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedEntity<?> parsedResult,
        boolean quickPath
    ) {

        int current = status.getPosition();

        try {
            this.processor.parse(
                text,
                status,
                attributes,
                parsedResult,
                quickPath
            );
        } catch (RuntimeException re) {
            status.setError(current, re.getMessage());
        }

    }

    private boolean isStrict(AttributeQuery attributes) {

        return attributes.get(Attributes.LENIENCY, Leniency.SMART).isStrict();

    }

    private char getPadChar(AttributeQuery attributes) {

        return attributes.get(Attributes.PAD_CHAR, Character.valueOf(' ')).charValue();

    }

    private String padExceeded() {

        return "Pad width exceeded: " + this.processor.getElement().name();

    }

    private String padMismatched() {

        return "Pad width mismatched: " + this.processor.getElement().name();

    }

    private boolean isPrinting(ChronoDisplay formattable) {

        if (this.sectionalAttrs == null) {
            return true;
        }

        ChronoCondition<ChronoDisplay> printCondition = this.sectionalAttrs.getCondition();
        return ((printCondition == null) || printCondition.test(formattable));

    }

}
