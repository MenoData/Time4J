/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ServiceLoader;


/**
 * <p>Defines a general access point of loading any text resources and services. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
/*[deutsch]
 * <p>Definiert einen allgemeinen Zugriffspunkt zum Laden von Textressourcen und Services. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 */
public abstract class ResourceLoader {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Name of system property responsible for getting an external instance. </p>
     *
     * <p>Time4J will throw an {@code Error} if the configuration entry is wrong. </p>
     *
     * @doctags.spec    All external subclasses must have a public no-arg constructor.
     */
    /**
     * <p>Name der <i>system property</i>, &uuml;ber die eine externe Instanz verwendet wird. </p>
     *
     * <p>Time4J wird einen {@code Error} werfen, wenn der Konfigurationseintrag falsch ist. </p>
     *
     * @doctags.spec    All external subclasses must have a public no-arg constructor.
     */
    public static final String EXTERNAL_RESOURCE_LOADER = "net.time4j.base.ResourceLoader";

    private static final boolean ANDROID;
    private static final ResourceLoader INSTANCE;

    static {
        ANDROID = "Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"));
        String rl = System.getProperty(EXTERNAL_RESOURCE_LOADER);

        if (rl == null) {
            INSTANCE = new StdResourceLoader();
        } else {
            try {
                INSTANCE = (ResourceLoader) Class.forName(rl).newInstance();
            } catch (Exception e) {
                throw new AssertionError("Wrong configuration of external resource loader!", e);
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
     * <p>Constructs an URL for given module resource. </p>
     *
     * <p>Some implementations might yield an url without verifying if the url resource really exists. </p>
     *
     * @param   moduleName      name of related time4j-module
     * @param   moduleRef       module-specific class reference
     * @param   path            path to text resource (must be understandable by class loaders)
     * @return  url of resource or {@code null} if unable to locate the resource
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erstellt einen URL f&uuml;r die angegebene Ressource. </p>
     *
     * <p>Einige Implementierungen k&ouml;nnen einen URL liefern, ohne die reale Existenz der URL-Ressource
     * zu pr&uuml;fen. </p>
     *
     * @param   moduleName      name of related time4j-module
     * @param   moduleRef       module-specific class reference
     * @param   path            path to text resource (must be understandable by class loaders)
     * @return  url of resource or {@code null} if unable to locate the resource
     * @since   3.5/4.3
     */
    public abstract URL locate(
        String moduleName,
        Class<?> moduleRef,
        String path
    );

    /**
     * <p>Loads given resource as input stream. </p>
     *
     * <p>Callers are responsible for closing the result stream. </p>
     *
     * @param   url         uniform resource locator as result of locate-method (optional)
     * @param   noCache     avoid caching?
     * @return  input stream or {@code null} if the resource could not be opened
     * @see     #locate(String, Class, String)
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>L&auml;dt die angegebene Ressource als {@code InputStream}. </p>
     *
     * <p>Aufrufer sind daf&uuml;r verantwortlich, den Eingabestrom zu schlie&szlig;en </p>
     *
     * @param   url         uniform resource locator as result of locate-method (optional)
     * @param   noCache     avoid caching?
     * @return  input stream or {@code null} if the resource could not be opened
     * @see     #locate(String, Class, String)
     * @since   3.5/4.3
     */
    public static InputStream load(
        URL url,
        boolean noCache
    ) {

        if (url == null) {
            return null;
        }

        try {
            if (noCache || ANDROID) {
                URLConnection conn = url.openConnection();
                conn.setUseCaches(false);
                conn.connect(); // explicit for clarity
                return conn.getInputStream();
            } else {
                return url.openStream();
            }
        } catch (IOException ioe) {
            return null;
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

        //~ Methoden ------------------------------------------------------

        @Override
        public URL locate(
            String moduleName,
            Class<?> moduleRef,
            String path
        ) {

            // first try url construction whose initialization time is much quicker than querying the class loader
            String constructedUrl = null;

            try {
                ProtectionDomain pd = moduleRef.getProtectionDomain(); // null on android platform
                CodeSource cs = ((pd == null) ? null : pd.getCodeSource());

                if (cs != null) {
                    constructedUrl = cs.getLocation().toExternalForm();
                    if (constructedUrl.endsWith(".jar")) {
                        constructedUrl = "jar:" + constructedUrl + "!/";
                    }
                    constructedUrl += path;
                    return new URL(constructedUrl);
                }
            } catch (SecurityException se) {
                // use fallback via class loader
            } catch (MalformedURLException e) {
                System.err.println("Warning: malformed resource path = " + constructedUrl);
            }

            // last try - ask the class loader
            return moduleRef.getClassLoader().getResource(path);

        }

        @Override
        public <S> Iterable<S> services(Class<S> serviceInterface) {

            return ServiceLoader.load(serviceInterface, serviceInterface.getClassLoader());

        }

    }

}
