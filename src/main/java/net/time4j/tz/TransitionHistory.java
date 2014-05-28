/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionHistory.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.GregorianDate;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;

import java.util.List;


/**
 * <p>H&auml;lt alle &Uuml;berg&auml;nge und Regeln einer Zeitzone. </p>
 *
 * @author  Meno Hochschild
 * @spec    All implementations must be immutable, thread-safe and serializable.
 */
public interface TransitionHistory {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Ermittelt die initiale Verschiebung unabh&auml;ngig davon, ob es
     * &Uuml;berg&auml;nge gibt oder nicht. </p>
     *
     * <p>Falls es &Uuml;berg&auml;nge gibt, mu&szlig; die initiale
     * Verschiebung mit der Verschiebung {@code getPreviousOffset()}
     * des ersten definierten &Uuml;bergangs identisch sein. </p>
     *
     * @return  fixed initial shift
     */
    ZonalOffset getInitialOffset();

    /**
     * <p>Ermittelt den letzten &Uuml;bergang, der die zur angegebenen
     * Referenzzeit zugeh&ouml;rige Verschiebung definiert. </p>
     *
     * @param   ut      unix reference time
     * @return  {@code ZonalTransition} or {@code null} if given reference time
     *          is before first defined transition
     */
    ZonalTransition getStartTransition(UnixTime ut);

    /**
     * <p>Bestimmt den passenden &Uuml;bergang, wenn die angegebene lokale
     * Zeit in eine L&uuml;cke oder eine &Uuml;berlappung auf dem lokalen
     * Zeitstrahl f&auml;llt. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  conflict transition on the local time axis for gaps or
     *          overlaps else {@code null}
     * @see     #getValidOffsets(GregorianDate,WallTime)
     */
    ZonalTransition getConflictTransition(
        GregorianDate localDate,
        WallTime localTime
    );

    /**
     * <p>Bestimmt die zur angegebenen lokalen Zeit passenden
     * zonalen Verschiebungen. </p>
     *
     * <p>Die Liste ist leer, wenn die lokale Zeit in eine L&uuml;cke auf
     * dem lokalen Zeitstrahl f&auml;llt. Die Liste hat genau zwei
     * Verschiebungen, wenn die lokale Zeit wegen einer &Uuml;berlappung
     * zu zwei verschiedenen Zeitpunkten auf der POSIX-Zeitskala geh&ouml;rt.
     * Ansonsten wird die Liste genau eine passende Verschiebung enthalten. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  unmodifiable list of shifts which fits the given local time
     * @see     #getConflictTransition(GregorianDate,WallTime)
     */
    List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    );

    /**
     * <p>Bestimmt alle vorhandenen zonalen &Uuml;berg&auml;nge ab der
     * UNIX-Epoche [1970-01-01T00:00Z] bis maximal zum ersten &Uuml;bergang
     * nach dem aktuellen heutigen Zeitpunkt. </p>
     *
     * <p>Zwar kann mittels {@link #getTransitions(UnixTime,UnixTime)}
     * auch ein potentiell gr&ouml;&szlig;eres Intervall abgefragt werden,
     * jedoch sind fr&uuml;here oder sp&auml;tere Zeitpunkte in aller Regel
     * mit gro&szlig;en Unsicherheiten verkn&uuml;pft. Zum Beispiel ist die
     * weithin verwendete IANA/Olson-Zeitzonendatenbank nur f&uuml;r Zeiten
     * ab der UNIX-Epoche gedacht und bietet ausgew&auml;hlte &auml;ltere
     * Zeitzonendaten lediglich nach bestem Wissen und Gewissen an. Generell
     * existiert das Zeitzonenkonzept erst ab ca. dem 19. Jahrhundert. Und
     * in der Zukunft liegende Zeitzonen&auml;nderungen sind wegen politischer
     * Willk&uuml;r sogar noch unsicherer. </p>
     *
     * @return  unmodifiable list of standard transitions (after 1970-01-01)
     *          maybe empty
     */
    List<ZonalTransition> getStdTransitions();

    /**
     * <p>Ermittelt die vorherigen &Uuml;berg&auml;nge in zeitlich absteigender
     * Reihenfolge, falls vorhanden. </p>
     *
     * <p>Grunds&auml;tzlich liefert die Methode nur &Uuml;berg&auml;nge
     * im Intervall von [1970-01-01T00:00Z] bis maximal zum ersten
     * &Uuml;bergang vor dem aktuellen Zeitpunkt (in zeitlich umgekehrter
     * Reihenfolge!). </p>
     *
     * @param   ut      unix reference time
     * @return  previous transitions before reference time
     *          (only standard transitions after 1970-01-01)
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getStdTransitionsBefore(UnixTime ut);

    /**
     * <p>Ermittelt die n&auml;chsten &Uuml;berg&auml;nge in zeitlich
     * aufsteigender Reihenfolge, falls vorhanden. </p>
     *
     * <p>Grunds&auml;tzlich liefert die Methode nur &Uuml;berg&auml;nge im
     * Intervall von [1970-01-01T00:00Z] bis maximal zum ersten &Uuml;bergang
     * nach dem aktuellen Zeitpunkt. </p>
     *
     * @param   ut      unix reference time
     * @return  next transitions after reference time (only standard
     *          transitions 1970-01-01)
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getStdTransitionsAfter(UnixTime ut);

    /**
     * <p>Bestimmt die im angegebenen POSIX-Intervall vorhandenen zonalen
     * &Uuml;berg&auml;nge. </p>
     *
     * @param   startInclusive  start time on POSIX time scale
     * @param   endExclusive    end time on POSIX time scale
     * @return  unmodifiable list of transitions maybe empty
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getTransitions(
        UnixTime startInclusive,
        UnixTime endExclusive
    );

}
