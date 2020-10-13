package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.base.ResourceLoader;
import net.time4j.format.Attributes;
import net.time4j.format.TextWidth;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class NengoTest {

    @Test
    public void ofRelatedGregorianYearModern() {
        assertThat(Nengo.ofRelatedGregorianYear(2019), is(Nengo.REIWA));
        assertThat(Nengo.ofRelatedGregorianYear(1989), is(Nengo.HEISEI));
        assertThat(Nengo.ofRelatedGregorianYear(1988), is(Nengo.SHOWA));
        assertThat(Nengo.ofRelatedGregorianYear(1926), is(Nengo.SHOWA));
        assertThat(Nengo.ofRelatedGregorianYear(1925), is(Nengo.TAISHO));
    }

    @Test
    public void ofRelatedGregorianYear749() { // 749 has two nengos!
        PlainDate expectedStart =
            ChronoHistory.ofFirstGregorianReform().convert(HistoricDate.of(HistoricEra.AD, 749, 8, 19));
        assertThat(Nengo.ofRelatedGregorianYear(749).getStart(), is(expectedStart));
        assertThat(Nengo.ofRelatedGregorianYear(749).findPrevious().get().getFirstRelatedGregorianYear(), is(749));
        assertThat(Nengo.ofRelatedGregorianYear(749).getDisplayName(Locale.ROOT), is(Nengo.hepburn("Tenpyô-shôhô", 0)));
    }

    @Test
    public void parseAmbivalentRomaji() {
        List<Nengo> expected =
            Arrays.asList(Nengo.ofRelatedGregorianYear(834), Nengo.ofRelatedGregorianYear(1312), Nengo.SHOWA);
        assertThat(Nengo.parseRomaji("shôwa"), is(expected));
        assertThat(Nengo.parseRomaji("shōwa"), is(expected));
        assertThat(Nengo.parseRomaji("sHōwa"), is(expected));
        assertThat(Nengo.parseRomaji("Shôwa"), is(expected));
        assertThat(Nengo.parseRomaji("Shōwa"), is(expected));
        assertThat(Nengo.parseRomaji("shÔwA"), is(expected));
        assertThat(Nengo.parseRomaji("shŌwA"), is(expected));
    }

    @Test
    public void parseUniqueRomaji() {
        List<Nengo> expected =
            Collections.singletonList(Nengo.ofRelatedGregorianYear(1394));
        assertThat(Nengo.parseRomaji("ôei"), is(expected));
        assertThat(Nengo.parseRomaji("Ôei"), is(expected));
        assertThat(Nengo.parseRomaji("ōei"), is(expected));
        assertThat(Nengo.parseRomaji("Ōei"), is(expected));
    }

    @Test
    public void parseUnknownRomaji() {
        List<Nengo> expected = Collections.emptyList();
        assertThat(Nengo.parseRomaji("xyz"), is(expected));
    }

    @Test
    public void meiji() {
        assertThat(Nengo.ofKanji("明治"), is(Nengo.MEIJI));
        assertThat(Nengo.parseRomaji("meiji").get(0), is(Nengo.MEIJI));
        assertThat(Nengo.ofRelatedGregorianYear(1873), is(Nengo.MEIJI));
        assertThat(Nengo.MEIJI.getFirstRelatedGregorianYear(), is(1868));
        assertThat(Nengo.MEIJI.getStart(), is(PlainDate.of(1868, 10, 23)));
        assertThat(Nengo.MEIJI.isModern(), is(true));
        assertThat(Nengo.MEIJI.matches(Nengo.Selector.MODERN), is(true));
        assertThat(Nengo.MEIJI.matches(Nengo.Selector.OFFICIAL), is(true));
        assertThat(Nengo.MEIJI.findNext().get(), is(Nengo.TAISHO));
        assertThat(Nengo.MEIJI.getDisplayName(Locale.JAPANESE), is("明治"));
        assertThat(Nengo.MEIJI.getDisplayName(Locale.ENGLISH), is("Meiji"));
        assertThat(Nengo.MEIJI.getDisplayName(Locale.ENGLISH, TextWidth.NARROW), is("M"));
    }

    @Test
    public void taisho() {
        assertThat(Nengo.ofKanji("大正"), is(Nengo.TAISHO));
        assertThat(Nengo.parseRomaji("taishô").get(0), is(Nengo.TAISHO));
        assertThat(Nengo.ofRelatedGregorianYear(1912), is(Nengo.TAISHO));
        assertThat(Nengo.TAISHO.getFirstRelatedGregorianYear(), is(1912));
        assertThat(Nengo.TAISHO.getStart(), is(PlainDate.of(1912, 7, 30)));
        assertThat(Nengo.TAISHO.isModern(), is(true));
        assertThat(Nengo.TAISHO.matches(Nengo.Selector.MODERN), is(true));
        assertThat(Nengo.TAISHO.matches(Nengo.Selector.OFFICIAL), is(true));
        assertThat(Nengo.TAISHO.findNext().get(), is(Nengo.SHOWA));
        assertThat(Nengo.TAISHO.getDisplayName(Locale.JAPANESE), is("大正"));
        assertThat(Nengo.TAISHO.getDisplayName(Locale.ENGLISH), is("Taishō"));
        assertThat(Nengo.TAISHO.getDisplayName(Locale.ENGLISH, TextWidth.NARROW), is("T"));
    }

    @Test
    public void showa() {
        assertThat(Nengo.ofKanji("昭和"), is(Nengo.SHOWA));
        assertThat(Nengo.parseRomaji("shôwa").get(2), is(Nengo.SHOWA));
        assertThat(Nengo.ofRelatedGregorianYear(1945), is(Nengo.SHOWA));
        assertThat(Nengo.SHOWA.getFirstRelatedGregorianYear(), is(1926));
        assertThat(Nengo.SHOWA.getStart(), is(PlainDate.of(1926, 12, 25)));
        assertThat(Nengo.SHOWA.isModern(), is(true));
        assertThat(Nengo.SHOWA.matches(Nengo.Selector.MODERN), is(true));
        assertThat(Nengo.SHOWA.matches(Nengo.Selector.OFFICIAL), is(true));
        assertThat(Nengo.SHOWA.findNext().get(), is(Nengo.HEISEI));
        assertThat(Nengo.SHOWA.getDisplayName(Locale.JAPANESE), is("昭和"));
        assertThat(Nengo.SHOWA.getDisplayName(Locale.ENGLISH), is("Shōwa"));
        assertThat(Nengo.SHOWA.getDisplayName(Locale.ENGLISH, TextWidth.NARROW), is("S"));
    }

    @Test
    public void heisei() {
        assertThat(Nengo.ofKanji("平成"), is(Nengo.HEISEI));
        assertThat(Nengo.parseRomaji("heisei").get(0), is(Nengo.HEISEI));
        assertThat(Nengo.ofRelatedGregorianYear(2017), is(Nengo.HEISEI));
        assertThat(Nengo.HEISEI.getFirstRelatedGregorianYear(), is(1989));
        assertThat(Nengo.HEISEI.getStart(), is(PlainDate.of(1989, 1, 8)));
        assertThat(Nengo.HEISEI.isModern(), is(true));
        assertThat(Nengo.HEISEI.matches(Nengo.Selector.MODERN), is(true));
        assertThat(Nengo.HEISEI.matches(Nengo.Selector.OFFICIAL), is(true));
        assertThat(Nengo.HEISEI.findNext().get(), is(Nengo.REIWA));
        assertThat(Nengo.HEISEI.getDisplayName(Locale.JAPANESE), is("平成"));
        assertThat(Nengo.HEISEI.getDisplayName(Locale.ENGLISH), is("Heisei"));
        assertThat(Nengo.HEISEI.getDisplayName(Locale.ENGLISH, TextWidth.NARROW), is("H"));
    }

    @Test
    public void reiwa() {
        assertThat(Nengo.ofKanji("令和"), is(Nengo.REIWA));
        assertThat(Nengo.parseRomaji("reiwa").get(0), is(Nengo.REIWA));
        assertThat(Nengo.ofRelatedGregorianYear(2020), is(Nengo.REIWA));
        assertThat(Nengo.REIWA.getFirstRelatedGregorianYear(), is(2019));
        assertThat(Nengo.REIWA.getStart(), is(PlainDate.of(2019, 5, 1)));
        assertThat(Nengo.REIWA.isModern(), is(true));
        assertThat(Nengo.REIWA.matches(Nengo.Selector.MODERN), is(true));
        assertThat(Nengo.REIWA.matches(Nengo.Selector.OFFICIAL), is(true));
        assertThat(Nengo.REIWA.findNext().isPresent(), is(false));
        assertThat(Nengo.REIWA.getDisplayName(Locale.JAPANESE), is("令和"));
        assertThat(Nengo.REIWA.getDisplayName(Locale.ENGLISH), is("Reiwa"));
        assertThat(Nengo.REIWA.getDisplayName(Locale.ENGLISH, TextWidth.NARROW), is("R"));
    }

    @Test
    public void newest() {
        assertThat(
            Nengo.NEWEST.getFirstRelatedGregorianYear() >= Nengo.REIWA.getFirstRelatedGregorianYear(),
            is(true));
        assertThat(
            Nengo.NEWEST,
            is(Nengo.stream(Nengo.Selector.MODERN).reduce((first, second) -> second).get())); // last nengo
    }

    @Test
    public void sanityCheck() throws IOException, ParseException {
        String path = "data/nengo.txt";
        URI uri = ResourceLoader.getInstance().locate("base", Nengo.class, path);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        if (is == null) {
            is = ResourceLoader.getInstance().load(Nengo.class, path, true);
        }

        List<Nengo> official = Nengo.list(Nengo.Selector.OFFICIAL);
        List<Nengo> northern = Nengo.list(Nengo.Selector.NORTHERN_COURT);
        int offsetO = -1;
        int offsetN = -1;

        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty() || line.startsWith("\uFEFF")) {
                    continue;
                }

                String[] parts = line.split(" ");
                int year = Integer.parseInt(parts[0]);
                PlainDate start = convert(parts[1]);
                String kanji = parts[2];
                String chinese = parts[3];
                String korean = parts[4];
                String russian = parts[5];
                String romaji = parts[6];

                Nengo nengo;

                if (line.endsWith("(N)")) {
                    offsetN++;
                    nengo = northern.get(offsetN);
                } else {
                    offsetO++;
                    nengo = official.get(offsetO);
                }

                assertThat(nengo.getFirstRelatedGregorianYear(), is(year));
                assertThat(nengo.getStart(), is(start));
                assertThat(nengo.getDisplayName(Locale.JAPANESE), is(kanji));
                assertThat(nengo.getDisplayName(Locale.CHINESE), is(chinese));
                assertThat(nengo.getDisplayName(Locale.KOREAN), is(korean));
                assertThat(nengo.getDisplayName(new Locale("ru")), is("Период " + russian));
                assertThat(nengo.getDisplayName(Locale.ENGLISH), is(Nengo.hepburn(romaji, 0)));
            }
        }

    }

    @Test
    public void stream() {
        assertThat(Nengo.stream().count(), is(228L)); // official nengos until REIWA
    }

    @Test
    public void list() {
        List<Nengo> nengos = Nengo.list(Nengo.Selector.MUROMACHI_PERIOD);
        assertThat(nengos.get(0), is(Nengo.ofRelatedGregorianYear(1336)));
        assertThat(nengos.get(nengos.size() - 1), is(Nengo.ofRelatedGregorianYear(1570)));
    }

    @Test
    public void parseRussianVariantCLDR() {
        Nengo.Element element = Nengo.Element.SINGLETON;
        Attributes attrs = new Attributes.Builder().setLanguage(new Locale("ru")).build();
        Nengo nengo =
            element.parse(
                "Эпоха Сьова",
                new ParsePosition(0),
                attrs);
        assertThat(nengo, is(Nengo.SHOWA));
    }

    @Test
    public void parseRussianVariantWikipedia() {
        Nengo.Element element = Nengo.Element.SINGLETON;
        Attributes attrs = new Attributes.Builder().setLanguage(new Locale("ru")).build();
        Nengo nengo =
            element.parse(
                "Период Сёва",
                new ParsePosition(0),
                attrs);
        assertThat(nengo, is(Nengo.SHOWA));
    }

    @Test
    public void calendarEra() {
        Nengo nengo = Nengo.ofRelatedGregorianYear(1393, Nengo.Selector.NORTHERN_COURT);
        assertThat(nengo.name(), is("Meitoku (1390-1394)"));
        assertThat(nengo.getValue(), is(-64));
    }

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        for (Nengo nengo : Nengo.list(Nengo.Selector.OFFICIAL)) {
            roundtrip(nengo);
        }
        for (Nengo nengo : Nengo.list(Nengo.Selector.NORTHERN_COURT)) {
            roundtrip(nengo);
        }
    }

    private static int roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        assertThat(ois.readObject() == obj, is(true)); // identity check
        ois.close();
        return data.length;
    }

    private static PlainDate convert(String date) {
        String[] components = date.split("-");
        int year = Integer.parseInt(components[0]);
        int month = Integer.parseInt(components[1]);
        int dom = Integer.parseInt(components[2]);
        HistoricDate hd = HistoricDate.of(HistoricEra.AD, year, month, dom);
        return ChronoHistory.ofFirstGregorianReform().convert(hd);
    }

}
