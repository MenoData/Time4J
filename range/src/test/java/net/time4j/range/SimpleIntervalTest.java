package net.time4j.range;

import net.time4j.Moment;
import net.time4j.TemporalType;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class SimpleIntervalTest {

    @Test
    public void onTraditionalTimeLine() {
        SimpleInterval<Date> expected =
            SimpleInterval.between(
                new Date(2L),
                new Date(27L)
            );
        assertThat(
            SimpleInterval.onTraditionalTimeLine().between(
                new Date(2L),
                new Date(27L)
            ),
            is(expected));
    }

    @Test(expected=NullPointerException.class)
    public void nullTimeLine() {
        SimpleInterval.onTimeLine(null);
    }

    @Test
    public void getStart() {
        SimpleInterval<Date> i1 =
            SimpleInterval.since(new Date(0L));
        assertThat(i1.getStart(), is(Boundary.ofClosed(new Date(0L))));
        SimpleInterval<Date> i2 =
            SimpleInterval.until(new Date(3600L));
        assertThat(i2.getStart(), is(Boundary.<Date>infinitePast()));
    }

    @Test
    public void getEnd() {
        SimpleInterval<Date> i1 =
            SimpleInterval.since(new Date(3600L));
        assertThat(i1.getEnd(), is(Boundary.<Date>infiniteFuture()));
        SimpleInterval<Date> i2 =
            SimpleInterval.until(new Date(0L));
        assertThat(i2.getEnd(), is(Boundary.ofOpen(new Date(0L))));
    }

    @Test
    public void isEmpty() {
        SimpleInterval<Date> i1 =
            SimpleInterval.since(new Date(0L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.isEmpty(), is(false));
        assertThat(i2.isEmpty(), is(true));
    }

    @Test
    public void isFinite() {
        SimpleInterval<Date> i1 =
            SimpleInterval.until(new Date(0L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.isFinite(), is(false));
        assertThat(i2.isFinite(), is(true));
    }

    @Test
    public void containsPoint() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), new Date(5L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.contains(new Date(-1L)), is(false));
        assertThat(i1.contains(new Date(0)), is(true));
        assertThat(i1.contains(new Date(4)), is(true));
        assertThat(i1.contains(new Date(5)), is(false));
        assertThat(i2.contains(new Date(0L)), is(false));
    }

    @Test
    public void containsInterval() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), new Date(5L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(2L));
        SimpleInterval<Date> i3 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.contains(i2), is(true));
        assertThat(i2.contains(i1), is(false));
        assertThat(i1.contains(i1), is(true));
        assertThat(i2.contains(i2), is(true));
        assertThat(i3.contains(i3), is(false));
        assertThat(i1.contains(i3), is(true));
        assertThat(i3.contains(i1), is(false));
        assertThat(i2.contains(i3), is(true));
        assertThat(i3.contains(i2), is(false));
    }

    @Test
    public void isAfterPoint() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), new Date(5L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.isAfter(new Date(-1L)), is(true));
        assertThat(i1.isAfter(new Date(0)), is(false));
        assertThat(i2.isAfter(new Date(-1L)), is(true));
        assertThat(i2.isAfter(new Date(0)), is(false));
    }

    @Test
    public void isAfterInterval() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), new Date(5L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.isAfter(i2), is(true));
        assertThat(i2.isAfter(i1), is(false));
    }

    @Test
    public void isBeforePoint() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), new Date(5L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.isBefore(new Date(5L)), is(true));
        assertThat(i1.isBefore(new Date(4)), is(false));
        assertThat(i2.isBefore(new Date(0L)), is(true));
        assertThat(i2.isBefore(new Date(-1L)), is(false));
    }

    @Test
    public void isBeforeInterval() {
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), new Date(5L));
        SimpleInterval<Date> i2 =
            SimpleInterval.between(new Date(0L), new Date(0L));
        assertThat(i1.isBefore(i2), is(false));
        assertThat(i2.isBefore(i1), is(true));
    }

    @Test
    public void abuts() {
        Date meetingPoint = new Date(7L);
        SimpleInterval<Date> i1 =
            SimpleInterval.between(new Date(0L), meetingPoint);
        SimpleInterval<Date> i2 =
            SimpleInterval.since(meetingPoint);
        assertThat(i1.abuts(i2), is(true));
        assertThat(i2.abuts(i1), is(true));
        assertThat(i1.abuts(SimpleInterval.between(meetingPoint, meetingPoint)), is(false)); // empty case
    }

    @Test
    public void intersects(){
        Date meetingPoint = new Date(7L);
        SimpleInterval<Date> i1 = SimpleInterval.between(new Date(0L), meetingPoint);
        SimpleInterval<Date> i2 = SimpleInterval.since(meetingPoint);
        assertThat(i1.intersects(i2), is(false));
        assertThat(i2.intersects(i1), is(false));

        meetingPoint = new Date(6L);
        SimpleInterval<Date> i3 = SimpleInterval.since(meetingPoint);
        assertThat(i1.intersects(i3), is(true));
        assertThat(i3.intersects(i1), is(true));
    }

    @Test
    public void findIntersection(){
        Date meetingPoint = new Date(7L);
        SimpleInterval<Date> i1 = SimpleInterval.between(new Date(0L), meetingPoint);
        SimpleInterval<Date> i2 = SimpleInterval.since(meetingPoint);
        assertThat(i1.findIntersection(i2), nullValue());
        assertThat(i2.findIntersection(i1), nullValue());

        meetingPoint = new Date(6L);
        SimpleInterval<Date> i3 = SimpleInterval.since(meetingPoint);
        SimpleInterval<Date> expected = SimpleInterval.between(meetingPoint, new Date(7L));
        assertThat(i1.findIntersection(i3), is(expected));
        assertThat(i3.findIntersection(i1), is(expected));
    }

    @Test
    public void print() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date meetingPoint = sdf.parse("2016-11-02T15:00Z");
        SimpleInterval<Date> i = SimpleInterval.between(new Date(0L), meetingPoint);
        ChronoFormatter<Date> printer =
            ChronoFormatter.ofPattern(
                "uuuu-MM-dd HH:mm:ssXXX",
                PatternType.CLDR,
                Locale.ROOT,
                Moment.axis(TemporalType.JAVA_UTIL_DATE)
            ).withTimezone(ZonalOffset.UTC);
        assertThat(i.print(printer, "from {0} to {1}"), is("from 1970-01-01 00:00:00Z to 2016-11-02 15:00:00Z"));
    }

    @Test
    public void parse() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date meetingPoint = sdf.parse("2016-11-02T15:00Z");
        SimpleInterval<Date> i = SimpleInterval.between(new Date(0L), meetingPoint);
        ChronoFormatter<Date> parser =
            ChronoFormatter.ofPattern(
                "uuuu-MM-dd HH:mm:ssXXX",
                PatternType.CLDR,
                Locale.ROOT,
                Moment.axis(TemporalType.JAVA_UTIL_DATE)
            ).withTimezone(ZonalOffset.UTC);
        assertThat(
            SimpleInterval.onTraditionalTimeLine().parse(
                "from 1970-01-01 00:00:00Z to 2016-11-02 15:00:00Z",
                parser,
                "from {0} to {1}"),
            is(i));
    }

}
