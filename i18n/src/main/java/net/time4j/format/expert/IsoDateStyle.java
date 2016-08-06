/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (IsoDateStyle.java) is part of project Time4J.
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


/**
 * <p>Determines a suitable style in ISO-format for printing gregorian calendar dates. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
/*[deutsch]
 * <p>Legt den Stil im ISO-Format f&uuml;r die Ausgabe eines gregorianischen Kalenderdatums fest. </p>
 *
 * @author  Meno Hochschild
 * @since   4.18
 */
public enum IsoDateStyle {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Style &quot;20160425&quot;. </p>
     */
    /*[deutsch]
     * <p>Stil &quot;20160425&quot;. </p>
     */
    BASIC_CALENDAR_DATE,

    /**
     * <p>Style &quot;2016116&quot;. </p>
     */
    /*[deutsch]
     * <p>Stil &quot;2016116&quot;. </p>
     */
    BASIC_ORDINAL_DATE,

    /**
     * <p>Style &quot;2016W171&quot;. </p>
     */
    /*[deutsch]
     * <p>Stil &quot;2016W171&quot;. </p>
     */
    BASIC_WEEK_DATE,

    /**
     * <p>Style &quot;2016-04-25&quot;. </p>
     */
    /*[deutsch]
     * <p>Stil &quot;2016-04-25&quot;. </p>
     */
    EXTENDED_CALENDAR_DATE,

    /**
     * <p>Style &quot;2016-116&quot;. </p>
     */
    /*[deutsch]
     * <p>Stil &quot;2016-116&quot;. </p>
     */
    EXTENDED_ORDINAL_DATE,

    /**
     * <p>Style &quot;2016-W17-1&quot;. </p>
     */
    /*[deutsch]
     * <p>Stil &quot;2016-W17-1&quot;. </p>
     */
    EXTENDED_WEEK_DATE;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Determines, if this style describes the basic or extended iso-format. </p>
     *
     * @return  {@code true} if extended else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieser Stil das <i>basic</i>- oder das <i>extended</i>-ISO-Format beschreibt. </p>
     *
     * @return  {@code true} if extended else {@code false}
     */
    public boolean isExtended() {

        return (this.compareTo(EXTENDED_CALENDAR_DATE) >= 0);

    }

}
