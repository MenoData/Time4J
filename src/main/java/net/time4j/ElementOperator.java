/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ElementOperator.java) is part of project Time4J.
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

import net.time4j.engine.AdvancedElement;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoOperator;
import net.time4j.tz.TZID;


/**
 * <p>Definiert eine Manipulation von Datums- oder Uhrzeitobjekten nach
 * dem Strategy-Entwurfsmuster. </p>
 *
 * @param       <T> generic target type this operator is applied to
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
public abstract class ElementOperator<T>
    implements ChronoOperator<T> {

    //~ Statische Felder/Initialisierungen --------------------------------

    static final int OP_UNDEFINED = -1;
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
        final AdvancedElement<?> element,
        final int type
    ) {
        super();

        this.element = element;
        this.type = type;

    }

    //~ Methoden ----------------------------------------------------------

    /**
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
    public ZonalOperator inStdTimezone() {

        return new Moment.Operator(this.onTimestamp(), this.element, this.type);

    }

    /**
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
     */
    public final ZonalOperator inTimezone(TZID tzid) {

        if (tzid == null) {
            throw new NullPointerException("Missing timezone id.");
        }

        return new Moment.Operator(
            this.onTimestamp(),
            this.element,
            this.type,
            tzid,
            null
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
