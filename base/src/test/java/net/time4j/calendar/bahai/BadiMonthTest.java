package net.time4j.calendar.bahai;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class BadiMonthTest {

    @Test
    public void valueOf() {
        assertThat(
            BadiMonth.valueOf(1),
            is(BadiMonth.BAHA));
        assertThat(
            BadiMonth.valueOf(2),
            is(BadiMonth.JALAL));
        assertThat(
            BadiMonth.valueOf(3),
            is(BadiMonth.JAMAL));
        assertThat(
            BadiMonth.valueOf(4),
            is(BadiMonth.AZAMAT));
        assertThat(
            BadiMonth.valueOf(5),
            is(BadiMonth.NUR));
        assertThat(
            BadiMonth.valueOf(6),
            is(BadiMonth.RAHMAT));
        assertThat(
            BadiMonth.valueOf(7),
            is(BadiMonth.KALIMAT));
        assertThat(
            BadiMonth.valueOf(8),
            is(BadiMonth.KAMAL));
        assertThat(
            BadiMonth.valueOf(9),
            is(BadiMonth.ASMA));
        assertThat(
            BadiMonth.valueOf(10),
            is(BadiMonth.IZZAT));
        assertThat(
            BadiMonth.valueOf(11),
            is(BadiMonth.MASHIYYAT));
        assertThat(
            BadiMonth.valueOf(12),
            is(BadiMonth.ILM));
        assertThat(
            BadiMonth.valueOf(13),
            is(BadiMonth.QUDRAT));
        assertThat(
            BadiMonth.valueOf(14),
            is(BadiMonth.QAWL));
        assertThat(
            BadiMonth.valueOf(15),
            is(BadiMonth.MASAIL));
        assertThat(
            BadiMonth.valueOf(16),
            is(BadiMonth.SHARAF));
        assertThat(
            BadiMonth.valueOf(17),
            is(BadiMonth.SULTAN));
        assertThat(
            BadiMonth.valueOf(18),
            is(BadiMonth.MULK));
        assertThat(
            BadiMonth.valueOf(19),
            is(BadiMonth.ALA));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfOutOfRange() {
        BadiMonth.valueOf(0);
    }

    @Test
    public void getValue() {
        assertThat(
            BadiMonth.BAHA.getValue(),
            is(1));
        assertThat(
            BadiMonth.JALAL.getValue(),
            is(2));
        assertThat(
            BadiMonth.JAMAL.getValue(),
            is(3));
        assertThat(
            BadiMonth.AZAMAT.getValue(),
            is(4));
        assertThat(
            BadiMonth.NUR.getValue(),
            is(5));
        assertThat(
            BadiMonth.RAHMAT.getValue(),
            is(6));
        assertThat(
            BadiMonth.KALIMAT.getValue(),
            is(7));
        assertThat(
            BadiMonth.KAMAL.getValue(),
            is(8));
        assertThat(
            BadiMonth.ASMA.getValue(),
            is(9));
        assertThat(
            BadiMonth.IZZAT.getValue(),
            is(10));
        assertThat(
            BadiMonth.MASHIYYAT.getValue(),
            is(11));
        assertThat(
            BadiMonth.ILM.getValue(),
            is(12));
        assertThat(
            BadiMonth.QUDRAT.getValue(),
            is(13));
        assertThat(
            BadiMonth.QAWL.getValue(),
            is(14));
        assertThat(
            BadiMonth.MASAIL.getValue(),
            is(15));
        assertThat(
            BadiMonth.SHARAF.getValue(),
            is(16));
        assertThat(
            BadiMonth.SULTAN.getValue(),
            is(17));
        assertThat(
            BadiMonth.MULK.getValue(),
            is(18));
        assertThat(
            BadiMonth.ALA.getValue(),
            is(19));
    }

}
