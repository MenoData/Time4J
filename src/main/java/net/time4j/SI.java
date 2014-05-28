/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (SI.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoUnit;


/**
 * <p>Definiert die SI-Sekunde als das 9.192.631.770-fache der Periodendauer
 * der dem &Uuml;bergang zwischen den beiden Hyperfeinstrukturniveaus des
 * Grundzustands von Atomen des Nuklids C&auml;sium-133 entsprechenden
 * Strahlung. </p>
 *
 * <p>SI-Sekunden wurden erst 1967 definiert, so da&szlig; jede Referenz
 * auf sie vor diesem Jahr gegenstandslos sind. Time4J unterst&uuml;tzt
 * aber das Rechnen damit ab der UTC-Epoche <strong>1972-01-01</strong>, weil
 * insbesondere die Klasse {@code Moment} alles davor in mittleren
 * Sonnensekunden speichert. </p>
 *
 * @author  Meno Hochschild
 */
public enum SI
    implements ChronoUnit {

    //~ Statische Felder/Initialisierungen --------------------------------

    SECONDS(1.0),

    NANOSECONDS(1.0 / 1000000000);

    //~ Instanzvariablen --------------------------------------------------

    private final double length;

    //~ Konstruktoren -----------------------------------------------------

    private SI(double length) {
        this.length = length;
    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public double getLength() {

        return this.length;

    }

    @Override
    public boolean isCalendrical() {

        return false;

    }

}
