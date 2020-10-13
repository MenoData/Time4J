package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class HebrewMonthTest {

    @Test
    public void getValue() {
        assertThat(HebrewMonth.TISHRI.getValue(), is(1));
        assertThat(HebrewMonth.HESHVAN.getValue(), is(2));
        assertThat(HebrewMonth.KISLEV.getValue(), is(3));
        assertThat(HebrewMonth.TEVET.getValue(), is(4));
        assertThat(HebrewMonth.SHEVAT.getValue(), is(5));
        assertThat(HebrewMonth.ADAR_I.getValue(), is(6));
        assertThat(HebrewMonth.ADAR_II.getValue(), is(7));
        assertThat(HebrewMonth.NISAN.getValue(), is(8));
        assertThat(HebrewMonth.IYAR.getValue(), is(9));
        assertThat(HebrewMonth.SIVAN.getValue(), is(10));
        assertThat(HebrewMonth.TAMUZ.getValue(), is(11));
        assertThat(HebrewMonth.AV.getValue(), is(12));
        assertThat(HebrewMonth.ELUL.getValue(), is(13));
    }

    @Test
    public void getCivilValueNormalYear() {
        assertThat(HebrewMonth.TISHRI.getCivilValue(false), is(1));
        assertThat(HebrewMonth.HESHVAN.getCivilValue(false), is(2));
        assertThat(HebrewMonth.KISLEV.getCivilValue(false), is(3));
        assertThat(HebrewMonth.TEVET.getCivilValue(false), is(4));
        assertThat(HebrewMonth.SHEVAT.getCivilValue(false), is(5));
        assertThat(HebrewMonth.ADAR_I.getCivilValue(false), is(6));
        assertThat(HebrewMonth.ADAR_II.getCivilValue(false), is(6));
        assertThat(HebrewMonth.NISAN.getCivilValue(false), is(7));
        assertThat(HebrewMonth.IYAR.getCivilValue(false), is(8));
        assertThat(HebrewMonth.SIVAN.getCivilValue(false), is(9));
        assertThat(HebrewMonth.TAMUZ.getCivilValue(false), is(10));
        assertThat(HebrewMonth.AV.getCivilValue(false), is(11));
        assertThat(HebrewMonth.ELUL.getCivilValue(false), is(12));
    }

    @Test
    public void getCivilValueLeapYear() {
        assertThat(HebrewMonth.TISHRI.getCivilValue(true), is(1));
        assertThat(HebrewMonth.HESHVAN.getCivilValue(true), is(2));
        assertThat(HebrewMonth.KISLEV.getCivilValue(true), is(3));
        assertThat(HebrewMonth.TEVET.getCivilValue(true), is(4));
        assertThat(HebrewMonth.SHEVAT.getCivilValue(true), is(5));
        assertThat(HebrewMonth.ADAR_I.getCivilValue(true), is(6));
        assertThat(HebrewMonth.ADAR_II.getCivilValue(true), is(7));
        assertThat(HebrewMonth.NISAN.getCivilValue(true), is(8));
        assertThat(HebrewMonth.IYAR.getCivilValue(true), is(9));
        assertThat(HebrewMonth.SIVAN.getCivilValue(true), is(10));
        assertThat(HebrewMonth.TAMUZ.getCivilValue(true), is(11));
        assertThat(HebrewMonth.AV.getCivilValue(true), is(12));
        assertThat(HebrewMonth.ELUL.getCivilValue(true), is(13));
    }

    @Test
    public void valueOfCivilNormalYear() {
        assertThat(HebrewMonth.valueOfCivil(1, false), is(HebrewMonth.TISHRI));
        assertThat(HebrewMonth.valueOfCivil(2, false), is(HebrewMonth.HESHVAN));
        assertThat(HebrewMonth.valueOfCivil(3, false), is(HebrewMonth.KISLEV));
        assertThat(HebrewMonth.valueOfCivil(4, false), is(HebrewMonth.TEVET));
        assertThat(HebrewMonth.valueOfCivil(5, false), is(HebrewMonth.SHEVAT));
        assertThat(HebrewMonth.valueOfCivil(6, false), is(HebrewMonth.ADAR_II));
        assertThat(HebrewMonth.valueOfCivil(7, false), is(HebrewMonth.NISAN));
        assertThat(HebrewMonth.valueOfCivil(8, false), is(HebrewMonth.IYAR));
        assertThat(HebrewMonth.valueOfCivil(9, false), is(HebrewMonth.SIVAN));
        assertThat(HebrewMonth.valueOfCivil(10, false), is(HebrewMonth.TAMUZ));
        assertThat(HebrewMonth.valueOfCivil(11, false), is(HebrewMonth.AV));
        assertThat(HebrewMonth.valueOfCivil(12, false), is(HebrewMonth.ELUL));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfCivilInvalid() {
        HebrewMonth.valueOfCivil(13, false);
    }

    @Test
    public void valueOfCivilLeapYear() {
        assertThat(HebrewMonth.valueOfCivil(1, true), is(HebrewMonth.TISHRI));
        assertThat(HebrewMonth.valueOfCivil(2, true), is(HebrewMonth.HESHVAN));
        assertThat(HebrewMonth.valueOfCivil(3, true), is(HebrewMonth.KISLEV));
        assertThat(HebrewMonth.valueOfCivil(4, true), is(HebrewMonth.TEVET));
        assertThat(HebrewMonth.valueOfCivil(5, true), is(HebrewMonth.SHEVAT));
        assertThat(HebrewMonth.valueOfCivil(6, true), is(HebrewMonth.ADAR_I));
        assertThat(HebrewMonth.valueOfCivil(7, true), is(HebrewMonth.ADAR_II));
        assertThat(HebrewMonth.valueOfCivil(8, true), is(HebrewMonth.NISAN));
        assertThat(HebrewMonth.valueOfCivil(9, true), is(HebrewMonth.IYAR));
        assertThat(HebrewMonth.valueOfCivil(10, true), is(HebrewMonth.SIVAN));
        assertThat(HebrewMonth.valueOfCivil(11, true), is(HebrewMonth.TAMUZ));
        assertThat(HebrewMonth.valueOfCivil(12, true), is(HebrewMonth.AV));
        assertThat(HebrewMonth.valueOfCivil(13, true), is(HebrewMonth.ELUL));
    }

    @Test
    public void getBiblicalValueNormalYear() {
        assertThat(HebrewMonth.TISHRI.getBiblicalValue(false), is(7));
        assertThat(HebrewMonth.HESHVAN.getBiblicalValue(false), is(8));
        assertThat(HebrewMonth.KISLEV.getBiblicalValue(false), is(9));
        assertThat(HebrewMonth.TEVET.getBiblicalValue(false), is(10));
        assertThat(HebrewMonth.SHEVAT.getBiblicalValue(false), is(11));
        assertThat(HebrewMonth.ADAR_I.getBiblicalValue(false), is(12));
        assertThat(HebrewMonth.ADAR_II.getBiblicalValue(false), is(12));
        assertThat(HebrewMonth.NISAN.getBiblicalValue(false), is(1));
        assertThat(HebrewMonth.IYAR.getBiblicalValue(false), is(2));
        assertThat(HebrewMonth.SIVAN.getBiblicalValue(false), is(3));
        assertThat(HebrewMonth.TAMUZ.getBiblicalValue(false), is(4));
        assertThat(HebrewMonth.AV.getBiblicalValue(false), is(5));
        assertThat(HebrewMonth.ELUL.getBiblicalValue(false), is(6));
    }

    @Test
    public void getBiblicalValueLeapYear() {
        assertThat(HebrewMonth.TISHRI.getBiblicalValue(true), is(7));
        assertThat(HebrewMonth.HESHVAN.getBiblicalValue(true), is(8));
        assertThat(HebrewMonth.KISLEV.getBiblicalValue(true), is(9));
        assertThat(HebrewMonth.TEVET.getBiblicalValue(true), is(10));
        assertThat(HebrewMonth.SHEVAT.getBiblicalValue(true), is(11));
        assertThat(HebrewMonth.ADAR_I.getBiblicalValue(true), is(12));
        assertThat(HebrewMonth.ADAR_II.getBiblicalValue(true), is(13));
        assertThat(HebrewMonth.NISAN.getBiblicalValue(true), is(1));
        assertThat(HebrewMonth.IYAR.getBiblicalValue(true), is(2));
        assertThat(HebrewMonth.SIVAN.getBiblicalValue(true), is(3));
        assertThat(HebrewMonth.TAMUZ.getBiblicalValue(true), is(4));
        assertThat(HebrewMonth.AV.getBiblicalValue(true), is(5));
        assertThat(HebrewMonth.ELUL.getBiblicalValue(true), is(6));
    }

    @Test
    public void valueOfBiblicalNormalYear() {
        assertThat(HebrewMonth.valueOfBiblical(1, false), is(HebrewMonth.NISAN));
        assertThat(HebrewMonth.valueOfBiblical(2, false), is(HebrewMonth.IYAR));
        assertThat(HebrewMonth.valueOfBiblical(3, false), is(HebrewMonth.SIVAN));
        assertThat(HebrewMonth.valueOfBiblical(4, false), is(HebrewMonth.TAMUZ));
        assertThat(HebrewMonth.valueOfBiblical(5, false), is(HebrewMonth.AV));
        assertThat(HebrewMonth.valueOfBiblical(6, false), is(HebrewMonth.ELUL));
        assertThat(HebrewMonth.valueOfBiblical(7, false), is(HebrewMonth.TISHRI));
        assertThat(HebrewMonth.valueOfBiblical(8, false), is(HebrewMonth.HESHVAN));
        assertThat(HebrewMonth.valueOfBiblical(9, false), is(HebrewMonth.KISLEV));
        assertThat(HebrewMonth.valueOfBiblical(10, false), is(HebrewMonth.TEVET));
        assertThat(HebrewMonth.valueOfBiblical(11, false), is(HebrewMonth.SHEVAT));
        assertThat(HebrewMonth.valueOfBiblical(12, false), is(HebrewMonth.ADAR_II));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfBiblicalInvalid() {
        HebrewMonth.valueOfBiblical(13, false);
    }

    @Test
    public void valueOfBiblicalLeapYear() {
        assertThat(HebrewMonth.valueOfBiblical(1, true), is(HebrewMonth.NISAN));
        assertThat(HebrewMonth.valueOfBiblical(2, true), is(HebrewMonth.IYAR));
        assertThat(HebrewMonth.valueOfBiblical(3, true), is(HebrewMonth.SIVAN));
        assertThat(HebrewMonth.valueOfBiblical(4, true), is(HebrewMonth.TAMUZ));
        assertThat(HebrewMonth.valueOfBiblical(5, true), is(HebrewMonth.AV));
        assertThat(HebrewMonth.valueOfBiblical(6, true), is(HebrewMonth.ELUL));
        assertThat(HebrewMonth.valueOfBiblical(7, true), is(HebrewMonth.TISHRI));
        assertThat(HebrewMonth.valueOfBiblical(8, true), is(HebrewMonth.HESHVAN));
        assertThat(HebrewMonth.valueOfBiblical(9, true), is(HebrewMonth.KISLEV));
        assertThat(HebrewMonth.valueOfBiblical(10, true), is(HebrewMonth.TEVET));
        assertThat(HebrewMonth.valueOfBiblical(11, true), is(HebrewMonth.SHEVAT));
        assertThat(HebrewMonth.valueOfBiblical(12, true), is(HebrewMonth.ADAR_I));
        assertThat(HebrewMonth.valueOfBiblical(13, true), is(HebrewMonth.ADAR_II));
    }

    @Test
    public void getDisplayName() {
        assertThat(
            HebrewMonth.ADAR_I.getDisplayName(Locale.ROOT, false),
            is("Adar I"));
        assertThat(
            HebrewMonth.ADAR_I.getDisplayName(Locale.ROOT, true),
            is("Adar I"));
        assertThat(
            HebrewMonth.ADAR_II.getDisplayName(Locale.ROOT, false),
            is("Adar"));
        assertThat(
            HebrewMonth.ADAR_II.getDisplayName(Locale.ROOT, true),
            is("Adar II"));
    }

    @Test
    public void isValid() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.NISAN, 30).isValid(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I),
            is(false));
        assertThat(
            HebrewCalendar.of(5779, HebrewMonth.NISAN, 30).isValid(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I),
            is(true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withAdarIWhenNormalYear() {
        HebrewCalendar.of(5778, HebrewMonth.NISAN, 30).with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I);
    }

    @Test
    public void withAdarIWhenLeapYear() {
        assertThat(
            HebrewCalendar.of(5779, HebrewMonth.NISAN, 30).with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I),
            is(HebrewCalendar.of(5779, HebrewMonth.ADAR_I, 30)));
    }

}