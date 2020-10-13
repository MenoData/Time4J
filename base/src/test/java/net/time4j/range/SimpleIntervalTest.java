package net.time4j.range;

import net.time4j.Moment;
import net.time4j.calendar.PersianCalendar;
import net.time4j.calendar.PersianMonth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.ChronoParser;
import net.time4j.format.expert.ChronoPrinter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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

    @Test
    public void onInstantTimeLine() {
        SimpleInterval<Instant> expected =
            SimpleInterval.between(
                Instant.EPOCH,
                Instant.parse("2016-11-01T24:00:00Z")
            );
        assertThat(
            SimpleInterval.onInstantTimeLine().between(
                Instant.EPOCH,
                Instant.parse("2016-11-01T24:00:00Z")
            ),
            is(expected));
    }

    @Test(expected=NullPointerException.class)
    public void nullTimeLine() {
        SimpleInterval.on(null);
    }

    @Test
    public void getStart() {
        SimpleInterval<Instant> i1 =
            SimpleInterval.since(Instant.EPOCH);
        assertThat(i1.getStart(), is(Boundary.ofClosed(Instant.EPOCH)));
        SimpleInterval<Instant> i2 =
            SimpleInterval.until(Instant.parse("2016-11-01T24:00:00Z"));
        assertThat(i2.getStart(), is(Boundary.infinitePast()));
    }

    @Test
    public void getEnd() {
        SimpleInterval<Instant> i1 =
            SimpleInterval.since(Instant.parse("2016-11-01T24:00:00Z"));
        assertThat(i1.getEnd(), is(Boundary.infiniteFuture()));
        SimpleInterval<Instant> i2 =
            SimpleInterval.until(Instant.EPOCH);
        assertThat(i2.getEnd(), is(Boundary.ofOpen(Instant.EPOCH)));
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
        Instant meetingPoint = Instant.parse("2016-11-01T24:00:00Z");
        SimpleInterval<Instant> i1 =
            SimpleInterval.between(Instant.EPOCH, meetingPoint);
        SimpleInterval<Instant> i2 =
            SimpleInterval.since(meetingPoint);
        assertThat(i1.abuts(i2), is(true));
        assertThat(i2.abuts(i1), is(true));
        assertThat(i1.abuts(SimpleInterval.between(meetingPoint, meetingPoint)), is(false)); // empty case
    }

    @Test
    public void intersects(){
        Instant meetingPoint = Instant.parse("2016-11-01T24:00:00Z");
        SimpleInterval<Instant> i1 = SimpleInterval.between(Instant.EPOCH, meetingPoint);
        SimpleInterval<Instant> i2 = SimpleInterval.since(meetingPoint);
        assertThat(i1.intersects(i2), is(false));
        assertThat(i2.intersects(i1), is(false));

        meetingPoint = meetingPoint.minusNanos(1);
        SimpleInterval<Instant> i3 = SimpleInterval.since(meetingPoint);
        assertThat(i1.intersects(i3), is(true));
        assertThat(i3.intersects(i1), is(true));
    }

    @Test
    public void findIntersection(){
        Instant meetingPoint = Instant.parse("2016-11-01T24:00:00Z");
        SimpleInterval<Instant> i1 = SimpleInterval.between(Instant.EPOCH, meetingPoint);
        SimpleInterval<Instant> i2 = SimpleInterval.since(meetingPoint);
        assertThat(i1.findIntersection(i2), is(Optional.empty()));
        assertThat(i2.findIntersection(i1), is(Optional.empty()));

        meetingPoint = meetingPoint.minusNanos(1);
        SimpleInterval<Instant> i3 = SimpleInterval.since(meetingPoint);
        SimpleInterval<Instant> expected = SimpleInterval.between(meetingPoint, meetingPoint.plusNanos(1));
        assertThat(i1.findIntersection(i3).get(), is(expected));
        assertThat(i3.findIntersection(i1).get(), is(expected));
    }

    @Test
    public void print() {
        Instant meetingPoint = Instant.parse("2016-11-01T24:00:00Z");
        SimpleInterval<Instant> i = SimpleInterval.between(Instant.EPOCH, meetingPoint);
        ChronoPrinter<Instant> printer =
            ChronoFormatter.ofPattern(
                "uuuu-MM-dd HH:mm:ssXXX",
                PatternType.CLDR,
                Locale.ROOT,
                Moment.threeten()
            ).withTimezone(ZonalOffset.UTC);
        assertThat(i.print(printer, "from {0} to {1}"), is("from 1970-01-01 00:00:00Z to 2016-11-02 00:00:00Z"));
        System.out.println(i.print(printer)); // 1970-01-01 00:00:00Z – 2016-11-02 00:00:00Z
    }

    @Test
    public void parse() throws ParseException {
        Instant meetingPoint = Instant.parse("2016-11-01T24:00:00Z");
        SimpleInterval<Instant> i = SimpleInterval.between(Instant.EPOCH, meetingPoint);
        ChronoParser<Instant> parser =
            ChronoFormatter.ofPattern(
                "uuuu-MM-dd HH:mm:ssXXX",
                PatternType.CLDR,
                Locale.ROOT,
                Moment.threeten()
            ).withTimezone(ZonalOffset.UTC);
        assertThat(
            SimpleInterval.onInstantTimeLine().parse(
                "from 1970-01-01 00:00:00Z to 2016-11-02 00:00:00Z",
                parser,
                "from {0} to {1}"),
            is(i));
    }

    @Test
    public void parseCalendrical() throws ParseException {
        PersianCalendar start = PersianCalendar.of(1392, PersianMonth.ESFAND, 27);
        PersianCalendar end = PersianCalendar.of(1393, PersianMonth.FARVARDIN, 6);

        ChronoFormatter<PersianCalendar> cf =
            ChronoFormatter.ofPattern("dd.MM.yyyy", PatternType.CLDR, Locale.ROOT, PersianCalendar.axis());
        SimpleInterval<PersianCalendar> interval =
            SimpleInterval.on(PersianCalendar.axis()).parse("27.12.1392 – 06.01.1393", cf);
        SimpleInterval<PersianCalendar> expected =
            SimpleInterval.on(PersianCalendar.axis()).between(start, end);

        assertThat(interval, is(expected));
        assertThat(interval.getStart().isClosed(), is(true));
        assertThat(interval.getEnd().isClosed(), is(true));
    }

}
