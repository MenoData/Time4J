/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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
 * elements of a {@code Chronology} used in formatting and parsing. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert eine konfigurationsabh&auml;ngige Erweiterung der
 * chronologischen Elemente einer Chronologie und wird beim Formatieren
 * und Parsen verwendet. </p>
 *
 * @author  Meno Hochschild
 */
public interface ChronoExtension {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Will be called by a {@code java.util.ServiceLoader} in order to determine
     * if the given chronological type should register this extension or not. </p>
     *
     * @param   chronoType  chronological type
     * @return  {@code true} if given type should register this extension else {@code false}
     * @since   3.0
     */
    /*[deutsch]
     * <p>Wird von einem {@code java.util.ServiceLoader} aufgerufen, um zu bestimmen,
     * ob der angegebene chronologische Typ diese Erweiterung registrieren soll. </p>
     *
     * @param   chronoType  chronological type
     * @return  {@code true} if given type should register this extension else {@code false}
     * @since   3.0
     */
    boolean accept(Class<?> chronoType);

    /**
     * <p>Returns the element set for given configuration. </p>
     *
     * <p>An empty {@code Set} indicates that this extension is not relevant
     * for the given configuration. </p>
     *
     * @param   locale          language and country setting
     * @param   attributes      global configuration attributes of formatter
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
     * @param   attributes      global configuration attributes of formatter
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
     * {@code entity.with(element, null)}. Note: The argument has
     * exceptionally no chronology. </p>
     *
     * @param   entity  any kind of map from chronological elements to
     *                  their values (note that the main use case of parsed
     *                  data has no chronology and allows the virtual value
     *                  {@code null} to be set as indication for removing
     *                  associated element)
     * @param   locale          language and country setting
     * @param   attributes      global configuration attributes of parser
     * @return  eventually changed entity
     * @throws  IllegalArgumentException if resolving fails due to inconsistencies
     * @since   3.0
     * @see     ChronoEntity#with(ChronoElement, Object) ChronoEntity.with(ChronoElement, V)
     */
    /*[deutsch]
     * <p>Aktualisiert bei Bedarf die angegebene Wertquelle, um die Werte von
     * Erweiterungselementen zu Werten von Standardelementen aufzul&ouml;sen.
     * </p>
     *
     * <p>Implementierungen d&uuml;rfen hier auch {@code null} als Pseudo-Wert
     * benutzen, um ein Element mittels {@code entity.with(element, null)}
     * aus der Wertquelle zu l&ouml;schen. Zu beachten: Das Argument hat als
     * Ausnahmefall keine Chronologie. </p>
     *
     * @param   entity  any kind of map from chronological elements to
     *                  their values (note that the main use case of parsed
     *                  data has no chronology and allows the virtual value
     *                  {@code null} to be set as indication for removing
     *                  associated element)
     * @param   locale          language and country setting
     * @param   attributes      global configuration attributes of parser
     * @return  eventually changed entity
     * @throws  IllegalArgumentException if resolving fails due to inconsistencies
     * @since   3.0
     * @see     ChronoEntity#with(ChronoElement, Object) ChronoEntity.with(ChronoElement, V)
     */
    ChronoEntity<?> resolve(
        ChronoEntity<?> entity,
        Locale locale,
        AttributeQuery attributes
    );

}
