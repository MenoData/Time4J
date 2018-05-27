/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AUSTRALIA.java) is part of project Time4J.
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

package net.time4j.tz.olson;


/**
 * <p>Contains all standard timezone IDs in Australia. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in Australien. </p>
 */
public enum AUSTRALIA
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

    ADELAIDE("Adelaide"),
    BRISBANE("Brisbane"),
    BROKEN_HILL("Broken_Hill"),
    CURRIE("Currie"),
    DARWIN("Darwin"),
    EUCLA("Eucla"),
    HOBART("Hobart"),
    LINDEMAN("Lindeman"),
    LORD_HOWE("Lord_Howe"),
    MELBOURNE("Melbourne"),
    PERTH("Perth"),
    SYDNEY("Sydney");

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;

    //~ Konstruktoren -----------------------------------------------------

    private AUSTRALIA(String city) {

        this.id = "Australia/" + city;
        this.city = city;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String canonical() {

        return this.id;

    }

    @Override
    public String getRegion() {

        return "Australia";

    }

    @Override
    public String getCity() {

        return this.city;

    }

    @Override
    public String getCountry() {

        return "AU";

    }

}

