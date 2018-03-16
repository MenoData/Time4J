/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ZonalOffset.java) is part of project Time4J.
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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Represents the shift of a local timestamp relative to UTC timezone
 * usually in full seconds. </p>
 *
 * <p>Following rule is the guideline (all data in seconds): </p>
 *
 * <p>{@code [Total Offset] = [Local Wall Time] - [POSIX Time]}</p>
 *
 * @author      Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die Verschiebung der lokalen Zeit relativ zur
 * UTC-Zeitzone normalerweise in vollen Sekunden. </p>
 *
 * <p>Es gilt folgende Beziehung zwischen einer lokalen Zeit und der
 * POSIX-Zeit (alle Angaben in Sekunden): </p>
 *
 * <p>{@code [Total Offset] = [Local Wall Time] - [POSIX Time]}</p>
 *
 * @author      Meno Hochschild
 */
public final class ZonalOffset
    implements Comparable<ZonalOffset>, TZID, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<Integer, ZonalOffset> OFFSET_CACHE =
        new ConcurrentHashMap<Integer, ZonalOffset>();

    private static final BigDecimal DECIMAL_60 = new BigDecimal(60);
    private static final BigDecimal DECIMAL_3600 = new BigDecimal(3600);
    private static final BigDecimal DECIMAL_NEG_180 = new BigDecimal(-180);
    private static final BigDecimal DECIMAL_POS_180 = new BigDecimal(180);
    private static final BigDecimal DECIMAL_240 = new BigDecimal(240);
    private static final BigDecimal MRD = new BigDecimal(1000000000);

    /**
     * <p>Constant for the UTC timezone representing a shift of
     * {@code 0} seconds with the canonical representation &quot;Z&quot;. </p>
     */
    /*[deutsch]
     * <p>Konstante f&uuml;r eine zeitliche Verschiebung von {@code 0} Sekunden
     * mit der kanonischen Darstellung &quot;Z&quot;. </p>
     */
    public static final ZonalOffset UTC;

    static {
        UTC = new ZonalOffset(0, 0);
        OFFSET_CACHE.put(Integer.valueOf(0), UTC);
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

    /**
     * <p>Creates a new shift based on a geographical longitude. </p>
     *
     * <p>Note that fractional offsets are not used in context of timezones,
     * but can only be applied to conversions between {@code PlainTimestamp}
     * and {@code Moment}. </p>
     *
     * @param   longitude   geographical longitude in degrees defined in
     *                      range {@code -180.0 <= longitude <= 180.0}
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Verschiebung auf Basis einer geographischen
     * L&auml;ngenangabe. </p>
     *
     * <p>Hinweis: Fraktionale Verschiebungen werden im Zeitzonenkontext
     * nicht verwendet, sondern nur dann, wenn ein {@code PlainTimestamp}
     * zu einem {@code Moment} oder zur&uuml;ck konvertiert wird. </p>
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
        BigDecimal decimal = delta.setScale(9, RoundingMode.HALF_UP).multiply(MRD);

        int total = integral.intValueExact();
        int fraction = decimal.intValueExact();

        if (fraction == 0) {
            return ZonalOffset.ofTotalSeconds(total);
        } else if (fraction == 1000000000) {
            return ZonalOffset.ofTotalSeconds(total + 1);
        } else if (fraction == -1000000000) {
            return ZonalOffset.ofTotalSeconds(total - 1);
        } else {
            return new ZonalOffset(total, fraction);
        }

    }

    /**
     * <p>Creates a new shift based on a geographical longitude. </p>
     *
     * <p>Note that fractional offsets are not used in context of timezones,
     * but can only be applied to conversions between {@code PlainTimestamp}
     * and {@code Moment}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   degrees     geographical length in degreed, defined in
     *                      range {@code 0 <= degrees <= 180}
     * @param   arcMinutes  arc minute part ({@code 0 <= arcMinutes <= 59})
     * @param   arcSeconds  arc second part ({@code 0 <= arcSeconds <= 59})
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails (also if total
     *          absolute offset goes beyond 180 degrees)
     * @deprecated  Use {@link #atLongitude(OffsetSign, int, int, double)} instead
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Verschiebung auf Basis einer geographischen
     * L&auml;ngenangabe. </p>
     *
     * <p>Hinweis: Fraktionale Verschiebungen werden im Zeitzonenkontext
     * nicht verwendet, sondern nur dann, wenn ein {@code PlainTimestamp}
     * zu einem {@code Moment} oder zur&uuml;ck konvertiert wird. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   degrees     geographical length in degreed, defined in
     *                      range {@code 0 <= degrees <= 180}
     * @param   arcMinutes  arc minute part ({@code 0 <= arcMinutes <= 59})
     * @param   arcSeconds  arc second part ({@code 0 <= arcSeconds <= 59})
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails (also if total
     *          absolute offset goes beyond 180 degrees)
     * @deprecated  Use {@link #atLongitude(OffsetSign, int, int, double)} instead
     */
    @Deprecated
    public static ZonalOffset atLongitude(
        OffsetSign sign,
        int degrees,
        int arcMinutes,
        int arcSeconds
    ) {

        return atLongitude(sign, degrees, arcMinutes, (double) arcSeconds);

    }

    /**
     * <p>Creates a new shift based on a geographical longitude. </p>
     *
     * <p>Note that fractional offsets are not used in context of timezones,
     * but can only be applied to conversions between {@code PlainTimestamp}
     * and {@code Moment}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   degrees     geographical length in degrees, defined in
     *                      range {@code 0 <= degrees <= 180}
     * @param   arcMinutes  arc minute part ({@code 0 <= arcMinutes <= 59})
     * @param   arcSeconds  arc second part ({@code 0.0 <= arcSeconds < 60.0})
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails (also if total
     *          absolute offset goes beyond 180 degrees)
     * @see     #atLongitude(BigDecimal)
     * @since   4.26/3.30
     */
    /*[deutsch]
     * <p>Konstruiert eine neue Verschiebung auf Basis einer geographischen
     * L&auml;ngenangabe. </p>
     *
     * <p>Hinweis: Fraktionale Verschiebungen werden im Zeitzonenkontext
     * nicht verwendet, sondern nur dann, wenn ein {@code PlainTimestamp}
     * zu einem {@code Moment} oder zur&uuml;ck konvertiert wird. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   degrees     geographical length in degrees, defined in
     *                      range {@code 0 <= degrees <= 180}
     * @param   arcMinutes  arc minute part ({@code 0 <= arcMinutes <= 59})
     * @param   arcSeconds  arc second part ({@code 0.0 <= arcSeconds < 60.0})
     * @return  zonal offset in decimal precision
     * @throws  IllegalArgumentException if range check fails (also if total
     *          absolute offset goes beyond 180 degrees)
     * @see     #atLongitude(BigDecimal)
     * @since   4.26/3.30
     */
    public static ZonalOffset atLongitude(
        OffsetSign sign,
        int degrees,
        int arcMinutes,
        double arcSeconds
    ) {

        if (sign == null) {
            throw new NullPointerException("Missing sign.");
        } else if ((degrees < 0) || (degrees > 180)) {
            throw new IllegalArgumentException(
                "Degrees of longitude out of range (0 <= degrees <= 180).");
        } else if ((arcMinutes < 0) || (arcMinutes > 59)) {
            throw new IllegalArgumentException(
                "Arc minute out of range (0 <= arcMinutes <= 59).");
        } else if ((Double.compare(arcSeconds, 0.0) < 0) || (Double.compare(arcSeconds, 60.0) >= 0)) {
            throw new IllegalArgumentException(
                "Arc second out of range (0.0 <= arcSeconds < 60.0).");
        }

        BigDecimal longitude = BigDecimal.valueOf(degrees);

        if (arcMinutes != 0) {
            BigDecimal arcMin =
                BigDecimal.valueOf(arcMinutes)
                    .setScale(15, RoundingMode.UNNECESSARY)
                    .divide(DECIMAL_60, RoundingMode.HALF_UP);
            longitude = longitude.add(arcMin);
        }

        if (arcSeconds != 0) {
            BigDecimal arcSec =
                BigDecimal.valueOf(arcSeconds)
                    .setScale(15, RoundingMode.FLOOR)
                    .divide(DECIMAL_3600, RoundingMode.HALF_UP);
            longitude = longitude.add(arcSec);
        }

        if (sign == OffsetSign.BEHIND_UTC) {
            longitude = longitude.negate();
        }

        return ZonalOffset.atLongitude(longitude);

    }

    /**
     * <p>Static factory method for a shift which has the given full
     * hour part. </p>
     *
     * <p>Is equivalent to {@code ofHoursMinutes(sign, hours, 0}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @return  zonal offset in hour precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #ofHoursMinutes(OffsetSign, int, int)
     */
    /*[deutsch]
     * <p>Statische Fabrikmethode f&uuml;r eine Zeitverschiebung, die den
     * angegebenen vollen Stundenanteil hat. </p>
     *
     * <p>Entspricht {@code ofHoursMinutes(sign, hours, 0}. </p>
     *
     * @param   sign        sign of shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @return  zonal offset in hour precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #ofHoursMinutes(OffsetSign, int, int)
     */
    public static ZonalOffset ofHours(
        OffsetSign sign,
        int hours
    ) {

        return ofHoursMinutes(sign, hours, 0);

    }

    /**
     * <p>Static factory method for a shift which  has given
     * hour and minute parts. </p>
     *
     * <p>The given numerical values are identical to the numerical
     * parts of the canonical representation &#x00B1;hh:mm&quot;.
     * The second part is always {@code 0}. Only values in the range
     * {@code -18:00 <= [total-offset] <= +18:00} are allowed. When
     * calculating the total offset the sign relates to both hour
     * and minute part. Example: The expression
     * {@code ZonalOffset.ofHoursMinutes(BEHIND_UTC, 4, 30)} has the
     * representation {@code -04:30} and a total shift in seconds of
     * {@code -(4 * 3600 + 30 * 60) = 16200}. </p>
     *
     * @param   sign        sign ofHoursMinutes shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @param   minutes     minute part ({@code 0 <= minutes <= 59})
     * @return  zonal offset in minute precision
     * @throws  IllegalArgumentException if range check fails
     */
    /*[deutsch]
     * <p>Statische Fabrikmethode f&uuml;r eine Zeitverschiebung, die die
     * angegebenen Stunden- und Minutenanteile hat. </p>
     *
     * <p>Die angegebenen Zahlenwerte entsprechen exakt den numerischen
     * Bestandteilen der kanonischen Darstellung &#x00B1;hh:mm&quot;. Der
     * Sekundenanteil ist hier immer {@code 0}. Erlaubt sind nur Werte im
     * Bereich {@code -18:00 <= [total-offset] <= +18:00}. Bei der Berechnung
     * der gesamten Verschiebung wird das Vorzeichen nicht nur auf den Stunden-,
     * sondern auch auf den Minutenteil mit &uuml;bertragen. Beispiel: Der
     * Ausdruck {@code ZonalOffset.ofHoursMinutes(BEHIND_UTC, 4, 30)} hat die
     * String-Darstellung {@code -04:30} und eine Gesamtverschiebung in
     * Sekunden von {@code -(4 * 3600 + 30 * 60) = 16200}. </p>
     *
     * @param   sign        sign ofHoursMinutes shift relative to zero meridian
     * @param   hours       hour part ({@code 0 <= hours <= 18})
     * @param   minutes     minute part ({@code 0 <= minutes <= 59})
     * @return  zonal offset in minute precision
     * @throws  IllegalArgumentException if range check fails
     */
    public static ZonalOffset ofHoursMinutes(
        OffsetSign sign,
        int hours,
        int minutes
    ) {

        if (sign == null) {
            throw new NullPointerException("Missing sign.");
        } else if ((hours < 0) || (hours > 18)) {
            throw new IllegalArgumentException(
                "Hour part out of range (0 <= hours <= 18) in: "
                + format(hours, minutes));
        } else if ((minutes < 0) || (minutes > 59)) {
            throw new IllegalArgumentException(
                "Minute part out of range (0 <= minutes <= 59) in: "
                + format(hours, minutes));
        } else if (
            (hours == 18)
            && (minutes != 0)
        ) {
            throw new IllegalArgumentException(
                "Time zone offset out of range "
                + "(-18:00:00 <= offset <= 18:00:00) in: "
                + format(hours, minutes));
        }

        int total = hours * 3600 + minutes * 60;

        if (sign == OffsetSign.BEHIND_UTC) {
            total = -total;
        }

        return ZonalOffset.ofTotalSeconds(total);

    }

    /**
     * <p>Creates a shift of the local time relative to UTC timezone
     * in integer seconds. </p>
     *
     * @param   total   total shift in seconds defined in range
     *                  {@code -18 * 3600 <= total <= 18 * 3600}
     * @return  zonal offset in second precision
     * @throws  IllegalArgumentException if range check fails
     * @see     #getIntegralAmount()
     */
    /*[deutsch]
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
     * <p>Creates a shift of the local time relative to UTC timezone
     * in integer seconds or fractional seconds. </p>
     *
     * <p>Note that fractional offsets are not used in context of timezones,
     * but can only be applied to conversions between {@code PlainTimestamp}
     * and {@code Moment}. </p>
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
    /*[deutsch]
     * <p>Konstruiert eine Verschiebung der lokalen Zeit relativ zur
     * UTC-Zeitzone in integralen oder fraktionalen Sekunden. </p>
     *
     * <p>Hinweis: Fraktionale Verschiebungen werden im Zeitzonenkontext
     * nicht verwendet, sondern nur dann, wenn ein {@code PlainTimestamp}
     * zu einem {@code Moment} oder zur&uuml;ck konvertiert wird. </p>
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
            }
            return result;
        } else {
            return new ZonalOffset(total, 0);
        }

    }

    /**
     * <p>Return the sign of this zonal shift. </p>
     *
     * @return  {@code BEHIND_UTC} if sign is negative else {@code AHEAD_OF_UTC}
     */
    /*[deutsch]
     * <p>Liefert das Vorzeichen der zonalen Verschiebung. </p>
     *
     * @return  {@code BEHIND_UTC} if sign is negative else {@code AHEAD_OF_UTC}
     */
    public OffsetSign getSign() {

        return (
            ((this.total < 0) || (this.fraction < 0))
            ? OffsetSign.BEHIND_UTC
            : OffsetSign.AHEAD_OF_UTC
        );

    }

    /**
     * <p>Returns the hour part of this shift as absolute amount. </p>
     *
     * @return  absolute hour part in range {@code 0 <= x <= 18}
     * @see     #getSign()
     */
    /*[deutsch]
     * <p>Liefert den Stundenanteil der Verschiebung als Absolutbetrag. </p>
     *
     * @return  absolute hour part in range {@code 0 <= x <= 18}
     * @see     #getSign()
     */
    public int getAbsoluteHours() {

        return (Math.abs(this.total) / 3600);

    }

    /**
     * <p>Returns the minute part of this shift as absolute amount. </p>
     *
     * @return  absolute minute part in range {@code 0 <= x <= 59}
     * @see     #getSign()
     */
    /*[deutsch]
     * <p>Liefert den Minutenanteil der Verschiebung als Absolutbetrag. </p>
     *
     * @return  absolute minute part in range {@code 0 <= x <= 59}
     * @see     #getSign()
     */
    public int getAbsoluteMinutes() {

        return ((Math.abs(this.total) / 60) % 60);

    }

    /**
     * <p>Returns the second part of this shift as absolute amount. </p>
     *
     * @return  absolute second part in range {@code 0 <= x <= 59}
     * @see     #getSign()
     */
    /*[deutsch]
     * <p>Liefert den Sekundenanteil der Verschiebung als Absolutbetrag. </p>
     *
     * @return  absolute second part in range {@code 0 <= x <= 59}
     * @see     #getSign()
     */
    public int getAbsoluteSeconds() {

        return (Math.abs(this.total) % 60);

    }

    /**
     * <p>Total shift in integer seconds without fractional part. </p>
     *
     * @return  integral part in seconds {@code -18 * 3600 <= x <= 18 * 3600}
     * @see     #getFractionalAmount()
     */
    /*[deutsch]
     * <p>Verschiebung in integralen Sekunden ohne Sekundenbruchteil. </p>
     *
     * @return  integral part in seconds {@code -18 * 3600 <= x <= 18 * 3600}
     * @see     #getFractionalAmount()
     */
    public int getIntegralAmount() {

        return this.total;

    }

    /**
     * <p>Returns the fractional second part of this shift in nanoseconds. </p>
     *
     * <p>Only longitudinal offsets may have fractional parts. </p>
     *
     * @return  fractional part in range {@code -999999999 <= x <= 999999999}
     * @see     #getIntegralAmount()
     */
    /*[deutsch]
     * <p>Liefert den Sekundenbruchteil der Verschiebung in
     * Nanosekundenform. </p>
     *
     * <p>Nur longitudinale Verschiebungen k&ouml;nnen einen fraktionalen
     * Anteil haben. </p>
     *
     * @return  fractional part in range {@code -999999999 <= x <= 999999999}
     * @see     #getIntegralAmount()
     */
    public int getFractionalAmount() {

        return this.fraction;

    }

    /**
     * <p>Compares the whole state with sign, hours, minutes, seconds and
     * fractional seconds in ascending order. </p>
     *
     * <p>Shifts with sign west for Greenwich (behind UTC) are considered as
     * smaller than shifts with sign east for Greenwich (ahead of UTC). </p>
     *
     * <p>The natural order is consistent with {@code equals()}. </p>
     */
    /*[deutsch]
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
     * <p>Compares the whole state. </p>
     */
    /*[deutsch]
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
     * <p>Calculates the hash value. </p>
     */
    /*[deutsch]
     * <p>Berechnet den Hash-Wert. </p>
     */
    @Override
    public int hashCode() {

        return (~this.total + (this.fraction % 64000));

    }

    /**
     * <p>Returns a complete short representation of this shift including
     * the sign. </p>
     *
     * <p>Notes: If there are only full minutes or hours the representation
     * is exactly as described in ISO-8601. Another long canonical
     * representation can be obtained by the method {@code canonical()}. </p>
     *
     * @return  String in ISO-8601 format &quot;&#x00B1;hh:mm&quot; or
     *          &quot;&#x00B1;hh:mm:ss&quot; if there is a second part
     *          or &quot;&#x00B1;hh:mm:ss.fffffffff&quot; if any fractional
     *          part exists
     */
    /*[deutsch]
     * <p>Liefert eine vollst&auml;ndige Kurzdarstellung der Verschiebung
     * mit Vorzeichen. </p>
     *
     * <p>Hinweise: Sofern nur volle Minuten oder Stunden vorliegen, entspricht
     * die Darstellung exakt dem empfohlenen ISO-8601-Standard. Eine andere
     * lange kanonische Darstellung ist mittels {@code canonical()}
     * erh&auml;ltlich. </p>
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
     * <p>Returns a long canonical representation of this shift. </p>
     *
     * <p>Notes: If this instance denotes the UTC timezone then this method
     * will yield the string &quot;Z&quot;. Another short canonical
     * representation can be obtained by the method {@code toString()}. </p>
     *
     * @return  String in format &quot;UTC&#x00B1;hh:mm&quot; or
     *          &quot;UTC&#x00B1;hh:mm:ss&quot; if there is a second part
     *          or &quot;UTC&#x00B1;hh:mm:ss.fffffffff&quot; if any fractional
     *          part exists or &quot;Z&quot; in timezone UTC
     * @see     #toString()
     */
    /*[deutsch]
     * <p>Liefert eine lange kanonische Darstellung der Verschiebung. </p>
     *
     * <p>Hinweise: Handelt es sich um die UTC-Zeitzone selbst, liefert die
     * Methode die Darstellung &quot;Z&quot;. Eine andere kurze kanonische
     * Darstellung ist mittels {@code toString()} erh&auml;ltlich. </p>
     *
     * @return  String in format &quot;UTC&#x00B1;hh:mm&quot; or
     *          &quot;UTC&#x00B1;hh:mm:ss&quot; if there is a second part
     *          or &quot;UTC&#x00B1;hh:mm:ss.fffffffff&quot; if any fractional
     *          part exists or &quot;Z&quot; in timezone UTC
     * @see     #toString()
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
     * <p>Interpretes a canonical representation as zonal offset. </p>
     *
     * <p>All string produced by the methods {@code canonical()} or
     * {@code toString()} are supported. Due to the technical nature
     * of canonical representations this method is not designed to
     * parse any kind of user-defined input, especially the use of
     * GMT-prefix is NOT canonical and outdated from a scientific
     * point of view. </p>
     *
     * <p>Examples for supported formats: </p>
     *
     * <ul>
     *     <li>UTC+5</li>
     *     <li>UTC+05</li>
     *     <li>UTC+5:30</li>
     *     <li>UTC+05:30</li>
     *     <li>UTC+5:30:21</li>
     *     <li>UTC+05:30:21</li>
     *     <li>UTC+5:30:21.123456789</li>
     *     <li>UTC+05:30:21.123456789</li>
     *     <li>+5</li>
     *     <li>+05</li>
     *     <li>+5:30</li>
     *     <li>+05:30</li>
     *     <li>+5:30:21</li>
     *     <li>+05:30:21</li>
     *     <li>+5:30:21.123456789</li>
     *     <li>+05:30:21.123456789</li>
     * </ul>
     *
     * <p>Note: All formats containing only the hour or the hour with only one digit
     * are first supported in version 3.1 or later. </p>
     *
     * @param   canonical       zonal offset in canonical form to be parsed
     * @return  parsed {@code ZonalOffset}
     * @throws  IllegalArgumentException if given input is not canonical
     * @since   2.2
     * @see     #canonical()
     * @see     #toString()
     */
    /*[deutsch]
     * <p>Interpretiert eine kanonische Darstellung als Verschiebung. </p>
     *
     * <p>Unterst&uuml;tzt werden alle von den Methoden {@code canonical()}
     * oder {@code toString()} produzierten Ausgaben. Aufgrund der technischen
     * Natur kanonischer Darstellungen ist diese Methode nicht dazu gedacht,
     * irgendeine benutzerdefinierte Darstellung zu interpretieren. Zu
     * beachten: Die Verwendung des GMT-Pr&auml;fix ist NICHT kanonisch
     * und von einem wissenschaftlichen Standpunkt aus gesehen veraltet. </p>
     *
     * <p>Beispiele f&uuml;r unterst&uuml;tzte Formate: </p>
     *
     * <ul>
     *     <li>UTC+5</li>
     *     <li>UTC+05</li>
     *     <li>UTC+5:30</li>
     *     <li>UTC+05:30</li>
     *     <li>UTC+5:30:21</li>
     *     <li>UTC+05:30:21</li>
     *     <li>UTC+5:30:21.123456789</li>
     *     <li>UTC+05:30:21.123456789</li>
     *     <li>+5</li>
     *     <li>+05</li>
     *     <li>+5:30</li>
     *     <li>+05:30</li>
     *     <li>+5:30:21</li>
     *     <li>+05:30:21</li>
     *     <li>+5:30:21.123456789</li>
     *     <li>+05:30:21.123456789</li>
     * </ul>
     *
     * <p>Anmerkung: Alle Formate mit einem einstelligen Stundenanteil oder nur mit Stundenanteil
     * werden erst seit Version 3.1 unterst&uuml;tzt. </p>
     *
     * @param   canonical       zonal offset in canonical form to be parsed
     * @return  parsed {@code ZonalOffset}
     * @throws  IllegalArgumentException if given input is not canonical
     * @since   2.2
     * @see     #canonical()
     * @see     #toString()
     */
    public static ZonalOffset parse(String canonical) {

        return parse(canonical, true);

    }

    /**
     * <p>Obtains a typical localized format pattern in minute precision. </p>
     *
     * <p>The character &quot;&#x00B1;&quot; represents a localized offset sign. And the double letters
     * &quot;hh&quot; and &quot;mm&quot; represent localized digits of hour respective minute part of the
     * offset. All other characters are to be interpreted as literals. Many locales return the format
     * &quot;GMT&#x00B1;hh:mm&quot;. </p>
     *
     * <p>This method is mainly designed for the internal use of the expert format engine of Time4J. </p>
     *
     * @param   locale  language setting
     * @return  localized offset pattern
     * @since   3.23/4.19
     */
    /*[deutsch]
     * <p>Ermittelt ein typisches sprachspezifisches Formatmuster in Minutengenauigkeit. </p>
     *
     * <p>Das Zeichen &quot;&#x00B1;&quot; repr&auml;sentiert ein lokalisiertes Vorzeichen. Und die
     * gedoppelten Buchstaben &quot;hh&quot; und &quot;mm&quot; repr&auml;sentieren lokalisierte
     * Dezimalziffern des Stunden- bzw. Minutenteils dieser Instanz. Alle anderen Zeichen m&uuml;ssen
     * als Literale interpretiert werden. Viele Sprachen liefern das Standardformat &quot;GMT&#x00B1;hh:mm&quot;. </p>
     *
     * <p>Diese Methode ist haupts&auml;chlich f&uuml;r die interne Verwendung in der <i>expert</i>-Formatmaschine
     * von Time4J gedacht. </p>
     *
     * @param   locale  language setting
     * @return  localized offset pattern
     * @since   3.23/4.19
     */
    public String getStdFormatPattern(Locale locale) {

        boolean zeroOffset = ((this.total == 0) && (this.fraction == 0));

        try {
            return Timezone.NAME_PROVIDER.getStdFormatPattern(zeroOffset, locale);
        } catch (Throwable t) { // can only happen if the provider is an outdated version
            return (zeroOffset ? "GMT" : "GMT\u00B1hh:mm");
        }

    }

    /**
     * <p>Interpretiert eine kanonische Darstellung als Verschiebung. </p>
     *
     * <p>Unterst&uuml;tzt werden alle von den Methoden {@code canonical()} oder
     * {@code toString()} produzierten Ausgaben. </p>
     *
     * @param   offset          zonal offset in canonical form to be parsed
     * @param   wantsException  shall an exception be thrown in case of error?
     * @return  parsed {@code ZonalOffset} or {@code null} in case of error
     * @see     #canonical()
     * @see     #toString()
     */
    static ZonalOffset parse(
        String offset,
        boolean wantsException
    ) {

        if (offset.equals("Z")) {
            return ZonalOffset.UTC;
        }

        int n = offset.length();
        String test = offset;

        if (n >= 3) {
            if (test.startsWith("UTC")) {
                test = offset.substring(3);
                n -= 3;
            } else if (test.startsWith("GMT")) {
                if (wantsException) {
                    throw new IllegalArgumentException(
                        "Use UTC-prefix for canonical offset instead: "
                        + offset);
                } else {
                    return null;
                }
            }
        }

        if (n >= 2) {
            OffsetSign sign = null;

            if (test.charAt(0) == '-') {
                sign = OffsetSign.BEHIND_UTC;
            } else if (test.charAt(0) == '+') {
                sign = OffsetSign.AHEAD_OF_UTC;
            }

            int hours = parse(test, 1, 2);

            if (hours >= 0) {
                if (n <= 3){
                    return ZonalOffset.ofHours(sign, hours);
                }

                int start = 4;

                if ((test.charAt(2) == ':')) {
                    start = 3;
                }

                int minutes = parse(test, start, 2);

                if (
                    (test.charAt(start - 1) == ':')
                    && (minutes >= 0)
                ) {
                    if (n == start + 2) {
                        return ZonalOffset.ofHoursMinutes(sign, hours, minutes);
                    } else if (
                        (n >= start + 5)
                        && (test.charAt(start + 2) == ':')
                    ) {
                        int seconds = parse(test, start + 3, 2);
                        if (seconds >= 0) {
                            int total = hours * 3600 + minutes * 60 + seconds;
                            if (sign == OffsetSign.BEHIND_UTC) {
                                total = -total;
                            }
                            if (n == start + 5) {
                                return ZonalOffset.ofTotalSeconds(total);
                            } else if (
                                (n == start + 15)
                                && (test.charAt(start + 5) == '.')
                            ) {
                                int fraction = parse(test, start + 6, 9);
                                if (fraction >= 0) {
                                    if (sign == OffsetSign.BEHIND_UTC) {
                                        fraction = -fraction;
                                    }
                                    return ZonalOffset.ofTotalSeconds(
                                        total,
                                        fraction);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (wantsException) {
            throw new IllegalArgumentException(
                "No canonical zonal offset: " + offset);
        } else {
            return null;
        }

    }

    /**
     * <p>Liefert die Verschiebung als Zeitzonenmodell. </p>
     *
     * @return  timezone data with fixed shift in full seconds
     */
    SingleOffsetTimezone getModel() {

        return SingleOffsetTimezone.of(this);

    }

    private static int parse(
        String offset,
        int index,
        int len
    ) {

        int amount = -1;

        for (int i = 0, n = Math.min(offset.length() - index, len); i < n; i++) {
            char c = offset.charAt(index + i);
            if ((c >= '0') && (c <= '9')) {
                if (amount == -1) {
                    amount = (c - '0');
                } else {
                    amount = amount * 10 + (c - '0');
                }
            } else {
                break;
            }
        }

        return amount;

    }

    private static String format(
        int hours,
        int minutes
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("[hours=");
        sb.append(hours);
        sb.append(",minutes=");
        sb.append(minutes);
        sb.append(']');
        return sb.toString();

    }

    /**
     * @serialData  Uses a specialized serialisation form as proxy. The format
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
     *  int header = (15 &lt;&lt; 4);
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
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {

        return new SPX(this, SPX.ZONAL_OFFSET_TYPE);

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
