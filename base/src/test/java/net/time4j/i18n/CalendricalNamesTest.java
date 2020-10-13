package net.time4j.i18n;

import net.time4j.Meridiem;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.Quarter;
import net.time4j.Weekday;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.OutputContext;
import net.time4j.format.TextProvider;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class CalendricalNamesTest {

    @Test
    public void smartMonthParsing() throws ParseException {
        assertThat(
            Month.parse("Sept.", Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is(Month.SEPTEMBER));
        assertThat(
            Month.parse("Sep.", Locale.GERMAN, TextWidth.ABBREVIATED, OutputContext.FORMAT),
            is(Month.SEPTEMBER));
    }

    @Test
    public void getInstance_Chronology_Locale() {
        Chronology<PlainTime> chronology = Chronology.lookup(PlainTime.class);
        Locale locale = Locale.GERMAN;
        CalendarText expResult =
            CalendarText.getInstance("iso8601", locale);
        CalendarText result =
            CalendarText.getInstance(chronology, locale);
        assertThat(result, is(expResult));
    }

    @Test
    public void getInstance_String_Locale() {
        Locale locale = Locale.US;
        CalendarText result = CalendarText.getInstance("iso8601", locale);
        TextProvider p = null;

        for (TextProvider tmp : ResourceLoader.getInstance().services(TextProvider.class)) {
            if (
                isCalendarTypeSupported(tmp, "iso8601")
                && isLocaleSupported(tmp, locale)
            ) {
                p = tmp;
                break;
            }
        }

        if (p != null) {
            assertThat(result.toString(), is(p.toString()));
        }

        result = CalendarText.getInstance("xyz", locale);
        assertThat(result.toString(), is("FallbackProvider(xyz/en_US)"));
    }

    @Test
    public void printMonthsDE() {
        TextWidth textWidth = TextWidth.NARROW;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.GERMAN);
        String result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.MARCH);
        assertThat(result, is("M"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.MARCH);
        assertThat(result, is("März"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getStdMonths(textWidth, OutputContext.STANDALONE)
            .print(Month.MARCH);
        assertThat(result, is("März"));

        textWidth = TextWidth.SHORT;
        result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.SEPTEMBER);
        assertThat(result, is("Sept."));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.SEPTEMBER);
        assertThat(result, is("Sept."));
    }

    @Test
    public void printMonthsKAB() { // ISO-639-3
        OutputContext oc = OutputContext.FORMAT;
        TextWidth textWidth = TextWidth.WIDE;
        CalendarText instance =
            CalendarText.getInstance("iso8601", new Locale("kab"));
        String result =
            instance.getStdMonths(textWidth, oc).print(Month.JANUARY);
        assertThat(result, is("Yennayer"));
    }

    @Test
    public void printMonthsRU() {
        OutputContext oc = OutputContext.FORMAT;
        TextWidth textWidth = TextWidth.NARROW;
        CalendarText instance =
           CalendarText.getInstance("iso8601", new Locale("ru"));
        String result =
            instance.getStdMonths(textWidth, oc).print(Month.FEBRUARY);
        assertThat(result, is("Ф"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getStdMonths(textWidth, oc)
            .print(Month.MARCH);
        assertThat(result, is("марта"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getStdMonths(textWidth, OutputContext.STANDALONE)
            .print(Month.MARCH);
        assertThat(result, is("март"));
    }

    @Test
    public void printMonthsZH() {
        TextWidth textWidth = TextWidth.NARROW;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.SIMPLIFIED_CHINESE);
        String result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.MARCH);
        assertThat(result, is("3"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.MARCH);
        assertThat(result, is("三月"));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getStdMonths(textWidth, OutputContext.FORMAT)
            .print(Month.MARCH);
        assertThat(result, is("3月"));
    }

    @Test
    public void parseMonths() {
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.GERMAN);
        OutputContext outputContext = OutputContext.FORMAT;
        ParsePosition status = new ParsePosition(0);
        Month value =
            instance.getStdMonths(TextWidth.ABBREVIATED, outputContext)
            .parse("Sept.", status, Month.class);
        assertThat(value, is(Month.SEPTEMBER));

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.WIDE, outputContext)
            .parse("MÄR", status, Month.class, toAttributes(true, true));
        assertThat(value, is(Month.MARCH));

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.WIDE, outputContext)
            .parse("MÄRz", status, Month.class, toAttributes(true, false));
        assertThat(value, is(Month.MARCH));

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.SHORT, outputContext)
            .parse("MÄ", status, Month.class, toAttributes(true, true));
        assertThat(value, is(Month.MARCH));

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.SHORT, outputContext)
            .parse("Sept.", status, Month.class);
        assertThat(value, is(Month.SEPTEMBER));

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.NARROW, outputContext)
            .parse("m", status, Month.class, toAttributes(true, false));
        assertThat(value, nullValue()); // ambivalent - March or May

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.NARROW, outputContext)
            .parse("d", status, Month.class, toAttributes(true, false));
        assertThat(value, is(Month.DECEMBER));

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.WIDE, outputContext)
            .parse("ju", status, Month.class, toAttributes(true, true));
        assertThat(value, nullValue()); // ambivalent - June or July

        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.WIDE, outputContext)
            .parse("jul", status, Month.class, toAttributes(true, true));
        assertThat(value, is(Month.JULY));

        Locale locale = Locale.JAPAN;
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
        instance =
           CalendarText.getInstance("iso8601", locale);
        status.setIndex(0);
        value =
            instance.getStdMonths(TextWidth.NARROW, outputContext)
            .parse(
                dfs.getShortMonths()[Calendar.MARCH],
                status,
                Month.class);
        assertThat(value, is(Month.MARCH));
    }

    @Test
    public void printQuartersEN() {
        TextWidth textWidth = TextWidth.NARROW;
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.ENGLISH);
        String result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q3);
        assertThat(result, is("3"));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q3);
        assertThat(result, is("Q3"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q3);
        assertThat(result, is("3rd quarter"));
    }

    @Test
    public void printQuartersDE() {
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.GERMAN);
        TextWidth textWidth = TextWidth.NARROW;
        String result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("1"));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("Q1"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("1. Quartal"));
    }

    @Test
    public void printQuartersZH() {
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.SIMPLIFIED_CHINESE);
        TextWidth textWidth = TextWidth.NARROW;
        String result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("1"));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("1季度"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("第一季度"));
    }

    @Test
    public void printQuartersAR() {
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", new Locale("ar"));
        TextWidth textWidth = TextWidth.NARROW;
        String result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q4);
        assertThat(result, is("٤"));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q1);
        assertThat(result, is("الربع الأول"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q2);
        assertThat(result, is("الربع الثاني"));
    }

    @Test
    public void printQuartersRU() {
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", new Locale("ru"));
        TextWidth textWidth = TextWidth.ABBREVIATED;
        String result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q4);
        assertThat(result, is("4-й кв."));

        textWidth = TextWidth.WIDE;
        result =
            instance.getQuarters(textWidth, outputContext)
            .print(Quarter.Q2);
        assertThat(result, is("2-й квартал"));
    }

    @Test
    public void parseQuarters() {
        TextWidth textWidth = TextWidth.SHORT;
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.GERMAN);
        Quarter result =
            instance.getQuarters(textWidth, outputContext)
            .parse("Q3", new ParsePosition(0), Quarter.class);
        assertThat(result, is(Quarter.Q3));
    }

    @Test
    public void printWeekdaysEN() {
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.ENGLISH);
        TextWidth textWidth;
        String result;

        textWidth = TextWidth.NARROW;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("F"));

        textWidth = TextWidth.SHORT;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("Fr"));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("Fri"));

        textWidth = TextWidth.WIDE;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("Friday"));
    }

    @Test
    public void printWeekdaysES() {
        TextWidth textWidth = TextWidth.WIDE;
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
            CalendarText.getInstance("iso8601", new Locale("es"));
        String result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.SATURDAY);
        assertThat(result, is("sábado"));

        textWidth = TextWidth.NARROW;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.SATURDAY);
        assertThat(result, is("S"));

        textWidth = TextWidth.WIDE;
        outputContext = OutputContext.STANDALONE;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.SATURDAY);
        assertThat(result, is("sábado"));
    }

    @Test
    public void printWeekdaysZH() {
        TextWidth textWidth = TextWidth.ABBREVIATED;
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
            CalendarText.getInstance("iso8601", Locale.SIMPLIFIED_CHINESE);
        String result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.SUNDAY);
        assertThat(result, is("周日"));
    }

    @Test
    public void printWeekdaysZH_TW() {
        TextWidth textWidth = TextWidth.ABBREVIATED;
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
            CalendarText.getInstance("iso8601", Locale.TRADITIONAL_CHINESE);
        String result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.SUNDAY);
        assertThat(result, is("週日"));
    }

    @Test
    public void printWeekdaysDE() {
        OutputContext outputContext = OutputContext.FORMAT;
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.GERMAN);
        TextWidth textWidth = TextWidth.NARROW;
        String result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("F"));

        textWidth = TextWidth.SHORT;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("Fr."));

        textWidth = TextWidth.ABBREVIATED;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("Fr."));

        textWidth = TextWidth.WIDE;
        result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.FRIDAY);
        assertThat(result, is("Freitag"));
    }

    @Test
    public void parseCzechWithMultipleContextInSmartMode() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("d. MMMM uuuu", PatternType.CLDR, new Locale("cs"));
        PlainDate expected = PlainDate.of(2016, 1, 1);
        assertThat(
            f.parse("1. leden 2016"),
            is(expected));
        assertThat(
            f.parse("1. ledna 2016"),
            is(expected));
    }

    @Test(expected=ParseException.class)
    public void parseCzechWithMultipleContextInStrictMode1() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("d. MMMM uuuu", PatternType.CLDR, new Locale("cs")).with(Leniency.STRICT);
        f.parse("1. leden 2016"); // standalone but parser expects embedded format mode (symbol M)
    }

    @Test(expected=ParseException.class)
    public void parseCzechWithMultipleContextInStrictMode2() throws ParseException {
        ChronoFormatter<PlainDate> f =
            ChronoFormatter.ofDatePattern("d. LLLL uuuu", PatternType.CLDR, new Locale("cs")).with(Leniency.STRICT);
        f.parse("1. ledna 2016"); // embedded format but parser expects standalone mode (symbol L)
    }

    @Test
    public void parseWeekdays() {
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.GERMAN);
        Weekday w =
            instance.getWeekdays(TextWidth.WIDE, OutputContext.FORMAT)
            .parse("FRE", new ParsePosition(0), Weekday.class, toAttributes(true, true));
        assertThat(w, is(Weekday.FRIDAY));

        instance = CalendarText.getInstance("iso8601", Locale.ENGLISH);
        Weekday w2 =
            instance.getWeekdays(TextWidth.WIDE, OutputContext.FORMAT)
            .parse("FRI", new ParsePosition(0), Weekday.class, toAttributes(true, true));
        assertThat(w2, is(Weekday.FRIDAY));
    }

    @Test
    public void eras() throws ClassNotFoundException {
        assertThat(
           Chronology.lookup(PlainDate.class)
                .getCalendarSystem()
                .getEras()
                .isEmpty(),
           is(true));
        TextWidth textWidth = TextWidth.WIDE;
        CalendarText instance = CalendarText.getInstance("iso8601", Locale.GERMAN);
        String result = instance.getEras(textWidth).print(HistoricEra.AD);
        assertThat(result, is("n. Chr."));
    }

    @Test
    public void printMeridiems() {
        TextWidth textWidth = TextWidth.WIDE;
        CalendarText instance = CalendarText.getInstance("iso8601", Locale.ENGLISH);
        assertThat(instance.getMeridiems(textWidth, OutputContext.FORMAT).print(Meridiem.PM), is("pm"));
        assertThat(instance.getMeridiems(textWidth, OutputContext.STANDALONE).print(Meridiem.PM), is("PM"));
    }

    @Test
    public void parseMeridiems() {
        TextWidth textWidth = TextWidth.WIDE;
        CalendarText instance = CalendarText.getInstance("iso8601", Locale.ENGLISH);
        Meridiem result1 =
            instance.getMeridiems(textWidth, OutputContext.FORMAT).parse(
                "pm", new ParsePosition(0), Meridiem.class, Leniency.STRICT);
        assertThat(result1, is(Meridiem.PM));
        Meridiem result2 =
            instance.getMeridiems(textWidth, OutputContext.STANDALONE).parse(
                "PM", new ParsePosition(0), Meridiem.class, Leniency.STRICT);
        assertThat(result2, is(Meridiem.PM));
    }

    @Test
    public void textFormsDelegate() {
        CalendarText instance =
           CalendarText.getInstance("iso8601", Locale.ENGLISH);
        assertThat(
            instance.getTextForms(PlainTime.AM_PM_OF_DAY).print(Meridiem.PM),
            is("PM"));
    }

    @Test(expected=MissingResourceException.class)
    public void textFormsException() {
        CalendarText instance =
           CalendarText.getInstance("xyz", Locale.ENGLISH);
        instance.getTextForms(PlainTime.AM_PM_OF_DAY);
    }

    @Test
    public void printQuartersVietnam() {
        TextWidth textWidth = TextWidth.WIDE;
        OutputContext outputContext = OutputContext.STANDALONE;
        CalendarText instance =
            CalendarText.getInstance("iso8601", new Locale("vi"));
        String result =
            instance.getWeekdays(textWidth, outputContext)
            .print(Weekday.MONDAY);
        // test for switching from standalone to format context
        assertThat(result, is("Thứ Hai"));
    }

    private static boolean isCalendarTypeSupported(
        TextProvider p,
        String calendarType
    ) {

        for (String c : p.getSupportedCalendarTypes()) {
            if (c.equals(calendarType)) {
                return true;
            }
        }

        return false;

    }

    private static boolean isLocaleSupported(
        TextProvider p,
        Locale locale
    ) {

        for (Locale l : p.getAvailableLocales()) {
            String lang = locale.getLanguage();
            String country = locale.getCountry();

            if (
                lang.equals(l.getLanguage())
                && (country.isEmpty() || country.equals(l.getCountry()))
            ) {
                return true;
            }
        }

        return false;

    }

    private static AttributeQuery toAttributes(
        boolean caseInsensitive,
        boolean partialCompare
    ) {

        return new Attributes.Builder()
            .set(Attributes.PARSE_CASE_INSENSITIVE, caseInsensitive)
            .set(Attributes.PARSE_PARTIAL_COMPARE, partialCompare)
            .build();

    }

}