package net.time4j.tz.olson;

import net.time4j.tz.NameStyle;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class PredefinedIDTest {

    @Test
    public void nameOfLisboa() {
        TZID tzid = EUROPE.LISBON;
        assertThat(tzid.canonical(), is("Europe/Lisbon"));
    }

    @Test
    public void nameOfBerlin() {
        TZID tzid = EUROPE.BERLIN;
        assertThat(tzid.canonical(), is("Europe/Berlin"));
    }

    @Test
    public void nameOfHongkong() {
        TZID tzid = ASIA.HONG_KONG;
        assertThat(tzid.canonical(), is("Asia/Hong_Kong"));
    }

    @Test
    public void nameOfBuenosAires() {
        TZID tzid = AMERICA.ARGENTINA.BUENOS_AIRES;
        assertThat(tzid.canonical(), is("America/Argentina/Buenos_Aires"));
    }

    @Test
    public void nameOfPortoNovo() {
        TZID tzid = AFRICA.PORTO_NOVO;
        assertThat(tzid.canonical(), is("Africa/Porto-Novo"));
    }

    @Test
    public void equalsByObject() {
        TZID tzid = new TZID() {
            @Override
            public String canonical() {
                return EUROPE.BERLIN.canonical();
            }
        };
        assertThat(tzid.equals(EUROPE.BERLIN), is(false));
    }

    @Test
    public void equalsByCanonical() {
        TZID tzid = new TZID() {
            @Override
            public String canonical() {
                return EUROPE.BERLIN.canonical();
            }
        };
        assertThat(
            tzid.canonical().equals(EUROPE.BERLIN.canonical()),
            is(true));
    }

    @Test
    public void predefinedTZ() {
        TZID tzid = AMERICA.ARGENTINA.BUENOS_AIRES;
        assertThat(
            Timezone.of(tzid).getID(),
            is(tzid));
        assertThat(
            Timezone.of(tzid).getID() == tzid,
            is(true));
    }

    @Test
    public void getCity() {
        StdZoneIdentifier tzid = AMERICA.ARGENTINA.BUENOS_AIRES;
        assertThat(tzid.getCity(), is("Buenos_Aires"));
    }

    @Test
    public void getRegion() {
        StdZoneIdentifier tzid = AMERICA.ARGENTINA.BUENOS_AIRES;
        assertThat(tzid.getRegion(), is("America/Argentina"));
    }

    @Test
    public void getPreferredIDsOfBerlin() {
        TZID tzid = EUROPE.BERLIN;
        assertThat(
            Timezone.getPreferredIDs(Locale.GERMANY, false, "DEFAULT"),
            is(Collections.singleton(tzid)));
    }

    @Test
    public void getSmartPreferredIDsOfUS() {
        Set<TZID> prefs = Timezone.getPreferredIDs(Locale.US, true, "DEFAULT");
        Map<String, List<TZID>> map = new HashMap<String, List<TZID>>();

        for (TZID tzid : prefs) {
            String name =
                Timezone.of(tzid).getDisplayName(
                    NameStyle.LONG_STANDARD_TIME, Locale.US);
            List<TZID> ids = map.get(name);
            if (ids == null) {
                ids = new ArrayList<TZID>();
                map.put(name, ids);
            }
            ids.add(tzid);

            System.out.println(
                "TZID="
                + tzid.canonical()
                + "\tName="
                + name);
        }

        for (String name : map.keySet()) {
            assertThat(map.get(name).size(), is(1));
        }

        System.out.println("Phoenix: "
            + Timezone.of("America/Phoenix").getDisplayName(
                NameStyle.LONG_DAYLIGHT_TIME, Locale.US));
    }

}