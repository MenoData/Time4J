package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static net.time4j.Month.MAY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(JUnit4.class)
public class FormattableTest {

    @Test
    public void dateFormat() {
        assertThat(
            String.format(Locale.ENGLISH, "%1$tb %1$te, %1$tY", PlainDate.of(1995, MAY, 23)),
            is("May 23, 1995")
        );
    }

    @Test
    public void timeFormat() {
        assertThat(
            String.format(Locale.ENGLISH, "%1$tr", PlainTime.of(17, 45, 23)),
            is("05:45:23 PM")
        );
    }

}
