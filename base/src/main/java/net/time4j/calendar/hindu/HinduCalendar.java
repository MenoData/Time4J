/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HinduCalendar.java) is part of project Time4J.
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

import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.MathUtils;
import net.time4j.calendar.IndianCalendar;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarSystem;
import net.time4j.engine.CalendarVariant;
import net.time4j.format.CalendarType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>The traditional Hindu calendar which exists in many regional variants. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Der traditionelle Hindukalender, der in vielen verschiedenen regionalen Varianten existiert. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
@CalendarType("extra/hindu")
public final class HinduCalendar
    extends CalendarVariant<HinduCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final Map<String, CalendarSystem<HinduCalendar>> CALSYS;
    private static final CalendarFamily<HinduCalendar> ENGINE;

    static {
        Map<String, CalendarSystem<HinduCalendar>> calsys = new VariantMap();
//        for (HinduRule rule : HinduRule.values()) {
//            HinduVariant v = HinduVariant.ofSolar(rule);
//            calsys.put(v.getVariant(), v.getCalendarSystem());
//        }
//        HinduVariant amanta = HinduVariant.ofAmanta();
//        HinduVariant purnimanta = HinduVariant.ofPurnimanta();
//        calsys.put(amanta.getVariant(), amanta.getCalendarSystem());
//        calsys.put(purnimanta.getVariant(), purnimanta.getCalendarSystem());
        CALSYS = calsys;

        ENGINE = null;

//        CalendarFamily.Builder<HinduCalendar> builder =
//            CalendarFamily.Builder.setUp(
//                HinduCalendar.class,
//                new Merger(),
//                CALSYS)
//                .appendElement(
//                    ERA,
//                    new EraRule())
//                .appendElement(
//                    YEAR_OF_ERA,
//                    new IntegerRule(YEAR_INDEX))
//                .appendElement(
//                    MONTH_OF_YEAR,
//                    new MonthRule())
//                .appendElement(
//                    CommonElements.RELATED_GREGORIAN_YEAR,
//                    new RelatedGregorianYearRule<>(CALSYS, DAY_OF_YEAR))
//                .appendElement(
//                    DAY_OF_MONTH,
//                    new IntegerRule(DAY_OF_MONTH_INDEX))
//                .appendElement(
//                    DAY_OF_YEAR,
//                    new IntegerRule(DAY_OF_YEAR_INDEX))
//                .appendElement(
//                    DAY_OF_WEEK,
//                    new WeekdayRule<>(
//                        getDefaultWeekmodel(),
//                        (context) -> context.getChronology().getCalendarSystem(context.getVariant())
//                    ))
//                .appendElement(
//                    WIM_ELEMENT,
//                    WeekdayInMonthElement.getRule(WIM_ELEMENT))
//                .appendExtension(
//                    new CommonElements.Weekengine(
//                        HijriCalendar.class,
//                        DAY_OF_MONTH,
//                        DAY_OF_YEAR,
//                        getDefaultWeekmodel()));
//        ENGINE = builder.build();
    }

    //~ Instanzvariablen --------------------------------------------------

    private final HinduVariant variant;
    private final int kyYear; // year of Kali Yuga (elapsed / expired)
    private final HinduMonth month;
    private final HinduDay dayOfMonth;

    //~ Konstruktoren -----------------------------------------------------

    HinduCalendar(
        HinduVariant variant,
        int kyYear,
        HinduMonth month,
        HinduDay dayOfMonth
    ) {
        super();

        if (variant == null) {
            throw new NullPointerException("Missing variant.");
        } else if (month == null) {
            throw new NullPointerException("Missing month.");
        } else if (dayOfMonth == null) {
            throw new NullPointerException("Missing day of month.");
        } else if (kyYear < 0) {
            throw new IllegalArgumentException("Kali yuga year must not be smaller than 0: " + kyYear);
        }

        this.variant = variant;
        this.kyYear = kyYear;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

    }

    //~ Methoden ----------------------------------------------------------

    @Override
    public String getVariant() {
        return this.variant.getVariant();
    }

    /**
     * <p>Obtains the era from the current Hindu variant. </p>
     *
     * <p>If the associated (elapsed) year becomes negative then the method will fall back to Kali Yuga era. </p>
     *
     * @return  HinduEra
     * @see     HinduVariant#getDefaultEra()
     */
    /*[deutsch]
     * <p>Liefert die Standard&auml;ra der aktuellen Hindu-Kalendervariante. </p>
     *
     * <p>Wenn das zugeordnete (abgelaufene) Jahr negativ werden sollte, wird die Methode
     * die &Auml;ra Kali Yuga liefern. </p>
     *
     * @return  HinduEra
     * @see     HinduVariant#getDefaultEra()
     */
    public HinduEra getEra() {
        HinduEra era = this.variant.getDefaultEra();
        if (era.yearOfEra(HinduEra.KALI_YUGA, this.kyYear) < 0) {
            era = HinduEra.KALI_YUGA;
        }
        return era;
    }

    /**
     * <p>Obtains the year according to the current era and according to if the current Hindu variant
     * uses elapsed years or current years. </p>
     *
     * @return  int
     * @see     #getEra()
     * @see     HinduEra#yearOfEra(HinduEra, int)
     * @see     HinduVariant#isUsingElapsedYears()
     */
    /*[deutsch]
     * <p>Liefert das Jahr passend zur aktuellen &Auml;ra und passend dazu, ob die
     * aktuelle Hindu-Kalendervariante abgelaufene oder laufende Jahre z&auml;hlt. </p>
     *
     * @return  int
     * @see     #getEra()
     * @see     HinduEra#yearOfEra(HinduEra, int)
     * @see     HinduVariant#isUsingElapsedYears()
     */
    public int getYear() {
        int y = this.getEra().yearOfEra(HinduEra.KALI_YUGA, this.kyYear);
        if (!this.variant.isUsingElapsedYears()) {
            y++;
        }
        return y;
    }

    /**
     * <p>Obtains the month. </p>
     *
     * @return  HinduMonth
     */
    /*[deutsch]
     * <p>Liefert den Monat. </p>
     *
     * @return  HinduMonth
     */
    public HinduMonth getMonth() {
        return this.month;
    }

    /**
     * <p>Obtains the day of month. </p>
     *
     * @return  HinduDay
     */
    /*[deutsch]
     * <p>Liefert den Tag des Monats. </p>
     *
     * @return  HinduDay
     */
    public HinduDay getDayOfMonth() {
        return this.dayOfMonth;
    }

    /**
     * <p>Determines the day of week. </p>
     *
     * @return  Weekday
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * @return  Weekday
     */
    public Weekday getDayOfWeek() {
        long utcDays = this.getCalendarSystem().transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HinduCalendar) {
            HinduCalendar that = (HinduCalendar) obj;
            return (
                this.variant.equals(that.variant)
                    && (this.kyYear == that.kyYear)
                    && this.month.equals(that.month)
                    && this.dayOfMonth.equals(that.dayOfMonth)
            );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (
            7 * this.variant.hashCode()
                + 17 * this.kyYear
                + 31 * this.month.hashCode()
                + 37 * this.dayOfMonth.hashCode()
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[hindu-");
        sb.append(this.variant);
        sb.append(",kali-yuga-year=");
        sb.append(this.kyYear);
        sb.append(",month=");
        sb.append(this.month);
        sb.append(",day-of-month=");
        sb.append(this.dayOfMonth);
        sb.append(']');
        return sb.toString();
    }

    /**
     * <p>Obtains the standard week model of this calendar. </p>
     *
     * @return  Weekmodel
     */
    /*[deutsch]
     * <p>Ermittelt das Standardwochenmodell dieses Kalenders. </p>
     *
     * @return  Weekmodel
     */
    public static Weekmodel getDefaultWeekmodel() {

        return IndianCalendar.getDefaultWeekmodel();

    }

    /**
     * <p>Returns the associated calendar family. </p>
     *
     * @return  chronology as calendar family
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Kalenderfamilie. </p>
     *
     * @return  chronology as calendar family
     */
    public static CalendarFamily<HinduCalendar> family() {
        return ENGINE;
    }

    @Override
    protected CalendarFamily<HinduCalendar> getChronology() {
        return ENGINE;
    }

    @Override
    protected HinduCalendar getContext() {
        return this;
    }

    @Override
    protected CalendarSystem<HinduCalendar> getCalendarSystem() {
        return this.variant.getCalendarSystem();
    }

    int getExpiredYearOfKaliYuga() {
        return this.kyYear;
    }

    //~ Innere Klassen ----------------------------------------------------

    private static class VariantMap
        extends ConcurrentHashMap<String, CalendarSystem<HinduCalendar>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public CalendarSystem<HinduCalendar> get(Object key) {

            CalendarSystem<HinduCalendar> calsys = super.get(key);

            if (calsys == null) {
                String variant = key.toString();
                calsys = HinduVariant.from(variant).getCalendarSystem();
                CalendarSystem<HinduCalendar> old = this.putIfAbsent(variant, calsys);

                if (old != null) {
                    calsys = old;
                }
            }

            return calsys;

        }

    }

}
