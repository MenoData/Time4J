package net.time4j.clock;

import java.io.IOException;

public class NetTimeConnectorTest {

    public static void main(String[] args) throws IOException {
        DaytimeClock dc = DaytimeClock.NIST;
        System.out.println(
            "Time server configuration: " + dc.getNetTimeConfiguration());
        System.out.println(
            "Time server reply: " + dc.getRawTimestamp());

        dc.connect();
        System.out.println("Connection result: " + dc.currentTime());

        HttpClock hc = new HttpClock("www.google.com");
        hc.connect();
        System.out.println("Google: " + hc.currentTime());

        SntpConnector clock = new SntpConnector("ptbtime1.ptb.de");
        clock.connect();
        System.out.println("PTB: " + clock.currentTime());

    }

}
