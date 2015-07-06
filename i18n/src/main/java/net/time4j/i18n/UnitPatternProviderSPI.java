/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UnitPatternProviderSPI.java) is part of project Time4J.
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
 * <p>{@code ServiceProvider}-implementation for accessing localized unit
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
public final class UnitPatternProviderSPI
    implements UnitPatternProvider {

    //~ Methoden ----------------------------------------------------------

    @Override
    public String getYearPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'Y', width, category);

    }

    @Override
    public String getMonthPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'M', width, category);

    }

    @Override
    public String getWeekPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'W', width, category);

    }

    @Override
    public String getDayPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'D', width, category);

    }

    @Override
    public String getHourPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'H', width, category);

    }

    @Override
    public String getMinutePattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'N', width, category);

    }

    @Override
    public String getSecondPattern(
        Locale language,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(language, 'S', width, category);

    }

    @Override
    public String getMilliPattern(
        Locale lang,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(lang, '3', width, category);

    }

    @Override
    public String getMicroPattern(
        Locale lang,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(lang, '6', width, category);

    }

    @Override
    public String getNanoPattern(
        Locale lang,
        TextWidth width,
        PluralCategory category
    ) {

        return this.getUnitPattern(lang, '9', width, category);

    }

    @Override
    public String getYearPattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'Y', future, category);

    }

    @Override
    public String getMonthPattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'M', future, category);

    }

    @Override
    public String getWeekPattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'W', future, category);

    }

    @Override
    public String getDayPattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'D', future, category);

    }

    @Override
    public String getHourPattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'H', future, category);

    }

    @Override
    public String getMinutePattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'N', future, category);

    }

    @Override
    public String getSecondPattern(
        Locale language,
        boolean future,
        PluralCategory category
    ) {

        return this.getRelativePattern(language, 'S', future, category);

    }

    @Override
	public String getNowWord(Locale lang) {

		return this.getPattern(
            lang,
            "reltime/pattern",
            "now",
            null,
            PluralCategory.OTHER);

	}

    @Override
    public String getListPattern(
        Locale desired,
        TextWidth width,
        int size
    ) {

        if (size < 2) {
            throw new IllegalArgumentException("Size must be greater than 1.");
        }

		ClassLoader loader = this.getClass().getClassLoader();
		ResourceBundle.Control control = UTF8ResourceControl.SINGLETON;
		ResourceBundle rb =
            ResourceBundle.getBundle("units/pattern", desired, loader, control);
        String exact = buildListKey(width, String.valueOf(size));

        if (rb.containsKey(exact)) {
            return rb.getString(exact);
        }

        String end = rb.getString(buildListKey(width, "end"));

        if (size == 2) {
            return end;
        }

        String start = rb.getString(buildListKey(width, "start"));
        String middle = rb.getString(buildListKey(width, "middle"));

        end = replace(end, '1', size - 1);
        end = replace(end, '0', size - 2);

        String previous = end;
        String result = previous;

        for (int i = size - 3; i >= 0; i--) {
            String pattern = ((i == 0) ? start : middle);
            int pos = -1;
            int n = pattern.length();

            for (int j = n - 1; j >= 0; j--) {
                if (
                    (j >= 2)
                    && (pattern.charAt(j) == '}')
                    && (pattern.charAt(j - 1) == '1')
                    && (pattern.charAt(j - 2) == '{')
                ) {
                    pos = j - 2;
                    break;
                }
            }

            if (pos > -1) {
                result = pattern.substring(0, pos) + previous;

                if (pos < n - 3) {
                    result += pattern.substring(pos + 3);
                }
            }

            if (i > 0) {
                previous = replace(result, '0', i);
            }
        }

        return result;

    }

	private String getUnitPattern(
		Locale		   lang,
		char		   unitID,
		TextWidth	   width,
		PluralCategory category
	) {

		return this.getPattern(
			lang,
			"units/pattern",
			buildKey(unitID, width, category),
			buildKey(unitID, width, PluralCategory.OTHER),
			category
		);

	}

	private String getRelativePattern(
		Locale		   lang,
		char		   unitID,
		boolean		   future,
		PluralCategory category
	) {

		return this.getPattern(
			lang,
			"reltime/pattern",
			buildKey(unitID, future, category),
			buildKey(unitID, future, PluralCategory.OTHER),
			category
		);

	}

	private String getPattern(
		Locale		   desired,
		String		   baseName,
		String		   key,
		String		   alt,
		PluralCategory category
	) {

		ClassLoader loader = this.getClass().getClassLoader();
		ResourceBundle.Control control = UTF8ResourceControl.SINGLETON;
		boolean init = true;
		ResourceBundle first = null;

		for (Locale locale : control.getCandidateLocales(baseName, desired)) {
			ResourceBundle rb = (
				init && (first != null)
				? first
				: ResourceBundle.getBundle(baseName, locale, loader, control));

			if (init) {
				if (locale.equals(rb.getLocale())) {
					init = false;
				} else {
					first = rb;
					continue;
				}
			}

			UTF8ResourceBundle bundle = UTF8ResourceBundle.class.cast(rb);

			if (bundle.getInternalKeys().contains(key)) {
				return bundle.getString(key);
			} else if (
				(category != PluralCategory.OTHER)
				&& bundle.getInternalKeys().contains(alt)
			) {
				return bundle.getString(alt);
			}

		}

		throw new MissingResourceException(
			"Can't find resource for bundle "
                + baseName + ".properties, key " + key,
			baseName + ".properties",
			key
		);

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

    private static String buildListKey(
        TextWidth width,
        String suffix
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append('L');

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

        return sb.append('-').append(suffix).toString();

    }

    private static String replace(
        String s,
        char search,
        int value
    ) {

        for (int i = 0, n = s.length() - 2; i < n; i++) {
            if (
                (s.charAt(i) == '{')
                && (s.charAt(i + 1) == search)
                && (s.charAt(i + 2) == '}')
            ) {
                StringBuilder b = new StringBuilder(n + 10);
                b.append(s);
                b.replace(i + 1, i + 2, String.valueOf(value));
                return b.toString();
            }
        }

        return s;

    }

}
