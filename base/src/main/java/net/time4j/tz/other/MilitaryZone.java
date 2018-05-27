/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (MilitaryZone.java) is part of project Time4J.
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

package net.time4j.tz.other;

import net.time4j.tz.TZID;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Represents a military timezone (used by US) where the globe is divided
 * into fixed offset zones using the NATO phonetic alphabet. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine milit&auml;rische Zeitzone (verwendet vom
 * US-Milit&auml;r), wo der Globus in Zonen mit festem Offset eingeteilt
 * ist, unter Verwendung des phonetischen Alphabets der NATO. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 */
public enum MilitaryZone
    implements TZID {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Offset UTC+01:00</p>
     */
    ALPHA(1, "Alpha"),

    /**
     * <p>Offset UTC+02:00</p>
     */
    BRAVO(2, "Bravo"),

    /**
     * <p>Offset UTC+03:00</p>
     */
    CHARLIE(3, "Charlie"),

    /**
     * <p>Offset UTC+04:00</p>
     */
    DELTA(4, "Delta"),

    /**
     * <p>Offset UTC+05:00</p>
     */
    ECHO(5, "Echo"),

    /**
     * <p>Offset UTC+06:00</p>
     */
    FOXTROT(6, "Foxtrot"),

    /**
     * <p>Offset UTC+07:00</p>
     */
    GOLF(7, "Golf"),

    /**
     * <p>Offset UTC+08:00</p>
     */
    HOTEL(8, "Hotel"),

    /**
     * <p>Offset UTC+09:00</p>
     */
    INDIA(9, "India"),

    /**
     * <p>Offset UTC+10:00</p>
     */
    KILO(10, "Kilo"),

    /**
     * <p>Offset UTC+11:00</p>
     */
    LIMA(11, "Lima"),

    /**
     * <p>Offset UTC+12:00</p>
     */
    MIKE(12, "Mike"),

    /**
     * <p>Offset UTC-01:00</p>
     */
    NOVEMBER(-1, "November"),

    /**
     * <p>Offset UTC-02:00</p>
     */
    OSCAR(-2, "Oscar"),

    /**
     * <p>Offset UTC-03:00</p>
     */
    PAPA(-3, "Papa"),

    /**
     * <p>Offset UTC-04:00</p>
     */
    QUEBEC(-4, "Quebec"),

    /**
     * <p>Offset UTC-05:00</p>
     */
    ROMEO(-5, "Romeo"),

    /**
     * <p>Offset UTC-06:00</p>
     */
    SIERRA(-6, "Sierra"),

    /**
     * <p>Offset UTC-07:00</p>
     */
    TANGO(-7, "Tango"),

    /**
     * <p>Offset UTC-08:00</p>
     */
    UNIFORM(-8, "Uniform"),

    /**
     * <p>Offset UTC-09:00</p>
     */
    VICTOR(-9, "Victor"),

    /**
     * <p>Offset UTC-10:00</p>
     */
    WHISKEY(-10, "Whiskey"),

    /**
     * <p>Offset UTC-11:00</p>
     */
    X_RAY(-11, "X-ray"),

    /**
     * <p>Offset UTC-12:00</p>
     */
    YANKEE(-12, "Yankee"),

    /**
     * <p>Equivalent to {@code ZonalOffset.UTC}. </p>
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code ZonalOffset.UTC}. </p>
     */
    ZULU(0, "Zulu");

    //~ Instanzvariablen --------------------------------------------------

    private transient final ZonalOffset offset;
    private transient final String full;

    //~ Konstruktoren -----------------------------------------------------

    private MilitaryZone(
        int offsetInHours,
        String full
    ){
        this.offset = ZonalOffset.ofTotalSeconds(offsetInHours * 3600);
        this.full = full;
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the associated timezone offset. </p>
     *
     * @return  ZonalOffset in full hours
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert den assoziierten Zeitzonenversatz. </p>
     *
     * @return  ZonalOffset in full hours
     * @since   2.2
     */
    public ZonalOffset getOffset() {

        return this.offset;

    }

    /**
     * <p>Yields the first letter of the full name (for example
     * &quot;A&quot;). </p>
     *
     * @return  String
     * @since   2.2
     */
    /*[deutsch]
     * <p>Liefert den ersten Buchstaben des vollen Namens (zum Beispiel
     * &quot;A&quot;). </p>
     *
     * @return  String
     * @since   2.2
     */
    public String getSymbol() {

        return this.full.substring(0, 1);

    }

    /**
     * <p>Yields the full name (for example &quot;Alpha&quot;). </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert den vollen Namen (zum Beispiel &quot;Alpha&quot;). </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        return this.full;

    }

    /**
     * <p>Yields a canonical form of this timezone identifier, for
     * example &quot;MILITARY~UTC+01:00&quot;. </p>
     *
     * @return  String in format &quot;MILITARY~{offset}&quot;
     */
    /*[deutsch]
     * <p>Liefert eine kanonische Form dieser Zeitzonen-ID, zum Beispiel
     * &quot;MILITARY~UTC+01:00&quot;. </p>
     *
     * @return  String in format &quot;MILITARY~{offset}&quot;
     */
    @Override
    public String canonical() {

        return "MILITARY~" + this.offset.canonical();

    }

}
