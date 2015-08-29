/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (VariantSource.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Provides the name of any calendar variant. </p>
 *
 * @author  Meno Hochschild
 * @since   3.6/4.4
 */
/*[deutsch]
 * <p>Liefert den Namen irgendeiner Kalendervariante. </p>
 *
 * @author  Meno Hochschild
 * @since   3.6/4.4
 */
public interface VariantSource {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the variant name. </p>
     *
     * @return  String
     * @since   3.6/4.4
     */
    /*[deutsch]
     * <p>Liefert den Variantennamen. </p>
     *
     * @return  String
     * @since   3.6/4.4
     */
    String getVariant();

}
