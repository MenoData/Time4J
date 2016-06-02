/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (BasicUnit.java) is part of project Time4J.
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
 * <p>Abstract time unit class which can define its own rule. </p>
 *
 * @see     TimePoint#plus(long, Object) TimePoint.plus(long, U)
 * @see     TimePoint#minus(long, Object) TimePoint.minus(long, U)
 * @see     TimePoint#until(TimePoint, Object) TimePoint.until(T, U)
 */
/*[deutsch]
 * <p>Abstrakte Zeiteinheitsklasse, die ihre eigene Regel definieren kann. </p>
 *
 * @see     TimePoint#plus(long, Object) TimePoint.plus(long, U)
 * @see     TimePoint#minus(long, Object) TimePoint.minus(long, U)
 * @see     TimePoint#until(TimePoint, Object) TimePoint.until(T, U)
 */
public abstract class BasicUnit
    implements ChronoUnit {

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Subclasses usually define a singleton and have no public
     * constructors. </p>
     */
    /*[deutsch]
     * <p>Subklassen werden normalerweise ein Singleton definieren und
     * keine &ouml;ffentlichen Konstruktoren anbieten. </p>
     */
    protected BasicUnit() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean isCalendrical() {

        return (Double.compare(this.getLength(), 86400.0) >= 0);
        
    }

    /**
     * <p>Derives an optional unit rule for given chronology. </p>
     *
     * <p>This implementation yields {@code null} so that a chronology
     * must register this unit. Specialized subclasses can define a rule
     * however if the chronology has not registered this unit. </p>
     *
     * @param   <T> generic type of time context
     * @param   chronology  chronology an unit rule is searched for
     * @return  unit rule or {@code null} if given chronology is
     *          not supported
     */
    /*[deutsch]
     * <p>Leitet eine optionale Einheitsregel f&uuml;r die angegebene
     * Chronologie ab. </p>
     *
     * <p>Diese Implementierung liefert {@code null}, so da&szlig; eine
     * gegebene Chronologie diese Einheit registrieren mu&szlig;.
     * Spezialisierte Subklassen k&ouml;nnen jedoch eine Regel definieren,
     * wenn eine Chronologie diese Einheit nicht registriert hat. </p>
     *
     * @param   <T> generic type of time context
     * @param   chronology  chronology an unit rule is searched for
     * @return  unit rule or {@code null} if given chronology is
     *          not supported
     */
    protected <T extends ChronoEntity<T>> UnitRule<T> derive(Chronology<T> chronology) {

        return null;

    }

    /**
     * <p>Derives an optional unit rule for given entity. </p>
     *
     * @param   <T> generic type of time context
     * @param   entity  the entity referencing a chronology
     * @return  unit rule or {@code null} if the underlying chronology is not supported
     * @since   3.21/4.17
     */
    /*[deutsch]
     * <p>Leitet eine optionale Einheitsregel f&uuml;r die angegebene Entit&auml;t ab. </p>
     *
     * @param   <T> generic type of time context
     * @param   entity  the entity referencing a chronology
     * @return  unit rule or {@code null} if the underlying chronology is not supported
     * @since   3.21/4.17
     */
    protected final <T extends ChronoEntity<T>> UnitRule<T> derive(T entity) {

        return this.derive(entity.getChronology());

    }

}
