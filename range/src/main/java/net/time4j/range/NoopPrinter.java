/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NoopPrinter.java) is part of project Time4J.
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

package net.time4j.range;

import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoFunction;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.ElementPosition;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;


enum NoopPrinter
    implements ChronoPrinter<Integer> {

    //~ Statische Felder/Initialisierungen --------------------------------

    NOOP;

    //~ Methoden ----------------------------------------------------------

    @Override // TODO: remove with v5.0
    public <R> R print(
        Integer formattable,
        Appendable buffer,
        AttributeQuery attributes,
        ChronoFunction<ChronoDisplay, R> query
    ) throws IOException {

        return null;

    }

    @Override
    public Set<ElementPosition> print(
        Integer formattable,
        StringBuilder buffer,
        AttributeQuery attributes
    ) {

        return Collections.emptySet();

    }

}
