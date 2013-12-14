/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoExtension.java) is part of project Time4J.
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

import java.util.Set;


/**
 * <p>Definiert eine konfigurationsabh&auml;ngige Erweiterung der
 * chronologischen Elemente einer Chronologie. </p>
 *
 * @author  Meno Hochschild
 */
public interface ChronoExtension {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liefert das Elementmodell zur angegebenen Konfiguration. </p>
     *
     * <p>Ein leeres Set als R&uuml;ckgabe signalisiert dem Aufrufer, da&szlig;
     * f&uuml;r die gegebene Konfiguration die Erweiterung nicht relevant
     * ist. </p>
     *
     * @param   attributes      Konfigurationsattribute
     * @return  erweitertes Elementmodell
     */
    Set<ChronoElement<?>> getElements(AttributeQuery attributes);

    /**
     * <p>Aktualisiert bei Bedarf die angegebene Wertquelle, um die Werte von
     * Erweiterungselementen zu Werten von Standardelementen aufzul&ouml;sen.
     * </p>
     *
     * <p>Implementierungen d&uuml;rfen hier auch {@code null} als Pseudo-Wert
     * benutzen, um ein Element mittels {@code parsedValues.with(element, null)}
     * aus der Wertquelle zu l&ouml;schen. Zu beachten: Das Argument hat als
     * Ausnahmefall keine Chronologie. </p>
     *
     * @param   parsedValues    interpretierte Elemente mitsamt ihren Werten
     * @return  bei Bedarf ge&auml;nderte Instanz
     * @see     ChronoEntity#with(ChronoElement, Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    <T extends ChronoEntity<T>> T resolve(T parsedValues);

}
