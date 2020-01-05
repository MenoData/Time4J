/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduVariant.java) is part of project Time4J.
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

package net.time4j.calendar.hindu;

import net.time4j.engine.CalendarSystem;
import net.time4j.engine.VariantSource;

import java.io.Serializable;
import java.util.StringTokenizer;


/**
 * <p>The Hindu calendar variants differ on various sets of calculations, month names and the choice of era. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Die Varianten des Hindukalenders unterscheiden sich in den Berechnungen, den Monatsnamen und der Wahl
 * einer &Auml;ra. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public final class HinduVariant
    implements VariantSource, Serializable {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int TYPE_SOLAR = 0;
    private static final int TYPE_AMANTA_CHAITRA = 1;
    private static final int TYPE_AMANTA_ASHADHA = 2;
    private static final int TYPE_AMANTA_KARTIKA = 3;
    private static final int TYPE_PURNIMANTA = 4;

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial  type of calendar
     *          (0 = solar, 1 = amanta - year starts with chaitra, 2 = amanta - year starts with ashadha,
     *          3 = amanta - year starts with kartika, 4 = purnimanta)
     */
    private final int type;

    /**
     * @serial  the rule to be used
     */
    private final HinduRule rule;

    /**
     * @serial  default era
     */
    private final HinduEra era;

    /**
     * @serial  determines if elapsed years are used
     */
    private final boolean elapsedMode;

    //~ Konstruktoren -----------------------------------------------------

    private HinduVariant(
        int type,
        HinduRule rule,
        HinduEra era
    ) {
        this(type, rule, era, useStandardElapsedMode(era, rule));

    }

    private HinduVariant(
        int type,
        HinduRule rule,
        HinduEra era,
        boolean elapsedMode
    ) {
        super();

        if ((type < 0) || (type > 4)) {
            throw new IllegalArgumentException("Type of Hindu variant out of range (0-4): " + type);
        } else if (rule == null) {
            throw new NullPointerException("Missing Hindu rule.");
        } else if (era == null) {
            throw new NullPointerException("Missing default Hindu era.");
        }

        this.type = type;
        this.rule = rule;
        this.era = era;
        this.elapsedMode = elapsedMode;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Constructs a variant for the solar Hindu calendar. </p>
     *
     * <p>The default era is usually SAKA unless the malayalam rule (KOLLAM) or
     * the bengal rule is chosen. The old Hindu calendar always uses KALI_YUGA. </p>
     *
     * @param   rule    the underlying algorithmic rule
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Konstruiert eine Variant f&uuml;r den solaren Hindukalender. </p>
     *
     * <p>Die Standard&auml;ra is normalerweise SAKA, es sei denn, die Malayalam-Regel (KOLLAM) oder
     * die Bengal-Regel wurden gew&auml;hlt. Der alte Hindukalender verwendet immer KALI_YUGA. </p>
     *
     * @param   rule    the underlying algorithmic rule
     * @return  HinduVariant
     */
    public static HinduVariant ofSolar(HinduRule rule) {

        HinduEra defaultEra;

        switch (rule) {
            case MALAYALI:
                defaultEra = HinduEra.KOLLAM;
                break;
            case BENGAL:
                defaultEra = HinduEra.BENGAL;
                break;
            case ARYA_SIDDHANTA:
                defaultEra = HinduEra.KALI_YUGA;
                break;
            default:
                defaultEra = HinduEra.SAKA;
        }

        return HinduVariant.ofSolar(rule, defaultEra);

    }

    /**
     * <p>Constructs a variant for the solar Hindu calendar and given default era. </p>
     *
     * @param   rule        the underlying algorithmic rule
     * @param   defaultEra  the era to be used
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Konstruiert eine Variant f&uuml;r den solaren Hindukalender und die angegebene
     * Standard&auml;ra. </p>
     *
     * @param   rule        the underlying algorithmic rule
     * @param   defaultEra  the era to be used
     * @return  HinduVariant
     */
    public static HinduVariant ofSolar(
        HinduRule rule,
        HinduEra defaultEra
    ) {

        return new HinduVariant(TYPE_SOLAR, rule, defaultEra);

    }

    /**
     * <p>The amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Chaitra. </p>
     *
     * <p>Equivalent to calling {@link #ofAmanta(HinduRule, HinduEra) ofAmanta(rule, HinduEra.VIKRAMA}. </p>
     *
     * @param   rule        the underlying algorithmic rule
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Das Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Chaitra beginnt. </p>
     *
     * <p>&Auml;quivalent zu {@link #ofAmanta(HinduRule, HinduEra) ofAmanta(rule, HinduEra.VIKRAMA}. </p>
     *
     * @param   rule        the underlying algorithmic rule
     * @return  HinduVariant
     */
    public static HinduVariant ofAmanta(HinduRule rule) {

        return HinduVariant.ofAmanta(rule, HinduEra.VIKRAMA);

    }

    /**
     * <p>The amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Chaitra. </p>
     *
     * @param   rule        the underlying algorithmic rule
     * @param   defaultEra  the era to be used
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Das Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Chaitra beginnt. </p>
     *
     * @param   rule        the underlying algorithmic rule
     * @param   defaultEra  the era to be used
     * @return  HinduVariant
     */
    public static HinduVariant ofAmanta(
        HinduRule rule,
        HinduEra defaultEra
    ) {

        return new HinduVariant(TYPE_AMANTA_CHAITRA, rule, defaultEra);

    }

    /**
     * <p>This amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Kartika. </p>
     *
     * <p>It uses the default era {@code HinduEra.VIKRAMA} and is applied in Gujarat. </p>
     *
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Dieses Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Kartika beginnt. </p>
     *
     * <p>Es verwendet die Standard&auml;ra {@code HinduEra.VIKRAMA} und wird in Gujarat angewandt. </p>
     *
     * @return  HinduVariant
     */
    public static HinduVariant ofGujaratStartingYearOnKartika() {

        return new HinduVariant(TYPE_AMANTA_KARTIKA, HinduRule.ORISSA, HinduEra.VIKRAMA);

    }

    /**
     * <p>This amanta scheme is a lunisolar calendar based on the new moon cycle
     * and starting the year with the month Ashadha. </p>
     *
     * <p>It uses the default era {@code HinduEra.VIKRAMA} and is applied in some parts of Gujarat. </p>
     *
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Dieses Amanta-Schema ist ein lunisolarer Kalender, der auf dem Neumondzyklus
     * basiert und das Jahr mit dem Monat Ashadha beginnt. </p>
     *
     * <p>Es verwendet die Standard&auml;ra {@code HinduEra.VIKRAMA} und wird in Teilen von
     * Gujarat angewandt. </p>
     *
     * @return  HinduVariant
     */
    public static HinduVariant ofGujaratStartingYearOnAshadha() {

        return new HinduVariant(TYPE_AMANTA_ASHADHA, HinduRule.ORISSA, HinduEra.VIKRAMA);

    }

    /**
     * <p>The purnimanta scheme is a lunisolar calendar based on the full moon cycle. </p>
     *
     * @return  HinduVariant
     */
    /*[deutsch]
     * <p>Das Purnimanta-Schema ist ein lunisolarer Kalender, der auf dem Vollmondzyklus
     * basiert. </p>
     *
     * @return  HinduVariant
     */
    public static HinduVariant ofPurnimanta() {

        return new HinduVariant(TYPE_PURNIMANTA, HinduRule.ORISSA, HinduEra.VIKRAMA);

    }

    /**
     * <p>Parses given variant string. </p>
     *
     * <p>The variant string is the same as created by calling {@link #getVariant()}. </p>
     *
     * @param   variant     variant string
     * @return  parsed variant
     * @throws  IllegalArgumentException if given argument cannot be parsed
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Varianttext. </p>
     *
     * <p>Der Varianttext ist der gleiche wie durch {@link #getVariant()} erzeugt. </p>
     *
     * @param   variant     variant string
     * @return  parsed variant
     * @throws  IllegalArgumentException if given argument cannot be parsed
     */
    public static HinduVariant from(String variant) {

        StringTokenizer st = new StringTokenizer(variant, "|");
        int count = 0;
        int lType = -1;
        HinduRule lRule = null;
        HinduEra lEra = null;
        boolean elapsedMode = false;

        while (st.hasMoreTokens()) {
            count++;
            String token = st.nextToken();
            switch (count) {
                case 1:
                    lType = Integer.valueOf(token);
                    break;
                case 2:
                    lRule = HinduRule.valueOf(token);
                    break;
                case 3:
                    lEra = HinduEra.valueOf(token);
                    break;
                case 4:
                    elapsedMode = token.equals("e");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid variant: " + variant);
            }
        }

        try {
            return new HinduVariant(lType, lRule, lEra, elapsedMode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid variant: " + variant);
        }

    }

    /**
     * Obtains the associated calendar system.
     *
     * @return  calendar system for this variant of Hindu calendar
     */
    /*[deutsch]
     * Liefert das zu dieser Variante passende Kalendersystem.
     *
     * @return  calendar system for this variant of Hindu calendar
     */
    public CalendarSystem<HinduCalendar> getCalendarSystem() {

        throw new UnsupportedOperationException("Not yet implemented: " + this.getVariant());

    }

    /**
     * <p>Obtains the underlying rule set. </p>
     *
     * @return  HinduRule
     */
    /*[deutsch]
     * <p>Liefert den zugrundelegenden Regelsatz. </p>
     *
     * @return  HinduRule
     */
    public HinduRule getRule() {

        return this.rule;

    }

    /**
     * <p>Obtains the default era. </p>
     *
     * @return  HinduEra
     */
    /*[deutsch]
     * <p>Liefert die zur Variante passende Standard&auml;ra. </p>
     *
     * @return  HinduEra
     */
    public HinduEra getDefaultEra() {

        return this.era;

    }

    /**
     * <p>Determines if this variant describes the solar Hindu calendar. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante den solaren Hindukalender beschreibt. </p>
     *
     * @return  boolean
     */
    public boolean isSolar() {

        return (this.type == TYPE_SOLAR);

    }

    /**
     * <p>Determines if this variant describes the lunisolar Hindu calendar. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante den lunisolaren Hindukalender beschreibt. </p>
     *
     * @return  boolean
     */
    public boolean isLunisolar() {

        return (this.type > 0);

    }

    /**
     * <p>Determines if this variant describes the purnimanta scheme. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante das Purnimanta-Schema beschreibt. </p>
     *
     * @return  boolean
     */
    public boolean isPurnimanta() {

        return (this.type == TYPE_PURNIMANTA);

    }

    /**
     * <p>Does this variant use elapsed years? </p>
     *
     * <p>Elapsed years are the standard, however, in most southern parts of India current years are used. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Verwendet diese Variante abgelaufene Jahre? </p>
     *
     * <p>Abgelaufene Jahre sind der Standard. Allerdings verwenden einige s&uuml;dliche Teile von Indien
     * laufende Jahre. </p>
     *
     * @return  boolean
     */
    public boolean isUsingElapsedYears() {

        return this.elapsedMode;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HinduVariant) {
            HinduVariant that = (HinduVariant) obj;
            return (
                (this.type == that.type)
                    && (this.rule == that.rule)
                    && (this.era == that.era)
                    && (this.elapsedMode == that.elapsedMode));
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return this.type + 17 * this.rule.hashCode() + 31 * this.era.hashCode() + (this.elapsedMode ? 1 : 0);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Hindu-variant=[");

        switch (this.type) {
            case 0:
                sb.append("solar");
                break;
            case 1:
                sb.append("amanta-chaitra");
                break;
            case 2:
                sb.append("amanta-ashadha");
                break;
            case 3:
                sb.append("amanta-kartika");
                break;
            case 4:
                sb.append("purnimanta");
                break;
            default:
                throw new UnsupportedOperationException("Unknown type of variant: " + this.type);
        }

        sb.append('|');
        sb.append(this.rule.name());
        sb.append('|');
        sb.append(this.era.name());
        sb.append('|');
        sb.append(this.elapsedMode ? "elapsed-year" : "current-year");
        sb.append(']');
        return sb.toString();

    }

    @Override
    public String getVariant() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.type);
        sb.append('|');
        sb.append(this.rule.name());
        sb.append('|');
        sb.append(this.era.name());
        sb.append('|');
        sb.append(this.elapsedMode ? "e" : "c");
        return sb.toString();

    }

    /**
     * <p>Creates a copy of this variant with given preferred era. </p>
     *
     * @param   defaultEra  the new deviating era
     * @return  modified copy or this variant if the era does not change
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit der angegebenen bevorzugten &Auml;ra. </p>
     *
     * @param   defaultEra  the new deviating era
     * @return  modified copy or this variant if the era does not change
     */
    public HinduVariant with(HinduEra defaultEra) {

        if (this.era.equals(defaultEra)) {
            return this;
        }

        return new HinduVariant(this.type, this.rule, defaultEra, this.elapsedMode);

    }

    /**
     * <p>Creates a copy of this variant with elapsed years. </p>
     *
     * <p>Note: Elapsed years count one less than current years.</p>
     *
     * @return  modified copy or this variant if the elapsed year mode does not change
     * @see     #withCurrentYears()
     * @see     #isUsingElapsedYears()
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit abgelaufenen Jahren. </p>
     *
     * <p>Hinweis: Abgelaufene Jahre z&auml;hlen eins weniger als laufende Jahre. </p>
     *
     * @return  modified copy or this variant if the elapsed year mode does not change
     * @see     #withCurrentYears()
     * @see     #isUsingElapsedYears()
     */
    public HinduVariant withElapsedYears() {

        if (this.elapsedMode) {
            return this;
        }

        return new HinduVariant(this.type, this.rule, this.era, true);

    }

    /**
     * <p>Creates a copy of this variant with current years. </p>
     *
     * <p>Note: Elapsed years count one less than current years.</p>
     *
     * @return  modified copy or this variant if the elapsed year mode does not change
     * @see     #withElapsedYears()
     * @see     #isUsingElapsedYears()
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit laufenden Jahren. </p>
     *
     * <p>Hinweis: Abgelaufene Jahre z&auml;hlen eins weniger als laufende Jahre. </p>
     *
     * @return  modified copy or this variant if the elapsed year mode does not change
     * @see     #withElapsedYears()
     * @see     #isUsingElapsedYears()
     */
    public HinduVariant withCurrentYears() {

        if (!this.elapsedMode) {
            return this;
        }

        return new HinduVariant(this.type, this.rule, this.era, false);

    }

    private static boolean useStandardElapsedMode(
        HinduEra era,
        HinduRule rule
    ) {

        switch (era) {
            case SAKA:
                switch (rule) {
                    case MADRAS:
                    case MALAYALI:
                    case TAMIL:
                        return false;
                    default:
                        return true;
                }
            case KOLLAM:
                return false;
            default:
                return true;
        }

    }

}
