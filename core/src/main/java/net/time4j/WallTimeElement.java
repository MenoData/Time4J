/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (WallTimeElement.java) is part of project Time4J.
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

package net.time4j;


/**
 * <p>Represents the wall time. </p>
 *
 * <p>Defines additional operators for moving a timestamp to a new wall time
 * possibly changing the day. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Uhrzeit. </p>
 *
 * <p>Definiert weitere Operatoren, die einen Zeitstempel zu einer neuen
 * Uhrzeit bewegen und bei Bedarf den Tag wechseln. </p>
 *
 * @author  Meno Hochschild
 */
public interface WallTimeElement
    extends ChronoElement<PlainTime> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Moves a timestamp to the next given wall time and change the day
     * if necessary. </p>
     *
     * @param   value   new wall time which is after current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die n&auml;chste angegebene Uhrzeit und
     * wechselt bei Bedarf den Tag. </p>
     *
     * @param   value   new wall time which is after current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    ElementOperator<PlainTimestamp> setToNext(PlainTime value);

    /**
     * <p>Moves a timestamp to the previous given wall time and change the day
     * backwards if necessary. </p>
     *
     * @param   value   new wall time which is before current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die vorherige angegebene Uhrzeit und
     * wechselt bei Bedarf den Tag r&uuml;ckw&auml;rts. </p>
     *
     * @param   value   new wall time which is before current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    ElementOperator<PlainTimestamp> setToPrevious(PlainTime value);

    /**
     * <p>Moves a timestamp to the next or same given wall time and change
     * the day if necessary. </p>
     *
     * @param   value   new wall time which is not before current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die n&auml;chste oder gleiche angegebene
     * Uhrzeit und wechselt bei Bedarf den Tag. </p>
     *
     * @param   value   new wall time which is not before current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    ElementOperator<PlainTimestamp> setToNextOrSame(PlainTime value);

    /**
     * <p>Moves a timestamp to the previous or same given wall time and
     * change the day backwards if necessary. </p>
     *
     * @param   value   new wall time which is not after current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die vorherige oder gleiche angegebene
     * Uhrzeit und wechselt bei Bedarf den Tag r&uuml;ckw&auml;rts. </p>
     *
     * @param   value   new wall time which is not after current wall time
     * @return  operator applicable on {@code PlainTimestamp}
     */
    ElementOperator<PlainTimestamp> setToPreviousOrSame(PlainTime value);

}
