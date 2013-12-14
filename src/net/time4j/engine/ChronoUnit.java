/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoUnit.java) is part of project Time4J.
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
 * <p>Externe Zeiteinheiten, die nicht in einer Chronologie (Zeitachse)
 * registriert sind, k&ouml;nnen dieses Interface implementieren, um
 * Standardberechnungen in Zeitspannen und Symbolformatierungen zu
 * unterst&uuml;tzen. </p>
 *
 * <p><strong>Namenskonvention:</strong> Zeiteinheiten haben Java-Namen in
 * Pluralform. </p>
 *
 * @author  Meno Hochschild
 */
public interface ChronoUnit {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Definiert die typische L&auml;nge dieser Zeiteinheit in Sekunden
     * ohne Ber&uuml;cksichtigung von Anomalien wie Zeitzoneneffekten oder
     * Schaltsekunden. </p>
     *
     * @return  Sekundenwert als Dezimalzahl
     */
    double getLength();

    /**
     * <p>Ist diese Zeiteinheit kalendarisch beziehungsweise mindestens
     * so lange wie ein Kalendertag? </p>
     *
     * <p>Implementierungshinweis: Die Methode mu&szlig; konsistent
     * mit der typischen Standardl&auml;nge sein. Der Ausdruck
     * {@code Double.compare(unit.getLength(), 86400.0) >= 0} ist
     * &auml;quivalent zu {@code unit.isCalendrical()}. </p>
     *
     * @return  {@code true} wenn mindestens so lang wie ein Tag,
     *          sonst {@code false}
     */
    boolean isCalendrical(); // TODO: Ab Java 8 (Time4J-2.0) default-Methode

}
