package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.engine.CalendarDate;
import net.time4j.format.Leniency;
import net.time4j.format.expert.ChronoFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.format.FormatStyle;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class JapaneseTransitionTest {

    @Test
    public void beforeHeiseiStartInSmartMode() {
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 7, Leniency.SMART);
        assertThat(jcal1.getEra(), is(Nengo.SHOWA));
        assertThat(jcal1.getYear(), is(64));
        assertThat(jcal1.getMonth().getNumber(), is(1));
        assertThat(jcal1.getDayOfMonth(), is(7));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(Nengo.SHOWA, 64, EastAsianMonth.valueOf(1), 7, Leniency.SMART);
        assertThat(jcal2.getEra(), is(Nengo.SHOWA));
        assertThat(jcal2.getYear(), is(64));
        assertThat(jcal2.getMonth().getNumber(), is(1));
        assertThat(jcal2.getDayOfMonth(), is(7));

        assertThat(jcal1.equals(jcal2), is(true));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(0));
    }

    @Test
    public void beforeHeiseiStartInLaxMode() {
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 7, Leniency.LAX);
        assertThat(jcal1.getEra(), is(Nengo.HEISEI));
        assertThat(jcal1.getYear(), is(1));
        assertThat(jcal1.getMonth().getNumber(), is(1));
        assertThat(jcal1.getDayOfMonth(), is(7));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(Nengo.SHOWA, 64, EastAsianMonth.valueOf(1), 7, Leniency.LAX);
        assertThat(jcal2.getEra(), is(Nengo.SHOWA));
        assertThat(jcal2.getYear(), is(64));
        assertThat(jcal2.getMonth().getNumber(), is(1));
        assertThat(jcal2.getDayOfMonth(), is(7));

        assertThat(jcal1.equals(jcal2), is(false));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(1));
    }

    @Test
    public void beforeHeiseiStartInStrictModeGood() {
        JapaneseCalendar jcal =
            JapaneseCalendar.of(Nengo.SHOWA, 64, EastAsianMonth.valueOf(1), 7, Leniency.STRICT);
        assertThat(jcal.getEra(), is(Nengo.SHOWA));
        assertThat(jcal.getYear(), is(64));
        assertThat(jcal.getMonth().getNumber(), is(1));
        assertThat(jcal.getDayOfMonth(), is(7));
    }

    @Test(expected=IllegalArgumentException.class)
    public void beforeHeiseiStartInStrictModeBad() {
        JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 7, Leniency.STRICT);
    }

    @Test
    public void atHeiseiStartInSmartMode() {
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 8, Leniency.SMART);
        assertThat(jcal1.getEra(), is(Nengo.HEISEI));
        assertThat(jcal1.getYear(), is(1));
        assertThat(jcal1.getMonth().getNumber(), is(1));
        assertThat(jcal1.getDayOfMonth(), is(8));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(Nengo.SHOWA, 64, EastAsianMonth.valueOf(1), 8, Leniency.SMART);
        assertThat(jcal2.getEra(), is(Nengo.HEISEI));
        assertThat(jcal2.getYear(), is(1));
        assertThat(jcal2.getMonth().getNumber(), is(1));
        assertThat(jcal2.getDayOfMonth(), is(8));

        assertThat(jcal1.equals(jcal2), is(true));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(0));
    }

    @Test
    public void atHeiseiStartInLaxMode() {
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 8, Leniency.LAX);
        assertThat(jcal1.getEra(), is(Nengo.HEISEI));
        assertThat(jcal1.getYear(), is(1));
        assertThat(jcal1.getMonth().getNumber(), is(1));
        assertThat(jcal1.getDayOfMonth(), is(8));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(Nengo.SHOWA, 64, EastAsianMonth.valueOf(1), 8, Leniency.LAX);
        assertThat(jcal2.getEra(), is(Nengo.SHOWA));
        assertThat(jcal2.getYear(), is(64));
        assertThat(jcal2.getMonth().getNumber(), is(1));
        assertThat(jcal2.getDayOfMonth(), is(8));

        assertThat(jcal1.equals(jcal2), is(false));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(1));
    }

    @Test
    public void atHeiseiStartInStrictModeGood() {
        JapaneseCalendar jcal =
            JapaneseCalendar.of(Nengo.HEISEI, 1, EastAsianMonth.valueOf(1), 8, Leniency.STRICT);
        assertThat(jcal.getEra(), is(Nengo.HEISEI));
        assertThat(jcal.getYear(), is(1));
        assertThat(jcal.getMonth().getNumber(), is(1));
        assertThat(jcal.getDayOfMonth(), is(8));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atHeiseiStartInStrictModeBad() {
        JapaneseCalendar.of(Nengo.SHOWA, 64, EastAsianMonth.valueOf(1), 8, Leniency.STRICT);
    }

    @Test
    public void beforeKamakuraStartInSmartMode() {
        Nengo bunji = Nengo.ofRelatedGregorianYear(1185);
        Nengo genryaku = Nengo.ofRelatedGregorianYear(1184);
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(bunji, 1, EastAsianMonth.valueOf(8), 13, Leniency.SMART);
        assertThat(jcal1.getEra(), is(genryaku));
        assertThat(jcal1.getYear(), is(2));
        assertThat(jcal1.getMonth().getNumber(), is(8));
        assertThat(jcal1.getDayOfMonth(), is(13));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(genryaku, 2, EastAsianMonth.valueOf(8), 13, Leniency.SMART);
        assertThat(jcal2.getEra(), is(genryaku));
        assertThat(jcal2.getYear(), is(2));
        assertThat(jcal2.getMonth().getNumber(), is(8));
        assertThat(jcal2.getDayOfMonth(), is(13));

        assertThat(jcal1.equals(jcal2), is(true));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(0));
    }

    @Test
    public void beforeKamakuraStartInLaxMode() {
        Nengo bunji = Nengo.ofRelatedGregorianYear(1185);
        Nengo genryaku = Nengo.ofRelatedGregorianYear(1184);
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(bunji, 1, EastAsianMonth.valueOf(8), 13, Leniency.LAX);
        assertThat(jcal1.getEra(), is(bunji));
        assertThat(jcal1.getYear(), is(1));
        assertThat(jcal1.getMonth().getNumber(), is(8));
        assertThat(jcal1.getDayOfMonth(), is(13));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(genryaku, 2, EastAsianMonth.valueOf(8), 13, Leniency.LAX);
        assertThat(jcal2.getEra(), is(genryaku));
        assertThat(jcal2.getYear(), is(2));
        assertThat(jcal2.getMonth().getNumber(), is(8));
        assertThat(jcal2.getDayOfMonth(), is(13));

        assertThat(jcal1.equals(jcal2), is(false));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(1));
    }

    @Test
    public void beforeKamakuraStartInStrictModeGood() {
        Nengo genryaku = Nengo.ofRelatedGregorianYear(1184);
        JapaneseCalendar jcal =
            JapaneseCalendar.of(genryaku, 2, EastAsianMonth.valueOf(8), 13, Leniency.STRICT);
        assertThat(jcal.getEra(), is(genryaku));
        assertThat(jcal.getYear(), is(2));
        assertThat(jcal.getMonth().getNumber(), is(8));
        assertThat(jcal.getDayOfMonth(), is(13));
    }

    @Test(expected=IllegalArgumentException.class)
    public void beforeKamakuraStartInStrictModeBad() {
        Nengo bunji = Nengo.ofRelatedGregorianYear(1185);
        JapaneseCalendar.of(bunji, 1, EastAsianMonth.valueOf(8), 13, Leniency.STRICT);
    }

    @Test
    public void atKamakuraStartInSmartMode() {
        Nengo bunji = Nengo.ofRelatedGregorianYear(1185);
        Nengo genryaku = Nengo.ofRelatedGregorianYear(1184);
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(bunji, 1, EastAsianMonth.valueOf(8), 14, Leniency.SMART);
        assertThat(jcal1.getEra(), is(bunji));
        assertThat(jcal1.getYear(), is(1));
        assertThat(jcal1.getMonth().getNumber(), is(8));
        assertThat(jcal1.getDayOfMonth(), is(14));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(genryaku, 2, EastAsianMonth.valueOf(8), 14, Leniency.SMART);
        assertThat(jcal2.getEra(), is(bunji));
        assertThat(jcal2.getYear(), is(1));
        assertThat(jcal2.getMonth().getNumber(), is(8));
        assertThat(jcal2.getDayOfMonth(), is(14));

        assertThat(jcal1.equals(jcal2), is(true));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(0));
    }

    @Test
    public void atKamakuraStartInLaxMode() {
        Nengo bunji = Nengo.ofRelatedGregorianYear(1185);
        Nengo genryaku = Nengo.ofRelatedGregorianYear(1184);
        JapaneseCalendar jcal1 =
            JapaneseCalendar.of(bunji, 1, EastAsianMonth.valueOf(8), 14, Leniency.LAX);
        assertThat(jcal1.getEra(), is(bunji));
        assertThat(jcal1.getYear(), is(1));
        assertThat(jcal1.getMonth().getNumber(), is(8));
        assertThat(jcal1.getDayOfMonth(), is(14));

        JapaneseCalendar jcal2 =
            JapaneseCalendar.of(genryaku, 2, EastAsianMonth.valueOf(8), 14, Leniency.LAX);
        assertThat(jcal2.getEra(), is(genryaku));
        assertThat(jcal2.getYear(), is(2));
        assertThat(jcal2.getMonth().getNumber(), is(8));
        assertThat(jcal2.getDayOfMonth(), is(14));

        assertThat(jcal1.equals(jcal2), is(false));
        assertThat(jcal1.isSimultaneous(jcal2), is(true));
        assertThat(jcal1.compareTo(jcal2), is(1));
    }

    @Test
    public void atKamakuraStartInStrictModeGood() {
        Nengo bunji = Nengo.ofRelatedGregorianYear(1185);
        JapaneseCalendar jcal =
            JapaneseCalendar.of(bunji, 1, EastAsianMonth.valueOf(8), 14, Leniency.STRICT);
        assertThat(jcal.getEra(), is(bunji));
        assertThat(jcal.getYear(), is(1));
        assertThat(jcal.getMonth().getNumber(), is(8));
        assertThat(jcal.getDayOfMonth(), is(14));
    }

    @Test(expected=IllegalArgumentException.class)
    public void atKamakuraStartInStrictModeBad() {
        Nengo genryaku = Nengo.ofRelatedGregorianYear(1184);
        JapaneseCalendar.of(genryaku, 2, EastAsianMonth.valueOf(8), 14, Leniency.STRICT);
    }

    @Test
    public void caSupport() {
        Locale locale = Locale.forLanguageTag("en-u-ca-japanese");
        ChronoFormatter<CalendarDate> f = ChronoFormatter.ofGenericCalendarStyle(FormatStyle.FULL, locale);
        assertThat(
            f.format(PlainDate.of(2017, 10, 1)),
            is("Sunday, October 1, 29 Heisei"));
    }

}