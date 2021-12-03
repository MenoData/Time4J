/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HttpClock.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.clock;

import net.time4j.Moment;
import net.time4j.base.MathUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import static net.time4j.TemporalType.JAVA_UTIL_DATE;


/**
 * <p>Represents a connection to a web server via the HTTP-protocol in
 * order to evaluate the DATE-header of the HTTP-response. </p>
 *
 * <p>Note: This implementation is <i>threadsafe</i>. </p>
 *
 * @author      Meno Hochschild
 * @since       2.1
 */
/*[deutsch]
 * <p>Repr&auml;sentiert eine Verbindung zu einem Web-Server &uuml;ber
 * das HTTP-Protokoll, um den DATE-Header der HTTP-Antwort auszuwerten. </p>
 *
 * <p>Hinweis: Diese Implementierung ist <i>threadsafe</i>, also gegen
 * konkurrierende Zugriffe gesch&uuml;tzt. </p>
 *
 * @author      Meno Hochschild
 * @since       2.1
 */
public class HttpClock
    extends NetTimeConnector<NetTimeConfiguration> {

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new clock which connects to given web server. </p>
     *
     * @param   server      web server address (example: www.google.com)
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Uhr, die zum angegebenen Web-Server verbindet. </p>
     *
     * @param   server      web server address (example: www.google.com)
     */
    public HttpClock(String server) {
        super(new SimpleHttpConfiguration(server));

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    protected Moment doConnect() throws IOException, ParseException {

        final NetTimeConfiguration cfg = this.getNetTimeConfiguration();

        URL url = new URL(cfg.getTimeServerAddress());
        int timeoutMillis =
            MathUtils.safeMultiply(cfg.getConnectionTimeout(), 1000);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeoutMillis);
        conn.setReadTimeout(timeoutMillis);
        conn.setUseCaches(false);
        conn.setRequestMethod("HEAD");

        int responseCode = conn.getResponseCode();

        if (responseCode >= 200 && responseCode <= 399) {
            return JAVA_UTIL_DATE.translate(new Date(conn.getDate()));
        }

        throw new IOException("HTTP server status: " + responseCode);

    }

    @Override
    protected Class<NetTimeConfiguration> getConfigurationType() {

        return NetTimeConfiguration.class;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class SimpleHttpConfiguration
        implements NetTimeConfiguration {

        //~ Instanzvariablen ----------------------------------------------

        private final String server;

        //~ Konstruktoren -------------------------------------------------

        SimpleHttpConfiguration(String server) {
            super();

            if (!server.startsWith("http")) {
                this.server = "http://" + server;
            } else {
                this.server = server;
            }

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String getTimeServerAddress() {

            return this.server;

        }

        @Override
        public int getTimeServerPort() {

            return (this.server.startsWith("https:") ? 443 : 80);

        }

        @Override
        public int getConnectionTimeout() {

            return DEFAULT_CONNECTION_TIMEOUT;

        }

        @Override
        public int getClockShiftWindow() {

            return 0;

        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("SimpleHttpConfiguration:[server=");
            sb.append(this.server);
            sb.append(",port=");
            sb.append(this.getTimeServerPort());
            sb.append(']');
            return sb.toString();

        }

    }

}
