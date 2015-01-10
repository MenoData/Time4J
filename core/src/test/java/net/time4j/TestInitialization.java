package net.time4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TestInitialization {

    @Test
    public void execute() {
        System.setProperty(
            "net.time4j.scale.leapseconds.path",
            "data/leapseconds2012.data");
    }

}