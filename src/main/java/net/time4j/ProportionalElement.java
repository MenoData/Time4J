/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ProportionalElement.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoFunction;
import net.time4j.engine.ChronoOperator;

import java.math.BigDecimal;


/**
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
     * @return  lenient operator
     */
    ChronoOperator<T> setLenient(V value);

    /**
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
     * <p>Rundet dieses chronologische Element so, da&szlig; sein
     * numerischer Wert m&ouml;glichst ein ganzzahliges Vielfaches der
     * angegebenen Schrittweite annimmt. </p>
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
