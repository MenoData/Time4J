/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AttributeProxy.java) is part of project Time4J.
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

package net.time4j.format.internal;

import net.time4j.engine.AttributeQuery;


/**
 * <p>A wrapper around another attribute query which is only allowed if no other attribute than
 * {@link net.time4j.format.Attributes#TRAILING_CHARACTERS} is overridden. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
/*[deutsch]
 * <p>Eine H&uuml;lle um eine andere {@code AttributeQuery}, deren Verwendung nur erlaubt ist,
 * wenn kein anderes Attribut als {@link net.time4j.format.Attributes#TRAILING_CHARACTERS}
 * &uuml;berschrieben ist. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
public interface AttributeProxy
    extends AttributeQuery {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the internal attribute query. </p>
     *
     * @return  attribute query
     */
    /*[deutsch]
     * <p>Liefert die gekapselten Attribute. </p>
     *
     * @return  attribute query
     */
    AttributeQuery getDelegate();

}
