/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UnitPatternSPI.java) is part of project Time4J.
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

import net.time4j.format.PluralCategory;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;


/**
 * <p>Service-Provider-Implementation for accessing localized unit patterns. </p>
 *
 * <p>The underlying properties files are located in the folder &quot;i18n&quot; relative
 * to class path and are encoded in UTF-8. The basic bundle names are &quot;years&quot;
 * &quot;months&quot;, &quot;weeks&quot;, &quot;days&quot;, &quot;hours&quot;,
 * &quot;minutes&quot; and &quot;seconds&quot;. This class uses a modified fallback
 * algorithm for searching the right properties file as documented in 
 * <a href="http://www.unicode.org/reports/tr35/#Multiple_Inheritance" target="_blank">CLDR</a>
 * published by unicode consortium. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Service-Provider-Implementierung f&uuml;r den Zugang zu lokalisierten
 * Zeiteinheitsmustern. </p>
 *
 * <p>Die zugrundeliegenden properties-Dateien liegen im Ordner &quot;i18n&quot; relativ
 * zum Klassenpfad und sind in UTF-8 kodiert. Die Basisnamen der Dateien sind &quot;years&quot;
 * &quot;months&quot;, &quot;weeks&quot;, &quot;days&quot;, &quot;hours&quot;,
 * &quot;minutes&quot; und &quot;seconds&quot;. Diese Klasse verwendet einen ge&auml;nderten
 * Suchalgorithmus, um die richtige properties-Datei zu erhalten - dokumentiert in
 * <a href="http://www.unicode.org/reports/tr35/#Multiple_Inheritance" target="_blank">CLDR</a>
 * ver&ouml;ffentlicht vom Unicode-Konsortium. </p>
 *
 * @author  Meno Hochschild
 */
public final class UnitPatternSPI {
    
    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of years. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for years
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Jahre. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for years
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getYearsPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "years");
        
    }
    
    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of months. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for months
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Monate. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for months
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getMonthsPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "months");
        
    }
    
    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of weeks. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for weeks
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Wochen. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for weeks
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getWeeksPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "weeks");
        
    }
    
    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of days. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for days
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Tage. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for days
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getDaysPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "days");
        
    }
    
    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of hours. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for hours
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Stunden. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for hours
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getHoursPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "hours");
        
    }
    
    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of minutes. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for minutes
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Minuten. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for minutes
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getMinutesPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "minutes");
        
    }
    
    /**
     * <p>Yields the localized unit pattern with unit name and a placeholder
     * &quot;{0}&quot; for the count of seconds. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for seconds
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    /*[deutsch]
     * <p>Liefert das lokalisierte Zeiteinheitsmuster mit Zeiteinheitstext und
     * einem Platzhalter &quot;{0}&quot; f&uuml;r die Anzahl der Sekunden. </p>
     *
     * @param   language    language setting
     * @param   category    plural category
     * @return  unit pattern for seconds
     * @throws  java.util.MissingResourceException if no pattern was found
     */
    public String getSecondsPattern(
        Locale language,
        PluralCategory category
    ) {
        
        return this.getUnitPattern(language, category, "seconds");
        
    }
    
    private String getUnitPattern(
        Locale language,
        PluralCategory category,
        String baseBundleName
    ) {
    
        ClassLoader loader = this.getClass().getClassLoader();
        ResourceBundle.Control control = UnitPatternControl.getInstance(category);
        ResourceBundle rb =
            ResourceBundle.getBundle(baseBundleName, language, loader, control);
        
        Set<String> keys = UnitPatternBundle.class.cast(rb).getInternalKeys();
        String key = category.name();
        
        if (!keys.containsKey(key)) {
            key = PluralCategory.OTHER.name();
        }
        
        return rb.getString(key);
        
    }

}
