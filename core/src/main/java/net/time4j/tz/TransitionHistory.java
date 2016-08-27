/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
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

import java.io.IOException;
import java.util.List;


/**
 * <p>Keeps all offset transitions and rules of a timezone. </p>
 *
 * <p>Note: This interface can be considered as stable since version v2.2.
 * Preliminary experimental versions of this interface existed since v1.0
 * but there was originally not any useable implementation. </p>
 *
 * <p><strong>Specification:</strong> All implementations must be immutable, thread-safe and serializable.</p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>H&auml;lt alle &Uuml;berg&auml;nge und Regeln einer Zeitzone. </p>
 *
 * <p>Hinweis: Dieses Interface kann als stabil seit Version 2.2 gelten.
 * Davor existierten experimentelle Versionen des Interface schon seit v1.0,
 * aber es gab urspr&uuml;nglich keine nutzbare Implementierung. </p>
 *
 * <p><strong>Specification:</strong> All implementations must be immutable, thread-safe and serializable.</p>
 *
 * @author  Meno Hochschild
 */
public interface TransitionHistory {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Return the initial offset no matter if there are any
     * transitions defined or not. </p>
     *
     * <p>If any transition is defined then the initial offset
     * is identical to the shift {@code getPreviousOffset()} of
     * the first defined transition in history. </p>
     *
     * @return  fixed initial shift in full seconds
     */
    /*[deutsch]
     * <p>Ermittelt die initiale Verschiebung unabh&auml;ngig davon, ob es
     * &Uuml;berg&auml;nge gibt oder nicht. </p>
     *
     * <p>Falls es &Uuml;berg&auml;nge gibt, mu&szlig; die initiale
     * Verschiebung mit der Verschiebung {@code getPreviousOffset()}
     * des ersten definierten &Uuml;bergangs identisch sein. </p>
     *
     * @return  fixed initial shift in full seconds
     */
    ZonalOffset getInitialOffset();

    /**
     * <p>Queries the last transition which defines the offset
     * for given global timestamp. </p>
     *
     * @param   ut      unix reference time
     * @return  {@code ZonalTransition} or {@code null} if given reference time
     *          is before first defined transition
     */
    /*[deutsch]
     * <p>Ermittelt den letzten &Uuml;bergang, der die zur angegebenen
     * Referenzzeit zugeh&ouml;rige Verschiebung definiert. </p>
     *
     * @param   ut      unix reference time
     * @return  {@code ZonalTransition} or {@code null} if given reference time
     *          is before first defined transition
     */
    ZonalTransition getStartTransition(UnixTime ut);

    /**
     * <p>Returns the conflict transition where given local timestamp
     * falls either in a gap or in an overlap on the local timeline. </p>
     *
     * <p>Note that only the expression {@code localDate.getYear()} is used
     * to determine the daylight saving rules to be applied in calculation.
     * This is particularly important if there is a wall time of 24:00. Here
     * only the date before merging to next day matters, not the date of the
     * whole timestamp. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  conflict transition on the local time axis for gaps or
     *          overlaps else {@code null}
     * @see     #getValidOffsets(GregorianDate,WallTime)
     */
    /*[deutsch]
     * <p>Bestimmt den passenden &Uuml;bergang, wenn die angegebene lokale
     * Zeit in eine L&uuml;cke oder eine &Uuml;berlappung auf dem lokalen
     * Zeitstrahl f&auml;llt. </p>
     *
     * <p>Zu beachten: Nur der Ausdruck {@code localDate.getYear()} wird
     * in der Ermittlung der passenden DST-Regeln benutzt. Das ist insbesondere
     * von Bedeutung, wenn die Uhrzeit 24:00 vorliegt. Hier z&auml;hlt nur
     * das Jahr des angegebenen Datums, nicht das des Zeitstempels, der
     * wegen der Uhrzeit evtl. im Folgejahr liegt. </p>
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
     * <p>Queries the next transition after given global timestamp. </p>
     *
     * @param   ut      unix reference time
     * @return  {@code ZonalTransition} or {@code null} if given reference time
     *          is after any defined transition
     * @deprecated  Use the equivalent {@code findNextTransition(UnixTime)} in version v4.18 or later
     */
    /*[deutsch]
     * <p>Ermittelt den n&auml;chsten &Uuml;bergang nach der angegebenen
     * Referenzzeit. </p>
     *
     * @param   ut      unix reference time
     * @return  {@code ZonalTransition} or {@code null} if given reference time
     *          is after any defined transition
     * @deprecated  Use the equivalent {@code findNextTransition(UnixTime)} in version v4.18 or later
     */
    @Deprecated
    ZonalTransition getNextTransition(UnixTime ut);

    /**
     * <p>Determines the suitable offsets at given local timestamp.. </p>
     *
     * <p>The offset list is empty if the local timestamp falls in a gap
     * on the local timeline. The list has exactly two offsets sorted by size
     * if the local timestamp belongs to two different timepoints on the
     * POSIX timescale due to an overlap. Otherwise the offset list
     * will contain exactly one suitable offset. </p>
     *
     * <p>Note that only the expression {@code localDate.getYear()} is used
     * to determine the daylight saving rules to be applied in calculation.
     * This is particularly important if there is a wall time of 24:00. Here
     * only the date before merging to next day matters, not the date of the
     * whole timestamp. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  unmodifiable list of shifts in full seconds which fits the
     *          given local time
     * @see     #getConflictTransition(GregorianDate,WallTime)
     */
    /*[deutsch]
     * <p>Bestimmt die zur angegebenen lokalen Zeit passenden
     * zonalen Verschiebungen. </p>
     *
     * <p>Die Liste ist leer, wenn die lokale Zeit in eine L&uuml;cke auf
     * dem lokalen Zeitstrahl f&auml;llt. Die Liste hat genau zwei nach
     * Gr&ouml;&szlig;e sortierte Verschiebungen, wenn die lokale Zeit wegen
     * einer &Uuml;berlappung zu zwei verschiedenen Zeitpunkten auf der
     * POSIX-Zeitskala geh&ouml;rt. Ansonsten wird die Liste genau eine
     * passende Verschiebung enthalten. </p>
     *
     * <p>Zu beachten: Nur der Ausdruck {@code localDate.getYear()} wird
     * in der Ermittlung der passenden DST-Regeln benutzt. Das ist insbesondere
     * von Bedeutung, wenn die Uhrzeit 24:00 vorliegt. Hier z&auml;hlt nur
     * das Jahr des angegebenen Datums, nicht das des Zeitstempels, der
     * wegen der Uhrzeit evtl. im Folgejahr liegt. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  unmodifiable list of shifts in full seconds which fits the
     *          given local time
     * @see     #getConflictTransition(GregorianDate,WallTime)
     */
    List<ZonalOffset> getValidOffsets(
        GregorianDate localDate,
        WallTime localTime
    );

    /**
     * <p>Return the offset transitions from UNIX epoch [1970-01-01T00:00Z]
     * until about one year after the current timestamp. </p>
     *
     * <p>Indeed, a potentially bigger interval is obtainable by
     * {@link #getTransitions(UnixTime,UnixTime)}, but earlier or
     * later timepoints are usually not reliable. For example the
     * wide-spread IANA/Olson-repository is only designed for times
     * since UNIX epoch and offers some selected older data to the
     * best of our knowledge. Users must be aware that even older
     * data can be changed as side effect of data corrections. Generally
     * the timezone concept was invented in 19th century. And future
     * transitions are even less reliable due to political arbitrariness. </p>
     *
     * @return  unmodifiable list of standard transitions (after 1970-01-01)
     *          maybe empty
     */
    /*[deutsch]
     * <p>Bestimmt alle vorhandenen zonalen &Uuml;berg&auml;nge ab der
     * UNIX-Epoche [1970-01-01T00:00Z] bis zirka ein Jahr nach dem aktuellen
     * heutigen Zeitpunkt. </p>
     *
     * <p>Zwar kann mittels {@link #getTransitions(UnixTime,UnixTime)}
     * auch ein potentiell gr&ouml;&szlig;eres Intervall abgefragt werden,
     * jedoch sind fr&uuml;here oder sp&auml;tere Zeitpunkte in aller Regel
     * mit gro&szlig;en Unsicherheiten verkn&uuml;pft. Zum Beispiel ist die
     * weithin verwendete IANA/Olson-Zeitzonendatenbank nur f&uuml;r Zeiten
     * ab der UNIX-Epoche gedacht und bietet ausgew&auml;hlte &auml;ltere
     * Zeitzonendaten lediglich nach bestem Wissen und Gewissen an. Anwender
     * m&uuml;ssen beachten, da&szlig; sich sogar historische alte Daten
     * nachtr&auml;glich &auml;ndern k&ouml;nnen. Generell existiert das
     * Zeitzonenkonzept erst ab ca. dem 19. Jahrhundert. Und in der Zukunft
     * liegende Zeitzonen&auml;nderungen sind wegen politischer Willk&uuml;r
     * sogar noch unsicherer. </p>
     *
     * @return  unmodifiable list of standard transitions (after 1970-01-01)
     *          maybe empty
     */
    List<ZonalTransition> getStdTransitions();

    /**
     * <p>Returns the defined transitions in given POSIX-interval. </p>
     *
     * @param   startInclusive  start time on POSIX time scale
     * @param   endExclusive    end time on POSIX time scale
     * @return  unmodifiable list of transitions maybe empty
     * @throws  IllegalArgumentException if start is after end
     * @see     #getStdTransitions()
     */
    /*[deutsch]
     * <p>Bestimmt die im angegebenen POSIX-Intervall vorhandenen zonalen
     * &Uuml;berg&auml;nge. </p>
     *
     * @param   startInclusive  start time on POSIX time scale
     * @param   endExclusive    end time on POSIX time scale
     * @return  unmodifiable list of transitions maybe empty
     * @throws  IllegalArgumentException if start is after end
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getTransitions(
        UnixTime startInclusive,
        UnixTime endExclusive
    );

    /**
     * <p>Determines if this history does not have any transitions. </p>
     *
     * @return  {@code true} if there are no transitions else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt ob diese Historie keine &Uuml;berg&auml;nge kennt. </p>
     *
     * @return  {@code true} if there are no transitions else {@code false}
     */
    boolean isEmpty();

    /**
     * <p>Creates a dump of this history and writes it to the given buffer. </p>
     *
     * @param   buffer          buffer to write the dump to
     * @throws  IOException     in any case of I/O-errors
     */
    /*[deutsch]
     * <p>Erzeugt eine Textzusammenfassung dieser Instanz und schreibt sie
     * in den angegebenen Puffer. </p>
     *
     * @param   buffer          buffer to write the dump to
     * @throws  IOException     in any case of I/O-errors
     */
    void dump(Appendable buffer) throws IOException;

}
