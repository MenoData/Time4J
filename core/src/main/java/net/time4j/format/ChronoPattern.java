/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoPattern.java) is part of project Time4J.
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

package net.time4j.format;


/**
 * <p>Allows a flexible interpretation of symbols in format patterns. </p>
 *
 * @param   <P> self-referencing type parameter as discriminator for the format engine to be used
 * @author  Meno Hochschild
 * @since   3.0
 */
/*[deutsch]
 * <p>Erlaubt eine flexible Interpretation von Symbolen in Formatmustern. </p>
 *
 * @param   <P> self-referencing type parameter as discriminator for the format engine to be used
 * @author  Meno Hochschild
 * @since   3.0
 */
public interface ChronoPattern<P extends ChronoPattern<P>> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the format engine this pattern is designed for. </p>
     *
     * @return  format and parse engine to be used
     * @since   3.0
     */
    /*[deutsch]
     * <p>Liefert die {@code FormatEngine}, f&uuml;r die dieses Formatmuster gedacht ist. </p>
     *
     * @return  format and parse engine to be used
     * @since   3.0
     */
    FormatEngine<P> getFormatEngine();

}
