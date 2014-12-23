/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (LeapSecondEvent.java) is part of project Time4J.
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

package net.time4j.scale;

import net.time4j.base.GregorianDate;


/**
 * <p>This interface describes that during the last minute of a given
 * calendar day an UTC-leapsecond was either introducted or left out. </p>
 *
 * <p>Example: If the day is given as [1972-06-30] then this means a leapsecond
 * in the last minute short before midnight of following day, namely at
 * [1972-06-30T23:59:60Z]. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Beschreibt, da&szlig; in der letzten Minute des hier bestimmten Tags eine
 * UTC-Schaltsekunde eingef&uuml;gt oder eine Sekunde ausgelassen wurde. </p>
 *
 * <p>Beispiel: Ist der Tag [1972-06-30] angeben, dann bedeutet das eine neue
 * Schaltsekunde in der letzten Minute kurz vor Mitternacht des Folgetags,
 * n&auml;mlich um [1972-06-30T23:59:60Z]. </p>
 *
 * @author  Meno Hochschild
 */
public interface LeapSecondEvent {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the date of leapsecond introduction. </p>
     *
     * @return  gregorian date where a leap second is inserted at the end
     */
    /*[deutsch]
     * <p>Ermittelt das Datum der Zeitumstellung. </p>
     *
     * @return  gregorian date where a leap second is inserted at the end
     */
    GregorianDate getDate();

    /**
     * <p>Returns the leapsecond shift of this event only. </p>
     *
     * <p>Note: Until the year 2014 there was only the shift of one
     * second extra so the return value of this method is always {@code +1}.
     * But negative leapseconds with the shift {@code -1} remain
     * theoretically possible according to UTC definition. </p>
     *
     * @return  event-related shift in seconds ({@code != 0})
     */
    /*[deutsch]
     * <p>Liefert die Schaltsekundenverschiebung nur dieses Ereignisses. </p>
     *
     * <p>Anmerkung: Bis zum Jahr 2014 gab es nur den Versatz von jeweils
     * einer Sekunde extra, also ist der R&uuml;ckgabewert dieser Methode
     * bis dahin immer {@code +1}. Aber auch negative Schaltsekunden mit
     * der Verschiebung {@code -1} bleiben nach der Norm prinzipiell
     * m&ouml;glich. </p>
     *
     * @return  event-related shift in seconds ({@code != 0})
     */
    int getShift();

}
