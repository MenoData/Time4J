package net.time4j.calendar;

import net.time4j.format.Attributes;
import net.time4j.format.NumberSystem;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.text.ParsePosition;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class TabotTest {

    @Test
    public void list() {
        int index = 1;
        for (Tabot tabot : Tabot.asList()) {
            assertThat(tabot.getDayOfMonth(), is(index));
            index++;
        }
    }

    @Test
    public void getDisplayNameRoot() {
        assertThat(
            Tabot.of(1).getDisplayName(Locale.ROOT),
            is("Lideta"));
    }

    @Test
    public void getDisplayNameAmharic() {
        assertThat(
            Tabot.of(1).getDisplayName(new Locale("am")),
            is("ልደታ"));
    }

    @Test
    public void getTabot() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).get(EthiopianCalendar.TABOT),
            is(Tabot.of(5)));
    }

    @Test
    public void getMinimumTabot() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).getMinimum(EthiopianCalendar.TABOT),
            is(Tabot.of(1)));
    }

    @Test
    public void getMaximumTabot() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).getMaximum(EthiopianCalendar.TABOT),
            is(Tabot.of(6)));
    }

    @Test
    public void isValid() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).isValid(
                EthiopianCalendar.TABOT,
                Tabot.of(6)),
            is(true));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).isValid(
                EthiopianCalendar.TABOT,
                Tabot.of(7)),
            is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withTabotEx() {
        EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).with(
            EthiopianCalendar.TABOT,
            Tabot.of(7));
    }

    @Test
    public void withTabot() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 5).with(
                EthiopianCalendar.TABOT,
                Tabot.of(6)),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 2015, 13, 6)));
    }

    @Test
    public void print() throws IOException {
        StringBuilder buffer = new StringBuilder();
        EthiopianCalendar.TABOT.print(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, 13, 1), buffer, Attributes.empty());
        assertThat(
            buffer.toString(),
            is("Lideta"));
    }

    @Test
    public void parse() {
        assertThat(
            EthiopianCalendar.TABOT.parse("Lideta", new ParsePosition(0), Attributes.empty()),
            is(Tabot.of(1)));
    }

    @Test
    public void format() {
        ChronoFormatter<EthiopianCalendar> f =
            ChronoFormatter.setUp(EthiopianCalendar.axis(), new Locale("am"))
                .addPattern("d MMMM y G", PatternType.CLDR_DATE)
                .addLiteral(" (")
                .addText(EthiopianCalendar.TABOT)
                .addLiteral(')')
                .build();
        assertThat(
            f.format(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, 6, 25)),
            is("25 የካቲት " + NumberSystem.ETHIOPIC.toNumeral(2007) + " ዓ/ም (መርቆርዮስ)"));
    }

}