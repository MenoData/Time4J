package net.time4j.calendar.frenchrev;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FrenchRepublicanMonthTest {

    @Test
    public void valueOf() {
        assertThat(
            FrenchRepublicanMonth.valueOf(1),
            is(FrenchRepublicanMonth.VENDEMIAIRE));
        assertThat(
            FrenchRepublicanMonth.valueOf(2),
            is(FrenchRepublicanMonth.BRUMAIRE));
        assertThat(
            FrenchRepublicanMonth.valueOf(3),
            is(FrenchRepublicanMonth.FRIMAIRE));
        assertThat(
            FrenchRepublicanMonth.valueOf(4),
            is(FrenchRepublicanMonth.NIVOSE));
        assertThat(
            FrenchRepublicanMonth.valueOf(5),
            is(FrenchRepublicanMonth.PLUVIOSE));
        assertThat(
            FrenchRepublicanMonth.valueOf(6),
            is(FrenchRepublicanMonth.VENTOSE));
        assertThat(
            FrenchRepublicanMonth.valueOf(7),
            is(FrenchRepublicanMonth.GERMINAL));
        assertThat(
            FrenchRepublicanMonth.valueOf(8),
            is(FrenchRepublicanMonth.FLOREAL));
        assertThat(
            FrenchRepublicanMonth.valueOf(9),
            is(FrenchRepublicanMonth.PRAIRIAL));
        assertThat(
            FrenchRepublicanMonth.valueOf(10),
            is(FrenchRepublicanMonth.MESSIDOR));
        assertThat(
            FrenchRepublicanMonth.valueOf(11),
            is(FrenchRepublicanMonth.THERMIDOR));
        assertThat(
            FrenchRepublicanMonth.valueOf(12),
            is(FrenchRepublicanMonth.FRUCTIDOR));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfOutOfRange() {
        FrenchRepublicanMonth.valueOf(13);
    }

    @Test
    public void getValue() {
        assertThat(
            FrenchRepublicanMonth.VENDEMIAIRE.getValue(),
            is(1));
        assertThat(
            FrenchRepublicanMonth.BRUMAIRE.getValue(),
            is(2));
        assertThat(
            FrenchRepublicanMonth.FRIMAIRE.getValue(),
            is(3));
        assertThat(
            FrenchRepublicanMonth.NIVOSE.getValue(),
            is(4));
        assertThat(
            FrenchRepublicanMonth.PLUVIOSE.getValue(),
            is(5));
        assertThat(
            FrenchRepublicanMonth.VENTOSE.getValue(),
            is(6));
        assertThat(
            FrenchRepublicanMonth.GERMINAL.getValue(),
            is(7));
        assertThat(
            FrenchRepublicanMonth.FLOREAL.getValue(),
            is(8));
        assertThat(
            FrenchRepublicanMonth.PRAIRIAL.getValue(),
            is(9));
        assertThat(
            FrenchRepublicanMonth.MESSIDOR.getValue(),
            is(10));
        assertThat(
            FrenchRepublicanMonth.THERMIDOR.getValue(),
            is(11));
        assertThat(
            FrenchRepublicanMonth.FRUCTIDOR.getValue(),
            is(12));
    }

    @Test
    public void monthNamesFR() {
        assertThat(
            FrenchRepublicanMonth.VENDEMIAIRE.getDisplayName(Locale.FRENCH),
            is("vendémiaire"));
        assertThat(
            FrenchRepublicanMonth.BRUMAIRE.getDisplayName(Locale.FRENCH),
            is("brumaire"));
        assertThat(
            FrenchRepublicanMonth.FRIMAIRE.getDisplayName(Locale.FRENCH),
            is("frimaire"));
        assertThat(
            FrenchRepublicanMonth.NIVOSE.getDisplayName(Locale.FRENCH),
            is("nivôse"));
        assertThat(
            FrenchRepublicanMonth.PLUVIOSE.getDisplayName(Locale.FRENCH),
            is("pluviôse"));
        assertThat(
            FrenchRepublicanMonth.VENTOSE.getDisplayName(Locale.FRENCH),
            is("ventôse"));
        assertThat(
            FrenchRepublicanMonth.GERMINAL.getDisplayName(Locale.FRENCH),
            is("germinal"));
        assertThat(
            FrenchRepublicanMonth.FLOREAL.getDisplayName(Locale.FRENCH),
            is("floréal"));
        assertThat(
            FrenchRepublicanMonth.PRAIRIAL.getDisplayName(Locale.FRENCH),
            is("prairial"));
        assertThat(
            FrenchRepublicanMonth.MESSIDOR.getDisplayName(Locale.FRENCH),
            is("messidor"));
        assertThat(
            FrenchRepublicanMonth.THERMIDOR.getDisplayName(Locale.FRENCH),
            is("thermidor"));
        assertThat(
            FrenchRepublicanMonth.FRUCTIDOR.getDisplayName(Locale.FRENCH),
            is("fructidor"));
    }

    @Test
    public void monthNamesDE() {
        assertThat(
            FrenchRepublicanMonth.VENDEMIAIRE.getDisplayName(Locale.GERMAN),
            is("Weinlesemonat"));
        assertThat(
            FrenchRepublicanMonth.BRUMAIRE.getDisplayName(Locale.GERMAN),
            is("Nebelmonat"));
        assertThat(
            FrenchRepublicanMonth.FRIMAIRE.getDisplayName(Locale.GERMAN),
            is("Reifmonat"));
        assertThat(
            FrenchRepublicanMonth.NIVOSE.getDisplayName(Locale.GERMAN),
            is("Schneemonat"));
        assertThat(
            FrenchRepublicanMonth.PLUVIOSE.getDisplayName(Locale.GERMAN),
            is("Regenmonat"));
        assertThat(
            FrenchRepublicanMonth.VENTOSE.getDisplayName(Locale.GERMAN),
            is("Windmonat"));
        assertThat(
            FrenchRepublicanMonth.GERMINAL.getDisplayName(Locale.GERMAN),
            is("Keimmonat"));
        assertThat(
            FrenchRepublicanMonth.FLOREAL.getDisplayName(Locale.GERMAN),
            is("Blütenmonat"));
        assertThat(
            FrenchRepublicanMonth.PRAIRIAL.getDisplayName(Locale.GERMAN),
            is("Wiesenmonat"));
        assertThat(
            FrenchRepublicanMonth.MESSIDOR.getDisplayName(Locale.GERMAN),
            is("Erntemonat"));
        assertThat(
            FrenchRepublicanMonth.THERMIDOR.getDisplayName(Locale.GERMAN),
            is("Hitzemonat"));
        assertThat(
            FrenchRepublicanMonth.FRUCTIDOR.getDisplayName(Locale.GERMAN),
            is("Fruchtmonat"));
    }

    @Test
    public void dayNames() {
        assertThat(
            FrenchRepublicanMonth.VENDEMIAIRE.getDayNameInFrench(1),
            is("Raisin"));
        assertThat(
            FrenchRepublicanMonth.FRUCTIDOR.getDayNameInFrench(30),
            is("Panier"));
    }

}
