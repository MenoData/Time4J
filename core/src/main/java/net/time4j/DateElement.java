/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (DateElement.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoEntity;

import java.io.ObjectStreamException;


/**
 * <p>Repr&auml;sentiert eine Datumskomponente. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class DateElement
    extends BasicElement<PlainDate> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton-Instanz.
     */
    static final DateElement INSTANCE = new DateElement();

    private static final long serialVersionUID = -6519899440006935829L;

    //~ Konstruktoren -----------------------------------------------------

    private DateElement() {
        super("CALENDAR_DATE");

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<PlainDate> getType() {

        return PlainDate.class;

    }

    @Override
    public PlainDate getDefaultMinimum() {

        return PlainDate.MIN;

    }

    @Override
    public PlainDate getDefaultMaximum() {

        return PlainDate.MAX;

    }

    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public boolean isDateElement() {

        return true;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    private Object readResolve() throws ObjectStreamException {

        return INSTANCE;

    }

}
