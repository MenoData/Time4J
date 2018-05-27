/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZoneProvider.java) is part of project Time4J.
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

package net.time4j.tz;


/**
 * <p>SPI interface which encapsulates the timezone transitions and names. </p>
 *
 * <p>Implementations are usually stateless and should normally not
 * try to manage a cache. Instead Time4J uses its own cache. The
 * fact that this interface is used per {@code java.util.ServiceLoader}
 * requires a concrete implementation to offer a public no-arg
 * constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.ServiceLoader
 * @deprecated  Use the super interfaces instead
 */
/*[deutsch]
 * <p>SPI-Interface, das eine Zeitzonendatenbank mitsamt Zeitzonennamen kapselt. </p>
 *
 * <p>Implementierungen sind in der Regel zustandslos und halten keinen
 * Cache. Letzterer sollte normalerweise der Klasse {@code Timezone}
 * vorbehalten sein. Weil dieses Interface mittels eines
 * {@code java.util.ServiceLoader} genutzt wird, mu&szlig; eine
 * konkrete Implementierung einen &ouml;ffentlichen Konstruktor ohne
 * Argumente definieren. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.ServiceLoader
 * @deprecated  Use the super interfaces instead
 */
@Deprecated
public interface ZoneProvider
    extends ZoneModelProvider, ZoneNameProvider {

}
