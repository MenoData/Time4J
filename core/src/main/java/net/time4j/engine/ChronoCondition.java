/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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
 * <p>Represents a temporal condition. </p>
 *
 * <p>Common examples are queries for Friday the thirteenth or if a date
 * matches a holiday or a weekend. This interface is very similar to the
 * type {@code ChronoQuery<T, Boolean>} but allows more clarity and does
 * not throw any exception. </p>
 *
 * @author  Meno Hochschild
 * @see     ChronoEntity#matches(ChronoCondition)
 */
/*[deutsch]
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
public interface ChronoCondition<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Decides if given context matches this condition. </p>
     *
     * <p>Due to better readability it is recommended to use following
     * equivalent approach instead of this method:: </p>
     *
     * <pre>
     *  import static net.time4j.Weekday.SATURDAY;
     *  import static net.time4j.Month.JANUARY;
     *
     *  PlainDate date = PlainDate.of(2014, JANUARY, 25);
     *  System.out.println(SATURDAY.test(date)); // direct use
     *  System.out.println(date.matches(SATURDAY)); // recommended callback
     * </pre>
     *
     * @param   context     context as base of testing this condition
     * @return  {@code true} if given time context matches this condition
     *          else {@code false}
     */
    /*[deutsch]
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
