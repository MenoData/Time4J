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
 * target="_blank">CLDR</a> published by unicode consortium. In such a
 * properties file all key variants must exist, varying over all units
 * and text widths. </p>
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
 * zu erhalten. Zu einer gegebenen Pluralkategorie m&uuml;ssen in einer
 * solchen Datei alle Schl&uuml;sselvarianten existieren (variierend &uuml;ber
 * alle Zeiteinheiten und Textbreiten). Der Algorithmus ist dokumentiert in
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
        ResourceBundle.Control control =
            UnitPatternControl.getInstance(category);
        ResourceBundle rb =
            ResourceBundle.getBundle("units/pattern", lang, loader, control);
        String key = buildKey(unitID, width, category);

        if (!UnitPatternBundle.class.cast(rb).getInternalKeys().contains(key)) {
            key = buildKey(unitID, width, PluralCategory.OTHER);
        }

        return rb.getString(key);

    }

    private String getRelativePattern(
        Locale lang,
        char unitID,
        boolean future,
        PluralCategory category
    ) {

        ClassLoader loader = this.getClass().getClassLoader();
        ResourceBundle.Control control =
            UnitPatternControl.getInstance(category);
        ResourceBundle rb =
            ResourceBundle.getBundle("reltime/pattern", lang, loader, control);
        String key = buildKey(unitID, future, category);

        if (!UnitPatternBundle.class.cast(rb).getInternalKeys().contains(key)) {
            key = buildKey(unitID, future, PluralCategory.OTHER);
        }

        return rb.getString(key);

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
