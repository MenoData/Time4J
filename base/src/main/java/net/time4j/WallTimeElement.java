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
 * @since   1.2
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Uhrzeit. </p>
 *
 * <p>Definiert weitere Operatoren, die einen Zeitstempel zu einer neuen
 * Uhrzeit bewegen und bei Bedarf den Tag wechseln. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
public interface WallTimeElement
    extends ZonalElement<PlainTime> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Moves a timestamp to the next given wall time and change the day
     * if necessary. </p>
     *
     * @param   value   new wall time which is after current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die n&auml;chste angegebene Uhrzeit und
     * wechselt bei Bedarf den Tag. </p>
     *
     * @param   value   new wall time which is after current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    ElementOperator<?> setToNext(PlainTime value);

    /**
     * <p>Moves a timestamp to the previous given wall time and change the day
     * backwards if necessary. </p>
     *
     * @param   value   new wall time which is before current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die vorherige angegebene Uhrzeit und
     * wechselt bei Bedarf den Tag r&uuml;ckw&auml;rts. </p>
     *
     * @param   value   new wall time which is before current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    ElementOperator<?> setToPrevious(PlainTime value);

    /**
     * <p>Moves a timestamp to the next or same given wall time and change
     * the day if necessary. </p>
     *
     * @param   value   new wall time which is not before current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die n&auml;chste oder gleiche angegebene
     * Uhrzeit und wechselt bei Bedarf den Tag. </p>
     *
     * @param   value   new wall time which is not before current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    ElementOperator<?> setToNextOrSame(PlainTime value);

    /**
     * <p>Moves a timestamp to the previous or same given wall time and
     * change the day backwards if necessary. </p>
     *
     * @param   value   new wall time which is not after current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    /*[deutsch]
     * <p>Setzt einen Zeitpunkt auf die vorherige oder gleiche angegebene
     * Uhrzeit und wechselt bei Bedarf den Tag r&uuml;ckw&auml;rts. </p>
     *
     * @param   value   new wall time which is not after current wall time
     * @return  operator directly applicable on {@code PlainTimestamp}
     * @since   1.2
     * @see     PlainTimestamp#with(ElementOperator)
     */
    ElementOperator<?> setToPreviousOrSame(PlainTime value);

    /**
     * <p>Performs rounding to full hour in half rounding mode. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Rundet kaufm&auml;nnisch zur vollen Stunde. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainTime> roundedToFullHour();

    /**
     * <p>Performs rounding to full minute in half rounding mode. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Rundet kaufm&auml;nnisch zur vollen Minute. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainTime> roundedToFullMinute();

    /**
     * <p>Adjusts to next full hour. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Verstellt zur n&auml;chsten vollen Stunde. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainTime> setToNextFullHour();

    /**
     * <p>Adjusts to next full minute. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Verstellt zur n&auml;chsten vollen Minute. </p>
     *
     * @return  operator also applicable on {@code PlainTimestamp}
     * @since   1.2
     */
    ElementOperator<PlainTime> setToNextFullMinute();

}
