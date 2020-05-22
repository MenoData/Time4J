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

import net.time4j.calendar.IndianMonth;
import net.time4j.calendar.astro.GeoLocation;
import net.time4j.engine.VariantSource;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
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

    // the holy city of Ujjain serves as main reference point of Hinduism
    static final GeoLocation UJJAIN =
        GeoLocation.of(
            23.0 + 9.0 / 60.0,
            75.0 + 46.0 / 60.0 + 6.0 / 3600.0);

    private static final HinduRule[] RULES = HinduRule.values();

    private static final int TYPE_OLD_SOLAR = -1;
    private static final int TYPE_OLD_LUNAR = -2;

    static final HinduVariant VAR_OLD_SOLAR = new HinduVariant(AryaSiddhanta.SOLAR);
    static final HinduVariant VAR_OLD_LUNAR = new HinduVariant(AryaSiddhanta.LUNAR);

    //~ Instanzvariablen --------------------------------------------------

    private transient final int type;
    private transient final HinduEra defaultEra;
    private transient final boolean elapsedMode;
    private transient final boolean altHinduSunrise;
    private transient final GeoLocation location;

    //~ Konstruktoren -----------------------------------------------------

    HinduVariant(
        HinduRule rule,
        HinduEra defaultEra
    ) {
        this(
            rule.ordinal(),
            defaultEra,
            useStandardElapsedMode(defaultEra, rule),
            false,
            UJJAIN
        );

    }

    private HinduVariant(AryaSiddhanta aryaSiddhanta) {
        this(
            (aryaSiddhanta == AryaSiddhanta.SOLAR) ? TYPE_OLD_SOLAR : TYPE_OLD_LUNAR,
            HinduEra.KALI_YUGA,
            true,
            false,
            UJJAIN
        );

    }

    private HinduVariant(
        int type,
        HinduEra defaultEra,
        boolean elapsedMode,
        boolean altHinduSunrise,
        GeoLocation location
    ) {
        super();

        if ((type < TYPE_OLD_LUNAR) || (type >= HinduRule.values().length)) {
            throw new IllegalArgumentException("Undefined Hindu rule.");
        } else if (defaultEra == null) {
            throw new NullPointerException("Missing default Hindu era.");
        } else if (location == null) {
            throw new NullPointerException("Missing geographical location.");
        }

        this.type = type;
        this.defaultEra = defaultEra;
        this.elapsedMode = elapsedMode;
        this.altHinduSunrise = altHinduSunrise;
        this.location = location;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Parses given variant string. </p>
     *
     * <p>The variant string is the same as created by calling {@code getVariant()}. </p>
     *
     * @param   variant variant string
     * @return  parsed variant
     * @throws  IllegalArgumentException if given argument cannot be parsed
     * @see     #getVariant()
     * @see     HinduRule#variant()
     * @see     AryaSiddhanta#variant()
     */
    /*[deutsch]
     * <p>Interpretiert den angegebenen Varianttext. </p>
     *
     * <p>Der Varianttext ist der gleiche wie durch {@code getVariant()} erzeugt. </p>
     *
     * @param   variant     variant string
     * @return  parsed variant
     * @throws  IllegalArgumentException if given argument cannot be parsed
     * @see     #getVariant()
     * @see     HinduRule#variant()
     * @see     AryaSiddhanta#variant()
     */
    public static HinduVariant from(String variant) {
        if (variant.startsWith(AryaSiddhanta.PREFIX)) {
            try {
                AryaSiddhanta aryaSiddhanta = AryaSiddhanta.valueOf(variant.substring(AryaSiddhanta.PREFIX.length()));
                return (aryaSiddhanta == AryaSiddhanta.SOLAR) ? VAR_OLD_SOLAR : VAR_OLD_LUNAR;
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalArgumentException("Invalid variant: " + variant, ex);
            }
        }

        StringTokenizer st = new StringTokenizer(variant, "|");
        int count = 0;
        int type = Integer.MIN_VALUE;
        HinduEra defaultEra = null;
        boolean elapsedMode = true;
        boolean altHinduSunrise = false;
        double latitude = UJJAIN.getLatitude();
        double longitude = UJJAIN.getLongitude();
        int altitude = UJJAIN.getAltitude();
        boolean ujjain = true;

        while (st.hasMoreTokens()) {
            count++;
            String token = st.nextToken();
            switch (count) {
                case 1:
                    type = Integer.valueOf(token);
                    break;
                case 2:
                    defaultEra = HinduEra.valueOf(token);
                    break;
                case 3:
                    elapsedMode = token.equals("elapsed");
                    break;
                case 4:
                    altHinduSunrise = token.equals("alt");
                    break;
                case 5:
                    latitude = Double.valueOf(token).doubleValue();
                    ujjain = (latitude == UJJAIN.getLatitude());
                    break;
                case 6:
                    longitude = Double.valueOf(token).doubleValue();
                    ujjain = ujjain && (longitude == UJJAIN.getLongitude());
                    break;
                case 7:
                    altitude = Integer.valueOf(token).intValue();
                    ujjain = ujjain && (altitude == 0);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid variant: " + variant);
            }
        }

        if (type < 0) {
            throw new IllegalArgumentException("Invalid variant: " + variant);
        }

        try {
            return new HinduVariant(
                type,
                defaultEra,
                elapsedMode,
                altHinduSunrise,
                ujjain ? UJJAIN : GeoLocation.of(latitude, longitude, altitude)
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid variant: " + variant);
        }

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
        return this.defaultEra;
    }

    /**
     * <p>Obtains the geographical reference point which is usually the holy city of Ujjain. </p>
     *
     * @return  GeoLocation
     */
    /*[deutsch]
     * <p>Liefert den geographischen Referenzpunkt, der gew&ouml;hnlich die heilige Stadt von Ujjain ist. </p>
     *
     * @return  GeoLocation
     */
    public GeoLocation getLocation() {
        return this.location;
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
        return !this.isLunisolar();
    }

    /**
     * <p>Determines if this variant describes the lunisolar Hindu calendar. </p>
     *
     * <p>Lunisolar variants follow either the amanta or the purnimanta scheme. </p>
     *
     * @return  boolean
     * @see     #isAmanta()
     * @see     #isPurnimanta()
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante den lunisolaren Hindukalender beschreibt. </p>
     *
     * <p>Lunisolare Varianten folgen entweder dem Amanta- oder dem Purnimanta-Schema. </p>
     *
     * @return  boolean
     * @see     #isAmanta()
     * @see     #isPurnimanta()
     */
    public boolean isLunisolar() {
        return (this.isAmanta() || this.isPurnimanta());
    }

    /**
     * <p>Determines if this variant describes the amanta scheme. </p>
     *
     * <p>Months are synchronized with the New Moon. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante das Amanta-Schema beschreibt. </p>
     *
     * <p>Die Monate folgen dem Neumondzyklus. </p>
     *
     * @return  boolean
     */
    public boolean isAmanta() {
        if (this.type == TYPE_OLD_LUNAR) {
            return true;
        }
        return ((this.type >= HinduRule.AMANTA.ordinal()) && (this.type < HinduRule.PURNIMANTA.ordinal()));
    }

    /**
     * <p>Determines if this variant describes the purnimanta scheme. </p>
     *
     * <p>Months are synchronized with the Full Moon. The first day of a purnimanta month
     * starts with 16 or higher. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante das Purnimanta-Schema beschreibt. </p>
     *
     * <p>Die Monate folgen dem Vollmondzyklus. Deren erster Tag f&auml;ngt mit 16 oder h&ouml;her an. </p>
     *
     * @return  boolean
     */
    public boolean isPurnimanta() {
        return (this.type == HinduRule.PURNIMANTA.ordinal());
    }

    /**
     * <p>Determines if this variant describes the old Hindu calendar based on mean astronomical values. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante den alten Hindu-Kalender beschreibt, der auf mittleren astronomischen
     * Werten beruht. </p>
     *
     * @return  boolean
     */
    public boolean isOld() {
        return (this.type < 0);
    }

    // Kerala region only
    boolean prefersRasiNames() {
        return (this.type == HinduRule.MADRAS.ordinal()) || (this.type == HinduRule.MALAYALI.ordinal());
    }

    /**
     * <p>Does this variant use elapsed years? </p>
     *
     * <p>Elapsed years are the standard, however, in most southern parts of India current years are used,
     * for example Madras, Malayali (Kollam) and Tamil use current years. </p>
     *
     * @return  boolean
     * @see     #withElapsedYears()
     * @see     #withCurrentYears()
     */
    /*[deutsch]
     * <p>Verwendet diese Variante abgelaufene Jahre? </p>
     *
     * <p>Abgelaufene Jahre sind der Standard. Allerdings verwenden einige s&uuml;dliche Teile von Indien
     * laufende Jahre, zum Beispiel gelten f&uuml;r Madras, Malayali (Kollam) und Tamil laufende Jahre. </p>
     *
     * @return  boolean
     * @see     #withElapsedYears()
     * @see     #withCurrentYears()
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
                    && (this.defaultEra == that.defaultEra)
                    && (this.elapsedMode == that.elapsedMode)
                    && (this.altHinduSunrise == that.altHinduSunrise)
                    && (this.location.getLatitude() == that.location.getLatitude())
                    && (this.location.getLongitude() == that.location.getLongitude())
                    && (this.location.getAltitude() == that.location.getAltitude()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.type
            + 17 * this.defaultEra.hashCode()
            + (this.elapsedMode ? 1 : 0)
            + (this.altHinduSunrise ? 100 : 99);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hindu-variant=[");

        switch (this.type) {
            case TYPE_OLD_SOLAR:
                sb.append("OLD-SOLAR");
                break;
            case TYPE_OLD_LUNAR:
                sb.append("OLD-LUNAR");
                break;
            default:
                sb.append(this.getRule().name());
        }

        if (!this.isOld()) {
            sb.append("|default-era=");
            sb.append(this.defaultEra.name());
            sb.append('|');
            sb.append(this.elapsedMode ? "elapsed-year-mode" : "current-year-mode");
            if (this.altHinduSunrise) {
                sb.append("|alt-hindu-sunrise");
            }
            if (this.location != UJJAIN) {
                sb.append("|lat=");
                sb.append(this.location.getLatitude());
                sb.append(",lng=");
                sb.append(this.location.getLongitude());
                int altitude = this.location.getAltitude();
                if (altitude != 0) {
                    sb.append(",alt=");
                    sb.append(altitude);
                }
            }
        }

        sb.append(']');
        return sb.toString();
    }

    @Override
    public String getVariant() {
        if (this.isOld()) {
            AryaSiddhanta old = ((this.type == TYPE_OLD_SOLAR) ? AryaSiddhanta.SOLAR : AryaSiddhanta.LUNAR);
            return AryaSiddhanta.PREFIX + old.name();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this.type);
        sb.append('|');
        sb.append(this.defaultEra.name());
        sb.append('|');
        sb.append(this.elapsedMode ? "elapsed" : "current");
        sb.append('|');
        sb.append(this.altHinduSunrise ? "alt" : "std");
        if (this.location != UJJAIN) {
            sb.append('|');
            sb.append(this.location.getLatitude());
            sb.append('|');
            sb.append(this.location.getLongitude());
            int altitude = this.location.getAltitude();
            if (altitude != 0) {
                sb.append('|');
                sb.append(altitude);
            }
        }
        return sb.toString();
    }

    /**
     * <p>Creates a copy of this variant with given preferred era. </p>
     *
     * <p>Note: The old Hindu calendar is not customizable. </p>
     *
     * @param   defaultEra  the new deviating era
     * @return  modified copy or this variant if the era does not change
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit der angegebenen bevorzugten &Auml;ra. </p>
     *
     * <p>Hinweis: Der alte Hindu-Kalender erlaubt keine Anpassung. </p>
     *
     * @param   defaultEra  the new deviating era
     * @return  modified copy or this variant if the era does not change
     */
    public HinduVariant with(HinduEra defaultEra) {
        if (this.isOld() || this.defaultEra.equals(defaultEra)) {
            return this;
        }

        return new HinduVariant(this.type, defaultEra, this.elapsedMode, this.altHinduSunrise, this.location);
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
        if (this.isOld() || this.elapsedMode) {
            return this;
        }

        return new HinduVariant(this.type, this.defaultEra, true, this.altHinduSunrise, this.location);
    }

    /**
     * <p>Creates a copy of this variant with current years. </p>
     *
     * <p>Note: Elapsed years count one less than current years. The old Hindu calendar is not customizable. </p>
     *
     * @return  modified copy or this variant if the elapsed year mode does not change
     * @see     #withElapsedYears()
     * @see     #isUsingElapsedYears()
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit laufenden Jahren. </p>
     *
     * <p>Hinweis: Abgelaufene Jahre z&auml;hlen eins weniger als laufende Jahre. Der alte Hindu-Kalender
     * erlaubt keine Anpassung. </p>
     *
     * @return  modified copy or this variant if the elapsed year mode does not change
     * @see     #withElapsedYears()
     * @see     #isUsingElapsedYears()
     */
    public HinduVariant withCurrentYears() {
        if (this.isOld() || !this.elapsedMode) {
            return this;
        }

        return new HinduVariant(this.type, this.defaultEra, false, this.altHinduSunrise, this.location);
    }

    /**
     * <p>Creates a copy of this variant with an alternative internal calculation for the sunrise. </p>
     *
     * <p>Note: The old Hindu calendar is not customizable. </p>
     *
     * @return  modified copy or this variant if the alternative calculation mode was already set
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit einer alternativen internen Berechnung f&uuml;r
     * den Sonnenaufgang. </p>
     *
     * <p>Hinweis: Der alte Hindu-Kalender erlaubt keine Anpassung. </p>
     *
     * @return  modified copy or this variant if the alternative calculation mode was already set
     */
    public HinduVariant withAlternativeHinduSunrise() {
        if (this.isOld() || this.altHinduSunrise) {
            return this;
        }

        return new HinduVariant(this.type, this.defaultEra, this.elapsedMode, true, this.location);
    }

    /**
     * <p>Creates a copy of this variant with an alternative geographical location. </p>
     *
     * <p>By default, the location of the Holy City Ujjain is used. </p>
     *
     * <p>Note: The old Hindu calendar is not customizable. </p>
     *
     * @param   location    alternative geographical location
     * @return  modified copy or this variant if the location does not change
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante mit einer alternativen geographischen Bezugsangabe. </p>
     *
     * <p>Standardm&auml;&szlig;ig wird der Ort der heiligen Stadt Ujjain verwendet. </p>
     *
     * <p>Hinweis: Der alte Hindu-Kalender erlaubt keine Anpassung. </p>
     *
     * @param   location    alternative geographical location
     * @return  modified copy or this variant if the location does not change
     */
    public HinduVariant withAlternativeLocation(GeoLocation location) {
        if (this.isOld()) {
            return this;
        }

        if (
            (location.getLatitude() == this.location.getLatitude())
            && (location.getLongitude() == this.location.getLongitude())
            && (location.getAltitude() == this.location.getAltitude())
        ) {
            return this;
        }

        return new HinduVariant(this.type, this.defaultEra, this.elapsedMode, this.altHinduSunrise, location);
    }

    /**
     * <p>Obtains the associated calendar system. </p>
     *
     * @return  calendar system for this variant of Hindu calendar
     */
    /*[deutsch]
     * <p>Liefert das zu dieser Variante passende Kalendersystem. </p>
     *
     * @return  calendar system for this variant of Hindu calendar
     */
    HinduCS getCalendarSystem() {
        switch (this.type) {
            case TYPE_OLD_SOLAR:
                return AryaSiddhanta.SOLAR.getCalendarSystem();
            case TYPE_OLD_LUNAR:
                return AryaSiddhanta.LUNAR.getCalendarSystem();
            default:
                return new ModernHinduCS(this);
        }
    }

    /**
     * <p>Obtains the number of the first month of Hindu year. </p>
     *
     * @return  int
     */
    /*[deutsch]
     * <p>Liefert die Nummer des ersten Monats des Hindu-Jahres. </p>
     *
     * @return  int
     */
    int getFirstMonthOfYear() {
        if (this.isOld()) {
            return 1;
        }

        IndianMonth month;

        switch (this.getRule()) {
            case AMANTA_ASHADHA:
                month = IndianMonth.ASHADHA;
                break;
            case AMANTA_KARTIKA:
                month = IndianMonth.KARTIKA;
                break;
            default:
                month = IndianMonth.CHAITRA;
        }

        return month.getValue();
    }

    private static boolean useStandardElapsedMode(
        HinduEra defaultEra,
        HinduRule rule
    ) {
        switch (defaultEra) {
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

    private HinduRule getRule() {
        return RULES[this.type];
    }

    private HinduCS toAmanta() {
        return new HinduVariant(
            HinduRule.AMANTA.ordinal(),
            this.defaultEra,
            this.elapsedMode,
            this.altHinduSunrise,
            this.location
        ).getCalendarSystem();
    }

    /**
     * @serialData  Uses <a href="../../../../serialized-form.html#net.time4j.calendar.hindu/SPX">
     *              a dedicated serialization form</a> as proxy. The first byte contains
     *              the type-ID {@code 21}. Then the variant is written as UTF-String.
     *
     * @return  replacement object in serialization graph
     */
    private Object writeReplace() {
        return new SPX(this, SPX.HINDU_VAR);
    }

    /**
     * @serialData  Blocks because a serialization proxy is required.
     * @param       in      object input stream
     * @throws InvalidObjectException (always)
     */
    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("Serialization proxy required.");
    }

    //~ Innere Klassen ----------------------------------------------------

    private static class ModernHinduCS // TODO: implement
        extends HinduCS {

        //~ Konstruktoren -------------------------------------------------

        ModernHinduCS(HinduVariant variant) {
            super(variant);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        HinduCalendar create(long utcDays) {
            switch (this.getRule()) {
                case AMANTA_ASHADHA:
                case AMANTA_KARTIKA:
                    HinduCalendar cal = super.variant.toAmanta().create(utcDays);
                    if (cal.getMonth().getValue().getValue() < super.variant.getFirstMonthOfYear()) {
                        cal =
                            new HinduCalendar(
                                super.variant,
                                cal.getExpiredYearOfKaliYuga() - 1,
                                cal.getMonth(),
                                cal.getDayOfMonth(),
                                utcDays
                            );
                    }
                    return cal;
            }
            return null;
        }

        @Override
        HinduCalendar create(int kyYear, HinduMonth month, HinduDay dom) {
            switch (this.getRule()) {
                case AMANTA_ASHADHA:
                case AMANTA_KARTIKA:
                    if (month.getValue().getValue() < super.variant.getFirstMonthOfYear()) {
                        kyYear++;
                    }
                    return super.variant.toAmanta().create(kyYear, month, dom);
            }
            return null;
        }

        @Override
        boolean isValid(int kyYear, HinduMonth month, HinduDay dom) {
            switch (this.getRule()) {
                case AMANTA_ASHADHA:
                case AMANTA_KARTIKA:
                    if (month.getValue().getValue() < super.variant.getFirstMonthOfYear()) {
                        kyYear++;
                    }
                    return super.variant.toAmanta().isValid(kyYear, month, dom);
            }
            return false;
        }

        @Override
        public long getMinimumSinceUTC() {
            return 0;
        }

        @Override
        public long getMaximumSinceUTC() {
            return 0;
        }

        private HinduRule getRule() {
            return super.variant.getRule();
        }

    }

}
