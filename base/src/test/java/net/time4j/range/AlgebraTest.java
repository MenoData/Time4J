package net.time4j.range;

import net.time4j.CalendarUnit;
import net.time4j.ClockUnit;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.expert.Iso8601Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class AlgebraTest {

    @Test
    public void precedesIfNoGap() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 10);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.precedes(b), is(false));
        assertThat(b.precededBy(a), is(false));

        // special check
        assertThat(b.withOpenStart().precededBy(a), is(true));
        assertThat(b.move(-1, CalendarUnit.DAYS).withOpenStart().precededBy(a), is(false));
    }

    @Test
    public void precedesIfGap() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 10);

        PlainDate startB = PlainDate.of(2014, 5, 12);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.precedes(b), is(true));
        assertThat(b.precededBy(a), is(true));
    }

    @Test
    public void meetsIfNoGap() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 10);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.meets(b), is(true));
        assertThat(b.metBy(a), is(true));
    }

    @Test
    public void meetsIfGap() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 10);

        PlainDate startB = PlainDate.of(2014, 5, 12);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.meets(b), is(false));
        assertThat(b.metBy(a), is(false));
    }

    @Test
    public void meetsOpenInterval() throws ParseException {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 1, 1);

        DateInterval a =
            DateInterval.between(startA, endA);
        DateInterval b1 =
            DateInterval.parse(
                "(2014-01-01/2014-05-31]",
                Iso8601Format.EXTENDED_CALENDAR_DATE,
                BracketPolicy.SHOW_ALWAYS);

        assertThat(a.meets(b1), is(true));
        assertThat(b1.metBy(a), is(true));

        DateInterval b2 =
            DateInterval.between(
                PlainDate.of(2014, 1, 1),
                PlainDate.of(2014, 5, 31)
            ).withOpenStart();
        assertThat(b1, is(b2));

        assertThat(a.meets(b2), is(true));
        assertThat(b2.metBy(a), is(true));
    }

    @Test
    public void precedesOpenInterval() throws ParseException {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 1, 1);

        DateInterval a =
            DateInterval.between(startA, endA);
        DateInterval b1 =
            DateInterval.parse(
                "(2014-01-01/2014-05-31]",
                Iso8601Format.EXTENDED_CALENDAR_DATE,
                BracketPolicy.SHOW_ALWAYS);
        DateInterval b2 =
            DateInterval.between(
                PlainDate.of(2014, 1, 1),
                PlainDate.of(2014, 5, 31)
            ).withOpenStart();
        assertThat(b1, is(b2));

        assertThat(a.precedes(b1), is(false));
        assertThat(b1.precededBy(a), is(false));
        assertThat(a.precedes(b2), is(false));
        assertThat(b2.precededBy(a), is(false));

        a = a.move(-1, CalendarUnit.DAYS);
        assertThat(a.precedes(b1), is(true));
        assertThat(b1.precededBy(a), is(true));
        assertThat(a.precedes(b2), is(true));
        assertThat(b2.precededBy(a), is(true));
    }

    @Test
    public void precedesPast() {
        PlainDate endA = PlainDate.axis().getMinimum();
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.until(endA);
        DateInterval b = DateInterval.until(endB);

        assertThat(a.precedes(b), is(false));
    }

    @Test
    public void meetsPast() {
        PlainDate endA = PlainDate.axis().getMinimum();
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.until(endA);
        DateInterval b = DateInterval.until(endB);

        assertThat(a.meets(b), is(false));
    }

    @Test
    public void precedesFuture() {
        PlainDate startA = PlainDate.of(2014, 5, 17);
        PlainDate startB = PlainDate.axis().getMaximum();

        DateInterval a = DateInterval.since(startA);
        DateInterval b = DateInterval.since(startB);

        assertThat(a.precedes(b), is(false));
    }

    @Test
    public void meetsFuture() {
        PlainDate startA = PlainDate.of(2014, 5, 17);
        PlainDate startB = PlainDate.axis().getMaximum();

        DateInterval a = DateInterval.since(startA);
        DateInterval b = DateInterval.since(startB);

        assertThat(a.meets(b), is(false));
    }

    @Test
    public void meetsEmpty1() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.meets(b), is(true));
        assertThat(b.metBy(a), is(true));
    }

    @Test
    public void meetsEmpty2() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.meets(b), is(false));
        assertThat(b.metBy(a), is(false));
    }

    @Test
    public void overlaps1() { // meets
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 10);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps2() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 11);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(true));
    }

    @Test
    public void overlaps3() { // meets
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 11);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA).withOpenEnd();
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps4() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 12);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(true));
    }

    @Test
    public void overlaps5() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(true));
    }

    @Test
    public void overlaps6() { // finishes
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 17);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps7() { // finishes
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB).withOpenEnd();

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps8() { // equals
        PlainDate startA = PlainDate.of(2014, 5, 11);
        PlainDate endA = PlainDate.of(2014, 5, 17);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 18);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB).withOpenEnd();

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps9() { // contains
        PlainDate startA = PlainDate.of(2014, 5, 11);
        PlainDate endA = PlainDate.of(2014, 5, 18);

        PlainDate startB = PlainDate.of(2014, 5, 12);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps10() { // during
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 5, 15);

        PlainDate startB = PlainDate.of(2014, 5, 12);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps11() { // starts
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 5, 13);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlaps12() { // overlappedBy
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 5, 10);
        PlainDate endB = PlainDate.of(2014, 5, 13);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlapsEmpty1() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 15);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlapsEmpty2() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.overlaps(b), is(false));
    }

    @Test
    public void overlappedBy() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 5, 10);
        PlainDate endB = PlainDate.of(2014, 5, 13);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.overlaps(a), is(true));
        assertThat(a.overlappedBy(b), is(true));
    }

    @Test
    public void finishedByEmpty1() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15); // ende inklusive

        PlainDate startB = PlainDate.of(2014, 6, 15); // ende exklusive

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.finishedBy(b), is(false));
        assertThat(b.finishedBy(a), is(false));
    }

    @Test
    public void finishedByEmpty2() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.finishedBy(b), is(false));
        assertThat(b.finishedBy(a), is(false));
    }

    @Test
    public void finishes1() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 17);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(true));
    }

    @Test
    public void finishes2() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 18);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA).withOpenEnd();
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(true));
    }

    @Test
    public void finishes3() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB).withOpenEnd();

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(true));
    }

    @Test
    public void finishes4() { // contains
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 18);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(false));
    }

    @Test
    public void finishes5() { // overlaps
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2014, 5, 11);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(false));
    }

    @Test
    public void finishesTSP1() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainDate.of(2014, 5, 17).atStartOfDay();

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB = PlainDate.of(2014, 5, 17).atStartOfDay();

        TimestampInterval a = TimestampInterval.between(startA, endA);
        TimestampInterval b = TimestampInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(true));
    }

    @Test
    public void finishesTSP2() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA =
            PlainDate.of(2014, 5, 17).atStartOfDay().minus(1, ClockUnit.NANOS);

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB = PlainDate.of(2014, 5, 17).atStartOfDay();

        TimestampInterval a =
            TimestampInterval.between(startA, endA).withClosedEnd();
        TimestampInterval b = TimestampInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(true));
    }

    @Test
    public void finishesTSP3() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainDate.of(2014, 5, 17).atStartOfDay();

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB =
            PlainDate.of(2014, 5, 17).atStartOfDay().minus(1, ClockUnit.NANOS);

        TimestampInterval a =
            TimestampInterval.between(startA, endA);
        TimestampInterval b =
            TimestampInterval.between(startB, endB).withClosedEnd();

        assertThat(a.finishes(b), is(false));
        assertThat(a.finishedBy(b), is(true));
    }

    @Test
    public void finishesTSPMax() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainTimestamp.axis().getMaximum();

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB = PlainTimestamp.axis().getMaximum();

        TimestampInterval a = TimestampInterval.between(startA, endA);
        TimestampInterval b = TimestampInterval.between(startB, endB);

        assertThat(a.finishes(b), is(false));
        assertThat(b.finishes(a), is(true));
        assertThat(a.finishedBy(b), is(true));
        assertThat(b.finishedBy(a), is(false));
    }

    @Test
    public void finishesTSPFuture() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();

        TimestampInterval a = TimestampInterval.since(startA);
        TimestampInterval b = TimestampInterval.since(startB);

        assertThat(a.finishes(b), is(false));
        assertThat(b.finishes(a), is(true));
        assertThat(a.finishedBy(b), is(true));
        assertThat(b.finishedBy(a), is(false));
    }

    @Test
    public void overlapsTSP1() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainDate.of(2014, 5, 12).atStartOfDay();

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB = PlainDate.of(2014, 5, 17).atStartOfDay();

        TimestampInterval a =
            TimestampInterval.between(startA, endA);
        TimestampInterval b =
            TimestampInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(true));
    }

    @Test
    public void overlapsTSP2() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainDate.of(2014, 5, 12).atStartOfDay();

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB =
            PlainDate.of(2014, 5, 17).atStartOfDay().minus(1, ClockUnit.NANOS);

        TimestampInterval a =
            TimestampInterval.between(startA, endA);
        TimestampInterval b =
            TimestampInterval.between(startB, endB).withClosedEnd();

        assertThat(a.overlaps(b), is(true));
    }

    @Test
    public void overlapsTSP3() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA =
            PlainDate.of(2014, 5, 12).atStartOfDay().minus(1, ClockUnit.NANOS);

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();
        PlainTimestamp endB = PlainDate.of(2014, 5, 17).atStartOfDay();

        TimestampInterval a =
            TimestampInterval.between(startA, endA).withClosedEnd();
        TimestampInterval b =
            TimestampInterval.between(startB, endB);

        assertThat(a.overlaps(b), is(true));
    }

    @Test
    public void overlapsTSPMaxFuture() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainTimestamp.axis().getMaximum();

        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();

        TimestampInterval a = TimestampInterval.between(startA, endA);
        TimestampInterval b = TimestampInterval.since(startB);

        assertThat(a.overlaps(b), is(true));
        assertThat(b.overlaps(a), is(false));
        assertThat(a.overlappedBy(b), is(false));
        assertThat(b.overlappedBy(a), is(true));
    }

    @Test
    public void overlapsTSPFutureFuture() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();

        TimestampInterval a = TimestampInterval.since(startA);
        TimestampInterval b = TimestampInterval.since(startB);

        assertThat(a.overlaps(b), is(false));
        assertThat(a.overlappedBy(b), is(false));
    }

    @Test
    public void startedByEmpty1() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 13);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(b.startedBy(a), is(false));
        assertThat(a.startedBy(b), is(true));
    }

    @Test
    public void startedByEmpty2() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 14);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(b.startedBy(a), is(false));
        assertThat(a.startedBy(b), is(false));
    }

    @Test
    public void starts1() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 18);

        PlainDate startB = PlainDate.of(2012, 2, 29);
        PlainDate endB = PlainDate.of(2014, 5, 19);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.starts(b), is(true));
        assertThat(a.startedBy(b), is(false));
    }

    @Test
    public void starts2() {
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 18);

        PlainDate startB = PlainDate.of(2012, 2, 29);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.starts(b), is(false));
        assertThat(a.startedBy(b), is(true));
    }

    @Test
    public void starts3() { // equivalent
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2012, 2, 29);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB).withOpenEnd();

        assertThat(a.starts(b), is(false));
        assertThat(a.startedBy(b), is(false));
    }

    @Test
    public void starts4() { // contains
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2012, 2, 28);
        PlainDate endB = PlainDate.of(2014, 5, 17);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.starts(b), is(false));
        assertThat(a.startedBy(b), is(false));
    }

    @Test
    public void starts5() { // overlaps
        PlainDate startA = PlainDate.of(2012, 2, 29);
        PlainDate endA = PlainDate.of(2014, 5, 16);

        PlainDate startB = PlainDate.of(2012, 2, 28);
        PlainDate endB = PlainDate.of(2014, 5, 14);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.starts(b), is(false));
        assertThat(a.startedBy(b), is(false));
    }

    @Test
    public void startsTSP() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endA = PlainDate.of(2014, 5, 17).atStartOfDay();

        PlainTimestamp startB = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp endB = PlainDate.of(2014, 5, 18).atStartOfDay();

        TimestampInterval a = TimestampInterval.between(startA, endA);
        TimestampInterval b = TimestampInterval.between(startB, endB);

        assertThat(a.starts(b), is(true));
        assertThat(b.starts(a), is(false));
        assertThat(a.startedBy(b), is(false));
        assertThat(b.startedBy(a), is(true));
    }

    @Test
    public void startsTSPMin() {
        PlainTimestamp startA = PlainTimestamp.axis().getMinimum();
        PlainTimestamp endA = PlainDate.of(2012, 2, 29).atStartOfDay();

        PlainTimestamp startB = PlainTimestamp.axis().getMinimum();
        PlainTimestamp endB = PlainDate.of(2014, 5, 11).atStartOfDay();

        TimestampInterval a = TimestampInterval.between(startA, endA);
        TimestampInterval b = TimestampInterval.between(startB, endB);

        assertThat(a.starts(b), is(true));
        assertThat(b.starts(a), is(false));
        assertThat(a.startedBy(b), is(false));
        assertThat(b.startedBy(a), is(true));
    }

    @Test
    public void startsTSPPast() {
        PlainTimestamp startA = PlainDate.of(2012, 2, 29).atStartOfDay();
        PlainTimestamp startB = PlainDate.of(2014, 5, 11).atStartOfDay();

        TimestampInterval a = TimestampInterval.until(startA);
        TimestampInterval b = TimestampInterval.until(startB);

        assertThat(a.starts(b), is(true));
        assertThat(b.starts(a), is(false));
        assertThat(a.startedBy(b), is(false));
        assertThat(b.startedBy(a), is(true));
    }

    @Test
    public void enclosesEmpty1() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 14);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(b.encloses(a), is(false));
        assertThat(a.encloses(b), is(true));
    }

    @Test
    public void enclosesEmpty2() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 15);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(b.encloses(a), is(false));
        assertThat(a.encloses(b), is(true));
    }

    @Test
    public void enclosesGreater() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 10);
        PlainDate endB = PlainDate.of(2014, 6, 19);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.encloses(a), is(true));
        assertThat(a.encloses(b), is(false));
        assertThat(a.enclosedBy(b), is(true));
        assertThat(b.enclosedBy(a), is(false));
    }

    @Test
    public void enclosesSmaller() {
        PlainDate startA = PlainDate.of(2014, 6, 1);
        PlainDate endA = PlainDate.of(2014, 6, 30);

        PlainDate startB = PlainDate.of(2014, 6, 10);
        PlainDate endB = PlainDate.of(2014, 6, 19);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.encloses(a), is(false));
        assertThat(a.encloses(b), is(true));
        assertThat(a.enclosedBy(b), is(false));
        assertThat(b.enclosedBy(a), is(true));
    }

    @Test
    public void enclosesSmallerWithSameStart() {
        PlainDate startA = PlainDate.of(2014, 6, 1);
        PlainDate endA = PlainDate.of(2014, 6, 30);

        PlainDate startB = PlainDate.of(2014, 6, 1);
        PlainDate endB = PlainDate.of(2014, 6, 19);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.encloses(a), is(false));
        assertThat(a.encloses(b), is(false));
        assertThat(a.enclosedBy(b), is(false));
        assertThat(b.enclosedBy(a), is(false));
    }

    @Test
    public void enclosesSmallerWithSameEnd() {
        PlainDate startA = PlainDate.of(2014, 6, 1);
        PlainDate endA = PlainDate.of(2014, 6, 30);

        PlainDate startB = PlainDate.of(2014, 6, 10);
        PlainDate endB = PlainDate.of(2014, 6, 30);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.encloses(a), is(false));
        assertThat(a.encloses(b), is(false));
        assertThat(a.enclosedBy(b), is(false));
        assertThat(b.enclosedBy(a), is(false));
    }

    @Test
    public void containsSmaller() {
        PlainDate startA = PlainDate.of(2014, 6, 1);
        PlainDate endA = PlainDate.of(2014, 6, 30);

        PlainDate startB = PlainDate.of(2014, 6, 10);
        PlainDate endB = PlainDate.of(2014, 6, 19);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.contains(a), is(false));
        assertThat(a.contains(b), is(true));
    }

    @Test
    public void containsSmallerWithSameStart() {
        PlainDate startA = PlainDate.of(2014, 6, 1);
        PlainDate endA = PlainDate.of(2014, 6, 30);

        PlainDate startB = PlainDate.of(2014, 6, 1);
        PlainDate endB = PlainDate.of(2014, 6, 19);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.contains(a), is(false));
        assertThat(a.contains(b), is(true));
    }

    @Test
    public void containsSmallerWithSameEnd() {
        PlainDate startA = PlainDate.of(2014, 6, 1);
        PlainDate endA = PlainDate.of(2014, 6, 30);

        PlainDate startB = PlainDate.of(2014, 6, 10);
        PlainDate endB = PlainDate.of(2014, 6, 30);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(b.contains(a), is(false));
        assertThat(a.contains(b), is(true));
    }

    @Test
    public void containsEmpty1() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 15);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.contains(b), is(true));
        assertThat(b.contains(a), is(false));
    }

    @Test
    public void containsEmpty2() {
        PlainDate startA = PlainDate.of(2014, 5, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.contains(b), is(false));
        assertThat(b.contains(a), is(false));
    }

    @Test
    public void equivalentTo1() {
        PlainDate startA = PlainDate.of(2014, 6, 16);
        PlainDate endA = PlainDate.of(2014, 6, 20);

        PlainDate startB = PlainDate.of(2014, 6, 16);
        PlainDate endB = PlainDate.of(2014, 6, 20);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.equivalentTo(b), is(true));
        assertThat(b.equivalentTo(a), is(true));
    }

    @Test
    public void equivalentTo2() {
        PlainDate startA = PlainDate.of(2014, 6, 16);
        PlainDate endA = PlainDate.of(2014, 6, 21);

        PlainDate startB = PlainDate.of(2014, 6, 16);
        PlainDate endB = PlainDate.of(2014, 6, 20);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.equivalentTo(b), is(false));
        assertThat(b.equivalentTo(a), is(false));
    }

    @Test
    public void equivalentTo3() {
        PlainDate startA = PlainDate.of(2014, 6, 16);
        PlainDate endA = PlainDate.of(2014, 6, 21);

        PlainDate startB = PlainDate.of(2014, 6, 16);
        PlainDate endB = PlainDate.of(2014, 6, 20);

        DateInterval a = DateInterval.between(startA, endA).withOpenEnd();
        DateInterval b = DateInterval.between(startB, endB);

        assertThat(a.equivalentTo(b), is(true));
        assertThat(b.equivalentTo(a), is(true));
    }

    @Test
    public void equivalentTo4() {
        PlainDate startA = PlainDate.of(2014, 6, 16);
        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.since(startA);
        DateInterval b = DateInterval.since(startB);

        assertThat(a.equivalentTo(b), is(true));
        assertThat(b.equivalentTo(a), is(true));
    }

    @Test
    public void emptyEquivalentToEmpty() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate startB = PlainDate.of(2014, 6, 13);

        DateInterval a = DateInterval.since(startA).collapse();
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.equivalentTo(b), is(true));
        assertThat(a.meets(b), is(false));
        assertThat(a.metBy(b), is(false));
    }

    @Test
    public void emptyTest() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.contains(b), is(false));

        assertThat(a.meets(b), is(true));
        assertThat(a.precedes(b), is(false));
        assertThat(a.overlaps(b), is(false));
        assertThat(a.starts(b), is(false));
        assertThat(a.finishes(b), is(false));
        assertThat(a.encloses(b), is(false));
        assertThat(a.metBy(b), is(false));
        assertThat(a.precededBy(b), is(false));
        assertThat(a.overlappedBy(b), is(false));
        assertThat(a.finishedBy(b), is(false));
        assertThat(a.startedBy(b), is(false));
        assertThat(a.enclosedBy(b), is(false));
        assertThat(a.equivalentTo(b), is(false));
    }

    @Test
    public void intersectsIfEmpty() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 14);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB).collapse();

        assertThat(a.intersects(b), is(false));
        assertThat(b.intersects(a), is(false));
        assertThat(a.findIntersection(b).isPresent(), is(false));
        assertThat(b.findIntersection(a).isPresent(), is(false));
    }

    @Test
    public void intersectsIfSameStart() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 13);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB);

        assertThat(a.intersects(b), is(true));
        assertThat(b.intersects(a), is(true));
        assertThat(a.findIntersection(b).get(), is(a));
        assertThat(b.findIntersection(a).get(), is(a));
    }

    @Test
    public void intersectsIfOverlap() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 14);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB);
        DateInterval c = DateInterval.between(startB, endA);

        assertThat(a.intersects(b), is(true));
        assertThat(b.intersects(a), is(true));
        assertThat(a.findIntersection(b).get(), is(c));
        assertThat(b.findIntersection(a).get(), is(c));
    }

    @Test
    public void intersectsIfSameEnd() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 15);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB);
        DateInterval c = DateInterval.atomic(startB);

        assertThat(a.intersects(b), is(true));
        assertThat(b.intersects(a), is(true));
        assertThat(a.findIntersection(b).get(), is(c));
        assertThat(b.findIntersection(a).get(), is(c));
    }

    @Test
    public void intersectsIfBefore() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB);

        assertThat(a.intersects(b), is(false));
        assertThat(b.intersects(a), is(false));
        assertThat(a.findIntersection(b).isPresent(), is(false));
        assertThat(b.findIntersection(a).isPresent(), is(false));
    }

    @Test
    public void intersectsWhenOpenAndEmpty() {
        PlainTime startA = PlainTime.of(6, 5);
        PlainTime endA = PlainTime.of(12, 15);

        PlainTime startB = PlainTime.of(12, 15);
        PlainTime endB = PlainTime.of(17, 30);

        ClockInterval a = ClockInterval.between(startA, endA);
        ClockInterval b = ClockInterval.between(startB, endB);

        assertThat(a.intersects(b), is(false));
        assertThat(b.intersects(a), is(false));
        assertThat(a.findIntersection(b).isPresent(), is(false));
        assertThat(b.findIntersection(a).isPresent(), is(false));
    }

    @Test
    public void intersectsWhenOpenAndNotEmpty() {
        PlainTime startA = PlainTime.of(6, 5);
        PlainTime endA = PlainTime.of(12, 15).plus(1, ClockUnit.NANOS);

        PlainTime startB = PlainTime.of(12, 15);
        PlainTime endB = PlainTime.of(17, 30);

        ClockInterval a = ClockInterval.between(startA, endA);
        ClockInterval b = ClockInterval.between(startB, endB);
        ClockInterval c = ClockInterval.between(startB, endA);

        assertThat(a.intersects(b), is(true));
        assertThat(b.intersects(a), is(true));
        assertThat(a.findIntersection(b).get(), is(c));
        assertThat(b.findIntersection(a).get(), is(c));
    }

    @Test
    public void abuts() {
        PlainDate startA = PlainDate.of(2014, 6, 13);
        PlainDate endA = PlainDate.of(2014, 6, 15);

        PlainDate startB = PlainDate.of(2014, 6, 16);

        DateInterval a = DateInterval.between(startA, endA);
        DateInterval b = DateInterval.since(startB);

        b = b.move(1, CalendarUnit.DAYS);
        assertThat(a.abuts(b), is(false));
        assertThat(b.abuts(a), is(false));

        b = b.move(-2, CalendarUnit.DAYS);
        assertThat(a.abuts(b), is(false));
        assertThat(b.abuts(a), is(false));

        b = b.move(1, CalendarUnit.DAYS);
        assertThat(a.abuts(b), is(true));
        assertThat(b.abuts(a), is(true));

        b = b.collapse();
        assertThat(a.abuts(b), is(false));
        assertThat(b.abuts(a), is(false));

    }

}