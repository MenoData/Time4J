/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UTF8ResourceControl.java) is part of project Time4J.
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

package net.time4j.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <p>Spezielles Bundle-Control wegen UTF-8-Dateizugriff und &Auml;nderung des
 * Standard-Fallback-Mechanismus. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
class UTF8ResourceControl
    extends ResourceBundle.Control {

    //~ Statische Felder/Initialisierungen --------------------------------

    public static final ResourceBundle.Control SINGLETON =
    	new UTF8ResourceControl();

    //~ Konstruktoren -----------------------------------------------------

    private UTF8ResourceControl() {
        super();

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public Locale getFallbackLocale(
        String baseName,
        Locale locale
    ) {

        if (baseName == null || locale == null) {
            throw new NullPointerException();
        }

        return null;

	}

    @Override
    public List<String> getFormats(String baseName) {

        return ResourceBundle.Control.FORMAT_PROPERTIES;

    }

    @Override
    public ResourceBundle newBundle(
        String baseName,
        Locale locale,
        String format,
        ClassLoader loader,
        boolean reload
    ) throws IllegalAccessException, InstantiationException, IOException {

        if (format.equals("java.properties")) {

            ResourceBundle bundle = null;
            InputStream stream = null;

            String bundleName =
                this.toBundleName(baseName, locale);
            String resourceName =
                this.toResourceName(bundleName, "properties");

            if (reload) {
                URL url = loader.getResource(resourceName);

                if (url != null) {
                    URLConnection uconn = url.openConnection();
                    uconn.setUseCaches(false);
                    stream = uconn.getInputStream();
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }

            if (stream != null) {
                Reader reader = null;

                try {
                    reader =
                        new BufferedReader(
                            new InputStreamReader(stream, "UTF-8"));
                    bundle = new UTF8ResourceBundle(reader);
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }

            return bundle;

        } else {
            throw new UnsupportedOperationException(
                "Unknown resource bundle format: " + format);
        }

    }

    @Override
    public String toBundleName(
        String baseName,
        Locale locale
    ) {

        if (locale == Locale.ROOT) {
            return baseName;
        }

        String language = LanguageMatch.getAlias(locale);
        String country = locale.getCountry();
        String variant = locale.getVariant();

        if (language.isEmpty() && country.isEmpty() && variant.isEmpty()) {
            return baseName;
        }

        StringBuilder sb = new StringBuilder(baseName);
        sb.append('_').append(language);

        if (!variant.isEmpty()) {
            sb.append('_').append(country).append('_').append(variant);
        } else if (!country.isEmpty()) {
            sb.append('_').append(country);
        }

        return sb.toString();

    }

}
