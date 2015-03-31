/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ProportionalElement.java) is part of project Time4J.
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

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoOperator;

import java.math.BigDecimal;


/**
 * <p>Defines an element which can interprete its value as proportional
 * value. </p>
 *
 * @param   <V> generic number type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert ein Element, das seinen Wert als Proportionalwert
 * interpretieren kann. </p>
 *
 * @param   <V> generic number type of element values
 * @param   <T> generic type of target entity an operator is applied to
 * @author  Meno Hochschild
 */
public interface ProportionalElement<V extends Number, T>
    extends AdjustableElement<V, T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Defines a query which interpretes the value of this element as
     * proportional rational number within the range between minimum and
     * maximum. </p>
     *
     * <p>Smaller elements with greater precision are not taken in account.
     * Following expression serves as formula for internal calculation:
     * {@code (value - min) / (max - min + 1)}. Example: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  PlainTime time = PlainTime.of(12, 45, 30); // T12:45:30
     *  System.out.println(time.get(MINUTE_OF_HOUR.ratio()));
     *  // output: 0.75 [= (45 - 0) / (59 - 0 + 1)]
     * </pre>
     *
     * <p>Note: In timezone-related timestamps possible jumps in local time
     * will be conserved. That means that minimum and maximum do not take
     * in account if they fall into a daylight saving gap or if there is
     * any kind of offset shift between them. </p>
     *
     * @return  query for proportional value
     */
    /*[deutsch]
     * <p>Definiert eine Abfrage, die den Wert dieses Elements als
     * Verh&auml;ltniszahl innerhalb seines Bereichs zwischen Minimum
     * und Maximum interpretiert. </p>
     *
     * <p>Kleinere Elemente mit gr&ouml;&szlig;erer Genauigkeit werden in der
     * Berechnung nicht ber&uuml;cksichtigt. Als Formel wird der Ausdruck
     * {@code (value - min) / (max - min + 1)} verwendet. Beispiel: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  PlainTime time = PlainTime.of(12, 45, 30); // T12:45:30
     *  System.out.println(time.get(MINUTE_OF_HOUR.ratio()));
     *  // Ausgabe: 0.75 [= (45 - 0) / (59 - 0 + 1)]
     * </pre>
     *
     * <p>Zu beachten: In zeitzonen-bezogenen Zeitstempeln bleiben eventuelle
     * Zeitzonenspr&uuml;nge erhalten. Das bedeutet, da&szlig; Minimum und
     * Maximum nicht ber&uuml;cksichtigen, ob sie in eine L&uuml;cke fallen
     * oder zwischen ihnen ein Offset-Sprung existiert. </p>
     *
     * @return  query for proportional value
     */
    ChronoFunction<ChronoEntity<?>, BigDecimal> ratio();

    /**
     * <p>Adjusts any kind of entity such that this element will be set to
     * the given value in lenient mode. </p>
     *
     * <pre>
     *  import static net.time4j.PlainDate.MONTH_OF_YEAR;
     *
     *  System.out.println(
     *      PlainDate.of(2011, 5, 31).with(MONTH_OF_YEAR.setLenient(13)));
     *  // output: 2012-01-31 (addition of 13 - 5 = 8 Monaten)
     * </pre>
     *
     * <p>Leniency does not always prevent exceptions. For example setting
     * of extreme values like {@code Long.MIN_VALUE} can cause an
     * {@code ArithmeticException}. Furthermore, moving the value of
     * {@code SECOND_OF_MINUTE} to the past applied on a {@code Moment}
     * can trigger a {@code ChronoException} if this action changes to
     * the pre-UTC-era before 1972. </p>
     *
     * @param   value   new value to be set in lenient way
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    /*[deutsch]
     * <p>Passt eine beliebige Entit&auml;t so an, da&szlig; dieses Element
     * auf den angegebenen Wert im Nachsichtigkeitsmodus gesetzt wird. </p>
     *
     * <pre>
     *  import static net.time4j.PlainDate.MONTH_OF_YEAR;
     *
     *  System.out.println(
     *      PlainDate.of(2011, 5, 31).with(MONTH_OF_YEAR.setLenient(13)));
     *  // Ausgabe: 2012-01-31 (Addition von 13 - 5 = 8 Monaten)
     * </pre>
     *
     * <p>Nachsichtigkeit bedeutet nicht, da&szlig; eine Ausnahme
     * unm&ouml;glich ist. Zum Beispiel kann das Setzen von extremen Werten
     * wie {@code Long.MIN_VALUE} eine {@code ArithmeticException} verursachen.
     * Auch kann etwa das Setzen des Elements {@code SECOND_OF_MINUTE}
     * auf einen {@code Moment} angewandt eine {@code ChronoException}
     * ausl&ouml;sen, wenn damit ein Wechsel von der UTC-&Auml;ra in die
     * UT-&Auml;ra vor 1972 verbunden ist. </p>
     *
     * @param   value   new value to be set in lenient way
     * @return  operator directly applicable also on {@code PlainTimestamp}
     */
    ElementOperator<T> setLenient(V value);

    /**
     * <p>Rounds this chronological element up and makes its numerical
     * value an integral multiple of given step width if possible. </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedUp(15)));
     *  // output: T18:45
     * </pre>
     *
     * <p>The new element value will always be set in lenient mode.
     * Example: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 49).with(MINUTE_OF_HOUR.roundedUp(15)));
     *  // output: T19 corresponding to T18:60 (60 as multiple of 15)
     * </pre>
     *
     * @param   stepwidth   controls the limits within the rounding will occur
     * @return  rounding operator in ceiling mode
     * @see     #roundedDown(int)
     */
    /*[deutsch]
     * <p>Rundet dieses chronologische Element so auf, da&szlig; sein
     * numerischer Wert m&ouml;glichst ein ganzzahliges Vielfaches der
     * angegebenen Schrittweite annimmt. </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedUp(15)));
     *  // Ausgabe: T18:45
     * </pre>
     *
     * <p>Der neu errechnete Elementwert wird immer nachsichtig mittels
     * {@link #setLenient(Number)} gesetzt. Beispiel: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 49).with(MINUTE_OF_HOUR.roundedUp(15)));
     *  // Ausgabe: T19 entsprechend T18:60 (60 als Vielfaches von 15)
     * </pre>
     *
     * @param   stepwidth   controls the limits within the rounding will occur
     * @return  rounding operator in ceiling mode
     * @see     #roundedDown(int)
     */
    ChronoOperator<T> roundedUp(int stepwidth);

    /**
     * <p>Rounds this chronological element up or down and makes its
     * numerical value an integral multiple of given step width if
     * possible. </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedHalf(15)));
     *  // output: T18:45
     *  System.out.println(
     *      PlainTime.of(18, 37).with(MINUTE_OF_HOUR.roundedHalf(15)));
     *  // output: T18:30
     * </pre>
     *
     * <p>The new element value will always be set in lenient mode. Is the
     * current value nearer to the lower limit then this function will
     * round down else round up. </p>
     *
     * @param   stepwidth   controls the limits within the rounding will occur
     * @return  rounding operator in ceiling or floor mode dependent on
     *          actual element value
     * @see     #roundedUp(int)
     * @see     #roundedDown(int)
     */
    /*[deutsch]
     * <p>Rundet dieses chronologische Element auf oder ab und stellt
     * sicher, da&szlig; sein numerischer Wert m&ouml;glichst ein
     * ganzzahliges Vielfaches der angegebenen Schrittweite annimmt. </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedHalf(15)));
     *  // Ausgabe: T18:45
     *  System.out.println(
     *      PlainTime.of(18, 37).with(MINUTE_OF_HOUR.roundedHalf(15)));
     *  // Ausgabe: T18:30
     * </pre>
     *
     * <p>Der neu errechnete Elementwert wird immer nachsichtig mittels
     * {@link #setLenient(Number)} gesetzt. Ist der aktuelle Wert n&auml;her
     * an der unteren Grenze, dann wird abgerundet, sonst aufgerundet. </p>
     *
     * @param   stepwidth   controls the limits within the rounding will occur
     * @return  rounding operator in ceiling or floor mode dependent on
     *          actual element value
     * @see     #roundedUp(int)
     * @see     #roundedDown(int)
     */
    ChronoOperator<T> roundedHalf(int stepwidth);

    /**
     * <p>Rounds this chronological element down and makes its numerical
     * value an integral multiple of given step width if possible. </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedDown(15)));
     *  // output: T18:30
     * </pre>
     *
     * <p>The new element value will always be set in lenient mode.
     * Example: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.CLOCK_HOUR_OF_DAY;
     *
     *  System.out.println(
     *      PlainTime.of(2, 30).with(CLOCK_HOUR_OF_DAY.roundedDown(3)));
     *  // output: T0
     * </pre>
     *
     * @param   stepwidth   controls the limits within the rounding will occur
     * @return  rounding operator in floor mode
     * @see     #roundedUp(int)
     */
    /*[deutsch]
     * <p>Rundet dieses chronologische Element so ab, da&szlig; sein
     * numerischer Wert m&ouml;glichst ein ganzzahliges Vielfaches der
     * angegebenen Schrittweite annimmt. </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.MINUTE_OF_HOUR;
     *
     *  System.out.println(
     *      PlainTime.of(18, 38).with(MINUTE_OF_HOUR.roundedDown(15)));
     *  // Ausgabe: T18:30
     * </pre>
     *
     * <p>Der neu errechnete Elementwert wird immer nachsichtig mittels
     * {@link #setLenient(Number)} gesetzt. Beispiel: </p>
     *
     * <pre>
     *  import static net.time4j.PlainTime.CLOCK_HOUR_OF_DAY;
     *
     *  System.out.println(
     *      PlainTime.of(2, 30).with(CLOCK_HOUR_OF_DAY.roundedDown(3)));
     *  // Ausgabe: T0
     * </pre>
     *
     * @param   stepwidth   controls the limits within the rounding will occur
     * @return  rounding operator in floor mode
     * @see     #roundedUp(int)
     */
    ChronoOperator<T> roundedDown(int stepwidth);

}
