/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StartOfDay.java) is part of project Time4J.
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
 * <p>Defines the start of a calendar day. </p>
 * 
 * <p>Background is the fact that some calendar systems start
 * calendar days at another time than midnight. For example the
 * islamic calendars start the day at sunset on previous day. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert den Beginn des Tages. </p>
 * 
 * <p>Hintergrund ist die Tatsache, da&szlig; einige Kalendersystem
 * den Tag zu einer anderen Zeit als Mitternacht starten. Zum
 * Beispiel fangen die islamischen Kalender den Tag zum
 * Sonnenuntergang am Vortag an. </p>
 *
 * @author  Meno Hochschild
 */
public interface StartOfDay {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>The calendar day starts at midnight. </p>
     *
     * <p>This setting is valid for all ISO-8601-systems by default. An
     * exception are certain timezones where days might start later than
     * midnight due to daylight-saving-change, for example in Brazil. </p>
     */
    /*[deutsch]
     * <p>Der Tag f&auml;ngt um Mitternacht an. </p>
     *
     * <p>Diese Einstellung gilt standardm&auml;&szlig;ig f&uuml;r alle
     * ISO-8601-konformen Datumsangaben. Eine Ausnahme sind allerdings in
     * bestimmten Zeitzonen auch im ISO-Standard Tage, die wegen einer
     * Sommerzeitumstellung erst nach Mitternacht starten, zum Beispiel
     * in Brasilien. </p>
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
     * <p>Queries the start time of given calendar day in seconds
     * relative to midnight (UTC). </p>
     *
     * <p>Example: In Israel the legal hebrew calendar day starts at
     * 6 PM on previous day, so the shift is negative with the value
     * {@code -6 * 60 * 60}. In a religious context the concrete time
     * of sunset is important so the given argument can be used to
     * calculate the season-dependent position of the sun. </p>
     *
     * @param   epochDays   count of days relative to UTC epoch [1972-01-01]
     * @return  shift in seconds relative to midnight (positive or negative)
     */
    /*[deutsch]
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
     * @param   epochDays   count of days relative to UTC epoch [1972-01-01]
     * @return  shift in seconds relative to midnight (positive or negative)
     */
    int getShift(long epochDays);

    /**
     * <p>Queries if the start of a calendar day always happens at the
     * same time. </p>
     *
     * @return  {@code true} if the day always starts at the same wall time
     *          else {@code false}
     */
    /*[deutsch]
     * <p>Erfolgt der Tagesbeginn immer zu einer festen Uhrzeit? </p>
     *
     * @return  {@code true} if the day always starts at the same wall time
     *          else {@code false}
     */
    boolean isFixed();

}
