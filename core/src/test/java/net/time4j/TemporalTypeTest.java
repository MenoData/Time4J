package net.time4j;

import net.time4j.scale.TimeScale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TemporalTypeTest {

    static {
        System.setProperty("net.time4j.sql.utc.conversion", "true");
    }

    @Test
    public void roundTripSQLDate() {
        assertThat(
            TemporalType.SQL_DATE.toTime4J(new java.sql.Date(86400 * 1000L)),
            is(PlainDate.of(1970, 1, 2)));
        assertThat(
            TemporalType.SQL_DATE.fromTime4J(PlainDate.of(1970, 1, 2)),
            is(new java.sql.Date(86400 * 1000L)));
    }

    @Test
    public void roundTripSQLTime() {
        assertThat(
            TemporalType.SQL_TIME.toTime4J(
                new java.sql.Time(86400 * 1000L - 1)),
            is(PlainTime.of(23, 59, 59, 999000000)));
        assertThat(
            TemporalType.SQL_TIME.fromTime4J(
                PlainTime.of(23, 59, 59, 999000000)),
            is(new java.sql.Time(86400 * 1000L - 1)));
    }

    @Test
    public void roundTripSQLTimestamp() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            TemporalType.SQL_TIMESTAMP.toTime4J(ts),
            is(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)));
        assertThat(
            TemporalType.SQL_TIMESTAMP.fromTime4J(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)),
            is(ts));
    }

    @Test
    public void roundTripJavaUtilDate() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.toTime4J(jud),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
        assertThat(
            TemporalType.JAVA_UTIL_DATE.fromTime4J(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud));
    }

}
