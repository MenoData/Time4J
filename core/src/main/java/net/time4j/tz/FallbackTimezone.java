/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (FallbackTimezone.java) is part of project Time4J.
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


/**
 * <p>Spezialimplementierung, die an eine andere Zeitzone delegiert, aber
 * die ID bewahrt. </p>
 *
 * @author      Meno Hochschild
 * @since       2.2
 * @serial      include
 */
final class FallbackTimezone
    extends Timezone {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = -2894726563499525332L;

    //~ Instanzvariablen --------------------------------------------------

    private final TZID tzid;
    private final Timezone fallback;

    //~ Konstruktoren -----------------------------------------------------

    FallbackTimezone(
        TZID tzid,
        Timezone fallback
    ) {
        super();

        if (
            (tzid == null)
            || (fallback == null)
        ) {
            throw new NullPointerException();
        }

        this.tzid = tzid;
        this.fallback = fallback;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public TZID getID() {

        return this.tzid;

    }

    @Override
    public ZonalOffset getOffset(UnixTime ut) {

        return this.fallback.getOffset(ut);

    }

    @Override
    public ZonalOffset getStandardOffset(UnixTime ut) {

        return this.fallback.getStandardOffset(ut);

    }

    @Override
    public ZonalOffset getDaylightSavingOffset(UnixTime ut){

        return this.fallback.getDaylightSavingOffset(ut);

    }

    @Override
    public ZonalOffset getOffset(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return this.fallback.getOffset(localDate, localTime);

    }

    @Override
    public boolean isInvalid(
        GregorianDate localDate,
        WallTime localTime
    ) {

        return this.fallback.isInvalid(localDate, localTime);

    }

    @Override
    public boolean isDaylightSaving(UnixTime ut) {

        return this.fallback.isDaylightSaving(ut);

    }

    @Override
    public boolean isFixed() {

        return this.fallback.isFixed();

    }

    @Override
    public TransitionHistory getHistory() {

        return this.fallback.getHistory();

    }

    @Override
    public TransitionStrategy getStrategy() {

        return this.fallback.getStrategy();

    }

    @Override
    public Timezone with(TransitionStrategy strategy) {

        return new FallbackTimezone(this.tzid, this.fallback.with(strategy));

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof FallbackTimezone) {
            FallbackTimezone that = (FallbackTimezone) obj;
            return (
                this.tzid.canonical().equals(that.tzid.canonical())
                && this.fallback.equals(that.fallback)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.tzid.canonical().hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append('[');
        sb.append(this.getClass().getName());
        sb.append(':');
        sb.append(this.tzid.canonical());
        sb.append(",fallback=");
        sb.append(this.fallback);
        sb.append(']');
        return sb.toString();

    }

    // used in serialization
    Timezone getFallback() {

        return this.fallback;

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
     *              is bit-compressed. The first byte contains in the four
     *              most significant bits the type id {@code 12}. Then the
     *              data bits for the id and the fallback timezone follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  int header = (12 &lt;&lt; 4);
     *  out.writeByte(header);
     *  out.writeObject(getID());
     *  out.writeObject(getFallback());
     * </pre>
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.FALLBACK_TIMEZONE_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws InvalidObjectException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

}
