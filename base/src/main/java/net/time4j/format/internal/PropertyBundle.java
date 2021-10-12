/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2019 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (PropertyBundle.java) is part of project Time4J.
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

package net.time4j.format.internal;

import net.time4j.base.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>Replacement for {@code java.util.ResourceBundle} when accessing the own property resources. </p>
 *
 * <p>Background: Java 9 or later does not permit the usage of {@code java.util.ResourceBundle.Control}
 * on the module path to define specific search strategies. </p>
 *
 * @author  Meno Hochschild
 * @since   5.0
 */
/*[deutsch]
 * <p>Ersatz f&uuml;r {@code java.util.ResourceBundle}, wenn es um den Zugang
 * zu eigenen <i>property</p>-Ressourcen geht. </p>
 *
 * <p>Hintergrund: Java 9 oder sp&auml;ter gestattet nicht den Gebrauch von {@code java.util.ResourceBundle.Control}
 * auf dem Modulpfad, um spezifische Suchstrategien zu definieren. </p>
 *
 * @author  Meno Hochschild
 * @since   5.0
 */
public final class PropertyBundle {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final ConcurrentMap<CacheKey, BundleReference> CACHE = new ConcurrentHashMap<>(32);
    private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue<>();

    //~ Instanzvariablen --------------------------------------------------

    private final PropertyBundle parent;
    private final Map<String, String> key2values;
    private final String baseName;
    private final Locale bundleLocale;

    //~ Konstruktoren -----------------------------------------------------

    private PropertyBundle(
        UTF8ResourceReader reader,
        String baseName,
        Locale bundleLocale
    ) throws IOException {
        super();

        this.parent = null;
        this.baseName = baseName;
        this.bundleLocale = bundleLocale;

        Map<String, String> map = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || (line.charAt(0) == '#')) {
                continue; // ignore white space or comment
            }
            for (int i = 0, n = line.length(); i < n; i++) {
                char c = line.charAt(i);
                if ((c == '=') && (i + 1 < n)) {
                    map.put(line.substring(0, i), line.substring(i + 1));
                    break;
                }
            }
        }

        this.key2values = Collections.unmodifiableMap(map);

    }

    private PropertyBundle(
        PropertyBundle ref,
        PropertyBundle parent
    ) {
        super();

        this.parent = parent;
        this.baseName = ref.baseName;
        this.bundleLocale = ref.bundleLocale;
        this.key2values = ref.key2values;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Tries to load a new property resource bundle. </p>
     *
     * @param   baseName    the base name
     * @param   locale      the locale containing language, country and variant
     * @return  property bundle if successfully loaded
     * @throws  MissingResourceException if no bundle could be loaded
     * @throws  IllegalArgumentException if the base name is empty
     */
    /*[deutsch]
     * <p>Versucht, ein neues Ressourcenb&uuml;ndel zu laden. </p>
     *
     * @param   baseName    the base name
     * @param   locale      the locale containing language, country and variant
     * @return  property bundle if successfully loaded
     * @throws  MissingResourceException if no bundle could be loaded
     * @throws  IllegalArgumentException if the base name is empty
     */
    public static PropertyBundle load(
        String baseName,
        Locale locale
    ) {

        if (baseName.isEmpty()) { // includes NPE-check
            throw new IllegalArgumentException("Base name must not be empty.");
        } else if (locale == null) {
            throw new NullPointerException("Missing locale.");
        }

        CacheKey cacheKey = new CacheKey(baseName, locale);
        BundleReference bundleRef = CACHE.get(cacheKey);
        PropertyBundle bundle;

        if (bundleRef != null) {
            bundle = bundleRef.get();
            bundleRef = null; // helps the GC
            if (bundle != null) {
                return bundle;
            }
        }

        Object ref;

        while ((ref = REFERENCE_QUEUE.poll()) != null) {
            CACHE.remove(((BundleReference) ref).cacheKey);
        }

        List<PropertyBundle> bundles = new ArrayList<>();

        for (Locale candidate : getCandidateLocales(locale)) {
            try {
                bundle = newBundle(baseName, candidate);
                if (bundle != null) {
                    bundles.add(bundle);
                }
            } catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        }

        if (bundles.isEmpty()) {
            throw new MissingResourceException(
                "Cannot find resource bundle for: " + toResourceName(baseName, locale),
                PropertyBundle.class.getName(),
                "");
        }

        for (int i = bundles.size() - 1; i >= 1; i--) {
            bundles.set(i - 1, bundles.get(i - 1).withParent(bundles.get(i)));
        }

        bundle = bundles.get(0);
        CACHE.putIfAbsent(cacheKey, new BundleReference(bundle, cacheKey));
        return bundle;

    }

    /**
     * <p>Obtains the property value for given key. </p>
     *
     * @param   key     the key of property resource
     * @return  the value associated with given key
     * @throws  MissingResourceException if the value cannot be found
     */
    /*[deutsch]
     * <p>Liefert den mit dem angegebenen Schl&uuml;ssel verkn&uuml;pften Eigenschaftenwert. </p>
     *
     * @param   key     the key of property resource
     * @return  the value associated with given key
     * @throws  MissingResourceException if the value cannot be found
     */
    public String getString(String key) {

        if (key == null) {
            throw new NullPointerException("Missing resource key.");
        }

        PropertyBundle p = this;

        do {
            String value = p.key2values.get(key);
            if (value != null) {
                return value;
            }
        } while ((p = p.parent) != null);

        throw new MissingResourceException(
            "Cannot find property resource for: " + toResourceName(this.baseName, this.bundleLocale) + "=>" + key,
            PropertyBundle.class.getName(),
            key);

    }

    /**
     * <p>Determines if the property value for given key exists. </p>
     *
     * @param   key     the key of property resource
     * @return  {@code true} if the property for given key exists else {@code false}
     */
    /*[deutsch]
     * <p>Ermittelt, ob zum angegebenen Schl&uuml;ssel ein Eigenschaftenwert vorhanden ist. </p>
     *
     * @param   key     the key of property resource
     * @return  {@code true} if the property for given key exists else {@code false}
     */
    public boolean containsKey(String key) {

        if (key == null) {
            throw new NullPointerException("Missing resource key.");
        }

        PropertyBundle p = this;

        do {
            String value = p.key2values.get(key);
            if (value != null) {
                return true;
            }
        } while ((p = p.parent) != null);

        return false;

    }

    /**
     * <p>Determines the set of all associated keys. </p>
     *
     * @return  Set
     */
    /*[deutsch]
     * <p>Ermittelt alle verkn&uuml;pften Schl&uuml;ssel. </p>
     *
     * @return  Set
     */
    public Set<String> keySet() {

        PropertyBundle p = this;
        Set<String> keys = new HashSet<>(p.key2values.keySet());

        while ((p = p.parent) != null) {
            keys.addAll(p.key2values.keySet());
        }

        return Collections.unmodifiableSet(keys);

    }

    /**
     * <p>Obtains the associated locale this bundle was requested for.</p>
     *
     * @return  Locale
     */
    /*[deutsch]
     * <p>Liefert die verkn&uuml;pfte {@code Locale}, f&uuml;r die diese Instanz angefordert wurde.</p>
     *
     * @return  Locale
     */
    public Locale getLocale() {

        return this.bundleLocale;

    }

    /**
     * <p>Removes all entries from static cache. </p>
     */
    /*[deutsch]
     * <p>Entfernt alle Eintr&auml;ge vom statischen Cache. </p>
     */
    public static void clearCache() {

        while ((REFERENCE_QUEUE.poll()) != null) {}
        CACHE.clear();

    }

    /**
     * <p>Obtains the keys of this bundle only (ignoring the parents). </p>
     *
     * @return  property keys contained only in this bundle
     */
    /*[deutsch]
     * <p>Liefert die internen Schl&uuml;ssel nur dieser Instanz ohne die der Eltern. </p>
     *
     * @return  property keys contained only in this bundle
     */
    public Set<String> getInternalKeys() {

        return this.key2values.keySet();

    }

    /**
     * <p>Constructs a list of candidate locales. </p>
     *
     * @param   locale  requested locale
     * @return  list of candidates in roughly same order as in {@code java.util.ResourceBundle}
     */
    /*[deutsch]
     * <p>Erstellt eine Kandidatenliste. </p>
     *
     * @param   locale  requested locale
     * @return  list of candidates in roughly same order as in {@code java.util.ResourceBundle}
     */
    public static List<Locale> getCandidateLocales(Locale locale) {

        String language = LanguageMatch.getAlias(locale);
        String country = FormatUtils.getRegion(locale);
        String variant = locale.getVariant();

        if (language.equals("zh") && locale.getScript().equals("Hant")) {
            country = "TW";
        }

        List<Locale> list = new LinkedList<>();

        if (!variant.isEmpty()) {
            list.add(new Locale(language, country, variant));
        }

        if (!country.isEmpty()) {
            list.add(new Locale(language, country, ""));
        }

        if (!language.isEmpty()) {
            list.add(new Locale(language, "", ""));
            if (language.equals("nn")) {
                list.add(new Locale("no", "", ""));
            }
        }

        list.add(Locale.ROOT);
        return list;

    }

    private PropertyBundle withParent(PropertyBundle parent) {

        if (parent == null) {
            return this;
        }

        return new PropertyBundle(this, parent);

    }

    private static PropertyBundle newBundle(
        String baseName,
        Locale locale
    ) throws IOException {

        PropertyBundle bundle = null;
        String resourceName = toResourceName(baseName, locale);

        URI uri = ResourceLoader.getInstance().locate("base", PropertyBundle.class, resourceName);
        InputStream stream = ResourceLoader.getInstance().load(uri, true);

        if (stream == null) {
            try {
                stream = ResourceLoader.getInstance().load(PropertyBundle.class, resourceName, true);
            } catch (IOException ioe) {
                // okay, maybe the resource is simply not there
                return null;
            }
        }

        if (stream != null) {
            UTF8ResourceReader reader = null;

            try {
                reader = new UTF8ResourceReader(stream);
                bundle = new PropertyBundle(reader, baseName, locale);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }

        return bundle;

    }

    private static String toResourceName(
        String baseName,
        Locale locale
    ) {

        String language = LanguageMatch.getAlias(locale);
        String country = FormatUtils.getRegion(locale);
        String variant = locale.getVariant();

        StringBuilder sb = new StringBuilder(baseName.length() + 20);
        sb.append(baseName.replace('.', '/'));

        if (!language.isEmpty()) {
            sb.append('_').append(language);
            if (!variant.isEmpty()) {
                sb.append('_').append(country).append('_').append(variant);
            } else if (!country.isEmpty()) {
                sb.append('_').append(country);
            }
        }

        return sb.append(".properties").toString();

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class CacheKey {

        //~ Instanzvariablen ----------------------------------------------

        private final String baseName;
        private final Locale locale;

        //~ Konstruktoren -------------------------------------------------

        CacheKey(
            String baseName,
            Locale locale
        ) {
            super();

            this.baseName = baseName;
            this.locale = locale;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            } else if (obj instanceof CacheKey) {
                CacheKey that = (CacheKey) obj;
                return this.baseName.equals(that.baseName) && this.locale.equals(that.locale);
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            int h = (this.baseName.hashCode() << 3);
            h ^= locale.hashCode();
            return h;

        }

        @Override
        public String toString() {

            return this.baseName + "/" + this.locale;

        }

    }

    private static class BundleReference extends SoftReference<PropertyBundle> {

        //~ Instanzvariablen ----------------------------------------------

        private CacheKey cacheKey;

        //~ Konstruktoren -------------------------------------------------

        BundleReference(
            PropertyBundle referent,
            CacheKey key
        ) {
            super(referent, REFERENCE_QUEUE);

            this.cacheKey = key;

        }

    }

}
