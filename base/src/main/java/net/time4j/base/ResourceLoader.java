/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2016 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (ResourceLoader.java) is part of project Time4J.
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

package net.time4j.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ServiceLoader;


/**
 * <p>Defines a general access point of loading any text resources and services. </p>
 *
 * <p><strong>Specification:</strong>
 * All external subclasses must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Definiert einen allgemeinen Zugriffspunkt zum Laden von Textressourcen und Services. </p>
 *
 * <p><strong>Specification:</strong>
 * All external subclasses must have a public no-arg constructor. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public abstract class ResourceLoader {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Name of system property responsible for getting an external instance as fully qualified class name. </p>
     *
     * <p>Time4J will throw an {@code Error} if the configuration entry is wrong. </p>
     */
    /**
     * <p>Name der <i>system property</i>, &uuml;ber die eine externe Instanz mittels eines vollst&auml;ndig
     * qualifizierten Klassennamens verwendet wird. </p>
     *
     * <p>Time4J wird einen {@code Error} werfen, wenn der Konfigurationseintrag falsch ist. </p>
     */
    public static final String EXTERNAL_RESOURCE_LOADER = "net.time4j.base.ResourceLoader";

    /**
     * <p>Name of system property controlling if the use of classloader should be enforced instead
     * of trying an URI-construction first. </p>
     *
     * <p>The value is either &quot;true&quot; or &quot;false&quot; (default). This property will be
     * ignored on Android platforms. </p>
     *
     * @since   3.16/4.13
     */
    /**
     * <p>Name der <i>system property</i>, die kontrolliert, ob nur der <i>Classloader</i> statt einer
     * URI-Konstruktion verwendet werden soll. </p>
     *
     * <p>Der Wert ist entweder &quot;true&quot; oder &quot;false&quot; (Vorgabe). Auf Android wird
     * diese Eigenschaft ignoriert. </p>
     *
     * @since   3.16/4.13
     */
    public static final String USE_OF_CLASSLOADER_ONLY = "net.time4j.base.useClassloaderOnly";

    private static final boolean ANDROID;
    private static final ResourceLoader INSTANCE;
    private static final boolean ENFORCE_USE_OF_CLASSLOADER;

    static {
        ANDROID = "Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"));
        ENFORCE_USE_OF_CLASSLOADER = !ANDROID && Boolean.getBoolean(USE_OF_CLASSLOADER_ONLY);
        String rl = System.getProperty(EXTERNAL_RESOURCE_LOADER);

        if (rl == null) {
            INSTANCE = new StdResourceLoader();
        } else {
            try {
                INSTANCE = (ResourceLoader) Class.forName(rl).newInstance();
            } catch (Exception e) {
                throw new AssertionError(
                    "Wrong configuration of external resource loader: " + e.getMessage());
            }
        }
    }

    //~ Konstruktoren -----------------------------------------------------

    /**
     * For subclasses only.
     *
     * @see     #getInstance()
     */
    protected ResourceLoader() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Applications should never use the constructor but this static method to achieve a general instance. </p>
     *
     * @return  {@code ResourceLoader}
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Anwendungen sollte niemals den Konstruktor, sondern diese statische Fabrikmethode benutzen, um eine
     * allgemeine Instanz zu erhalten. </p>
     *
     * @return  {@code ResourceLoader}
     * @since   3.5/4.3
     */
    public static ResourceLoader getInstance() {

        return INSTANCE;

    }

    /**
     * <p>Constructs an URI for given module resource. </p>
     *
     * <p>Attention: Some implementations might yield an uri without verifying if the uri resource really exists. </p>
     *
     * @param   moduleName      name of related time4j-module
     * @param   moduleRef       module-specific class reference
     * @param   path            path to text resource
     * @return  uri of resource or {@code null} if unable to locate the resource
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erstellt einen URI f&uuml;r die angegebene Ressource. </p>
     *
     * <p>Achtung: Einige Implementierungen k&ouml;nnen einen URI liefern, ohne die reale Existenz der URI-Ressource
     * zu pr&uuml;fen. </p>
     *
     * @param   moduleName      name of related time4j-module
     * @param   moduleRef       module-specific class reference
     * @param   path            path to text resource
     * @return  uri of resource or {@code null} if unable to locate the resource
     * @since   3.5/4.3
     */
    public abstract URI locate(
        String moduleName,
        Class<?> moduleRef,
        String path
    );

    /**
     * <p>Loads given URI-resource as input stream. </p>
     *
     * <p>Callers are responsible for closing the result stream. </p>
     *
     * @param   uri         uniform resource identifier as result of locate-method (optional)
     * @param   noCache     avoid caching?
     * @return  input stream or {@code null} if the resource could not be opened
     * @see     #locate(String, Class, String)
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>L&auml;dt die angegebene URI-Ressource als {@code InputStream}. </p>
     *
     * <p>Aufrufer sind daf&uuml;r verantwortlich, den Eingabestrom zu schlie&szlig;en </p>
     *
     * @param   uri         uniform resource identifier as result of locate-method (optional)
     * @param   noCache     avoid caching?
     * @return  input stream or {@code null} if the resource could not be opened
     * @see     #locate(String, Class, String)
     * @since   3.5/4.3
     */
    public abstract InputStream load(
        URI uri,
        boolean noCache
    );

    /**
     * <p>Loads a resource as input stream based on the classloader of given module reference. </p>
     *
     * <p>Callers are responsible for closing the result stream. </p>
     *
     * @param   moduleRef   module-specific class reference
     * @param   path        path to text resource (must be understandable by class loaders)
     * @param   noCache     avoid caching?
     * @return  input stream
     * @throws  IOException if the stream cannot be opened or if this method is called on Android platforms
     * @since   3.16/4.13
     */
    /*[deutsch]
     * <p>L&auml;dt eine Ressource als {@code InputStream}, indem der <i>Classloader</i> der
     * angegebenen Modulreferenz herangezogen wird. </p>
     *
     * <p>Aufrufer sind daf&uuml;r verantwortlich, den Eingabestrom zu schlie&szlig;en </p>
     *
     * @param   moduleRef   module-specific class reference
     * @param   path        path to text resource (must be understandable by class loaders)
     * @param   noCache     avoid caching?
     * @return  input stream
     * @throws  IOException if the stream cannot be opened or if this method is called on Android platforms
     * @since   3.16/4.13
     */
    public final InputStream load(
        Class<?> moduleRef,
        String path,
        boolean noCache
    ) throws IOException {

        if (ANDROID) {
            throw new FileNotFoundException(path);
        }

        URL url = moduleRef.getClassLoader().getResource(path);

        if (url == null) {
            throw new FileNotFoundException(path);
        } else if (noCache) {
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.connect(); // explicit for clarity
            return conn.getInputStream();
        } else {
            return url.openStream();
        }

    }

    /**
     * <p>Finds a collection of service providers available for given service provider interface. </p>
     *
     * @param   <S> generic service type
     * @param   serviceInterface    service provider interface
     * @return  iterable collection of service providers
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Findet eine Menge von <i>Service Provider</i>-Objekten, die zum angegebenen Interface
     * verf&uuml;gbar sind. </p>
     *
     * @param   <S> generic service type
     * @param   serviceInterface    service provider interface
     * @return  iterable collection of service providers
     * @since   3.5/4.3
     */
    public abstract <S> Iterable<S> services(Class<S> serviceInterface);

    //~ Innere Klassen ----------------------------------------------------

    private static class StdResourceLoader
        extends ResourceLoader {

        //~ Konstruktoren -------------------------------------------------

        protected StdResourceLoader() {
            super();

            if (ANDROID) {
                throw new IllegalStateException("The module time4j-android is not active. Check your configuration.");
            }

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public URI locate(
            String moduleName,
            Class<?> moduleRef,
            String path
        ) {

            // try uri construction whose initialization time is much quicker than querying the class loader
            String constructedUri = null;

            try {
                ProtectionDomain pd = moduleRef.getProtectionDomain(); // null on android platform
                CodeSource cs = ((pd == null) ? null : pd.getCodeSource());

                if (cs != null) {
                    constructedUri = cs.getLocation().toExternalForm();
                    if (constructedUri.endsWith(".jar")) {
                        constructedUri = "jar:" + constructedUri + "!/";
                    }
                    constructedUri += path;
                    return new URI(constructedUri);
                }
            } catch (SecurityException se) {
                // use fallback via class loader later
            } catch (URISyntaxException e) {
                System.err.println("Warning: malformed resource path = " + constructedUri);
            }

            return null;

        }

        @Override
        public InputStream load(
            URI uri,
            boolean noCache
        ) {

            if ((uri == null) || ENFORCE_USE_OF_CLASSLOADER) {
                return null;
            }

            try {
                URL url = uri.toURL();

                if (noCache) {
                    URLConnection conn = url.openConnection();
                    conn.setUseCaches(false);
                    conn.connect(); // explicit for clarity
                    return conn.getInputStream();
                } else {
                    return url.openStream();
                }
            } catch (IOException ioe) {
                if (uri.toString().contains(".repository")) { // print warning for tzdata-repository only
                    System.err.println(
                        "Warning: Loading of resource " + uri + " failed (" + ioe.getMessage() + "). "
                        + "Consider setting the system property \""
                        + USE_OF_CLASSLOADER_ONLY + "\" for reducing overhead.");
                    ioe.printStackTrace(System.err);
                }
                return null;
            }

        }

        @Override
        public <S> Iterable<S> services(Class<S> serviceInterface) {

            return ServiceLoader.load(serviceInterface, serviceInterface.getClassLoader());

        }

    }

}
