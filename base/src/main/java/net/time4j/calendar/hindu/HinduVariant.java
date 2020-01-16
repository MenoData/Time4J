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

import net.time4j.calendar.astro.GeoLocation;
import net.time4j.engine.CalendarEra;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.EpochDays;
import net.time4j.engine.VariantSource;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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

    private static final HinduRule[] RULES = HinduRule.values();

    // Attention: not serializable (TODO: serialization via SPX)
    private static final GeoLocation UJJAIN = GeoLocation.of(23.0 + 9.0 / 60.0, 75.0 + 46.0 / 60.0 + 6.0 / 3600.0);

    private static final int TYPE_OLD_SOLAR = -1;
    private static final int TYPE_OLD_LUNAR = -2;

    static final HinduVariant VAR_OLD_SOLAR = new HinduVariant(AryaSiddhanta.SOLAR);
    static final HinduVariant VAR_OLD_LUNAR = new HinduVariant(AryaSiddhanta.LUNAR);

    //~ Instanzvariablen --------------------------------------------------

    /**
     * @serial the rule to be used
     */
    private final int type;

    /**
     * @serial default era
     */
    private final HinduEra defaultEra;

    /**
     * @serial determines if elapsed years are used
     */
    private final boolean elapsedMode;

    /**
     * @serial determines if astronomical calculations for an alternative sunrise are used
     */
    private final boolean altHinduSunrise;

    /**
     * @serial determines the geographical location as reference in time zone offset calculations
     */
    private final GeoLocation location;

    //~ Konstruktoren -----------------------------------------------------

    HinduVariant(
        HinduRule rule,
        HinduEra defaultEra
    ) {
        this(rule.ordinal(), defaultEra, useStandardElapsedMode(defaultEra, rule), false, UJJAIN);

    }

    private HinduVariant(AryaSiddhanta aryaSiddhanta) {
        this(
            (aryaSiddhanta == AryaSiddhanta.SOLAR) ? TYPE_OLD_SOLAR : TYPE_OLD_LUNAR,
            HinduEra.KALI_YUGA,
            true,
            false,
            UJJAIN);
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
     * <p>
     * <p>The variant string is the same as created by calling {@link #getVariant()}. </p>
     *
     * @param   variant variant string
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
        if (variant.startsWith(AryaSiddhanta.PREFIX)) {
            return new HinduVariant(AryaSiddhanta.valueOf(variant.substring(AryaSiddhanta.PREFIX.length())));
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
                    break;
                case 6:
                    longitude = Double.valueOf(token).doubleValue();
                    break;
                case 7:
                    altitude = Integer.valueOf(token).intValue();
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
                GeoLocation.of(latitude, longitude, altitude)
            );
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
        switch (this.type) {
            case TYPE_OLD_SOLAR:
                return AryaSiddhanta.SOLAR.getCalendarSystem();
            case TYPE_OLD_LUNAR:
                return AryaSiddhanta.LUNAR.getCalendarSystem();
            default:
                return this.getRule().getCalendarSystem();
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
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante den lunisolaren Hindukalender beschreibt. </p>
     *
     * @return  boolean
     */
    public boolean isLunisolar() {
        return ((this.type == TYPE_OLD_LUNAR) || this.isAmanta() || this.isPurnimanta());
    }

    /**
     * <p>Determines if this variant describes the amanta scheme. </p>
     *
     * @return  boolean
     */
    /*[deutsch]
     * <p>Bestimmt, ob diese Variante das Amanta-Schema beschreibt. </p>
     *
     * @return  boolean
     */
    public boolean isAmanta() {
        return ((this.type >= HinduRule.AMANTA.ordinal()) && (this.type < HinduRule.PURNIMANTA.ordinal()));
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
        return (this.type == HinduRule.PURNIMANTA.ordinal());
    }

    /**
     * <p>Does this variant use elapsed years? </p>
     * <p>
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
        sb.append(']');
        return sb.toString();
    }

    @Override
    public String getVariant() {
        if (this.type < 0) {
            AryaSiddhanta old = ((this.type == TYPE_OLD_SOLAR) ? AryaSiddhanta.SOLAR : AryaSiddhanta.LUNAR);
            return old.getVariant();
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
        if (this.defaultEra.equals(defaultEra)) {
            return this;
        }

        return new HinduVariant(this.type, defaultEra, this.elapsedMode, this.altHinduSunrise, this.location);
    }

    /**
     * <p>Creates a copy of this variant with elapsed years. </p>
     * <p>
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

        return new HinduVariant(this.type, this.defaultEra, true, this.altHinduSunrise, this.location);
    }

    /**
     * <p>Creates a copy of this variant with current years. </p>
     * <p>
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

        return new HinduVariant(this.type, this.defaultEra, false, this.altHinduSunrise, this.location);
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

    //~ Innere Klassen ----------------------------------------------------

    static abstract class BaseCS
        implements CalendarSystem<HinduCalendar> {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final long KALI_YUGA_EPOCH = -1132959; // julian-BCE-3102-02-18 (as rata die)

        //~ Instanzvariablen ----------------------------------------------

        final HinduVariant variant;

        //~ Konstruktoren -------------------------------------------------

        BaseCS(HinduVariant variant) {
            super();

            if (variant == null) {
                throw new NullPointerException();
            }

            this.variant = variant;
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public long getMinimumSinceUTC() {
            return EpochDays.UTC.transform(KALI_YUGA_EPOCH, EpochDays.RATA_DIE);
        }

        @Override
        public long getMaximumSinceUTC() {
            return 0; // TODO: implementieren
        }

        @Override
        public List<CalendarEra> getEras() {
            return Arrays.asList(HinduEra.values());
        }

        // used in subclasses
        static double modulo(double x, double y) {
            return x - y * Math.floor(x / y);
        }

    }

    private static class OldSolarCS
        extends BaseCS {

        //~ Statische Felder/Initialisierungen ----------------------------

        static final double ARYA_SOLAR_YEAR = 15779175.0 / 43200.0;
        static final double ARYA_SOLAR_MONTH = ARYA_SOLAR_YEAR / 12.0;

        //~ Konstruktoren -------------------------------------------------

        OldSolarCS(HinduVariant variant) {
            super(variant);
        }

        //~ Methoden ------------------------------------------------------

        @Override
        public HinduCalendar transform(long utcDays) {
            double sun = EpochDays.RATA_DIE.transform(utcDays, EpochDays.UTC) - KALI_YUGA_EPOCH + 0.25;
            int y = (int) Math.floor(sun / ARYA_SOLAR_YEAR);
            int m = (int) modulo(Math.floor(sun / ARYA_SOLAR_MONTH), 12) + 1;
            int dom = (int) Math.floor(modulo(sun, ARYA_SOLAR_MONTH)) + 1;

            return new HinduCalendar(
                super.variant,
                y,
                HinduMonth.ofSolar(m),
                HinduDay.valueOf(dom));
        }

        @Override
        public long transform(HinduCalendar date) {
            double d =
                KALI_YUGA_EPOCH
                    + date.getExpiredYearOfKaliYuga() * ARYA_SOLAR_YEAR
                    + (date.getMonth().getRasi() - 1) * ARYA_SOLAR_MONTH
                    + date.getDayOfMonth().getValue()
                    - 1.25;
            return EpochDays.UTC.transform((long) Math.ceil(d), EpochDays.RATA_DIE);
        }

    }

}