/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ValidationElement.java) is part of project Time4J.
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


/**
 * <p>A specialized element for communicating validation failures
 * during parsing. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     ChronoMerger
 */
/*[deutsch]
 * <p>Ein Spezialelement, das einen Validierungsfehler w&auml;hrend eines
 * Interpretationsvorgangs anzeigen kann. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @see     ChronoMerger
 */
public enum ValidationElement
    implements ChronoElement<String> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Identifies an error message in any parsed chronological entity. </p>
     */
    /*[deutsch]
     * <p>Markiert eine Fehlermeldung in einer gegebenen chronologischen
     * Entit&auml;t. </p>
     */
    ERROR_MESSAGE;

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<String> getType() {

        return String.class;

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
    public String getDefaultMinimum() {

        return "";

    }

    @Override
    public String getDefaultMaximum() {

        return String.valueOf('\uFFFF');

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

}
