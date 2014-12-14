package net.time4j.clock;

import java.io.IOException;

public class DaytimeConnectorTest {

    public static void main(String[] args) throws IOException {
        DaytimeConnector dc = DaytimeConnector.ofNIST();
        System.out.println(
            "Time server configuration: " + dc.getNetTimeConfiguration());
        System.out.println(
            "Time server reply: " + dc.getRawTimestamp());

        dc.connect();
        System.out.println("Connection result: " + dc.currentTime());
    }

}
