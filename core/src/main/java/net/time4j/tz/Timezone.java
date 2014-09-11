/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
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
import net.time4j.base.UnixTime;
import net.time4j.base.WallTime;

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Loads and keeps timezone data including the rules. </p>
 *
 * @author      Meno Hochschild
 * @concurrency All static methods are thread-safe while this class is
 *              immutable as long as the underlying timezone data are.
 */
/*[deutsch]
 * <p>L&auml;dt und h&auml;lt Zeitzonendaten mitsamt ihren Regeln. </p>
 *
 * @author      Meno Hochschild
 * @concurrency All static methods are thread-safe while this class is
 *              immutable as long as the underlying timezone data are.
 */
public abstract class Timezone
    implements Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int MRD = 1000000000;

    /**
     * <p>This standard strategy which is also used by JDK subtracts
     * the next defined offset from any local timestamp in order to
     * calculate the global time. </p>
     *
     * @see     #getOffset(GregorianDate,WallTime)
     */
    /*[deutsch]
     * <p>Diese auch vom JDK verwendete Standardstrategie zieht von einem
     * beliebigen lokalen Zeitstempel den jeweils n&auml;chstdefinierten
     * Offset ab, um die globale Zeit zu erhalten. </p>
     *
     * @see     #getOffset(GregorianDate,WallTime)
     */
    public static final TransitionStrategy DEFAULT_CONFLICT_STRATEGY =
        Strategy.DEFAULT;

    /**
     * <p>In addition to the  {@link #DEFAULT_CONFLICT_STRATEGY
     * standard strategy}, this strategy ensures the use of valid local
     * timestamps. </p>
     */
    /*[deutsch]
     * <p>Legt bei Transformationen von lokalen Zeitstempeln zu UTC fest,
     * da&szlig; nur in der Zeitzone g&uuml;ltige Zeitstempel zugelassen
     * werden. </p>
     *
     * <p>Ansonsten wird die {@link #DEFAULT_CONFLICT_STRATEGY
     * Standardstrategie} verwendet. </p>
     */
    public static final TransitionStrategy STRICT_MODE = Strategy.STRICT;

    private static final boolean ALLOW_SYSTEM_TZ_OVERRIDE =
        Boolean.getBoolean("net.time4j.allow.system.tz.override");

    private static volatile NameData NAME_DATA = null;
    private static volatile Timezone SYSTEM_TZ_CURRENT = null;
    private static volatile boolean ACTIVE = true;
    private static int SOFT_LIMIT = 11;

    private static final Map<String, TZID> PREDEFINED;
    private static final Map<String, Set<TZID>> TERRITORIES;
    private static final Provider PROVIDER;
    private static final ConcurrentMap<String, NamedReference> CACHE;
    private static final ReferenceQueue<Timezone> QUEUE;
    private static final LinkedList<Timezone> LAST_USED;

    private static final Timezone SYSTEM_TZ_ORIGINAL;

    static {
        CACHE = new ConcurrentHashMap<String, NamedReference>();
        QUEUE = new ReferenceQueue<Timezone>();
        LAST_USED = new LinkedList<Timezone>(); // strong references

        List<Class<? extends TZID>> areas;

        try {
            areas =
                loadPredefined(
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

        for (Class<? extends TZID> area : areas) {
            for (TZID tzid : area.getEnumConstants()) {
                temp1.put(tzid.canonical(), tzid);
            }
        }

        PREDEFINED = Collections.unmodifiableMap(temp1);

        Map<String, Set<TZID>> temp2 = new HashMap<String, Set<TZID>>();
        Class<?>[] emptyTypes =  new Class<?>[0];
        Object[] emptyParams = new Object[0];

        try {
            for (Class<? extends TZID> area : areas) {
                Method m = area.getDeclaredMethod("getCountry", emptyTypes);
                m.setAccessible(true);
                for (TZID tzid : area.getEnumConstants()) {
                    String country = (String) m.invoke(tzid, emptyParams);
                    addTerritory(temp2, country, tzid);
                }
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }

        if (areas.isEmpty()) {
            System.out.println(
                "Warning: olson-module is not available "
                + "so there are no preferred timezones for any locale.");
        } else {
            TZID svalbard = new NamedID("Arctic/Longyearbyen");
            temp2.put("SJ", Collections.singleton(svalbard));
        }

        TERRITORIES = Collections.unmodifiableMap(temp2);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (cl == null) {
            cl = Provider.class.getClassLoader();
        }

        ServiceLoader<Provider> sl = ServiceLoader.load(Provider.class, cl);
        Provider loaded = null;

        for (Provider provider : sl) {
            if (
                (loaded == null)
                || (provider.getVersion().compareTo(loaded.getVersion()) > 0)
            ) {
                loaded = provider;
                break;
            }
        }

        PROVIDER = (
            (loaded == null)
            ? new PlatformTZProvider()
            : loaded
        );

        Timezone systemTZ = null;

        try {
            String zoneID = System.getProperty("user.timezone");

            if (
                "Z".equals(zoneID)
                || "UTC".equals(zoneID)
            ) {
                systemTZ = ZonalOffset.UTC.getModel();
            } else if (zoneID != null) {
                systemTZ = Timezone.getTZ(new NamedID(zoneID), false);
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
            SYSTEM_TZ_CURRENT = SYSTEM_TZ_ORIGINAL;
        }

        // Aliases + Available-IDs
        NAME_DATA = new NameData(PROVIDER);
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

        return NAME_DATA.availables;

    }

    /**
     * <p>Gets a {@code Set} of preferred timezone IDs for given
     * ISO-3166-country code. </p>
     *
     * <p>This information is necessary to enable parsing of timezone names
     * and is only available if the olson-module &quot;net.time4j.tz.olson&quot;
     * is accessible in class path. </p>
     *
     * @param   locale  ISO-3166-alpha-2-country to be evaluated
     * @return  unmodifiable set of preferred timezone ids
     */
    /*[deutsch]
     * <p>Liefert die f&uuml;r einen gegebenen ISO-3166-L&auml;ndercode
     * bevorzugten Zeitzonenkennungen. </p>
     *
     * <p>Diese Information ist f&uuml;r die Interpretation von Zeitzonennamen
     * notwendig und steht nur dann zur Verf&uuml;gung, wenn das olson-Modul
     * &quot;net.time4j.tz.olson&quot; im Klassenpfad existiert. </p>
     *
     * @param   locale  ISO-3166-alpha-2-country to be evaluated
     * @return  unmodifiable set of preferred timezone ids
     */
    public static Set<TZID> getPreferredIDs(Locale locale) {

        Set<TZID> p = TERRITORIES.get(locale.getCountry());

        if (p == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(p);
        }

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
     * <p>Note: If the system timezone cannot be determined (for example
     * due to a wrong property value for &quot;user.timezone&quot;) then
     * this method will fall back to UTC timezone.. </p>
     *
     * @return  default timezone data of system
     * @see     java.util.TimeZone#getDefault()
     *          java.util.TimeZone.getDefault()
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
     * <p>Zu beachten: Kann die Standard-Zeitzone zum Beispiel wegen eines
     * falschen Property-Werts in &quot;user.timezone&quot; nicht interpretiert
     * werden, f&auml;llt diese Methode auf die UTC-Zeitzone zur&uuml;ck. </p>
     *
     * @return  default timezone data of system
     * @see     java.util.TimeZone#getDefault()
     *          java.util.TimeZone.getDefault()
     */
    public static Timezone ofSystem() {

        if (ALLOW_SYSTEM_TZ_OVERRIDE) {
            return SYSTEM_TZ_CURRENT;
        } else {
            return SYSTEM_TZ_ORIGINAL;
        }

    }

    /**
     * <p>Gets the timezone for given identifier. </p>
     *
     * @param   tzid    timezone id as interface
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Liefert die Zeitzone mit der angegebenen ID. </p>
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
     * @param   tzid    timezone id as String
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    /*[deutsch]
     * <p>Liefert die Zeitzone mit der angegebenen ID. </p>
     *
     * @param   tzid    timezone id as String
     * @return  timezone data
     * @throws  IllegalArgumentException if given timezone cannot be loaded
     */
    public static Timezone of(String tzid) {

        return Timezone.getTZ(tzid, true);

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
     * @param   tzid        preferred timezone id
     * @param   fallback    alternative timezone id
     * @return  timezone data
     */
    public static Timezone of(
        String tzid,
        TZID fallback
    ) {

        Timezone ret = Timezone.getTZ(tzid, false);

        if (ret == null) {
            ret = Timezone.getTZ(fallback, false);

            if (ret == null) {
                ret = Timezone.ofSystem();
            }
        }

        return ret;

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
     * <p>Calculates the offset for given global timestamp. </p>
     *
     * @param   ut      unix time
     * @return  shift in seconds which yields local time if added to unix time
     * @see     java.util.TimeZone#getOffset(long)
     *          java.util.TimeZone.getOffset(long)
     */
    /*[deutsch]
     * <p>Ermittelt die Zeitzonenverschiebung zum angegebenen Zeitpunkt auf
     * der UT-Weltzeitlinie in Sekunden. </p>
     *
     * @param   ut      unix time
     * @return  shift in seconds which yields local time if added to unix time
     * @see     java.util.TimeZone#getOffset(long)
     *          java.util.TimeZone.getOffset(long)
     */
    public abstract ZonalOffset getOffset(UnixTime ut);

    /**
     * <p>Calculates the offset for given local timestamp. </p>
     *
     * <p>In case of gaps or overlaps, this method uses the standard strategy
     * to get the next defined offset. This behaviour is conform to the JDK. </p>
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
     * auf dem lokalen Zeitstrahl wird die Standardstrategie verwendet, den
     * n&auml;chstdefinierten Offset zu ermitteln. Dieses Verhalten ist zum
     * JDK konform. </p>
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
     * in this timezone? </p>
     *
     * <p>The DST correction can be obtained as difference between total
     * offset and raw offset if the raw offset has not changed yet.
     * As alternative the DST correction can be obtained by evaluating
     * the transition offset history. </p>
     *
     * @param   ut      unix time
     * @return  {@code true} if the argument represents summer time
     *          else {@code false}
     * @see     java.util.TimeZone#inDaylightTime(java.util.Date)
     *          java.util.TimeZone.inDaylightTime(java.util.Date)
     */
    /*[deutsch]
     * <p>Herrscht zum angegebenen Zeitpunkt Sommerzeit in der Zeitzone? </p>
     *
     * <p>Die DST-Korrektur selbst kann als Differenz zwischen dem Gesamt-Offset
     * und dem Standard-Offset erhalten werden, wenn sich der Standard-Offset
     * historisch nicht ge&auml;ndert hat. Alternativ und genauer kann die
     * DST-Korrektur &uuml;ber die Offset-Historie ermittelt werden. </p>
     *
     * @param   ut      unix time
     * @return  {@code true} if the argument represents summer time
     *          else {@code false}
     * @see     java.util.TimeZone#inDaylightTime(java.util.Date)
     *          java.util.TimeZone.inDaylightTime(java.util.Date)
     */
    public abstract boolean isDaylightSaving(UnixTime ut);

    /**
     * <p>Gets the underlying offset transitions and rules if available. </p>
     *
     * @return  {@code TransitionHistory} or {@code null} if there is no
     *          better {@code Provider} than {@code java.util.TimeZone}
     */
    /*[deutsch]
     * <p>Liefert die zugrundeliegenden &Uuml;berg&auml;nge und Regeln,
     * falls vorhanden. </p>
     *
     * @return  {@code TransitionHistory} or {@code null} if there is no
     *          better {@code Provider} than {@code java.util.TimeZone}
     */
    public abstract TransitionHistory getHistory();

    /**
     * <p>Describes the underlying repository with name and optionally
     * location and version. </p>
     *
     * @return  String
     */
    /*[deutsch]
     * <p>Beschreibt die Zeitzonendatenbank mit Name und optional
     * Ort und Version. </p>
     *
     * @return  String
     */
    public static String getProviderInfo() {

        StringBuilder sb = new StringBuilder(128);
        sb.append(Timezone.class.getName());
        sb.append("[provider=");
        sb.append(PROVIDER.getName());

        if (!(PROVIDER instanceof PlatformTZProvider)) {
            sb.append(",location=");
            sb.append(PROVIDER.getLocation());
            sb.append(",version=");
            sb.append(PROVIDER.getVersion());
        }

        sb.append(']');
        return sb.toString();

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

        if (locale == null) {
            throw new NullPointerException("Missing locale.");
        }

        String id = this.getID().canonical();
        java.util.TimeZone tz = PlatformTimezone.findZone(id);

        if (tz.getID().equals(id)) {
            return tz.getDisplayName(
                style.isDaylightSaving(),
                style.isAbbreviation()
                    ? java.util.TimeZone.SHORT
                    : java.util.TimeZone.LONG,
                locale
            );
        } else {
            return id;
        }

    }

    private static Timezone getDefaultTZ() {

        String zoneID = java.util.TimeZone.getDefault().getID();
        return Timezone.of(zoneID, ZonalOffset.UTC);

    }

    private static Timezone getTZ(
        TZID tzid,
        boolean wantsException
    ) {

        // Liegt eine feste Verschiebung vor?
        if (tzid instanceof ZonalOffset) {
            return ((ZonalOffset) tzid).getModel();
        }

        return Timezone.getTZ(tzid.canonical(), wantsException);

    }

    private static Timezone getTZ(
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

        // enums bevorzugen
        TZID resolved = PREDEFINED.get(zoneID);

        if (resolved == null) {
            resolved = new NamedID(zoneID);
        }

        // java.util.TimeZone hat keine öffentliche Historie
        if (PROVIDER instanceof PlatformTZProvider) {
            PlatformTimezone test = new PlatformTimezone(resolved, zoneID);

            if (
                test.isGMT()
                && !zoneID.equals("GMT")
                && !zoneID.startsWith("UT")
                && !zoneID.equals("Z")
            ) {
                // JDK-Fallback verhindern => null
            } else {
                tz = test;
            }
        } else { // exakte Suche in Historie
            TransitionHistory history = PROVIDER.load(zoneID, false);

            if (history == null) {
                // Alias-Suche + Fallback-Option
                tz = Timezone.getZoneByAlias(resolved, zoneID);
            } else {
                tz = new HistorizedTimezone(resolved, history);
            }
        }

        // Ungültige ID?
        if (tz == null) {
            if (wantsException) {
                throw new IllegalArgumentException(
                    "Unknown timezone: " + zoneID);
            } else {
                return null;
            }
        }

        // bei Bedarf im Cache speichern
        if (ACTIVE) {
            NamedReference oldRef =
                CACHE.putIfAbsent(
                    zoneID,
                    new NamedReference(tz, QUEUE)
                );

            if (oldRef == null) {
                synchronized (Timezone.class) {
                    LAST_USED.addFirst(tz);

                    while (LAST_USED.size() >= SOFT_LIMIT) {
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
        TZID tzid,
        String zoneID
    ) {

        TransitionHistory history = null;
        String alias = zoneID;
        Map<String, String> aliases = NAME_DATA.aliases;

        while (
            (history == null)
            && ((alias = aliases.get(alias)) != null)
        ) {
            history = PROVIDER.load(alias, false);
        }

        if (
            (history == null)
            && PROVIDER.isFallbackEnabled()
        ) {
            history = PROVIDER.load(zoneID, true);
        }

        if (history == null) {
            return null;
        } else {
            return new HistorizedTimezone(tzid, history);
        }

    }

    @SuppressWarnings("unchecked")
    private static List<Class<? extends TZID>> loadPredefined(String... names)
        throws ClassNotFoundException {

        List<Class<? extends TZID>> classes =
            new ArrayList<Class<? extends TZID>>();

        for (String name : names) {
            Class<?> clazz = Class.forName("net.time4j.tz.olson." + name);

            if (TZID.class.isAssignableFrom(clazz)) {
                classes.add((Class<? extends TZID>) clazz);
            }
        }

        return Collections.unmodifiableList(classes);

    }

    private static void addTerritory(
        Map<String, Set<TZID>> map,
        String country,
        TZID tz
    ) {

        Set<TZID> preferred = map.get(country);

        if (preferred == null) {
            preferred = new LinkedHashSet<TZID>();
            map.put(country, preferred);
        }

        preferred.add(tz);

    }

    //~ Innere Interfaces -------------------------------------------------

    /**
     * <p>SPI interface which encapsulates the timezone repository and
     * provides all necessary data for a given timezone id. </p>
     *
     * <p>Implementations are usually stateless and should normally not
     * try to manage a cache. Instead Time4J uses its own cache. The
     * fact that this interface is used per {@code java.util.ServiceLoader}
     * requires a concrete implementation to offer a public no-arg
     * constructor. </p>
     *
     * @author  Meno Hochschild
     * @see     java.util.ServiceLoader
     */
    /*[deutsch]
     * <p>SPI-Interface, das eine Zeitzonendatenbank kapselt und passend zu
     * einer Zeitzonen-ID (hier als String statt als {@code TZID}) die
     * Zeitzonendaten liefert. </p>
     *
     * <p>Implementierungen sind in der Regel zustandslos und halten keinen
     * Cache. Letzterer sollte normalerweise der Klasse {@code Timezone}
     * vorbehalten sein. Weil dieses Interface mittels eines
     * {@code java.util.ServiceLoader} genutzt wird, mu&szlig; eine
     * konkrete Implementierung einen &ouml;ffentlichen Konstruktor ohne
     * Argumente definieren. </p>
     *
     * @author  Meno Hochschild
     * @see     java.util.ServiceLoader
     */
    public interface Provider {

        //~ Methoden ------------------------------------------------------

        /**
         * <p>Gets all available and supported timezone identifiers. </p>
         *
         * @return  unmodifiable set of timezone ids
         * @see     java.util.TimeZone#getAvailableIDs()
         */
        /*[deutsch]
         * <p>Liefert alle verf&uuml;gbaren Zeitzonenkennungen. </p>
         *
         * @return  unmodifiable set of timezone ids
         * @see     java.util.TimeZone#getAvailableIDs()
         */
        Set<String> getAvailableIDs();

        /**
         * <p>Gets an alias table whose keys represent alternative identifiers
         * mapped to other aliases or finally canonical timezone IDs.. </p>
         *
         * <p>Example: &quot;PST&quot; => &quot;America/Los_Angeles&quot;. </p>
         *
         * @return  map from all timezone aliases to canoncial ids
         */
        /*[deutsch]
         * <p>Liefert eine Alias-Tabelle, in der die Schl&uuml;ssel alternative
         * Zonen-IDs darstellen und in der die zugeordneten Werte wieder
         * Aliasnamen oder letztlich kanonische Zonen-IDs sind. </p>
         *
         * <p>Beispiel: &quot;PST&quot; => &quot;America/Los_Angeles&quot;. </p>
         *
         * @return  map from all timezone aliases to canoncial ids
         */
        Map<String, String> getAliases();

        /**
         * <p>Loads an offset transition table for given timezone id. </p>
         *
         * <p>This callback method has a second argument which indicates if
         * Time4J wants this method to return exactly matching data (default)
         * or permits the use of aliases (only possible if the method
         * {@code isFallbackEnabled()} returns {@code true}). </p>
         *
         * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
         * @param   fallback    fallback allowed if a timezone id cannot be
         *                      found, not even by alias?
         * @return  timezone history or {@code null} if there are no data
         * @throws  IllegalStateException if timezone database is broken
         * @see     #getAvailableIDs()
         * @see     #getAliases()
         * @see     #isFallbackEnabled()
         * @see     java.util.TimeZone#getTimeZone(String)
         */
        /*[deutsch]
         * <p>L&auml;dt die Zeitzonendaten zur angegebenen Zonen-ID. </p>
         *
         * <p>Diese Methode wird von {@code Timezone} aufgerufen. Das zweite
         * Argument ist normalerweise {@code false}, so da&szlig; es sich um
         * eine exakte Suchanforderung handelt. Nur wenn die Methode
         * {@code isFallbackEnabled()} den Wert {@code true} zur&uuml;ckgibt
         * und vorher weder die exakte Suche noch die Alias-Suche erfolgreich
         * waren, kann ein erneuter Aufruf mit dem zweiten Argument
         * {@code true} erfolgen. </p>
         *
         * @param   zoneID      timezone id (i.e. &quot;Europe/London&quot;)
         * @param   fallback    fallback allowed if a timezone id cannot be
         *                      found, not even by alias?
         * @return  timezone history or {@code null} if there are no data
         * @throws  IllegalStateException if timezone database is broken
         * @see     #getAvailableIDs()
         * @see     #getAliases()
         * @see     #isFallbackEnabled()
         * @see     java.util.TimeZone#getTimeZone(String)
         */
        TransitionHistory load(
            String zoneID,
            boolean fallback
        );

        /**
         * <p>Determines if in case of a failed search another timezone should
         * be permitted as alternative with possibly different rules. </p>
         *
         * @return  boolean
         * @see     #load(String, boolean)
         */
        /*[deutsch]
         * <p>Soll eine alternative Zeitzone mit eventuell anderen Regeln
         * geliefert werden, wenn die Suche nach einer Zeitzone erfolglos
         * war? </p>
         *
         * @return  boolean
         * @see     #load(String, boolean)
         */
        boolean isFallbackEnabled();

        /**
         * <p>Gets the name of the underlying repository. </p>
         *
         * <p>The Olson/IANA-repository has the name
         * &quot;TZDB&quot;. </p>
         *
         * @return  String
         */
        /*[deutsch]
         * <p>Gibt den Namen dieser Zeitzonendatenbank an. </p>
         *
         * <p>Die Olson/IANA-Zeitzonendatenbank hat den Namen
         * &quot;TZDB&quot;. </p>
         *
         * @return  String
         */
        String getName();

        /**
         * <p>Describes the location or source of the repository. </p>
         *
         * @return  String which refers to an URI or empty if unknown
         */
        /*[deutsch]
         * <p>Beschreibt die Quelle der Zeitzonendatenbank. </p>
         *
         * @return  String which refers to an URI or empty if unknown
         */
        String getLocation();

        /**
         * <p>Queries the version of the underlying repository. </p>
         *
         * <p>In most cases the version has the Olson format starting with
         * a four-digit year number followed by a small letter in range
         * a-z. </p>
         *
         * @return  String (for example &quot;2011n&quot;) or empty if unknown
         */
        /*[deutsch]
         * <p>Liefert die Version der Zeitzonendatenbank. </p>
         *
         * <p>Meist liegt die Version im Olson-Format vor. Dieses Format sieht
         * als Versionskennung eine 4-stellige Jahreszahl gefolgt von einem
         * Buchstaben im Bereich a-z vor. </p>
         *
         * @return  String (for example &quot;2011n&quot;) or empty if unknown
         */
        String getVersion();

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

            NAME_DATA = new NameData(PROVIDER);
            CACHE.clear();

            if (ALLOW_SYSTEM_TZ_OVERRIDE) {
                SYSTEM_TZ_CURRENT = Timezone.getDefaultTZ();
            }

        }

        /**
         * <p>Aktivates or deactivates the internal cache. </p>
         *
         * <p>The timezone cache is active by default. Switching off the cache can
         * make the performance worse especially if the underlying {@code Provider}
         * itself has no cache. </p>
         *
         * @param   active  {@code true} if chache shall be active
         *                  else {@code false}
         */
        /*[deutsch]
         * <p>Aktiviert oder deaktiviert den internen Cache. </p>
         *
         * <p>Standardm&auml;&szlig;ig ist der Cache aktiv. Ein Abschalten des
         * Cache kann die Performance insbesondere dann verschlechtern, wenn der
         * zugrundeliegende {@code Provider} selbst keinen Cache hat. </p>
         *
         * @param   active  {@code true} if chache shall be active
         *                  else {@code false}
         */
        public static void setCacheActive(boolean active) {

            ACTIVE = active;

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
                SOFT_LIMIT = minimumCacheSize + 1;
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

    private static class NamedID
        implements TZID, Serializable {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final long serialVersionUID = -4889632013137688471L;

        //~ Instanzvariablen ----------------------------------------------

        /**
         * @serial  timezone id
         */
        private final String tzid;

        //~ Konstruktoren -------------------------------------------------

        NamedID(String tzid) {
            super();

            this.tzid = tzid;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public String canonical() {

            return this.tzid;

        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof NamedID) {
                NamedID that = (NamedID) obj;
                return this.tzid.equals(that.tzid);
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            return this.tzid.hashCode();

        }

        @Override
        public String toString() {

            return this.getClass().getName() + "@" + this.tzid;

        }

    }

    private static class NameData {

        //~ Instanzvariablen ----------------------------------------------

        private final Map<String, String> aliases;
        private final List<TZID> availables;

        //~ Konstruktoren -------------------------------------------------

        NameData(Provider provider) {
            super();

            Set<String> ids = provider.getAvailableIDs();
            List<TZID> list = new ArrayList<TZID>();
            list.add(ZonalOffset.UTC);

            for (String id : ids) {
                TZID tzid = PREDEFINED.get(id);

                if (tzid == null) {
                    list.add(new NamedID(id));
                } else if (tzid != ZonalOffset.UTC) {
                    list.add(tzid);
                }
            }

            Collections.sort(
                list,
                new Comparator<TZID>() {
                    @Override
                    public int compare(
                        TZID o1,
                        TZID o2
                    ) {
                        return o1.canonical().compareTo(o2.canonical());
                    }
                }
            );

            this.availables =
                Collections.unmodifiableList(list);
            this.aliases =
                Collections.unmodifiableMap(
                    new HashMap<String, String>(provider.getAliases()));

        }

    }

    private static class PlatformTZProvider
        implements Provider {

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
        public boolean isFallbackEnabled() {

            return false; // JDK hat eingebauten Fallback-Mechanismus!

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
        public TransitionHistory load(
            String zoneID,
            boolean fallback
        ) {

            return null; // keine öffentliche Historie

        }

    }

    private static enum Strategy
        implements TransitionStrategy {

        //~ Statische Felder/Initialisierungen ----------------------------

        DEFAULT, STRICT;

        //~ Methoden ------------------------------------------------------

        @Override
        public ZonalOffset resolve(
            GregorianDate date,
            WallTime time,
            Timezone tz
        ) {

            if (
                (this == STRICT)
                && tz.isInvalid(date, time)
            ) {
                throw new IllegalArgumentException(
                    "Invalid local timestamp due to timezone transition: "
                    + date + time
                    + " [" + tz.getID() + "]"
                );
            }

            return tz.getOffset(date, time);

        }

    }

}
