/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalTransition.java) is part of project Time4J.
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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
 * <p>Represents the change of a shift of the local time relative to
 * POSIX-time in any timezone. </p>
 *
 * <p>This class contains informations about the global timestamp of the
 * transition and the shifts/offsets before and after the transitions.
 * A change of a zonal shift can either be caused by special historical
 * events and political actions (change of raw time) or by establishing
 * <i>daylight saving</i>-rules (change from winter time to summer time and
 * reverse - DST). Therefore the total shift {@code getTotalOffset()} is always
 * the sum of the parts {@code getRawOffset()} and {@code getExtraOffset()}. </p>
 *
 * <p>Shifts are described on the local timeline in seconds. Following
 * relationship holds between local time and POSIX-time: </p>
 *
 * <p>{@code getTotalOffset() = [Local Wall Time] - [POSIX Time]}</p>
 *
 * <p>A zonal transition induces a gap on the local timeline if the new
 * shift is greater than the old shift. And an overlap occurs if the new
 * shift is smaller than the old shift. A local time is not defined within
 * gaps and ambivalent in overlapping regions. </p>
 *
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Beschreibt einen Wechsel der Verschiebung der lokalen Zeit relativ
 * zur POSIX-Zeit in einer Zeitzone. </p>
 *
 * <p>Diese Klasse enth&auml;lt neben dem Zeitpunkt des &Uuml;bergangs auch
 * Informationen &uuml;ber die Verschiebung vor und nach dem Wechsel. Ein
 * Wechsel der Verschiebung kann entweder durch einmalige historische bzw.
 * politische &Auml;nderungen (&Auml;nderung der Standardverschiebung der
 * Zeitzone <i>standard time</i>) oder durch <i>daylight saving</i>-Schemata
 * bedingt sein, also die Umstellung von Winterzeit auf Sommerzeit und
 * umgekehrt (DST). Somit ist die Gesamtverschiebung {@code getTotalOffset()}
 * immer die Summe aus den einzelnen Verschiebungsanteilen
 * {@code getRawOffset()} und {@code getExtraOffset()}. </p>
 *
 * <p>Verschiebungen werden grunds&auml;tzlich auf dem lokalen Zeitstrahl einer
 * Zeitzone beschrieben. Es gilt somit folgende Beziehung zwischen einer
 * lokalen Zeit und der POSIX-Zeit (alle Angaben in Sekunden): </p>
 *
 * <p>{@code getTotalOffset() = [Local Wall Time] - [POSIX Time]}</p>
 *
 * <p>An einem &Uuml;bergang tritt eine L&uuml;cke auf dem lokalen Zeitstrahl
 * auf, wenn die neue Verschiebung gr&ouml;&szlig;er als die alte Verschiebung
 * ist. Und eine &Uuml;berlappung tritt auf, wenn die neue Verschiebung kleiner
 * als die alte Verschiebung ist. Eine lokale Zeitangabe ist auf L&uuml;cken
 * nicht definiert und auf &Uuml;berlappungen zweideutig. </p>
 *
 * @author      Meno Hochschild
 */
public final class ZonalTransition
    implements Comparable<ZonalTransition>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 4594838367057225304L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  POSIX time in seconds since 1970-01-01T00:00:00Z
     */
    /*[deutsch]
     * @serial  POSIX-Zeit in Sekunden seit 1970-01-01T00:00:00Z
     */
    private final long posix;

    /**
     * @serial  previous total shift in seconds
     */
    /*[deutsch]
     * @serial  alte Gesamtverschiebung in Sekunden
     */
    private final int previous;

    /**
     * @serial  new total shift in seconds
     */
    /*[deutsch]
     * @serial  neue Gesamtverschiebung in Sekunden
     */
    private final int total;

    /**
     * @serial  new extra shift in seconds (typically DST)
     */
    /*[deutsch]
     * @serial  neue Extra-Verschiebung in Sekunden (typischerweise DST)
     */
    private final int extra;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new transition between two shifts. </p>
     *
     * <p>The given daylight-saving offset might be exceptionally equal to {@code Integer.MAX_VALUE}
     * which signals an offset of zero but interpreted as daylight-saving. This special feature is
     * supported since version v3.39/4.34. </p>
     *
     * @param   posixTime               POSIX time of transition
     * @param   previousOffset          previous total shift in seconds
     * @param   totalOffset             new total shift in seconds
     * @param   extraOffset             extra shift in seconds
     * @throws  IllegalArgumentException if any offset is out of range {@code -18 * 3600 <= total <= 18 * 3600}
     * @see     net.time4j.scale.UniversalTime#getPosixTime()
     * @see     ZonalOffset#getIntegralAmount()
     */
    /*[deutsch]
     * <p>Konstruiert einen neuen &Uuml;bergang zwischen zwei
     * Verschiebungen. </p>
     *
     * <p>Die angegebene Extra-Verschiebung darf ausnahmsweise auch gleich {@code Integer.MAX_VALUE} sein.
     * Dieser besondere Wert zeigt eine Null-Verschiebung an, die aber als abweichende Sommerzeiteinstellung
     * interpretiert wird. Dieses spezielle Verhalten wird erst seit Version v3.39/4.34 angeboten. </p>
     *
     * @param   posixTime               POSIX time of transition
     * @param   previousOffset          previous total shift in seconds
     * @param   totalOffset             new total shift in seconds
     * @param   extraOffset             extra shift in seconds
     * @throws  IllegalArgumentException if any offset is out of range {@code -18 * 3600 <= total <= 18 * 3600}
     * @see     net.time4j.scale.UniversalTime#getPosixTime()
     * @see     ZonalOffset#getIntegralAmount()
     */
    public ZonalTransition(
        long posixTime,
        int previousOffset,
        int totalOffset,
        int extraOffset
    ) {
        super();

        this.posix = posixTime;
        this.previous = previousOffset;
        this.total = totalOffset;
        this.extra = extraOffset;

        checkRange(previousOffset);
        checkRange(totalOffset);
        checkDST(extraOffset);

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Returns the global timestamp of this transition from one shift to
     * another as POSIX-timestamp. </p>
     *
     * @return  transition time relative to [1970-01-01T00:00:00] in seconds
     *          (without leap seconds)
     * @see     net.time4j.scale.TimeScale#POSIX
     */
    /*[deutsch]
     * <p>Stellt die Zeit des &Uuml;bergangs von einer Verschiebung zur anderen
     * als POSIX-Zeit dar. </p>
     *
     * @return  transition time relative to [1970-01-01T00:00:00] in seconds
     *          (without leap seconds)
     * @see     net.time4j.scale.TimeScale#POSIX
     */
    public long getPosixTime() {

        return this.posix;

    }

    /**
     * <p>Returns the total shift before this transition. </p>
     *
     * @return  previous total shift in seconds
     * @see     #getTotalOffset()
     * @see     ZonalOffset#getIntegralAmount()
     */
    /*[deutsch]
     * <p>Liefert die Gesamtverschiebung vor diesem &Uuml;bergang. </p>
     *
     * @return  previous total shift in seconds
     * @see     #getTotalOffset()
     * @see     ZonalOffset#getIntegralAmount()
     */
    public int getPreviousOffset() {

        return this.previous;

    }

    /**
     * <p>Returns the total shift after this transition. </p>
     *
     * @return  new total shift in seconds
     * @see     #getPreviousOffset()
     * @see     #getRawOffset()
     * @see     #getExtraOffset()
     * @see     ZonalOffset#getIntegralAmount()
     */
    /*[deutsch]
     * <p>Liefert die Gesamtverschiebung nach diesem &Uuml;bergang. </p>
     *
     * @return  new total shift in seconds
     * @see     #getPreviousOffset()
     * @see     #getRawOffset()
     * @see     #getExtraOffset()
     * @see     ZonalOffset#getIntegralAmount()
     */
    public int getTotalOffset() {

        return this.total;

    }

    /**
     * <p>Returns the raw shift after this transition as difference
     * between total shift and an extra shift (often daylight savings). </p>
     *
     * <p>Negative raw shifts are related to timezones west for
     * Greenwich, positive to timezones east for Greenwich. The addition
     * of the raw shift to POSIX-time yields the <i>standard local time</i>
     * typically corresponding to winter time. </p>
     *
     * <p>Some few countries define the legal standard time the other way round.
     * For example, Ireland labels the summer time as standard time. Hence this class
     * avoids the usage of the term &quot;standard time&quot;, but prefers the term
     * &quot;raw offset&quot;. </p>
     *
     * @return  raw shift in seconds after transition
     * @see     #getTotalOffset()
     * @see     #getExtraOffset()
     * @since   5.5
     */
    /*[deutsch]
     * <p>Liefert die aktuelle Standardverschiebung nach diesem &Uuml;bergang
     * als Differenz zwischen Gesamtverschiebung und einer extra Verschiebung. </p>
     *
     * <p>Negative Verschiebungen beziehen sich auf Zeitzonen westlich
     * des Nullmeridians von Greenwich, positive auf Zeitzonen &ouml;stlich
     * davon. Die Addition dieser Verschiebung zur POSIX-Zeit ergibt die
     * <i>standard local time</i>, die typischerweise der Winterzeit entspricht. </p>
     *
     * <p>Einige wenige L&auml;nder definieren die gesetzliche Standardzeit in
     * umgekehrter Weise. Zum Beispiel bezeichnet Irland die Sommerzeit als Standardzeit.
     * Deshalb bevorzugt diese Klasse den Begriff &quot;raw offset&quot; (Rohverschiebung). </p>
     *
     * @return  raw shift in seconds after transition
     * @see     #getTotalOffset()
     * @see     #getExtraOffset()
     * @since   5.5
     */
    public int getRawOffset() {

        return (this.total - this.getExtraOffset());

    }

    /**
     * <p>Returns typically the DST-shift (daylight savings) after this transition that is
     * the shift normally induced by change to summer time. </p>
     *
     * <p>This offset defines a deviation from the raw offset. The only legitimate method
     * to determine if a time zone is actually in daylight saving mode is the method
     * {@link Timezone#isDaylightSaving Timezone.isDaylightSaving(moment)}. </p>
     *
     * @return  extra shift in seconds after transition, can also be negative
     * @see     #getTotalOffset()
     * @see     #getRawOffset()
     * @since   5.5
     */
    /*[deutsch]
     * <p>Liefert typischerweise die DST-Verschiebung nach dem &Uuml;bergang, also
     * normalerweise den durch die Sommerzeit induzierten Versatz. </p>
     *
     * <p>Hiermit wird eine Abweichung von der Standardverschiebung definiert. Ob aber &uuml;berhaupt
     * ein &Uuml;bergang als <i>daylightSaving</i> interpretiert werden darf, kann nur mittels
     * {@link Timezone#isDaylightSaving Timezone.isDaylightSaving(moment)} entschieden werden. </p>
     *
     * @return  extra shift in seconds after transition, can also be negative
     * @see     #getTotalOffset()
     * @see     #getRawOffset()
     * @since   5.5
     */
    public int getExtraOffset() {

        return ((this.extra == Integer.MAX_VALUE) ? 0 : this.extra); // for backwards compatibility in serialization

    }

    /**
     * <p>Gets the difference between new and old total shift as
     * measure for the size of this transition. </p>
     *
     * @return  change of total shift in seconds (negative in case of overlap)
     */
    /*[deutsch]
     * <p>Liefert die Differenz zwischen neuer und alter Gesamtverschiebung
     * als Ma&szlig; f&uuml;r die Gr&ouml;&szlig;e des &Uuml;bergangs. </p>
     *
     * @return  change of total shift in seconds (negative in case of overlap)
     */
    public int getSize() {

        return (this.total - this.previous);

    }

    /**
     * <p>Queries if this transition represents a gap on the local timeline
     * where local timestamps are invalid. </p>
     *
     * @return  {@code true} if this transition represents a gap (by definition
     *          the new total shift is bigger than the previous one)
     *          else {@code false}
     */
    /*[deutsch]
     * <p>Ist dieser &Uuml;bergang eine L&uuml;cke, w&auml;hrend der eine
     * lokale Zeitangabe ung&uuml;ltig ist? </p>
     *
     * @return  {@code true} if this transition represents a gap (by definition
     *          the new total shift is bigger than the previous one)
     *          else {@code false}
     */
    public boolean isGap() {

        return (this.total > this.previous);

    }

    /**
     * <p>Queries if this transition represents an overlap on the local
     * timeline where local timestamps are ambivalent. </p>
     *
     * @return  {@code true} if this transition represents an overlap (by
     *          definition the new total shift is smaller than the previous
     *          one) else {@code false}
     */
    /*[deutsch]
     * <p>Ist dieser &Uuml;bergang eine &Uuml;berlappung, w&auml;hrend der
     * eine lokale Zeitangabe nicht mehr eindeutig definiert ist? </p>
     *
     * @return  {@code true} if this transition represents an overlap (by
     *          definition the new total shift is smaller than the previous
     *          one) else {@code false}
     */
    public boolean isOverlap() {

        return (this.total < this.previous);

    }

    /**
     * <p>Compares preferrably the timeline order based on the global
     * timestamps of transitions, otherwise the total shift and finally
     * the extra shift. </p>
     *
     * <p>The natural order is consistent with {@code equals()}. </p>
     */
    /*[deutsch]
     * <p>Beruht bevorzugt auf der zeitlichen Reihenfolge des POSIX-Zeitpunkts
     * des &Uuml;bergangs, sonst auf den Gesamtverschiebungen und zuletzt auf
     * der Extra-Verschiebung. </p>
     *
     * <p>Die nat&uuml;rliche Ordnung ist konsistent mit {@code equals()}. </p>
     */
    @Override
    public int compareTo(ZonalTransition other) {

        long delta = (this.posix - other.posix);

        if (delta == 0) {
            delta = (this.previous - other.previous);
            if (delta == 0) {
                delta = (this.total - other.total);
                if (delta == 0) {
                    delta = (this.getExtraOffset() - other.getExtraOffset());
                    if (delta == 0) {
                        return 0;
                    }
                }
            }
        }

        return ((delta < 0) ? -1 : 1);

    }

    /**
     * <p>Based on the whole state with global POSIX-timestamp and all
     * internal shifts. </p>
     */
    /*[deutsch]
     * <p>Basiert auf der POSIX-Zeit und allen Verschiebungen. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ZonalTransition) {
            ZonalTransition that = (ZonalTransition) obj;

            return (
                (this.posix == that.posix)
                && (this.previous == that.previous)
                && (this.total == that.total)
                && (this.getExtraOffset() == that.getExtraOffset()));
        }

        return false;

    }

    /**
     * <p>Based on the POSIX-timestamp of the transition. </p>
     */
    /*[deutsch]
     * <p>Basiert auf der POSIX-Zeit des &Uuml;bergangs. </p>
     */
    @Override
    public int hashCode() {

        return (int) (this.posix ^ (this.posix >>> 32));

    }

    /**
     * <p>Supports debugging. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Unterst&uuml;tzt Debugging-Ausgaben. </p>
     *
     * @return  String
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(128);
        sb.append("[POSIX=");
        sb.append(this.posix);
        sb.append(", previous-offset=");
        sb.append(this.previous);
        sb.append(", total-offset=");
        sb.append(this.total);
        sb.append(", extra-offset=");
        sb.append(this.getExtraOffset());
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Synonym for {@code getRawOffset()}. </p>
     *
     * @return  raw shift in seconds after transition
     * @see     #getRawOffset()
     * @deprecated  method was renamed and is subject to removal in future versions
     */
    @Deprecated
    public int getStandardOffset() {

        return this.getRawOffset();

    }

    /**
     * <p>Synonym for {@code getExtraOffset()}. </p>
     *
     * @return  daylight-saving-shift in seconds after transition, can also be negative
     * @see     #getExtraOffset()
     * @deprecated  method was renamed and is subject to removal in future versions
     */
    @Deprecated
    public int getDaylightSavingOffset() {

        return this.getExtraOffset();

    }

    private static void checkRange(int offset) {

        if ((offset < -18 * 3600) || (offset > 18 * 3600)) {
            throw new IllegalArgumentException(
                "Offset out of range: " + offset);
        }

    }

    private static void checkDST(int dst) {

        if (dst != Integer.MAX_VALUE) {
            checkRange(dst);
        }

    }

    /**
     * @serialData  Checks the consistency.
     * @param       in      object input stream
     * @throws      IOException in any case of inconsistencies
     * @throws      ClassNotFoundException if class-loading fails
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        try {
            checkRange(this.previous);
            checkRange(this.total);
            checkDST(this.extra);
        } catch (IllegalArgumentException iae) {
            throw new InvalidObjectException(iae.getMessage());
        }

    }

}
