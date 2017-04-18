/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2017 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Nengo.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoException;
import net.time4j.engine.EpochDays;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarText;
import net.time4j.format.Leniency;
import net.time4j.format.TextElement;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.Iso8601Format;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.URI;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * The system of Japanese eras from AD 701 until today.
 *
 * <p>It is possible to register a new nengo by setting the system property
 * &quot;net.time4j.calendar.japanese.supplemental.era&quot;. Its value is
 * a sequence of key-value-pairs (without any space or tab). The keys
 * &quot;name&quot; (given in romaji), &quot;kanji&quot; (japanese - 2 chars) and
 * &quot;since&quot; (ISO-8601-date) are mandatory. If necessary, the optional
 * keys &quot;chinese&quot; (2 chars), &quot;korean&quot; or &quot;russian&quot; can
 * be specified, too. Example: </p>
 *
 * <p>&quot;-Dnet.time4j.calendar.japanese.supplemental.era name=newEra,kanji=KK,since=9999-12-31&quot;</p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
/*[deutsch]
 * Das System japanischer &Auml;ra-Bezeichnungen vom Jahr AD 701 bis heute.
 *
 * <p>Die Registrierung eines neuen Nengo ist m&ouml;glich, indem die <i>system property</i>
 * &quot;net.time4j.calendar.japanese.supplemental.era&quot; gesetzt wird. Ihr Wert ist eine
 * Folge von Schl&uuml;ssel-Wert-Paaren ohne Leerzeichen oder Tabulatoren. Die Schl&uuml;ssel
 * &quot;name&quot; (in Romaji), &quot;kanji&quot; (japanisch - 2 Zeichen) und &quot;since&quot;
 * (ISO-8601-Datum) sind Pflichtangaben. Falls notwendig, k&ouml;nnen die optionalen Schl&uuml;ssel
 * &quot;chinese&quot; (chinesisch - 2 Zeichen), &quot;korean&quot; (koreanisch) oder
 * &quot;russian&quot; (russisch) auch angegeben werden. Beispiel: </p>
 *
 * <p>&quot;-Dnet.time4j.calendar.japanese.supplemental.era name=newEra,kanji=KK,since=9999-12-31&quot;</p>
 *
 * @author  Meno Hochschild
 * @since   3.32/4.27
 */
public final class Nengo
    implements CalendarEra, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final byte COURT_NORTHERN = 1;
    private static final byte COURT_STANDARD = 0;
    private static final byte COURT_SOUTHERN = -1;

    private static final String NEW_ERA_PROPERTY = "net.time4j.calendar.japanese.supplemental.era";

    private static final Nengo[] OFFICIAL_NENGOS;
    private static final Nengo[] NORTHERN_NENGOS;
    private static final Nengo NENGO_KENMU;
    private static final Nengo NENGO_OEI;
    private static final Map<String, Nengo> KANJI_TO_NENGO;
    private static final Map<String, Nengo> CHINESE_TO_NENGO;
    private static final TST KOREAN_TO_NENGO;
    private static final TST RUSSIAN_TO_NENGO;
    private static final TST ROMAJI_TO_NENGO;

    static {
        List<Nengo> official = new ArrayList<Nengo>(256);
        List<Nengo> northern = new ArrayList<Nengo>(16);
        Nengo kenmu = null;
        Nengo oei = null;
        Map<String, Nengo> kanjiToNengo = new HashMap<String, Nengo>();
        Map<String, Nengo> chineseToNengo = new HashMap<String, Nengo>();
        TST koreanToNengo = new TST();
        TST russianToNengo = new TST();
        TST romajiToNengo = new TST();

        String path = "data/nengo.data";
        URI uri = ResourceLoader.getInstance().locate("calendar", Nengo.class, path);
        InputStream is = ResourceLoader.getInstance().load(uri, true);

        try {
            if (is == null) {
                is = ResourceLoader.getInstance().load(Nengo.class, path, true);
            }

            DataInputStream in = new DataInputStream(is);

            while (true) { // throws EOFException
                int relgregyear = in.readShort();
                int start = in.readInt();
                String kanji = in.readUTF();
                String chinese = in.readUTF();
                String korean = in.readUTF();
                String russian = in.readUTF();
                byte court = in.readByte();
                int count = in.readByte();
                List<String> romaji = new ArrayList<String>(count);
                for (int i = 0; i < count; i++) {
                    String token = in.readUTF();
                    romaji.add(token);
                }
                String name = romaji.get(0);
                Nengo nengo;
                if (court == COURT_NORTHERN) {
                    nengo =
                        new Nengo(relgregyear, start, kanji, chinese, korean, russian, name, court, northern.size());
                    northern.add(nengo);
                } else {
                    nengo =
                        new Nengo(relgregyear, start, kanji, chinese, korean, russian, name, court, official.size());
                    official.add(nengo);
                    if (relgregyear == 1334) {
                        kenmu = nengo;
                    } else if (relgregyear == 1394) {
                        oei = nengo;
                    }
                }
                if ((nengo.court != COURT_NORTHERN) || (nengo.relgregyear != 1334)) { // exclusion of Kenmu (N)
                    kanjiToNengo.put(kanji, nengo);
                    if (chineseToNengo.put(chinese, nengo) != null) { // sanity check
                        throw new IllegalStateException(nengo.relgregyear + " " + nengo.chinese);
                    }
                    koreanToNengo.insert(korean, nengo);
                    russianToNengo.insert(russian, nengo);
                    for (String r : romaji) {
                        romajiToNengo.insert(r, nengo);
                    }
                }
            }
        } catch (EOFException eof) {
            // end-of-loop-condition
        } catch (IOException ioe) {
            throw new IllegalStateException("Invalid nengo data.", ioe);
        }

        String newEraDef = System.getProperty(NEW_ERA_PROPERTY);

        if (newEraDef != null) {
            String[] keyValuePairs = newEraDef.split(",");
            String name = null;
            String kanji = null;
            String chinese = null;
            String korean = null;
            String russian = null;
            PlainDate since = null;
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    if (keyValue[0].equals("name")) {
                        name = hepburn(keyValue[1], 0);

                    } else if (keyValue[0].equals("kanji")) {
                        kanji = keyValue[1];
                        if (kanji.length() != 2) {
                            throw new IllegalArgumentException("Japanese kanji must be of length 2.");
                        }

                    } else if (keyValue[0].equals("chinese")) {
                        chinese = keyValue[1];
                        if (chinese.length() != 2) {
                            throw new IllegalArgumentException("Chinese kanji must be of length 2.");
                        }

                    } else if (keyValue[0].equals("korean")) {
                        korean = keyValue[1];

                    } else if (keyValue[0].equals("russian")) {
                        russian = capitalize(keyValue[1], 0);

                    } else if (keyValue[0].equals("since")) {
                        try {
                            since = Iso8601Format.parseDate(keyValue[1]);
                        } catch (ParseException pe) {
                            // will be handled later
                        }

                    }
                }
            }
            if ((name != null) && (kanji != null) && (since != null)) {
                if (since.isBefore(PlainDate.of(1989, 1, 9))) {
                    throw new IllegalStateException("New Japanese era must be after Heisei.");
                } else {
                    if (chinese == null) {
                        chinese = kanji;
                    }
                    if (korean == null) {
                        korean = name;
                    }
                    if (russian == null) {
                        russian = name;
                    }
                    Nengo newNengo =
                        new Nengo(
                            since.getYear(),
                            since.getDaysSinceEpochUTC(),
                            kanji, chinese, korean, russian, name,
                            COURT_STANDARD, official.size());
                    official.add(newNengo);
                    kanjiToNengo.put(kanji, newNengo);
                    chineseToNengo.put(chinese, newNengo);
                    koreanToNengo.insert(korean, newNengo);
                    russianToNengo.insert(russian, newNengo);
                    romajiToNengo.insert(name, newNengo);
                }
            } else {
                throw new IllegalStateException("Invalid syntax: " + newEraDef);
            }
        }

        OFFICIAL_NENGOS = official.toArray(new Nengo[official.size()]);
        NORTHERN_NENGOS = northern.toArray(new Nengo[northern.size()]);
        NENGO_KENMU = kenmu;   // southern variant
        NENGO_OEI = oei;       // after nanboku-chō-period

        KANJI_TO_NENGO = Collections.unmodifiableMap(kanjiToNengo);
        CHINESE_TO_NENGO = Collections.unmodifiableMap(chineseToNengo);
        KOREAN_TO_NENGO = koreanToNengo;
        RUSSIAN_TO_NENGO = russianToNengo;
        ROMAJI_TO_NENGO = romajiToNengo;
    }

    /**
     * <p>Meji is valid from year 1868 until 1912-07-29. </p>
     *
     * <p>It uses the lunisolar calendar until end of year 1872. </p>
     */
    /*[deutsch]
     * <p>Meiji gilt vom Jahr 1868 bis 1912-07-29. </p>
     *
     * <p>Dieser Nengo verwendet den lunisolaren Kalender bis Ende 1872. </p>
     */
    public static final Nengo MEIJI = OFFICIAL_NENGOS[223];

    /**
     * <p>Taish&#333; is valid from 1912-07-30 until 1926-12-24. </p>
     */
    /*[deutsch]
     * <p>Taish&#333; gilt von 1912-07-30 bis 1926-12-24. </p>
     */
    public static final Nengo TAISHO = OFFICIAL_NENGOS[224];

    /**
     * <p>Sh&#333;wa is valid from 1926-12-25 until 1989-01-07. </p>
     */
    /*[deutsch]
     * <p>Sh&#333;wa gilt von 1926-12-25 bis 1989-01-07. </p>
     */
    public static final Nengo SHOWA = OFFICIAL_NENGOS[225];

    /**
     * <p>Heisei is valid from 1989-01-08 onwards (emperor Akihito). </p>
     */
    /*[deutsch]
     * <p>Heisei gilt ab 1989-01-08 (Kaiser Akihito). </p>
     */
    public static final Nengo HEISEI = OFFICIAL_NENGOS[226];

    /**
     * Format attribute which helps to resolve possible ambivalences in parsing.
     *
     * <p>Standard value is: {@link Selector#OFFICIAL}</p>
     */
    /*[deutsch]
     * Formatattribut, das hilft, m&ouml;gliche Mehrdeutigkeiten beim Interpretieren aufzul&ouml;sen.
     *
     * <p>Standardwert ist: {@link Selector#OFFICIAL}</p>
     */
    public static final AttributeKey<Selector> SELECTOR = Attributes.createKey("NENGO_SELECTOR", Selector.class);

    private static final String MEIJI_KEY = "meiji";
    private static final String TAISHO_KEY = "taisho";
    private static final String SHOWA_KEY = "showa";
    private static final String HEISEI_KEY = "heisei";

    private static final String[] MODERN_KEYS = { HEISEI_KEY, SHOWA_KEY, TAISHO_KEY, MEIJI_KEY };

    private static final long serialVersionUID = 5696395761628504723L;

    //~ Instanzvariablen --------------------------------------------------

    private transient final int relgregyear;
    private transient final long start;
    private transient final String kanji;
    private transient final String chinese;
    private transient final String korean;
    private transient final String russian;
    private transient final String romaji;

    /**
     * @serial  indicates if this nengo belongs to the northern or southern court of Nanboku-ch&#333; period
     */
    private final byte court;

    /**
     * @serial  internal array index pointer
     */
    private final int index;

    //~ Konstruktoren -----------------------------------------------------

    private Nengo(
        int relgregyear,
        long start,
        String kanji,
        String chinese,
        String korean,
        String russian,
        String romaji,
        byte court,
        int index
    ) {
        super();

        if (kanji.isEmpty()) {
            throw new IllegalArgumentException("Missing kanji.");
        } else if (romaji.isEmpty()) {
            throw new IllegalArgumentException("Missing latin transcription.");
        }

        if ((court > 1) || (court < -1)) {
            throw new IllegalArgumentException("Undefined court byte: " + court);
        }

        this.relgregyear = relgregyear;
        this.start = start;
        this.kanji = kanji;
        this.chinese = chinese;
        this.korean = korean;
        this.russian = russian;
        this.romaji = romaji;
        this.court = court;
        this.index = index;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Obtains the official nengo for given related gregorian year. </p>
     *
     * <p>In case two or more nengos happen for one given year, Time4J will choose the latest nengo
     * whose first related gregorian year is still smaller than or equal to given year. </p>
     *
     * @param   year        the gregorian year which relates to the first day of any Japanese year (New Year)
     * @return  found nengo
     * @throws  IllegalArgumentException if no suitable nengo could be found for given year
     * @see     Nengo.Selector#OFFICIAL
     * @see     #ofRelatedGregorianYear(int, Selector)
     */
    /*[deutsch]
     * <p>Liefert den offiziellen Nengo f&uuml;r das angegebene gregorianische Bezugsjahr. </p>
     *
     * <p>Falls zwei oder mehr Nengos zum Jahr passen, wird Time4J den letzten Nengo w&auml;hlen, dessen
     * erstes gregorianisches Bezugsjahr noch kleiner oder gleich dem angegebenen Jahr ist. </p>
     *
     * @param   year        the gregorian year which relates to the first day of any Japanese year (New Year)
     * @return  found nengo
     * @throws  IllegalArgumentException if no suitable nengo could be found for given year
     * @see     Nengo.Selector#OFFICIAL
     * @see     #ofRelatedGregorianYear(int, Selector)
     */
    public static Nengo ofRelatedGregorianYear(int year) {

        return ofRelatedGregorianYear(year, Selector.OFFICIAL);

    }

    /**
     * <p>Obtains the nengo for given related gregorian year and selector strategy. </p>
     *
     * <p>In case two or more nengos happen for one given year, Time4J will choose the latest nengo
     * whose first related gregorian year is still smaller than or equal to given year. </p>
     *
     * @param   year        the gregorian year which relates to the first day of any Japanese year (New Year)
     * @param   selector    strategy how to select nengos
     * @return  found nengo
     * @throws  IllegalArgumentException if no suitable nengo could be found for given parameters
     */
    /*[deutsch]
     * <p>Liefert den passenden Nengo f&uuml;r die angegebenen Parameter. </p>
     *
     * <p>Falls zwei oder mehr Nengos zum Jahr passen, wird Time4J den letzten Nengo w&auml;hlen, dessen
     * erstes gregorianisches Bezugsjahr noch kleiner oder gleich dem angegebenen Jahr ist. </p>
     *
     * @param   year        the gregorian year which relates to the first day of any Japanese year (New Year)
     * @param   selector    strategy how to select nengos
     * @return  found nengo
     * @throws  IllegalArgumentException if no suitable nengo could be found for given parameters
     */
    public static Nengo ofRelatedGregorianYear(
        int year,
        Selector selector
    ) {

        Nengo nengo = null;

        if (year >= 701) {
            switch (selector) {
                case OFFICIAL:
                    if (year >= 1873) {
                        return Nengo.ofRelatedGregorianYear(year, Selector.MODERN);
                    } else {
                        int low = 0;
                        int high = OFFICIAL_NENGOS.length - 1;
                        while (low <= high) {
                            int middle = ((low + high) >> 1);
                            if (OFFICIAL_NENGOS[middle].getFirstRelatedGregorianYear() <= year) {
                                low = middle + 1;
                            } else {
                                high = middle - 1;
                            }
                        }
                        if (low == 0) {
                            break;
                        } else {
                            return OFFICIAL_NENGOS[low - 1];
                        }
                    }
                case MODERN:
                    for (int i = OFFICIAL_NENGOS.length - 1, n = getLowerBound(selector); i >= n; i--) {
                        Nengo test = OFFICIAL_NENGOS[i];
                        if (test.relgregyear <= year) {
                            nengo = test;
                            break;
                        }
                    }
                    break;
                case NORTHERN_COURT:
                    if ((year >= 1332) && (year <= 1394)) {
                        for (int i = NORTHERN_NENGOS.length - 1; i >= 0; i--) {
                            Nengo northern = NORTHERN_NENGOS[i];
                            if (northern.relgregyear <= year) {
                                nengo = northern;
                                break;
                            }
                        }
                    }
                    break;
                case SOUTHERN_COURT:
                    if ((year >= 1334) && (year <= 1393)) {
                        for (int i = NENGO_OEI.index - 1; OFFICIAL_NENGOS[i].court == COURT_SOUTHERN; i--) {
                            Nengo southern = OFFICIAL_NENGOS[i];
                            if (southern.relgregyear <= year) {
                                nengo = southern;
                                break;
                            }
                        }
                    }
                    break;
                default:
                    int min = getLowerBound(selector);
                    int max = getUpperBound(selector);
                    if ((year >= OFFICIAL_NENGOS[min].relgregyear) && (year <= OFFICIAL_NENGOS[max + 1].relgregyear)) {
                        for (int i = max; i >= min; i--) {
                            Nengo test = OFFICIAL_NENGOS[i];
                            if (test.relgregyear <= year) {
                                nengo = test;
                                break;
                            }
                        }
                    }
                    break;
            }
        }

        if (nengo == null) {
            throw new IllegalArgumentException(
                "Could not find nengo for year=" + year + ", selector=" + selector + ".");
        } else {
            return nengo;
        }

    }

    /**
     * <p>Tries to find a suitable nengo for given japanese kanji representation. </p>
     *
     * @param   kanji   the representation in japanese kanji
     * @return  suitable nengo with same kanji
     * @throws  IllegalArgumentException if no suitable nengo could be found for given kanji representation
     */
    /*[deutsch]
     * <p>Versucht, einen passenden Nengo zur in japanischen Kanji angegebenen Repr&auml;sentation zu finden. </p>
     *
     * @param   kanji   the representation in japanese kanji
     * @return  suitable nengo with same kanji
     * @throws  IllegalArgumentException if no suitable nengo could be found for given kanji representation
     */
    public static Nengo ofKanji(String kanji) {

        Nengo nengo = KANJI_TO_NENGO.get(kanji);

        if (nengo == null) {
            throw new IllegalArgumentException(
                "Could not find any nengo for Japanese kanji: " + kanji);
        } else {
            return nengo;
        }

    }

    /**
     * <p>Tries to find suitable nengos for given representation in romaji. </p>
     *
     * <p>The internal parser is case-insensitive and able to translate vowels with circumflex into vowels
     * with macron. Some labels are ambivalent. One single nengo can even have more than one romaji representation.
     * For example, the label &quot;Sh&#333;wa&quot; has existed three times in history (for different nengos).
     * Here the first nengo &quot;J&#333;wa&quot; (year 834) was also read as &quot;Sh&#333;wa&quot;. </p>
     *
     * @param   romaji      the representation in romaji (hepburn variant)
     * @return  unmodifiable list of suitable nengos maybe empty
     */
    /*[deutsch]
     * <p>Versucht, passende Nengos zur in Romaji angegebenen Repr&auml;sentation zu finden. </p>
     *
     * <p>Der interne Interpretierer unterscheidet nicht zwischen Gro&szlig;- und Kleinschreibung
     * und ist in der Lage, Vokale mit Zirkumflex in Vokale mit Makron zu &uuml;bersetzen. Einige
     * Bezeichnungen sind mehrdeutig. Ein einzelner Nengo kann &uuml;berdies mehrere gleichzeitige
     * Bezeichnungen haben. Zum Beispiel hat die Bezeichnung &quot;Sh&#333;wa&quot; dreimal
     * in der Geschichte Japans existiert (f&uuml;r verschiedene Nengos), wobei der erste Nengo
     * &quot;J&#333;wa&quot; (im Jahre 834) auch unter dem Namen &quot;Sh&#333;wa&quot; gef&uuml;hrt wurde. </p>
     *
     * @param   romaji      the representation in romaji (hepburn variant)
     * @return  unmodifiable list of suitable nengos maybe empty
     */
    public static List<Nengo> parseRomaji(String romaji) {

        String query = hepburn(romaji, 0);
        String prefix = ROMAJI_TO_NENGO.longestPrefixOf(query, 0);
        return ROMAJI_TO_NENGO.find(prefix);

    }

    /**
     * <p>Obtains a list of all official nengos in chronological ascending order. </p>
     *
     * @return  unmodifiable list of nengos
     * @see     Nengo.Selector#OFFICIAL
     * @see     #list(Selector)
     */
    /*[deutsch]
     * <p>Liefert eine Liste aller offiziellen Nengos in zeitlich aufsteigender Reihenfolge. </p>
     *
     * @return  unmodifiable list of nengos
     * @see     Nengo.Selector#OFFICIAL
     * @see     #list(Selector)
     */
    public static List<Nengo> list() {

        return list(Selector.OFFICIAL);

    }

    /**
     * <p>Obtains a list of all nengos in chronological ascending order selected by given selector. </p>
     *
     * @param   selector    strategy how to select nengos
     * @return  unmodifiable list of nengos
     */
    /*[deutsch]
     * <p>Liefert eine Liste aller Nengos in zeitlich aufsteigender Reihenfolge,
     * die zum angegebenen Selektor passen. </p>
     *
     * @param   selector    strategy how to select nengos
     * @return  unmodifiable list of nengos
     */
    public static List<Nengo> list(Selector selector) {

        List<Nengo> nengos;

        switch (selector){
            case OFFICIAL:
                nengos = Arrays.asList(OFFICIAL_NENGOS);
                break;
            case NORTHERN_COURT:
                nengos = Arrays.asList(NORTHERN_NENGOS);
                break;
            default:
                int min = getLowerBound(selector);
                int max = getUpperBound(selector);
                nengos = new ArrayList<Nengo>(max - min + 1);
                for (int i = min; i <= max; i++) {
                    nengos.add(OFFICIAL_NENGOS[i]);
                }
                break;
        }

        return Collections.unmodifiableList(nengos);

    }

    /**
     * <p>Does this nengo match given selector? </p>
     *
     * @param   selector    strategy how to select nengos
     * @return  {@code true} if given selector would select this nengo else {@code false}
     */
    /*[deutsch]
     * <p>Passt dieser Nengo zum angegebenen Selector? </p>
     *
     * @param   selector    strategy how to select nengos
     * @return  {@code true} if given selector would select this nengo else {@code false}
     */
    public boolean matches(Selector selector) {

        return selector.test(this);

    }

    /**
     * <p>Obtains the gregorian year which relates to the first day of associated Japanese year
     * where this nengo was used the first time. </p>
     *
     * @return  related gregorian year
     * @see     #getStart()
     */
    /*[deutsch]
     * <p>Liefert das gregorianische Jahr, das sich auf den Neujahrstag des assoziierten japanischen Jahres
     * bezieht, in dem dieser Nengo zum ersten Mal gebraucht wurde. </p>
     *
     * @return  related gregorian year
     * @see     #getStart()
     */
    public int getFirstRelatedGregorianYear() {

        return this.relgregyear;

    }

    /**
     * <p>Obtains the first date when this nengo took effect. </p>
     *
     * <p>Due to the differences between old Japanese calendar and modern gregorian variant, it is possible
     * that the year of start date deviates from the related gregorian year. An example is the nengo Eich&#333;
     * with the related gregorian year 1096 although it started first on AD-1096-01-03 (julian). </p>
     *
     * @return  gregorian date of first introduction of this nengo
     * @see     #getFirstRelatedGregorianYear()
     */
    /*[deutsch]
     * <p>Liefert das erste Datum, wenn dieser Nengo in Kraft trat. </p>
     *
     * <p>Wegen der Differenzen zwischen dem alten japanischen Kalender und der modernen gregorianischen Variante
     * ist es m&ouml;glich, da&szlig; das Jahr des Startdatums vom gregorianischen Bezugsjahr abweicht. Ein
     * Beispiel ist der Nengo Eich&#333; mit dem Bezugsjahr 1096, obwohl er erst am julianischen Datum
     * AD-1096-01-03 begann. </p>
     *
     * @return  gregorian date of first introduction of this nengo
     * @see     #getFirstRelatedGregorianYear()
     */
    public PlainDate getStart() {

        return PlainDate.of(this.start, EpochDays.UTC);

    }

    /**
     * <p>Determines if this nengo is used by modern Japan (since Meiji). </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Ermittelt, ob dieser Nengo im modernen Japan verwendet wird (seit Meiji). </p>
     *
     * @return  boolean
     */
    public boolean isModern() {

        return (this.index >= MEIJI.index);

    }

    /**
     * <p>Equivalent to {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      the language which determines the name to be displayed
     * @return  localized display name
     * @see     #getDisplayName(Locale, TextWidth)
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code getDisplayName(locale, TextWidth.WIDE)}. </p>
     *
     * @param   locale      the language which determines the name to be displayed
     * @return  localized display name
     * @see     #getDisplayName(Locale, TextWidth)
     */
    public String getDisplayName(Locale locale) {

        return this.getDisplayName(locale, TextWidth.WIDE);

    }

    /**
     * <p>Obtains the localized name. </p>
     *
     * <p>For all modern nengos (Meiji or later), the display name will be determined on base of best efforts,
     * possibly in narrow or abbreviated notation, too. However, older nengos are localized in a simplified manner:
     * There is no support for abbreviated or narrow text forms, and the localization is limited to the languages
     * Japanese, Chinese, Korean and Russian (based on Wikipedia). All other languages will be printed using the
     * hebonshiki romaji system. </p>
     *
     * @param   locale      the language which determines the name to be displayed
     * @param   width       text width (not relevant for older nengos)
     * @return  localized display name
     */
    /*[deutsch]
     * <p>Liefert den lokalisierten Anzeigenamen. </p>
     *
     * <p>Alle modernen Nengos (Meiji oder sp&auml;ter) benutzen den bestm&ouml;glichen Anzeigenamen, eventuell
     * auch als Abk&uuml;rzung oder in <i>narrow</i>-Schreibweise. Aber: &Auml;ltere Nengos werden in einer
     * vereinfachten Art und Weise lokalisiert, und es gibt keine Abk&uuml;rzungen. Die Sprachunterst&uuml;tzung
     * ist dann auf Japanisch, Chinesisch, Koreanisch und Russisch basierend auf Wikipedia beschr&auml;nkt.
     * F&uuml;r alle anderen Sprachen wird das Hepburn-Umschreibungssystem verwendet (hebonshiki romaji). </p>
     *
     * @param   locale      the language which determines the name to be displayed
     * @param   width       text width (not relevant for older nengos)
     * @return  localized display name
     */
    public String getDisplayName(
        Locale locale,
        TextWidth width
    ) {

        if (locale.getLanguage().isEmpty()) {
            return this.romaji;
        } else if (((this.index >= MEIJI.index)) && (this.index <= HEISEI.index) && !locale.getLanguage().equals("ru")) {
            String key;
            if (this.equals(HEISEI)) {
                key = HEISEI_KEY;
            } else if (this.equals(SHOWA)) {
                key = SHOWA_KEY;
            } else if (this.equals(TAISHO)) {
                key = TAISHO_KEY;
            } else {
                key = MEIJI_KEY;
            }
            if (width == TextWidth.NARROW) {
                key = key + "_n";
            }
            Map<String, String> textForms = CalendarText.getInstance("japanese", locale).getTextForms();
            return textForms.get(key);
        } else { // also for any new nengo registered via system property
            if (locale.getLanguage().equals("ja")) {
                return this.kanji;
            } else if (locale.getLanguage().equals("zh")) {
                return this.chinese;
            } else if (locale.getLanguage().equals("ko")) {
                return this.korean;
            } else if (locale.getLanguage().equals("ru")) {
                return "Период " + this.russian;
            } else {
                return this.romaji;
            }
        }

    }

    /**
     * <p>Tries to find the next nengo in chronological order. </p>
     *
     * <p>Note: If this nengo represents a nengo of northern court in the Nanboku-ch&#333; period (1336-1392) then
     * the next nengo will be the next one of northern court only but not of southern court. If this
     * nengo is the last one of northern court then the next nengo will be &#332;ei (1394). </p>
     *
     * @return  next nengo which is only present if this nengo is not the newest nengo else {@code null}
     */
    /*[deutsch]
     * <p>Versucht, den n&auml;chsten Nengo in chronologischer Reihenfolge zu finden. </p>
     *
     * <p>Hinweis: Wenn dieser Nengo einen Nengo des Nordhofs in der Nanboku-ch&#333;-Zeit (1336-1392)
     * repr&auml;sentiert, dann wird der n&auml;chste Nengo ebenfalls vom Nordhof sein, aber nicht vom
     * S&uuml;dhof. Falls dieser Nengo der letzte des Nordhofs ist (Meitoku), dann ist der n&auml;chste
     * der Nengo &#332;ei (1394). </p>
     *
     * @return  next nengo which is only present if this nengo is not the newest nengo else {@code null}
     */
    public Nengo findNext() {

        if (this.court == COURT_NORTHERN) {
            if (this.index == NORTHERN_NENGOS.length - 1) {
                return NENGO_OEI;
            } else {
                return NORTHERN_NENGOS[this.index + 1];
            }
        } else if (this.index == OFFICIAL_NENGOS.length - 1) {
            return null;
        } else {
            return OFFICIAL_NENGOS[this.index + 1];
        }

    }

    /**
     * <p>Tries to find the previous nengo in chronological order. </p>
     *
     * <p>Note: If this nengo represents a nengo of northern court in the Nanboku-ch&#333; period (1336-1392) then
     * the previous nengo will be the previous one of northern court only but not of southern court. If this
     * nengo is the first one of northern court then the previous nengo will be Genk&#333: (1391). </p>
     *
     * @return  previous nengo which is only present if this nengo is not the first nengo else {@code null}
     */
    /*[deutsch]
     * <p>Versucht, den vorherigen Nengo in chronologischer Reihenfolge zu finden. </p>
     *
     * <p>Hinweis: Wenn dieser Nengo einen Nengo des Nordhofs in der Nanboku-ch&#333;-Zeit (1336-1392)
     * repr&auml;sentiert, dann wird der vorherige Nengo ebenfalls vom Nordhof sein, aber nicht vom
     * S&uuml;dhof. Falls dieser Nengo der erste des Nordhofs ist, dann ist der vorherige
     * der Nengo Genk&#333: (1391). </p>
     *
     * @return  previous nengo which is only present if this nengo is not the first nengo else {@code null}
     */
    public Nengo findPrevious() {

        if (this.court == COURT_NORTHERN) {
            if (this.index == 0) {
                return OFFICIAL_NENGOS[NENGO_KENMU.index - 1];
            } else {
                return NORTHERN_NENGOS[this.index - 1];
            }
        } else if (this.index == 0) {
            return null;
        } else {
            return OFFICIAL_NENGOS[this.index - 1];
        }

    }

    /**
     * <p>Obtains a descriptive text with romaji and the relevant year interval. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Liefert einen Beschreibungstext mit romaji-Umschreibung und dem relevanten Jahrintervall. </p>
     *
     * @return  String
     */
    @Override
    public String name() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.romaji);
        sb.append(" (");
        Nengo next = this.findNext();
        if (next != null) {
            sb.append(this.relgregyear);
            sb.append('-');
            sb.append(next.relgregyear);
        } else {
            sb.append("since ");
            sb.append(this.relgregyear);
        }
        sb.append(')');
        return sb.toString();

    }

    @Override
    public int getValue() {

        if (this.matches(Selector.NORTHERN_COURT)) {
            return (this.index - NORTHERN_NENGOS.length + NENGO_OEI.index - Nengo.SHOWA.index + 1);
        }

        return (this.index - Nengo.SHOWA.index + 1);

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof Nengo) {
            Nengo that = (Nengo) obj;
            return (
                (this.relgregyear == that.relgregyear)
                && (this.start == that.start)
                && this.kanji.equals(that.kanji)
                && this.romaji.equals(that.romaji)
                && (this.court == that.court));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (int) (this.start ^ (this.start >>> 32));

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.romaji);
        sb.append(' ');
        sb.append(this.kanji);
        sb.append(' ');
        Nengo next = this.findNext();
        if (next != null) {
            sb.append(this.relgregyear);
            sb.append('-');
            sb.append(next.relgregyear);
        } else {
            sb.append("since ");
            sb.append(this.relgregyear);
        }
        if (this.court != COURT_STANDARD) {
            sb.append(" (");
            sb.append((this.court == COURT_NORTHERN) ? 'N' : 'S');
            sb.append(')');
        }
        return sb.toString();

    }

    // verwendet in JapaneseCalendar
    long getStartAsDaysSinceEpochUTC() {

        return this.start;

    }

    // verwendet in JapaneseCalendar
    int getIndexOfficial() {

        return this.index;

    }

    // verwendet in JapaneseCalendar
    static Nengo ofIndexOfficial(int index) {

        return OFFICIAL_NENGOS[index];

    }

    // useful because some computer keyboards don't manage macrons, see => https://en.wikipedia.org/wiki/Macron
    static String hepburn(
        CharSequence text,
        int offset
    ) {

        StringBuilder sb = null;
        int n = Math.min(text.length(), offset + 32);

        for (int i = offset; i < n; i++) {
            final char c = text.charAt(i);
            char std;
            if (i == offset) { // capitalization
                std = ((c == 'Ô') || (c == 'ô') || (c == 'ō') ? 'Ō' : Character.toUpperCase(c));
                std = ((c == 'Û') || (c == 'û') || (c == 'ū') ? 'Ū' : std);
            } else {
                std = ((c == 'Ô') || (c == 'ô') || (c == 'Ō') ? 'ō' : Character.toLowerCase(c));
                std = ((c == 'Û') || (c == 'û') || (c == 'Ū') ? 'ū' : std);
            }
            std = ((c == '\'') ? '\u2019' : std); // use ’ as apostroph
            std = ((c == ' ') ? '-' : std); // use hyphen instead of space
            if ((sb != null) || (std != c)) {
                if (sb == null) {
                    sb = new StringBuilder(32);
                    sb.append(text.subSequence(offset, i));
                }
                sb.append(std);
            }
        }

        return ((sb == null) ? text.subSequence(offset, n).toString() : sb.toString());

    }

    private static String capitalize(
        CharSequence text,
        int offset
    ) {

        StringBuilder sb = null;
        int n = Math.min(text.length(), offset + 32);
        boolean ucase = true;

        for (int i = offset; i < n; i++) {
            final char c = text.charAt(i);
            char std = (ucase ? Character.toUpperCase(c) : Character.toLowerCase(c));
            ucase = (c == ' ');
            if ((sb != null) || (std != c)) {
                if (sb == null) {
                    sb = new StringBuilder(32);
                    sb.append(text.subSequence(offset, i));
                }
                sb.append(std);
            }
        }

        return ((sb == null) ? text.subSequence(offset, n).toString() : sb.toString());

    }

    private static int getUpperBound(Selector selector) {

        switch (selector) {
            case EDO_PERIOD:
                return MEIJI.index - 1;
            case AZUCHI_MOMOYAMA_PERIOD:
                return 187;
            case MUROMACHI_PERIOD:
                return 184;
            case NORTHERN_COURT:
                return NORTHERN_NENGOS.length - 1;
            case SOUTHERN_COURT:
                return NENGO_KENMU.index + 8;
            case KAMAKURA_PERIOD:
                return NENGO_KENMU.index - 1;
            case HEIAN_PERIOD:
                return 102;
            case NARA_PERIOD:
                return 14;
            case ASUKA_PERIOD:
                return 2;
            default:
                return OFFICIAL_NENGOS.length - 1;
        }

    }

    private static int getLowerBound(Selector selector) {

        switch (selector) {
            case MODERN:
                return MEIJI.index;
            case EDO_PERIOD:
                return 188;
            case AZUCHI_MOMOYAMA_PERIOD:
                return 185;
            case MUROMACHI_PERIOD:
                return NENGO_KENMU.index + 1;
            case SOUTHERN_COURT:
                return NENGO_KENMU.index;
            case KAMAKURA_PERIOD:
                return 103;
            case HEIAN_PERIOD:
                return 15;
            case NARA_PERIOD:
                return 3;
            default:
                return 0;
        }

    }

    private static Nengo of(
        int index,
        boolean northern
    ) {

        return (northern ? NORTHERN_NENGOS[index] : OFFICIAL_NENGOS[index]);

    }

    /**
     * @serialData  Preserves the singleton semantic
     * @return      cached singleton
     * @throws      ObjectStreamException if deserializing is not possible
     */
    private Object readResolve() throws ObjectStreamException {

        try {
            return Nengo.of(this.index, (this.court == COURT_NORTHERN));
        } catch (ArrayIndexOutOfBoundsException iooe) {
            throw new StreamCorruptedException();
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Represents a strategy how to select nengos. </p>
     *
     * @author  Meno Hochschild
     * @since   3.30/4.26
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert eine Strategie, wie Nengos ausgew&auml;hlt werden. </p>
     *
     * @author  Meno Hochschild
     * @since   3.30/4.26
     */
    public static enum Selector
        implements ChronoCondition<Nengo> {

        //~ Statische Felder/Initialisierungen ----------------------------

        /**
         * <p>Selects all nengos with the exception of those of the northern court
         * in the Nanboku-ch&#333; period (1336-1392). </p>
         *
         * <p>This selector is the default. </p>
         */
        /*[deutsch]
         * <p>W&auml;hlt alle Nengos mit Ausnahme der des Nordhofs in der Nanboku-ch&#333;-Zeit (1336-1392) aus. </p>
         *
         * <p>Dieser Selektor ist der Standard. </p>
         */
        OFFICIAL {
            @Override
            public boolean test(Nengo nengo) {
                return (nengo.court != COURT_NORTHERN);
            }
        },

        /**
         * <p>Selects only nengos from Meiji onwards (introduction of japanese-gregorian
         * calendar in year 1873 = Meiji 6). </p>
         */
        /*[deutsch]
         * <p>W&auml;hlt nur Nengos seit Meiji aus (Einf&uuml;hrung des japanisch-gregorianischen
         * Kalenders im Jahre 1873 = Meji 6). </p>
         */
        MODERN {
            @Override
            public boolean test(Nengo nengo) {
                return (nengo.index >= MEIJI.index);
            }
        },

        /**
         * <p>Selects all nengos of the Edo period (1603-1868). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Edo_period">Wikipedia</a>. </p>
         */
        /**
         * <p>W&auml;hlt alle Nengos der Edo-Zeit aus (1603-1868). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Edo-Zeit">Wikipedia</a>. </p>
         */
        EDO_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 1603) && (nengo.relgregyear < 1868));
            }
        },

        /**
         * <p>Selects all nengos of the Azuchi-Momoyama period (1573-1603). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Azuchi%E2%80%93Momoyama_period">Wikipedia</a>. </p>
         */
        /**
         * <p>W&auml;hlt alle Nengos der Azuchi-Momoyama-Zeit aus (1573-1603). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Azuchi-Momoyama-Zeit">Wikipedia</a>. </p>
         */
        AZUCHI_MOMOYAMA_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 1573) && (nengo.relgregyear < 1603));
            }
        },

        /**
         * <p>Selects all nengos of the Muromachi period (1336-1573). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Muromachi_period">Wikipedia</a>.
         * The nengos of the Kenmu-restoration do not belong to this period. Furthermore, all nengos
         * of the northern court are excluded because otherwise a historical order of generated
         * lists is hard to achieve. </p>
         */
        /**
         * <p>W&auml;hlt alle Nengos der Muromachi-Zeit aus (1336-1573). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Muromachi-Zeit">Wikipedia</a>.
         * Die Nengos der Kenmu-Restauration geh&ouml;ren nicht zu dieser Zeit. Auch werden die
         * Nengos des Nordhofs ausgeschlossen, weil sonst eine historische Sortierung von
         * generierten Listen schwierig wird. </p>
         */
        MUROMACHI_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 1336) && (nengo.relgregyear < 1573) && (nengo.court != COURT_NORTHERN));
            }
        },

        /**
         * <p>Selects the nengos of the northern court only (during the Nanboku-ch&#333; period 1336-1392
         * and in addition, the nengo Sh&#333;kei (1332)). </p>
         *
         * <p>The northern variant of the Nengo Kenmu (Kenmu restoration) is also matched by this selector. </p>
         */
        /*[deutsch]
         * <p>W&auml;hlt nur die Nengos des Nordhofs in der Nanboku-ch&#333;-Zeit (1336-1392) und
         * zus&auml;tzlich den Nengo Sh&#333;kei (1332) aus. </p>
         *
         * <p>Die Nordvariante des Nengo Kenmu (Kenmu Restauration) wird auch von diesem Selektor abgedeckt. </p>
         */
        NORTHERN_COURT {
            @Override
            public boolean test(Nengo nengo) {
                return (nengo.court == COURT_NORTHERN);
            }
        },

        /**
         * <p>Selects the nengos of the southern court only (during the Nanboku-ch&#333; period 1336-1392). </p>
         *
         * <p>The southern variant of the Nengo Kenmu (Kenmu restoration) is also matched by this selector.
         * Historical note: The last year 10 of nengo Gench&#x016B; was then replaced by Meitoku 4. </p>
         */
        /*[deutsch]
         * <p>W&auml;hlt nur die Nengos des S&uuml;dhofs in der Nanboku-ch&#333;-Zeit (1336-1392) aus. </p>
         *
         * <p>Die S&uuml;dvariante des Nengo Kenmu (Kenmu Restauration) wird auch von diesem Selektor abgedeckt.
         * Historische Bemerkung: Das letzte Jahr 10 des Nengo Gench&#x016B; wurde durch Meitoku 4 ersetzt. </p>
         */
        SOUTHERN_COURT {
            @Override
            public boolean test(Nengo nengo) {
                return (nengo.court == COURT_SOUTHERN);
            }
        },

        /**
         * <p>Selects all nengos of the Kamakura period (1185-1332). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Kamakura_period">Wikipedia</a></p>
         */
        /**
         * <p>W&auml;hlt alle Nengos der Kamakura-Zeit aus (1185-1332). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Kamakura-Zeit">Wikipedia</a></p>
         */
        KAMAKURA_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 1185) && (nengo.relgregyear < 1332)); // excluding Shōkei (N)
            }
        },

        /**
         * <p>Selects all nengos of the Heian period (794-1185). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Heian_period">Wikipedia</a></p>
         */
        /**
         * <p>W&auml;hlt alle Nengos der Heian-Zeit aus (794-1185). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Heian-Zeit">Wikipedia</a></p>
         */
        HEIAN_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 794) && (nengo.relgregyear < 1185));
            }
        },

        /**
         * <p>Selects all nengos of the Nara period (710-794). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Nara_period">Wikipedia</a></p>
         */
        /**
         * <p>W&auml;hlt alle Nengos der Nara-Zeit aus (710-794). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Nara-Zeit">Wikipedia</a></p>
         */
        NARA_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 710) && (nengo.relgregyear < 794));
            }
        },

        /**
         * <p>Selects all nengos of the Asuka period (538-710). </p>
         *
         * <p>See also <a href="https://en.wikipedia.org/wiki/Asuka_period">Wikipedia</a>. Time4J
         * only supports the last three nengos of this period because the historical data are not
         * exactly known and there were also historical gaps in nengo counting. </p>
         */
        /*[deutsch]
         * <p>W&auml;hlt alle Nengos der Asuka-Zeit aus (538-710). </p>
         *
         * <p>Siehe auch <a href="https://de.wikipedia.org/wiki/Asuka-Zeit">Wikipedia</a>. Time4J
         * unterst&uuml;tzt nur die letzten drei Nengos dieser Zeit aus, weil die historischen
         * Daten unsicher und auch mit L&uuml;cken in der Nengo-Z&auml;hlung behaftet sind. </p>
         */
        ASUKA_PERIOD {
            @Override
            public boolean test(Nengo nengo) {
                return ((nengo.relgregyear >= 538) && (nengo.relgregyear < 710));
            }
        }

    }

    static class Element
        implements TextElement<Nengo>, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final Element SINGLETON = new Element();

        private static final long serialVersionUID = -1099321098836107792L;

        //~ Konstruktoren -------------------------------------------------

        private Element() {
            super();

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public void print(
            ChronoDisplay context,
            Appendable buffer,
            AttributeQuery attributes
        ) throws IOException, ChronoException {

            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            buffer.append(context.get(this).getDisplayName(locale, width));

        }

        @Override
        public Nengo parse(
            CharSequence text,
            ParsePosition pp,
            AttributeQuery attributes
        ) {

            Locale locale = attributes.get(Attributes.LANGUAGE, Locale.ROOT);
            TextWidth width = attributes.get(Attributes.TEXT_WIDTH, TextWidth.WIDE);
            Map<String, String> textForms = CalendarText.getInstance("japanese", locale).getTextForms();
            int offset = pp.getIndex();

            if (offset >= text.length()) {
                pp.setErrorIndex(offset);
                return null;
            }

            String query = (locale.getLanguage().equals("ru") ? capitalize(text, offset) : hepburn(text, offset));
            Nengo candidate = null;
            int len = 0;

            for (int i = 0; i < MODERN_KEYS.length; i++) {
                String key = MODERN_KEYS[i];
                if (width == TextWidth.NARROW) {
                    key = key + "_n";
                }
                String test = textForms.get(key);
                if (query.startsWith(test)) {
                    switch (i) {
                        case 0:
                            candidate = HEISEI;
                            break;
                        case 1:
                            candidate = SHOWA;
                            break;
                        case 2:
                            candidate = TAISHO;
                            break;
                        case 3:
                            candidate = MEIJI;
                            break;
                        default:
                            throw new AssertionError();
                    }
                    len = test.length();
                    if ((width != TextWidth.NARROW) && (candidate != SHOWA)) { // Shōwa is ambivalent!
                        pp.setIndex(offset + len);
                        return candidate;
                    } else {
                        break;
                    }
                }
            }

            if (query.length() < 2) { // NARROW
                if (candidate != null) {
                    pp.setIndex(offset + 1);
                }
                return candidate;
            }

            String prefix = null;
            int extra = 0;
            List<Nengo> candidates = Collections.emptyList();

            if (locale.getLanguage().equals("ja")) {
                int end = ((query.length() >= 4) ? 4 : 2);
                String test = query.substring(0, end);
                Nengo nengo = KANJI_TO_NENGO.get(test);
                if ((nengo == null) && (end == 4)) {
                    test = query.substring(0, 2);
                    nengo = KANJI_TO_NENGO.get(test);
                }
                if (nengo != null) {
                    prefix = test;
                    if (nengo == candidate) { // resolving Shōwa
                        candidate = null;
                    }
                    candidates = Collections.singletonList(nengo);
                }
            } else if (locale.getLanguage().equals("zh")) {
                int end = ((query.length() >= 4) ? 4 : 2);
                String test = query.substring(0, end);
                Nengo nengo = CHINESE_TO_NENGO.get(test);
                if ((nengo == null) && (end == 4)) {
                    test = query.substring(0, 2);
                    nengo = CHINESE_TO_NENGO.get(test);
                }
                if (nengo != null) {
                    prefix = test;
                    if (nengo == candidate) { // resolving Shōwa
                        candidate = null;
                    }
                    candidates = Collections.singletonList(nengo);
                }
            } else if (locale.getLanguage().equals("ko")) {
                prefix = KOREAN_TO_NENGO.longestPrefixOf(query, offset);
                candidates = KOREAN_TO_NENGO.find(prefix);

            } else if (locale.getLanguage().equals("ru")) {
                if (query.startsWith("Период ")) {
                    query = query.substring(7);
                    extra = 7;
                }
                prefix = RUSSIAN_TO_NENGO.longestPrefixOf(query, offset);
                candidates = RUSSIAN_TO_NENGO.find(prefix);

            } else {
                prefix = ROMAJI_TO_NENGO.longestPrefixOf(query, offset);
                candidates = ROMAJI_TO_NENGO.find(prefix);

            }

            int count = candidates.size();

            if (count == 0) {
                if (candidate == null) {
                    return null;
                } else {
                    pp.setIndex(offset + len);
                    return candidate;
                }
            }

            int end = prefix.length() + extra;

            if (len < end) {
                candidate = null; // we have now a better string match
            } else if (len > end) {
                pp.setIndex(offset + len);
                return candidate;
            }

            assert ((candidate == null) || (candidate == SHOWA));

            if (count == 1) {
                Nengo nengo = candidates.get(0);
                if ((candidate == null) || (nengo == candidate)) {
                    pp.setIndex(offset + end);
                    return nengo;
                }
            }

            // handle general ambivalence
            Selector selector = attributes.get(SELECTOR, Selector.OFFICIAL);
            List<Nengo> nengos = new ArrayList<Nengo>(candidates);

            if ((candidate != null) && !nengos.contains(candidate)) {
                nengos.add(candidate);
            }

            Collections.sort(
                nengos,
                new Comparator<Nengo>() {
                    @Override
                    public int compare(Nengo n1, Nengo n2) {
                        return ((n1.start < n2.start) ? 1 : ((n1.start == n2.start) ? 0 : -1));
                    }
                }); // descending order
            Iterator<Nengo> iter = nengos.iterator();

            while (iter.hasNext()) {
                Nengo nengo = iter.next();
                if (!nengo.matches(selector)) {
                    iter.remove();
                }
            }

            if (nengos.size() == 1) {
                pp.setIndex(offset + end);
                return nengos.get(0);
            } else if ((nengos.size() > 1) && !attributes.get(Attributes.LENIENCY, Leniency.SMART).isStrict()) {
                pp.setIndex(offset + end);
                return nengos.get(0);
            }

            return null; // Nengo could not be parsed

        }

        @Override
        public String name() {

            return "ERA";

        }

        @Override
        public Class<Nengo> getType() {

            return Nengo.class;

        }

        @Override
        public char getSymbol() {

            return 'G';

        }

        @Override
        public int compare(
            ChronoDisplay o1,
            ChronoDisplay o2
        ) {

            Nengo n1 = o1.get(this);
            Nengo n2 = o2.get(this);

            if (n1.start < n2.start) {
                return -1;
            } else if (n1.start > n2.start) {
                return 1;
            } else if (n1.court == COURT_NORTHERN) {
                return ((n2.court == COURT_NORTHERN) ? 0 : 1);
            } else {
                return ((n2.court == COURT_NORTHERN) ? -1 : 0);
            }

        }

        @Override
        public Nengo getDefaultMinimum() {

            return Nengo.OFFICIAL_NENGOS[0];

        }

        @Override
        public Nengo getDefaultMaximum() {

            return Nengo.OFFICIAL_NENGOS[OFFICIAL_NENGOS.length - 1];

        }

        @Override
        public boolean isDateElement() {

            return true;

        }

        @Override
        public boolean isTimeElement() {

            return false;

        }

        @Override
        public boolean isLenient() {

            return false;

        }

        @Override
        public String getDisplayName(Locale language) {

            String key = "L_era";
            String lname = CalendarText.getIsoInstance(language).getTextForms().get(key);
            return ((lname == null) ? this.name() : lname);

        }

        /**
         * @serialData  Preserves the singleton semantic
         * @return      singleton instance
         */
        private Object readResolve() throws ObjectStreamException {

            return SINGLETON;

        }

    }

    private static class TST {

        //~ Instanzvariablen ----------------------------------------------

        private Node root = null;

        //~ Methoden ------------------------------------------------------

        List<Nengo> find(String key) {

            if ((key == null) || (key.length() == 0)) {
                return Collections.emptyList();
            }

            Node node = find(this.root, key, 0);

            if (node == null) {
                return Collections.emptyList();
            } else {
                return Collections.unmodifiableList(node.nengos);
            }

        }

        private static Node find(
            Node node,
            String key,
            int pos
        ) {

            if (node == null) {
                return null;
            }

            char c = key.charAt(pos);

            if (c < node.c) {
                return find(node.left, key, pos);
            } else if (c > node.c) {
                return find(node.right, key, pos);
            } else if (pos < key.length() - 1) {
                return find(node.mid, key, pos + 1);
            } else {
                return node;
            }

        }

        void insert(
            String key,
            Nengo nengo
        ) {

            if (key.isEmpty()) {
                throw new IllegalArgumentException("Empty key cannot be inserted.");
            }

            this.root = insert(this.root, key, nengo, 0);

        }

        private static Node insert(
            Node node,
            String key,
            Nengo nengo,
            int pos
        ) {

            char c = key.charAt(pos);

            if (node == null) {
                node = new Node();
                node.c = c;
            }

            if (c < node.c) {
                node.left = insert(node.left, key, nengo, pos);
            } else if (c > node.c) {
                node.right = insert(node.right, key, nengo, pos);
            } else if (pos < key.length() - 1) {
                node.mid = insert(node.mid, key, nengo, pos + 1);
            } else {
                if (node.nengos == null) {
                    node.nengos = new ArrayList<Nengo>();
                }
                node.nengos.add(nengo); // end node
            }

            return node;

        }

        String longestPrefixOf(
            CharSequence query,
            int offset
        ) {

            int len = offset;
            Node node = this.root;
            int i = offset;
            int n = query.length();

            while ((node != null) && (i < n)) {
                char c = query.charAt(i);

                if (c < node.c) {
                    node = node.left;
                } else if (c > node.c) {
                    node = node.right;
                } else {
                    i++;
                    if (node.nengos != null) { // end node condition
                        len = i;
                    }
                    node = node.mid;
                }
            }

            return ((offset >= len) ? null : query.subSequence(offset, len).toString());

        }

    }

    private static class Node {

        //~ Instanzvariablen ----------------------------------------------

        private char c = '\u0000';
        private Node left = null;
        private Node mid = null;
        private Node right = null;
        private List<Nengo> nengos = null;

    }

}

