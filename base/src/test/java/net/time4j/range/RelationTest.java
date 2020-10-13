package net.time4j.range;

import net.time4j.PlainDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.time4j.CalendarUnit.DAYS;
import static net.time4j.CalendarUnit.MONTHS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class RelationTest {

    @Test
    public void inversePRECEDES() {
        assertThat(
            IntervalRelation.PRECEDES.inverse(),
            is(IntervalRelation.PRECEDED_BY));
    }

    @Test
    public void inversePRECEDED_BY() {
        assertThat(
            IntervalRelation.PRECEDED_BY.inverse(),
            is(IntervalRelation.PRECEDES));
    }

    @Test
    public void inverseMEETS() {
        assertThat(
            IntervalRelation.MEETS.inverse(),
            is(IntervalRelation.MET_BY));
    }

    @Test
    public void inverseMET_BY() {
        assertThat(
            IntervalRelation.MET_BY.inverse(),
            is(IntervalRelation.MEETS));
    }

    @Test
    public void inverseOVERLAPS() {
        assertThat(
            IntervalRelation.OVERLAPS.inverse(),
            is(IntervalRelation.OVERLAPPED_BY));
    }

    @Test
    public void inverseOVERLAPPED_BY() {
        assertThat(
            IntervalRelation.OVERLAPPED_BY.inverse(),
            is(IntervalRelation.OVERLAPS));
    }

    @Test
    public void inverseFINISHES() {
        assertThat(
            IntervalRelation.FINISHES.inverse(),
            is(IntervalRelation.FINISHED_BY));
    }

    @Test
    public void inverseFINISHED_BY() {
        assertThat(
            IntervalRelation.FINISHED_BY.inverse(),
            is(IntervalRelation.FINISHES));
    }

    @Test
    public void inverseSTARTS() {
        assertThat(
            IntervalRelation.STARTS.inverse(),
            is(IntervalRelation.STARTED_BY));
    }

    @Test
    public void inverseSTARTED_BY() {
        assertThat(
            IntervalRelation.STARTED_BY.inverse(),
            is(IntervalRelation.STARTS));
    }

    @Test
    public void inverseENCLOSES() {
        assertThat(
            IntervalRelation.ENCLOSES.inverse(),
            is(IntervalRelation.ENCLOSED_BY));
    }

    @Test
    public void inverseENCLOSED_BY() {
        assertThat(
            IntervalRelation.ENCLOSED_BY.inverse(),
            is(IntervalRelation.ENCLOSES));
    }

    @Test
    public void inverseEQUIVALENT() {
        assertThat(
            IntervalRelation.EQUIVALENT.inverse(),
            is(IntervalRelation.EQUIVALENT));
    }

    @Test
    public void matchesPRECEDES() {
        PlainDate start1 = PlainDate.of(2014, 5, 17);
        PlainDate end1 = PlainDate.of(2014, 7, 9);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 11);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.PRECEDES.matches(a, b), is(true));
    }

    @Test
    public void matchesPRECEDED_BY() {
        PlainDate start1 = PlainDate.of(2014, 5, 17);
        PlainDate end1 = PlainDate.of(2014, 7, 9);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 11);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.PRECEDED_BY.matches(b, a), is(true));
    }

    @Test
    public void matchesMEETS() {
        PlainDate start1 = PlainDate.of(2014, 5, 17);
        PlainDate end1 = PlainDate.of(2014, 7, 9);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 10);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.MEETS.matches(a, b), is(true));
    }

    @Test
    public void matchesMET_BY() {
        PlainDate start1 = PlainDate.of(2014, 5, 17);
        PlainDate end1 = PlainDate.of(2014, 7, 9);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 10);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.MET_BY.matches(b, a), is(true));
    }

    @Test
    public void matchesOVERLAPS() {
        PlainDate start1 = PlainDate.of(2014, 5, 17);
        PlainDate end1 = PlainDate.of(2014, 7, 9);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 9);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.OVERLAPS.matches(a, b), is(true));
    }

    @Test
    public void matchesOVERLAPPED_BY() {
        PlainDate start1 = PlainDate.of(2014, 5, 17);
        PlainDate end1 = PlainDate.of(2014, 7, 9);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 9);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.OVERLAPPED_BY.matches(b, a), is(true));
    }

    @Test
    public void matchesFINISHES() {
        PlainDate start1 = PlainDate.of(2014, 8, 17);
        PlainDate end1 = PlainDate.of(2014, 10, 31);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 9);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.FINISHES.matches(a, b), is(true));
    }

    @Test
    public void matchesFINISHED_BY() {
        PlainDate start1 = PlainDate.of(2014, 8, 17);
        PlainDate end1 = PlainDate.of(2014, 10, 31);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 7, 9);
        PlainDate end2 = PlainDate.of(2014, 10, 31);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.FINISHED_BY.matches(b, a), is(true));
    }

    @Test
    public void matchesSTARTS() {
        PlainDate start1 = PlainDate.of(2014, 8, 17);
        PlainDate end1 = PlainDate.of(2014, 10, 21);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 8, 17);
        PlainDate end2 = PlainDate.of(2014, 10, 30);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.STARTS.matches(a, b), is(true));
    }

    @Test
    public void matchesSTARTED_BY() {
        PlainDate start1 = PlainDate.of(2014, 8, 17);
        PlainDate end1 = PlainDate.of(2014, 10, 21);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 8, 17);
        PlainDate end2 = PlainDate.of(2014, 10, 30);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.STARTED_BY.matches(b, a), is(true));
    }

    @Test
    public void matchesENCLOSES() {
        PlainDate start1 = PlainDate.of(2014, 8, 16);
        PlainDate end1 = PlainDate.of(2014, 10, 31);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 8, 17);
        PlainDate end2 = PlainDate.of(2014, 10, 30);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.ENCLOSES.matches(a, b), is(true));
    }

    @Test
    public void matchesENCLOSED_BY() {
        PlainDate start1 = PlainDate.of(2014, 8, 16);
        PlainDate end1 = PlainDate.of(2014, 10, 31);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 8, 17);
        PlainDate end2 = PlainDate.of(2014, 10, 30);
        DateInterval b = DateInterval.between(start2, end2);

        assertThat(IntervalRelation.ENCLOSED_BY.matches(b, a), is(true));
    }

    @Test
    public void matchesEQUIVALENT() {
        PlainDate start1 = PlainDate.of(2014, 8, 16);
        PlainDate end1 = PlainDate.of(2014, 10, 31);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 8, 16);
        PlainDate end2 = PlainDate.of(2014, 11, 1);
        DateInterval b = DateInterval.between(start2, end2).withOpenEnd();

        assertThat(IntervalRelation.EQUIVALENT.matches(a, b), is(true));
    }

    @Test
    public void betweenBiggerAndSmaller() {
        DateInterval a = bigger();
        DateInterval b = smaller();
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.PRECEDES));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.MEETS));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.OVERLAPS));

        a = a.move(8, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.FINISHED_BY));

        a = a.move(62, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.ENCLOSES));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.STARTED_BY));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.OVERLAPPED_BY));

        a = a.move(8, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.MET_BY));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.PRECEDED_BY));
    }

    @Test
    public void betweenSmallerAndBigger() {
        DateInterval a = smaller();
        DateInterval b = bigger().move(2, MONTHS).move(22, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.PRECEDES));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.MEETS));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.OVERLAPS));

        a = a.move(8, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.STARTS));

        a = a.move(2, MONTHS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.ENCLOSED_BY));

        a = a.move(2, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.FINISHES));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.OVERLAPPED_BY));

        a = a.move(8, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.MET_BY));

        a = a.move(1, DAYS);
        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.PRECEDED_BY));
    }

    @Test
    public void betweenEqualIntervals() {
        PlainDate start1 = PlainDate.of(2014, 8, 16);
        PlainDate end1 = PlainDate.of(2014, 10, 31);
        DateInterval a = DateInterval.between(start1, end1);

        PlainDate start2 = PlainDate.of(2014, 8, 16);
        PlainDate end2 = PlainDate.of(2014, 11, 1);
        DateInterval b = DateInterval.between(start2, end2).withOpenEnd();

        assertThat(
            IntervalRelation.between(a, b),
            is(IntervalRelation.EQUIVALENT));
    }

    private static DateInterval bigger() {

        PlainDate start = PlainDate.of(2014, 8, 2);
        PlainDate end = PlainDate.of(2014, 10, 12);
        return DateInterval.between(start, end);

    }

    private static DateInterval smaller() {

        PlainDate start = PlainDate.of(2014, 10, 14);
        PlainDate end = PlainDate.of(2014, 10, 22);
        return DateInterval.between(start, end);

    }

}