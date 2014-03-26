/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalOffset.java) is part of project Time4J.
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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Repr&auml;sentiert die Verschiebung der lokalen Zeit relativ zur
 * UTC-Zeitzone in Sekunden. </p>
 *
 * <p>Es gilt folgende Beziehung zwischen einer lokalen Zeit und der
 * POSIX-Zeit (alle Angaben in Sekunden): </p>
 *
 * <p>{@code [Total Offset] = [Local Wall Time] - [POSIX Time]}</p>
 *
 * @author      Meno Hochschild
 * @concurrency <immutable>
 */
public final class ZonalOffset
    implements Comparable<ZonalOffset>, TZID, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<Integer, ZonalOffset> OFFSET_CACHE =
        new ConcurrentHashMap<Integer, ZonalOffset>();
    private static final ConcurrentMap<String, ZonalOffset> ID_CACHE =
        new ConcurrentHashMap<String, ZonalOffset>();

    private static final BigDecimal DECIMAL_60 = new BigDecimal(60);
    private static final BigDecimal DECIMAL_3600 = new BigDecimal(3600);
    private static final BigDecimal DECIMAL_NEG_180 = new BigDecimal(-180);
    private static final BigDecimal DECIMAL_POS_180 = new BigDecimal(180);
    private static final BigDecimal DECIMAL_240 = new BigDecimal(240);
    private static final BigDecimal MRD = new BigDecimal(1000000000);

    /**
     * <p>Konstante f&uuml;r eine zeitliche Verschiebung von {@code 0} Sekunden
     * mit der kanonischen Darstellung &quot;Z&quot;. </p>
     */
    public static final ZonalOffset UTC;

    static {
        UTC = new ZonalOffset(0, 0);
        OFFSET_CACHE.put(Integer.valueOf(0), UTC);
        ID_CACHE.put("Z", UTC);
    }

    private static final long serialVersionUID = -1410512619471503090L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int total;
    private transient final int fraction;
    private transient final String name;

    //~ Konstruktoren -----------------------------------------------------

    private ZonalOffset(
        int total,
        int fraction
    ) {
        super();

        if (fraction == 0) {
            if (
                (total < -18 * 3600)
                || (total > 18 * 3600)
            ) {
                throw new IllegalArgumentException(
                    "Total seconds out of range: " + total);
            }
        } else if (Math.abs(fraction) > 999999999) {
            throw new IllegalArgumentException(
                "Fraction out of range: " + fraction);
        } else if (
            (total < -11 * 3600)
            || (total > 11 * 3600)
        ) {
            throw new IllegalArgumentException(
                "Total seconds out of range while fraction is non-zero: "
                + total);
        } else if (
            ((total < 0) && (fraction > 0))
            || ((total > 0) && (fraction < 0))
        ) {
            throw new IllegalArgumentException(
                "Different signs: offset=" + total + ", fraction=" + fraction);
        }

        boolean negative = ((total < 0) || (fraction < 0));
        StringBuilder sb = new StringBuilder();
        sb.append(negative ? '-' : '+');

        int absValue = Math.abs(total);
        int hours = absValue / 3600;
        int minutes = (absValue / 60) % 60;
        int seconds = absValue % 60;

        if (hours < 10) {
            sb.append('0');
        }

        sb.append(hours);
        sb.append(':');

        if (minutes < 10) {
            sb.append('0');
        }

        sb.append(minutes);

        if (
            (seconds != 0)
            || (fraction != 0)
        ) {
            sb.append(':');
            if (seconds < 10) {
                sb.append('0');
            }
            sb.append(seconds);

            if (fraction != 0) {
                sb.append('.');
                String f = String.valueOf(Math.abs(fraction));
                for (int i = 0, len = 9 - f.length(); i < len; i++) {
                    sb.append('0');
                }
                sb.append(f);
            }
        }

        this.name = sb.toString();
        this.total = total;
        this.fraction = fraction;

    }

    //~ Methoden ----------------------------------------------------------

//    /**
//     * <p>Konstruiert eine neue Verschiebung auf Basis einer geographischen
//     * L&auml;ngenangabe. </p>
//     *
//     * <p>Hinweis: Diese Methode ist weniger genau als die BigDecimal-Variante.
//     * Ein Beispiel: </p>
//     *
//     * <pre>
//     *  System.out.println(ZonalOffset.atLongitude(new BigDecimal("-14.001")));
//     *  // Ausgabe: -00:56:00.240000000
//     *  System.out.println(ZonalOffset.atLongitude(-14.001));
//     *  // Ausgabe: -00:56:00.239999999
//     * </pre>
//     *
//     * @param   longitude   geographical longitude in degrees defined in
//     *                      range {@code -180.0 <= longitude <= 180.0}
//     * @return  zonal offset in double precision
//     * @throws  IllegalArgumentException if range check fails or if
//     *          no defined longitude is given
//     * @see     #atLongitude(BigDecimal)
//     */
//    public static ZonalOffset atLongitude(double longitude) {
//
//        if (Double.isNaN(longitude)) {
//            throw new IllegalArgumentException("Undefined longitude.");
//        } else if (Double.isInfinite(longitude)) {
//            throw new IllegalArgumentException("Infinite longitude.");
//        } else if (
//            (Double.compare(180.0, longitude) < 0)
//            || (Double.compare(-180.0, longitude) > 0)
//        ) {
//            throw new IllegalArgumentException("Out of range: " + longitude);
//        }
//
//        double offset = longitude * 240.0; // (longitude * 3600 / 15.0)
//        int total = (int) offset;
//        int fraction = (int) ((offset - total) * 1000000000);
//
//        if (fraction == 0) {
//            return ZonalOffset.ofTotalSeconds(total);
//        } else {
//            return new ZonalOffset(total, fraction);
//        }
//
//    }

    /**
     * <p>Konstruiert eine neue Verschiebung auf Basis einer geographischen
     * L&auml;ngenangabe. </p>
     *
     * @param   longitude   geographical longitude in degrees defined in
     *                      range {@code -180.0 <= longitude <= 180.0}
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails
     */
    public static ZonalOffset atLongitude(BigDecimal longitude) {

        if (
            (longitude.compareTo(DECIMAL_POS_180) > 0)
            || (longitude.compareTo(DECIMAL_NEG_180) < 0)
        ) {
            throw new IllegalArgumentException("Out of range: " + longitude);
        }

        BigDecimal offset = longitude.multiply(DECIMAL_240);
        BigDecimal integral = offset.setScale(0, RoundingMode.DOWN);
        BigDecimal delta = offset.subtract(integral);
        BigDecimal decimal =
            delta.setScale(9, RoundingMode.HALF_UP).multiply(MRD);

        int total = integral.intValueExact();
        int fraction = decimal.intValueExact();

        if (fraction == 0) {
            return ZonalOffset.ofTotalSeconds(total);
        } else {
            return new ZonalOffset(total, fraction);
        }

    }

    /**
     * <p>Konstruiert eine neue Verschiebung auf Basis einer geographischen
     * L&auml;ngenangabe. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   degrees     geographical length in degreed, defined in
     *                      range {@code 0 <= degrees <= 180}
     * @param   arcMinutes  arc minute part ({@code 0 <= arcMinutes <= 59})
     * @param   arcSeconds  arc second part ({@code 0 <= arcSeconds <= 59})
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #atLongitude(BigDecimal)
     */
    public static ZonalOffset atLongitude(
        Sign sign,
        int degrees,
        int arcMinutes,
        int arcSeconds
    ) {

        if (sign == null) {
            throw new NullPointerException("Missing sign.");
        } else if ((degrees < 0) || (degrees > 180)) {
            throw new IllegalArgumentException(
                "Degrees of longitude out of range (0 <= degrees <= 180).");
        } else if ((arcMinutes < 0) || (arcMinutes > 59)) {
            throw new IllegalArgumentException(
                "Arc minute out of range (0 <= arcMinutes <= 59).");
        } else if ((arcSeconds < 0) || (arcSeconds > 59)) {
            throw new IllegalArgumentException(
                "Arc second out of range (0 <= arcSeconds <= 59).");
        }

        BigDecimal longitude = BigDecimal.valueOf(degrees);

        if (arcMinutes != 0) {
            BigDecimal arcMin =
                BigDecimal.valueOf(arcMinutes)
                    .setScale(9)
                    .divide(DECIMAL_60, RoundingMode.HALF_UP);
            longitude = longitude.add(arcMin);
        }

        if (arcSeconds != 0) {
            BigDecimal arcSec =
                BigDecimal.valueOf(arcSeconds)
                    .setScale(9)
                    .divide(DECIMAL_3600, RoundingMode.HALF_UP);
            longitude = longitude.add(arcSec);
        }

        if (sign == Sign.BEHIND_UTC) {
            longitude = longitude.negate();
        }

        return ZonalOffset.atLongitude(longitude);

    }

    /**
     * <p>Statische Fabrikmethode f&uuml;r eine Zeitverschiebung, die den
     * angegebenen vollen Stundenanteil hat. </p>
     *
     * <p>Entspricht {@code of(sign, hours, 0, 0}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @return  zonal offset in hour precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #of(Sign, int, int, int)
     */
    public static ZonalOffset of(
        Sign sign,
        int hours
    ) {

        return of(sign, hours, 0, 0);

    }

    /**
     * <p>Statische Fabrikmethode f&uuml;r eine Zeitverschiebung, die die
     * angegebenen Stunden- und Minutenanteile hat. </p>
     *
     * <p>Die angegebenen Zahlenwerte entsprechen exakt den numerischen
     * Bestandteilen der kanonischen Darstellung &#x00B1;hh:mm&quot;. Der
     * Sekundenanteil ist hier immer {@code 0}. Entspricht dem Ausdruck
     * {@code valueOf(sign, hours, minutes, 0}. Erlaubt sind nur Werte im
     * Bereich {@code -18:00 <= [total-offset] <= +18:00}. Bei der Berechnung
     * der gesamten Verschiebung wird das Vorzeichen nicht nur auf den Stunden-,
     * sondern auch auf den Minutenteil mit &uuml;bertragen. Beispiel: Der
     * Ausdruck {@code ZonalOffset.of(BEHIND_UTC, 4, 30)} hat die
     * String-Darstellung {@code -04:30} und eine Gesamtverschiebung in
     * Sekunden von {@code -(4 * 3600 + 30 * 60) = 16200}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @param   minutes     minute part ({@code 0 <= minutes <= 59})
     * @return  zonal offset in minute precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #of(Sign, int, int, int)
     */
    public static ZonalOffset of(
        Sign sign,
        int hours,
        int minutes
    ) {

        return of(sign, hours, minutes, 0);

    }

    /**
     * <p>Statische Fabrikmethode f&uuml;r eine Verschiebung, die die
     * angegebenen Zeitanteile hat. </p>
     *
     * <p>Die angegebenen Zahlenwerte entsprechen exakt den numerischen
     * Bestandteilen der kanonischen Darstellung &#x00B1;hh:mm:ss&quot;.
     * Nur Werte im Bereich {@code -18:00:00 <= [total-offset] <= +18:00:00}
     * sind erlaubt. Bei der Berechnung der gesamten Verschiebung wird das
     * Vorzeichen nicht nur auf den Stunden-, sondern auch auf den Minuten-
     * und den Sekundenteil mit &uuml;bertragen. Beispiel: Der Ausdruck
     * {@code ZonalOffset.of(BEHIND_UTC, 4, 30, 7)} hat die Darstellung
     * {@code -04:30:07} und eine Gesamtverschiebung in Sekunden von
     * {@code -(4 * 3600 + 30 * 60 + 7) = 16207}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @param   minutes     minute part ({@code 0 <= minutes <= 59})
     * @param   seconds     second part ({@code 0 <= seconds <= 59})
     * @return  zonal offset in second precision
     * @throws  IllegalArgumentException if range check fails
     */
    public static ZonalOffset of(
        Sign sign,
        int hours,
        int minutes,
        int seconds
    ) {

        if (sign == null) {
            throw new NullPointerException("Missing sign.");
        } else if ((hours < 0) || (hours > 18)) {
            throw new IllegalArgumentException(
                "Hour part out of range (0 <= hours <= 18) in: "
                + format(hours, minutes, seconds));
        } else if ((minutes < 0) || (minutes > 59)) {
            throw new IllegalArgumentException(
                "Minute part out of range (0 <= minutes <= 59) in: "
                + format(hours, minutes, seconds));
        } else if ((seconds < 0) || (seconds > 59)) {
            throw new IllegalArgumentException(
                "Second part out of range (0 <= seconds <= 59) in: "
                + format(hours, minutes, seconds));
        } else if (
            (hours == 18)
            && ((minutes != 0) || (seconds != 0))
        ) {
            throw new IllegalArgumentException(
                "Time zone offset out of range "
                + "(-18:00:00 <= offset <= 18:00:00) in: "
                + format(hours, minutes, seconds));
        }

        int total = hours * 3600 + minutes * 60 + seconds;

        if (sign == Sign.BEHIND_UTC) {
            total = -total;
        }

        return ZonalOffset.ofTotalSeconds(total);

    }

    /**
     * <p>Konstruiert eine Verschiebung der lokalen Zeit relativ zur
     * UTC-Zeitzone in integralen Sekunden. </p>
     *
     * @param   total   total shift in seconds defined in range
     *                  {@code -18 * 3600 <= total <= 18 * 3600}
     * @return  zonal offset in second precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #getIntegralAmount()
     */
    public static ZonalOffset ofTotalSeconds(int total) {

        return ZonalOffset.ofTotalSeconds(total, 0);

    }

    /**
     * <p>Konstruiert eine Verschiebung der lokalen Zeit relativ zur
     * UTC-Zeitzone in integralen oder fraktionalen Sekunden. </p>
     *
     * @param   total       total shift in seconds defined in range
     *                      {@code -18 * 3600 <= total <= 18 * 3600}
     * @param   fraction    fraction of second
     * @return  zonal offset in (sub-)second precision
     * @throws  IllegalArgumentException if any arguments are out of range
     *                                   or have different signs
     * @see     #getIntegralAmount()
     * @see     #getFractionalAmount()
     */
    public static ZonalOffset ofTotalSeconds(
        int total,
        int fraction
    ) {

        if (fraction != 0) {
            return new ZonalOffset(total, fraction);
        } else if (total == 0) {
            return UTC;
        } else if ((total % (15 * 60)) == 0) { // Viertelstundenintervall
            Integer value = Integer.valueOf(total);
            ZonalOffset result = OFFSET_CACHE.get(value);
            if (result == null) {
                result = new ZonalOffset(total, 0);
                OFFSET_CACHE.putIfAbsent(value, result);
                result = OFFSET_CACHE.get(value);
                ID_CACHE.putIfAbsent(result.name, result);
            }
            return result;
        } else {
            return new ZonalOffset(total, 0);
        }

    }

    /**
     * <p>Liefert das Vorzeichen der zonalen Verschiebung. </p>
     *
     * @return  {@code BEHIND_UTC} if sign is negative else {@code AHEAD_OF_UTC}
     */
    public Sign getSign() {

        return (
            ((this.total < 0) || (this.fraction < 0))
            ? Sign.BEHIND_UTC
            : Sign.AHEAD_OF_UTC
        );

    }

    /**
     * <p>Liefert den Stundenanteil der Verschiebung als Absolutbetrag. </p>
     *
     * @return  absolute hour part in range {@code 0 <= x <= 18}
     * @see     #getSign()
     */
    public int getAbsoluteHours() {

        return (Math.abs(this.total) / 3600);

    }

    /**
     * <p>Liefert den Minutenanteil der Verschiebung als Absolutbetrag. </p>
     *
     * @return  absolute minute part in range {@code 0 <= x <= 59}
     * @see     #getSign()
     */
    public int getAbsoluteMinutes() {

        return ((Math.abs(this.total) / 60) % 60);

    }

    /**
     * <p>Liefert den Sekundenanteil der Verschiebung als Absolutbetrag. </p>
     *
     * @return  absolute second part in range {@code 0 <= x <= 59}
     * @see     #getSign()
     */
    public int getAbsoluteSeconds() {

        return (Math.abs(this.total) % 60);

    }

    /**
     * <p>Verschiebung in integralen Sekunden ohne Sekundenbruchteil. </p>
     *
     * @return  integral part in seconds {@code -18 * 3600 <= x <= 18 * 3600}
     * @see     #getFractionalAmount()
     */
    public int getIntegralAmount() {

        return this.total;

    }

    /**
     * <p>Liefert den Sekundenbruchteil der Verschiebung in
     * Nanosekundenform. </p>
     *
     * @return  fractional part in range {@code -999999999 <= x <= 999999999}
     * @see     #getIntegralAmount()
     */
    public int getFractionalAmount() {

        return this.fraction;

    }

    /**
     * <p>Vergleicht die gesamte zeitliche Verschiebung. </p>
     *
     * <p>Es wird aufsteigend sortiert. Verschiebungen westlich von Greenwich
     * gelten als kleiner im Vergleich zu Verschiebungen &ouml;stlich von
     * Greenwich. </p>
     *
     * <p>Die nat&uuml;rliche Ordnung ist konsistent mit {@code equals()}. </p>
     */
    @Override
    public int compareTo(ZonalOffset obj) {

        if (this.total < obj.total) {
            return -1;
        } else if (this.total > obj.total) {
            return 1;
        } else {
            int delta = (this.fraction - obj.fraction);
            return ((delta < 0) ? -1 : ((delta == 0) ? 0 : 1));
        }

    }

    /**
     * <p>Vergleicht die internen Verschiebungswerte. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof ZonalOffset) {
            ZonalOffset that = (ZonalOffset) obj;
            return (
                (this.total == that.total)
                && (this.fraction == that.fraction)
            );
        } else {
            return false;
        }

    }

    /**
     * <p>Berechnet den Hash-Wert. </p>
     */
    @Override
    public int hashCode() {

        return (~this.total + (this.fraction % 64000));

    }

    /**
     * <p>Liefert eine vollst&auml;ndige Darstellung der Verschiebung
     * mit Vorzeichen. </p>
     *
     * <p>Sofern nur volle Minuten oder Stunden vorliegen, entspricht die
     * Darstellung exakt dem empfohlenen ISO-8601-Standard. </p>
     *
     * @return  String in ISO-8601 format &quot;&#x00B1;hh:mm&quot; or
     *          &quot;&#x00B1;hh:mm:ss&quot; if there is a second part
     *          or &quot;&#x00B1;hh:mm:ss.fffffffff&quot; if any fractional
     *          part exists
     */
    @Override
    public String toString() {

        return this.name;

    }

    /**
     * <p>Liefert eine kanonische Darstellung der Verschiebung. </p>
     *
     * <p>Hinweis: Handelt es sich um die UTC-Zeitzone selbst, liefert die
     * Methode die Darstellung &quot;Z&quot;. </p>
     *
     * @return  String in format &quot;UTC&#x00B1;hh:mm&quot; or
     *          &quot;UTC&#x00B1;hh:mm:ss&quot; if there is a second part
     *          or &quot;UTC&#x00B1;hh:mm:ss.fffffffff&quot; if any fractional
     *          part exists or &quot;Z&quot; in time zone UTC
     */
    @Override
    public String canonical() {

        if (
            (this.total == 0)
            && (this.fraction == 0)
        ) {
            return "Z";
        }

        return "UTC" + this.name;

    }

    /**
     * <p>Liefert die Verschiebung als Zeitzonenmodell. </p>
     *
     * @return  time zone data with fixed shift
     */
    SingleOffsetTimeZone getModel() {

        return new SingleOffsetTimeZone(this);

    }

    private static String format(
        int hours,
        int minutes,
        int seconds
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("[hours=");
        sb.append(hours);
        sb.append(",minutes=");
        sb.append(minutes);
        sb.append(",seconds=");
        sb.append(seconds);
        sb.append(']');
        return sb.toString();

    }

    /**
     * @serialData  Uses
     *              <a href="../../../serialized-form.html#net.time4j.tz.SPX">
     *              a specialized serialisation form</a> as proxy. The format
     *              is bit-compressed. The first byte contains in the four
     *              most significant bits the type id {@code 15}. If there is
     *              any fractional part then the four least significant bits
     *              are {@code 1} else {@code 0}. After that the data bits
     *              for the integral total shift and optionally the fractional
     *              part follow.
     *
     * Schematic algorithm:
     *
     * <pre>
     *  boolean hasFraction = (this.getFractionalAmount() != 0);
     *  int header = (15 << 4);
     *
     *  if (hasFraction) {
     *      header |= 1;
     *  }
     *
     *  out.writeByte(header);
     *  out.writeInt(this.getIntegralAmount());
     *
     *  if (hasFraction) {
     *      out.writeInt(this.getFractionalAmount());
     *  }
     * </pre>
     */
    private Object writeReplace() throws ObjectStreamException {

        return new SPX(this, SPX.ZONAL_OFFSET_TYPE);

    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @throws      InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        throw new InvalidObjectException("Serialization proxy required.");

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Repr&auml;sentiert das Vorzeichen der zonalen Verschiebung. </p>
     */
    public static enum Sign {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * <p>Negatives Vorzeichen. </p>
         */
        BEHIND_UTC,

        /**
         * <p>Positives Vorzeichen (auch bei Null-Offset). </p>
         */
        AHEAD_OF_UTC;

    }

}
