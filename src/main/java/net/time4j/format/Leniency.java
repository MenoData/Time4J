/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Leniency.java) is part of project Time4J.
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
 * <p>Nachsichtigkeitsmodus beim Parsen von Text zu chronologischen Typen. </p>
 *
 * @author  Meno Hochschild
 */
public enum Leniency {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Stellt das strikte Einhalten von Wertbereichsgrenzen und anderen
     * G&uuml;ltigkeitseinschr&auml;nkungen sicher. </p>
     *
     * <p>Wertbereichs&uuml;berschreitungen werden immer mit einer Ausnahme
     * quittiert. </p>
     *
     * <p>Auch findet eine Konsistenzpr&uuml;fung statt, die pr&uuml;ft,
     * da&szlig; alle gegebenen Informationen zueinander passen m&uuml;ssen
     * (z.B. der richtige Wochentag passend zu einem Datum). Beim Parsen werden
     * nur hier die angegebenen Grenzen f&uuml;r die minimale und die maximale
     * Anzahl von zu interpretierenden Zeichen genau gepr&uuml;ft. </p>
     */
    STRICT,

    /**
     * <p>Mit dieser Standardvorgabe wird versucht, einen Mittelweg zwischen
     * einer pedantischen und laxen Strategie zu w&auml;hlen, indem zwar auf
     * Wertbereichs&uuml;berschreitungen geachtet wird, aber bestimmte
     * Einstellungen wie die Breite von numerischen Elementen tolerant
     * gehandhabt werden. </p>
     *
     * <p>Eine Konsistenzpr&uuml;fung wie im strikten Modus findet nicht
     * statt. So wird ein falscher Wochentag ignoriert und das Datum eher
     * auf Basis von Jahr, Monat und Tag des Monats interpretiert. </p>
     */
    SMART,

    /**
     * <p>Die Daten werden beim ersten passenden Treffer ohne weitere
     * Konsistenzpr&uuml;fung interpretiert. </p>
     *
     * <p>Auch aus dem definierten Wertbereich fallende Werte wie die
     * Uhrzeit &quot;T25:00:00&quot; oder das Datum &quot;2014-02-31&quot;
     * werden akzeptiert und mit &Uuml;berlauf uminterpretiert. </p>
     */
    LAX;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ist dieser Nachsichtigkeitsmodus strikt? </p>
     *
     * @return  boolean
     */
    public boolean isStrict() {

        return (this == STRICT);

    }

    /**
     * <p>Ist dieser Nachsichtigkeitsmodus <i>smart</i>? </p>
     *
     * @return  boolean
     */
    public boolean isSmart() {

        return (this == SMART);

    }

    /**
     * <p>Ist dieser Nachsichtigkeitsmodus lax? </p>
     *
     * @return  boolean
     */
    public boolean isLax() {

        return (this == LAX);

    }

}
