package net.time4j.calendar;

import net.time4j.base.GregorianMath;
import net.time4j.engine.CalendarDays;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSystem;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class RelatedGregorianYearTest {

    @Test
    public void coptic() {
        assertThat(
            CopticCalendar.of(1724, 1, 1).get(CopticCalendar.RELATED_GREGORIAN_YEAR), // 2007-09-12
            is(2007));
        assertThat(
            CopticCalendar.of(1724, 6, 1).get(CopticCalendar.RELATED_GREGORIAN_YEAR), // 2008-02-09
            is(2007));
        assertThat(
            CopticCalendar.of(1724, 13, 5).get(CopticCalendar.RELATED_GREGORIAN_YEAR),
            is(2007));
        assertThat(
            CopticCalendar.of(1724, 13, 5).plus(CalendarDays.ONE).get(CopticCalendar.RELATED_GREGORIAN_YEAR),
            is(2008));
    }

    @Test
    public void copticMinimum() {
        CopticCalendar date = CopticCalendar.of(1724, 13, 5);
        assertThat(
            date.getMinimum(CopticCalendar.RELATED_GREGORIAN_YEAR),
            is(284));
    }

    @Test
    public void copticMaximum() {
        CopticCalendar date = CopticCalendar.of(1724, 13, 5);
        assertThat(
            date.getMaximum(CopticCalendar.RELATED_GREGORIAN_YEAR),
            is(10282));
    }

    @Test
    public void formatCoptic() {
        ChronoFormatter<CopticCalendar> f =
            ChronoFormatter.ofPattern("d. MMMM yyyy (r)", PatternType.CLDR, Locale.ENGLISH, CopticCalendar.axis());
        assertThat(
            f.format(CopticCalendar.of(1724, 6, 1)),
            is("1. Amshir 1724 (2007)"));
    }

    @Test
    public void parseCoptic() throws ParseException {
        ChronoFormatter<CopticCalendar> f =
            ChronoFormatter.ofPattern("d. MMMM yyyy (r)", PatternType.CLDR, Locale.ENGLISH, CopticCalendar.axis())
                .with(Leniency.STRICT);
        assertThat(
            f.parse("1. Amshir 1724 (2007)"),
            is(CopticCalendar.of(1724, 6, 1)));
    }

    @Test
    public void ethiopian() {
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2009, 1, 2).get(EthiopianCalendar.RELATED_GREGORIAN_YEAR),
            is(2016));
        assertThat(
            EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2009, 6, 2).get(EthiopianCalendar.RELATED_GREGORIAN_YEAR),
            is(2016));
    }

    @Test
    public void ethiopianMinimum() {
        EthiopianCalendar date = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2009, 6, 2);
        assertThat(
            date.getMinimum(EthiopianCalendar.RELATED_GREGORIAN_YEAR),
            is(-5492));
    }

    @Test
    public void ethiopianMaximum() {
        EthiopianCalendar date = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2009, 6, 2);
        assertThat(
            date.getMaximum(EthiopianCalendar.RELATED_GREGORIAN_YEAR),
            is(10006));
    }

    @Test
    public void formatEthiopianWithAmharicYear() {
        EthiopianCalendar date = EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2009, 6, 4);
        ChronoFormatter<EthiopianCalendar> f =
            ChronoFormatter.setUp(EthiopianCalendar.axis(), Locale.ENGLISH)
            .startSection(Attributes.NUMBER_SYSTEM, NumberSystem.ARABIC)
            .addPattern("d. MMMM ", PatternType.CLDR)
            .endSection()
            .addPattern("yyyy (r)", PatternType.CLDR)
            .build()
            .with(Attributes.NUMBER_SYSTEM, NumberSystem.ETHIOPIC);
        assertThat(
            f.format(date),
            is("4. Yekatit " + NumberSystem.ETHIOPIC.toNumeral(2009) + " (2016)"));
    }

    @Test
    public void thaisolar() {
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 5, 10).get(ThaiSolarCalendar.RELATED_GREGORIAN_YEAR), // 1939-05-10
            is(1939));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2482, 3, 31).get(ThaiSolarCalendar.RELATED_GREGORIAN_YEAR), // 1940-03-31
            is(1939));
        assertThat(
            ThaiSolarCalendar.ofBuddhist(2558, 3, 15).get(ThaiSolarCalendar.RELATED_GREGORIAN_YEAR), // 2015-03-15
            is(2015));
    }

    @Test
    public void thaisolarMinimum() {
        ThaiSolarCalendar date = ThaiSolarCalendar.ofBuddhist(2482, 3, 31);
        assertThat(
            date.getMinimum(ThaiSolarCalendar.RELATED_GREGORIAN_YEAR),
            is(-542));
    }

    @Test
    public void thaisolarMaximum() {
        ThaiSolarCalendar date = ThaiSolarCalendar.ofBuddhist(2482, 3, 31);
        assertThat(
            date.getMaximum(ThaiSolarCalendar.RELATED_GREGORIAN_YEAR),
            is(GregorianMath.MAX_YEAR));
    }

    @Test
    public void readOnlyElement1() {
        CopticCalendar date = CopticCalendar.of(1724, 13, 5);
        assertThat(
            date.isValid(CopticCalendar.RELATED_GREGORIAN_YEAR, 2007),
            is(true));
        assertThat(
            date.with(CopticCalendar.RELATED_GREGORIAN_YEAR, 2007),
            is(date));
    }

    @Test
    public void readOnlyElement2() {
        CopticCalendar date = CopticCalendar.of(1724, 13, 5);
        assertThat(
            date.isValid(CopticCalendar.RELATED_GREGORIAN_YEAR, 2008),
            is(false));
    }

    @Test(expected=IllegalArgumentException.class)
    public void readOnlyElement3() {
        CopticCalendar date = CopticCalendar.of(1724, 13, 5);
        date.with(CopticCalendar.RELATED_GREGORIAN_YEAR, 2008);
    }

    // --- basic element tests ---

    @Test
    public void name() {
        assertThat(CopticCalendar.RELATED_GREGORIAN_YEAR.name(), is("RELATED_GREGORIAN_YEAR"));
    }

    @Test
    public void isDateElement() {
        assertThat(CopticCalendar.RELATED_GREGORIAN_YEAR.isDateElement(), is(true));
    }

    @Test
    public void isTimeElement() {
        assertThat(CopticCalendar.RELATED_GREGORIAN_YEAR.isTimeElement(), is(false));
    }

    @Test
    public void getSymbol() {
        assertThat(CopticCalendar.RELATED_GREGORIAN_YEAR.getSymbol(), is('r'));
    }

    @Test
    public void serialization() throws IOException, ClassNotFoundException {
        roundtrip(CopticCalendar.RELATED_GREGORIAN_YEAR);
    }

    private static void roundtrip(Object obj)
        throws IOException, ClassNotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] data = baos.toByteArray();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        assertThat(ois.readObject(), is(obj));
        ois.close();
    }

}
