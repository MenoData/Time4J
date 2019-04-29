/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FormattedContent.java) is part of project Time4J.
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

package net.time4j.calendar.bahai;


/**
 * Controls the type of content to be formatted in the Badi calendar.
 *
 * @author  Meno Hochschild
 * @see     BadiMonth
 * @see     BadiIntercalaryDays
 * @see     BadiCalendar#YEAR_OF_VAHID
 * @since   5.3
 */
/*[deutsch]
 * <p>Steuert den Typ des Inhalts von zu formatierenden Elementen im Badi-Kalender. </p>
 *
 * @author  Meno Hochschild
 * @see     BadiMonth
 * @see     BadiIntercalaryDays
 * @see     BadiCalendar#YEAR_OF_VAHID
 * @since   5.3
 */
public enum FormattedContent {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Mandates the usual way of transcription of element content in a given language.
     */
    /*[deutsch]
     * Bestimmt, da&szlig; ein Element in der sprach&uuml;blichen Umschreibung zu formatieren ist.
     */
    TRANSCRIPTION {
        @Override
        String variant() {
            return "t";
        }
    },

    /**
     * Calls for the meaning of element content in a given language.
     */
    /*[deutsch]
     * Bestimmt, da&szlig; die sprachabh&auml;ngige Bedeutung eines Elements zu formatieren ist.
     */
    MEANING {
        @Override
        String variant() {
            return "m";
        }
    },

    /**
     * Mandates the HTML way of transcription of element content in a given language.
     *
     * Only relevant for a few languages like English, Spanish and Portuguese.
     */
    /*[deutsch]
     * Bestimmt, da&szlig; ein Element in der HTML-Umschreibung zu formatieren ist.
     *
     * Nur f&uuml;r wenige Sprachen wie Englisch, Spanisch und Portugiesisch relevant.
     */
    HTML {
        @Override
        String variant() {
            return "h";
        }
    };

    //~ Methoden ----------------------------------------------------------

    // obtains the variant key in text resources
    abstract String variant();

}
