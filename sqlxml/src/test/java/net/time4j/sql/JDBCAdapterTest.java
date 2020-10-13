package net.time4j.sql;

import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.scale.TimeScale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JDBCAdapterTest {

    static {
        System.setProperty("net.time4j.sql.utc.conversion", "true");
    }

    @Test
    public void sqlDateToTime4J() {
        assertThat(
            JDBCAdapter.SQL_DATE.translate(new java.sql.Date(86400 * 1000L)),
            is(PlainDate.of(1970, 1, 2)));
    }

    @Test
    public void sqlDateFromTime4J() {
        assertThat(
            JDBCAdapter.SQL_DATE.from(PlainDate.of(1970, 1, 2)),
            is(new java.sql.Date(86400 * 1000L)));
    }

    @Test
    public void sqlTimeToTime4J() {
        assertThat(
            JDBCAdapter.SQL_TIME.translate(
                new java.sql.Time(86400 * 1000L - 1)),
            is(PlainTime.of(23, 59, 59, 999000000)));
    }

    @Test
    public void sqlTimeFromTime4J() {
        assertThat(
            JDBCAdapter.SQL_TIME.from(
                PlainTime.of(23, 59, 59, 999000000)),
            is(new java.sql.Time(86400 * 1000L - 1)));
    }

    @Test
    public void sqlTimestampToTime4J() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            JDBCAdapter.SQL_TIMESTAMP.translate(ts),
            is(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)));
    }

    @Test
    public void sqlTimestampFromTime4J() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            JDBCAdapter.SQL_TIMESTAMP.from(
                PlainTimestamp.of(2012, 7, 1, 0, 0, 0)
                .plus(210, ClockUnit.NANOS)),
            is(ts));
    }

    @Test
    public void sqlTimestampWithZoneToTime4J() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            JDBCAdapter.SQL_TIMESTAMP_WITH_ZONE.translate(ts),
            is(Moment.of(1341100800L, 210, TimeScale.POSIX)));
    }

    @Test
    public void sqlTimestampWithZoneFromTime4J() {
        java.sql.Timestamp ts =
            new java.sql.Timestamp(1341100800L * 1000);
        ts.setNanos(210);
        assertThat(
            JDBCAdapter.SQL_TIMESTAMP_WITH_ZONE.from(Moment.of(1341100800L, 210, TimeScale.POSIX)),
            is(ts));
    }

}
