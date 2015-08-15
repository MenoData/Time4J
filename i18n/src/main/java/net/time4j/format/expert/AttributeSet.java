/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (AttributeSet.java) is part of project Time4J.
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

package net.time4j.format.expert;

import net.time4j.PlainDate;
import net.time4j.base.ResourceLoader;
import net.time4j.engine.AttributeKey;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.ChronoCondition;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.Chronology;
import net.time4j.format.Attributes;
import net.time4j.format.Leniency;
import net.time4j.format.NumberSymbolProvider;
import net.time4j.format.OutputContext;
import net.time4j.format.TextWidth;
import net.time4j.history.ChronoHistory;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>A decorator for standard format attributes. </p>
 *
 * @author  Meno Hochschild
 * @since   3.0
 */
final class AttributeSet
    implements AttributeQuery {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final NumberSymbolProvider NUMBER_SYMBOLS;

    static {
        NumberSymbolProvider p = null;

        for (NumberSymbolProvider tmp : ResourceLoader.getInstance().services(NumberSymbolProvider.class)) {
            p = tmp;
            break;
        }

        if (p == null) {
            p = NumberSymbolProvider.DEFAULT;
        }

        NUMBER_SYMBOLS = p;
    }

    private static final char ISO_DECIMAL_SEPARATOR = (
        Boolean.getBoolean("net.time4j.format.iso.decimal.dot")
        ? '.'
        : ',' // Empfehlung des ISO-Standards
    );

    private static final
        ConcurrentMap<Locale, NumericalSymbols> NUMBER_SYMBOL_CACHE =
            new ConcurrentHashMap<Locale, NumericalSymbols>();
    private static final NumericalSymbols DEFAULT_NUMERICAL_SYMBOLS =
        new NumericalSymbols('0', ISO_DECIMAL_SEPARATOR);

    //~ Instanzvariablen --------------------------------------------------

    private final Attributes attributes;
    private final Locale locale;
    private final int level; // Ebene der optionalen Verarbeitungshierarchie
    private final int section; // Identifiziert eine optionale Attributsektion
    private final ChronoCondition<ChronoDisplay> printCondition; // nullable
    private final PlainDate cutover; // nullable

    //~ Konstruktoren -----------------------------------------------------

    AttributeSet(
        Attributes attributes,
        Locale locale
    ) {
        this(attributes, locale, 0, 0, null, null);

    }

    AttributeSet(
        Attributes attributes,
        Locale locale,
        int level,
        int section,
        ChronoCondition<ChronoDisplay> printCondition,
        PlainDate cutover
    ) {
        super();

        if (attributes == null) {
            throw new NullPointerException("Missing format attributes.");
        }

        this.attributes = attributes;
        this.locale = ((locale == null) ? Locale.ROOT : locale);
        this.level = level;
        this.section = section;
        this.printCondition = printCondition;
        this.cutover = cutover;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public boolean contains(AttributeKey<?> key) {

        if (key == ChronoHistory.ATTRIBUTE_CUTOVER_DATE) {
            return (this.cutover != null);
        }

        return this.attributes.contains(key);

    }

    @Override
    public <A> A get(AttributeKey<A> key) {

        if (key == ChronoHistory.ATTRIBUTE_CUTOVER_DATE) {
            if (this.cutover == null) {
                throw new NoSuchElementException(key.name());
            } else {
                return key.type().cast(this.cutover);
            }
        }

        return this.attributes.get(key);

    }

    @Override
    public <A> A get(
        AttributeKey<A> key,
        A defaultValue
    ) {

        if (key == ChronoHistory.ATTRIBUTE_CUTOVER_DATE) {
            if (this.cutover == null) {
                return defaultValue;
            } else {
                return key.type().cast(this.cutover);
            }
        }

        return this.attributes.get(key, defaultValue);

    }

    /**
     * <p>Compares all internal format attributes. </p>
     */
    /*[deutsch]
     * <p>Vergleicht auf Basis aller internen Formatattribute. </p>
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof AttributeSet) {
            AttributeSet that = (AttributeSet) obj;
            return (
                this.attributes.equals(that.attributes)
                && this.locale.equals(that.locale)
                && (this.level == that.level)
                && (this.section == that.section)
                && isEqual(this.printCondition, that.printCondition)
                && isEqual(this.cutover, that.cutover)
            );
        } else {
            return false;
        }

    }

    /*[deutsch]
     * <p>Berechnet den Hash-Code. </p>
     */
    @Override
    public int hashCode() {

        return this.attributes.hashCode();

    }

    /**
     * <p>Supports mainly debugging. </p>
     */
    /*[deutsch]
     * <p>Dient vorwiegend der Debugging-Unterst&uuml;tzung. </p>
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append('[');
        sb.append(this.attributes);
        sb.append(",locale=");
        sb.append(this.locale);
        sb.append(",level=");
        sb.append(this.level);
        sb.append(",section=");
        sb.append(this.section);
        sb.append(",print-condition=");
        sb.append(this.printCondition);
        sb.append(",gregorian-cutover=");
        sb.append(this.cutover);
        sb.append(']');
        return sb.toString();

    }

    Attributes getAttributes() {

        return this.attributes;

    }

    Locale getLocale() {

        return this.locale;

    }

    int getLevel() {

        return this.level;

    }

    int getSection() {

        return this.section;

    }

    ChronoCondition<ChronoDisplay> getCondition() {

        return this.printCondition; // nullable

    }

    static AttributeSet createDefaults(
        Chronology<?> chronology,
        Locale locale
    ) {

        Attributes.Builder builder = new Attributes.Builder(chronology);
        builder.set(Attributes.LENIENCY, Leniency.SMART);
        builder.set(Attributes.TEXT_WIDTH, TextWidth.WIDE);
        builder.set(Attributes.OUTPUT_CONTEXT, OutputContext.FORMAT);
        builder.set(Attributes.PAD_CHAR, ' ');
        AttributeSet as = new AttributeSet(builder.build(), locale);
        return as.withLocale(locale);

    }

    /**
     * <p>Setzt die Attribute neu. </p>
     *
     * @param   attributes  new format attributes
     */
    AttributeSet withAttributes(Attributes attributes) {

        return new AttributeSet(attributes, this.locale, this.level, this.section, this.printCondition, this.cutover);

    }

    /**
     * <p>Setzt die Sprach- und L&auml;ndereinstellung. </p>
     *
     * <p>Die Attribute {@link Attributes#ZERO_DIGIT}, {@link Attributes#DECIMAL_SEPARATOR}
     * und {@link Attributes#LANGUAGE} werden automatisch mit angepasst. </p>
     *
     * @param   locale      new language and country setting
     * @return  this instance for method chaining
     */
    AttributeSet withLocale(Locale locale) {

        Attributes.Builder builder = new Attributes.Builder();
        builder.setAll(this.attributes);

        if (
            locale.getLanguage().isEmpty()
            && locale.getCountry().isEmpty()
        ) {
            locale = Locale.ROOT;
            builder.set(Attributes.ZERO_DIGIT, '0');
            builder.set(Attributes.DECIMAL_SEPARATOR, ISO_DECIMAL_SEPARATOR);
        } else {
            NumericalSymbols symbols = NUMBER_SYMBOL_CACHE.get(locale);

            if (symbols == null) {
                symbols = DEFAULT_NUMERICAL_SYMBOLS;

                for (Locale test : NUMBER_SYMBOLS.getAvailableLocales()) {
                    if (locale.equals(test)) {
                        symbols =
                            new NumericalSymbols(
                                NUMBER_SYMBOLS.getZeroDigit(locale),
                                NUMBER_SYMBOLS.getDecimalSeparator(locale)
                            );
                        break;
                    }
                }

                NumericalSymbols old =
                    NUMBER_SYMBOL_CACHE.putIfAbsent(locale, symbols);
                if (old != null) {
                    symbols = old;
                }
            }

            builder.set(Attributes.ZERO_DIGIT, symbols.zeroDigit);
            builder.set(Attributes.DECIMAL_SEPARATOR, symbols.decimalSeparator);
        }

        builder.setLanguage(locale);
        return new AttributeSet(builder.build(), locale, this.level, this.section, this.printCondition, this.cutover);

    }

    private static boolean isEqual(Object o1, Object o2) {

        if (o1 == null) {
            return (o2 == null);
        } else {
            return o1.equals(o2);
        }

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class NumericalSymbols {

        //~ Instanzvariablen ----------------------------------------------

        private final char zeroDigit;
        private final char decimalSeparator;

        //~ Konstruktoren -------------------------------------------------

        NumericalSymbols(
            char zeroDigit,
            char decimalSeparator
        ) {
            super();

            this.zeroDigit = zeroDigit;
            this.decimalSeparator = decimalSeparator;

        }

    }

}
