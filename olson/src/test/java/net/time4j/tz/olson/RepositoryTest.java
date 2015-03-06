package net.time4j.tz.olson;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class RepositoryTest {

    @Test
    public void findRepository2012c() throws IOException {
        String propertyKey = "net.time4j.tz.repository.version";
        String old = System.getProperty(propertyKey);
        try {
            System.setProperty(propertyKey, "2012c");
            TimezoneRepositoryProviderSPI p =
                new TimezoneRepositoryProviderSPI();
            assertThat(p.getVersion(), is("2012c"));
        } finally {
            if (old == null) {
                System.clearProperty(propertyKey);
            } else {
                System.setProperty(propertyKey, old);
            }
        }
    }

}