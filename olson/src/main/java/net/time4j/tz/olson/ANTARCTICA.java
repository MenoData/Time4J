/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ANTARCTICA.java) is part of project Time4J.
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
 * <p>Contains all standard timezone IDs in Antarctica. </p>
 */
/*[deutsch]
 * <p>Enth&auml;lt alle Standard-Zeitzonen-IDs in der Antarktis. </p>
 */
public enum ANTARCTICA
    implements StdZoneIdentifier {

    //~ Statische Felder/Initialisierungen --------------------------------

    CASEY("Casey", "AQ"),
    DAVIS("Davis", "AQ"),
    DUMONTDURVILLE("DumontDUrville", "AQ"),
    MACQUARIE("Macquarie", "AU"),
    MAWSON("Mawson", "AQ"),
    MCMURDO("McMurdo", "AQ"),
    PALMER("Palmer", "AQ"),
    ROTHERA("Rothera", "AQ"),
    SYOWA("Syowa", "AQ"),
    VOSTOK("Vostok", "AQ");

    //~ Instanzvariablen --------------------------------------------------

    private final String id;
    private final String city;
    private final String country;

    //~ Konstruktoren -----------------------------------------------------

    private ANTARCTICA(
        String city,
        String country
    ) {

        this.id = "Antarctica/" + city;
        this.city = city;
        this.country = country;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String canonical() {

        return this.id;

    }

    @Override
    public String getRegion() {

        return "Antarctica";

    }

    @Override
    public String getCity() {

        return this.city;

    }

    @Override
    public String getCountry() {

        return this.country;

    }

}
