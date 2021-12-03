/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FixedClock.java) is part of project Time4J.
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

package net.time4j.clock;

import net.time4j.Moment;
import net.time4j.base.UnixTime;


/**
 * <p>Represents a fixed clock which always display the same current time. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine stillstehende Uhr, die immer die gleiche feste
 * Zeit anzeigt. </p>
 *
 * @author  Meno Hochschild
 * @since   2.1
 */
public final class FixedClock
    extends AbstractClock {

    //~ Instanzvariablen --------------------------------------------------

    private final Moment moment;

    //~ Konstruktoren -----------------------------------------------------

    private FixedClock(Moment moment) {
        super();

        this.moment = moment;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new fixed clock. </p>
     *
     * @param   ut      fixed clock time
     * @return  new clock instance with fixed time
     * @since   2.1
     */
    /*[deutsch]
     * <p>Erzeugt eine neue feststehende Uhr. </p>
     *
     * @param   ut      fixed clock time
     * @return  new clock instance with fixed time
     * @since   2.1
     */
    public static FixedClock of(UnixTime ut) {

        return new FixedClock(Moment.from(ut));

    }

    @Override
    public Moment currentTime() {

        return this.moment;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        } else if (obj instanceof FixedClock) {
            FixedClock that = (FixedClock) obj;
            return this.moment.equals(that.moment);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return 31 * this.moment.hashCode();

    }

    /**
     * <p>For debugging purposes. </p>
     *
     * @return  description of clock state
     */
    /*[deutsch]
     * <p>F&uuml;r Debugging-Zwecke. </p>
     *
     * @return  description of clock state
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("FixedClock[");
        sb.append("moment=");
        sb.append(this.moment);
        sb.append(']');
        return sb.toString();

    }

}
