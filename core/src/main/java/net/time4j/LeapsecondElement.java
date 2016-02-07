/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LeapsecondElement.java) is part of project Time4J.
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

import java.io.ObjectStreamException;


/**
 * <p>Repr&auml;sentiert einen Zeiger auf eine Schaltsekundeninformation. </p>
 *
 * @author      Meno Hochschild
 */
final class LeapsecondElement
    extends BasicElement<Boolean> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Singleton-Instanz.
     */
    static final LeapsecondElement INSTANCE = new LeapsecondElement();

    private static final long serialVersionUID = -5143702899727667978L;

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
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    protected boolean isSingleton() {

        return true;

    }

    private Object readResolve() throws ObjectStreamException {

        return INSTANCE;

    }

}
