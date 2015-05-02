package net.time4j;

import net.time4j.engine.ChronoException;
import net.time4j.scale.LeapSeconds;
import net.time4j.scale.TimeScale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class TemporalTypeTest {

    @Test
    public void javaUtilDateToTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.translate(jud),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

    @Test
    public void javaUtilDateFromTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.JAVA_UTIL_DATE.from(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud));
    }

    @Test
    public void millisSinceUnixToTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.MILLIS_SINCE_UNIX.translate(jud.getTime()),
            is(Moment.of(1341100800L, TimeScale.POSIX)));
    }

    @Test
    public void millisSinceUnixFromTime4J() {
        java.util.Date jud = new java.util.Date(1341100800L * 1000);
        assertThat(
            TemporalType.MILLIS_SINCE_UNIX.from(
                Moment.of(1341100800L, TimeScale.POSIX)),
            is(jud.getTime()));
    }

    @Test
    public void localDateToTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE.translate(LocalDate.of(2015, java.time.Month.APRIL, 30)),
            is(PlainDate.of(2015, 4, 30))
        );
        assertThat(
            TemporalType.LOCAL_DATE.translate(LocalDate.MIN),
            is(PlainDate.axis().getMinimum())
        );
        assertThat(
            TemporalType.LOCAL_DATE.translate(LocalDate.MAX),
            is(PlainDate.axis().getMaximum())
        );
    }

    @Test
    public void localDateFromTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE.from(PlainDate.of(2015, 4, 30)),
            is(LocalDate.of(2015, java.time.Month.APRIL, 30))
        );
        assertThat(
            TemporalType.LOCAL_DATE.from(PlainDate.axis().getMinimum()),
            is(LocalDate.MIN)
        );
        assertThat(
            TemporalType.LOCAL_DATE.from(PlainDate.axis().getMaximum()),
            is(LocalDate.MAX)
        );
    }

    @Test
    public void localTimeToTime4J() {
        assertThat(
            TemporalType.LOCAL_TIME.translate(LocalTime.of(17, 45, 11, 4)),
            is(PlainTime.of(17, 45, 11, 4))
        );
        assertThat(
            TemporalType.LOCAL_TIME.translate(LocalTime.MIN),
            is(PlainTime.axis().getMinimum())
        );
        assertThat(
            TemporalType.LOCAL_TIME.translate(LocalTime.MAX),
            is(PlainTime.of(23, 59, 59, 999_999_999))
        );
    }

    @Test
    public void localTimeFromTime4J() {
        assertThat(
            TemporalType.LOCAL_TIME.from(PlainTime.of(17, 45, 11, 4)),
            is(LocalTime.of(17, 45, 11, 4))
        );
        assertThat(
            TemporalType.LOCAL_TIME.from(PlainTime.axis().getMinimum()),
            is(LocalTime.MIN)
        );
    }

    @Test(expected=ChronoException.class)
    public void localTimeFromTime4JOverflow() {
        TemporalType.LOCAL_TIME.from(PlainTime.axis().getMaximum());
    }

    @Test
    public void localDateTimeToTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE_TIME.translate(
                LocalDateTime.of(2015, java.time.Month.APRIL, 30, 17, 45)),
            is(PlainTimestamp.of(2015, 4, 30, 17, 45))
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.translate(LocalDateTime.MIN),
            is(PlainTimestamp.axis().getMinimum())
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.translate(LocalDateTime.MAX),
            is(PlainTimestamp.axis().getMaximum())
        );
    }

    @Test
    public void localDateTimeFromTime4J() {
        assertThat(
            TemporalType.LOCAL_DATE_TIME.from(PlainTimestamp.of(2015, 4, 30, 17, 45)),
            is(LocalDateTime.of(2015, java.time.Month.APRIL, 30, 17, 45))
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.from(PlainTimestamp.axis().getMinimum()),
            is(LocalDateTime.MIN)
        );
        assertThat(
            TemporalType.LOCAL_DATE_TIME.from(PlainTimestamp.axis().getMaximum()),
            is(LocalDateTime.MAX)
        );
    }

    @Test
    public void instantToTime4J() {
        Moment expected = Moment.of(86401, 123_456_789, TimeScale.POSIX);
        assertThat(
            TemporalType.INSTANT.translate(Instant.ofEpochSecond(86401, 123_456_789)),
            is(expected)
        );
    }

    @Test
    public void instantFromTime4J() {
        assertThat(
            TemporalType.INSTANT.from(Moment.of(86401, 123_456_789, TimeScale.POSIX)),
            is(Instant.ofEpochSecond(86401, 123_456_789))
        );
    }

    @Test(expected=ChronoException.class)
    public void instantFromTime4JLS() {
        if (LeapSeconds.getInstance().isEnabled()) {
            Moment ls = PlainDate.of(2012, 7, 1).atStartOfDay().atUTC().minus(1, SI.SECONDS);
            TemporalType.INSTANT.from(ls);
        } else {
            throw new ChronoException("Cannot test disabled leap second.");
        }
    }

}
