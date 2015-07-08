/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AltTextProvider.java) is part of project Time4J.
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
 * <p>SPI interface which is identical to {@code TextProvider} but enables a different name
 * of the associated service loader file intended for the Android platform. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>SPI-Interface, das zu seinem Super-Interface {@code TextProvider} nichts hinzuf&uuml;gt,
 * aber eine {@code ServiceLoader}-Datei mit einem anderen Namen speziell f&uuml;r die
 * Android-Plattform erm&ouml;glicht. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @see     java.util.ServiceLoader
 */
public interface AltTextProvider
    extends TextProvider {

}
