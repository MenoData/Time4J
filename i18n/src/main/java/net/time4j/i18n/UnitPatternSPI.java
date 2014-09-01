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
import net.time4j.format.TextWidth;
import net.time4j.format.UnitPatternProvider;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * <p>Service-Provider-Implementation for accessing localized unit
 * patterns. </p>
 *
 * <p>The underlying properties files are located in the folder
 * &quot;units&quot; relative to class path and are encoded in UTF-8. The basic
 * bundle name is &quot;pattern&quot;. This class uses a modified fallback
 * algorithm for searching the right properties file as documented in
 * <a href="http://www.unicode.org/reports/tr35/#Multiple_Inheritance"
 * target="_blank">CLDR</a> published by unicode consortium. </p>
 *
 * <p>The case is similar for past and future patterns - with the difference
 * that the folder &quot;reltime&quot; is used instead. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
/*[deutsch]
 * <p>Service-Provider-Implementierung f&uuml;r den Zugang zu lokalisierten
 * Zeiteinheitsmustern. </p>
 *
 * <p>Die zugrundeliegenden properties-Dateien liegen im Ordner
 * &quot;units&quot; relativ zum Klassenpfad und sind in UTF-8 kodiert.
 * Der Basisname der Dateien ist &quot;patternquot;. Diese Klasse verwendet
 * einen ge&auml;nderten Suchalgorithmus, um die richtige properties-Datei
 * zu erhalten. Der Algorithmus ist dokumentiert in
 * <a href="http://www.unicode.org/reports/tr35/#Multiple_Inheritance"
 * target="_blank">CLDR</a> ver&ouml;ffentlicht vom Unicode-Konsortium. </p>
 *
 * <p>Die Vergangenheits- und Zukunftsmuster verhalten sich genauso - nur mit
 * dem Unterschied, da&szlig; stattdessen der Ordner &quot;reltime&quot;
 * benutzt wird. </p>
 *
 * @author  Meno Hochschild
 * @since   1.2
 */
public final class UnitPatternSPI
    implements UnitPatternProvider {

    //~ Methoden ----------------------------------------------------------

    @Override
    public String getYearsPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'Y', width, category);

    }

    @Override
    public String getMonthsPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'M', width, category);

    }

    @Override
    public String getWeeksPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'W', width, category);

    }

    @Override
    public String getDaysPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'D', width, category);

    }

    @Override
    public String getHoursPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'H', width, category);

    }

    @Override
    public String getMinutesPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'N', width, category);

    }

    @Override
    public String getSecondsPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'S', width, category);

    }

    @Override
    public String getPastYearsPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'Y', false, category);

    }

    @Override
    public String getPastMonthsPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'M', false, category);

    }

    @Override
    public String getPastWeeksPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'W', false, category);

    }

    @Override
    public String getPastDaysPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'D', false, category);

    }

    @Override
    public String getPastHoursPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'H', false, category);

    }

    @Override
    public String getPastMinutesPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'N', false, category);

    }

    @Override
    public String getPastSecondsPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'S', false, category);

    }

    @Override
    public String getFutureYearsPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'Y', true, category);

    }

    @Override
    public String getFutureMonthsPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'M', true, category);

    }

    @Override
    public String getFutureWeeksPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'W', true, category);

    }

    @Override
    public String getFutureDaysPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'D', true, category);

    }

    @Override
    public String getFutureHoursPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'H', true, category);

    }

    @Override
    public String getFutureMinutesPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'N', true, category);

    }

    @Override
    public String getFutureSecondsPattern(
        Locale language,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'S', true, category);

    }

    private String getUnitPattern(
        Locale lang,
        char unitID,
        TextWidth width,
        PluralCategory category
    ) {

        ClassLoader loader = this.getClass().getClassLoader();
        ResourceBundle.Control control = UnitPatternControl.SINGLETON;
        String baseName = "units/pattern";
    	String key = buildKey(unitID, width, category);
        boolean init = true;
        
        for (Locale locale : control.getCandidateLocales(baseName, lang)) {
            ResourceBundle rb =
                    ResourceBundle.getBundle(baseName, locale, loader, control);
            
        	if (init) {
	        	if (rb.getLocale().equals(locale)) {
	        		init = false;
	        	} else {
	        		continue;
	        	}
        	}
        	
        	UnitPatternBundle bundle = UnitPatternBundle.class.cast(rb);
        	
        	if (bundle.getInternalKeys().contains(key)) {
        		return bundle.getString(key);
        	} else if (category != PluralCategory.OTHER) {
        		String alt = buildKey(unitID, width, PluralCategory.OTHER);
        		
        		if (bundle.getInternalKeys().contains(alt)) {
        			return bundle.getString(alt);
        		}
        	}
        	
        }
        
        throw new MissingResourceException(
        	"Can't find resource for bundle " + baseName + ".properties, key " + key,
        	baseName + ".properties", 
        	key);

    }

    private String getRelativePattern(
        Locale lang,
        char unitID,
        boolean future,
        PluralCategory category
    ) {

        ClassLoader loader = this.getClass().getClassLoader();
        ResourceBundle.Control control = UnitPatternControl.SINGLETON;
        String baseName = "reltime/pattern";
    	String key = buildKey(unitID, future, category);
        boolean init = true;
        
        for (Locale locale : control.getCandidateLocales(baseName, lang)) {
            ResourceBundle rb =
                    ResourceBundle.getBundle(baseName, locale, loader, control);
            
        	if (init) {
	        	if (rb.getLocale().equals(locale)) {
	        		init = false;
	        	} else {
	        		continue;
	        	}
        	}
        	
        	UnitPatternBundle bundle = UnitPatternBundle.class.cast(rb);
        	
        	if (bundle.getInternalKeys().contains(key)) {
        		return bundle.getString(key);
        	} else if (category != PluralCategory.OTHER) {
        		String alt = buildKey(unitID, future, PluralCategory.OTHER);
        		
        		if (bundle.getInternalKeys().contains(alt)) {
        			return bundle.getString(alt);
        		}
        	}
        	
        }
        
        throw new MissingResourceException(
        	"Can't find resource for bundle " + baseName + ".properties, key " + key,
        	baseName + ".properties", 
        	key);

    }

    private static String buildKey(
        char unitID,
        TextWidth width,
        PluralCategory category
    ) {

        StringBuilder sb = new StringBuilder(3);
        sb.append(unitID);

        switch (width) {
            case WIDE:
                sb.append('w');
                break;
            case ABBREVIATED:
            case SHORT:
                sb.append('s');
                break;
            case NARROW:
                sb.append('n');
                break;
            default:
                throw new UnsupportedOperationException(width.name());
        }

        return sb.append(category.ordinal()).toString();

    }

    private static String buildKey(
        char unitID,
        boolean future,
        PluralCategory category
    ) {

        StringBuilder sb = new StringBuilder(3);
        sb.append(unitID);
        sb.append(future ? '+' : '-');
        return sb.append(category.ordinal()).toString();

    }

}
