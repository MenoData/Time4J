/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ChronoCondition.java) is part of project Time4J.
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
 * <p>Repr&auml;sentiert eine zeitliche Bedingung. </p>
 *
 * <p>Die g&auml;ngigsten Beispiele w&auml;ren etwa Freitag, der 13. oder
 * die Frage, ob ein Feiertag oder ein Wochenende vorliegen. Dieses Interface
 * ist im Prinzip sehr &auml;hnlich zu {@code ChronoQuery<T, Boolean>}, erlaubt
 * aber eine bessere sprachliche Klarheit und wirft keine Ausnahme. </p>
 *
 * @author  Meno Hochschild
 * @see     ChronoEntity#matches(ChronoCondition)
 */
// TODO: Ab Java 8 aktiv => extends Predicate<T>
public interface ChronoCondition<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Entscheidet, ob der angegebene Zeitwertkontext diese Bedingung
     * erf&uuml;llt. </p>
     *
     * <p>Aus Gr&uuml;nden der Lesbarkeit ist es meistens besser, statt dieser
     * Methode vielmehr folgenden &auml;quivalenten Ausdruck zu verwenden: </p>
     *
     * <pre>
     *  import static net.time4j.Weekday.SATURDAY;
     *  import static net.time4j.Month.JANUARY;
     *
     *  PlainDate date = PlainDate.of(2014, JANUARY, 25);
     *  System.out.println(SATURDAY.test(date)); // direkte Benutzung
     *  System.out.println(date.matches(SATURDAY)); // empfohlenes Callback
     * </pre>
     *
     * @param   context     context as base of testing this condition
     * @return  {@code true} if given time context matches this condition
     *          else {@code false}
     */
    boolean test(T context);

}
