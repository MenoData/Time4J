package net.time4j;

import net.time4j.engine.ChronoEntity;
import net.time4j.scale.TimeScale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TemporalTypesTest {

    static {
        System.setProperty("net.time4j.sql.utc.conversion", "true");
    }

    @Test
    public void name() {
        assertThat(
            TemporalTypes.JAVA_UTIL_DATE.name(),
            is("JAVA_UTIL_DATE"));
        assertThat(
            TemporalTypes.MILLIS_SINCE_UNIX.name(),
            is("MILLIS_SINCE_UNIX"));
        assertThat(TemporalTypes.SQL_DATE.name(), is("SQL_DATE"));
        assertThat(TemporalTypes.SQL_TIME.name(), is("SQL_TIME"));
        assertThat(TemporalTypes.SQL_TIMESTAMP.name(), is("SQL_TIMESTAMP"));
    }

    @Test
    public void compare() {
        ChronoEntity<?> o1 = PlainTime.MIN; // T00
        ChronoEntity<?> o2 = PlainTime.MAX; // T24
        assertThat(TemporalTypes.SQL_TIME.compare(o2, o1), is(1));
        assertThat(TemporalTypes.SQL_TIME.compare(o1, o1), is(0));
        assertThat(TemporalTypes.SQL_TIME.compare(o1, o2), is(-1));
    }

    @Test
    public void defaultMin() {
        assertThat(
            TemporalTypes.SQL_DATE.getDefaultMinimum(),
            is(PlainDate.of(1900, 1, 1).get(TemporalTypes.SQL_DATE)));
        assertThat(
            TemporalTypes.SQL_TIME.getDefaultMinimum(),
            is(PlainTime.MIN.get(TemporalTypes.SQL_TIME)));
    }

    @Test
    public void defaultMax() {
        assertThat(
            TemporalTypes.SQL_DATE.getDefaultMaximum().getTime(),
            is(
                PlainDate.of(9999, 12, 31)
                    .get(TemporalTypes.SQL_DATE).getTime()
                + 86399999));
        assertThat(
            TemporalTypes.SQL_TIME.getDefaultMaximum().getTime(),
            is(
                PlainTime.of(23, 59, 59, 999999999)
                    .get(TemporalTypes.SQL_TIME).getTime()));
    }

    @Test
    public void transformSQLDate() {
        assertThat(
            TemporalTypes.SQL_DATE.transform(new java.sql.Date(86400 * 1000L)),
            is(PlainDate.of(1970, 1, 2)));
    }

    @Test
    public void transformSQLTime() {
        assertThat(
            TemporalTypes.SQL_TIME.transform(
                new java.sql.Time(86400 * 1000L - 1)),
            is(PlainTime.of(23, 59, 59, 999000000)));
    }

    @Test
    public void transformSQLTimestamp() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            TemporalTypes.SQL_TIMESTAMP.transform(ts),
            is(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)));
    }

    @Test
    public void transformJavaUtilDate() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalTypes.JAVA_UTIL_DATE.transform(jud),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

}