/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2021 Meno Hochschild, <http://www.menodata.de/>
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

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.IndianMonth;
import net.time4j.calendar.astro.GeoLocation;
import net.time4j.calendar.astro.JulianDay;
import net.time4j.calendar.astro.MoonPhase;
import net.time4j.calendar.astro.StdSolarCalculator;
import net.time4j.engine.EpochDays;
import net.time4j.engine.VariantSource;
import net.time4j.scale.TimeScale;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.function.LongFunction;


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

    private static final double U_OFFSET = 5 * 3600 + 184.4;

    private static final HinduRule[] RULES = HinduRule.values();

    private static final int TYPE_OLD_SOLAR = -1;
    private static final int TYPE_OLD_LUNAR = -2;

    static final HinduVariant VAR_OLD_SOLAR = new HinduVariant(AryaSiddhanta.SOLAR);
    static final HinduVariant VAR_OLD_LUNAR = new HinduVariant(AryaSiddhanta.LUNAR);

    //~ Instanzvariablen --------------------------------------------------

    private transient final int type;
    private transient final HinduEra defaultEra;
    private transient final boolean elapsedMode;
    private transient final double depressionAngle;
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
            Double.NaN,
            UJJAIN
        );

    }

    private HinduVariant(AryaSiddhanta aryaSiddhanta) {
        this(
            (aryaSiddhanta == AryaSiddhanta.SOLAR) ? TYPE_OLD_SOLAR : TYPE_OLD_LUNAR,
            HinduEra.KALI_YUGA,
            true,
            Double.NaN,
            UJJAIN
        );

    }

    private HinduVariant(
        int type,
        HinduEra defaultEra,
        boolean elapsedMode,
        double depressionAngle,
        GeoLocation location
    ) {
        super();

        if ((type < TYPE_OLD_LUNAR) || (type >= HinduRule.values().length)) {
            throw new IllegalArgumentException("Undefined Hindu rule.");
        } else if (defaultEra == null) {
            throw new NullPointerException("Missing default Hindu era.");
        } else if (location == null) {
            throw new NullPointerException("Missing geographical location.");
        } else if (Double.isInfinite(depressionAngle)) {
            throw new IllegalArgumentException("Infinite depression angle.");
        } else if (!Double.isNaN(depressionAngle) && Math.abs(depressionAngle) > 10.0) {
            throw new IllegalArgumentException("Depression angle is too big: " + depressionAngle);
        }

        this.type = type;
        this.defaultEra = defaultEra;
        this.elapsedMode = elapsedMode;
        this.depressionAngle = depressionAngle;
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
        double depressionAngle = Double.NaN;
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
                    if (!token.equals("oldstyle") && !token.equals("alt") && !token.equals("std")) {
                        depressionAngle = Double.valueOf(token).doubleValue();
                    }
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
                depressionAngle,
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
                    && equals(this.depressionAngle, that.depressionAngle)
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
            + (Double.isNaN(this.depressionAngle) ? 100 : (int) this.depressionAngle * 100);
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
            if (!Double.isNaN(this.depressionAngle)) {
                sb.append("|depression-angle=");
                sb.append(this.depressionAngle);
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
        sb.append(Double.isNaN(this.depressionAngle) ? "oldstyle" : this.depressionAngle);
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

        return new HinduVariant(this.type, defaultEra, this.elapsedMode, this.depressionAngle, this.location);
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

        return new HinduVariant(this.type, this.defaultEra, true, this.depressionAngle, this.location);
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

        return new HinduVariant(this.type, this.defaultEra, false, this.depressionAngle, this.location);
    }

    /**
     * <p>Outdated method without any effect. </p>
     *
     * @return      this variant (unchanged)
     * @deprecated  Use {@link #withModernAstronomy(double)} instead
     */
    /*[deutsch]
     * <p>Veraltete Methode ohne Wirkung. </p>
     *
     * @return      this variant (unchanged)
     * @deprecated  Use {@link #withModernAstronomy(double)} instead
     */
    @Deprecated
    public HinduVariant withAlternativeHinduSunrise() {
        return this;
    }

    /**
     * <p>Creates a copy of this variant based on modern astronomy which also deploys an alternative
     * internal calculation for the sunrise or sunset. </p>
     *
     * <p>Note: The old Hindu calendar is not customizable. Most calendar makers in India use geometric sunrise
     * without refraction for the modern Hindu calendar, i.e. they use {@code 0.0} as depression angle. Another
     * author, Lahiri, uses the angle of 47' corresponding to (47 / 60) degrees. </p>
     *
     * @param   depressionAngle     the depression angle of sun used in sunrise/sunset-calculations
     * @return  modified copy or this variant if in old style (Arya-Siddhanta)
     * @throws  IllegalArgumentException if the depression angle is not a rational number in range -10.0 &lt;= x &lt;= 10.0
     */
    /*[deutsch]
     * <p>Erzeugt eine Kopie dieser Variante basierend auf moderner Astronomie, die auch mit einer alternativen
     * internen Berechnung f&uuml;r den Sonnenaufgang oder Untergang aufwartet. </p>
     *
     * <p>Hinweis: Der alte Hindu-Kalender erlaubt keine Anpassung. Die meisten Kalendermacher in Indien
     * verwenden heutzutage den geometrischen Sonnenaufgang ohne Beugungskorrektur im modernen Hindukalender,
     * d.h., sie verwenden {@code 0.0} als Winkelkorrektur. Ein anderer Autor, Lahiri, benutzt den Winkel
     * von 47 Bogenminuten entsprechend ~ (47 / 60) Grad. </p>
     *
     * @param   depressionAngle     the depression angle of sun used in sunrise/sunset-calculations
     * @return  modified copy or this variant if in old style (Arya-Siddhanta)
     * @throws  IllegalArgumentException if the depression angle is not a rational number in range -10.0 &lt;= x &lt;= 10.0
     */
    public HinduVariant withModernAstronomy(double depressionAngle) {
        if (Double.isNaN(depressionAngle) || Double.isInfinite(depressionAngle)) {
            throw new IllegalArgumentException("Depression angle must be a finite number.");
        } else if (this.isOld()) {
            return this;
        }

        return new HinduVariant(this.type, this.defaultEra, this.elapsedMode, depressionAngle, this.location);
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
     * @throws  IllegalArgumentException    if the absolute latitude is beyond 60 degrees
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
     * @throws  IllegalArgumentException    if the absolute latitude is beyond 60 degrees
     */
    public HinduVariant withAlternativeLocation(GeoLocation location) {
        if (Math.abs(location.getLatitude()) > 60.0) {
            throw new IllegalArgumentException("Latitudes beyond +/-60° degrees not supported.");
        } else if (this.isOld()) {
            return this;
        }

        if (
            (location.getLatitude() == this.location.getLatitude())
            && (location.getLongitude() == this.location.getLongitude())
            && (location.getAltitude() == this.location.getAltitude())
        ) {
            return this;
        }

        return new HinduVariant(this.type, this.defaultEra, this.elapsedMode, this.depressionAngle, location);
    }

    // Kerala region only
    boolean prefersRasiNames() {
        return (this.type == HinduRule.MADRAS.ordinal()) || (this.type == HinduRule.MALAYALI.ordinal());
    }

    // obtains the associated calendar system
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

    // obtains the number of the first month of Hindu year
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

    // also used by purnimanta new year
    HinduCS toAmanta() {
        return new HinduVariant(
            HinduRule.AMANTA.ordinal(),
            this.defaultEra,
            this.elapsedMode,
            this.depressionAngle,
            this.location
        ).getCalendarSystem();
    }

    private boolean useModernAstronomy() {
        return !Double.isNaN(this.depressionAngle);
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

    private static boolean equals(
        double d1,
        double d2
    ) {
        if (Double.isNaN(d1)) {
            return Double.isNaN(d2);
        } else if (Double.isNaN(d2)) {
            return false;
        } else {
            return (d1 == d2);
        }
    }

    private HinduRule getRule() {
        return RULES[this.type];
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

    @SuppressWarnings("ConstantConditions")
    static class ModernHinduCS
        extends HinduCS {

        //~ Statische Felder/Initialisierungen ----------------------------

        private static final int MIN_YEAR = 1200;
        private static final int MAX_YEAR = 5999;

        private static final boolean CC = false; // handling of CC sign error in calculating of precession
        static final double SIDEREAL_YEAR = 365d + (279457.0 / 1080000.0);
        static final double SIDEREAL_START = CC ? 336.1360597836302 : 336.1360765905204;

        private static final double SIDEREAL_MONTH = 27d + (4644439.0 / 14438334.0);
        private static final double SYNODIC_MONTH = 29d + (7087771.0 / 13358334.0);
        private static final double EPSILON = Math.pow(2, -1000); // max recursion depth: 1000

        private static final double CREATION = KALI_YUGA_EPOCH - 1955880000d * SIDEREAL_YEAR;
        private static final double ANOMALISTIC_YEAR = 1577917828000d / (4320000000L - 387);
        private static final double ANOMALISTIC_MONTH = 1577917828d / (57753336 - 488199);
        private static final double MEAN_SIDEREAL_YEAR = 365.25636;
        private static final double MEAN_SYNODIC_MONTH = 29.530588861;

        private static final double[] RISING_SIGN_FACTORS;

        static {
            double[] f = new double[6];
            f[0] = 1670d / 1800d;
            f[1] = 1795d / 1800d;
            f[2] = 1935d / 1800d;
            f[3] = 1935d / 1800d;
            f[4] = 1795d / 1800d;
            f[5] = 1670d / 1800d;
            RISING_SIGN_FACTORS = f;
        }

        //~ Instanzvariablen ----------------------------------------------

        private volatile long min = Long.MIN_VALUE;
        private volatile long max = Long.MAX_VALUE;

        //~ Konstruktoren -------------------------------------------------

        ModernHinduCS(HinduVariant variant) {
            super(variant);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        HinduCalendar create(long utcDays) {
            HinduVariant hv = super.variant;

            switch (this.getRule()) {
                case ORISSA:
                case TAMIL:
                case MALAYALI:
                case MADRAS:
                    return hSolarFromFixed(utcDays, hv);
                case AMANTA:
                    return hLunarFromFixed(utcDays, hv);
                case AMANTA_ASHADHA:
                case AMANTA_KARTIKA:
                    HinduCalendar cal = hv.toAmanta().create(utcDays);
                    int kyYear = cal.getExpiredYearOfKaliYuga();
                    if (cal.getMonth().getValue().getValue() < hv.getFirstMonthOfYear()) {
                        kyYear--;
                    }
                    return new HinduCalendar(hv, kyYear, cal.getMonth(), cal.getDayOfMonth(), utcDays);
                case PURNIMANTA:
                    HinduCS amantaCS = hv.toAmanta();
                    HinduCalendar amantaCal = amantaCS.create(utcDays);
                    HinduMonth m =
                        HinduMonth.ofLunisolar(
                            (amantaCal.getDayOfMonth().getValue() >= 16)
                                ? amantaCS.create(utcDays + 20).getMonth().getValue().getValue()
                                : amantaCal.getMonth().getValue().getValue()
                        );
                    if (amantaCal.getMonth().isLeap()) {
                        m = m.withLeap();
                    }
                    return new HinduCalendar(
                        hv,
                        amantaCal.getExpiredYearOfKaliYuga(),
                        m,
                        amantaCal.getDayOfMonth(),
                        utcDays
                    );
                default:
                    throw new UnsupportedOperationException(this.getRule().name());
            }
        }

        @Override
        HinduCalendar create(int kyYear, HinduMonth month, HinduDay dom) {
            HinduVariant hv = super.variant;
            long utcDays;

            switch (this.getRule()) {
                case ORISSA:
                case TAMIL:
                case MALAYALI:
                case MADRAS:
                    utcDays = hFixedFromSolar(kyYear, month, dom, hv);
                    break;
                case AMANTA:
                    utcDays = hFixedFromLunar(kyYear, month, dom, hv);
                    break;
                case AMANTA_ASHADHA:
                case AMANTA_KARTIKA:
                    int amantaYear = kyYear;
                    if (month.getValue().getValue() < hv.getFirstMonthOfYear()) {
                        amantaYear++;
                    }
                    HinduCalendar cal = hv.toAmanta().create(amantaYear, month, dom);
                    return new HinduCalendar(hv, kyYear, month, dom, cal.getDaysSinceEpochUTC());
                case PURNIMANTA:
                    HinduMonth m;
                    if (month.isLeap() || (dom.getValue() <= 15)) {
                        m = month;
                    } else if (super.variant.toAmanta().isExpunged(kyYear, prevMonth(month, 1))) {
                        m = prevMonth(month, 2);
                    } else {
                        m = prevMonth(month, 1);
                    }
                    utcDays = hFixedFromLunar(kyYear, m, dom, hv);
                    break;
                default:
                    throw new UnsupportedOperationException(this.getRule().name());
            }

            return new HinduCalendar(hv, kyYear, month, dom, utcDays);
        }

        @Override
        boolean isValid(int kyYear, HinduMonth month, HinduDay dom) {

            if ((kyYear < MIN_YEAR) || (kyYear > MAX_YEAR) || (month == null) || (dom == null)) {
                return false;
            }

            if (super.variant.isSolar() && (month.isLeap() || dom.isLeap())) {
                return false;
            } else if (super.variant.isLunisolar() && (dom.getValue() > 30)) {
                return false;
            }

            HinduCS calsys = this;
            HinduRule rule = this.getRule();

            if ((rule == HinduRule.AMANTA_ASHADHA) || (rule == HinduRule.AMANTA_KARTIKA)) {
                if (month.getValue().getValue() < super.variant.getFirstMonthOfYear()) {
                    kyYear++;
                }
                calsys = super.variant.toAmanta();
            }

            return !calsys.isExpunged(kyYear, month, dom);
        }

        @Override
        public long getMinimumSinceUTC() {
            if (this.min == Long.MIN_VALUE) {
                HinduCalendar cal;
                if (super.variant.isPurnimanta()) {
                    cal = this.createNewYear(MIN_YEAR + 1).withFirstDayOfMonth();
                } else {
                    cal = this.createNewYear(MIN_YEAR);
                }
                this.min = cal.getDaysSinceEpochUTC();
            }
            return this.min;
        }

        @Override
        public long getMaximumSinceUTC() {
            if (this.max == Long.MAX_VALUE) {
                HinduCalendar cal = this.createNewYear(MAX_YEAR + 1);
                if (super.variant.isPurnimanta()) {
                    cal = cal.withFirstDayOfMonth();
                }
                this.max = cal.getDaysSinceEpochUTC() - 1;
            }
            return this.max;
        }

        private HinduCalendar createNewYear(int year) {
            // AGRAHAYANA is chosen as intermediate value to satisfy AMANTA_ASHADHA and AMANTA_KARTIKA, too
            return this.create(year, HinduMonth.of(IndianMonth.AGRAHAYANA), HinduDay.valueOf(1)).withNewYear();
        }

        private HinduRule getRule() {
            return super.variant.getRule();
        }

        private static HinduMonth prevMonth(
            HinduMonth month,
            int steps
        ) {
            int m = month.getValue().getValue() - steps;

            if (m <= 0) {
                m += 12;
            }

            return HinduMonth.ofLunisolar(m);
        }

        //~ Hindu astronomy taken from Dershowitz/Reingold ----------------

        private static double hSineTable(double entry) {
            double exact = 3438d * Math.sin(Math.toRadians(entry * 3.75));
            double error = 0.215 * Math.signum(exact) * Math.signum(Math.abs(exact) - 1716d);
            return Math.floor(exact + error + 0.5) / 3438d;
        }

        private static double hSine(double theta) {
            double entry = theta / 3.75;
            double fraction = modulo(entry, 1d);
            return fraction * hSineTable(Math.ceil(entry)) + (1d - fraction) * hSineTable(Math.floor(entry));
        }

        private static double hArcSin(double amp) {
            if (amp < 0) {
                return -hArcSin(-amp);
            } else {
                int pos = 0;
                while (amp > hSineTable(pos)) {
                    pos++;
                }
                double below = hSineTable(pos - 1);
                return 3.75 * (pos - 1 + ((amp - below) / (hSineTable(pos) - below)));
            }
        }

        private static double hMeanPosition(
            double t,
            double period
        ) {
            return 360d * modulo((t - CREATION) / period, 1d);
        }

        private static double hTruePosition(
            double t,
            double period,
            double size,
            double anomalistic,
            double change
        ) {
            double lambda = hMeanPosition(t, period);
            double offset = hSine(hMeanPosition(t, anomalistic));
            double contraction = Math.abs(offset) * change * size;
            double equation = hArcSin(offset * (size - contraction));
            return modulo(lambda - equation, 360d);
        }

        private static double hSiderealSolarLongitude(double t) {
            return modulo(
                StdSolarCalculator.CC.getFeature(toJDE(t).getValue(), "solar-longitude")
                    - hPrecession(t)
                    + SIDEREAL_START,
                360d);
        }

        static double hSolarLongitude(double t) {
            return hTruePosition(t, SIDEREAL_YEAR, 14d / 360d, ANOMALISTIC_YEAR, 1d / 42d);
        }

        // verified with Meeus example 21.c
        static double hPrecession(double t) {
            double jct = toJDE(t).getCenturyJ2000();
            int sign = CC ? 1 : -1;

            // using Meeus (21.6)
            double eta = modulo(
                ((47.0029 / 3600) + ((-0.03302 / 3600) + (0.00006 / 3600) * jct) * jct) * jct,
                360);
            double P = modulo(
                174.876384 + ((-869.8089 / 3600) + (0.03536 / 3600) * jct) * jct,
                360);
            double p = modulo(
                ((5029.0966 / 3600) + ((1.11113 / 3600) + (sign * 0.000006 / 3600) * jct) * jct) * jct,
                360);

            // use solar latitude = 0 and solar longitude (at mesha samkranti) = 0 in Meeus (21.7)
            double A = Math.cos(Math.toRadians(eta)) * Math.sin(Math.toRadians(P));
            double B = Math.cos(Math.toRadians(P));
            double arg = Math.toDegrees(Math.atan2(A, B));
            return modulo(p + P - arg, 360d);
        }

        private static JulianDay toJDE(double t) { // argument as rata die
            long unix = Math.round((t + 1721424L - 2440587L) * 86400);
            return JulianDay.ofEphemerisTime(Moment.of(unix, TimeScale.POSIX));
        }

        private static double toRataDie(Moment m) {
            return (m.getPosixTime() / 86400d) + 2440587L - 1721424L;
        }

        private static int hZodiac(double t) {
            return (int) (Math.floor(hSolarLongitude(t) / 30d) + 1);
        }

        private static int hSiderealZodiac(double t) {
            return (int) (Math.floor(hSiderealSolarLongitude(t) / 30d) + 1);
        }

        private static double hLunarLongitude(double t) {
            return hTruePosition(t, SIDEREAL_MONTH, 32d / 360d, ANOMALISTIC_MONTH, 1d / 96d);
        }

        private static double hLunarPhase(double t) {
            return modulo(hLunarLongitude(t) - hSolarLongitude(t), 360d);
        }

        private static int hLunarDayFromMoment(
            double t,
            HinduVariant variant
        ) {
            double lunarPhase;

            if (variant.useModernAstronomy()) {
                double jde = toJDE(t).getValue();
                double solarLongitude = StdSolarCalculator.CC.getFeature(jde, "solar-longitude");
                double lunarLongitude = StdSolarCalculator.CC.getFeature(jde, "lunar-longitude");
                double phi = modulo(lunarLongitude - solarLongitude, 360d);
                int n = (int) Math.round((t - nthNewMoon(0)) / MEAN_SYNODIC_MONTH);
                double phi2 = 360 * modulo((t - nthNewMoon(n)) / MEAN_SYNODIC_MONTH, 1d);
                lunarPhase = (Math.abs(phi - phi2) > 180) ? phi2 : phi;
            } else {
                lunarPhase = hLunarPhase(t);
            }

            return (int) (Math.floor(lunarPhase / 12d) + 1);
        }

        private static double nthNewMoon(int n) {
            return toRataDie(MoonPhase.NEW_MOON.atLunation(n - 24724));
        }

        private static double hNewMoonBefore(double t) {
            double tau = t - ((hLunarPhase(t) * SYNODIC_MONTH) / 360d);
            return binarySearchLunarPhase(tau - 1, Math.min(t, tau + 1));
        }

        private static double binarySearchLunarPhase(
            double low,
            double high
        ) {
            double x = (low + high) / 2;

            if ((hZodiac(low) == hZodiac(high)) || (high - low < EPSILON)) {
                return x;
            }

            if (hLunarPhase((low + high) / 2) < 180) {
                return binarySearchLunarPhase(low, x);
            } else {
                return binarySearchLunarPhase(x, high);
            }
        }

        private static int hCalendarYear(
            double t,
            HinduVariant variant
        ) {
            if (variant.useModernAstronomy()) {
                return (int) Math.floor(
                    0.5 + ((t - KALI_YUGA_EPOCH) / MEAN_SIDEREAL_YEAR) - (hSiderealSolarLongitude(t) / 360d));
            } else {
                return (int) Math.floor(
                    0.5 + ((t - KALI_YUGA_EPOCH) / SIDEREAL_YEAR) - (hSolarLongitude(t) / 360d));
            }
        }

        private static HinduCalendar hSolarFromFixed(
            long utcDays,
            HinduVariant variant
        ) {
            assert variant.isSolar();

            LongFunction<Double> function = hCritical(variant);
            long rataDie = EpochDays.RATA_DIE.transform(utcDays, EpochDays.UTC);
            double critical = function.apply(rataDie);
            int kyYear = hCalendarYear(critical, variant);

            int m;
            long start;

            if (variant.useModernAstronomy()) {
                m = hSiderealZodiac(critical);
                start = rataDie - 3 - (int) modulo(Math.floor(hSiderealSolarLongitude(critical)), 30);

                while (hSiderealZodiac(function.apply(start)) != m) {
                    start++;
                }
            } else {
                m = hZodiac(critical);
                start = rataDie - 3 - (int) modulo(Math.floor(hSolarLongitude(critical)), 30);

                while (hZodiac(function.apply(start)) != m) {
                    start++;
                }
            }

            return new HinduCalendar(
                variant,
                kyYear,
                HinduMonth.ofSolar(m),
                HinduDay.valueOf((int) (rataDie - start + 1)),
                utcDays
            );
        }

        private static long hFixedFromSolar(
            int kyYear,
            HinduMonth month,
            HinduDay dom,
            HinduVariant variant
        ) {
            assert variant.isSolar();

            int m = month.getRasi();
            LongFunction<Double> function = hCritical(variant);
            double siderealYear = (variant.useModernAstronomy() ? MEAN_SIDEREAL_YEAR : SIDEREAL_YEAR);
            long start = KALI_YUGA_EPOCH - 3 + (long) Math.floor(siderealYear * (kyYear + ((m - 1) / 12d)));

            if (variant.useModernAstronomy()) {
                while (hSiderealZodiac(function.apply(start)) != m) {
                    start++;
                }
            } else {
                while (hZodiac(function.apply(start)) != m) {
                    start++;
                }
            }

            return EpochDays.UTC.transform(dom.getValue() - 1 + start, EpochDays.RATA_DIE);
        }

        private static LongFunction<Double> hCritical(final HinduVariant variant) {
            switch (variant.getRule()) {
                case ORISSA:
                    return (rataDie) -> hSunrise(rataDie + 1, variant);
                case TAMIL:
                    return (rataDie) -> hSunset(rataDie, variant);
                case MALAYALI:
                    return (rataDie) -> hStandardFromSundial(rataDie + (13 * 60 + 12) / 1440d, variant);
                case MADRAS:
                    return (rataDie) -> hStandardFromSundial(rataDie + 1, variant);
                default:
                    throw new UnsupportedOperationException("Not yet implemented.");
            }
        }

        private static HinduCalendar hLunarFromFixed(
            long utcDays,
            HinduVariant variant
        ) {
            assert variant.isLunisolar();

            long rataDie = EpochDays.RATA_DIE.transform(utcDays, EpochDays.UTC);
            double critical = hSunrise(rataDie, variant);
            int dom = hLunarDayFromMoment(critical, variant);
            HinduDay dayOfMonth = HinduDay.valueOf(dom);

            if (hLunarDayFromMoment(hSunrise(rataDie - 1, variant), variant) == dom) {
                dayOfMonth = dayOfMonth.withLeap();
            }

            double lastNewMoon;
            double nextNewMoon;
            int solarMonth;
            int nextSolarMonth;

            if (variant.useModernAstronomy()) {
                Moment m = toJDE(critical).toMoment();
                lastNewMoon = toRataDie(MoonPhase.NEW_MOON.before(m));
                nextNewMoon = toRataDie(MoonPhase.NEW_MOON.atOrAfter(m));
                solarMonth = hSiderealZodiac(lastNewMoon);
                nextSolarMonth = hSiderealZodiac(nextNewMoon);
            } else {
                lastNewMoon = hNewMoonBefore(critical);
                nextNewMoon = hNewMoonBefore(Math.floor(lastNewMoon) + 35d);
                solarMonth = hZodiac(lastNewMoon);
                nextSolarMonth = hZodiac(nextNewMoon);
            }

            int lunarMonth = ((solarMonth == 12) ? 1 : solarMonth + 1);
            HinduMonth month = HinduMonth.ofLunisolar(lunarMonth);

            if (nextSolarMonth == solarMonth) {
                month = month.withLeap();
            }

            int kyYear = hCalendarYear((lunarMonth <= 2) ? rataDie + 180d : rataDie, variant);

            return new HinduCalendar(
                variant,
                kyYear,
                month,
                dayOfMonth,
                utcDays
            );
        }

        private static long hFixedFromLunar(
            int kyYear,
            HinduMonth month,
            HinduDay dom,
            HinduVariant variant
        ) {
            assert variant.isLunisolar();

            int m = month.getValue().getValue();
            double approx, x;

            if (variant.useModernAstronomy()) {
                approx = KALI_YUGA_EPOCH + MEAN_SIDEREAL_YEAR * (kyYear + ((m - 1) / 12d));
                x = hSiderealSolarLongitude(approx);
            } else {
                approx = KALI_YUGA_EPOCH + SIDEREAL_YEAR * (kyYear + ((m - 1) / 12d));
                x = hSolarLongitude(approx);
            }

            x = (x / 360d) - ((m - 1) / 12d);
            long s = (long) Math.floor(approx - SIDEREAL_YEAR * (-0.5 + modulo(x + 0.5, 1d)));
            int k = hLunarDayFromMoment(s + 0.25, variant);
            int day = dom.getValue();
            int temp;

            if ((k > 3) && (k < 27)) {
                temp = k;
            } else  {
                HinduCalendar mid = hLunarFromFixed(EpochDays.UTC.transform(s - 15, EpochDays.RATA_DIE), variant);
                if (
                    (mid.getMonth().getValue() != month.getValue())
                    || (mid.getMonth().isLeap() && !month.isLeap())
                ) {
                    temp = -15 + (int) modulo(k + 15, 30);
                } else {
                    temp = 15 + (int) modulo(k - 15, 30);
                }
            }

            long est = s + day - temp;
            long d = est + 14 - (int) modulo(hLunarDayFromMoment(est + 0.25, variant) - day + 15, 30);

            while (true) {
                int ld = hLunarDayFromMoment(hSunrise(d, variant), variant);
                int mm = (int) modulo(day + 1, 30);
                int day2 = (mm == 0) ? 30 : mm;

                if ((ld == day) || (ld == day2)) {
                    break;
                } else {
                    d++;
                }
            }

            if (dom.isLeap()) {
                d++;
            }

            return EpochDays.UTC.transform(d, EpochDays.RATA_DIE);
        }

        private static double hAscensionalDifference(
            double rataDie,
            GeoLocation location
        ) {
            double sinDelta = (1397d / 3438d) * hSine(hTropicalLongitude(rataDie));
            double latitude = location.getLatitude();
            double diurnalRadius = hSine(90 + hArcSin(sinDelta));
            double earthSine = sinDelta * (hSine(latitude) / hSine(90 + latitude));
            return hArcSin(-1 * earthSine / diurnalRadius);
        }

        private static double hSolarSiderealDifference(double rataDie) {
            return hDailyMotion(rataDie) * hRisingSign(rataDie);
        }

        private static double hEquationOfTime(double rataDie) {
            double offset = hSine(hMeanPosition(rataDie, ANOMALISTIC_YEAR));
            double equationSun = offset * (57 + 18d / 60d) * ((14d / 360d) - (Math.abs(offset) / 1080d));
            return (hDailyMotion(rataDie) / 360d) * (equationSun / 360d) * SIDEREAL_YEAR;
        }

        private static double hTropicalLongitude(double rataDie) {
            double x = (600d / 1577917828d) * (rataDie - KALI_YUGA_EPOCH) - 0.25;
            double precession = 27 - Math.abs(108 * (-0.5 + modulo(x + 0.5, 1d)));
            return modulo(hSolarLongitude(rataDie) - precession, 360d);
        }

        private static double hDailyMotion(double rataDie) {
            double anomaly = hMeanPosition(rataDie, ANOMALISTIC_YEAR);
            double epicycle = (14d / 360d) - (1d / 1080d) * Math.abs(hSine(anomaly));
            double entry = Math.floor(anomaly / 3.75d);
            double sineTableStep = hSineTable(entry + 1) - hSineTable(entry);
            double factor = 1d - (3438d / 225d) * sineTableStep * epicycle;
            return (360d / SIDEREAL_YEAR) * factor;
        }

        private static double hRisingSign(double rataDie) {
            int index = (int) Math.floor(hTropicalLongitude(rataDie) / 30d);
            return RISING_SIGN_FACTORS[(int) modulo(index, 6)];
        }

        private static double hSunrise(
            double rataDie,
            HinduVariant variant
        ) {
            if (variant.useModernAstronomy()) {
                GeoLocation location = variant.getLocation();
                PlainDate date = PlainDate.of((long) Math.floor(rataDie), EpochDays.RATA_DIE);
                Moment astroSunrise = // CC: page 357, citation of Purewal (14)
                    StdSolarCalculator.CC.sunrise(
                        date,
                        location.getLatitude(),
                        location.getLongitude(),
                        90.0 + variant.depressionAngle
                    ).get();
                double unixDays = (astroSunrise.getPosixTime() + U_OFFSET) / 86400d;
                long fixed = EpochDays.RATA_DIE.transform((long) Math.floor(unixDays), EpochDays.UNIX);
                return fixed + unixDays - Math.floor(unixDays);
            } else {
                double od = rataDie + 0.25 + (UJJAIN.getLongitude() - variant.getLocation().getLongitude()) / 360d;
                double ascDiff = hAscensionalDifference(rataDie, variant.getLocation());
                double f = 0.25 * hSolarSiderealDifference(rataDie) + ascDiff;
                return od - hEquationOfTime(rataDie) + (1577917828d / (1582237828d * 360d)) * f;
            }
        }

        private static double hSunset(
            double rataDie,
            HinduVariant variant
        ) {
            if (variant.useModernAstronomy()) {
                GeoLocation location = variant.getLocation();
                PlainDate date = PlainDate.of((long) Math.floor(rataDie), EpochDays.RATA_DIE);
                Moment astroSunset = // CC: page 357, citation of Purewal (14)
                    StdSolarCalculator.CC.sunset(
                        date,
                        location.getLatitude(),
                        location.getLongitude(),
                        90.0 + variant.depressionAngle
                    ).get();
                double unixDays = (astroSunset.getPosixTime() + U_OFFSET) / 86400d;
                long fixed = EpochDays.RATA_DIE.transform((long) Math.floor(unixDays), EpochDays.UNIX);
                return fixed + unixDays - Math.floor(unixDays);
            } else {
                double od = rataDie + 0.75 + (UJJAIN.getLongitude() - variant.getLocation().getLongitude()) / 360d;
                double ascDiff = hAscensionalDifference(rataDie, variant.getLocation());
                double f = 0.75 * hSolarSiderealDifference(rataDie) - ascDiff;
                return od - hEquationOfTime(rataDie) + (1577917828d / (1582237828d * 360d)) * f;
            }
        }

        private static double hStandardFromSundial(
            double t,
            HinduVariant variant
        ) {
            double date = Math.floor(t);
            double time = t - date;
            int q = (int) Math.floor(4 * time);
            double a, b, c;

            if (q == 0) {
                a = hSunset(date - 1, variant);
                b = hSunrise(date, variant);
                c = -0.25;
            } else if (q == 3) {
                a = hSunset(date, variant);
                b = hSunrise(date + 1, variant);
                c = 0.75;
            } else {
                a = hSunrise(date, variant);
                b = hSunset(date, variant);
                c = 0.25;
            }

            return a + 2 * (b - a) * (time - c);
        }

    }

}
