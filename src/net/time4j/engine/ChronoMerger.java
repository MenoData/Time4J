/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoMerger.java) is part of project Time4J.
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

import de.menodata.annotations4j.Nullable;
import net.time4j.base.TimeSource;


/**
 * <p>Erzeugt aus chronologischen Informationen einen neuen Zeitpunkt. </p>
 *
 * <p>Dieses Interface abstrahiert das Wissen der jeweiligen Zeitwertklasse,
 * wie aus beliebigen chronologischen Informationen eine neue Zeitwertinstanz
 * zu konstruieren ist und wird zum Beispiel beim Parsen von textuellen
 * Darstellungen zu Zeitwertobjekten ben&ouml;tigt. Die konkreten Algorithmen
 * sind in den jeweiligen Subklassen von {@code ChronoEntity} dokumentiert. </p>
 *
 * <p>Implementierungshinweis: Alle Klassen dieses Typs m&uuml;ssen
 * <i>immutable</i>, also unver&auml;nderlich sein. </p>
 *
 * @param   <T> Zeitwerttyp (kompatibel zu {@link ChronoEntity})
 * @author  Meno Hochschild
 */
public interface ChronoMerger<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Konstruiert eine neue Entit&auml;t, die der aktuellen Zeit
     * entspricht. </p>
     *
     * <p>In einer rein datumsbezogenen kalendarischen Chronologie wird hier
     * das aktuelle Tagesdatum erzeugt, indem zus&auml;tzlich &uuml;ber die
     * Attribute die notwendige Zeitzone ermittelt wird. </p>
     *
     * @param   clock           Quelle f&uuml;r die aktuelle Zeit
     * @param   attributes      Attribute zur Steuerung des Parse-Vorgangs
     * @return  neue Zeitwertinstanz oder {@code null} wenn die angegebenen
     *          Daten nicht ausreichen
     */
    @Nullable
    T createFrom(
        TimeSource<?> clock,
        AttributeQuery attributes
    );

    /**
     * <p>Konstruiert eine neue Entit&auml;t basierend auf den angegebenen
     * chronologischen Daten. </p>
     *
     * <p>Typischerweise wird mit verschiedenen Priorit&auml;ten das Argument
     * {@code parsedValues} nach Elementen abgefragt, die gruppenweise einen
     * Zeitwert konstruieren. Zum Beispiel kann ein Datum entweder &uuml;ber
     * die Epochentage, die Gruppe Jahr-Monat-Tag oder die Gruppe Jahr und Tag
     * des Jahres konstruiert werden. </p>
     *
     * <p>Gew&ouml;hnlich ruft ein Textinterpretierer diese Methode auf,
     * nachdem ein Text elementweise in chronologische Werte aufgel&ouml;st
     * wurde. </p>
     *
     * @param   parsedValues    interpretierte Elemente mitsamt ihren Werten
     * @param   attributes      Attribute zur Steuerung des Parse-Vorgangs
     * @return  neue Zeitwertinstanz oder {@code null} wenn die angegebenen
     *          Daten nicht ausreichen
     */
    @Nullable
    T createFrom(
        ChronoEntity<?> parsedValues,
        AttributeQuery attributes
    );

}
