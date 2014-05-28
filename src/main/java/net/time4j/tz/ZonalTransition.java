/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
import java.io.ObjectInputStream;
import java.io.Serializable;


/**
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
 * {@code getStandardOffset()} und {@code getDaylightSavingOffset()}. </p>
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
 * @concurrency <immutable>
 */
public final class ZonalTransition
    implements Comparable<ZonalTransition>, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final long serialVersionUID = 4594838367057225304L;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  POSIX time in seconds since 1970-01-01T00:00:00Z
     */
    private final long posix;

    /**
     * @serial  previous total shift in seconds
     */
    private final int previous;

    /**
     * @serial  new total shift in seconds
     */
    private final int total;

    /**
     * @serial  new daylight-saving-shift in seconds (DST)
     */
    private final int dst;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert einen neuen &Uuml;bergang zwischen zwei
     * Verschiebungen. </p>
     *
     * @param   posixTime               POSIX time of transition
     * @param   previousOffset          previous total shift in seconds
     * @param   totalOffset             new total shift in seconds
     * @param   daylightSavingOffset    DST-shift in seconds
     * @throws  IllegalArgumentException if the DST-shift is negative
     * @see     net.time4j.scale.UniversalTime#getPosixTime()
     * @see     ZonalOffset#getIntegralAmount()
     */
    public ZonalTransition(
        long posixTime,
        int previousOffset,
        int totalOffset,
        int daylightSavingOffset
    ) {

        this.posix = posixTime;
        this.previous = previousOffset;
        this.total = totalOffset;
        this.dst = daylightSavingOffset;

        checkDST(daylightSavingOffset);

    }

    //~ Methoden ----------------------------------------------------------

    /**
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
     * <p>Liefert die Gesamtverschiebung nach diesem &Uuml;bergang. </p>
     *
     * @return  new total shift in seconds
     * @see     #getPreviousOffset()
     * @see     #getStandardOffset()
     * @see     #getDaylightSavingOffset()
     * @see     #isDaylightSaving()
     * @see     ZonalOffset#getIntegralAmount()
     */
    public int getTotalOffset() {

        return this.total;

    }

    /**
     * <p>Liefert die aktuelle Standardverschiebung nach diesem &Uuml;bergang
     * als Differenz zwischen Gesamtverschiebung und DST-Verschiebung. </p>
     *
     * <p>Negative Standardverschiebungen beziehen sich auf Zeitzonen westlich
     * des Nullmeridians von Greenwich, positive auf Zeitzonen &ouml;stlich
     * davon. Die Addition dieser Verschiebung zur POSIX-Zeit ergibt die
     * <i>standard local time</i>, die der Winterzeit entspricht. </p>
     *
     * @return  raw shift in seconds after transition
     * @see     #getTotalOffset()
     * @see     #getDaylightSavingOffset()
     * @see     #isDaylightSaving()
     */
    public int getStandardOffset() {

        return (this.total - this.dst);

    }

    /**
     * <p>Liefert die DST-Verschiebung nach dem &Uuml;bergang, also den durch
     * die Sommerzeit induzierten Versatz. </p>
     *
     * <p>Wenn die Methode {@code isDaylightSaving()} den Wert {@code false}
     * ergibt, dann liefert diese Methode einfach nur den Wert {@code 0}. </p>
     *
     * @return  daylight-saving-shift in seconds after transition
     * @see     #getTotalOffset()
     * @see     #getStandardOffset()
     * @see     #isDaylightSaving()
     */
    public int getDaylightSavingOffset() {

        return this.dst;

    }

    /**
     * <p>Liegt nach diesem &Uuml;bergang Sommerzeit vor? </p>
     *
     * @return  boolean
     * @see     #getDaylightSavingOffset()
     */
    public boolean isDaylightSaving() {

        return (this.dst != 0);

    }

    /**
     * <p>Liefert die Differenz zwischen neuer und alter Gesamtverschiebung
     * als Ma&szlig; f&uuml;r die Gr&ouml;&szlig;e des &Uuml;bergangs. </p>
     *
     * @return  change of total shift in seconds
     */
    public int getSize() {

        return (this.total - this.previous);

    }

    /**
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
     * <p>Beruht bevorzugt auf der zeitlichen Reihenfolge des POSIX-Zeitpunkts
     * des &Uuml;bergangs, sonst auf den Gesamtverschiebungen und zuletzt auf
     * der DST-Verschiebung. </p>
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
                    delta = (this.dst - other.dst);
                    if (delta == 0) {
                        return 0;
                    }
                }
            }
        }

        return ((delta < 0) ? -1 : 1);

    }

    /**
     * <p>Basiert auf der POSIX-Zeit und allen Verschiebungen. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ZonalTransition) {
            ZonalTransition that = (ZonalTransition) obj;

            if (
                (this.posix == that.posix)
                && (this.previous == that.previous)
                && (this.total == that.total)
                && (this.dst == that.dst)
            ) {
                return true;
            }
        }

        return false;

    }

    /**
     * <p>Basiert auf der POSIX-Zeit des &Uuml;bergangs. </p>
     */
    @Override
    public int hashCode() {

        return (int) (this.posix ^ (this.posix >>> 32));

    }

    /**
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
        sb.append(", dst-offset=");
        sb.append(this.dst);
        sb.append(']');
        return sb.toString();

    }

    private static void checkDST(int dst) {

        if (dst < 0) {
            throw new IllegalArgumentException("Negative DST: " + dst);
        }

    }

    /**
     * @serialData  Checks the consistency.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        checkDST(this.dst);

    }

}
