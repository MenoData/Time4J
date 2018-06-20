package net.time4j.i18n;

import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;

import java.util.Locale;
import java.util.MissingResourceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


// checks the sanity of ISO-properties-files
@RunWith(JUnit4.class)
public class IsoSanityTest {

    @Test
    public void checkLanguages() {

        IsoTextProviderSPI spi = IsoTextProviderSPI.SINGLETON;

        try {
            for (String s : IsoTextProviderSPI.getPrimaryLanguages()) {
                Locale loc = new Locale(s);
                for (TextWidth tw : TextWidth.values()) {
                    for (OutputContext oc : OutputContext.values()) {
                        assertThat(
                            spi.months("", loc, tw, oc, false),
                            notNullValue());
                    }
                }
                for (TextWidth tw : TextWidth.values()) {
                    for (OutputContext oc : OutputContext.values()) {
                        assertThat(
                            spi.quarters("", loc, tw, oc),
                            notNullValue());
                    }
                }
                for (TextWidth tw : TextWidth.values()) {
                    for (OutputContext oc : OutputContext.values()) {
                        assertThat(
                            spi.weekdays("", loc, tw, oc),
                            notNullValue());
                    }
                }
            }
        } catch (MissingResourceException mre) {
            fail(mre.getMessage());
        }

    }

}