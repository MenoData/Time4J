/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalOperator.java) is part of project Time4J.
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

import net.time4j.base.GregorianDate;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;
import net.time4j.engine.ChronoOperator;
import net.time4j.tz.Timezone;
import net.time4j.tz.TransitionStrategy;


/**
 * <p>Definiert eine zeitzonenbedingte Manipulation von UTC-Momenten, indem eine
 * besondere Strategie zur Auswertung von Offset-Wechseln beachtet wird. </p>
 *
 * <p>Die Standardstrategie w&auml;hlt als Offset den jeweils n&auml;chsten
 * definierten Offset aus, wenn eine lokale Zeit in eine L&uuml;cke oder
 * &Uuml;berlappung f&auml;llt. Anschlie&szlig;end wird die lokale Zeit
 * mit dem Offset verrechnet und so ein UTC-Zeitstempel gebildet. Mit diesem
 * Operator kann eine abweichende Strategie gew&auml;hlt werden. </p>
 *
 * @author  Meno Hochschild
 */
public abstract class ZonalOperator
    implements ChronoOperator<Moment> {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Legt bei Transformationen von lokalen Zeitstempeln zu UTC fest,
     * da&szlig; nur in der Zeitzone g&uuml;ltige Zeitstempel zugelassen
     * werden. </p>
     */
    public static final TransitionStrategy STRICT_MODE =
        new TransitionStrategy() {
            @Override
            public UnixTime resolve(
                GregorianDate localDate,
                WallTime localTime,
                Timezone timezone
            ) {
                PlainTimestamp timestamp =
                    PlainTimestamp.of(
                        PlainDate.from(localDate),
                        PlainTime.from(localTime));

                if (timezone.isInvalid(localDate, localTime)) {
                    throw new IllegalArgumentException(
                        "Invalid local timestamp due to timezone transition: "
                        + timestamp
                        + " [" + timezone.getID() + "]"
                    );
                }

                Moment result = timestamp.inTimezone(timezone);
                Moment.checkNegativeLS(result.getPosixTime(), timestamp);
                return result;
            }
        };

    //~ Konstruktoren -----------------------------------------------------

    /**
     * Paket-privater Konstruktor.
     */
    ZonalOperator() {
        // no external instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Erzeugt einen Operator, der einen {@link Moment} mit Hilfe der
     * internen Zeitzonenreferenz und der angegebenen &Uuml;bergangsstrategie
     * anpassen kann. </p>
     *
     * <p>Hinweis: Der Operator wandelt meist den gegebenen {@code Moment}
     * in einen lokalen Zeitstempel um, bearbeitet dann diese lokale
     * Darstellung und konvertiert das Ergebnis in einen neuen {@code Moment}
     * zur&uuml;ck. Ein Spezialfall sind Inkrementierungen und Dekrementierungen
     * von (Sub-)Sekundenelementen, bei denen ggf. direkt auf dem globalen
     * Zeitstrahl operiert wird. </p>
     *
     * @param   strategy    conflict resolving strategy
     * @return  operator with the given timezone reference, applicable on
     *          instances of {@code Moment}
     */
    public abstract ChronoOperator<Moment> select(TransitionStrategy strategy);

}
