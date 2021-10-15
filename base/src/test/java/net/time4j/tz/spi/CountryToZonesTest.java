package net.time4j.tz.spi;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class CountryToZonesTest {

    @Test
    public void loadingWithoutError() {
        String name = "data/zone1970.tab";
        Map<String, Set<String>> map = new TreeMap<>();
        ZoneNameProviderSPI.loadTerritories(map, name);
        assertThat(map.size(), is(247)); // tzdb-2021c
    }

}
