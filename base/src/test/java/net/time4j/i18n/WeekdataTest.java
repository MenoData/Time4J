package net.time4j.i18n;

import net.time4j.PlainDate;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.format.Attributes;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class WeekdataTest {

    @Test
    public void isWeekendISO() {
        assertThat(
            PlainDate.of(2014, 4, 4).isWeekend(Locale.ROOT),
            is(false));
        assertThat(
            PlainDate.of(2014, 4, 5).isWeekend(Locale.ROOT),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 6).isWeekend(Locale.ROOT),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 7).isWeekend(Locale.ROOT),
            is(false));
    }

    @Test
    public void isWeekendUS() {
        assertThat(
            PlainDate.of(2014, 4, 4).isWeekend(Locale.US),
            is(false));
        assertThat(
            PlainDate.of(2014, 4, 5).isWeekend(Locale.US),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 6).isWeekend(Locale.US),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 7).isWeekend(Locale.US),
            is(false));
    }

    @Test
    public void isWeekendYemen() {
        Locale yemen = new Locale("ar", "Ye"); // Friday + Saturday in CLDR v30
        assertThat(
            PlainDate.of(2014, 4, 3).isWeekend(yemen),
            is(false));
        assertThat(
            PlainDate.of(2014, 4, 4).isWeekend(yemen),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 5).isWeekend(yemen),
            is(true));
        assertThat(
            PlainDate.of(2014, 4, 6).isWeekend(yemen),
            is(false));
    }

    @Test
    public void modelUS() {
        assertThat(
            Weekmodel.of(Locale.US),
            is(Weekmodel.of(Weekday.SUNDAY, 1)));
        assertThat(
            Weekmodel.of(Locale.US).getFirstDayOfWeek(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(Locale.US).getMinimalDaysInFirstWeek(),
            is(1));
        assertThat(
            Weekmodel.of(Locale.US).getStartOfWeekend(),
            is(Weekday.SATURDAY));
        assertThat(
            Weekmodel.of(Locale.US).getEndOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(Locale.US).getFirstWorkday(),
            is(Weekday.MONDAY));
    }

    @Test
    public void modelAfghanistan() {
        Locale afghanistan = new Locale("fa", "AF");
        assertThat(
            Weekmodel.of(afghanistan),
            is(Weekmodel.of(
                Weekday.SATURDAY, 1, Weekday.THURSDAY, Weekday.FRIDAY)));
        assertThat(
            Weekmodel.of(afghanistan).getFirstDayOfWeek(),
            is(Weekday.SATURDAY));
        assertThat(
            Weekmodel.of(afghanistan).getMinimalDaysInFirstWeek(),
            is(1));
        assertThat(
            Weekmodel.of(afghanistan).getStartOfWeekend(),
            is(Weekday.THURSDAY));
        assertThat(
            Weekmodel.of(afghanistan).getEndOfWeekend(),
            is(Weekday.FRIDAY));
        assertThat(
            Weekmodel.of(afghanistan).getFirstWorkday(),
            is(Weekday.SATURDAY));
    }

    @Test
    public void modelIndia() {
        Locale india = new Locale("", "IN");
        assertThat(
            Weekmodel.of(india),
            is(Weekmodel.of(
                Weekday.SUNDAY, 1, Weekday.SUNDAY, Weekday.SUNDAY)));
        assertThat(
            Weekmodel.of(india).getFirstDayOfWeek(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(india).getMinimalDaysInFirstWeek(),
            is(1));
        assertThat(
            Weekmodel.of(india).getStartOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(india).getEndOfWeekend(),
            is(Weekday.SUNDAY));
        assertThat(
            Weekmodel.of(india).getFirstWorkday(),
            is(Weekday.MONDAY));
    }

    @Test
    public void weekend() {
        Locale afghanistan = new Locale("fa", "AF");
        PlainDate date = PlainDate.of(2013, 3, 30); // Samstag
        assertThat(date.matches(Weekmodel.ISO.weekend()), is(true));
        assertThat(date.matches(Weekmodel.of(afghanistan).weekend()), is(false));

        date = PlainDate.of(2013, 3, 28); // Donnerstag
        assertThat(date.matches(Weekmodel.ISO.weekend()), is(false));
        assertThat(date.matches(Weekmodel.of(afghanistan).weekend()), is(true));
    }

    @Test
    public void extension_fw_rg() {
        Locale locale1 = Locale.forLanguageTag("en-US-u-fw-mon");
        assertThat(Weekmodel.of(locale1), is(Weekmodel.of(Weekday.MONDAY, 1)));
        Locale locale2 = Locale.forLanguageTag("en-US-u-fw-xxx");
        assertThat(Weekmodel.of(locale2), is(Weekmodel.of(Weekday.SUNDAY, 1)));
        Locale locale3 = Locale.forLanguageTag("en-US-u-rg-GBZZZZ");
        assertThat(Weekmodel.of(locale3), is(Weekmodel.of(Weekday.MONDAY, 4)));
        Locale locale4 = Locale.forLanguageTag("en-US");
        assertThat(Weekmodel.of(locale4), is(Weekmodel.of(Weekday.SUNDAY, 1)));
    }

    @Test
    public void hantScript() {
        Locale simplifiedChinese = Locale.SIMPLIFIED_CHINESE;
        Locale traditionalChinese = new Locale.Builder().setLanguage("zh").setScript("Hant").build();
        assertThat(traditionalChinese, is(Locale.forLanguageTag("zh-Hant")));
        assertThat(
            Weekday.MONDAY.getDisplayName(simplifiedChinese, TextWidth.ABBREVIATED, OutputContext.STANDALONE),
            is("周一")
        );
        assertThat(
            Weekday.MONDAY.getDisplayName(traditionalChinese, TextWidth.ABBREVIATED, OutputContext.STANDALONE),
            is("週一")
        );
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.setUp(PlainDate.axis(), simplifiedChinese)
                .addPattern("EEE", PatternType.CLDR)
                .addLiteral('/')
                .startSection(Attributes.TEXT_WIDTH, TextWidth.ABBREVIATED)
                .addText(Weekmodel.of(simplifiedChinese).localDayOfWeek())
                .endSection()
                .build();
        assertThat(
            f.format(PlainDate.of(2000, 1, Weekday.MONDAY)),
            is("周一/周一")
        );
        assertThat(
            f.with(traditionalChinese).format(PlainDate.of(2000, 1, Weekday.MONDAY)),
            is("週一/週一")
        );
    }

}
