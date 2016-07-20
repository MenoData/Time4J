/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SkipProcessor.java) is part of project Time4J.
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

import java.io.IOException;
import java.util.Set;


/**
 * <p>Verarbeitet nicht interpretierbare Zeichen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.18/4.14
 */
final class SkipProcessor
    implements FormatProcessor<Void> {

    //~ Instanzvariablen --------------------------------------------------

    private final int count;
    private final ChronoCondition<Character> condition;

    //~ Konstruktoren -----------------------------------------------------

    SkipProcessor(int keepRemainingChars) {
        super();

        if (keepRemainingChars < 0) {
            throw new IllegalArgumentException("Must not be negative: " + keepRemainingChars);
        }

        this.condition = null;
        this.count = keepRemainingChars;

    }

    SkipProcessor(
        ChronoCondition<Character> unparseable,
        int maxIterations
    ) {
        super();

        if (unparseable == null) {
            throw new NullPointerException("Missing condition for unparseable chars.");
        } else if (maxIterations < 1) {
            throw new IllegalArgumentException("Must be positive: " + maxIterations);
        }

        this.condition = unparseable;
        this.count = maxIterations;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        // no-op

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        ParsedValues parsedResult,
        boolean quickPath
    ) {

        int start = status.getPosition();
        int len = text.length();
        int offset;

        if (this.condition == null) {
            offset = len - this.count; // minus keepRemainingChars
        } else {
            offset = start;
            for (int i = 0; i < this.count && (i + start < len); i++) {
                if (this.condition.test(Character.valueOf(text.charAt(i + start)))) {
                    offset++;
                } else {
                    break;
                }
            }
        }

        offset = Math.min(Math.max(offset, 0), len);

        if (offset > start) {
            status.setPosition(offset);
        }

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof SkipProcessor) {
            SkipProcessor that = (SkipProcessor) obj;

            return (
                (this.count == that.count)
                && ((this.condition == null) ? (that.condition == null) : this.condition.equals(that.condition))
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return ((this.condition == null) ? this.count : (~this.count ^ this.condition.hashCode()));

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName());
        if (this.condition == null) {
            sb.append("[keepRemainingChars=");
            sb.append(this.count);
        } else {
            sb.append("[condition=");
            sb.append(this.condition);
            sb.append(", maxIterations=");
            sb.append(this.count);
        }
        sb.append(']');
        return sb.toString();

    }

    // optional
    @Override
    public ChronoElement<Void> getElement() {

        return null;

    }

    @Override
    public FormatProcessor<Void> withElement(ChronoElement<Void> element) {

        return this;

    }

    @Override
    public boolean isNumerical() {

        return false;

    }

    @Override
    public FormatProcessor<Void> quickPath(
        ChronoFormatter<?> formatter,
        AttributeQuery attributes,
        int reserved
    ) {

        return this;

    }

}
