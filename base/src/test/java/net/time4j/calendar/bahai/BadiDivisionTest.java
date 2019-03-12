package net.time4j.calendar.bahai;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class BadiDivisionTest {

    @Test
    public void getDisplayNameEN() {
        assertThat(
            BadiMonth.BAHA.getDisplayName(Locale.ENGLISH),
            is("Bahá"));
        assertThat(
            BadiMonth.JALAL.getDisplayName(Locale.ENGLISH),
            is("Jalál"));
        assertThat(
            BadiMonth.JAMAL.getDisplayName(Locale.ENGLISH),
            is("Jamál"));
        assertThat(
            BadiMonth.AZAMAT.getDisplayName(Locale.ENGLISH),
            is("‘Aẓamat"));
        assertThat(
            BadiMonth.NUR.getDisplayName(Locale.ENGLISH),
            is("Núr"));
        assertThat(
            BadiMonth.RAHMAT.getDisplayName(Locale.ENGLISH),
            is("Raḥmat"));
        assertThat(
            BadiMonth.KALIMAT.getDisplayName(Locale.ENGLISH),
            is("Kalimát"));
        assertThat(
            BadiMonth.KAMAL.getDisplayName(Locale.ENGLISH),
            is("Kamál"));
        assertThat(
            BadiMonth.ASMA.getDisplayName(Locale.ENGLISH),
            is("Asmá"));
        assertThat(
            BadiMonth.IZZAT.getDisplayName(Locale.ENGLISH),
            is("‘Izzat"));
        assertThat(
            BadiMonth.MASHIYYAT.getDisplayName(Locale.ENGLISH),
            is("Mashíyyat"));
        assertThat(
            BadiMonth.ILM.getDisplayName(Locale.ENGLISH),
            is("‘Ilm"));
        assertThat(
            BadiMonth.QUDRAT.getDisplayName(Locale.ENGLISH),
            is("Qudrat"));
        assertThat(
            BadiMonth.QAWL.getDisplayName(Locale.ENGLISH),
            is("Qawl"));
        assertThat(
            BadiMonth.MASAIL.getDisplayName(Locale.ENGLISH),
            is("Masá’il"));
        assertThat(
            BadiMonth.SHARAF.getDisplayName(Locale.ENGLISH),
            is("Sharaf"));
        assertThat(
            BadiMonth.SULTAN.getDisplayName(Locale.ENGLISH),
            is("Sulṭán"));
        assertThat(
            BadiMonth.MULK.getDisplayName(Locale.ENGLISH),
            is("Mulk"));
        assertThat(
            BadiIntercalaryDays.AYYAM_I_HA.getDisplayName(Locale.ENGLISH),
            is("Ayyám-i-Há"));
        assertThat(
            BadiMonth.ALA.getDisplayName(Locale.ENGLISH),
            is("‘Alá"));
    }

    @Test
    public void getMeaningEN() {
        assertThat(
            BadiMonth.BAHA.getMeaning(Locale.ENGLISH),
            is("Splendor"));
        assertThat(
            BadiMonth.JALAL.getMeaning(Locale.ENGLISH),
            is("Glory"));
        assertThat(
            BadiMonth.JAMAL.getMeaning(Locale.ENGLISH),
            is("Beauty"));
        assertThat(
            BadiMonth.AZAMAT.getMeaning(Locale.ENGLISH),
            is("Grandeur"));
        assertThat(
            BadiMonth.NUR.getMeaning(Locale.ENGLISH),
            is("Light"));
        assertThat(
            BadiMonth.RAHMAT.getMeaning(Locale.ENGLISH),
            is("Mercy"));
        assertThat(
            BadiMonth.KALIMAT.getMeaning(Locale.ENGLISH),
            is("Words"));
        assertThat(
            BadiMonth.KAMAL.getMeaning(Locale.ENGLISH),
            is("Perfection"));
        assertThat(
            BadiMonth.ASMA.getMeaning(Locale.ENGLISH),
            is("Names"));
        assertThat(
            BadiMonth.IZZAT.getMeaning(Locale.ENGLISH),
            is("Might"));
        assertThat(
            BadiMonth.MASHIYYAT.getMeaning(Locale.ENGLISH),
            is("Will"));
        assertThat(
            BadiMonth.ILM.getMeaning(Locale.ENGLISH),
            is("Knowledge"));
        assertThat(
            BadiMonth.QUDRAT.getMeaning(Locale.ENGLISH),
            is("Power"));
        assertThat(
            BadiMonth.QAWL.getMeaning(Locale.ENGLISH),
            is("Speech"));
        assertThat(
            BadiMonth.MASAIL.getMeaning(Locale.ENGLISH),
            is("Questions"));
        assertThat(
            BadiMonth.SHARAF.getMeaning(Locale.ENGLISH),
            is("Honor"));
        assertThat(
            BadiMonth.SULTAN.getMeaning(Locale.ENGLISH),
            is("Sovereignty"));
        assertThat(
            BadiMonth.MULK.getMeaning(Locale.ENGLISH),
            is("Dominion"));
        assertThat(
            BadiIntercalaryDays.AYYAM_I_HA.getMeaning(Locale.ENGLISH),
            is("Intercalary Days"));
        assertThat(
            BadiMonth.ALA.getMeaning(Locale.ENGLISH),
            is("Loftiness"));
    }

    @Test
    public void getMeaningFallback() {
        Locale unknown = new Locale("xyz");
        assertThat(
            BadiMonth.BAHA.getMeaning(unknown),
            is("Bahá"));
        assertThat(
            BadiIntercalaryDays.AYYAM_I_HA.getMeaning(unknown),
            is("Ayyám-i-Há"));
    }

    @Test
    public void comparator() {
        assertThat(
            BadiDivision.comparator().compare(BadiMonth.ALA, BadiMonth.ALA) == 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiMonth.MULK, BadiMonth.MULK) == 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiMonth.ALA, BadiMonth.MULK) > 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiMonth.MULK, BadiMonth.ALA) < 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiMonth.MULK, BadiIntercalaryDays.AYYAM_I_HA) < 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiIntercalaryDays.AYYAM_I_HA, BadiMonth.MULK) > 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiIntercalaryDays.AYYAM_I_HA, BadiIntercalaryDays.AYYAM_I_HA) == 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiMonth.ALA, BadiIntercalaryDays.AYYAM_I_HA) > 0,
            is(true));
        assertThat(
            BadiDivision.comparator().compare(BadiIntercalaryDays.AYYAM_I_HA, BadiMonth.ALA) < 0,
            is(true));
    }

}
