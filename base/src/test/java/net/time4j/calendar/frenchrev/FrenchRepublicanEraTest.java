package net.time4j.calendar.frenchrev;

import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FrenchRepublicanEraTest {

    @Test
    public void values() {
        assertThat(
            FrenchRepublicanEra.values().length,
            is(1));
    }

    @Test
    public void eraNames() {
        assertThat(
            FrenchRepublicanEra.REPUBLICAN.getDisplayName(Locale.FRENCH, TextWidth.WIDE),
            is("république française"));
        assertThat(
            FrenchRepublicanEra.REPUBLICAN.getDisplayName(Locale.FRENCH, TextWidth.ABBREVIATED),
            is("RF"));
    }

}
