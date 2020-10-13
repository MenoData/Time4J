package net.time4j.calendar;

import net.time4j.Meridiem;
import net.time4j.PlainTime;
import net.time4j.SystemClock;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class EthiopianTimeTest {

    @Test
    public void hourMapping00() {
        assertThat(
            EthiopianTime.from(PlainTime.of(0)),
            is(EthiopianTime.ofNight(6, 0)));
        assertThat(
            EthiopianTime.ofNight(6, 0).toISO(),
            is(PlainTime.of(0)));
        assertThat(
            EthiopianTime.ofNight(6, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping01() {
        assertThat(
            EthiopianTime.from(PlainTime.of(1)),
            is(EthiopianTime.ofNight(7, 0)));
        assertThat(
            EthiopianTime.ofNight(7, 0).toISO(),
            is(PlainTime.of(1)));
        assertThat(
            EthiopianTime.ofNight(7, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping02() {
        assertThat(
            EthiopianTime.from(PlainTime.of(2)),
            is(EthiopianTime.ofNight(8, 0)));
        assertThat(
            EthiopianTime.ofNight(8, 0).toISO(),
            is(PlainTime.of(2)));
        assertThat(
            EthiopianTime.ofNight(8, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping03() {
        assertThat(
            EthiopianTime.from(PlainTime.of(3)),
            is(EthiopianTime.ofNight(9, 0)));
        assertThat(
            EthiopianTime.ofNight(9, 0).toISO(),
            is(PlainTime.of(3)));
        assertThat(
            EthiopianTime.ofNight(9, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping04() {
        assertThat(
            EthiopianTime.from(PlainTime.of(4)),
            is(EthiopianTime.ofNight(10, 0)));
        assertThat(
            EthiopianTime.ofNight(10, 0).toISO(),
            is(PlainTime.of(4)));
        assertThat(
            EthiopianTime.ofNight(10, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping05() {
        assertThat(
            EthiopianTime.from(PlainTime.of(5)),
            is(EthiopianTime.ofNight(11, 0)));
        assertThat(
            EthiopianTime.ofNight(11, 0).toISO(),
            is(PlainTime.of(5)));
        assertThat(
            EthiopianTime.ofNight(11, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping06() {
        assertThat(
            EthiopianTime.from(PlainTime.of(6)),
            is(EthiopianTime.ofDay(12, 0)));
        assertThat(
            EthiopianTime.ofDay(12, 0).toISO(),
            is(PlainTime.of(6)));
        assertThat(
            EthiopianTime.ofDay(12, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping07() {
        assertThat(
            EthiopianTime.from(PlainTime.of(7)),
            is(EthiopianTime.ofDay(1, 0)));
        assertThat(
            EthiopianTime.ofDay(1, 0).toISO(),
            is(PlainTime.of(7)));
        assertThat(
            EthiopianTime.ofDay(1, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping08() {
        assertThat(
            EthiopianTime.from(PlainTime.of(8)),
            is(EthiopianTime.ofDay(2, 0)));
        assertThat(
            EthiopianTime.ofDay(2, 0).toISO(),
            is(PlainTime.of(8)));
        assertThat(
            EthiopianTime.ofDay(2, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping09() {
        assertThat(
            EthiopianTime.from(PlainTime.of(9)),
            is(EthiopianTime.ofDay(3, 0)));
        assertThat(
            EthiopianTime.ofDay(3, 0).toISO(),
            is(PlainTime.of(9)));
        assertThat(
            EthiopianTime.ofDay(3, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping10() {
        assertThat(
            EthiopianTime.from(PlainTime.of(10)),
            is(EthiopianTime.ofDay(4, 0)));
        assertThat(
            EthiopianTime.ofDay(4, 0).toISO(),
            is(PlainTime.of(10)));
        assertThat(
            EthiopianTime.ofDay(4, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping11() {
        assertThat(
            EthiopianTime.from(PlainTime.of(11)),
            is(EthiopianTime.ofDay(5, 0)));
        assertThat(
            EthiopianTime.ofDay(5, 0).toISO(),
            is(PlainTime.of(11)));
        assertThat(
            EthiopianTime.ofDay(5, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping12() {
        assertThat(
            EthiopianTime.from(PlainTime.of(12)),
            is(EthiopianTime.ofDay(6, 0)));
        assertThat(
            EthiopianTime.ofDay(6, 0).toISO(),
            is(PlainTime.of(12)));
        assertThat(
            EthiopianTime.ofDay(6, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping13() {
        assertThat(
            EthiopianTime.from(PlainTime.of(13)),
            is(EthiopianTime.ofDay(7, 0)));
        assertThat(
            EthiopianTime.ofDay(7, 0).toISO(),
            is(PlainTime.of(13)));
        assertThat(
            EthiopianTime.ofDay(7, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping14() {
        assertThat(
            EthiopianTime.from(PlainTime.of(14)),
            is(EthiopianTime.ofDay(8, 0)));
        assertThat(
            EthiopianTime.ofDay(8, 0).toISO(),
            is(PlainTime.of(14)));
        assertThat(
            EthiopianTime.ofDay(8, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping15() {
        assertThat(
            EthiopianTime.from(PlainTime.of(15)),
            is(EthiopianTime.ofDay(9, 0)));
        assertThat(
            EthiopianTime.ofDay(9, 0).toISO(),
            is(PlainTime.of(15)));
        assertThat(
            EthiopianTime.ofDay(9, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping16() {
        assertThat(
            EthiopianTime.from(PlainTime.of(16)),
            is(EthiopianTime.ofDay(10, 0)));
        assertThat(
            EthiopianTime.ofDay(10, 0).toISO(),
            is(PlainTime.of(16)));
        assertThat(
            EthiopianTime.ofDay(10, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping17() {
        assertThat(
            EthiopianTime.from(PlainTime.of(17)),
            is(EthiopianTime.ofDay(11, 0)));
        assertThat(
            EthiopianTime.ofDay(11, 0).toISO(),
            is(PlainTime.of(17)));
        assertThat(
            EthiopianTime.ofDay(11, 0).isDay(),
            is(true));
    }

    @Test
    public void hourMapping18() {
        assertThat(
            EthiopianTime.from(PlainTime.of(18)),
            is(EthiopianTime.ofNight(12, 0)));
        assertThat(
            EthiopianTime.ofNight(12, 0).toISO(),
            is(PlainTime.of(18)));
        assertThat(
            EthiopianTime.ofNight(12, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping19() {
        assertThat(
            EthiopianTime.from(PlainTime.of(19)),
            is(EthiopianTime.ofNight(1, 0)));
        assertThat(
            EthiopianTime.ofNight(1, 0).toISO(),
            is(PlainTime.of(19)));
        assertThat(
            EthiopianTime.ofNight(1, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping20() {
        assertThat(
            EthiopianTime.from(PlainTime.of(20)),
            is(EthiopianTime.ofNight(2, 0)));
        assertThat(
            EthiopianTime.ofNight(2, 0).toISO(),
            is(PlainTime.of(20)));
        assertThat(
            EthiopianTime.ofNight(2, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping21() {
        assertThat(
            EthiopianTime.from(PlainTime.of(21)),
            is(EthiopianTime.ofNight(3, 0)));
        assertThat(
            EthiopianTime.ofNight(3, 0).toISO(),
            is(PlainTime.of(21)));
        assertThat(
            EthiopianTime.ofNight(3, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping22() {
        assertThat(
            EthiopianTime.from(PlainTime.of(22)),
            is(EthiopianTime.ofNight(4, 0)));
        assertThat(
            EthiopianTime.ofNight(4, 0).toISO(),
            is(PlainTime.of(22)));
        assertThat(
            EthiopianTime.ofNight(4, 0).isNight(),
            is(true));
    }

    @Test
    public void hourMapping23() {
        assertThat(
            EthiopianTime.from(PlainTime.of(23)),
            is(EthiopianTime.ofNight(5, 0)));
        assertThat(
            EthiopianTime.ofNight(5, 0).toISO(),
            is(PlainTime.of(23)));
        assertThat(
            EthiopianTime.ofNight(5, 0).isNight(),
            is(true));
    }

    @Test
    public void getters() {
        EthiopianTime ethio = EthiopianTime.ofDay(12, 15, 30);
        assertThat(ethio.getHour(), is(12));
        assertThat(ethio.getMinute(), is(15));
        assertThat(ethio.getSecond(), is(30));
    }

    @Test
    public void comparison() {
        EthiopianTime e1 = EthiopianTime.ofNight(11, 15, 30);
        EthiopianTime e2 = EthiopianTime.ofDay(12, 15, 30);
        assertThat(e1.isBefore(e2), is(false));
        assertThat(e1.isAfter(e2), is(true));
        assertThat(e1.isSimultaneous(e2), is(false));
        assertThat(
            EthiopianTime.Unit.HOURS.between(e1, e2),
            is(-23));

        e2 = e2.minus(1, EthiopianTime.Unit.HOURS);
        assertThat(e1.isBefore(e2), is(false));
        assertThat(e1.isAfter(e2), is(false));
        assertThat(e1.isSimultaneous(e2), is(true));
        assertThat(
            EthiopianTime.Unit.HOURS.between(e1, e2),
            is(0));
    }

    @Test
    public void elements() {
        EthiopianTime ethio = EthiopianTime.ofDay(12, 15, 30);
        assertThat(ethio.get(EthiopianTime.ISO_TIME), is(PlainTime.of(6, 15, 30)));
        assertThat(ethio.get(EthiopianTime.AM_PM_OF_DAY), is(Meridiem.AM));
        assertThat(ethio.get(EthiopianTime.DIGITAL_HOUR_OF_DAY), is(6));
        assertThat(ethio.get(EthiopianTime.ETHIOPIAN_HOUR), is(12));
        assertThat(ethio.get(EthiopianTime.MINUTE_OF_HOUR), is(15));
        assertThat(ethio.get(EthiopianTime.SECOND_OF_MINUTE), is(30));
    }

    @Test
    public void parseAmPm() throws ParseException {
        // in context of Ethiopian Time AND AM/PM-marker: interprete as western time
        ChronoFormatter<EthiopianTime> f =
            ChronoFormatter.setUp(EthiopianTime.axis(), new Locale("am"))
                .addPattern("h:mm a", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            f.parse("11:30 ከሰዓት").toISO(),
            is(PlainTime.of(23, 30)));
        assertThat(
            f.parse("12:00 ጥዋት").toISO(),
            is(PlainTime.midnightAtStartOfDay()));
        assertThat(
            f.parse("12:30 ጥዋት").toISO(),
            is(PlainTime.of(0, 30)));
    }

    @Test
    public void parseEkulLeilit() throws ParseException {
        ChronoFormatter<EthiopianTime> f =
            ChronoFormatter.setUp(EthiopianTime.axis(), new Locale("am"))
                .addPattern("h:mm B", PatternType.CLDR)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            f.parse("5:30 እኩለ ሌሊት").toISO(),
            is(PlainTime.of(23, 30)));
        assertThat(
            f.parse("6:00 እኩለ ሌሊት").toISO(),
            is(PlainTime.midnightAtStartOfDay()));
        assertThat(
            f.parse("6:30 እኩለ ሌሊት").toISO(),
            is(PlainTime.of(0, 30)));
    }

    @Test
    public void nowInSystemTime() {
        assertThat(EthiopianTime.nowInSystemTime(), is(SystemClock.inLocalView().now(EthiopianTime.axis())));
    }

}
