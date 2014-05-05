/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TransitionStrategy.java) is part of project Time4J.
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


/**
 * <p>Dient der Aufl&ouml;sung von lokalen Zeitangaben zu einer UTC-Weltzeit,
 * wenn wegen L&uuml;cken oder &Uuml;berlappungen auf dem lokalen Zeitstrahl
 * Konflikte auftreten. </p>
 *
 * @author  Meno Hochschild
 */
public enum TransitionStrategy {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>W&auml;hlt die jeweils n&auml;chste g&uuml;ltige Verschiebung. </p>
     *
     * <p>Diese Strategie wird auch vom JDK gew&auml;hlt. Zum Beispiel sieht
     * der Wechsel von der Winterzeit zur Sommerzeit und zur&uuml;ck in der
     * Zeitzone &quot;Europe/Berlin&quot; so aus: </p>
     *
     * <dl>
     * <dt>Wechsel Winterzeit zu Sommerzeit mit einer ung&uuml;ltigen
     * lokalen Zeit:</dt>
     * <dd>[2013-03-31T01:30+01:00] (setze 1 Stunde sp&auml;ter)</dd>
     * <dd>=&gt; [2013-03-31T02:30+01:00] // ung&uuml;ltig!</dd>
     * <dd>=&gt; [2013-03-31T03:30+02:00] // Sommerzeit</dd>
     * <dt>Wechsel Sommerzeit zu Winterzeit mit einer zweideutigen
     * lokalen Zeit:</dt>
     * <dd>[2013-10-27T01:30+02:00] (setze 1 Stunde sp&auml;ter)</dd>
     * <dd>=&gt; [2013-10-27T02:30+02:00] // Sommerzeit</dd>
     * <dd>=&gt; [2013-10-27T02:30+01:00] // Winterzeit</dd>
     * </dl>
     */
    PUSH_FORWARD,

    /**
     * <p>Wirft f&uuml;r ung&uuml;ltige lokale Zeiten eine
     * {@code ChronoException} und verh&auml;lt sich sonst wie
     * {@link #PUSH_FORWARD}. </p>
     */
    STRICT;

//    EARLIER_OFFSET,
//    LATER_OFFSET;

}
