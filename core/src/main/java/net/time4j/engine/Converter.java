/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Converter.java) is part of project Time4J.
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
 * <p>Serves as bridge to temporal types of JDK or other date and time libraries.</p>
 *
 * @param   <S>  source type in other library
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   3.24/4.20
 * @doctags.spec All implementations must be ideally stateless or at least be immutable
 */
/*[deutsch]
 * <p>Dient als Br&uuml;cke zu Datums- und Zeittypen aus dem JDK oder anderen Bibliotheken. </p>
 *
 * @param   <S>  source type in other library
 * @param   <T>  target type in Time4J
 * @author  Meno Hochschild
 * @since   3.24/4.20
 * @doctags.spec All implementations must be ideally stateless or at least be immutable
 */
public interface Converter<S, T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Converts the external type to a type in Time4J. </p>
     *
     * @param   source  external object
     * @return  translated Time4J-object
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     */
    /*[deutsch]
     * <p>Konvertiert den externen Typ nach Time4J. </p>
     *
     * @param   source  external object
     * @return  translated Time4J-object
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     */
    T translate(S source);

    /**
     * <p>Converts the Time4J-type to an external type.</p>
     *
     * @param   time4j Time4J-object
     * @return  translated object of external type
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     */
    /*[deutsch]
     * <p>Konvertiert den Time4J-Typ zu einem externen Typ.</p>
     *
     * @param   time4j Time4J-object
     * @return  translated object of external type
     * @throws  ArithmeticException in case of numerical overflow
     * @throws  ChronoException  if conversion fails
     */
    S from(T time4j);

    /**
     * <p>Obtains the type of the source. </p>
     *
     * @return  Class
     * @since   3.24/4.20
     */
    /*[deutsch]
     * <p>Ermittelt den Quelltyp. </p>
     *
     * @return  Class
     * @since   3.24/4.20
     */
    Class<S> getSourceType();

}
