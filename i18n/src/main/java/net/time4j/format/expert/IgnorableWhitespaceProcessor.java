/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IgnorableWhitespaceProcessor.java) is part of project Time4J.
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
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * <p>Verarbeitet ignorierbare nicht-anzeigbare Zeichen. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
enum IgnorableWhitespaceProcessor
    implements FormatProcessor<Void> {

    //~ Statische Felder/Initialisierungen --------------------------------

    SINGLETON;

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoDisplay formattable,
        Appendable buffer,
        AttributeQuery attributes,
        Set<ElementPosition> positions,
        boolean quickPath
    ) throws IOException {

        buffer.append(' ');

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        AttributeQuery attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        boolean quickPath
    ) {

        int offset = status.getPosition();

        while (offset < text.length()) {
            char c = text.charAt(offset);

            if (Character.isWhitespace(c)) {
                offset++;
            } else {
                break;
            }
        }

        status.setPosition(offset);

    }

    @Override
    public String toString() {

        return "{IGNORABLE_WHITE_SPACE}";

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
        AttributeQuery attributes,
        int reserved
    ) {

        return this;

    }

}
