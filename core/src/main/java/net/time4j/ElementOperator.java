/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ElementOperator.java) is part of project Time4J.
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

import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoOperator;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;


/**
 * <p>Defines any manipulation of date or wall time objects following the
 * strategy design pattern. </p>
 *
 * @param       <T> generic target type this operator is applied to
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert eine Manipulation von Datums- oder Uhrzeitobjekten nach
 * dem Strategy-Entwurfsmuster. </p>
 *
 * @param       <T> generic target type this operator is applied to
 * @author      Meno Hochschild
 */
public abstract class ElementOperator<T>
    implements ChronoOperator<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final int OP_NEW_VALUE = -1;
    static final int OP_MINIMIZE = 0;
    static final int OP_MAXIMIZE = 1;
    static final int OP_DECREMENT = 2;
    static final int OP_INCREMENT = 3;
    static final int OP_FLOOR = 4;
    static final int OP_CEILING = 5;

    static final int OP_LENIENT = 6;
    static final int OP_WIM = 7;
    static final int OP_YOW = 8;

    static final int OP_NAV_NEXT = 9;
    static final int OP_NAV_PREVIOUS = 10;
    static final int OP_NAV_NEXT_OR_SAME = 11;
    static final int OP_NAV_PREVIOUS_OR_SAME = 12;

    static final int OP_ROUND_FULL_HOUR = 13;
    static final int OP_ROUND_FULL_MINUTE = 14;
    static final int OP_NEXT_FULL_HOUR = 15;
    static final int OP_NEXT_FULL_MINUTE = 16;

    static final int OP_FIRST_DAY_OF_NEXT_MONTH = 17;
    static final int OP_FIRST_DAY_OF_NEXT_QUARTER = 18;
    static final int OP_FIRST_DAY_OF_NEXT_YEAR = 19;
    static final int OP_LAST_DAY_OF_PREVIOUS_MONTH = 20;
    static final int OP_LAST_DAY_OF_PREVIOUS_QUARTER = 21;
    static final int OP_LAST_DAY_OF_PREVIOUS_YEAR = 22;

    //~ Instanzvariablen --------------------------------------------------

    private final ChronoElement<?> element;
    private final int type;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Paket-privater Konstruktor. </p>
     *
     * @param   element         element this operator will be applied on
     * @param   type            operator type
     */
    ElementOperator(
        ChronoElement<?> element,
        int type
    ) {
        super();

        this.element = element;
        this.type = type;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates an operator which can adjust a {@link Moment} in the
     * system timezone. </p>
     *
     * <p>Note: Usually the operator converts the given {@code Moment} to
     * a {@code PlainTimestamp} then processes this local timestamp and
     * finally converts the result back to a new {@code Moment}. A special
     * case are incrementing and decrementing of (sub-)second elements which
     * eventually operate directly on the UTC timeline. </p>
     *
     * @return  operator with the default system timezone reference,
     *          applicable on instances of {@code Moment}
     */
    /*[deutsch]
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe der Systemzeitzonenreferenz anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Dekrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @return  operator with the default system timezone reference,
     *          applicable on instances of {@code Moment}
     */
    public ChronoOperator<Moment> inStdTimezone() {

        return new Moment.Operator(this.onTimestamp(), this.element, this.type);

    }

    /**
     * <p>Creates an operator which can adjust a {@link Moment} in the
     * given timezone. </p>
     *
     * <p>Note: Usually the operator converts the given {@code Moment} to
     * a {@code PlainTimestamp} then processes this local timestamp and
     * finally converts the result back to a new {@code Moment}. A special
     * case are incrementing and decrementing of (sub-)second elements which
     * eventually operate directly on the UTC timeline. </p>
     *
     * @param   tzid        timezone id
     * @return  operator with the given timezone reference, applicable on
     *          instances of {@code Moment}
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe einer Zeitzonenreferenz anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Dekrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @param   tzid        timezone id
     * @return  operator with the given timezone reference, applicable on
     *          instances of {@code Moment}
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public final ChronoOperator<Moment> inTimezone(TZID tzid) {

        return new Moment.Operator(
            this.onTimestamp(),
            this.element,
            this.type,
            Timezone.of(tzid)
        );

    }

    /**
     * <p>Creates an operator which can adjust a {@link Moment} in the
     * given timezone. </p>
     *
     * <p>Note: Usually the operator converts the given {@code Moment} to
     * a {@code PlainTimestamp} then processes this local timestamp and
     * finally converts the result back to a new {@code Moment}. A special
     * case are incrementing and decrementing of (sub-)second elements which
     * eventually operate directly on the UTC timeline. </p>
     *
     * @param   tz          timezone
     * @return  operator with the given timezone reference, applicable on
     *          instances of {@code Moment}
     */
    /*[deutsch]
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe einer Zeitzonenreferenz anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Dekrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @param   tz          timezone
     * @return  operator with the given timezone reference, applicable on
     *          instances of {@code Moment}
     */
    public final ChronoOperator<Moment> in(Timezone tz) {

        if (tz == null) {
            throw new NullPointerException("Missing timezone.");
        }

        return new Moment.Operator(
            this.onTimestamp(),
            this.element,
            this.type,
            tz
        );

    }

    /**
     * <p>Equivalent to {@code at(ZonalOffset.UTC)}. </p>
     *
     * @return  operator for UTC+00:00, applicable on instances
     *          of {@code Moment}
     * @since   1.2
     * @see     #at(ZonalOffset)
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code at(ZonalOffset.UTC)}. </p>
     *
     * @return  operator for UTC+00:00, applicable on instances
     *          of {@code Moment}
     * @since   1.2
     * @see     #at(ZonalOffset)
     */
    public final ChronoOperator<Moment> atUTC() {

        return this.at(ZonalOffset.UTC);

    }

    /**
     * <p>Creates an operator which can adjust a {@link Moment} at the
     * given timezone offset. </p>
     *
     * <p>Note: Usually the operator converts the given {@code Moment} to
     * a {@code PlainTimestamp} then processes this local timestamp and
     * finally converts the result back to a new {@code Moment}. A special
     * case are incrementing and decrementing of (sub-)second elements which
     * eventually operate directly on the UTC timeline. </p>
     *
     * @param   offset  timezone offset
     * @return  operator with the given timezone offset, applicable on
     *          instances of {@code Moment}
     * @since   1.2
     */
    /*[deutsch]
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit
     * Hilfe eines Zeitzonen-Offsets anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Dekrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @param   offset  timezone offset
     * @return  operator with the given timezone offset, applicable on
     *          instances of {@code Moment}
     * @since   1.2
     */
    public final ChronoOperator<Moment> at(ZonalOffset offset) {

        return new Moment.Operator(
            this.onTimestamp(),
            this.element,
            this.type,
            Timezone.of(offset)
        );

    }

    /**
     * <p>Liefert eine Operatorvariante f&uuml;r den
     * {@code PlainTimestamp}-Kontext. </p>
     *
     * @return  cached operator
     */
    abstract ChronoOperator<PlainTimestamp> onTimestamp();

    /**
     * <p>Liefert das interne Element. </p>
     *
     * @return  element reference
     */
    ChronoElement<?> getElement() {

        return this.element;

    }

    /**
     * <p>Liefert den Operatortyp. </p>
     *
     * @return  type of this operator
     */
    int getType() {

        return this.type;

    }

}
