/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LeapsecondElement.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoEntity;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;


/**
 * <p>Repr&auml;sentiert einen Zeiger auf eine Schaltsekundeninformation. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 * @serial      exclude
 */
@SuppressWarnings("serial")
final class LeapsecondElement
    extends BasicElement<Boolean> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton-Instanz.
     */
    static final LeapsecondElement INSTANCE = new LeapsecondElement();

    //~ Konstruktoren -----------------------------------------------------

    private LeapsecondElement() {
        super("LEAPSECOND");

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Boolean> getType() {

        return Boolean.class;

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
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        return o1.get(this).compareTo(o2.get(this));

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    /**
     * @serialData  Blocks because serialization is not supported.
     * @throws      NotSerializableException (always)
     */
    private void readObject(ObjectInputStream in) throws IOException {

        throw new NotSerializableException();

    }

}
