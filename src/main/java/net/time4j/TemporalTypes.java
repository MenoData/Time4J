/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TemporalTypes.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.base.MathUtils;
import net.time4j.engine.BasicElement;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.scale.TimeScale;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Dient als Br&uuml;cke zu Datums- und Zeittypen aus dem JDK oder
 * anderen Bibliotheken. </p>
 *
 * <p>Alle Singleton-Instanzen sind als statische Konstanten definiert und
 * unver&auml;nderlich (<i>immutable</i>). </p>
 *
 * @param   <S> source type in other library
 * @param   <T> target type in Time4J
 * @author  Meno Hochschild
 */
public class TemporalTypes<S extends Comparable<?>, T extends ChronoEntity<T>>
    extends BasicElement<S> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final boolean WITH_SQL_UTC_CONVERSION =
        Boolean.getBoolean("net.time4j.sql.utc.conversion");
    private static final PlainDate UNIX_DATE = PlainDate.of(0, EpochDays.UNIX);

    private static final long serialVersionUID = 1081658250255619999L;
    private static final int MIO = 1000000;
    private static final java.sql.Timestamp TIMESTAMP_MAX;

    static {
        java.sql.Timestamp ts = new java.sql.Timestamp(Long.MAX_VALUE);
        ts.setNanos(999999999);
        TIMESTAMP_MAX = ts;
    }

    /**
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Zeitstempel des Typs
     * {@code java.util.Date} und der Klasse {@code Moment}. </p>
     *
     * <p>Die Konversion ber&uuml;cksichtigt KEINE UTC-Schaltsekunden. Der
     * unterst&uuml;tzte Wertbereich ist etwas kleiner als in der Klasse
     * {@code Moment}. Beispiel: </p>
     *
     * <pre>
     *  java.util.Date instant = new java.util.Date(86401 * 1000);
     *  Moment ut = TemporalTypes.JAVA_UTIL_DATE.transform(instant);
     *  System.out.println(
     *      ut.get(TemporalTypes.JAVA_UTIL_DATE).equals(instant));
     *  // Ausgabe: true
     * </pre>
     */
    public static final TemporalTypes<java.util.Date, Moment> JAVA_UTIL_DATE =
        new TemporalTypes<java.util.Date, Moment>(
            "JAVA_UTIL_DATE",
            java.util.Date.class,
            Moment.class,
            new JavaUtilDateRule(),
            new Comparator<ChronoEntity<?>>() {
                @Override
                public int compare(
                    ChronoEntity<?> o1,
                    ChronoEntity<?> o2
                ) {
                    java.util.Date ts1 = o1.get(JAVA_UTIL_DATE);
                    java.util.Date ts2 = o2.get(JAVA_UTIL_DATE);
                    return ts1.compareTo(ts2);
                }
            },
            new java.util.Date(Long.MIN_VALUE),
            new java.util.Date(Long.MAX_VALUE),
            true,
            true
        );

    /**
     * <p>Br&uuml;cke zwischen einem traditionellen Java-Zeitstempel als
     * Anzahl der Millisekunden seit der UNIX-Epoche und der Klasse
     * {@code Moment}. </p>
     *
     * <p>Die Konversion ber&uuml;cksichtigt KEINE UTC-Schaltsekunden. Der
     * unterst&uuml;tzte Wertbereich ist etwas kleiner als in der Klasse
     * {@code Moment}. Beispiel: </p>
     *
     * <pre>
     *  long instant = 86401 * 1000L;
     *  Moment ut = TemporalTypes.MILLIS_SINCE_UNIX.transform(instant);
     *  System.out.println(ut);
     *  // Ausgabe: 1970-01-02T00:00:01Z
     * </pre>
     */
    public static final TemporalTypes<Long, Moment> MILLIS_SINCE_UNIX =
        new TemporalTypes<Long, Moment>(
            "MILLIS_SINCE_UNIX",
            Long.class,
            Moment.class,
            new MillisSinceUnixRule(),
            new Comparator<ChronoEntity<?>>() {
                @Override
                public int compare(
                    ChronoEntity<?> o1,
                    ChronoEntity<?> o2
                ) {
                    Long ts1 = o1.get(MILLIS_SINCE_UNIX);
                    Long ts2 = o2.get(MILLIS_SINCE_UNIX);
                    return ts1.compareTo(ts2);
                }
            },
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            true,
            true
        );

    /**
     * <p>Br&uuml;cke zwischen einem JDBC-Date und der Klasse
     * {@code PlainDate}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot;
     * auf den Wert &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die
     * Konversion NICHT die Standardzeitzone des Systems und setzt somit voraus,
     * da&szlig; ein SQL-DATE java-seitig ebenfalls ohne Zeitzonenkalkulation
     * erzeugt wurde. Das ist de facto der Fall, wenn auf dem Application-Server
     * UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Date sqlValue = new java.sql.Date(86400 * 1000);
     *  PlainDate date =
     *      TemporalTypes.SQL_DATE.transform(sqlValue); // 1970-01-02
     *  System.out.println(
     *      date.get(TemporalTypes.SQL_DATE).equals(sqlValue));
     *  // Ausgabe: true
     * </pre>
     *
     * <p><strong>Zu beachten:</strong> Die Konversion ist nur m&ouml;glich,
     * wenn ein Datum ein Jahr im Bereich {@code 1900-9999} hat, denn
     * sonst kann eine JDBC-kompatible Datenbank den Datumswert per
     * SQL-Spezifikation nicht speichern. Es wird dringend empfohlen, ein
     * SQL-DATE nur als abstraktes JDBC-Objekt zu interpretieren, weil
     * seine Textausgabe via {@code java.sql.Date.toString()}-Methode nicht
     * zuverl&auml;ssig ist (Abh&auml;ngigkeit vom gregorianisch-julianischen
     * Umstellungstag + evtl. Zeitzoneneffekte). Die konkrete Formatierung kann
     * von Time4J korrekt zum Beispiel via {@code PlainDate.toString()} oder
     * &uuml;ber einen geeigneten {@code ChronoFormatter} geleistet werden. </p>
     */
    public static final TemporalTypes<java.sql.Date, PlainDate> SQL_DATE =
        new TemporalTypes<java.sql.Date, PlainDate>(
            "SQL_DATE",
            java.sql.Date.class,
            PlainDate.class,
            new SqlDateRule(),
            new Comparator<ChronoEntity<?>>() {
                @Override
                public int compare(
                    ChronoEntity<?> o1,
                    ChronoEntity<?> o2
                ) {
                    return o1.get(SQL_DATE).compareTo(o2.get(SQL_DATE));
                }
            },
            new java.sql.Date(-2208988800000L), // 1900-01-01
            new java.sql.Date(253402214400000L + 86399999), // 9999-12-31
            true,
            false
        );

    /**
     * <p>Br&uuml;cke zwischen einem JDBC-Time und der Klasse
     * {@code PlainTime}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot;
     * auf den Wert &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die
     * Konversion NICHT die Standardzeitzone des Systems und setzt somit voraus,
     * da&szlig; ein SQL-TIME java-seitig ebenfalls ohne Zeitzonenkalkulation
     * erzeugt wurde. Das ist de facto der Fall, wenn auf dem Application-Server
     * UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Time sqlValue = new java.sql.Time(43200 * 1000);
     *  PlainTime time =
     *      TemporalTypes.SQL_TIME.transform(sqlValue); // T12:00:00
     *  System.out.println(
     *      time.get(TemporalTypes.SQL_TIME).equals(sqlValue));
     *  // Ausgabe: true
     * </pre>
     *
     * <p><strong>Zu beachten:</strong> Die Konversion geschieht nur in
     * Milli-, nicht in Nanosekundenpr&auml;zision, so da&szlig; eventuell
     * Informationsverluste auftreten k&ouml;nnen. Auch ist die Textausgabe
     * mittels {@code java.sql.Time.toString()} durch Zeitzoneneffekte
     * verf&auml;lscht. Konkrete Textausgaben sollen daher immer durch Time4J
     * erfolgen. </p>
     */
    public static final TemporalTypes<java.sql.Time, PlainTime> SQL_TIME =
        new TemporalTypes<java.sql.Time, PlainTime>(
            "SQL_TIME",
            java.sql.Time.class,
            PlainTime.class,
            new SqlTimeRule(),
            new Comparator<ChronoEntity<?>>() {
                @Override
                public int compare(
                    ChronoEntity<?> o1,
                    ChronoEntity<?> o2
                ) {
                    return o1.get(SQL_TIME).compareTo(o2.get(SQL_TIME));
                }
            },
            new java.sql.Time(PlainTime.MILLI_OF_DAY.getDefaultMinimum()),
            new java.sql.Time(PlainTime.MILLI_OF_DAY.getDefaultMaximum()),
            false,
            true
        );

    /**
     * <p>Br&uuml;cke zwischen einem JDBC-Timestamp und der Klasse
     * {@code PlainTimestamp}. </p>
     *
     * <p>Wenn die System-Property &quot;net.time4j.sql.utc.conversion&quot;
     * auf den Wert &quot;true&quot; gesetzt ist, dann ber&uuml;cksichtigt die
     * Konversion NICHT die Standardzeitzone des Systems und setzt somit voraus,
     * da&szlig; ein SQL-TIMESTAMP java-seitig auch ohne Zeitzonenkalkulation
     * erzeugt wurde. Das ist de facto der Fall, wenn auf dem Application-Server
     * UTC die Standardzeitzone ist. </p>
     *
     * <p>Beispiel (UTC als Standardzeitzone): </p>
     *
     * <pre>
     *  java.sql.Timestamp sqlValue = new java.sql.Timestamp(86401 * 1000);
     *  sqlValue.setNanos(1);
     *  PlainTimestamp ts = // 1970-01-02T00:00:01,000000001
     *      TemporalTypes.SQL_TIMESTAMP.transform(sqlValue);
     *  System.out.println(
     *      ts.get(TemporalTypes.SQL_TIMESTAMP).equals(sqlValue));
     *  // Ausgabe: true
     * </pre>
     */
    public static final
    TemporalTypes<java.sql.Timestamp, PlainTimestamp> SQL_TIMESTAMP =
        new TemporalTypes<java.sql.Timestamp, PlainTimestamp>(
            "SQL_TIMESTAMP",
            java.sql.Timestamp.class,
            PlainTimestamp.class,
            new SqlTimestampRule(),
            new Comparator<ChronoEntity<?>>() {
                @Override
                public int compare(
                    ChronoEntity<?> o1,
                    ChronoEntity<?> o2
                ) {
                    java.sql.Timestamp ts1 = o1.get(SQL_TIMESTAMP);
                    java.sql.Timestamp ts2 = o2.get(SQL_TIMESTAMP);
                    return ts1.compareTo(ts2);
                }
            },
            new java.sql.Timestamp(Long.MIN_VALUE),
            TIMESTAMP_MAX,
            true,
            true
        );

    private static final Map<String, TemporalTypes<?, ?>> CACHE;

    static {
        Map<String, TemporalTypes<?, ?>> map =
            new HashMap<String, TemporalTypes<?, ?>>();
        fill(map, TemporalTypes.JAVA_UTIL_DATE);
        fill(map, TemporalTypes.MILLIS_SINCE_UNIX);
        fill(map, TemporalTypes.SQL_DATE);
        fill(map, TemporalTypes.SQL_TIME);
        fill(map, TemporalTypes.SQL_TIMESTAMP);
        CACHE = Collections.unmodifiableMap(map);
    }

    //~ Instanzvariablen --------------------------------------------------

    private transient final Class<S> sourceType;
    private transient final Class<T> targetType;
    private transient final ElementRule<T, S> rule;
    private transient final Comparator<ChronoEntity<?>> comparator;
    private transient final S dmin;
    private transient final S dmax;
    private transient final boolean dateLike;
    private transient final boolean timeLike;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Konstruiert eine neue Instanz. </p>
     *
     * <p>SPEZIFIKATION: Subklassen d&uuml;rfen nur einmalig eine Instanz
     * erzeugen und m&uuml;ssen sie dann per Singleton-Muster einer statischen
     * Konstanten zuweisen. Auch ist die Unver&auml;nderlichkeit der konkreten
     * Klasse zwingend erforderlich. </p>
     *
     * @param   name        unique element name
     * @param   sourceType  source type in other library
     * @param   targetType  target type in Time4J
     * @param   rule        element rule
     * @param   comparator  for comparisons
     * @param   dmin        default minimum
     * @param   dmax        default maximum
     * @param   dateLike    date element?
     * @param   timeLike    time element?
     * @throws  IllegalArgumentException if given name is already used or empty
     */
    protected TemporalTypes(
        String name,
        Class<S> sourceType,
        Class<T> targetType,
        ElementRule<T, S> rule,
        Comparator<ChronoEntity<?>> comparator,
        S dmin,
        S dmax,
        boolean dateLike,
        boolean timeLike
    ) {
        super(name);

        if (sourceType == null) {
            throw new NullPointerException("Missing source type.");
        } else if (targetType == null) {
            throw new NullPointerException("Missing target type.");
        } else if (rule == null) {
            throw new NullPointerException("Missing chronological rule.");
        } else if (comparator == null) {
            throw new NullPointerException("Missing comparator.");
        } else if (dmin == null) {
            throw new NullPointerException("Missing standard minimum.");
        } else if (dmax == null) {
            throw new NullPointerException("Missing standard maximum.");
        }

        this.sourceType = sourceType;
        this.targetType = targetType;
        this.rule = rule;
        this.comparator = comparator;
        this.dmin = dmin;
        this.dmax = dmax;
        this.dateLike = dateLike;
        this.timeLike = timeLike;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Class<S> getType() {

        return this.sourceType;

    }

    /**
     * <p>Wandelt den angegebenen Zeitwert aus dem Fremdsystem zu einem
     * Time4J-Typ um. </p>
     *
     * <p>Die umgekehrte Konversion wird sehr einfach durch den Ausdruck
     * {@code time4jType.get(TemporalTypes.XYZ)} realisiert. Beispiel: </p>
     *
     * <pre>
     *  java.sql.Date sqlValue = new java.sql.Date(86400 * 1000);
     *  PlainDate date = TemporalTypes.SQL_DATE.transform(sqlValue);
     *  System.out.println(date);
     *  // Ausgabe: 1970-01-02
     * </pre>
     *
     * @param   value   datetime value to be transformed
     * @return  Time4J-value
     */
    public T transform(S value) {

        T context = null;

        if (this.getClass() != TemporalTypes.class) {
            context =
                Chronology.lookup(this.targetType).createFrom(
                    SystemClock.INSTANCE,
                    new Attributes.Builder().setStdTimezone().build());
        }

        return this.rule.withValue(context, value, this.isLenient());

    }

    @Override
    public int compare(
        ChronoEntity<?> o1,
        ChronoEntity<?> o2
    ) {

        return this.comparator.compare(o1, o2);

    }

    @Override
    public S getDefaultMinimum() {

        return this.dmin;

    }

    @Override
    public S getDefaultMaximum() {

        return this.dmax;

    }

    @Override
    public boolean isDateElement() {

        return this.dateLike;

    }

    @Override
    public boolean isTimeElement() {

        return this.timeLike;

    }

    /**
     * <p>Leitet eine Elementregel f&uuml;r die angegebene Chronologie ab,
     * wenn letztere dem Zieltyp entspricht. </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends ChronoEntity<T>>
        ElementRule<T, S> derive(Chronology<T> chronology) {

        if (chronology.getChronoType() == this.targetType) {
            return (ElementRule<T, S>) this.rule;
        }

        return null;

    }

    private static void fill(
        Map<String, TemporalTypes<?, ?>> map,
        TemporalTypes<?, ?> tt
    ) {

        map.put(tt.name(), tt);

    }

    /**
     * @serialData  Resolves to singleton constants if possible.
     */
    private Object readResolve() throws ObjectStreamException {

        Object instance = CACHE.get(this.name());
        return ((instance == null) ? this : instance);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class JavaUtilDateRule
        implements ElementRule<Moment, java.util.Date> {

        //~ Instanzvariablen ----------------------------------------------

        private final ElementRule<Moment, Long> rule =
            new MillisSinceUnixRule();

        //~ Methoden ------------------------------------------------------

        @Override
        public java.util.Date getValue(Moment context) {

            return new java.util.Date(this.rule.getValue(context));

        }

        @Override
        public Moment withValue(
            Moment context,
            java.util.Date value,
            boolean lenient
        ) {

            return this.rule.withValue(
                context,
                Long.valueOf(value.getTime()),
                lenient
            );

        }

        @Override
        public boolean isValid(
            Moment context,
            java.util.Date value
        ) {

            return (value != null); // jeder Date-Wert ist gültig

        }

        @Override
        public java.util.Date getMinimum(Moment context) {

            return TemporalTypes.JAVA_UTIL_DATE.getDefaultMinimum();

        }

        @Override
        public java.util.Date getMaximum(Moment context) {

            return TemporalTypes.JAVA_UTIL_DATE.getDefaultMaximum();

        }

        @Override
        public ChronoElement<?> getChildAtFloor(Moment context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(Moment context) {

            return null;

        }

    }

    private static class MillisSinceUnixRule
        implements ElementRule<Moment, Long> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Long getValue(Moment context) {

            long posixTime = context.getPosixTime();
            int fraction = context.getNanosecond();

            return MathUtils.safeAdd(
                MathUtils.safeMultiply(posixTime, 1000),
                fraction / MIO
            );

        }

        @Override
        public Moment withValue(
            Moment context,
            Long value,
            boolean lenient
        ) {

            long millis = value.longValue();
            long seconds = MathUtils.floorDivide(millis, 1000);
            int nanos = MathUtils.floorModulo(millis, 1000) * MIO;
            return Moment.of(seconds, nanos, TimeScale.POSIX);

        }

        @Override
        public boolean isValid(
            Moment context,
            Long value
        ) {

            return (value != null); // jeder long-Wert ist gültig

        }

        @Override
        public Long getMinimum(Moment context) {

            return TemporalTypes.MILLIS_SINCE_UNIX.getDefaultMinimum();

        }

        @Override
        public Long getMaximum(Moment context) {

            return TemporalTypes.MILLIS_SINCE_UNIX.getDefaultMaximum();

        }

        @Override
        public ChronoElement<?> getChildAtFloor(Moment context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(Moment context) {

            return null;

        }

    }

    private static class SqlDateRule
        implements ElementRule<PlainDate, java.sql.Date> {

        //~ Methoden ------------------------------------------------------

        @Override
        public java.sql.Date getValue(PlainDate context) {

            int year = context.getYear();

            if (
                (year < 1900)
                || (year > 9999)
            ) {
                throw new ChronoException(
                    "SQL-Date is only defined in year range of 1900-9999.");
            }

            long millis = // localMillis
                MathUtils.safeMultiply(
                    context.getDaysSinceUTC() + 2 * 365, 86400 * 1000);

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(context, PlainTime.MIN);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Date(millis);

        }

        @Override
        public PlainDate withValue(
            PlainDate context,
            java.sql.Date value,
            boolean lenient
        ) {

            long millis = value.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime =
                    Moment.of(
                        MathUtils.floorDivide(millis, 1000),
                        TimeScale.POSIX);
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            return PlainDate.axis().getCalendarSystem().transform(
                MathUtils.floorDivide(millis, 86400 * 1000) - 2 * 365);

        }

        @Override
        public boolean isValid(
            PlainDate context,
            java.sql.Date value
        ) {

            return (value != null); // jeder Date-Wert ist gültig

        }

        @Override
        public java.sql.Date getMinimum(PlainDate context) {

            return TemporalTypes.SQL_DATE.getDefaultMinimum();

        }

        @Override
        public java.sql.Date getMaximum(PlainDate context) {

            return TemporalTypes.SQL_DATE.getDefaultMaximum();

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainDate context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainDate context) {

            return null;

        }

    }

    private static class SqlTimeRule
        implements ElementRule<PlainTime, java.sql.Time> {

        //~ Methoden ------------------------------------------------------

        @Override
        public java.sql.Time getValue(PlainTime context) {

            long millis = // local millis
                context.get(PlainTime.MILLI_OF_DAY).intValue();

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(UNIX_DATE, context);
                millis -= offset.getIntegralAmount() * 1000;
            }

            return new java.sql.Time(millis);

        }

        @Override
        public PlainTime withValue(
            PlainTime context,
            java.sql.Time value,
            boolean lenient
        ) {

            long millis = value.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime =
                    Moment.of(
                        MathUtils.floorDivide(millis, 1000),
                        TimeScale.POSIX);
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            return PlainTime.MIN.with(
                PlainTime.MILLI_OF_DAY,
                MathUtils.floorModulo(millis, 86400 * 1000)
            );

        }

        @Override
        public boolean isValid(
            PlainTime context,
            java.sql.Time value
        ) {

            return (value != null); // jeder Time-Wert ist gültig

        }

        @Override
        public java.sql.Time getMinimum(PlainTime context) {

            return TemporalTypes.SQL_TIME.getDefaultMinimum();

        }

        @Override
        public java.sql.Time getMaximum(PlainTime context) {

            return TemporalTypes.SQL_TIME.getDefaultMaximum();

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTime context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTime context) {

            return null;

        }

    }

    private static class SqlTimestampRule
        implements ElementRule<PlainTimestamp, java.sql.Timestamp> {

        //~ Methoden ------------------------------------------------------

        @Override
        public java.sql.Timestamp getValue(PlainTimestamp context) {

            long dateMillis = // local millis
                MathUtils.safeMultiply(
                    context.getCalendarDate().getDaysSinceUTC() + 2 * 365,
                    86400 * 1000);
            long timeMillis = // local millis
                context.get(PlainTime.MILLI_OF_DAY).intValue();

            if (!WITH_SQL_UTC_CONVERSION) {
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(context, context);
                timeMillis -= offset.getIntegralAmount() * 1000;
            }

            java.sql.Timestamp ret =
                new java.sql.Timestamp(
                    MathUtils.safeAdd(dateMillis, timeMillis));
            ret.setNanos(context.get(PlainTime.NANO_OF_SECOND).intValue());
            return ret;

        }

        @Override
        public PlainTimestamp withValue(
            PlainTimestamp context,
            java.sql.Timestamp value,
            boolean lenient
        ) {

            long millis = value.getTime(); // UTC zone

            if (!WITH_SQL_UTC_CONVERSION) {
                Moment unixTime =
                    Moment.of(
                        MathUtils.floorDivide(millis, 1000),
                        TimeScale.POSIX);
                ZonalOffset offset =
                    Timezone.ofSystem().getOffset(unixTime);
                millis += offset.getIntegralAmount() * 1000;
            }

            PlainDate date =
                PlainDate.of(
                    MathUtils.floorDivide(millis, 86400 * 1000),
                    EpochDays.UNIX);
            PlainTime time =
                PlainTime.createFromMillis(
                    MathUtils.floorModulo(millis, 86400 * 1000));
            PlainTimestamp ts = PlainTimestamp.of(date, time);
            return ts.with(PlainTime.NANO_OF_SECOND, value.getNanos());

        }

        @Override
        public boolean isValid(
            PlainTimestamp context,
            java.sql.Timestamp value
        ) {

            return (value != null); // jeder Timestamp-Wert ist gültig

        }

        @Override
        public java.sql.Timestamp getMinimum(PlainTimestamp context) {

            return TemporalTypes.SQL_TIMESTAMP.getDefaultMinimum();

        }

        @Override
        public java.sql.Timestamp getMaximum(PlainTimestamp context) {

            return TemporalTypes.SQL_TIMESTAMP.getDefaultMaximum();

        }

        @Override
        public ChronoElement<?> getChildAtFloor(PlainTimestamp context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(PlainTimestamp context) {

            return null;

        }

    }

}
