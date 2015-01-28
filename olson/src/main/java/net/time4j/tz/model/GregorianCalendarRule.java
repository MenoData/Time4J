/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (GregorianCalendarRule.java) is part of project Time4J.
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

package net.time4j.tz.model;

import net.time4j.Month;
import net.time4j.PlainTime;


/**
 * <p>Represents a standard daylight saving rule following the gregorian
 * calendar. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  exclude
 * @concurrency <immutable>
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Standardregel f&uuml;r Zeitumstellungen im
 * gregorianischen Kalender. </p>
 *
 * @author  Meno Hochschild
 * @since   2.2
 * @serial  exclude
 * @concurrency <immutable>
 */
abstract class GregorianCalendarRule
    extends DaylightSavingRule {

    //~ Instanzvariablen --------------------------------------------------

    private transient final byte month;

    //~ Konstruktoren -----------------------------------------------------

    GregorianCalendarRule(
        Month month,
        PlainTime timeOfDay,
        OffsetIndicator indicator,
        int savings
    ) {
        super(timeOfDay, indicator, savings);

        this.month = (byte) month.getValue();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Benutzt unter anderem in der Serialisierung. </p>
     *
     * @return  byte
     */
    byte getMonth() {

        return this.month;

    }

}
