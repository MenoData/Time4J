/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionHistory.java) is part of project Time4J.
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

package net.time4j.tz;

import de.menodata.annotations4j.Nullable;
import java.util.List;
import net.time4j.base.GregorianDate;
import net.time4j.base.WallTime;
import net.time4j.base.UnixTime;


/**
 * <p>H&auml;lt alle &Uuml;berg&auml;nge und Regeln einer Zeitzone. </p>
 *
 * <p>Konkrete Implementierungen m&uuml;ssen unver&auml;nderlich und
 * serialisierbar sein. </p>
 *
 * @author  Meno Hochschild
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
     * @return  feste Anfangsverschiebung
     */
    ZonalOffset getInitialOffset();

    /**
     * <p>Ermittelt den letzten &Uuml;bergang, der die zur angegebenen
     * Referenzzeit zugeh&ouml;rige Verschiebung definiert. </p>
     *
     * @param   time    POSIX-Referenzzeit
     * @return  {@code ZonalTransition} oder {@code null} wenn die Referenzzeit
     *          vor dem ersten definierten &Uuml;bergang liegt
     */
    @Nullable
    ZonalTransition getStartTransition(UnixTime time);

    /**
     * <p>Bestimmt den passenden &Uuml;bergang, wenn die angegebene lokale
     * Zeit in eine L&uuml;cke oder eine &Uuml;berlappung auf dem lokalen
     * Zeitstrahl f&auml;llt. </p>
     *
     * @param   localDate   lokales Datum
     * @param   localTime   lokale Uhrzeit
     * @return  Konflikt&uuml;bergang auf dem lokalen Zeitstrahl f&uuml;r
     *          L&uuml;cken oder &Uuml;berlappungen, sonst {@code null}
     * @see     #getValidOffsets(GregorianDate,WallTime)
     */
    @Nullable
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
     * @param   localDate   lokales Datum
     * @param   localTime   lokale Uhrzeit
     * @return  unver&auml;nderliche Liste von Verschiebungen, die
     *          zur lokalen Zeit passen
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
     * @return  unver&auml;nderliche Liste von &Uuml;berg&auml;ngen,
     *          kann leer sein
     */
    List<ZonalTransition> getStdTransitions();

    /**
     * <p>Ermittelt die vorherigen &Uuml;berg&auml;nge in zeitlich absteigender
     * Reihenfolge, falls vorhanden. </p>
     *
     * <p>Grunds&auml;tzlich liefert die Methode nur &Uuml;berg&auml;nge im
     * Intervall von [1970-01-01T00:00Z] bis maximal zum ersten &Uuml;bergang
     * nach dem aktuellen Zeitpunkt (in zeitlich umgekehrter Reihenfolge!). </p>
     *
     * @param   time    POSIX-Referenzzeit
     * @return  vorherige Standard-&Uuml;berg&auml;nge
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getStdTransitionsBefore(UnixTime time);

    /**
     * <p>Ermittelt die n&auml;chsten &Uuml;berg&auml;nge in zeitlich
     * aufsteigender Reihenfolge, falls vorhanden. </p>
     *
     * <p>Grunds&auml;tzlich liefert die Methode nur &Uuml;berg&auml;nge im
     * Intervall von [1970-01-01T00:00Z] bis maximal zum ersten &Uuml;bergang
     * nach dem aktuellen Zeitpunkt. </p>
     *
     * @param   time    POSIX-Referenzzeit
     * @return  n&auml;chste Standard-&Uuml;berg&auml;nge
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getStdTransitionsAfter(UnixTime time);

    /**
     * <p>Bestimmt die im angegebenen POSIX-Intervall vorhandenen zonalen
     * &Uuml;berg&auml;nge. </p>
     *
     * @param   startInclusive  Startzeitpunkt auf der POSIX-Zeitskala
     * @param   endExclusive    Endzeitpunkt auf der POSIX-Zeitskala
     * @return  unver&auml;nderliche Liste von &Uuml;berg&auml;ngen,
     *          kann leer sein
     * @see     #getStdTransitions()
     */
    List<ZonalTransition> getTransitions(
        UnixTime startInclusive, UnixTime endExclusive);

}
