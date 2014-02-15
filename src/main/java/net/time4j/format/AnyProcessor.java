/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AnyProcessor.java) is part of project Time4J.
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

package net.time4j.format;

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * <p>Ein Prozessor, der nichts ausgibt und den ganzen Rest eines Texts
 * beim Interpretieren ohne Folgen konsumiert. </p>
 *
 * @author  Meno Hochschild
 */
final class AnyProcessor
    implements FormatProcessor<Void> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Singleton. */
    static final AnyProcessor INSTANCE = new AnyProcessor();

    //~ Konstruktoren -----------------------------------------------------

    private AnyProcessor() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public void print(
        ChronoEntity<?> formattable,
        Appendable buffer,
        Attributes attributes,
        Set<ElementPosition> positions, // optional
        FormatStep step
    ) throws IOException {

        // no-op

    }

    @Override
    public void parse(
        CharSequence text,
        ParseLog status,
        Attributes attributes,
        Map<ChronoElement<?>, Object> parsedResult,
        FormatStep step
    ) {

        status.setPosition(text.length());

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

}
