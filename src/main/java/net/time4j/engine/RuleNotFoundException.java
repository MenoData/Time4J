/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (RuleNotFoundException.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Signalisiert das Fehlen einer chronologischen Regel f&uuml;r ein
 * chronologisches Element oder eine Zeiteinheit. </p>
 *
 * @author  Meno Hochschild
 */
public class RuleNotFoundException
    extends ChronoException {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -5638437652574160520L;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Erzeugt eine neue Instanz von <code>RuleNotFoundException</code>.
     *
     * @param   chronology  Chronologie, in der die Regel gesucht wird
     * @param   element     Element, zu dem keine Regel gefunden wurde
     */
    RuleNotFoundException(
        Chronology<?> chronology,
        ChronoElement<?> element
    ) {
        super(createMessage(chronology, element));

    }

    /**
     * Erzeugt eine neue Instanz von <code>RuleNotFoundException</code>.
     *
     * @param   chronology  Chronologie, in der die Regel gesucht wird
     * @param   unit        Zeiteinheit, zu der keine Regel gefunden wurde
     */
    RuleNotFoundException(
        Chronology<?> chronology,
        Object unit
    ) {
        super(createMessage(chronology, unit));

    }

    //~ Methoden ----------------------------------------------------------

    private static String createMessage(
        Chronology<?> chronology,
        ChronoElement<?> element
    ) {

        return (
            "Cannot find any rule for chronological element \""
            + element.name()
            + "\" in: "
            + chronology.getChronoType().getName());

    }

    private static String createMessage(
        Chronology<?> chronology,
        Object unit
    ) {

        return (
            "Cannot find any rule for chronological unit \""
            + getName(unit)
            + "\" in: "
            + chronology.getChronoType().getName());

    }

    private static String getName(Object unit) {

        if (unit instanceof Enum) {
            return Enum.class.cast(unit).name();
        } else {
            return unit.toString();
        }

    }

}
