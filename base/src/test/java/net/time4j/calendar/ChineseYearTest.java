package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class ChineseYearTest {

    @Test
    public void forGregorian2637BC() {
        EastAsianYear eay = EastAsianYear.forGregorian(-2636);
        assertThat(eay.getCycle(), is(1));
        assertThat(eay.getYearOfCycle().getNumber(), is(1));
        assertThat(eay.getElapsedCyclicYears(), is(0));
        CyclicYear cy = eay.getYearOfCycle();
        assertThat(cy.inCycle(eay.getCycle()).getElapsedCyclicYears(), is(eay.getElapsedCyclicYears()));
    }

    @Test
    public void forGregorian1998() {
        EastAsianYear eay = EastAsianYear.forGregorian(1998);
        assertThat(eay.getCycle(), is(78));
        assertThat(eay.getYearOfCycle().getNumber(), is(15));
        assertThat(eay.getElapsedCyclicYears(), is(4634));
        CyclicYear cy = eay.getYearOfCycle();
        assertThat(cy.inCycle(eay.getCycle()).getElapsedCyclicYears(), is(eay.getElapsedCyclicYears()));
    }

    @Test
    public void forMinguo() {
        EastAsianYear eay = EastAsianYear.forMinguo(2);
        assertThat(eay.getCycle(), is(76));
        assertThat(eay.getYearOfCycle().getNumber(), is(50));
        assertThat(eay.getElapsedCyclicYears(), is(4634 - 85));
        CyclicYear cy = eay.getYearOfCycle();
        assertThat(cy.inCycle(eay.getCycle()).getElapsedCyclicYears(), is(eay.getElapsedCyclicYears()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void inQingDynastyAmbivalent() {
        CyclicYear.of(39).inQingDynasty(ChineseEra.QING_KANGXI_1662_1723);
    }

    @Test
    public void inQingDynasty() {
        for (int i = 1; i <= 60; i++) {
            if (i != 39) {
                assertThat(
                    CyclicYear.of(i).inQingDynasty(ChineseEra.QING_KANGXI_1662_1723).getYearOfCycle().getNumber(),
                    is(i));
            }
        }
    }

    @Test
    public void roll() {
        assertThat(CyclicYear.of(5).roll(-7), is(CyclicYear.of(58)));
        assertThat(CyclicYear.of(5).roll(59), is(CyclicYear.of(4)));
        assertThat(CyclicYear.of(5).roll(61), is(CyclicYear.of(6)));
    }

}