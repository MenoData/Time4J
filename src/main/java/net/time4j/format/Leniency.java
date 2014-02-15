/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Leniency.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
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
     * <p>Wertbereichs&uuml;berschreitungen werden mit einer Ausnahme quittiert.
     * Auch findet eine Konsistenzpr&uuml;fung statt, die pr&uuml;ft, da&szlig;
     * alle gegebenen Informationen zueinander passen m&uuml;ssen (z.B. der
     * richtige Wochentag passend zu einem Datum). Beim Parsen werden nur hier
     * die angegebenen Grenzen f&uuml;r die minimale und die maximale Anzahl
     * von zu interpretierenden Zeichen genau gepr&uuml;ft. </p>
     */
    STRICT,

    /**
     * <p>Mit dieser Standardvorgabe wird versucht, einen Mittelweg zwischen
     * einer pedantischen und laxen Strategie zu w&auml;hlen, indem zwar auf
     * die Konsistenz der Daten geachtet wird, aber bestimmte Einstellungen
     * wie die Breite von numerischen Elementen tolerant gehandhabt wird. </p>
     */
    SMART,

    /**
     * <p>Die Daten werden beim ersten passenden Treffer ohne weitere
     * Konsistenzpr&uuml;fung interpretiert. </p>
     *
     * <p>Auch aus dem definierten Wertbereich fallende Werte wie die
     * Uhrzeit &quot;T25:00:00&quot; werden akzeptiert und uminterpretiert. </p>
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
