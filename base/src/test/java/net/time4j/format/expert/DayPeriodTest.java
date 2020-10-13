package net.time4j.format.expert;

import net.time4j.DayPeriod;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.format.internal.IsoTextProviderSPI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class DayPeriodTest {

    @Test
    public void displayMidnight() {
        assertThat(
            PlainTime.midnightAtEndOfDay().get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("midnight"));
        assertThat(
            PlainTime.midnightAtStartOfDay().get(
                DayPeriod.of(Locale.GERMAN).approximate()),
            is("Mitternacht"));
    }

    @Test
    public void displayMorning() {
        assertThat(
            PlainTime.of(11, 59).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("am"));
        assertThat(
            PlainTime.of(11, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("in the morning"));
    }

    @Test
    public void displayNoon() {
        assertThat(
            PlainTime.of(12).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("noon"));
        assertThat(
            PlainTime.of(12).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("noon"));
    }

    @Test
    public void displayAfternoon() {
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.ENGLISH).fixed()),
            is("pm"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.GERMAN).fixed(TextWidth.ABBREVIATED, OutputContext.FORMAT)),
            is("PM"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate()),
            is("in the afternoon"));
        assertThat(
            PlainTime.of(17, 59).get(
                DayPeriod.of(Locale.GERMAN).approximate()),
            is("nachmittags"));
    }

    @Test
    public void displayEvening() {
        assertThat(
            PlainTime.of(20, 45).get(
                DayPeriod.of(Locale.ENGLISH).approximate(TextWidth.WIDE, OutputContext.STANDALONE)
            ),
            is("evening"));
    }

    @Test
    public void displayNight() {
        assertThat(
            PlainTime.of(5, 59).get(
                DayPeriod.of(Locale.ENGLISH).approximate(TextWidth.WIDE, OutputContext.FORMAT)
            ),
            is("at night"));
    }

    @Test
    public void startEndForMidnight() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(0)),
            is(PlainTime.of(21)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(0)),
            is(PlainTime.of(6)));
    }

    @Test
    public void startEndForNight1() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(4)),
            is(PlainTime.of(21)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(4)),
            is(PlainTime.of(6)));
    }

    @Test
    public void startEndForMorning() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(9)),
            is(PlainTime.of(6)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(9)),
            is(PlainTime.of(12)));
    }

    @Test
    public void startEndForNoon() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(12)),
            is(PlainTime.of(12)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(12)),
            is(PlainTime.of(18)));
    }

    @Test
    public void startEndForAfternoon() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(15)),
            is(PlainTime.of(12)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(15)),
            is(PlainTime.of(18)));
    }

    @Test
    public void startEndForEvening() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(20)),
            is(PlainTime.of(18)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(20)),
            is(PlainTime.of(21)));
    }

    @Test
    public void startEndForNight2() {
        assertThat(
            DayPeriod.of(Locale.US).getStart(PlainTime.of(23)),
            is(PlainTime.of(21)));
        assertThat(
            DayPeriod.of(Locale.US).getEnd(PlainTime.of(23)),
            is(PlainTime.of(6)));
    }

    @Test
    public void fallback() {
        assertThat(
            PlainTime.of(5).get(DayPeriod.of(new Locale("xyz")).approximate()),
            is("AM"));
        assertThat(
            PlainTime.of(12).get(DayPeriod.of(new Locale("xyz")).approximate()),
            is("PM"));
    }

    @Test
    public void colombiaMorning() {
        DayPeriod dp = DayPeriod.of(new Locale("es", "CO"));
        assertThat(
            dp.getStart(PlainTime.of(3)),
            is(PlainTime.midnightAtStartOfDay()));
        assertThat(
            dp.getStart(PlainTime.of(7)),
            is(PlainTime.midnightAtStartOfDay()));
    }

    @Test
    public void formatFixedEnglish() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm b", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.format(PlainTime.of(3, 45)),
            is("3:45 am"));
        assertThat(
            f.parse("3:45 am"),
            is(PlainTime.of(3, 45)));
        assertThat(
            f.format(PlainTime.of(23, 45)),
            is("11:45 pm"));
        assertThat(
            f.parse("11:45 pm"),
            is(PlainTime.of(23, 45)));
        assertThat(
            f.format(PlainTime.of(0)),
            is("12:00 midnight"));
        assertThat(
            f.parse("12:00 midnight"),
            is(PlainTime.of(0)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 noon"));
        assertThat(
            f.parse("12:00 noon"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(17, 15)),
            is("5:15 pm"));
        assertThat(
            f.parse("5:15 pm"),
            is(PlainTime.of(17, 15)));
    }

    @Test
    public void formatFlexibleEnglishWide() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBB", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.format(PlainTime.of(3, 45)),
            is("3:45 at night"));
        assertThat(
            f.parse("3:45 at night"),
            is(PlainTime.of(3, 45)));
        assertThat(
            f.format(PlainTime.of(23, 45)),
            is("11:45 at night"));
        assertThat(
            f.parse("11:45 at night"),
            is(PlainTime.of(23, 45)));
        assertThat(
            f.format(PlainTime.of(0)),
            is("12:00 midnight"));
        assertThat(
            f.parse("12:00 midnight"),
            is(PlainTime.of(0)));
        assertThat(
            f.parse("12:00 at night"),
            is(PlainTime.of(0)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 noon"));
        assertThat(
            f.parse("12:00 noon"),
            is(PlainTime.of(12)));
        assertThat(
            f.parse("12:00 in the afternoon"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(17, 15)),
            is("5:15 in the afternoon"));
        assertThat(
            f.parse("5:15 in the afternoon"),
            is(PlainTime.of(17, 15)));
    }

    @Test
    public void consistencyAtMidnight1() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBB", PatternType.CLDR, Locale.ENGLISH).with(Leniency.STRICT);
        assertThat(
            f.parse("12:00 midnight"),
            is(PlainTime.of(0)));
    }

    @Test(expected=ParseException.class)
    public void consistencyAtMidnight2() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBB", PatternType.CLDR, Locale.ENGLISH).with(Leniency.STRICT);
        assertThat(
            f.parse("12:00 at night"),
            is(PlainTime.of(0)));
    }

    @Test
    public void formatFlexibleEnglishNarrow() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBBB", PatternType.CLDR, Locale.ENGLISH);
        assertThat(
            f.format(PlainTime.of(3, 45)),
            is("3:45 at night"));
        assertThat(
            f.parse("3:45 at night"),
            is(PlainTime.of(3, 45)));
        assertThat(
            f.format(PlainTime.of(23, 45)),
            is("11:45 at night"));
        assertThat(
            f.parse("11:45 at night"),
            is(PlainTime.of(23, 45)));
        assertThat(
            f.format(PlainTime.of(0)),
            is("12:00 mi"));
        assertThat(
            f.parse("12:00 at night"),
            is(PlainTime.of(0)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 n"));
        assertThat(
            f.parse("12:00 n"),
            is(PlainTime.of(12)));
        assertThat(
            f.parse("12:00 in the afternoon"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(17, 15)),
            is("5:15 in the afternoon"));
        assertThat(
            f.parse("5:15 in the afternoon"),
            is(PlainTime.of(17, 15)));
    }

    @Test
    public void formatFlexibleGerman0345() throws ParseException {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.ofTimestampPattern("d. MMMM uuuu h:mm BBBB", PatternType.CLDR, Locale.GERMAN);
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 3, 45)),
            is("10. Dezember 2015 3:45 nachts"));
        assertThat(
            f.parse("10. Dezember 2015 3:45 nachts"),
            is(PlainTimestamp.of(2015, 12, 10, 3, 45)));
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 15, 45)),
            is("10. Dezember 2015 3:45 nachmittags"));
        assertThat(
            f.parse("10. Dezember 2015 3:45 nachmittags"),
            is(PlainTimestamp.of(2015, 12, 10, 15, 45)));
    }

    @Test
    public void formatFlexibleGerman0900() throws ParseException {
        ChronoFormatter<PlainTimestamp> f =
            ChronoFormatter.ofTimestampPattern("d. MMMM uuuu h:mm BBBB", PatternType.CLDR, Locale.GERMAN);
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 9, 0)),
            is("10. Dezember 2015 9:00 morgens"));
        assertThat(
            f.parse("10. Dezember 2015 9:00 morgens"),
            is(PlainTimestamp.of(2015, 12, 10, 9, 0)));
        assertThat(
            f.format(PlainTimestamp.of(2015, 12, 10, 21, 0)),
            is("10. Dezember 2015 9:00 abends"));
        assertThat(
            f.parse("10. Dezember 2015 9:00 abends"),
            is(PlainTimestamp.of(2015, 12, 10, 21, 0)));
    }

    @Test
    public void formatFlexibleIndonesian() throws ParseException { // test for ambivalent code "afternoon1"
        ChronoFormatter<PlainTime> f =
            ChronoFormatter.ofTimePattern("h:mm BBBB", PatternType.CLDR, new Locale("id")); // or "in"
        assertThat(
            f.format(PlainTime.of(13, 45)),
            is("1:45 siang"));
        assertThat(
            f.parse("1:45 siang"),
            is(PlainTime.of(13, 45)));
        assertThat(
            f.format(PlainTime.of(11, 15)),
            is("11:15 siang"));
        assertThat(
            f.parse("11:15 siang"),
            is(PlainTime.of(11, 15)));
    }

    @Test
    public void formatCustom() throws ParseException {
        Map<PlainTime, String> timeToLabels = new HashMap<>();
        timeToLabels.put(PlainTime.of(23), "night");
        timeToLabels.put(PlainTime.of(7), "morning");
        timeToLabels.put(PlainTime.of(12), "afternoon");
        timeToLabels.put(PlainTime.of(18, 30), "evening");

        ChronoFormatter<PlainTime> f =
            ChronoFormatter.setUp(PlainTime.axis(), Locale.ENGLISH)
                .addPattern("h:mm ", PatternType.CLDR)
                .addDayPeriod(timeToLabels)
                .build()
                .with(Leniency.STRICT);
        assertThat(
            f.format(PlainTime.of(11, 59)),
            is("11:59 morning"));
        assertThat(
            f.parse("11:59 morning"),
            is(PlainTime.of(11, 59)));
        assertThat(
            f.format(PlainTime.of(12)),
            is("12:00 afternoon"));
        assertThat(
            f.parse("12:00 afternoon"),
            is(PlainTime.of(12)));
        assertThat(
            f.format(PlainTime.of(18, 29)),
            is("6:29 afternoon"));
        assertThat(
            f.parse("6:29 afternoon"),
            is(PlainTime.of(18, 29)));
        assertThat(
            f.format(PlainTime.of(18, 30)),
            is("6:30 evening"));
        assertThat(
            f.parse("6:30 evening"),
            is(PlainTime.of(18, 30)));
    }

    @Test(expected=ParseException.class)
    public void parseCustomWithInconsistency() throws ParseException {
        Map<PlainTime, String> timeToLabels = new HashMap<>();
        timeToLabels.put(PlainTime.of(23), "night");
        timeToLabels.put(PlainTime.of(7), "morning");
        timeToLabels.put(PlainTime.of(12), "afternoon");
        timeToLabels.put(PlainTime.of(18, 30), "evening");

        ChronoFormatter<PlainTime> f =
            ChronoFormatter.setUp(PlainTime.axis(), Locale.ENGLISH)
                .addPattern("h:mm ", PatternType.CLDR)
                .addDayPeriod(timeToLabels)
                .build()
                .with(Leniency.STRICT);
        f.parse("12:00 morning");
    }

    @Test
    public void parseFlexibleZulu() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter
                .ofTimePattern("h:mm BBBB", PatternType.CLDR, new Locale("zu"))
                .with(Leniency.STRICT);
        assertThat(
            f.with(Attributes.OUTPUT_CONTEXT, OutputContext.STANDALONE).parse("5:45 entathakusa"),
            is(PlainTime.of(5, 45)));
        assertThat(
            f.parse("5:45 entathakusa"),
            is(PlainTime.of(5, 45)));
        assertThat(
            f.parse("9:45 ekuseni"),
            is(PlainTime.of(9, 45)));
        assertThat(
            f.parse("10:45 emini"),
            is(PlainTime.of(10, 45)));
        assertThat(
            f.parse("12:45 emini"),
            is(PlainTime.of(12, 45)));
        assertThat(
            f.parse("6:45 ntambama"),
            is(PlainTime.of(18, 45)));
        assertThat(
            f.parse("7:45 ebusuku"),
            is(PlainTime.of(19, 45)));
    }

    @Test // test for ambivalent dayperiods, now adjusted for CLDR-35
    public void parseFlexibleFarsi() throws ParseException {
        ChronoFormatter<PlainTime> f =
            ChronoFormatter
                .ofTimePattern("h:mm BBBB", PatternType.CLDR, new Locale("fa"))
                .with(Leniency.STRICT)
                .with(Attributes.ZERO_DIGIT, '0');
        assertThat(
            f.parse("3:45 عصر"), // afternoon1
            is(PlainTime.of(15, 45)));
        assertThat(
            f.parse("7:45 شب"), // night1
            is(PlainTime.of(19, 45)));
    }

    @Test
    public void checkSanity()
        throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Method mKey =
            DayPeriod.class.getDeclaredMethod(
                "createKey", Map.class, TextWidth.class, OutputContext.class, String.class);
        mKey.setAccessible(true);
        Field field = DayPeriod.class.getDeclaredField("codeMap");
        field.setAccessible(true);

        for (Locale locale : IsoTextProviderSPI.SINGLETON.getAvailableLocales()) {
            Map<String, String> textForms = CalendarText.getIsoInstance(locale).getTextForms();
            DayPeriod dp = DayPeriod.of(locale);
            Map<?, ?> m = Map.class.cast(field.get(dp));
            Set<String> codes = new LinkedHashSet<>();
            for (Object code : m.values()) {
                codes.add(code.toString());
            }

            for (TextWidth tw : TextWidth.values()) {
                if (tw == TextWidth.SHORT) {
                    continue;
                }
                for (OutputContext oc : OutputContext.values()) {
                    Set<String> translations = new HashSet<>();
                    for (String code : codes) {
                        String key = mKey.invoke(null, textForms, tw, oc, code).toString();
                        String text = textForms.get(key);
                        if (text == null) {
                            fail("Fallback problem detected: " + locale + "/" + tw + "/" + oc + "/" + code);
                        } else if (!translations.add(text) && (tw != TextWidth.NARROW) && isCheckWanted(locale)) {
                            fail("Ambivalent text forms detected: " + locale + "/" + tw + "/" + oc + "/" + code);
                        }
                    }
                }
            }
        }
    }

    private static boolean isCheckWanted(Locale locale) {
/*
        String lang = locale.getLanguage();
        // require manual check
        return !(lang.equals("fr") || lang.equals("ms")); // fr + ms corrected for wrong CLDR-v32-data
*/
        return true;
    }

}
