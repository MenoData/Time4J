package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class EvangelistTest {

    @Test
    public void getDisplayNameRoot() {
        assertThat(
            Evangelist.MATTHEW.getDisplayName(Locale.ROOT),
            is("I."));
        assertThat(
            Evangelist.MARK.getDisplayName(Locale.ROOT),
            is("II."));
        assertThat(
            Evangelist.LUKE.getDisplayName(Locale.ROOT),
            is("III."));
        assertThat(
            Evangelist.JOHN.getDisplayName(Locale.ROOT),
            is("IV."));
    }

    @Test
    public void getDisplayNameEnglish() {
        assertThat(
            Evangelist.MATTHEW.getDisplayName(Locale.ENGLISH),
            is("Matthew"));
        assertThat(
            Evangelist.MARK.getDisplayName(Locale.ENGLISH),
            is("Mark"));
        assertThat(
            Evangelist.LUKE.getDisplayName(Locale.ENGLISH),
            is("Luke"));
        assertThat(
            Evangelist.JOHN.getDisplayName(Locale.ENGLISH),
            is("John"));
    }

    @Test
    public void getDisplayNameGerman() {
        assertThat(
            Evangelist.MATTHEW.getDisplayName(Locale.GERMAN),
            is("Matthäus"));
        assertThat(
            Evangelist.MARK.getDisplayName(Locale.GERMAN),
            is("Markus"));
        assertThat(
            Evangelist.LUKE.getDisplayName(Locale.GERMAN),
            is("Lukas"));
        assertThat(
            Evangelist.JOHN.getDisplayName(Locale.GERMAN),
            is("Johannes"));
    }

    @Test
    public void getEvangelist() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 1, 1, 1).get(EthiopianCalendar.EVANGELIST),
            is(Evangelist.MATTHEW));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1, 1, 1).get(EthiopianCalendar.EVANGELIST),
            is(Evangelist.MATTHEW));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 15499, 1, 1).get(EthiopianCalendar.EVANGELIST),
            is(Evangelist.LUKE));
    }

    @Test
    public void isValid() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 15497, 1, 1).isValid(
                EthiopianCalendar.EVANGELIST,
                Evangelist.JOHN),
            is(false));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 15497, 1, 1).isValid(
                EthiopianCalendar.EVANGELIST,
                Evangelist.LUKE),
            is(true));
    }

    @Test(expected=ArithmeticException.class)
    public void withEvangelistEx() {
        EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 15499, 1, 1).with(
            EthiopianCalendar.EVANGELIST,
            Evangelist.JOHN);
    }

    @Test
    public void withEvangelist() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 1, 1, 1).with(
                EthiopianCalendar.EVANGELIST,
                Evangelist.JOHN),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 4, 1, 1)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 5499, 1, 1).with(
                EthiopianCalendar.EVANGELIST,
                Evangelist.LUKE),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 5499, 1, 1)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 5499, 1, 1).with(
                EthiopianCalendar.EVANGELIST,
                Evangelist.JOHN),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_ALEM, 5500, 1, 1)));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 1, 1, 1).with(
                EthiopianCalendar.EVANGELIST,
                Evangelist.LUKE),
            is(EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 3, 1, 1)));
    }

}