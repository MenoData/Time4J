package net.time4j.tz.other;

import net.time4j.Moment;
import net.time4j.SystemClock;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.Iso8601Format;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import net.time4j.tz.ZonalOffset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Locale;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(JUnit4.class)
public class WindowsZoneTest {

    @Test
    public void getVersion() {
        assertThat(
            WindowsZone.getVersion(),
            is("$Revision: 13477 $")); // CLDR 32
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getAvailableNamesIsUnmodifiable() {
        Set<String> names = WindowsZone.getAvailableNames();
        System.out.println("windows-zones=" + names);
        names.clear();
    }

    @Test
    public void loadAll() {
        for (TZID tzid : Timezone.getAvailableIDs("WINDOWS")) {
            try {
                Timezone.of(tzid);
            } catch (RuntimeException ex) {
                fail(ex.getMessage());
            }
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofInvalidNameXYZ() {
        WindowsZone.of("xyz");
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofInvalidNameAsWinID() {
        WindowsZone.of("WINDOWS~America/New_York");
    }

    @Test(expected=IllegalArgumentException.class)
    public void ofInvalidNameAsOlsonID() {
        WindowsZone.of("America/New_York");
    }

    @Test
    public void testToString() {
        String name = "Eastern Standard Time";
        WindowsZone wzn = WindowsZone.of(name);
        assertThat(
            wzn.toString(),
            is(name));
    }

    @Test
    public void resolveSmartUS() {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        assertThat(
            wzn.resolveSmart(Locale.US).canonical(),
            is("WINDOWS~America/New_York"));
    }

    @Test
    public void resolveSmartFrance1() {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        TZID tzid = wzn.resolveSmart(Locale.FRANCE);
        assertThat(tzid, nullValue());
    }

    @Test
    public void resolveSmartFrance2() {
        WindowsZone wzn = WindowsZone.of("Romance Standard Time");
        assertThat(
            wzn.resolveSmart(Locale.FRANCE).canonical(),
            is("WINDOWS~Europe/Paris"));
    }

    @Test
    public void resolveSmartEnglish() {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        TZID tzid = wzn.resolveSmart(Locale.ENGLISH);
        assertThat(tzid, nullValue());
    }

    @Test
    public void resolve() {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        assertThat(
            wzn.resolve(Locale.US).size(),
            is(7));
    }

    @Test
    public void getDisplayNameUS() {
        String name = "Eastern Standard Time";
        WindowsZone wzn = WindowsZone.of(name);
        TZID tzid = wzn.resolveSmart(Locale.US);

        for (NameStyle style : NameStyle.values()) {
            assertThat(
                Timezone.of(tzid).getDisplayName(style, Locale.US),
                is(name));
        }
    }

    @Test
    public void getDisplayNameFrance() {
        String name = "Romance Standard Time";
        WindowsZone wzn = WindowsZone.of(name);
        TZID tzid = wzn.resolveSmart(Locale.FRANCE);

        for (NameStyle style : NameStyle.values()) {
            assertThat(
                Timezone.of(tzid).getDisplayName(style, Locale.FRANCE),
                is(name));
        }
    }

    @Test
    public void getOffset() {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        TZID winzone = wzn.resolveSmart(Locale.US);
        Moment now = SystemClock.INSTANCE.currentTime();
        ZonalOffset offset = Timezone.of(winzone).getOffset(now);
        ZonalOffset expected = Timezone.of("America/New_York").getOffset(now);
        assertThat(offset, is(expected));
    }

    @Test
    public void parseName() throws ParseException {
        ChronoFormatter<Moment> formatter =
            ChronoFormatter.setUp(Moment.class, Locale.FRANCE)
                .addPattern("uuuu-MM-dd'T'HH:mm:ss.SSSzzzz", PatternType.CLDR).build()
                .withTimezone("America/New_York");
        String input = "2012-07-01T01:59:60.123Romance Standard Time";
        String v = "2012-06-30T23:59:60,123000000Z";
        Moment leapsecond = Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse(v);
        assertThat(
            formatter.parse(input),
            is(leapsecond));
    }

    @Test
    public void serializeName() throws IOException, ClassNotFoundException {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        assertThat(wzn, is(roundtrip(wzn)));
    }

    @Test
    public void serializeTimezone() throws IOException, ClassNotFoundException {
        WindowsZone wzn = WindowsZone.of("Eastern Standard Time");
        TZID winzone = wzn.resolveSmart(Locale.US);
        Timezone tz = Timezone.of(winzone);
        assertThat(tz, is(roundtrip(tz)));
    }

    @Test
    public void normalize() {
        assertThat(
            Timezone.normalize(WindowsZone.of("Eastern Standard Time").resolveSmart(Locale.US)).canonical(),
            is("America/New_York"));
    }

    @Test
    public void toStringStatic() {
        assertThat(
            WindowsZone.toString(Timezone.of("America/New_York").getID(), Locale.US),
            is("Eastern Standard Time"));
        assertThat(
            WindowsZone.toString("Asia/Kolkata", new Locale("en", "IN")),
            is("India Standard Time"));
        // CLDR uses "Asia/Calcutta", but Time4J adjusts it during compiling of winzone-data
    }

    private static Object roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object ser = ois.readObject();
        ois.close();
        return ser;
    }

}