/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoExtension.java) is part of project Time4J.
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

package net.time4j.engine;

import java.util.Locale;
import java.util.Set;


/**
 * <p>Defines a configuration-dependent extension of the chronological
 * elements of a {@code Chronology}. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert eine konfigurationsabh&auml;ngige Erweiterung der
 * chronologischen Elemente einer Chronologie. </p>
 *
 * @author  Meno Hochschild
 */
public interface ChronoExtension {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the element set for given configuration. </p>
     *
     * <p>An empty {@code Set} indicates that this extension is not relevant
     * for the given configuration. </p>
     *
     * @param   locale          language and country setting
     * @param   attributes      configuration attributes
     * @return  extended element model
     */
    /*[deutsch]
     * <p>Liefert das Elementmodell zur angegebenen Konfiguration. </p>
     *
     * <p>Ein leeres {@code Set} als R&uuml;ckgabe signalisiert dem Aufrufer,
     * da&szlig; f&uuml;r die gegebene Konfiguration die Erweiterung nicht
     * relevant ist. </p>
     *
     * @param   locale          language and country setting
     * @param   attributes      configuration attributes
     * @return  extended element model
     */
    Set<ChronoElement<?>> getElements(
        Locale locale,
        AttributeQuery attributes
    );

    /**
     * <p>Updates the given value source if necessary in order to resolve
     * the values of extension elements to values of standard elements. </p>
     *
     * <p>Implementations are allowed to use {@code null} as pseudo-value
     * in order to delete an element from given source via the expression
     * {@code parsedValues.with(element, null)}. Note: The argument has
     * exceptionally no chronology. </p>
     *
     * @param   <T> generic type of applicable chronological entity
     * @param   entity  any kind of map from chronological elements to
     *                  their values (note that the main use case of parsed
     *                  data has no chronology and allows the virtual value
     *                  {@code null} to be set as indication for removing
     *                  associated element)
     * @return  eventually changed argument
     * @see     ChronoEntity#with(ChronoElement, Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Aktualisiert bei Bedarf die angegebene Wertquelle, um die Werte von
     * Erweiterungselementen zu Werten von Standardelementen aufzul&ouml;sen.
     * </p>
     *
     * <p>Implementierungen d&uuml;rfen hier auch {@code null} als Pseudo-Wert
     * benutzen, um ein Element mittels {@code parsedValues.with(element, null)}
     * aus der Wertquelle zu l&ouml;schen. Zu beachten: Das Argument hat als
     * Ausnahmefall keine Chronologie. </p>
     *
     * @param   <T> generic type of applicable chronological entity
     * @param   entity  any kind of map from chronological elements to
     *                  their values (note that the main use case of parsed
     *                  data has no chronology and allows the virtual value
     *                  {@code null} to be set as indication for removing
     *                  associated element)
     * @return  eventually changed argument
     * @see     ChronoEntity#with(ChronoElement, Object)
     *          ChronoEntity.with(ChronoElement, V)
     */
    <T extends ChronoEntity<T>> T resolve(T entity);

}
