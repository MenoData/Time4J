/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2018 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (Timezone.java) is part of project Time4J.
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

package net.time4j.tz;

import net.time4j.base.GregorianDate;
import net.time4j.base.ResourceLoader;
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Loads and keeps timezone data including the rules. </p>
 *
 * <p>Timezones are identified by keys which have canonical forms as
 * documented in {@link TZID}. If the keys don't specify any provider
 * (no char &quot;~&quot;) then the timezone data and rules will be
 * looked up using the default {@code ZoneModelProvider}. This default provider
 * is loaded by {@code java.util.ServiceLoader} if its name is equal
 * to &quot;TZDB&quot; and its version string is not empty but of
 * the highest value (lexicographically). If no such provider can be
 * found then Time4J uses the platform provider based on the public
 * API of {@code java.util.TimeZone} which does not expose its
 * transition history however. </p>
 *
 * <p>Note: The concept of timezones is strongly based on the idea to
 * avoid any unrounded amounts of seconds or subseconds. Timezones were
 * historically first introduced by british railway companies to
 * guarantee fixed departure timetables. Consequently ISO-8601 only
 * knows timezone offsets in full minutes. The widely used TZDB-repository
 * of IANA knows in extreme case offsets in full seconds which is also
 * allowed by Time4J. Although the Time4J-library recognizes
 * {@link ZonalOffset#atLongitude(java.math.BigDecimal) fractional offsets}
 * based on the geographical longitude, its {@code Timezone}-API will
 * always ignore any fractional parts. </p>
 *
 * @author      Meno Hochschild
 * @serial      exclude
 */
/*[deutsch]
 * <p>L&auml;dt und h&auml;lt Zeitzonendaten mitsamt ihren Regeln. </p>
 *
 * <p>Zeitzonen werden durch Schl&uuml;ssel identifiziert, welche eine
 * kanonische Form wie in {@link TZID} dokumentiert haben. Wenn die
 * Schl&uuml;ssel nicht einen spezifischen {@code ZoneModelProvider} festlegen
 * (fehlende Tilde &quot;~&quot;), dann werden Zeitzonendaten und Regeln
 * vom Standard-Provider abgefragt. Dieser wird &uuml;ber einen
 * {@code java.util.ServiceLoader} geladen, wenn sein Name gleich
 * &quot;TZDB&quot; ist und seine Version lexikalisch die h&ouml;chste
 * und nicht-leer ist. Kann kein solcher {@code ZoneModelProvider} gefunden
 * werden, dann verwendet Time4J ersatzweise das &ouml;ffentliche API von
 * {@code java.util.TimeZone} (welches allerdings keine Historie
 * exponiert). </p>
 *
 * <p>Hinweis: Das Zeitzonenkonzept fu&szlig;t stark auf der Idee,
 * irgendwelche nicht-runden Sekunden- oder Subsekundenbetr&auml;ge
 * zu vermeiden. Historisch wurden Zeitzonen zuerst von britischen
 * Eisenbahngesellschaften mit der Motivation eingef&uuml;hrt, landesweit
 * feste Fahrpl&auml;ne zu erm&ouml;glichen. Konsequenterweise kennt
 * ISO-8601 nur Zeitzonen-Offsets (Verschiebungen) in vollen Minuten.
 * Die weit verbreitete Zeitzonendatenbank TZDB von IANA kennt in
 * Extremf&auml;llen auch Offsets in vollen Sekunden, was von Time4J
 * akzeptiert wird. Obwohl die Time4J-Bibliothek
 * {@link ZonalOffset#atLongitude(java.math.BigDecimal) fraktionale Offsets}
 * basierend auf der geographischen L&auml;nge kennt, wird das
 * {@code Timezone}-API immer fraktionale Subsekunden ignorieren. </p>
 *
 * @author      Meno Hochschild
 * @serial      exclude
 */
public abstract class Timezone
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final String NEW_LINE =
        System.getProperty("line.separator");
    private static final String REPOSITORY_VERSION =
        System.getProperty("net.time4j.tz.repository.version");

    private static final Comparator<TZID> ID_COMPARATOR =
        new Comparator<TZID>() {
            @Override
            public int compare(
                TZID o1,
                TZID o2
            ) {
                return o1.canonical().compareTo(o2.canonical());
            }
        };

    /**
     * <p>This standard strategy which is also used by the JDK-class
     * {@code java.util.GregorianCalendar} subtracts the next defined
     * offset from any local timestamp in order to calculate the global
     * time while pushing forward an invalid local time. </p>
     *
     * <p>Equivalent to
     * {@code GapResolver.PUSH_FORWARD.and(OverlapResolver.LATER_OFFSET)}. </p>
     *
     * @see     #getOffset(GregorianDate,WallTime)
     */
    /*[deutsch]
     * <p>Diese auch von der JDK-Klasse {@code java.util.GregorianCalendar}
     * verwendete Standardstrategie zieht von einem beliebigen lokalen
     * Zeitstempel den jeweils n&auml;chstdefinierten Offset ab, um die
     * globale Zeit zu erhalten, wobei eine ung&uuml;ltige lokale Zeit
     * um die L&auml;nge einer Offset-Verschiebung vorgeschoben wird. </p>
     *
     * <p>&Auml;quivalent zu:
     * {@code GapResolver.PUSH_FORWARD.and(OverlapResolver.LATER_OFFSET)}. </p>
     *
     * @see     #getOffset(GregorianDate,WallTime)
     */
    public static final TransitionStrategy DEFAULT_CONFLICT_STRATEGY =
        GapResolver.PUSH_FORWARD.and(OverlapResolver.LATER_OFFSET);

    /**
     * <p>In addition to the  {@link #DEFAULT_CONFLICT_STRATEGY
     * standard strategy}, this strategy ensures the use of valid local
     * timestamps. </p>
     *
     * <p>Equivalent to
     * {@code GapResolver.ABORT.and(OverlapResolver.LATER_OFFSET)}. </p>
     */
    /*[deutsch]
     * <p>Legt bei Transformationen von lokalen Zeitstempeln zu UTC fest,
     * da&szlig; nur in der Zeitzone g&uuml;ltige Zeitstempel zugelassen
     * werden. </p>
     *
     * <p>Ansonsten wird die {@link #DEFAULT_CONFLICT_STRATEGY
     * Standardstrategie} verwendet. &Auml;quivalent zu:
     * {@code GapResolver.ABORT.and(OverlapResolver.LATER_OFFSET)}. </p>
     */
    public static final TransitionStrategy STRICT_MODE =
        GapResolver.ABORT.and(OverlapResolver.LATER_OFFSET);

    private static final boolean ANDROID = "Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"));

    private static final boolean ALLOW_SYSTEM_TZ_OVERRIDE =
        ANDROID || Boolean.getBoolean("net.time4j.allow.system.tz.override");

    private static volatile ZonalKeys zonalKeys = null;
    private static volatile Timezone currentSystemTZ = null;
    private static volatile boolean cacheActive = true;
    private static int softLimit = 11;

    private static final String NAME_JUT = "java.util.TimeZone";
    private static final String NAME_TZDB = "TZDB";
    private static final String NAME_DEFAULT = "DEFAULT";

    private static final Map<String, TZID> PREDEFINED;
    private static final Map<String, TZID> ETCETERA;
    private static final ZoneModelProvider PLATFORM_PROVIDER;
    private static final ZoneModelProvider DEFAULT_PROVIDER;
    private static final ConcurrentMap<String, NamedReference> CACHE;
    private static final ReferenceQueue<Timezone> QUEUE;
    private static final LinkedList<Timezone> LAST_USED;
    private static final ConcurrentMap<String, ZoneModelProvider> PROVIDERS;

    /**
     * Default provider for tz-name-repository.
     */
    static final ZoneNameProvider NAME_PROVIDER;

    private static final Timezone SYSTEM_TZ_ORIGINAL;

    static {
        CACHE = new ConcurrentHashMap<String, NamedReference>();
        PROVIDERS = new ConcurrentHashMap<String, ZoneModelProvider>();
        QUEUE = new ReferenceQueue<Timezone>();
        LAST_USED = new LinkedList<Timezone>(); // strong references

        List<Class<? extends TZID>> areas;

        try {
            areas =
                loadPredefined(
                    Timezone.class.getClassLoader(),
                    "AFRICA",
                    "AMERICA",
                    "AMERICA$ARGENTINA",
                    "AMERICA$INDIANA",
                    "AMERICA$KENTUCKY",
                    "AMERICA$NORTH_DAKOTA",
                    "ANTARCTICA",
                    "ASIA",
                    "ATLANTIC",
                    "AUSTRALIA",
                    "EUROPE",
                    "INDIAN",
                    "PACIFIC");
        } catch (ClassNotFoundException cnfe) {
            // olson-package not available
            areas = Collections.emptyList();
        }

        Map<String, TZID> temp1 = new HashMap<String, TZID>();
        temp1.put("Z", ZonalOffset.UTC);
        temp1.put("UT", ZonalOffset.UTC);
        temp1.put("UTC", ZonalOffset.UTC);
        temp1.put("GMT", ZonalOffset.UTC);
        temp1.put("UTC0", ZonalOffset.UTC);
        temp1.put("GMT0", ZonalOffset.UTC);

        for (Class<? extends TZID> area : areas) {
            for (TZID tzid : area.getEnumConstants()) {
                temp1.put(tzid.canonical(), tzid);
            }
        }

        PREDEFINED = Collections.unmodifiableMap(temp1);

        Map<String, TZID> etcetera = new HashMap<String, TZID>();
        fillEtcetera(etcetera);
        ETCETERA = Collections.unmodifiableMap(etcetera);

        ZoneModelProvider zp = null;
        ZoneNameProvider np = null;

        for (ZoneModelProvider provider : ResourceLoader.getInstance().services(ZoneModelProvider.class)) {
            String name = provider.getName();

            if (name.equals(NAME_TZDB)) {
                zp = compareTZDB(provider, zp);
            } else if (!name.isEmpty() && !name.equals(NAME_DEFAULT)) {
                PROVIDERS.put(name, provider);
            }
        }

        for (ZoneNameProvider provider : ResourceLoader.getInstance().services(ZoneNameProvider.class)) {
            np = provider;
        }

        PLATFORM_PROVIDER = new PlatformZoneProvider();
        NAME_PROVIDER = ((np == null) ? PLATFORM_PROVIDER.getSpecificZoneNameRepository() : np);
        PROVIDERS.put(NAME_JUT, PLATFORM_PROVIDER);

        if (zp == null) {
            DEFAULT_PROVIDER = PLATFORM_PROVIDER;
        } else {
            PROVIDERS.put(NAME_TZDB, zp);
            DEFAULT_PROVIDER = zp;
        }

        Timezone systemTZ = null;

        try {
            String zoneID = System.getProperty("user.timezone");

            if ("Z".equals(zoneID) || "UTC".equals(zoneID)) {
                systemTZ = ZonalOffset.UTC.getModel();
            } else if (zoneID != null) {
                systemTZ = Timezone.getTZ(resolve(zoneID), zoneID, false);
            }
        } catch (SecurityException se) {
            // OK, dann Zugriff auf j.u.TimeZone.getDefault()
        }

        if (systemTZ == null) {
            SYSTEM_TZ_ORIGINAL = Timezone.getDefaultTZ();
        } else {
            SYSTEM_TZ_ORIGINAL = systemTZ;
        }

        if (ALLOW_SYSTEM_TZ_OVERRIDE) {
            currentSystemTZ = SYSTEM_TZ_ORIGINAL;
        }

        // Cache für Available-IDs
        zonalKeys = new ZonalKeys();
    }

    //~ Konstruktoren -----------------------------------------------------

    /**
     * <p>Nur zur paket-privaten Verwendung. </p>
     */
    Timezone() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Gets all available timezone IDs. </p>
     *
     * @return  unmodifiable list of available timezone ids in ascending order
     */
    /*[deutsch]
     * <p>Liefert alle verf&uuml;gbaren Zeitzonenkennungen. </p>
     *
     * @return  unmodifiable list of available timezone ids in ascending order
     */
    public static List<TZID> getAvailableIDs() {

        return zonalKeys.availables;

    }

    /**
     * <p>Gets all available timezone IDs for given {@code ZoneModelProvider}. </p>
     *
     * <p>Note that this method will return an empty list if given provider
     * name does not refer to any registered provider. If the name is equal
     * to &quot;DEFAULT&quot; then the default {@code ZoneModelProvider} will be
     * queried. </p>
     *
     * @param   provider    the registered zone model provider whose ids are searched
     * @return  unmodifiable list of available timezone ids in ascending order
     * @throws  IllegalArgumentException if the provider argument is empty
     * @since   2.2
     * @see     ZoneModelProvider#getName()
     * @see     ZoneModelProvider#getAvailableIDs()
     */
    /*[deutsch]
     * <p>Liefert alle verf&uuml;gbaren Zeitzonenkennungen zum angegebenen
     * {@code ZoneModelProvider}. </p>
     *
     * <p>Hinweis: Wenn das Argument keinen registrierten {@code ZoneModelProvider}
     * referenziert, dann liefert diese Methode eine leere Liste. Wenn das
     * Argument gleich &quot;DEFAULT&quot; ist, dann wird der
     * Standard-{@code ZoneModelProvider} abgefragt. </p>
     *
     * @param   provider    the registered zone model provider whose ids are searched
     * @return  unmodifiable list of available timezone ids in ascending order
     * @throws  IllegalArgumentException if the provider argument is empty
     * @since   2.2
     * @see     ZoneModelProvider#getName()
     * @see     ZoneModelProvider#getAvailableIDs()
     */
    public static List<TZID> getAvailableIDs(String provider) {

        if (provider.equals("INCLUDE_ALIAS")) {
            return zonalKeys.availablesAndAliases;
        }

        ZoneModelProvider zp = getProvider(provider);

        if (zp == null) {
            return Collections.emptyList();
        }

        List<TZID> result = new ArrayList<TZID>();

        for (String id : zp.getAvailableIDs()) {
            result.add(resolve(id));
        }

        Collections.sort(result, ID_COMPARATOR);
        return Collections.unmodifiableList(result);

    }

    /**
     * <p>Gets a provider-specific {@code Set} of preferred timezone IDs
     * for given ISO-3166-country code. </p>
     *
     * <p>This information is necessary to enable parsing of timezone names.
     * If the given provider does not denote a valid registered zone model provider then
     * this method will produce an empty set. if given provider name is &quot;DEFAULT&quot;
     * then the default zone name provider will be queried. </p>
     *
     * @param   locale      ISO-3166-alpha-2-country to be evaluated
     * @param   smart       if {@code true} then try to select zone ids such
     *                      that there is only one preferred id per zone name
     * @param   provider    the registered zone model provider whose preferred ids are queried
     * @return  unmodifiable set of preferred timezone ids
     * @throws  IllegalArgumentException if the provider argument is empty
     * @since   2.2
     * @see     ZoneModelProvider#getSpecificZoneNameRepository()
     * @see     ZoneNameProvider#getPreferredIDs(Locale, boolean)
     */
    /*[deutsch]
     * <p>Liefert die f&uuml;r einen gegebenen ISO-3166-L&auml;ndercode
     * und {@code ZoneNameProvider} bevorzugten Zeitzonenkennungen. </p>
     *
     * <p>Diese Information ist f&uuml;r die Interpretation von Zeitzonennamen
     * notwendig. Wenn das Argument {@code provider} keinen registrierten {@code ZoneModelProvider}
     * referenziert, liefert diese Methode eine leere Menge. Wenn das Argument gleich
     * &quot;DEFAULT&quot; ist, dann wird der Standard-{@code ZoneNameProvider}
     * abgefragt. </p>
     *
     * @param   locale      ISO-3166-alpha-2-country to be evaluated
     * @param   smart       if {@code true} then try to select zone ids such
     *                      that there is only one preferred id per zone name
     * @param   provider    the registered zone model provider whose preferred ids are queried
     * @return  unmodifiable set of preferred timezone ids
     * @throws  IllegalArgumentException if the provider argument is empty
     * @since   2.2
     * @see     ZoneModelProvider#getSpecificZoneNameRepository()
     * @see     ZoneNameProvider#getPreferredIDs(Locale, boolean)
     */
    public static Set<TZID> getPreferredIDs(
        Locale locale,
        boolean smart,
        String provider
    ) {

        ZoneModelProvider zp = getProvider(provider);

        if (zp == null) {
            return Collections.emptySet();
        }

        ZoneNameProvider np = zp.getSpecificZoneNameRepository();

        if (np == null) {
            np = NAME_PROVIDER;
        }

        Set<TZID> p = new HashSet<TZID>();

        for (String id : np.getPreferredIDs(locale, smart)) {
            p.add(resolve(id));
        }

        return Collections.unmodifiableSet(p);

    }

    /**
     * <p>Gets the system timezone. </p>
     *
     * <p>The underlying algorithm to determine the system timezone is
     * primarily based on the system property &quot;user.timezone&quot;
     * then on the method {@code java.util.TimeZone.getDefault()}. If
     * the system property &quot;net.time4j.allow.system.tz.override&quot;
     * is set to &quot;true&quot; then the system timezone can be changed
     * by a combined approach of {@code java.util.TimeZone.setDefault()}
     * and the method {@link Cache#refresh()}. Otherwise this class
     * will determine the system timezone only for one time while being
     * loaded. </p>
     *
     * <p>Once the identifier of the system timezone has been evaluated,
     * it will be combined with the best available timezone data and rules. </p>
     *
     * <p>Note: If the system timezone cannot be determined (for example
     * due to a wrong property value for &quot;user.timezone&quot;) then
     * this method will fall back to UTC timezone.. </p>
     *
     * @return  default timezone data of system
     * @see     java.util.TimeZone#getDefault() java.util.TimeZone.getDefault()
     * @see     #ofPlatform()
     */
    /*[deutsch]
     * <p>Liefert die Standard-Zeitzone des Systems. </p>
     *
     * <p>Der verwendete Algorithmus basiert vorrangig auf der
     * System-Property &quot;user.timezone&quot;, dann erst auf der
     * Methode {@code java.util.TimeZone.getDefault()}. Ist zus&auml;tzlich
     * die Property &quot;net.time4j.allow.system.tz.override&quot; auf den
     * Wert &quot;true&quot; gesetzt, dann kann nach einer Kombination aus
     * {@code java.util.TimeZone.setDefault()} und der Methode
     * {@link Cache#refresh()} in dieser Klasse die Standard-Zeitzone
     * auch ge&auml;ndert werden, sonst wird sie einmalig beim Laden
     * dieser Klasse gesetzt. </p>
     *
     * <p>Sobald die ID der Systemzeitzone ermittelt worden ist, wird Time4J
     * sie mit den am besten geeigneten Zeitzonendaten und Regeln kombinieren. </p>
     *
     * <p>Zu beachten: Kann die Standard-Zeitzone zum Beispiel wegen eines
     * falschen Property-Werts in &quot;user.timezone&quot; nicht interpretiert
     * werden, f&auml;llt diese Methode auf die UTC-Zeitzone zur&uuml;ck. </p>
     *
     * @return  default timezone data of system
     * @see     java.util.TimeZone#getDefault() java.util.TimeZone.getDefault()
     * @see     #ofPlatform()
     */
    public static Timezone ofSystem() {

        if (ALLOW_SYSTEM_TZ_OVERRIDE && (currentSystemTZ != null)) {
            return currentSystemTZ;
        } else {
            // detect premature class initialization
            assert (SYSTEM_TZ_ORIGINAL != null);
            return SYSTEM_TZ_ORIGINAL;
        }

    }

    /**
     * <p>Obtains the platform timezone with data and rules of the platform. </p>
     *
     * <p>Note that this method will never access the timezone repository of Time4J. In contrast,
     * {@code Timezone.ofSystem()} only evaluates the identifier of the system timezone and combines
     * it with the best available data and rules (possibly based on Time4J-data). Using the platform
     * data can only be recommended in some situations on mobile platforms where device users try to
     * compensate bad timezone data by adjusting the clock. Hence this method would help to achieve
     * the correct local time as set on the mobile device. </p>
     *
     * @return  system timezone using the platform timezone data and rules
     * @since   3.29/4.25
     * @see     #ofSystem()
     * @see     java.util.TimeZone
     */
    /*[deutsch]
     * <p>Liefert die Plattform-Zeitzone mitsamt den Daten und Regeln der Plattform. </p>
     *
     * <p>Zu beachten: Diese Methode wird niemals die Zeitzonendatenbank von Time4J nutzen. Im Gegensatz
     * dazu wird {@code Timezone.ofSystem()} die ID der Systemzeitzone ermitteln und sie mit den besten
     * verf&uuml;gbaren Daten und Regeln kombinieren (eventuell basierend auf Time4J-Daten). Die Verwendung
     * der Plattform-Zeitzone kann nur in einigen Situationen auf Mobilger&auml;ten empfohlen werden, wo
     * Anwender schlechte oder veraltete Zeitzonendaten mit Uhrverstellungen zu kompensieren versuchen.
     * In einem solchen Fall kann diese Methode helfen, die richtige lokale Zeit zu erhalten, wie sie auf
     * dem Mobilger&auml;t eingestellt worden ist. </p>
     *
     * @return  system timezone using the platform timezone data and rules
     * @since   3.29/4.25
     * @see     #ofSystem()
     * @see     java.util.TimeZone
     */
    public static Timezone ofPlatform() {

        return new PlatformTimezone();

    }

    /**
     * <p>Gets the timezone for given identifier. </p>
     *
     * <p>Queries the underlying {@code ZoneModelProvider}. </p>
     *
     * @param   tzid    timezone id as interface
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Liefert die Zeitzone mit der angegebenen ID. </p>
     *
     * <p>Fragt den zugrundeliegenden {@code ZoneModelProvider} ab. </p>
     *
     * @param   tzid    timezone id as interface
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public static Timezone of(TZID tzid) {

        return Timezone.getTZ(tzid, true);

    }

    /**
     * <p>Gets the timezone for given identifier. </p>
     *
     * <p>Queries the underlying {@code ZoneModelProvider}. </p>
     *
     * @param   tzid    timezone id as String
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Liefert die Zeitzone mit der angegebenen ID. </p>
     *
     * <p>Fragt den zugrundeliegenden {@code ZoneModelProvider} ab. </p>
     *
     * @param   tzid    timezone id as String
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public static Timezone of(String tzid) {

        return Timezone.getTZ(null, tzid, true);

    }

    /**
     * <p>Tries to load the timezone with the first given identifer else
     * with given alternative identifier. </p>
     *
     * <p>If the timezone cannot be loaded with first identifier then
     * this method will load the timezone using the alternative. In case
     * of failure, this method will finally load the system timezone.
     * In contrast to {@link #of(TZID)}, this method never throws any
     * exception. </p>
     *
     * <p>Queries the underlying {@code ZoneModelProvider}. </p>
     *
     * @param   tzid        preferred timezone id
     * @param   fallback    alternative timezone id
     * @return  timezone data
     */
    /*[deutsch]
     * <p>Versucht bevorzugt, die Zeitzone mit der angegebenen ID zu laden,
     * sonst eine Alternative. </p>
     *
     * <p>Ist die Zeitzone zur ID nicht ladbar, dann wird als zweiter Versuch
     * die Zeitzone passend zum zweiten Argument geladen. Schl&auml;gt auch
     * das fehl, wird schlie&szlig;lich die Standard-Zeitzone der JVM geladen.
     * Im Gegensatz zu {@link #of(TZID)} wirft diese Methode niemals eine
     * Ausnahme. </p>
     *
     * <p>Fragt den zugrundeliegenden {@code ZoneModelProvider} ab. </p>
     *
     * @param   tzid        preferred timezone id
     * @param   fallback    alternative timezone id
     * @return  timezone data
     */
    public static Timezone of(
        String tzid,
        TZID fallback
    ) {

        Timezone ret = Timezone.getTZ(null, tzid, false);

        if (ret == null) {
            ret = Timezone.getTZ(fallback, false);

            if (ret == null) {
                ret = Timezone.ofSystem();
            }
        }

        return ret;

    }

    /**
     * <p>Creates a new synthetic timezone based only on given data. </p>
     *
     * @param   tzid        timezone id
     * @param   history     history of offset transitions
     * @return  new instance of timezone data
     * @throws  IllegalArgumentException if a fixed zonal offset is combined
     *          with a non-empty history
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine neue synthetische Zeitzone basierend nur auf den
     * angegebenen Daten. </p>
     *
     * @param   tzid        timezone id
     * @param   history     history of offset transitions
     * @return  new instance of timezone data
     * @throws  IllegalArgumentException if a fixed zonal offset is combined
     *          with a non-empty history
     * @since   2.2
     */
    public static Timezone of(
        String tzid,
        TransitionHistory history
    ) {

        return new HistorizedTimezone(resolve(tzid), history);

    }

    /**
     * <p>Equivalent to {@code normalize(tzid.canonical())}. </p>
     *
     * @param   tzid        timezone id which might need normalization
     * @return  normalized identifier
     * @throws  IllegalArgumentException if given identifier is invalid (for example empty)
     * @see     #normalize(String)
     * @since   3.41/4.36
     */
    /*[deutsch]
     * <p>&Auml;quivalent zu {@code normalize(tzid.canonical())}. </p>
     *
     * @param   tzid        timezone id which might need normalization
     * @return  normalized identifier
     * @throws  IllegalArgumentException if given identifier is invalid (for example empty)
     * @see     #normalize(String)
     * @since   3.41/4.36
     */
    public static TZID normalize(TZID tzid) {

        return normalize(tzid.canonical());

    }

    /**
     * <p>Tries to normalize given timezone identifier on the base of best efforts. </p>
     *
     * <p>This method is only capable of resolving old aliases like &quot;Asia/Calcutta&quot;
     * to modern identifiers (like &quot;Asia/Kolkata&quot;) if the underlying
     * {@code ZoneModelProvider} supports resolving of aliases. This is true if
     * the tzdata-module is present on the class-path. However, the standard platform
     * zone data do not support such a feature. But fixed offsets like &quot;UTC+01&quot;
     * or the outdated form &quot;Etc/GMT+4&quot; can always be resolved to instances
     * of {@code ZonalOffset}. </p>
     *
     * @param   tzid        timezone id which might need normalization
     * @return  normalized identifier
     * @throws  IllegalArgumentException if given identifier is invalid (for example empty)
     * @see     TZID#canonical()
     * @see     ZonalOffset#parse(String)
     * @since   3.41/4.36
     */
    /*[deutsch]
     * <p>Versucht das Beste, die angegebene Zeitzonenkennung zu einer gebr&auml;chlicheren Variante
     * zu normalisieren. </p>
     *
     * <p>Diese Methode kann nur dann veraltete Aliaskennungen wie &quot;Asia/Calcutta&quot; (zu
     * &quot;Asia/Kolkata&quot;) aufl&ouml;sen, wenn der zugrundeliegende {@code ZoneModelProvider}
     * das unterst&uuml;tzt. Das ist der Fall, wenn das tzdata-Modul im Klassenpfad vorhanden ist.
     * Allerdings wird diese F&auml;higkeit von den Standard-Zeitzonendaten von Java nicht in diesem
     * Ausma&szlig; unterst&uuml;tzt. Aber feste Zeitzonenverschiebungen wie &quot;UTC+01&quot;
     * oder die veraltete Form &quot;Etc/GMT+4&quot; k&ouml;nnen immer zu Instanzen von
     * {@code ZonalOffset} aufgel&ouml;st werden. </p>
     *
     * @param   tzid        timezone id which might need normalization
     * @return  normalized identifier
     * @throws  IllegalArgumentException if given identifier is invalid (for example empty)
     * @see     TZID#canonical()
     * @see     ZonalOffset#parse(String)
     * @since   3.41/4.36
     */
    public static TZID normalize(String tzid) {

        String providerName = "";
        String zoneKey = tzid;

        for (int i = 0, n = zoneKey.length(); i < n; i++) {
            if (zoneKey.charAt(i) == '~') {
                providerName = zoneKey.substring(0, i);
                zoneKey = zoneKey.substring(i + 1); // maybe empty string
                break;
            }
        }

        if (zoneKey.isEmpty()) {
            throw new IllegalArgumentException("Empty zone identifier: " + tzid);
        }

        ZoneModelProvider provider = DEFAULT_PROVIDER;
        boolean useDefault = (providerName.isEmpty() || providerName.equals(NAME_DEFAULT));

        if (!useDefault && !providerName.equals("WINDOWS") && !providerName.equals("MILITARY")) {
            provider = PROVIDERS.get(providerName);

            if (provider == null) {
                String msg;
                if (providerName.equals(NAME_TZDB)) {
                    msg = "TZDB provider not available: ";
                } else {
                    msg = "Timezone model provider not registered: ";
                }
                throw new IllegalArgumentException(msg + tzid);
            }
        }

        String resolved;
        String alias = zoneKey;
        Map<String, String> aliases = provider.getAliases();

        while ((resolved = aliases.get(alias)) != null) {
            alias = resolved;
        }

        if (ETCETERA.containsKey(alias)) {
            return ETCETERA.get(alias);
        }

        return resolve(alias);

    }

    /**
     * <p>Gets the associated timezone identifier. </p>
     *
     * @return  timezone id
     * @see     java.util.TimeZone#getID() java.util.TimeZone.getID()
     */
    /*[deutsch]
     * <p>Liefert die Zeitzonen-ID. </p>
     *
     * @return  timezone id
     * @see     java.util.TimeZone#getID() java.util.TimeZone.getID()
     */
    public abstract TZID getID();

    /**
     * <p>Calculates the total offset for given global timestamp. </p>
     *
     * <p>Note: The returned offset has never any subsecond part, normally
     * not even seconds but full minutes or hours. </p>
     *
     * @param   ut      unix time
     * @return  total shift in seconds which yields local time if added to unix time
     * @see     java.util.TimeZone#getOffset(long)
     *          java.util.TimeZone.getOffset(long)
     * @see     #getStandardOffset(UnixTime)
     * @see     #getDaylightSavingOffset(UnixTime)
     */
    /*[deutsch]
     * <p>Ermittelt die gesamte Zeitzonenverschiebung zum angegebenen Zeitpunkt auf
     * der UT-Weltzeitlinie in Sekunden. </p>
     *
     * <p>Hinweis: Die zur&uuml;ckgegebene Verschiebung hat niemals
     * Subsekundenteile, normalerweise auch nicht Sekundenteile, sondern
     * nur volle Minuten oder Stunden. </p>
     *
     * @param   ut      unix time
     * @return  total shift in seconds which yields local time if added to unix time
     * @see     java.util.TimeZone#getOffset(long)
     *          java.util.TimeZone.getOffset(long)
     * @see     #getStandardOffset(UnixTime)
     * @see     #getDaylightSavingOffset(UnixTime)
     */
    public abstract ZonalOffset getOffset(UnixTime ut);

    /**
     * <p>Calculates the standard offset for given global timestamp. </p>
     *
     * <p>Note: The returned offset has never any subsecond part, normally
     * not even seconds but full minutes or hours. </p>
     *
     * @param   ut      unix time
     * @return  standard shift in seconds which yields standard local time if added to unix time
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Ermittelt die Standard-Zeitzonenverschiebung zum angegebenen Zeitpunkt auf
     * der UT-Weltzeitlinie in Sekunden. </p>
     *
     * <p>Hinweis: Die zur&uuml;ckgegebene Verschiebung hat niemals
     * Subsekundenteile, normalerweise auch nicht Sekundenteile, sondern
     * nur volle Minuten oder Stunden. </p>
     *
     * @param   ut      unix time
     * @return  standard shift in seconds which yields standard local time if added to unix time
     * @since   3.2/4.1
     */
    public abstract ZonalOffset getStandardOffset(UnixTime ut);

    /**
     * <p>Calculates the daylight saving amount for given global timestamp. </p>
     *
     * <p>Notes: The returned offset has never any subsecond part, normally
     * not even seconds but full minutes or hours. Starting with tzdb-version 2018b,
     * the obtained daylight saving amount might also be negative in some rare cases,
     * see the case of Ireland (Europe/Dublin) which has a negative DST-offset in
     * winter and zero in summer standard time. Users are asked not to interprete
     * too much in this method. It should only be interpreted as deviation relative
     * to standard time (what ever this might be). </p>
     *
     * @param   ut      unix time
     * @return  DST-shift in seconds which yields local wall time if added to standard local time
     * @see     #isDaylightSaving(UnixTime)
     * @since   3.2/4.1
     */
    /*[deutsch]
     * <p>Ermittelt die Tageslichtverschiebung zum angegebenen Zeitpunkt auf
     * der UT-Weltzeitlinie in Sekunden. </p>
     *
     * <p>Hinweise: Die zur&uuml;ckgegebene Verschiebung hat niemals
     * Subsekundenteile, normalerweise auch nicht Sekundenteile, sondern
     * nur volle Minuten oder Stunden. Beginnend mit der TZDB-Version 2018b
     * kann der erhaltene Betrag in seltenen F&auml;llen auch negativ sein.
     * Siehe zum Beispiel den Fall von Irland (Europe/Dublin), das eine
     * negative Tageslichtverschiebung im Winter kennt, die nur im Sommer
     * als Standardzeit gleich null ist. Anwender sollten nicht zuviel in
     * diese Methode hineininterpretieren. Stattdessen ist die Tageslichtverschiebung
     * einfach nur als Abweichung von der Standardzeit zu deuten, was auch immer
     * letztere sei. </p>
     *
     * @param   ut      unix time
     * @return  DST-shift in seconds which yields local wall time if added to standard local time
     * @see     #isDaylightSaving(UnixTime)
     * @since   3.2/4.1
     */
    public abstract ZonalOffset getDaylightSavingOffset(UnixTime ut);

    /**
     * <p>Calculates the offset for given local timestamp. </p>
     *
     * <p>In case of gaps or overlaps, this method uses the
     * {@link #DEFAULT_CONFLICT_STRATEGY standard strategy}
     * to get the next defined offset. This behaviour is conform to the
     * JDK-class {@code java.util.GregorianCalendar}. </p>
     *
     * <p>Note: The returned offset has never any subsecond part, normally
     * not even seconds but full minutes or hours. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  shift in seconds which yields unix time if subtracted
     *          from local time choosing later offset at gaps or overlaps
     * @see     java.util.TimeZone#getOffset(int, int, int, int, int, int)
     *          java.util.TimeZone.getOffset(int, int, int, int, int, int)
     */
    /*[deutsch]
     * <p>Ermittelt die Zeitzonenverschiebung zum angegebenen lokalen
     * Zeitpunkt in Sekunden. </p>
     *
     * <p>Als Konfliktstrategie f&uuml;r L&uuml;cken oder &Uuml;berlappungen
     * auf dem lokalen Zeitstrahl wird die {@link #DEFAULT_CONFLICT_STRATEGY
     * Standardstrategie} verwendet, den n&auml;chstdefinierten Offset zu
     * ermitteln. Dieses Verhalten ist zur JDK-Klasse
     * {@code java.util.GregorianCalendar} konform. </p>
     *
     * <p>Hinweis: Die zur&uuml;ckgegebene Verschiebung hat niemals
     * Subsekundenteile, normalerweise auch nicht Sekundenteile, sondern
     * nur volle Minuten oder Stunden. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  shift in seconds which yields unix time if subtracted
     *          from local time choosing later offset at gaps or overlaps
     * @see     java.util.TimeZone#getOffset(int, int, int, int, int, int)
     *          java.util.TimeZone.getOffset(int, int, int, int, int, int)
     */
    public abstract ZonalOffset getOffset(
        GregorianDate localDate,
        WallTime localTime
    );

    /**
     * <p>Evaluates if given local timestamp is invalid due to a gap
     * on the local timeline. </p>
     *
     * <p>A typical example is the transition from standard to daylight
     * saving time because the clock will be manually adjusted such
     * that the clock is moved forward by usually one hour. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  {@code true} if the local time is not defined due to
     *          transition gaps else {@code false}
     */
    /*[deutsch]
     * <p>Bestimmt, ob der angegebene lokale Zeitpunkt in eine L&uuml;cke
     * f&auml;llt. </p>
     *
     * <p>Das klassiche Beispiel liegt vor, wenn wegen eines &Uuml;bergangs
     * von der Winter- zur Sommerzeit eine bestimmte Uhrzeit nicht existiert,
     * weil die Uhr vorgestellt wurde. </p>
     *
     * @param   localDate   local date in timezone
     * @param   localTime   local wall time in timezone
     * @return  {@code true} if the local time is not defined due to
     *          transition gaps else {@code false}
     */
    public abstract boolean isInvalid(
        GregorianDate localDate,
        WallTime localTime
    );

    /**
     * <p>Queries if given global timestamp matches daylight saving time
     * relative to winter in this timezone? </p>
     *
     * <p>The DST correction can be obtained as difference between total
     * offset and raw offset if the raw offset has not changed yet.
     * As alternative, the DST correction can be obtained by evaluating
     * the transition offset history. About the rare case of Ireland
     * which observes standard time in summer and has a negative DST
     * correction in winter (using the TZDB-version 2018b), this method
     * will yield {@code true} in summer and {@code false} in winter. </p>
     *
     * @param   ut      unix time
     * @return  {@code true} if the argument represents any kind of summer time else {@code false}
     * @see     java.util.TimeZone#inDaylightTime(java.util.Date)
     *          java.util.TimeZone.inDaylightTime(java.util.Date)
     */
    /*[deutsch]
     * <p>Herrscht zum angegebenen Zeitpunkt Sommerzeit in der Zeitzone? </p>
     *
     * <p>Die DST-Korrektur selbst kann als Differenz zwischen dem Gesamt-Offset
     * und dem Standard-Offset erhalten werden, wenn sich der Standard-Offset
     * historisch nicht ge&auml;ndert hat. Alternativ und genauer kann die
     * DST-Korrektur &uuml;ber die Offset-Historie ermittelt werden. Was den
     * Fall von Irland angeht, das die Sommerzeit als Standardzeit definiert
     * und im Winter eine negative DST-Verschiebung kennt (in der TZDB-Version
     * 2018b), so wird diese Methode {@code true} im Sommer und {@code false}
     * im Winter liefern. </p>
     *
     * @param   ut      unix time
     * @return  {@code true} if the argument represents any kind of summer time else {@code false}
     * @see     java.util.TimeZone#inDaylightTime(java.util.Date)
     *          java.util.TimeZone.inDaylightTime(java.util.Date)
     */
    public abstract boolean isDaylightSaving(UnixTime ut);

    /**
     * <p>Determines if this timezone has no offset transitions and always
     * uses a fixed offset. </p>
     *
     * @return  {@code true} if there is no transition else {@code false}
     * @since   1.2.1
     */
    /*[deutsch]
     * <p>Legt fest, ob diese Zeitzone keine &Uuml;berg&auml;nge kennt und
     * nur einen festen Offset benutzt. </p>
     *
     * @return  {@code true} if there is no transition else {@code false}
     * @since   1.2.1
     */
    public abstract boolean isFixed();

    /**
     * <p>Gets the underlying offset transitions and rules if available. </p>
     *
     * @return  {@code TransitionHistory} or {@code null} if there is no
     *          better {@code ZoneModelProvider} than {@code java.util.TimeZone}
     */
    /*[deutsch]
     * <p>Liefert die zugrundeliegenden &Uuml;berg&auml;nge und Regeln,
     * falls vorhanden. </p>
     *
     * @return  {@code TransitionHistory} or {@code null} if there is no
     *          better {@code ZoneModelProvider} than {@code java.util.TimeZone}
     */
    public abstract TransitionHistory getHistory();

    /**
     * <p>Describes all registered {@code ZoneModelProvider}-instances with
     * name and optionally location and version. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Beschreibt alle registrierten {@code ZoneModelProvider}-Instanzen
     * mit Namen und optional Ort und Version. </p>
     *
     * @return  String
     */
    public static String getProviderInfo() {

        StringBuilder sb = new StringBuilder(128);
        sb.append(Timezone.class.getName());
        sb.append(":[default-provider=");
        sb.append(DEFAULT_PROVIDER.getName());
        sb.append(", registered={");

        for (String key : PROVIDERS.keySet()) {
            ZoneModelProvider provider = PROVIDERS.get(key);
            if (provider != null) { // defensive against parallel threads
                sb.append("(name=");
                sb.append(provider.getName());

                String location = provider.getLocation();

                if (!location.isEmpty()) {
                    sb.append(",location=");
                    sb.append(location);
                }

                String version = provider.getVersion();

                if (!version.isEmpty()) {
                    sb.append(",version=");
                    sb.append(version);
                }

                sb.append(')');
            }
        }

        sb.append("}]");
        return sb.toString();

    }

    /**
     * <p>Tries to get the version of given registered zone model provider. </p>
     *
     * @param   provider    name of zone model provider
     * @return  String (empty if unknown)
     * @throws  IllegalArgumentException if the provider argument is empty
     * @since   2.2
     */
    /*[deutsch]
     * <p>Versucht die Version des angegebenen und registrierten
     * {@code ZoneModelProvider} zu ermitteln. </p>
     *
     * @param   provider    name of zone model provider
     * @return  String (empty if unknown)
     * @throws  IllegalArgumentException if the provider argument is empty
     * @since   2.2
     */
    public static String getVersion(String provider) {

        ZoneModelProvider zp = getProvider(provider);
        return ((zp == null) ? "" : zp.getVersion());

    }

    /**
     * <p>Yields the names of all registered
     * {@code ZoneModelProvider}-instances. </p>
     *
     * @return  unmodifiable list of provider names
     * @since   2.2
     * @see     ZoneModelProvider#getName()
     */
    /*[deutsch]
     * <p>Liefert die Namen aller registrierten
     * {@code ZoneModelProvider}-Instanzen. </p>
     *
     * @return  unmodifiable list of provider names
     * @since   2.2
     * @see     ZoneModelProvider#getName()
     */
    public static Set<String> getRegisteredProviders() {

        return Collections.unmodifiableSet(PROVIDERS.keySet());

    }

    /**
     * <p>Gets the strategy for resolving local timestamps. </p>
     *
     * @return  transition strategy for resolving local timestamps
     * @see     #with(TransitionStrategy)
     */
    /*[deutsch]
     * <p>Ermittelt die Strategie zur Aufl&ouml;sung von lokalen
     * Zeitstempeln. </p>
     *
     * @return  transition strategy for resolving local timestamps
     * @see     #with(TransitionStrategy)
     */
    public abstract TransitionStrategy getStrategy();

    /**
     * <p>Creates a copy of this timezone which uses given strategy for
     * resolving local timestamps. </p>
     *
     * <p>If this timezone has a fixed offset then the strategy will be
     * ignored because in this case there can never be a conflict.
     * Otherwise if there is no public offset transition history then
     * the only supported strategies are {@link #DEFAULT_CONFLICT_STRATEGY}
     * and {@link #STRICT_MODE}. </p>
     *
     * @param   strategy    transition strategy for resolving local timestamps
     * @return  copy of this timezone with given strategy
     * @throws  UnsupportedOperationException if given strategy requires
     *          a transition history and this timezone does not have one
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Zeitzone, die zur Aufl&ouml;sung von
     * lokalen Zeitstempeln die angegebene Strategie nutzt. </p>
     *
     * <p>Hat diese Zeitzone einen festen Offset, wird die Strategie
     * ignoriert, da hier nie eine Konfliktsituation auftreten kann.
     * Wenn andererseits eine Zeitzone keine &ouml;ffentliche Historie
     * kennt, dann werden nur {@link #DEFAULT_CONFLICT_STRATEGY} und
     * {@link #STRICT_MODE} unterst&uuml;tzt. </p>
     *
     * @param   strategy    transition strategy for resolving local timestamps
     * @return  copy of this timezone with given strategy
     * @throws  UnsupportedOperationException if given strategy requires
     *          a transition history and this timezone does not have one
     */
    public abstract Timezone with(TransitionStrategy strategy);

    /**
     * <p>Returns the name of this timezone suitable for presentation to
     * users in given style and locale. </p>
     *
     * <p>If the name is not available then this method will yield the
     * ID of this timezone. </p>
     *
     * @param   style               name style
     * @param   locale              language setting
     * @return  localized timezone name for display purposes
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     * @see     Locale#getDefault()
     * @see     #getID()
     */
    /*[deutsch]
     * <p>Liefert den anzuzeigenden Zeitzonennamen. </p>
     *
     * <p>Ist der Zeitzonenname nicht ermittelbar, wird die ID der Zeitzone
     * geliefert. </p>
     *
     * @param   style               name style
     * @param   locale              language setting
     * @return  localized timezone name for display purposes
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     * @see     Locale#getDefault()
     * @see     #getID()
     */
    public String getDisplayName(
        NameStyle style,
        Locale locale
    ) {

        return getDisplayName(this.getID(), style, locale);

    }

    /**
     * <p>Returns the name of a timezone suitable for presentation to
     * users in given style and locale. </p>
     *
     * <p>If the name is not available then this method will yield the canonical
     * ID of given timezone identifier. </p>
     *
     * @param   tzid                timezone id
     * @param   style               name style
     * @param   locale              language setting
     * @return  localized timezone name for display purposes
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     * @see     Locale#getDefault()
     * @see     #getID()
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Liefert den anzuzeigenden Zeitzonennamen. </p>
     *
     * <p>Ist der Zeitzonenname nicht ermittelbar, wird die ID der Zeitzone geliefert. </p>
     *
     * @param   tzid                timezone id
     * @param   style               name style
     * @param   locale              language setting
     * @return  localized timezone name for display purposes
     * @see     java.util.TimeZone#getDisplayName(boolean,int,Locale)
     *          java.util.TimeZone.getDisplayName(boolean,int,Locale)
     * @see     Locale#getDefault()
     * @see     #getID()
     * @since   3.20/4.16
     */
    public static String getDisplayName(
        TZID tzid,
        NameStyle style,
        Locale locale
    ) {

        String canonical = tzid.canonical();
        int index = canonical.indexOf('~');
        ZoneModelProvider provider = DEFAULT_PROVIDER;
        String zoneID = canonical;

        if (index >= 0) {
            String pname = canonical.substring(0, index);
            if (!pname.equals(NAME_DEFAULT)) {
                provider = PROVIDERS.get(pname);
                if (provider == null) {
                    return canonical;
                }
            }
            zoneID = canonical.substring(index + 1);
        }

        ZoneNameProvider np = provider.getSpecificZoneNameRepository();

        if (np == null) {
            np = NAME_PROVIDER;
        }

        String name = np.getDisplayName(zoneID, style, locale);

        if (name.isEmpty()) {
            if (np != NAME_PROVIDER) {
                name = NAME_PROVIDER.getDisplayName(zoneID, style, locale);
            }
            name = (name.isEmpty() ? canonical : name);
        }

        return name;

    }

    /**
     * <p>Registers manually the given zone model provider. </p>
     *
     * <p>Repeated registrations of the same provider are ignored. </p>
     *
     * @param   provider    custom zone model provider to be registered
     * @return  {@code true} if registration was successful else {@code false}
     * @throws  IllegalArgumentException if given {@code ZoneModelProvider}
     *          refers to default, platform or TZDB-provider by name
     * @since   3.20/4.16
     */
    /*[deutsch]
     * <p>Registriert manuell den angegebenen {@code ZoneModelProvider}. </p>
     *
     * <p>Wiederholte Registrierungen des gleichen {@code ZoneModelProvider}
     * werden ignoriert. </p>
     *
     * @param   provider    custom zone model provider to be registered
     * @return  {@code true} if registration was successful else {@code false}
     * @throws  IllegalArgumentException if given {@code ZoneModelProvider}
     *          refers to default, platform or TZDB-provider by name
     * @since   3.20/4.16
     */
    public static boolean registerProvider(ZoneModelProvider provider) {

        String name = provider.getName();

        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                "Missing name of zone model provider.");
        } else if (name.equals(NAME_TZDB)) {
            throw new IllegalArgumentException(
                "TZDB provider cannot be registered after startup.");
        } else if (name.equals(NAME_JUT)) {
            throw new IllegalArgumentException(
                "Platform provider cannot be replaced.");
        } else if (name.equals(NAME_DEFAULT)) {
            throw new IllegalArgumentException(
                "Default zone model provider cannot be overridden.");
        }

        boolean inserted = (PROVIDERS.putIfAbsent(name, provider) == null);

        if (inserted) {
            zonalKeys = new ZonalKeys();
        }

        return inserted;

    }

    /**
     * <p>Creates a dump of this timezone and writes it to the given
     * buffer. </p>
     *
     * @param   buffer          buffer to write the dump to
     * @throws  IOException     in any case of I/O-errors
     * @since   2.2
     */
    /*[deutsch]
     * <p>Erzeugt eine Textzusammenfassung dieser Instanz und schreibt sie
     * in den angegebenen Puffer. </p>
     *
     * @param   buffer          buffer to write the dump to
     * @throws  IOException     in any case of I/O-errors
     * @since   2.2
     */
    public void dump(Appendable buffer) throws IOException {

        StringBuilder sb = new StringBuilder(4096);
        sb.append("Start Of Dump =>").append(NEW_LINE);
        sb.append("*** Timezone-ID:").append(NEW_LINE);
        sb.append(">>> ").append(this.getID().canonical()).append(NEW_LINE);

        if (this.isFixed()) {
            sb.append("*** Fixed offset:").append(NEW_LINE).append(">>> ");
            sb.append(this.getHistory().getInitialOffset()).append(NEW_LINE);
        } else {
            sb.append("*** Strategy:").append(NEW_LINE);
            sb.append(">>> ").append(this.getStrategy()).append(NEW_LINE);

            TransitionHistory history = this.getHistory();
            sb.append("*** History:").append(NEW_LINE);

            if (history == null) {
                sb.append(">>> Not public!").append(NEW_LINE);
            } else {
                history.dump(sb);
            }
        }

        sb.append("<= End Of Dump").append(NEW_LINE);
        buffer.append(sb.toString());

    }

    private static Timezone getDefaultTZ() {

        String zoneID = java.util.TimeZone.getDefault().getID();
        Timezone ret = Timezone.getTZ(null, zoneID, false);

        if (ret == null) {
            // exception case if the zoneID cannot be resolved
            ret = new PlatformTimezone(new NamedID(zoneID));
        }

        return ret;
    }

    private static Timezone getTZ(
        TZID tzid,
        boolean wantsException
    ) {

        // Liegt eine feste Verschiebung vor?
        if (tzid instanceof ZonalOffset) {
            return ((ZonalOffset) tzid).getModel();
        }

        return Timezone.getTZ(tzid, tzid.canonical(), wantsException);

    }

    private static Timezone getTZ(
        TZID tzid, // optional
        String zoneID,
        boolean wantsException
    ) {

        // Suche im Cache
        Timezone tz = null;
        NamedReference sref = CACHE.get(zoneID);

        if (sref != null) {
            tz = sref.get();
            if (tz == null) {
                CACHE.remove(sref.tzid);
            }
        }

        if (tz != null) {
            return tz;
        }

        // ZoneModelProvider auflösen
        String providerName = "";
        String zoneKey = zoneID;

        for (int i = 0, n = zoneID.length(); i < n; i++) {
            if (zoneID.charAt(i) == '~') {
                providerName = zoneID.substring(0, i);
                zoneKey = zoneID.substring(i + 1); // maybe empty string
                break;
            }
        }

        if (zoneKey.isEmpty()) {
            if (wantsException) {
                throw new IllegalArgumentException("Timezone key is empty.");
            } else {
                return null;
            }
        }

        ZoneModelProvider provider = DEFAULT_PROVIDER;

        boolean useDefault = (
            providerName.isEmpty()
            || providerName.equals(NAME_DEFAULT));

        if (!useDefault) {
            provider = PROVIDERS.get(providerName);

            if (provider == null) {
                if (wantsException) {
                    String msg;
                    if (providerName.equals(NAME_TZDB)) {
                        msg = "TZDB provider not available: ";
                    } else {
                        msg = "Timezone model provider not registered: ";
                    }
                    throw new IllegalArgumentException(msg + zoneID);
                } else {
                    return null;
                }
            }
        }

        // enums bevorzugen
        TZID resolved = tzid;

        if (resolved == null) {
            if (useDefault) {
                TZID result = resolve(zoneKey);
                if (result instanceof ZonalOffset) {
                    return ((ZonalOffset) result).getModel();
                } else {
                    resolved = result;
                }
            } else {
                resolved = new NamedID(zoneID);
            }
        }

        // java.util.TimeZone hat keine öffentliche Historie
        if (provider == PLATFORM_PROVIDER) {
            PlatformTimezone test = new PlatformTimezone(resolved, zoneKey);

            // JDK-Fallback verhindern => tz == null
            if (
                !test.isGMT()
                || zoneKey.equals("GMT")
                || zoneKey.startsWith("UT")
                || zoneKey.equals("Z")
            ) {
                tz = test;
            }
        } else { // exakte Suche in Historie
            TransitionHistory history = provider.load(zoneKey);

            if (history == null) {
                // Alias-Suche + Fallback-Option
                tz = Timezone.getZoneByAlias(provider, resolved, zoneKey);
            } else {
                tz = new HistorizedTimezone(resolved, history);
            }
        }

        // Ungültige ID?
        if (tz == null) {
            if (!wantsException) {
                return null;
            } else if (java.util.TimeZone.getDefault().getID().equals(zoneID)) {
                // edge case: platform zone with customized id
                return new PlatformTimezone(new NamedID(zoneID));
            } else {
                throw new IllegalArgumentException("Unknown timezone: " + zoneID);
            }
        }

        // bei Bedarf im Cache speichern
        if (cacheActive) {
            NamedReference oldRef =
                CACHE.putIfAbsent(
                    zoneID,
                    new NamedReference(tz, QUEUE)
                );

            if (oldRef == null) {
                synchronized (Timezone.class) {
                    LAST_USED.addFirst(tz);

                    while (LAST_USED.size() >= softLimit) {
                        LAST_USED.removeLast();
                    }
                }
            } else {
                Timezone oldZone = oldRef.get();

                if (oldZone != null) {
                    tz = oldZone;
                }
            }
        }

        return tz;

    }

    private static Timezone getZoneByAlias(
        ZoneModelProvider provider,
        TZID tzid,
        String zoneKey
    ) {

        TransitionHistory history = null;
        String alias = zoneKey;
        Map<String, String> aliases = provider.getAliases();

        while (
            (history == null)
            && ((alias = aliases.get(alias)) != null)
        ) {
            history = provider.load(alias);
        }

        if (history == null) {
            String fallback = provider.getFallback();

            if (fallback.isEmpty()) {
                return null;
            } else if (fallback.equals(provider.getName())) {
                throw new IllegalArgumentException(
                    "Circular zone model provider fallback: " + provider.getName());
            } else {
                return new FallbackTimezone(
                    tzid,
                    Timezone.of(fallback + "~" + zoneKey));
            }
        } else {
            return new HistorizedTimezone(tzid, history);
        }

    }

    private static TZID resolve(String zoneKey) {

        // enums bevorzugen
        TZID resolved = PREDEFINED.get(zoneKey);

        if (resolved == null) {
            if (zoneKey.startsWith("GMT")) {
                zoneKey = "UTC" + zoneKey.substring(3);
            }

            resolved = ZonalOffset.parse(zoneKey, false);

            if (resolved == null) {
                resolved = new NamedID(zoneKey);
            }
        }

        return resolved;

    }

    private static void fillEtcetera(Map<String, TZID> map) {
        map.put("Etc/GMT", ZonalOffset.UTC);
        map.put("Etc/Greenwich", ZonalOffset.UTC);
        map.put("Etc/Universal", ZonalOffset.UTC);
        map.put("Etc/Zulu", ZonalOffset.UTC);
        map.put("Etc/GMT+0", ZonalOffset.UTC);
        map.put("Etc/GMT-0", ZonalOffset.UTC);
        map.put("Etc/GMT0", ZonalOffset.UTC);
        map.put("Etc/UTC", ZonalOffset.UTC);
        map.put("Etc/UCT", ZonalOffset.UTC);
        map.put("Etc/GMT-14", ZonalOffset.ofTotalSeconds(14 * 3600));
        map.put("Etc/GMT-13", ZonalOffset.ofTotalSeconds(13 * 3600));
        map.put("Etc/GMT-12", ZonalOffset.ofTotalSeconds(12 * 3600));
        map.put("Etc/GMT-11", ZonalOffset.ofTotalSeconds(11 * 3600));
        map.put("Etc/GMT-10", ZonalOffset.ofTotalSeconds(10 * 3600));
        map.put("Etc/GMT-9", ZonalOffset.ofTotalSeconds(9 * 3600));
        map.put("Etc/GMT-8", ZonalOffset.ofTotalSeconds(8 * 3600));
        map.put("Etc/GMT-7", ZonalOffset.ofTotalSeconds(7 * 3600));
        map.put("Etc/GMT-6", ZonalOffset.ofTotalSeconds(6 * 3600));
        map.put("Etc/GMT-5", ZonalOffset.ofTotalSeconds(5 * 3600));
        map.put("Etc/GMT-4", ZonalOffset.ofTotalSeconds(4 * 3600));
        map.put("Etc/GMT-3", ZonalOffset.ofTotalSeconds(3 * 3600));
        map.put("Etc/GMT-2", ZonalOffset.ofTotalSeconds(2 * 3600));
        map.put("Etc/GMT-1", ZonalOffset.ofTotalSeconds(3600));
        map.put("Etc/GMT+1", ZonalOffset.ofTotalSeconds(-3600));
        map.put("Etc/GMT+2", ZonalOffset.ofTotalSeconds(-2 * 3600));
        map.put("Etc/GMT+3", ZonalOffset.ofTotalSeconds(-3 * 3600));
        map.put("Etc/GMT+4", ZonalOffset.ofTotalSeconds(-4 * 3600));
        map.put("Etc/GMT+5", ZonalOffset.ofTotalSeconds(-5 * 3600));
        map.put("Etc/GMT+6", ZonalOffset.ofTotalSeconds(-6 * 3600));
        map.put("Etc/GMT+7", ZonalOffset.ofTotalSeconds(-7 * 3600));
        map.put("Etc/GMT+8", ZonalOffset.ofTotalSeconds(-8 * 3600));
        map.put("Etc/GMT+9", ZonalOffset.ofTotalSeconds(-9 * 3600));
        map.put("Etc/GMT+10", ZonalOffset.ofTotalSeconds(-10 * 3600));
        map.put("Etc/GMT+11", ZonalOffset.ofTotalSeconds(-11 * 3600));
        map.put("Etc/GMT+12", ZonalOffset.ofTotalSeconds(-12 * 3600));
    }

    @SuppressWarnings("unchecked")
    private static List<Class<? extends TZID>> loadPredefined(
        ClassLoader loader,
        String... names
    ) throws ClassNotFoundException {

        List<Class<? extends TZID>> classes =
            new ArrayList<Class<? extends TZID>>();

        for (String name : names) {
            Class<?> clazz =
                Class.forName("net.time4j.tz.olson." + name, true, loader);

            if (TZID.class.isAssignableFrom(clazz)) {
                classes.add((Class<? extends TZID>) clazz);
            }
        }

        return Collections.unmodifiableList(classes);

    }

    private static ZoneModelProvider getProvider(String provider) {

        if (provider.isEmpty()) {
            throw new IllegalArgumentException("Missing zone model provider.");
        }

        return (
            provider.equals(NAME_DEFAULT)
            ? DEFAULT_PROVIDER
            : PROVIDERS.get(provider));

    }

    private static ZoneModelProvider compareTZDB(
        ZoneModelProvider provider,
        ZoneModelProvider zp
    ) {

        String v = provider.getVersion();

        if (!v.isEmpty()) {
            if (v.equals(REPOSITORY_VERSION)) {
                zp = provider;
            } else if (REPOSITORY_VERSION == null) {
                if (
                    (zp == null)
                    || (v.compareTo(zp.getVersion()) > 0)
                ) {
                    zp = provider;
                } else if (
                    (v.compareTo(zp.getVersion()) == 0)
                    && !provider.getLocation().contains("{java.home}")
                ) {
                    zp = provider;
                }
            }
        }

        return zp;

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Offers some static methods for the configuration of the
     * timezone cache. </p>
     */
    /*[deutsch]
     * <p>Bietet statische Methoden zum Konfigurieren des
     * Zeitzonendatenpuffers. </p>
     */
    public static class Cache {

        //~ Konstruktoren -------------------------------------------------

        private Cache() {
            // no instantiation
        }

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Can refresh the timezone cache in case of a dynamic
         * update of the underlying timezone repository. </p>
         *
         * <p>First the internal cache will be cleared. Furthermore,
         * if needed the system timezone will be determined again. </p>
         */
        /*[deutsch]
         * <p>Erlaubt eine Aktualisierung, wenn sich die Zeitzonendatenbank
         * ge&auml;ndert hat (<i>dynamic update</i>). </p>
         *
         * <p>Der interne Cache wird entleert. Auch wird bei Bedarf die
         * Standard-Zeitzone neu ermittelt. </p>
         */
        public static void refresh() {

            synchronized (Timezone.class) {
                while (QUEUE.poll() != null) {}
                LAST_USED.clear();
            }

            zonalKeys = new ZonalKeys();
            CACHE.clear();

            if (ALLOW_SYSTEM_TZ_OVERRIDE) {
                currentSystemTZ = Timezone.getDefaultTZ();
            }

        }

        /**
         * <p>Aktivates or deactivates the internal cache. </p>
         *
         * <p>The timezone cache is active by default. Switching off the cache can
         * make the performance worse especially if the underlying {@code ZoneModelProvider}
         * itself has no cache. </p>
         *
         * @param   active  {@code true} if cache shall be active else {@code false}
         */
        /*[deutsch]
         * <p>Aktiviert oder deaktiviert den internen Cache. </p>
         *
         * <p>Standardm&auml;&szlig;ig ist der Cache aktiv. Ein Abschalten des
         * Cache kann die Performance insbesondere dann verschlechtern, wenn der
         * zugrundeliegende {@code ZoneModelProvider} selbst keinen Cache hat. </p>
         *
         * @param   active  {@code true} if cache shall be active else {@code false}
         */
        public static void setCacheActive(boolean active) {

            cacheActive = active;

            if (!active) {
                CACHE.clear();
            }

        }

        /**
         * <p>Updates the size of the internal timezone cache. </p>
         *
         * @param   minimumCacheSize    new minimum size of cache
         * @throws  IllegalArgumentException if the argument is negative
         */
        /*[deutsch]
         * <p>Konfiguriert die Gr&ouml;&szlig;e des internen Cache neu. </p>
         *
         * @param   minimumCacheSize    new minimum size of cache
         * @throws  IllegalArgumentException if the argument is negative
         */
        public static void setMinimumCacheSize(int minimumCacheSize) {

            if (minimumCacheSize < 0) {
                throw new IllegalArgumentException(
                    "Negative timezone cache size: " + minimumCacheSize);
            }

            NamedReference ref;

            while ((ref = (NamedReference) QUEUE.poll()) != null) {
                CACHE.remove(ref.tzid);
            }

            synchronized (Timezone.class) {
                softLimit = minimumCacheSize + 1;
                int n = LAST_USED.size() - minimumCacheSize;

                for (int i = 0; i < n; i++) {
                    LAST_USED.removeLast();
                }
            }

        }

    }

    private static class NamedReference
        extends SoftReference<Timezone> {

        //~ Instanzvariablen ----------------------------------------------

        private final String tzid;

        //~ Konstruktoren -------------------------------------------------

        NamedReference(
            Timezone tz,
            ReferenceQueue<Timezone> queue
        ) {
            super(tz, queue);
            this.tzid = tz.getID().canonical();

        }

    }

    private static class ZonalKeys {

        //~ Instanzvariablen ----------------------------------------------

        private final List<TZID> availables;
        private final List<TZID> availablesAndAliases;

        //~ Konstruktoren -------------------------------------------------

        ZonalKeys() {
            super();

            List<TZID> list = new ArrayList<TZID>(1024);
            List<TZID> listAndAliases = new ArrayList<TZID>(1024);
            list.add(ZonalOffset.UTC);

            for (Map.Entry<String, ZoneModelProvider> e : PROVIDERS.entrySet()) {
                ZoneModelProvider zp = e.getValue();

                if (
                    (zp == PLATFORM_PROVIDER)
                    && (DEFAULT_PROVIDER != PLATFORM_PROVIDER)
                ) {
                    continue;
                }

                for (String id : zp.getAvailableIDs()) {
                    TZID tzid = resolve(id);

                    // wegen resolve() genügt Vergleich per equals()
                    if (!list.contains(tzid)) {
                        list.add(tzid);
                    }
                }

                listAndAliases.addAll(list);

                for (String alias : zp.getAliases().keySet()) {
                    TZID tzid = resolve(alias);

                    // wegen resolve() genügt Vergleich per equals()
                    if (!listAndAliases.contains(tzid)) {
                        listAndAliases.add(tzid);
                    }
                }
            }

            Collections.sort(list, ID_COMPARATOR);
            Collections.sort(listAndAliases, ID_COMPARATOR);
            this.availables = Collections.unmodifiableList(list);
            this.availablesAndAliases = Collections.unmodifiableList(listAndAliases);

        }

    }

    private static class PlatformZoneProvider
        implements ZoneModelProvider, ZoneNameProvider {

        //~ Methoden ------------------------------------------------------

        @Override
        public Set<String> getAvailableIDs() {

            Set<String> ret = new HashSet<String>();
            String[] temp = java.util.TimeZone.getAvailableIDs();
            ret.addAll(Arrays.asList(temp));
            return ret;

        }

        @Override
        public Map<String, String> getAliases() {

            return Collections.emptyMap(); // JDK hat eingebaute Alias-Suche!

        }

        @Override
        public String getFallback() {

            return "";

        }

        @Override
        public String getName() {

            return "java.util.TimeZone";

        }

        @Override
        public String getLocation() {

            return "";

        }

        @Override
        public String getVersion() {

            return "";

        }

        @Override
        public TransitionHistory load(String zoneID) {

            return null; // leider keine öffentliche Historie!!!

        }

        @Override
        public ZoneNameProvider getSpecificZoneNameRepository() {

            return this;

        }

        @Override
        public Set<String> getPreferredIDs(
            Locale locale,
            boolean smart
        ) {

            return Collections.emptySet();

        }

        @Override
        public String getDisplayName(
            String tzid,
            NameStyle style,
            Locale locale
        ) {

            if (locale == null) {
                throw new NullPointerException("Missing locale.");
            } else if (tzid.isEmpty()) {
                return "";
            }

            // Note: There is no official API for querying the generic zone names
            java.util.TimeZone tz = PlatformTimezone.findZone(tzid);

            if (tz.getID().equals(tzid)) {
                return tz.getDisplayName(
                    style.isDaylightSaving(),
                    style.isAbbreviation()
                        ? java.util.TimeZone.SHORT
                        : java.util.TimeZone.LONG,
                    locale
                );
            }

            return "";

        }

        @Override
        public String getStdFormatPattern(
            boolean zeroOffset,
            Locale locale
        ) {

            return (zeroOffset ? "GMT" : "GMT\u00B1hh:mm");

        }

    }

}
