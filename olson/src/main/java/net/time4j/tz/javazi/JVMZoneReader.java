/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (JVMZoneReader.java) is part of project Time4J.
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

package net.time4j.tz.javazi;

import net.time4j.ClockUnit;
import net.time4j.Month;
import net.time4j.PlainTime;
import net.time4j.Weekday;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.model.DaylightSavingRule;
import net.time4j.tz.model.GregorianTimezoneRule;
import net.time4j.tz.model.OffsetIndicator;
import net.time4j.tz.model.TransitionModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;


/**
 * <p>Liest die JVM-Zeitzonendatenbank im Ordner &quot;lib/zi&quot; aus. </p>
 *
 * @author  Meno Hochschild
 */
class JVMZoneReader {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final byte[] ZI_MAGIC_NUMBER = {
        (byte)'j',
        (byte)'a',
        (byte)'v',
        (byte)'a',
        (byte)'z',
        (byte)'i',
        (byte)'\0'
    };

    private static final byte ZI_FORMAT_VERSION = 0x01;

    private static final byte TAG_RAW_OFFSET = 1;
    private static final byte TAG_DST_SAVING = 2;
    private static final byte TAG_CHECK_SUM = 3;
    private static final byte TAG_TRANSITION = 4;
    private static final byte TAG_OFFSET_TABLE = 5;
    private static final byte TAG_SIMPLE_TIME_ZONE = 6;
    private static final byte TAG_GMT_OFFSET_WILL_CHANGE = 7;

    private static final byte[] ZM_MAGIC_NUMBER = {
        (byte)'j',
        (byte)'a',
        (byte)'v',
        (byte)'a',
        (byte)'z',
        (byte)'m',
        (byte)'\0'
    };

    private static final byte ZM_FORMAT_VERSION = 0x01;

    private static final byte TAG_TZ_DATA_VERSION = 68; // 0x44
    private static final byte TAG_ZONE_ALIASES = 67; // 0x43

    private static final Set<String> SOLAR;

    static {
        Set<String> solar = new HashSet<String>();
        solar.add("Asia/Riyadh87");
        solar.add("Asia/Riyadh88");
        solar.add("Asia/Riyadh89");
        SOLAR = Collections.unmodifiableSet(solar);
    }

    //~ Konstruktoren -----------------------------------------------------

    private JVMZoneReader() {
        // no instantiation
    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Liest die Versionskennung aus der Datei &quot;ZoneInfoMappings&quot;
     * ein. </p>
     *
     * @return  String
     */
    static String getVersion() {

        int index;
        int filesize;

        String version = "";

        try {

            byte[] bytes = readZoneFile("ZoneInfoMappings");

            if (bytes == null) {
                return ""; // Datei existiert nicht
            }

            for (index = 0; index < ZM_MAGIC_NUMBER.length; index++) {
                if (bytes[index] != ZM_MAGIC_NUMBER[index]) {
                    throw new IllegalStateException(
                        "Zone mapping file has wrong magic number!");
                }
            }

            if (bytes[index++] > ZM_FORMAT_VERSION) {
                throw new IllegalStateException(
                    "Zone mapping file has incompatible version ("
                    + bytes[index - 1]
                    + ")."
                );
            }

            filesize = bytes.length;

            while (index < filesize) {
                byte tag =
                    bytes[index++];
                int length =
                    ((bytes[index++] & 0xFF) << 8) + (bytes[index++] & 0xFF);

                if (tag == TAG_TZ_DATA_VERSION) {
                    version = new String(bytes, index, length - 1, "UTF-8");
                    break;
                } else {
                    index += length;
                }
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Corrupted zone mapping file.", ex);
        }

        return version;

    }

    /**
     * <p>Liefert eine Alias-Tabelle, in der die Schl&uuml;ssel alternative
     * Zonen-IDs darstellen (z.B. &quot;PST&quot;) und in der die zugeordneten
     * Werte kanonische Zonen-IDs sind (z.B. &quot;America/Los_Angeles&quot;).
     * </p>
     *
     * @return  Map
     */
    static Map<String, String> getZoneAliases() {

        int index;
        int filesize;

        Map<String, String> aliases = Collections.emptyMap();

        try {

            byte[] bytes = readZoneFile("ZoneInfoMappings");

            if (bytes == null) {
                return aliases; // Datei existiert nicht
            }

            for (index = 0; index < ZM_MAGIC_NUMBER.length; index++) {
                if (bytes[index] != ZM_MAGIC_NUMBER[index]) {
                    throw new IllegalStateException(
                        "Zone mapping file has wrong magic number!");
                }
            }

            if (bytes[index++] > ZM_FORMAT_VERSION) {
                throw new IllegalStateException(
                    "Zone mapping file has incompatible version ("
                    + bytes[index - 1]
                    + ")."
                );
            }

            filesize = bytes.length;

            while (index < filesize) {
                byte tag =
                    bytes[index++];
                int length =
                    ((bytes[index++] & 0xFF) << 8) + (bytes[index++] & 0xFF);

                if (tag == TAG_ZONE_ALIASES) {
                    int n = (bytes[index++] << 8) + (bytes[index++] & 0xFF);
                    aliases = new HashMap<String, String>(n);
                    for (int i = 0; i < n; i++) {
                        byte m = bytes[index++];
                        String alias = new String(bytes, index, m, "UTF-8");
                        index += m;
                        m = bytes[index++];
                        String realName = new String(bytes, index, m, "UTF-8");
                        index += m;
                        aliases.put(alias, realName);
                    }
                    break;
                } else {
                    index += length;
                }
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Corrupted zone mapping file.", ex);
        }

        return aliases;

    }

    /**
     * <p>Liest die JVM-Zonendaten zur angegebenen Zonen-ID ein. </p>
     *
     * @param   id      Zonen-ID
     * @return  erweiterte Zeitzonenhistorie oder {@code null}, wenn
     *          nicht gefunden
     * @throws  IllegalStateException bei Fehlern in der Dateiverarbeitung
     */
    static TransitionHistory getHistory(String id) {

        int index;
        int filesize;

        int rawOffset = 0;
        int dstSavings = 0;
        int crc32 = 0;
        long[] transitions = null;
        int[] offsets = null;
        int[] simpleTimeZoneParams = null;
        boolean willGMTOffsetChange = false;
        TransitionHistory model = null;

        try {

            byte[] bytes = readZoneFile(id);

            if (bytes == null) {
                return null; // Datei nicht gefunden
            }

            for (index = 0; index < ZI_MAGIC_NUMBER.length; index++) {
                if (bytes[index] != ZI_MAGIC_NUMBER[index]) {
                    throw new IllegalStateException(
                        "Zone file has wrong magic number: "
                        + id
                    );
                }
            }

            if (bytes[index++] > ZI_FORMAT_VERSION) {
                throw new IllegalStateException(
                    "Zone file has incompatible version ("
                    + bytes[index - 1]
                    + "): "
                    + id
                );
            }

            filesize = bytes.length;

            while (index < filesize) {
                byte tag =
                    bytes[index++];
                int length =
                    ((bytes[index++] & 0xFF) << 8) + (bytes[index++] & 0xFF);

                if (filesize < index + length) {
                    break;
                }

                switch (tag) {
                    case TAG_RAW_OFFSET:
                        {
                            int v = 0;
                            for (int i = 0; i < 4; i++) {
                                v = (v << 8) + ((bytes[index++] & 0xFF));
                            }
                            rawOffset = v / 1000; // Sekunden
                        }
                        break;

                    case TAG_DST_SAVING:
                        {
                            int v = 0;
                            for (int i = 0; i < 2; i++) {
                                v = (v << 8) + ((bytes[index++] & 0xFF));
                            }
                            dstSavings = v; // Sekunden
                        }
                        break;

                    case TAG_CHECK_SUM:
                        {
                            int v = 0;
                            for (int i = 0; i < 4; i++) {
                                v = (v << 8) + ((bytes[index++] & 0xFF));
                            }
                            crc32 = v;
                        }
                        break;

                    case TAG_TRANSITION:
                        {
                            int n = length / 8;
                            transitions = new long[n];
                            for (int i = 0; i < n; i ++) {
                                long v = 0;
                                for (int j = 0; j < 8; j++) {
                                    v = (v << 8) + ((bytes[index++] & 0xFF));
                                }
                                transitions[i] = v;
                            }
                        }
                        break;

                    case TAG_OFFSET_TABLE:
                        {
                            int n = length / 4;
                            offsets = new int[n];
                            for (int i = 0; i < n; i ++) {
                                int v = 0;
                                for (int j = 0; j < 4; j++) {
                                    v = (v << 8) + ((bytes[index++] & 0xFF));
                                }
                                offsets[i] = v / 1000; // Sekunden
                            }
                        }
                        break;

                    case TAG_SIMPLE_TIME_ZONE:
                        {
                            if (length != 32 && length != 40) {
                                throw new IllegalStateException(
                                    "Wrong SimpleTimeZone parameter size: "
                                    + length
                                );
                            }
                            int n = length / 4;
                            simpleTimeZoneParams = new int[n];
                            for (int i = 0; i < n; i++) {
                                int v = 0;
                                for (int j = 0; j < 4; j++) {
                                    v = (v << 8) + ((bytes[index++] & 0xFF));
                                }
                                simpleTimeZoneParams[i] = v;
                            }
                        }
                        break;

                    case TAG_GMT_OFFSET_WILL_CHANGE:
                        {
                            if (length != 1) {
                                System.err.println(
                                    "[JAVA ZI] Wrong byte length for: "
                                    + "TAG_GMT_OFFSET_WILL_CHANGE");
                            }
                            willGMTOffsetChange = (bytes[index++] == 1);
                        }
                        break;

                    default:
                        System.err.println(
                            "[JAVA ZI] Unknown tag <" + tag + "> ignored.");
                        index += length;
                }
            }

        } catch (Exception ex) {
            throw new IllegalStateException(
                "Corrupted zoneinfo file: " + id,
                ex
            );
        }

        if (index != filesize) {
            throw new IllegalStateException("Wrong zoneinfo file size: " + id);
        }

        try {

            ZonalTransition[] ots = null;

            if (
                (transitions != null)
                && (offsets != null)
            ) {
                ots = new ZonalTransition[transitions.length];
                int previous = rawOffset;
                int j = 0;

                for (int i = 0; i < transitions.length; i++) {
                    long value = transitions[i];
                    long ut = (value >> 12) / 1000; // Vorzeichen konservieren!
                    int total = offsets[(int) (value & 0x0FL)];

                    int didx = (int) ((value >>> 4) & 0x0FL);
                    int dst = ((didx == 0) ? 0 : offsets[didx]);

                    if (SOLAR.contains(id)) {
                        dst = 0; // workaround for negative dst
                    }

                    if (previous != total) {
                        ots[j] = new ZonalTransition(ut, previous, total, dst);
                        j++;
                        previous = total;
                    }
                }

                if (j < transitions.length) {
                    ZonalTransition[] copy = new ZonalTransition[j];
                    System.arraycopy(ots, 0, copy, 0, j);
                    ots = copy;
                }
            }

            if (simpleTimeZoneParams == null) {
                if (ots == null) {
                    ZonalOffset offset = ZonalOffset.ofTotalSeconds(rawOffset);
                    List<DaylightSavingRule> rules = Collections.emptyList();
                    model = TransitionModel.of(offset, rules);
                } else {
                    model = TransitionModel.of(Arrays.asList(ots));
                }
            } else {
                DaylightSavingRule startPattern;
                DaylightSavingRule endPattern;

                if (simpleTimeZoneParams.length == 10) {
                    startPattern =
                        createPattern(
                            simpleTimeZoneParams[0],
                            simpleTimeZoneParams[1],
                            simpleTimeZoneParams[2],
                            simpleTimeZoneParams[3],
                            getIndicator(simpleTimeZoneParams[4]),
                            dstSavings
                        );
                    endPattern =
                        createPattern(
                            simpleTimeZoneParams[5],
                            simpleTimeZoneParams[6],
                            simpleTimeZoneParams[7],
                            simpleTimeZoneParams[8],
                            getIndicator(simpleTimeZoneParams[9]),
                            0
                        );
                } else {
                    startPattern =
                        createPattern(
                            simpleTimeZoneParams[0],
                            simpleTimeZoneParams[1],
                            simpleTimeZoneParams[2],
                            simpleTimeZoneParams[3],
                            OffsetIndicator.WALL_TIME,
                            dstSavings
                        );
                    endPattern =
                        createPattern(
                            simpleTimeZoneParams[4],
                            simpleTimeZoneParams[5],
                            simpleTimeZoneParams[6],
                            simpleTimeZoneParams[7],
                            OffsetIndicator.WALL_TIME,
                            0
                        );
                }

                List<DaylightSavingRule> rules =
                    new ArrayList<DaylightSavingRule>(2);
                rules.add(startPattern);
                rules.add(endPattern);

                if (ots == null) {
                    // dieses Modell sollte normalerweise nicht vorkommen
                    model =
                        TransitionModel.of(
                            ZonalOffset.ofTotalSeconds(rawOffset),
                            rules);
                } else {
                    try {
                        model =
                            TransitionModel.of(
                                ZonalOffset.ofTotalSeconds(rawOffset),
                                Arrays.asList(ots),
                                rules);
                    } catch (IllegalArgumentException iae) {
                        // hack for handling broken data (removing last rules)
                        // sometimes inconsistent rules like in 2013h/Casablanca
                        model = TransitionModel.of(Arrays.asList(ots));
                        System.err.println("Inconsistent timezone data: " + id);
                    }
                }
            }

        } catch (RuntimeException re) {
            throw new IllegalStateException(
                "Corrupted zoneinfo file: " + id,
                re
            );
        }

        return model;

    }

    private static OffsetIndicator getIndicator(int simpleTimeZoneParam) {

        switch (simpleTimeZoneParam) {
            case SimpleTimeZone.UTC_TIME:
                return OffsetIndicator.UTC_TIME;
            case SimpleTimeZone.STANDARD_TIME:
                return OffsetIndicator.STANDARD_TIME;
            case SimpleTimeZone.WALL_TIME:
                return OffsetIndicator.WALL_TIME;
            default:
                throw new IllegalArgumentException(
                    "Not supported: " + simpleTimeZoneParam);
        }

    }

    // Datumsmuster gemäß simpleTimeZoneParams bestimmen
    private static DaylightSavingRule createPattern(
        int month,
        int dayOfMonth,
        int dayOfWeek,
        int timeOfDay,
        OffsetIndicator indicator,
        int dstSavings
    ) {

        PlainTime tod =
            PlainTime.midnightAtStartOfDay().plus(
                timeOfDay / 1000,
                ClockUnit.SECONDS);
        Month m = Month.valueOf(month + 1);

        if (dayOfWeek == 0) {
            return GregorianTimezoneRule.ofFixedDay(
                m,
                dayOfMonth,
                tod,
                indicator,
                dstSavings
            );
        } else if (dayOfWeek > 0) {
            return GregorianTimezoneRule.ofLastWeekday(
                m,
                Weekday.valueOf((dayOfWeek == 1) ? 7 : dayOfWeek - 1),
                tod,
                indicator,
                dstSavings
            );
        } else {
            int dow = -dayOfWeek;
            dow = ((dow == 1) ? 7 : dow - 1);

            if (dayOfMonth > 0) {
                return GregorianTimezoneRule.ofWeekdayAfterDate(
                    m,
                    Math.abs(dayOfMonth),
                    Weekday.valueOf(dow),
                    tod,
                    indicator,
                    dstSavings
                );
            } else {
                return GregorianTimezoneRule.ofWeekdayBeforeDate(
                    m,
                    Math.abs(dayOfMonth),
                    Weekday.valueOf(dow),
                    tod,
                    indicator,
                    dstSavings
                );
            }
        }

    }

    private static byte[] readZoneFile(String id) throws IOException {

        byte[] buffer = null;
        InputStream fis = null;
        String fileName = null;
        IOException ioe = null;

        try {

            char pathSeparator =
                System.getProperty("file.separator").charAt(0);

            StringBuilder sb = new StringBuilder(128);

            sb.append(System.getProperty("java.home"));
            sb.append(pathSeparator);
            sb.append("lib");
            sb.append(pathSeparator);
            sb.append("zi");
            sb.append(pathSeparator);

            if (pathSeparator == '/') {
                sb.append(id);
            } else {
                sb.append(id.replace('/', pathSeparator));
            }

            fileName = sb.toString();
            File file = new File(fileName);

            if (!file.exists()) {
                return null;
            } else if (!file.canRead()) {
                throw new IOException("Zone file not readable: " + fileName);
            }

            int filesize = (int) file.length();
            fis = new FileInputStream(file);
            byte[] bytes = new byte[filesize];

            if (fis.read(bytes) == filesize) {
                buffer = bytes;
            } else {
                throw new IOException("Cannot read zone file: " + fileName);
            }

        } catch (IOException ex) {

            ioe = ex;

        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                System.err.println(
                    "Closing of zone info file failed: " + ex.getMessage());
            }
        }

        if (ioe == null) {
            return buffer;
        } else {
            throw ioe;
        }

    }

}
