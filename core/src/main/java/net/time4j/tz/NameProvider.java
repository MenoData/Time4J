/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NameProvider.java) is part of project Time4J.
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

import java.util.Locale;


/**
 * <p>SPI-interface for accessing localized timezone names in historized
 * timezones. </p>
 *
 * <p>The fact that this interface is used per {@code java.util.ServiceLoader}
 * requires a concrete implementation to offer a public no-arg
 * constructor. Time4J can register several {@code NameProvider}s. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.ServiceLoader
 */
/*[deutsch]
 * <p>SPI-Interface als Zugang zu lokalisierten Zeitzonennamen in
 * historisierten Zeitzonen. </p>
 *
 * <p>Die Tatsache, da&szlig; dieses Interface mittels eines
 * {@code java.util.ServiceLoader}-Mechanismus benutzt wird, erfordert
 * von jedweder konkreten Implementierung einen &ouml;ffentlichen
 * parameterlosen Konstruktor. Time4J kann mehrere {@code NameProvider}
 * registrieren. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     java.util.ServiceLoader
 */
public interface NameProvider {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the name of this timezone suitable for presentation to
     * users in given style and locale. </p>
     *
     * @param   tzid                timezone identifier
     * @param   style               name style
     * @param   locale              language setting
     * @return  localized timezone name for display purposes
     *          or {@code null} if not supported
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     */
    /*[deutsch]
     * <p>Liefert den anzuzeigenden Zeitzonennamen. </p>
     *
     * @param   tzid                timezone identifier
     * @param   style               name style
     * @param   locale              language setting
     * @return  localized timezone name for display purposes
     *          or {@code null} if not supported
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     */
    String getDisplayName(
        String tzid,
        NameStyle style,
        Locale locale
    );

}
