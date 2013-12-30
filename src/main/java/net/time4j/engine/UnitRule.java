/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UnitRule.java) is part of project Time4J.
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

package net.time4j.engine;

import de.menodata.annotations4j.Nullable;


/**
 * <p>Repr&auml;sentiert die Additions- und Subtraktionsregel einer
 * Zeiteinheit. </p>
 *
 * <p>Eine Zeiteinheitsregel wird in der {@code TimeAxis} zusammen mit
 * der assoziierten Zeiteinheit registriert, was gew&ouml;hnlich beim Laden
 * der jeweiligen {@code TimePoint}-Klasse geschieht. Pro Chronologie und
 * pro Zeiteinheit gibt es genau eine Regelinstanz. Implementierungen
 * m&uuml;ssen immer <i>immutable</i> sein. </p>
 *
 * @param   <T> Typ des Zeitwertkontexts (ein Subtyp von {@code TimePoint})
 * @author  Meno Hochschild
 */
public interface UnitRule<T> {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Addiert den angegebenen Betrag zu einem Zeitpunkt im Kontext der
     * assoziierten Zeiteinheit. </p>
     *
     * <p>Wird von den {@code plus()}- und {@code minus()}-Methoden
     * in der Klasse {@code TimePoint} aufgerufen. Wenn nicht besonders
     * spezifiziert, wird ein eventueller &Uuml;berlauf so aufgel&ouml;st,
     * da&szlig; der zuletzt g&uuml;ltige Zeitpunkt gesucht wird. Zum
     * Beispiel wird die Addition eines Monats zum 31. Mai gew&ouml;hnlich
     * den 30. Juni ergeben (weil der 31. Juni nicht existiert). </p>
     *
     * @param   timepoint   Zeitpunkt
     * @param   amount      Anzahl der zu addierenden Zeiteinheiten
     * @return  Additionsergebnis
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     TimePoint#plus(long, Object) TimePoint.plus(long, U)
     * @see     TimePoint#minus(long, Object) TimePoint.minus(long, U)
     */
    T addTo(T timepoint, long amount);

    /**
     * <p>Ermittelt die Differenz zwischen den angegebenen Zeitwertpunkten
     * als Anzahl der assoziierten Zeiteinheit. </p>
     *
     * <p>Wird von der {@code TimePoint.until(T, U)}-Methode aufgerufen,
     * d.h., der Ausdruck {@code start.until(end, unit)} entspricht
     * gerade {@code ruleForUnit.between(start, end)}. Es werden nur
     * volle Zeiteinheiten gez&auml;hlt. Ein Subtraktionsrest wird immer
     * unterschlagen. </p>
     *
     * @param   start   Startzeitpunkt
     * @param   end     Endzeitpunkt
     * @return  Differenz in Zeiteinheiten (negativ, wenn {@code end} vor
     *          {@code start} liegt)
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     TimePoint#until(TimePoint, Object) TimePoint.until(T, U)
     */
    long between(T start, T end);

    //~ Innere Interfaces -------------------------------------------------

    /**
     * <p>Enth&auml;lt eine Regel f&uuml;r eine Zeiteinheit. </p>
     *
     * @see     TimePoint#plus(long, Object) TimePoint.plus(long, U)
     * @see     TimePoint#minus(long, Object) TimePoint.minus(long, U)
     * @see     TimePoint#until(TimePoint, Object) TimePoint.until(T, U)
     */
    public static interface Source {

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Leitet eine optionale Einheitsregel f&uuml;r die angegebene
         * Chronologie ab. </p>
         *
         * @param   <T> Zeitpunkttyp
         * @param   chronology  Chronologie, zu der eine Einheitsregel
         *                      gesucht wird
         * @return  Einheitsregel oder {@code null} wenn die Chronologie
         *          nicht passt
         */
        @Nullable
        <T extends ChronoEntity<T>> UnitRule<T> derive(
            Chronology<T> chronology
        );

    }

}
