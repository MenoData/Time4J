/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NonAmbivalentMap.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>Spezialimplementierung einer {@code Map}, die kein &Uuml;berschreiben
 * von gespeicherten Eintr&auml;gen zul&auml;sst, wenn die Werte zu einem
 * Schl&uuml;ssel verschieden sind. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
class NonAmbivalentMap
    extends HashMap<ChronoElement<?>, Object> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1245025551222311435L;

    //~ Konstruktoren -----------------------------------------------------

    NonAmbivalentMap(Map<? extends ChronoElement<?>, ?> map) {
        super(map);

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Object put(ChronoElement<?> key, Object value) {

        Object obj = super.put(key, value);

        if (
            (key == null)
            || (obj == null)
            || obj.equals(value)
        ) {
            return obj;
        } else {
            throw new AmbivalentValueException(key);
        }

    }

}
