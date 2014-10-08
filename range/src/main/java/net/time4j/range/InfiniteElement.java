/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (InfiniteElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;


/**
 * <p>Identifiziert ein Unendlich-Symbol. </p>
 *
 * @author  Meno Hochschild
 */
enum InfiniteElement
    implements ChronoElement<Boolean> {

    //~ Statische Felder/Initialisierungen --------------------------------

    START_INFINITE,

    END_INFINITE;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Boolean> getType() {

        return Boolean.class;

    }

    @Override
    public char getSymbol() {

        return '\u0000';

    }

    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public Boolean getDefaultMinimum() {

        return Boolean.FALSE;

    }

    @Override
    public Boolean getDefaultMaximum() {

        return Boolean.TRUE;

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public boolean isLenient() {

        return false;

    }

}
