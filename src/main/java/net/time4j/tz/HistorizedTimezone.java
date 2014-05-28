/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HistorizedTimezone.java) is part of project Time4J.
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
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.List;


/**
 * <p>Provider-abh&auml;ngige Implementierung einer Zeitzone. </p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
final class HistorizedTimezone
    extends Timezone {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 1738909257417361021L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  timezone id
     */
    private final TZID id;

    /**
     * @serial  offset transition model
     */
    private final TransitionHistory history;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Standard-Konstruktor. </p>
     *
     * @param   id          timezone id
     * @param   history     offset transition model
     */
    HistorizedTimezone(
        TZID id,
        TransitionHistory history
    ) {
        super();

        if (id == null) {
            throw new NullPointerException("Missing timezone id.");
        } else if (history == null) {
            throw new NullPointerException("Missing timezone history.");
        }

        this.id = id;
        this.history = history;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public TZID getID() {

        return this.id;

    }

    @Override
    public ZonalOffset getOffset(UnixTime ut) {

        ZonalTransition t = this.history.getStartTransition(ut);

        return (
            (t == null)
            ? this.history.getInitialOffset()
            : ZonalOffset.ofTotalSeconds(t.getTotalOffset())
        );

    }

    @Override
    public ZonalOffset getOffset(
        GregorianDate localDate,
        WallTime localTime
    ) {

        List<ZonalOffset> offsets =
            this.history.getValidOffsets(localDate, localTime);

        if (offsets.size() == 1) {
            return offsets.get(0);
        } else {
            ZonalTransition t =
                this.history.getConflictTransition(localDate, localTime);
            return ZonalOffset.ofTotalSeconds(t.getTotalOffset());
        }

    }

    @Override
    public boolean isInvalid(
        GregorianDate localDate,
        WallTime localTime
    ) {

        ZonalTransition t =
            this.history.getConflictTransition(localDate, localTime);
        return ((t != null) && t.isGap());

    }

    @Override
    public boolean isDaylightSaving(UnixTime ut) {

        ZonalTransition t = this.history.getStartTransition(ut);
        return ((t == null) ? false : t.isDaylightSaving());

    }

    @Override
    public TransitionHistory getHistory() {

        return this.history;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HistorizedTimezone) {
            HistorizedTimezone that = (HistorizedTimezone) obj;
            return (
                this.id.canonical().equals(that.id.canonical())
                && this.history.equals(that.history)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.id.canonical().hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append('[');
        sb.append(this.getClass().getName());
        sb.append(':');
        sb.append(this.id.canonical());
        sb.append(",history={");
        sb.append(this.history);
        sb.append("}]");
        return sb.toString();

    }

    /**
     * @serialData  Checks the consistency.
     * @throws      InvalidObjectException in case of inconsistencies
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        if (this.id == null) {
            throw new InvalidObjectException("Missing id.");
        } else if (this.history == null) {
            throw new InvalidObjectException("Missing history.");
        }

    }

}
