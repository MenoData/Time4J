/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AmbivalentValueException.java) is part of project Time4J.
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

import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoElement;


/**
 * <p>Zeigt an, da&szlig; das dasselbe Element mit verschiedenen Werten
 * assoziiert wurde. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
class AmbivalentValueException
    extends ChronoException {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -4315329288187364457L;

    //~ Konstruktoren -----------------------------------------------------

    AmbivalentValueException(ChronoElement<?> element) {
        super(
            "Duplicate element parsed with different values: "
            + element.name());

    }

}
