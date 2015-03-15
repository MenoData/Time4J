/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimezoneRepositoryCompiler.java) is part of project Time4J.
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

package net.time4j.tool;

import net.time4j.ClockUnit;
import net.time4j.Month;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.base.GregorianMath;
import net.time4j.base.MathUtils;
import net.time4j.engine.EpochDays;
import net.time4j.tz.TransitionHistory;
import net.time4j.tz.ZonalOffset;
import net.time4j.tz.ZonalTransition;
import net.time4j.tz.model.DaylightSavingRule;
import net.time4j.tz.model.GregorianTimezoneRule;
import net.time4j.tz.model.OffsetIndicator;
import net.time4j.tz.model.TransitionModel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import static net.time4j.ClockUnit.SECONDS;


/**
 * <p>Compiler for translating timezone data in original format invented
 * by Arthur David Olson. </p>
 *
 * <p>The tz-database is currently maintained (in March 2015) by the
 * organization <a href="http://www.iana.org/time-zones">IANA</a>. File
 * archives have the name &quot;tzdata&lt;version&gt;.tar.gz&quot;, where
 * the version is composed of a 4-digit year and a small letter a-z. Example:
 * &quot;tzdata2011n.tar.gz&quot;. </p>
 *
 * <p>The existence of an editable directory with the name &quot;tzrepo&quot;
 * in the classpath is provided if not specified otherwise. This directory
 * either contains the original files (&quot;tzdata&lt;version&gt;.tar.gz&quot;)
 * packed as tar.gz or the unpacked contents as subdirectories. This compiler
 * will create the translated binaries - in the same directory - with the name
 * &quot;tzdata&lt;version&gt;.repository&quot; which can then be loaded
 * by the class {@code TimezoneRepositoryProvider}. The directory
 * &quot;tzrepo&quot; is editable if it is not an archive file and if
 * the application has write access. </p>
 *
 * @author  Meno Hochschild
 * @since   2.3
 */
/*[deutsch]
 * <p>Compiler zum &Uuml;bersetzen von Zeitzonen-Dateien im Olson-Format. </p>
 *
 * <p>Die TZ-Datenbank wird zur Zeit (Stand M&auml;rz 2015) von der Organisation
 * <a href="http://www.iana.org/time-zones">IANA</a> verwaltet. Dateiarchive
 * haben den Namen &quot;tzdata&lt;version&gt;.tar.gz&quot;, wobei die Version
 * aus einer 4-stelligen Jahreszahl und einem Buchstaben a-z zusammengesetzt
 * ist. Beispiel: &quot;tzdata2011n.tar.gz&quot;. </p>
 *
 * <p>Vorausgesetzt wird die Existenz eines editierbaren Verzeichnisses mit
 * dem Namen &quot;tzrepo&quot; im Klassenpfad wenn nicht explizit angegeben.
 * Dieses Verzeichnis enth&auml;lt entweder die Original-Dateien
 * (&quot;tzdata&lt;version&gt;.tar.gz&quot;) im tar.gz-Format verpackt
 * oder die ausgepackten Inhalte als Unterverzeichnisse. Dieser Compiler
 * erstellt im selben Verzeichnis die &Uuml;bersetzungsdateien
 * (&quot;tzdata&lt;version&gt;.repository&quot;), welche dann zur Laufzeit vom
 * {@code TimezoneRepositoryProvider} ausgewertet werden k&ouml;nnen. Editierbar
 * ist das Verzeichnis &quot;tzrepo&quot;, wenn es nicht in einer Archivdatei
 * liegt und das Programm Schreibrechte besitzt. </p>
 *
 * @author  Meno Hochschild
 * @since   2.3
 */
public class TimezoneRepositoryCompiler {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String WORK_DIRECTORY_NAME = "tzrepo";
    private static final String TZDATA = "tzdata";
    private static final String TAR_GZ_EXTENSION = ".tar.gz";
    private static final String LF = System.getProperty("line.separator");

    private static final String[] LONG_MONTHS =
        {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
    private static final String[] SHORT_MONTHS =
        {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String[] LONG_DAYS =
        {"MONDAY", "TUESDAY", "WEDNESDAY",
            "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
    private static final String[] SHORT_DAYS =
        {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private static final List<String> FILES_IGNORED_ALWAYS;
    private static final List<String> FILES_IGNORED_BY_COMPILER;

    static {
        List<String> tmp1 = new ArrayList<String>();
        tmp1.add("yearistype.sh");
        tmp1.add("factory");
        tmp1.add("systemv");
        tmp1.add("pacificnew");
        tmp1.add("backzone");
        tmp1.add("solar87");
        tmp1.add("solar88");
        tmp1.add("solar89");
        tmp1.add("checklinks.awk");
        tmp1.add("checktab.awk");
        tmp1.add("leapseconds.awk");
        tmp1.add("Makefile");
        tmp1.add("zoneinfo2tdf.pl");
        FILES_IGNORED_ALWAYS = Collections.unmodifiableList(tmp1);

        List<String> tmp2 = new ArrayList<String>();
        tmp2.add("Theory");
        tmp2.add("CONTRIBUTING");
        tmp2.add("NEWS");
        tmp2.add("README");
        FILES_IGNORED_BY_COMPILER = Collections.unmodifiableList(tmp2);
    }

    private static final Comparator<RuleLine> RC = new RuleComparator();

    //~ Instanzvariablen --------------------------------------------------

    private final File workdir;
    private final boolean verbose;
    private final boolean lmt;

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Creates a new instance which works on the editable directory with
     * the name &quot;tzrepo&quot; on the class path. </p>
     *
     * @throws  IllegalStateException if the directory &quot;tzrepo&quot; does
     *          not exist or cannot be found
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz, die auf dem editierbaren Verzeichnis mit
     * dem Namen &quot;tzrepo&quot; im Klassenpfad arbeitet. </p>
     *
     * @throws  IllegalStateException wenn das vorausgesetzte Verzeichnis
     *          nicht vorhanden ist oder nicht gefunden wird
     */
    public TimezoneRepositoryCompiler() {
        super();

        try {
            this.workdir = getDefaultWorkDirectory();
            this.verbose = false;
            this.lmt = false;
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }

    }

    /**
     * <p>Creates a new instance for the given working directory. </p>
     *
     * <p>The second argument controls if LMT-lines will be taken into
     * account. Such lines are invented for the purpose of evaluating the
     * local mean time in second precision related to the archetypical
     * city/location of a given timezone. Default is {@code false} because
     * such an information is usually wrong except for the archetypical
     * cities/locations themselves. </p>
     *
     * @param   workdir     working directory
     * @param   lmt         taking into account LMT-timezone data?
     * @throws  IllegalArgumentException if the given directory does not exist
     *          or is not a directory
     */
    /*[deutsch]
     * <p>Erzeugt eine neue Instanz auf dem angegebenen Arbeitsverzeichnis. </p>
     *
     * <p>Mit dem zweiten Argument k&ouml;nnen auch LMT-Zeilen in der
     * Zeitzonendatenbank ber&uuml;cksichtigt werden. Das ist eine Erfindung,
     * wonach in der gegebenen Zeitzone die sekundengenaue mittlere Ortszeit
     * des archetypischen Zeitzonenorts gegolten h&auml;tte. Standard ist
     * {@code false}, weil das au&szlig;er f&uuml;r die archetypischen Orte
     * selbst in der Regel falsch ist. </p>
     *
     * @param   workdir     Arbeitsverzeichnis
     * @param   lmt         LMT-Zonendaten ber&uuml;cksichtigen?
     * @throws  IllegalArgumentException wenn das Argument nicht existiert
     *          oder kein Verzeichnis darstellt
     */
    public TimezoneRepositoryCompiler(
        File workdir,
        boolean lmt
    ) {
        this(workdir, false, lmt);

    }

    private TimezoneRepositoryCompiler(
        File workdir,
        boolean verbose,
        boolean lmt
    ) {
        super();

        this.workdir = workdir;
        this.verbose = verbose;
        this.lmt = lmt;

        if (
            (workdir == null)
            || !workdir.exists()
        ) {
            throw new IllegalArgumentException(
                "Work directory does not exist: " + workdir);
        } else if (!workdir.isDirectory()) {
            throw new IllegalArgumentException(
                "Directory required: " + workdir);
        }

    }

    //~ Methoden ----------------------------------------------------------

	/**
	 * <p>Main executable method which accepts command line options for
     * controlling if the timezone data shall only be unpacked or compiled. </p>
     *
     * <p>Recognized options are: </p>
     *
     * <dl>
     *  <dt>-help</dt>
     *  <dd>Print a help message</dd>
     *  <dt>-verbose</dt>
     *  <dd>Print details during execution</dd>
     *  <dt>-workdir</dt>
     *  <dd>Set working directory which contains timezone data
     *  by giving next command line argument as absolute path</dd>
     *  <dt>-unpack</dt>
     *  <dd>Unpack timezone archive to subdirectory</dd>
     *  <dt>-compile</dt>
     *  <dd>Compile timezone data (archives or subdirectories)</dd>
     *  <dt>-version</dt>
     *  <dd>Use only given timezone version instead of newest available version
     *  (example: -version 2011n)</dd>
     *  <dt>-lmt</dt>
     *  <dd>Include LMT zone entries during compilation</dd>
     * </dl>
     *
     * @param   args    command line parameters
     * @throws  IllegalArgumentException if the working directory is wrong
     * @throws  IOException in case of any I/O-failure
	 */
	/*[deutsch]
	 * <p>Stand-alone-Methode gesteuert &uuml;ber die Kommandozeilenparameter,
     * die bestimmen, ob nur ausgepackt oder stattdessen kompiliert werden
     * soll. </p>
     *
     * <p>Unterst&uuml;tzte Kommandozeilenparameter sind: </p>
     *
     * <dl>
     *  <dt>-help</dt>
     *  <dd>Print a help message</dd>
     *  <dt>-verbose</dt>
     *  <dd>Print details during execution</dd>
     *  <dt>-workdir</dt>
     *  <dd>Set working directory which contains timezone data
     *  by giving next command line argument as absolute path</dd>
     *  <dt>-unpack</dt>
     *  <dd>Unpack timezone archive to subdirectory</dd>
     *  <dt>-compile</dt>
     *  <dd>Compile timezone data (archives or subdirectories)</dd>
     *  <dt>-version</dt>
     *  <dd>Use only given timezone version instead of newest available version
     *  (example: -version 2011n)</dd>
     *  <dt>-lmt</dt>
     *  <dd>Include LMT zone entries during compilation</dd>
     * </dl>
     *
     * @param   args    Kommandozeilenparameter
     * @throws  IllegalArgumentException bei falschem Arbeitsverzeichnis
     * @throws  IOException bei Zugriffsfehlern
	 */
	public static void main(String[] args) throws IOException {

        if (
            (args == null)
            || (args.length == 0)
        ) {
            printOptions();
            return;
        }

        boolean verbose = false;
        String version = null;
        File workdir = null;
        boolean helpMode = false;
        boolean unpackMode = false;
        boolean compileMode = false;
        boolean lmt = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.charAt(0) != '-') {
                break;
            } else if (arg.equals("-help")) {
                helpMode = true;
            } else if (arg.equals("-unpack")) {
                unpackMode = true;
            } else if (arg.equals("-compile")) {
                compileMode = true;
            } else if (arg.equals("-verbose")) {
                verbose = true;
            } else if (arg.equals("-lmt")) {
                lmt = true;
            } else if (
                arg.equals("-version")
                && (version == null)
                && (++i < args.length)
            ) {
                version = args[i];
            } else if (
                arg.equals("-workdir")
                && (workdir == null)
                && (++i < args.length)
            ) {
                workdir = new File(args[i]);
            } else {
                System.out.println("Unrecognized option: " + arg);
            }
        }

        if (helpMode) {
            printOptions();
            return;
        }

        if (workdir == null) {
            workdir = getDefaultWorkDirectory();
        }

        TimezoneRepositoryCompiler tc =
            new TimezoneRepositoryCompiler(workdir, verbose, lmt);

        if (unpackMode) {
            if (version == null) {
                tc.unpack();
            } else {
                tc.unpack(version);
            }
        }

        if (compileMode) {
            if (version == null) {
                tc.compile();
            } else {
                tc.compile(version);
            }
        }

	}

    /**
     * <p>Determines the working directory of this instance. </p>
     *
     * @return  directory as {@code File}-object
     */
    /*[deutsch]
     * <p>Gibt das verwendete Basisverzeichnis an. </p>
     *
     * @return  Verzeichnis als {@code File}-Objekt
     */
    public File getWorkingDirectory() {

        return this.workdir;

    }

    /**
     * <p>Unpacks a tar-gz-archive of the newest version into a subdirectory
     * (floating-mode). </p>
     *
     * @throws  IOException
     */
    /*[deutsch]
     * <p>Packt ein tar-gz-Archiv der neuesten Version als Unterverzeichnis
     * aus (floating-mode). </p>
     *
     * @throws  IOException bei Zugriffsfehlern
     */
    public void unpack() throws IOException {

        String version = this.getNewestArchiveVersion(new VersionComparator());

        if (version == null) {
            throw new FileNotFoundException(
                "Archive not found in: " + this.workdir);
        } else {
            this.unpack(version);
        }

    }

    /**
     * <p>Unpacks a tar-gz-archive of given version into a subdirectory. </p>
     *
     * @param   version     timezone version (for example &quot;2015a&quot;)
     * @throws  IOException
     */
    /*[deutsch]
     * <p>Packt ein tar-gz-Archiv der angegebenen Version als Unterverzeichnis
     * aus. </p>
     *
     * @param   version     TZ-Version (zum Beispiel &quot;2015a&quot;)
     * @throws  IOException bei Zugriffsfehlern
     */
    public void unpack(String version) throws IOException {

        File archiveFile =
            new File(this.workdir, TZDATA + version + TAR_GZ_EXTENSION);

        if (!archiveFile.exists()) {
            throw new FileNotFoundException(archiveFile.toString());
        }

        File subdir = new File(this.workdir, TZDATA + version);

        if (
            !subdir.exists()
            && !subdir.mkdir()
        ) {
            throw new IOException(
                "Cannot create subdirectory for unpacked version: " + subdir);
        }

        if (this.verbose) {
            System.out.println(
                "Start unpacking of version " + version + " ...");
        }

        Map<String, String> contents = loadArchive(archiveFile);
        Writer writer = null;

        try {
            for (Map.Entry<String, String> entry : contents.entrySet()) {
                String name = entry.getKey();
                if (FILES_IGNORED_ALWAYS.contains(name)) {
                    continue;
                }
                File file = new File(subdir, name);
                if (this.verbose) {
                    System.out.println("Unpacking " + name + " to " + file);
                }
                writer =
                    new BufferedWriter(
                        new OutputStreamWriter(
                            new FileOutputStream(file), "UTF-8"));
                writer.write(entry.getValue());
                writer.close();
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
        }

        System.out.println("Version \"" + version + "\" unpacked.");

    }

    /**
     * <p>Compiles the timezone data of the newest version (floating mode)
     * where subdirectories are preferred compared with archive files. </p>
     *
     * @throws  IOException
     */
    /*[deutsch]
     * <p>Kompiliert die Zeitzonendaten in der neuesten Version (floating mode),
     * wobei Unterverzeichnisse Vorrang vor Archiven haben. </p>
     *
     * @throws  IOException bei Zugriffsfehlern
     */
    public void compile() throws IOException {

        Comparator<String> comp = new VersionComparator();
        String aversion = this.getNewestArchiveVersion(comp);
        String dversion = this.getNewestDirectoryVersion(comp);

        String version;
        boolean directory;

        if (aversion == null) {
            if (dversion == null) {
                throw new FileNotFoundException(
                    "Time zone data not found in: " + this.workdir);
            } else {
                version = dversion;
                directory = true;
            }
        } else if (dversion == null) {
            version = aversion;
            directory = false;
        } else if (comp.compare(aversion, dversion) <= 0) {
            version = dversion;
            directory = true;
        } else {
            version = aversion;
            directory = false;
        }

        String name = TZDATA + version;

        if (!directory) {
            name += TAR_GZ_EXTENSION;
        }

        this.compile(new File(this.workdir, name), version);

    }

    /**
     * <p>Compiles the timezone data of given version where subdirectories
     * are preferred compared with archive files. </p>
     *
     * @param   version     timezone version to be compiled
     * @throws  IOException
     */
    /*[deutsch]
     * <p>Kompiliert die Zeitzonendaten in der angegebenen Version,
     * wobei Unterverzeichnisse Vorrang vor Archiven haben. </p>
     *
     * @param   version     zu kompilierende Version
     * @throws  IOException bei Zugriffsfehlern
     */
    public void compile(String version) throws IOException {

        File file = new File(this.workdir, TZDATA + version);

        if (
            !file.exists()
            || !file.isDirectory()
        ) {
            file = new File(this.workdir, TZDATA + version + TAR_GZ_EXTENSION);

            if (!file.exists()) {
                throw new FileNotFoundException(file.toString());
            }
        }

        this.compile(file, version);

    }

    private void compile(
        File file,
        String version
    ) throws IOException {

        if (this.verbose) {
            System.out.println(
                "Start compiling of version " + version + " ...");
        }

        Map<String, String> contents;

        if (file.isDirectory()) {
            contents = loadDirectory(file);
        } else {
            contents = loadArchive(file);
        }

        Map<String, List<ZoneLine>> zones =
            new TreeMap<String, List<ZoneLine>>();
        Map<String, List<RuleLine>> rules =
            new HashMap<String, List<RuleLine>>();
        List<LinkLine> links = new ArrayList<LinkLine>();
        List<LeapLine> leaps = new ArrayList<LeapLine>();
        PlainDate expires = PlainDate.axis().getMinimum();
        boolean expireMode = false;

        for (Map.Entry<String, String> e : contents.entrySet()) {

            String key = e.getKey();

            if (
                FILES_IGNORED_ALWAYS.contains(key)
                || FILES_IGNORED_BY_COMPILER.contains(key)
            ) {
                continue;
            } else if (this.verbose && !key.endsWith(".tab")) {
                System.out.println(
                    "Parsing content of \""
                    + key
                    + "\" in process...");
            }

            if (key.equals("leap-seconds.list")) {
                expireMode = true;
            }

            BufferedReader br = // Zeilenumbrüche herausfiltern
                new BufferedReader(new StringReader(e.getValue()));
            String zoneID = null;
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                int n = line.length();
                StringBuilder sb = new StringBuilder(n);
                boolean quotation = false;

                // Kommentare ausschneiden
                for (int i = 0; i < n; i++) {
                    char c = line.charAt(i);

                    if (c == '\"') {
                        quotation = !quotation;
                    } else if (quotation) {
                        // ignore char
                    } else if (c == '#') {
                        if (
                            expireMode
                            && (i + 1 < n)
                            && (line.charAt(i + 1) == '@')
                        ) {
                            long ntp = Long.parseLong(line.substring(2).trim());
                            expires =
                                PlainTimestamp.of(1900, 1, 1, 0, 0)
                                .plus(ntp, ClockUnit.SECONDS)
                                .getCalendarDate();
                            expireMode = false;
                        }
                        break;
                    } else if (Character.isWhitespace(c)) {
                        if (
                            (sb.length() == 0)
                            || (sb.charAt(sb.length() - 1) != '\t')
                        ) {
                            sb.append('\t');
                        }
                    } else {
                        sb.append(c);
                    }
                }

                line = sb.toString().trim();

                if (!line.isEmpty()) {
                    String[] fields = line.split("\t");

                    if (fields[0].equals("Rule")) {
                        if (!fields[4].equals("-")) { // TYPE-Feld
                            if (this.verbose) {
                                System.out.println(
                                    "Ignoring line with filled type info: "
                                    + key
                                    + " => "
                                    + line
                                );
                            }
                        } else {
                            String ruleName = fields[1];
                            List<RuleLine> ruleLines = rules.get(ruleName);

                            if (ruleLines == null) {
                                ruleLines = new ArrayList<RuleLine>();
                                rules.put(ruleName, ruleLines);
                            }

                            ruleLines.add(new RuleLine(fields));
                            Collections.sort(ruleLines, RC);
                        }
                    } else if (fields[0].equals("Zone")) {
                        zoneID = fields[1];
                        List<ZoneLine> zoneLines = new ArrayList<ZoneLine>();
                        ZoneLine zl = new ZoneLine(zoneID, fields);
                        zoneLines.add(zl);
                        zones.put(zoneID, zoneLines);
                        if (zl.indicator == null) {
                            zoneID = null; // last zone line
                        }
                    } else if (zoneID != null) { // continuation zone line
                        ZoneLine zl = new ZoneLine(zoneID, fields);
                        zones.get(zoneID).add(zl);
                        if (zl.indicator == null) {
                            zoneID = null; // last zone line
                        }
                    } else if (fields[0].equals("Link")) {
                        links.add(new LinkLine(fields));
                    } else if (fields[0].equals("Leap")) {
                        leaps.add(new LeapLine(fields));
                    }
                }
            }
        }

        File subdir = new File(this.workdir, TZDATA + version);

        if (
            !subdir.exists()
            && !subdir.mkdir()
        ) {
            throw new IOException(
                "Cannot create subdirectory for compiled version: " + subdir);
        }

        ObjectOutputStream oos =
            new ObjectOutputStream(
                new FileOutputStream(
                    new File(subdir, TZDATA + ".repository")));
        try {
            oos.writeByte('t');
            oos.writeByte('z');
            oos.writeByte('r');
            oos.writeByte('e');
            oos.writeByte('p');
            oos.writeByte('o');
            oos.writeUTF(version);
            this.compile(oos, zones, rules);
            this.compileLinks(oos, zones.keySet(), links);
            this.compileLeapSeconds(oos, leaps, expires);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                // ignored
            }
        }

        if (this.verbose) {
            int rcount = 0;
            for (List<?> list : rules.values()) {
                rcount += list.size();
            }
            int zcount = 0;
            for (List<?> list : zones.values()) {
                zcount += list.size();
            }
            System.out.println("Size of tz-repository: " + zones.size());
            System.out.println("Count of parsed zone lines = " + zcount);
            System.out.println("Count of parsed rule lines = " + rcount);
            System.out.println("Count of parsed link lines = " + links.size());
            System.out.println("Count of parsed leap lines = " + leaps.size());
        }

        System.out.println("Version \"" + version + "\" compiled.");

    }

    private void compile(
        ObjectOutputStream oos,
        Map<String, List<ZoneLine>> zoneMap,
        Map<String, List<RuleLine>> ruleMap
    ) throws IOException {

        oos.writeInt(zoneMap.size());

        for (Map.Entry<String, List<ZoneLine>> zoneEntry : zoneMap.entrySet()) {
            String zoneID = zoneEntry.getKey();
            List<ZonalTransition> transitions =
                new ArrayList<ZonalTransition>();
            List<DaylightSavingRule> rules =
                new ArrayList<DaylightSavingRule>();
            ZoneLine previous = null;
            int initialOffset = 0;
            int dstOffset = 0;
            boolean hasLMT = true;
            int lmtCount = 0;

            for (ZoneLine current : zoneEntry.getValue()) {
                if (previous == null) { // first line
                    if (current.fixedSaving != null) {
                        dstOffset = current.fixedSaving.intValue();
                    } else if (current.ruleName != null) {
                        List<RuleLine> rlines = ruleMap.get(current.ruleName);
                        RuleLine first = null;
                        for (RuleLine rline : rlines) {
                            if ((first == null) || (rline.from < first.from)) {
                                first = rline;
                            }
                        }
                        dstOffset =
                            addTransitions(
                                transitions,
                                rules,
                                current,
                                dstOffset,
                                rlines,
                                first.from,
                                Long.MIN_VALUE);
                    }
                    initialOffset = current.rawOffset + dstOffset;
                } else {
                    int oldDst = dstOffset;
                    int shift = getShift(previous, oldDst);
                    long startTime = previous.until - shift;
                    int startYear = getStartYear(startTime);

                    if (current.fixedSaving != null) {
                        dstOffset = current.fixedSaving.intValue();
                    } else if (current.ruleName != null) {
                        dstOffset =
                            getRuleOffset(
                                ruleMap.get(current.ruleName),
                                previous.rawOffset,
                                oldDst,
                                startYear,
                                startTime);
                    }

                    if (
                        (previous.rawOffset != current.rawOffset)
                        || (dstOffset != oldDst)
                    ) {
                        addTransition(
                            transitions,
                            new ZonalTransition(
                                startTime,
                                previous.rawOffset + oldDst,
                                current.rawOffset + dstOffset,
                                dstOffset));
                    }

                    if (current.ruleName != null) {
                        dstOffset =
                            addTransitions(
                                transitions,
                                rules,
                                current,
                                dstOffset,
                                ruleMap.get(current.ruleName),
                                startYear,
                                startTime);
                    }
                }

                previous = current;
                hasLMT = hasLMT && current.format.equals("LMT");
                if (hasLMT) {
                    lmtCount++;
                }
            }

            if (!this.lmt) {
                while ((lmtCount > 0) && !transitions.isEmpty()) {
                    ZonalTransition lmtTransition = transitions.remove(0);
                    initialOffset = lmtTransition.getTotalOffset();
                    lmtCount--;
                }
            }

            try {
                TransitionHistory history =
                    TransitionModel.of(
                        ZonalOffset.ofTotalSeconds(initialOffset),
                        transitions,
                        rules);
                oos.writeUTF(zoneID);
                oos.writeObject(history);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException(
                    "Inconsistent data found for: " + zoneID,
                    iae);
            }
        }

    }

    private void compileLinks(
        ObjectOutputStream oos,
        Set<String> zones,
        List<LinkLine> links
    ) throws IOException {

        List<String> sortedZones = new ArrayList<String>(zones);
        Map<String, String> aliases = new HashMap<String, String>();
        Map<String, Integer> normalized = new HashMap<String, Integer>();

        for (LinkLine link : links) {
            aliases.put(link.from, link.to);
        }

        for (String alias : aliases.keySet()) {
            String value;
            String key = alias;

            while ((value = aliases.get(key)) != null) {
                key = value;
            }

            int index = Collections.binarySearch(sortedZones, key);

            if (index < 0) {
                throw new IllegalArgumentException(
                    "Link target not found: " + alias);
            }

            normalized.put(alias, index);
        }

        oos.writeShort(normalized.size());

        for (String alias : normalized.keySet()) {
            oos.writeUTF(alias);
            oos.writeShort(normalized.get(alias).shortValue());
        }

    }

    private void compileLeapSeconds(
        ObjectOutputStream oos,
        List<LeapLine> leaps,
        PlainDate expires
    ) throws IOException {

        oos.writeShort(leaps.size());

        for (LeapLine ll : leaps) {
            oos.writeShort(ll.year);
            oos.writeByte(ll.month);
            oos.writeByte(ll.day);
            oos.writeByte(ll.shift);
        }

        oos.writeObject(expires);

    }

    private static int getRuleOffset(
        List<RuleLine> rules,
        int rawOffset,
        int oldDst,
        int year,
        long startTime
    ) {

        List<RuleLine> lines = new ArrayList<RuleLine>(rules.size());

        for (RuleLine rline : rules) {
            if ((rline.from <= year) && (rline.to >= year)) {
                lines.add(rline);
            }
        }

        for (int i = lines.size() - 1; i >= 0; i--) {
            RuleLine rline = lines.get(i);
            int prevSavings = (
                (i > 0)
                ? lines.get(i - 1).pattern.getSavings()
                : oldDst);
            int shift =
                getShift(rline.pattern.getIndicator(), rawOffset, prevSavings);
            if (startTime >= rline.getPosixTime(year, shift)) {
                return rline.pattern.getSavings();
            }
        }

        return (lines.isEmpty() ? 0 : oldDst);

    }

    private static int addTransitions(
        List<ZonalTransition> transitions,
        List<DaylightSavingRule> rules,
        ZoneLine zoneLine,
        int dstOffset,
        List<RuleLine> ruleLines,
        int startYear,
        long startTime
    ) {

        boolean exit = false;
        int endYear = startYear;

        if (zoneLine.indicator == null) { // last line
            for (RuleLine line : ruleLines) {
                if (line.to == Integer.MAX_VALUE) {
                    if (line.from > endYear) {
                        endYear = line.from;
                    }
                    rules.add(line.pattern);
                } else if (line.to > endYear) {
                    endYear = line.to;
                }
            }
        } else { // continuation line
            endYear = getEstimatedYear(zoneLine);
        }

        for (int year = startYear - 1; !exit && (year <= endYear + 1); year++) {
            for (RuleLine line : ruleLines) { // ascending order within a year
                if (
                    (line.from > year)
                    || (line.to < year)
                ) {
                    continue;
                }

                int oldDst = dstOffset;
                int ruleShift =
                    getShift(
                        line.pattern.getIndicator(),
                        zoneLine.rawOffset,
                        oldDst);
                long tt = line.getPosixTime(year, ruleShift);
                long endTime = Long.MAX_VALUE;

                if (zoneLine.indicator != null) { // continuation line
                    endTime = zoneLine.until - getShift(zoneLine, oldDst);
                }

                if (tt < startTime) {
                    continue;
                } else if (tt >= endTime) {
                    exit = true;
                    break;
                } else {
                    dstOffset = line.pattern.getSavings();
                }

                addTransition(
                    transitions,
                    new ZonalTransition(
                        tt,
                        zoneLine.rawOffset + oldDst,
                        zoneLine.rawOffset + dstOffset,
                        dstOffset));
            }
        }

        return dstOffset;

    }

    private static void addTransition(
        List<ZonalTransition> transitions,
        ZonalTransition newTransition
    ) {

        if (transitions.isEmpty()) {
            transitions.add(newTransition);
            return;
        }

        int index = transitions.size() - 1;
        ZonalTransition last = transitions.get(index);

        int total = newTransition.getTotalOffset();
        int dst = newTransition.getDaylightSavingOffset();
        long tt = newTransition.getPosixTime();

        if (last.getPosixTime() == tt) {
            // TODO: Wird das wirklich gebraucht?
            ZonalTransition zt =
                new ZonalTransition(
                    last.getPosixTime(),
                    last.getPreviousOffset(),
                    total,
                    dst);
            transitions.set(index, zt);
        } else if (
            (last.getTotalOffset() != total)
            || (last.getDaylightSavingOffset() != dst)
        ) {
            transitions.add(newTransition);
        }

    }

    private static int getEstimatedYear(ZoneLine zoneLine) {

        return getStartYear(zoneLine.until);

    }

    private static int getStartYear(long time) {

        long mjd =
            EpochDays.MODIFIED_JULIAN_DATE.transform(
                MathUtils.floorDivide(time, 86400),
                EpochDays.UNIX);
        return GregorianMath.readYear(GregorianMath.toPackedDate(mjd));

    }

    private static int getShift(
        ZoneLine line,
        int dstOffset
    ) {

        return getShift(line.indicator, line.rawOffset, dstOffset);

    }

    private static int getShift(
        OffsetIndicator indicator,
        int rawOffset,
        int dstOffset
    ) {

        switch (indicator) {
            case UTC_TIME:
                return 0;
            case STANDARD_TIME:
                return rawOffset;
            case WALL_TIME:
                return rawOffset + dstOffset;
            default:
                throw new UnsupportedOperationException(indicator.name());
        }

    }

    private static Map<String, String> loadArchive(File archive)
        throws IOException {

        if (!archive.getName().endsWith("tar.gz")) {
            return Collections.emptyMap();
        }

        Map<String, String> contents = new HashMap<String, String>();
        TarInputStream inStream = null;

        try {
			inStream =
                new TarInputStream(
                    new GZIPInputStream(
                        new FileInputStream(archive)));

            TarEntry entry;

            while ((entry = inStream.getNextEntry()) != null) {
                byte[] data = new byte[2048];
                int count = 0;

                if (entry.isNormalFile()) {
                    ByteArrayOutputStream bos =
                        new ByteArrayOutputStream();

                    while ((count = inStream.read(data)) != -1) {
                        bos.write(data, 0, count);
                    }

                    contents.put(entry.getName(), bos.toString("UTF-8"));
                    bos.close();
                }
            }

        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
			inStream = null;
        }

        return contents;

    }

    private static Map<String, String> loadDirectory(File directory)
        throws IOException {

        if (!directory.isDirectory()) {
            return Collections.emptyMap();
        }

        InputStream inStream = null;
        Map<String, String> contents = new HashMap<String, String>();

        try {
            for (File subdir : directory.listFiles()) {
                byte[] data = new byte[2048];
                int count = 0;

                if (subdir.isFile()) {
                    inStream =
                        new BufferedInputStream(
                            new FileInputStream(subdir));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    while ((count = inStream.read(data)) != -1) {
                        bos.write(data, 0, count);
                    }

                    contents.put(subdir.getName(), bos.toString("UTF-8"));
                    bos.close();
                }
            }

        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
            }
			inStream = null;
        }

        return contents;

    }

    private static File getDefaultWorkDirectory() throws IOException {

        ClassLoader loader = TimezoneRepositoryCompiler.class.getClassLoader();
        URL url = loader.getResource(WORK_DIRECTORY_NAME);

        try {
            if (url != null) {
                URI uri = url.toURI();
                if (uri.getScheme().equals("file")) {
                    File dir = new File(uri);
                    if (dir.isDirectory()) {
                        return dir;
                    }
                }
            }
        } catch (URISyntaxException use) {
            throw new IOException(use);
        }

        throw new FileNotFoundException(
            "Editable timezone directory \""
            + WORK_DIRECTORY_NAME
            + "\" not found in classpath."
        );

    }

    private String getNewestArchiveVersion(Comparator<String> comp) {

        List<String> versions = new ArrayList<String>();

        for (File file : this.workdir.listFiles()) {
            if (file.isFile()) {
                String name = file.getName();
                if (
                    name.startsWith(TZDATA)
                    && name.endsWith(TAR_GZ_EXTENSION)
                    && (name.length() == 18)
                ) {
                    versions.add(name.substring(6, 11));
                }
            }
        }

        if (versions.isEmpty()) {
            return null;
        } else {
            Collections.sort(versions, comp);
            return versions.get(0);
        }

    }

    private String getNewestDirectoryVersion(Comparator<String> comp) {

        List<String> versions = new ArrayList<String>();

        for (File file : this.workdir.listFiles()) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (
                    name.startsWith(TZDATA)
                    && (name.length() == 11)
                ) {
                    versions.add(name.substring(6));
                }
            }
        }

        if (versions.isEmpty()) {
            return null;
        } else {
            Collections.sort(versions, comp);
            return versions.get(0);
        }

    }

    private static void printOptions() {

        StringBuilder sb = new StringBuilder(512);
        sb.append("Usage of timezone compiler with command line options:");
        sb.append(LF);

        sb.append("-help      Print this usage message");
        sb.append(LF);

        sb.append("-verbose   Print details during execution");
        sb.append(LF);

        sb.append("-workdir   Set working directory which contains timezone ");
        sb.append("data by giving next command line argument as absolute path");
        sb.append(LF);

        sb.append("-unpack    Unpack timezone archive to subdirectory");
        sb.append(LF);

        sb.append("-compile   Compile timezone data ");
        sb.append("(archives or subdirectories)");
        sb.append(LF);

        sb.append("-version   Use only given timezone version ");
        sb.append("instead of newest available version ");
        sb.append("(example: -version 2011n)");
        sb.append(LF);

        sb.append("-lmt       Include LMT zone entries during compilation");
        sb.append(LF);

        System.out.println(sb.toString());

    }

    private static int getMonth(String field) {

        int found = -1;

        for (int i = 0; i < 12; i++) {
            if (field.equalsIgnoreCase(SHORT_MONTHS[i])) {
                found = i;
                break;
            }
        }

        if (found == -1) {
            String ucase = field.toUpperCase(Locale.US);

            for (int i = 0; i < 12; i++) {
                if (LONG_MONTHS[i].startsWith(ucase)) {
                    found = i;
                    break;
                }
            }
        }

        if (found == -1) {
            throw new IllegalArgumentException(
                "Not representable as month: " + field);
        } else {
            return found + 1;
        }

    }

    private static Weekday getWeekday(String field) {

        int found = -1;

        for (int i = 0; i < 7; i++) {
            if (field.equalsIgnoreCase(SHORT_DAYS[i])) {
                found = i;
                break;
            }
        }

        if (found == -1) {
            String ucase = field.toUpperCase(Locale.US);

            for (int i = 0; i < 7; i++) {
                if (LONG_DAYS[i].startsWith(ucase)) {
                    found = i;
                    break;
                }
            }
        }

        if (found == -1) {
            throw new IllegalArgumentException(
                "Not representable as weekday: " + field);
        } else {
            return Weekday.valueOf(found + 1);
        }

    }

    private static int[] getTimeInfo(String timeOfDay) {

        int[] ret = new int[2];
        ret[0] = 0; // Vorgabe
        ret[1] = 2; // WALL TIME als Standard

        int len = timeOfDay.length();
        char c = timeOfDay.charAt(len - 1);

        if (
            (len == 1)
            && (c == '-')
        ) {
            return ret;
        }

        // Großbuchstaben eigentlich nicht zulässig => Toleranz-Einstellung
        switch (c) {
            case 'u':
            case 'U':
            case 'g':
            case 'G':
            case 'z':
            case 'Z':
                timeOfDay = timeOfDay.substring(0, len - 1);
                ret[1] = 0; // UNIX-Zeit
                break;
            case 's':
            case 'S':
                timeOfDay = timeOfDay.substring(0, len - 1);
                ret[1] = 1; // Standardzeit
                break;
            case 'w':
            case 'W':
                timeOfDay = timeOfDay.substring(0, len - 1);
                break;
            default:
                if (!isDigit(c)) {
                    throw new IllegalArgumentException(timeOfDay);
                }
        }

        if (timeOfDay.equals("-")) {
            return ret;
        }

        StringTokenizer st = new StringTokenizer(timeOfDay, ":");
        int h = 0;
        int m = 0;
        int s = 0;

        for (int i = 0; st.hasMoreTokens(); i++) {
            String part = st.nextToken();

            if (part.charAt(0) == '-') {
                throw new IllegalArgumentException(timeOfDay);
            }

            switch (i) {
                case 0:
                    h = Integer.parseInt(part);
                    break;
                case 1:
                    m = Integer.parseInt(part);
                    break;
                case 2:
                    s = Integer.parseInt(part);
                    break;
                default:
                    // break;
            }

        }

        ret[0] =  h * 3600 + m * 60 + s;
        return ret;

    }

    private static int getOffset(String field) {

        boolean negative = (field.charAt(0) == '-');

        if (negative) {
            if (field.length() == 1) {
                return 0;
            } else {
                field = field.substring(1);
            }
        }

        StringTokenizer st = new StringTokenizer(field, ":");
        int h = 0;
        int m = 0;
        int s = 0;

        for (int i = 0; st.hasMoreTokens(); i++) {
            String part = st.nextToken();

            if (part.charAt(0) == '-') {
                throw new IllegalArgumentException(field);
            }

            switch (i) {
                case 0:
                    h = Integer.parseInt(part);
                    break;
                case 1:
                    m = Integer.parseInt(part);
                    break;
                case 2:
                    s = Integer.parseInt(part);
                    break;
                default:
                    // break;
            }

        }

        int ret =  h * 3600 + m * 60 + s;
        return (negative ? -ret : ret);

    }

    private static boolean isDigit(char c) {

        return ((c >= '0') && (c <= '9'));

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class VersionComparator
        implements Comparator<String> {

        //~ Methoden ------------------------------------------------------

        @Override
        public int compare(String o1, String o2) {

            try {

                int n1 = Integer.parseInt(o1.substring(0, 4));
                int n2 = Integer.parseInt(o2.substring(0, 4));

                if (n1 == n2) {
                    return o2.substring(4).compareTo(o1.substring(4));
                } else if (n1 < n2) {
                    return 1;
                } else {
                    return -1;
                }

            } catch (RuntimeException re) {

                ClassCastException cce =
                    new ClassCastException(
                        "Unexpected version format: ("
                        + o1
                        + " | "
                        + o2
                        + ")"
                    );

                cce.initCause(re);
                throw cce;

            }

        }

    }

    private static class RuleLine {

        //~ Instanzvariablen ----------------------------------------------

        private final String name;
        private final int from;
        private final int to;
        private final DaylightSavingRule pattern;
        private final String letter;

        //~ Konstruktoren -------------------------------------------------

        RuleLine(String[] fields) {
            super();

            try {
                this.name = fields[1];

                if ("minimum".startsWith(fields[2])) {
                    this.from = Integer.MIN_VALUE;
                } else {
                    this.from = Integer.parseInt(fields[2]);
                }

                if ("maximum".startsWith(fields[3])) {
                    this.to = Integer.MAX_VALUE;
                } else if (fields[3].equals("only")) {
                    this.to = this.from;
                } else {
                    this.to = Integer.parseInt(fields[3]);
                }

                int month = getMonth(fields[5]);
                String on = fields[6];
                int[] timeInfo = getTimeInfo(fields[7]);
                OffsetIndicator idx = OffsetIndicator.values()[timeInfo[1]];
                int dst = getOffset(fields[8]);
                this.pattern = getPattern(month, on, timeInfo[0], idx, dst);

                this.letter = (fields[9].equals("-") ? "" : fields[9]);

            } catch (RuntimeException re) {
                throw new IllegalStateException(
                    "Actual rule line: " + Arrays.toString(fields),
                    re
                );
            }

        }

        //~ Methoden ------------------------------------------------------

        long getPosixTime(
            int year,
            int offset
        ) {

            PlainDate date = this.pattern.getDate(year);
            PlainTimestamp tsp = date.at(this.pattern.getTimeOfDay());
            return tsp.at(ZonalOffset.ofTotalSeconds(offset)).getPosixTime();

        }

        private static DaylightSavingRule getPattern(
            int month,
            String on,
            int timeOfDay,
            OffsetIndicator indicator,
            int dst
        ) {

            Month m = Month.valueOf(month);
            PlainTime time =
                PlainTime.midnightAtStartOfDay().plus(timeOfDay, SECONDS);

            if (isDigit(on.charAt(0))) {

                return GregorianTimezoneRule.ofFixedDay(
                    m,
                    Integer.parseInt(on),
                    time,
                    indicator,
                    dst
                );

            } else if (on.startsWith("last")) {

                return GregorianTimezoneRule.ofLastWeekday(
                    m,
                    getWeekday(on.substring(4)),
                    time,
                    indicator,
                    dst);

            } else {

                int pos = -1;

                for (int i = 0, n = on.length(); i < n; i++) {
                    char c = on.charAt(i);
                    if (
                        (c == '>')
                        || (c == '<')
                    ) {
                        pos = i;
                        break;
                    }
                }

                if (
                    (pos == -1)
                    || (on.charAt(pos + 1) != '=')
                ) {
                    throw new IllegalArgumentException(on);
                }

                int dayOfMonth = Integer.parseInt(on.substring(pos + 2));
                Weekday dayOfWeek = getWeekday(on.substring(0, pos));

                if (on.charAt(pos) == '>') {
                    return GregorianTimezoneRule.ofWeekdayAfterDate(
                        m,
                        dayOfMonth,
                        dayOfWeek,
                        time,
                        indicator,
                        dst);
                } else {
                    return GregorianTimezoneRule.ofWeekdayBeforeDate(
                        m,
                        dayOfMonth,
                        dayOfWeek,
                        time,
                        indicator,
                        dst);
                }

            }

        }

    }

    private static class RuleComparator
        implements Comparator<RuleLine> {

        //~ Methoden ------------------------------------------------------

        @Override
        public int compare(
            RuleLine o1,
            RuleLine o2
        ) {

            if (!o1.name.equals(o2.name)) {
                throw new ClassCastException("Different rule names.");
            }

            int prototypeYear = 2000; // must be a leap year
            long s1 = o1.getPosixTime(prototypeYear, 0);
            long s2 = o2.getPosixTime(prototypeYear, 0);

            if (s1 < s2) {
                return -1;
            } else if (s1 > s2) {
                return 1;
            } else {
                return 0;
            }

        }

    }

    private static class ZoneLine {

        //~ Instanzvariablen ----------------------------------------------

        private final int rawOffset;
        private final String ruleName;
        private final Integer fixedSaving;
        private final String format;
        private final long until;
        private final OffsetIndicator indicator;

        //~ Konstruktoren -------------------------------------------------

        ZoneLine(
            String id,
            String[] fields
        ) {
            super();

            int startIndex = (fields[0].equals("Zone") ? 2 : 0);

            try {
                String rawField = fields[startIndex + 0];
                if (rawField.equals("-")) {
                    throw new IllegalArgumentException(
                        "Undefined raw offset: " + id);
                } else {
                    this.rawOffset = getOffset(rawField);
                }

                String rulesave = fields[startIndex + 1];

                if (Character.isLetter(rulesave.charAt(0))) {
                    this.ruleName = rulesave;
                    this.fixedSaving = null;
                } else {
                    this.ruleName = null;
                    this.fixedSaving = Integer.valueOf(getOffset(rulesave));
                }

                this.format = fields[startIndex + 2];

                int year = Integer.MAX_VALUE;
                int month = 1;
                int day = 1;
                int timeOfDay = 0;
                OffsetIndicator tl = OffsetIndicator.WALL_TIME;

                for (int i = startIndex + 3; i < fields.length; i++) {
                    String f = fields[i];

                    switch (i - startIndex - 3) {
                        case 0:
                            year = Integer.parseInt(f);
                            break;
                        case 1:
                            month = getMonth(f);
                            break;
                        case 2:
                            day = getDayOfMonth(year, month, f);
                            break;
                        case 3:
                            int[] timeInfo = getTimeInfo(f);
                            tl = OffsetIndicator.values()[timeInfo[1]];
                            timeOfDay = timeInfo[0];
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Unknown UNTIL-field: " + f);
                    }
                }

                if (fields.length == startIndex + 3) {
                    this.until = Long.MAX_VALUE; // UNTIL is missing
                    this.indicator = null; // marks last Zone-line
                } else {
                    this.until =
                        EpochDays.UNIX.transform(
                            GregorianMath.toMJD(year, month, day),
                            EpochDays.MODIFIED_JULIAN_DATE) * 86400L
                        + timeOfDay;
                    this.indicator = tl;
                }

            } catch (RuntimeException re) {
                throw new IllegalStateException(
                    "[" + id + "] Actual zone line: " + Arrays.toString(fields),
                    re
                );
            }

        }

        //~ Methoden ------------------------------------------------------

        private static int getDayOfMonth(
            int year,
            int month,
            String on
        ) {

            if (isDigit(on.charAt(0))) {

                return Integer.parseInt(on);

            } else if (on.startsWith("last")) {

                int weekday = getWeekday(on.substring(4)).getValue();
                int lastDay = GregorianMath.getLengthOfMonth(year, month);
                int lastW = GregorianMath.getDayOfWeek(year, month, lastDay);
                int delta = (lastW - weekday);

                if (delta < 0) {
                    delta += 7;
                }

                return (lastDay - delta);

            } else {

                int pos = -1;

                for (int i = 0, n = on.length(); i < n; i++) {
                    char c = on.charAt(i);
                    if (
                        (c == '>')
                        || (c == '<')
                    ) {
                        pos = i;
                        break;
                    }
                }

                if (
                    (pos == -1)
                    || (on.charAt(pos + 1) != '=')
                ) {
                    throw new IllegalArgumentException(on);
                }

                int dayOfMonth = Integer.parseInt(on.substring(pos + 2));
                int dayOfWeek = getWeekday(on.substring(0, pos)).getValue();
                int ref = GregorianMath.getDayOfWeek(year, month, dayOfMonth);

                if (ref == dayOfWeek) {
                    return dayOfMonth;
                }

                boolean after = (on.charAt(pos) == '>');
                int delta = (ref - dayOfWeek);
                int sgn = -1;

                if (after) {
                    delta = -delta;
                    sgn = 1;
                }

                if (delta < 0) {
                    delta += 7;
                }

                return dayOfMonth + delta * sgn;

            }

        }

    }

    private static class LinkLine {

        //~ Instanzvariablen ----------------------------------------------

        private final String from;
        private final String to;

        //~ Konstruktoren -------------------------------------------------

        LinkLine(String[] fields) {
            super();

            this.from = fields[2];
            this.to = fields[1];

        }

    }

    private static class LeapLine {

        //~ Instanzvariablen ----------------------------------------------

        private final int year;
        private final int month;
        private final int day;
        private final byte shift;

        //~ Konstruktoren -------------------------------------------------

        LeapLine(String[] fields) {
            super();

            try {
                this.year = Integer.parseInt(fields[1]);
                this.month = getMonth(fields[2]);
                this.day = Integer.parseInt(fields[3]);
            } catch (RuntimeException re) {
                throw new IllegalStateException(Arrays.toString(fields), re);
            }

            String corr = fields[5];

            if (corr.equals("+")) {
                this.shift = 1;
                if (!fields[4].equals("23:59:60")) {
                    throw new IllegalArgumentException(
                        "Unexpected leap time: " + Arrays.toString(fields));
                }
            } else if (corr.equals("-")) {
                this.shift = -1;
                if (!fields[4].equals("23:59:58")) {
                    throw new IllegalArgumentException(
                        "Unexpected leap time: " + Arrays.toString(fields));
                }
            } else {
                throw new IllegalArgumentException(
                    "Unexpected correction: " + Arrays.toString(fields));
            }

            if (!"STATIONARY".startsWith(fields[6].toUpperCase(Locale.US))) {
                throw new UnsupportedOperationException(
                    "Leap line not stationary: " + Arrays.toString(fields));
            }

        }

    }

}
