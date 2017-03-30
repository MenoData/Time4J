/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FlagElement.java) is part of project Time4J.
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

/**
 * <p>A specialized element for indicating special state during parsing. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
/*[deutsch]
 * <p>Ein Spezialelement, das einen besonderen Zustand w&auml;hrend eines
 * Interpretationsvorgangs anzeigen kann. </p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
public enum FlagElement
    implements ChronoElement<Boolean> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Identifies the existence of a leap second in any parsed chronological entity. </p>
     */
    /*[deutsch]
     * <p>Markiert die Existenz einer Schaltsekunde in einer gegebenen chronologischen Entit&auml;t. </p>
     */
    LEAP_SECOND,

    /**
     * <p>Identifies a summer or winter time information in any parsed chronological entity. </p>
     */
    /*[deutsch]
     * <p>Markiert eine Sommer- oder Winterzeitinformation in einer gegebenen chronologischen Entit&auml;t. </p>
     */
    DAYLIGHT_SAVING;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<Boolean> getType() {

        return Boolean.class;

    }

    @Override
    public char getSymbol() {

        return '\u0000';

    }

    @Override
    public int compare(
        ChronoDisplay o1,
        ChronoDisplay o2
    ) {

        boolean b1 = o1.contains(this);
        boolean b2 = o2.contains(this);
        return ((b1 == b2) ? 0 : (b1 ? 1 : -1));

    }

    @Override
    public Boolean getDefaultMinimum() {

        return Boolean.FALSE;

    }

    @Override
    public Boolean getDefaultMaximum() {

        return Boolean.TRUE;

    }

    @Override
    public boolean isDateElement() {

        return false;

    }

    @Override
    public boolean isTimeElement() {

        return false;

    }

    @Override
    public boolean isLenient() {

        return false;

    }

    @Override
    public String getDisplayName(Locale language) {

        return this.name();

    }

}
