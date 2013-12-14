/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StartOfDay.java) is part of project Time4J.
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
 * <p>Definiert den Beginn des Tages. </p>
 *
 * @author  Meno Hochschild
 */
public interface StartOfDay {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Der Tag f&auml;ngt um Mitternacht an. </p>
     *
     * <p>Diese Einstellung gilt standardm&auml;&szlig;ig f&uuml;r alle
     * ISO-8601-konformen Datumsangaben. Eine Ausnahme sind allerdings in
     * bestimmten Zeitzonen auch im ISO-Standard Tage, die wegen einer
     * Sommerzeitumstellung erst nach Mitternacht starten. </p>
     */
    static final StartOfDay MIDNIGHT =
        new StartOfDay() {
            @Override
            public int getShift(long epochDays) {
                return 0;
            }
            @Override
            public boolean isFixed() {
                return true;
            }
        };

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt die Startzeit des angegebenen Tages in Sekunden relativ
     * zu Mitternacht (UTC). </p>
     *
     * <p>Beispiel: In Israel f&auml;ngt der hebr&auml;ische Tag gesetzlich
     * bereits um 18 Uhr am Vortag an, also ist die Verschiebung negativ mit
     * dem Wert {@code -6 * 60 * 60}. Im religi&ouml;sen Verwendungskontext
     * mu&szlig; sogar die konkrete Uhrzeit des Sonnenuntergangs herangezogen
     * werden. In letzterem Fall wird f&uuml;r die erforderliche astronomische
     * Berechnung das Argument zur saisonalen Bestimmung des Sonnenstands
     * benutzt. </p>
     *
     * @param   epochDays   Tagesnummer als Anzahl der Tage relativ zur
     *                      UTC-Epoche [1972-01-01]
     * @return  Verschiebung in Sekunden relativ zu Mitternacht
     */
    int getShift(long epochDays);

    /**
     * <p>Erfolgt der Tagesbeginn immer zu einer festen Uhrzeit? </p>
     *
     * @return  {@code true} wenn der Tagesbeginn immer zur gleichen Uhrzeit
     *          stattfindet, sonst {@code false}
     */
    boolean isFixed();

}
