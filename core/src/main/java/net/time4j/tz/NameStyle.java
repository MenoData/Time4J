/*
 * -----------------------------------------------------------------------
 * Copyright © 2014-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (NameStyle.java) is part of project Time4J.
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

package net.time4j.tz;


/**
 * <p>Defines the style of a timezone name. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert den Stil eines Zeitzonennamens. </p>
 *
 * @author  Meno Hochschild
 */
public enum NameStyle {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Abbreviation in winter time
     * (usually standard time, Ireland is an exception however). </p>
     *
     * <p>Example: CET </p>
     */
    /*[deutsch]
     * <p>Abk&uuml;rzung in der Winterzeit
     * (meistens als Normalzeit bekannt, eine Ausnahme ist Irland). </p>
     *
     * <p>Beispiel: MEZ </p>
     */
    SHORT_STANDARD_TIME,

    /**
     * <p>Long name in winter time
     * (usually standard time, Ireland is an exception however). </p>
     *
     * <p>Example: Central European Time </p>
     */
    /*[deutsch]
     * <p>Langer Name in der Winterzeit
     * (meistens als Normalzeit bekannt, eine Ausnahme ist Irland). </p>
     *
     * <p>Beispiel: Mitteleurop&auml;ische Zeit </p>
     */
    LONG_STANDARD_TIME,

    /**
     * <p>Abbreviation in summer time (daylight saving). </p>
     *
     * <p>Example: CEST </p>
     */
    /*[deutsch]
     * <p>Abk&uuml;rzung in der Sommerzeit. </p>
     *
     * <p>Beispiel: MESZ </p>
     */
    SHORT_DAYLIGHT_TIME,

    /**
     * <p>Long name in summer time (daylight saving). </p>
     *
     * <p>Example: Central European Summer Time </p>
     */
    /*[deutsch]
     * <p>Langer Name in der Sommerzeit. </p>
     *
     * <p>Beispiel: Mitteleurop&auml;ische Sommerzeit </p>
     */
    LONG_DAYLIGHT_TIME;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Does this style denote an abbreviation? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt eine Abk&uuml;rzung vor? </p>
     *
     * @return  boolean
     */
    public boolean isAbbreviation() {

        return (
            (this == SHORT_STANDARD_TIME)
            || (this == SHORT_DAYLIGHT_TIME)
        );

    }

    /**
     * <p>Does this style denote a daylight saving time? </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Liegt eine Sommerzeitform vor? </p>
     *
     * @return  boolean
     */
    public boolean isDaylightSaving() {

        return (
            (this == SHORT_DAYLIGHT_TIME)
            || (this == LONG_DAYLIGHT_TIME)
        );

    }

}
