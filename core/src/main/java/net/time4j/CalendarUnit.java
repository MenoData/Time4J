/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (CalendarUnit.java) is part of project Time4J.
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

package net.time4j;

import net.time4j.engine.ChronoEntity;
import net.time4j.engine.TimePoint;
import net.time4j.engine.UnitRule;


/**
 * <p>Represents the most common time units related to a standard
 * ISO-8601-calendar. </p>
 *
 * <p><strong>Default behaviour of addition or subtraction of month-related
 * units: </strong></p>
 *
 * <p>If the addition of months results in an invalid intermediate date
 * then the final date will be just the last valid date that is the last
 * day of current month. Example: </p>
 *
 * <pre>
 *  PlainDate date = PlainDate.of(2013, 1, 31);
 *  System.out.println(date.plus(1, MONTHS));
 *  // Output: 2013-02-28
 * </pre>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Repr&auml;sentiert die am meisten gebr&auml;uchlichen Zeiteinheiten
 * bezogen auf einen ISO-8601-konformen Kalender. </p>
 *
 * <p><strong>Standardverhalten der Addition oder Subtraktion von
 * monatsbezogenen Einheiten: </strong></p>
 *
 * <p>Wenn die Addition von Monaten zu einem ung&uuml;ltigen Datum
 * f&uuml;hrt, dann wird das zuletzt g&uuml;ltige Datum bestimmt, also der
 * letzte Tag des aktuellen Monats. Beispiel: </p>
 *
 * <pre>
 *  PlainDate date = PlainDate.of(2013, 1, 31);
 *  System.out.println(date.plus(1, MONTHS));
 *  // Ausgabe: 2013-02-28
 * </pre>
 *
 * @author  Meno Hochschild
 */
public enum CalendarUnit
    implements IsoDateUnit {

    //~ Statische Felder/Initialisierungen --------------------------------

    /** Time unit &quot;millennia&quot; (symbol I) */
    /*[deutsch] Zeiteinheit &quot;Jahrtausende&quot; (Symbol I) */
    MILLENNIA() {
        @Override
        public char getSymbol() {
            return 'I';
        }
        @Override
        public double getLength() {
            return 31556952000.0; // 1000.0 * 365.2425 * 86400.0
        }
    },

    /** Time unit &quot;centuries&quot; (symbol C) */
    /*[deutsch] Zeiteinheit &quot;Jahrhunderte&quot; (Symbol C) */
    CENTURIES() {
        @Override
        public char getSymbol() {
            return 'C';
        }
        @Override
        public double getLength() {
            return 3155695200.0; // 100.0 * 365.2425 * 86400.0
        }
    },

    /** Time unit &quot;decades&quot; (symbol E) */
    /*[deutsch] Zeiteinheit &quot;Jahrzehnte&quot; (Symbol E) */
    DECADES() {
        @Override
        public char getSymbol() {
            return 'E';
        }
        @Override
        public double getLength() {
            return 315569520.0; // 10.0 * 365.2425 * 86400.0
        }
    },

    /** Time unit &quot;calendar years&quot; (symbol Y) */
    /*[deutsch] Zeiteinheit &quot;Jahre&quot; (Symbol Y) */
    YEARS() {
        @Override
        public char getSymbol() {
            return 'Y';
        }
        @Override
        public double getLength() {
            return 31556952.0; // 365.2425 * 86400.0
        }
    },

    /** Time unit &quot;quarter years&quot; (symbol Q) */
    /*[deutsch] Zeiteinheit &quot;Quartale&quot; (Symbol Q) */
    QUARTERS() {
        @Override
        public char getSymbol() {
            return 'Q';
        }
        @Override
        public double getLength() {
            return 7889238.0; // 365.2425 * 86400.0 / 4.0
        }
    },

    /** Time unit &quot;months&quot; (symbol M) */
    /*[deutsch] Zeiteinheit &quot;Monate&quot; (Symbol M) */
    MONTHS() {
        @Override
        public char getSymbol() {
            return 'M';
        }
        @Override
        public double getLength() {
            return 2629746.0; // 365.2425 * 86400.0 / 12.0
        }
    },

    /** Time unit &quot;weeks&quot; (symbol W) */
    /*[deutsch] Zeiteinheit &quot;Wochen&quot; (Symbol W) */
    WEEKS() {
        @Override
        public char getSymbol() {
            return 'W';
        }
        @Override
        public double getLength() {
            return 604800.0; // 86400.0 * 7
        }
    },

    /** Time unit &quot;days&quot; (symbol D) */
    /*[deutsch] Zeiteinheit &quot;Tage&quot; (Symbol D) */
    DAYS() {
        @Override
        public char getSymbol() {
            return 'D';
        }
        @Override
        public double getLength() {
            return 86400.0;
        }
    };

    //~ Instanzvariablen --------------------------------------------------

    private final IsoDateUnit eof =
        new OverflowUnit(this, OverflowUnit.POLICY_END_OF_MONTH);
    private final IsoDateUnit kld =
        new OverflowUnit(this, OverflowUnit.POLICY_KEEPING_LAST_DATE);
    private final IsoDateUnit ui =
        new OverflowUnit(this, OverflowUnit.POLICY_UNLESS_INVALID);
    private final IsoDateUnit nvd =
        new OverflowUnit(this, OverflowUnit.POLICY_NEXT_VALID_DATE);
    private final IsoDateUnit co =
        new OverflowUnit(this, OverflowUnit.POLICY_CARRY_OVER);

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Calculates the temporal distance between given calendar dates
     * in this calendar unit. </p>
     *
     * @param   <T> generic type of calendar date
     * @param   start   starting date
     * @param   end     ending date
     * @return  duration as count of this unit
     */
    /*[deutsch]
     * <p>Ermittelt den zeitlichen Abstand zwischen den angegebenen
     * Datumsangaben gemessen in dieser Einheit. </p>
     *
     * @param   <T> generic type of calendar date
     * @param   start   starting date
     * @param   end     ending date
     * @return  duration as count of this unit
     */
    public <T extends TimePoint<? super CalendarUnit, T>> long between(
        T start,
        T end
    ) {

        return start.until(end, this);

    }

    /**
     * <p>A calendar unit is always calendrical. </p>
     *
     * @return  {@code true}
     */
    /*[deutsch]
     * <p>Eine Datumseinheit ist immer kalendarisch. </p>
     *
     * @return  {@code true}
     */
    @Override
    public boolean isCalendrical() {

        return true;

    }

    /**
     * <p>Defines a variation of this unit which resolves invalid intermediate
     * dates in additions and subtractions to the first valid date after
     * (the first day of following month). </p>
     *
     * <p>Example for months: </p>
     *
     * <pre>
     *  PlainDate date = PlainDate.of(2013, 1, 31);
     *  System.out.println(date.plus(1, MONTHS.nextValidDate()));
     *  // Output: 2013-03-01
     * </pre>
     *
     * <p>Note: The metric for calculation of temporal distances remains
     * unaffected. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    /*[deutsch]
     * <p>Definiert eine Variante dieser Zeiteinheit, in der bei Additionen
     * und Subtraktionen ung&uuml;ltige Datumswerte zum ersten Tag des
     * Folgemonats aufgel&ouml;st werden. </p>
     *
     * <p>Beispiel f&uuml;r Monate: </p>
     *
     * <pre>
     *  PlainDate date = PlainDate.of(2013, 1, 31);
     *  System.out.println(date.plus(1, MONTHS.nextValidDate()));
     *  // Ausgabe: 2013-03-01
     * </pre>
     *
     * <p>Notiz: Die Metrik zur Berechnung von Zeitabst&auml;nden bleibt
     * unver&auml;ndert erhalten. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    public IsoDateUnit nextValidDate() {

        switch (this) {
            case WEEKS:
            case DAYS:
                return this;
            default:
                return this.nvd;
        }

    }

    /**
     * <p>Defines a variation of this unit which resolves invalid intermediate
     * dates in additions and subtractions by transferring any day overflow
     * to the following month. </p>
     *
     * <p>Example for months: </p>
     *
     * <pre>
     *  PlainDate date = PlainDate.of(2013, 1, 31);
     *  System.out.println(date.plus(1, MONTHS.withCarryOver()));
     *  // Output: 2013-03-03
     * </pre>
     *
     * <p>Note: The metric for calculation of temporal distances remains
     * unaffected. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    /*[deutsch]
     * <p>Definiert eine Variante dieser Zeiteinheit, in der bei Additionen
     * und Subtraktionen ein &Uuml;berlauf auf den Folgemonat &uuml;bertragen
     * wird. </p>
     *
     * <p>Beispiel f&uuml;r Monate: </p>
     *
     * <pre>
     *  PlainDate date = PlainDate.of(2013, 1, 31);
     *  System.out.println(date.plus(1, MONTHS.withCarryOver()));
     *  // Ausgabe: 2013-03-03
     * </pre>
     *
     * <p>Notiz: Die Metrik zur Berechnung von Zeitabst&auml;nden bleibt
     * unver&auml;ndert erhalten. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    public IsoDateUnit withCarryOver() {

        switch (this) {
            case WEEKS:
            case DAYS:
                return this;
            default:
                return this.co;
        }

    }

    /**
     * <p>Defines a variation of this unit which handles invalid
     * intermediate dates in additions and subtractions by throwing
     * a {@code ChronoException}. </p>
     *
     * <p>Example for months: </p>
     *
     * <pre>
     *  PlainDate date = PlainDate.of(2013, 1, 31);
     *  System.out.println(date.plus(1, MONTHS.unlessInvalid()));
     *  // February 31th does not exist =&gt; throws ChronoException
     * </pre>
     *
     * <p>Note: The metric for calculation of temporal distances remains
     * unaffected. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    /*[deutsch]
     * <p>Definiert eine Variante dieser Zeiteinheit, in der bei Additionen
     * und Subtraktionen ung&uuml;ltige Datumswerte nicht korrigiert, sondern
     * mit einer Ausnahme vom Typ {@code ChronoException} quittiert werden. </p>
     *
     * <p>Beispiel f&uuml;r Monate: </p>
     *
     * <pre>
     *  PlainDate date = PlainDate.of(2013, 1, 31);
     *  System.out.println(date.plus(1, MONTHS.unlessInvalid()));
     *  // 31. Februar nicht vorhanden =&gt; throws ChronoException
     * </pre>
     *
     * <p>Notiz: Die Metrik zur Berechnung von Zeitabst&auml;nden bleibt
     * unver&auml;ndert erhalten. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    public IsoDateUnit unlessInvalid() {

        switch (this) {
            case WEEKS:
            case DAYS:
                return this;
            default:
                return this.ui;
        }

    }

    /**
     * <p>Defines a variation of this unit which always sets the resulting
     * date in additions and subtractions to the end of month even if there
     * is no day overflow. </p>
     *
     * <p>Example for months: </p>
     *
     * <pre>
     *  PlainDate date1 = PlainDate.of(2013, 2, 27);
     *  System.out.println(date1.plus(2, MONTHS.atEndOfMonth()));
     *  // Ausgabe: 2013-04-30
     *  PlainDate date2 = PlainDate.of(2013, 2, 28);
     *  System.out.println(date2.plus(2, MONTHS.atEndOfMonth()));
     *  // Ausgabe: 2013-04-30
     * </pre>
     *
     * <p>Note: The metric for calculation of temporal distances remains
     * unaffected. An alternative which only jumps to the end of month
     * if the original date is the last day of month can be achieved
     * by {@link #keepingEndOfMonth()}. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    /*[deutsch]
     * <p>Definiert eine Variante dieser Zeiteinheit, in der bei Additionen
     * und Subtraktionen grunds&auml;tzlich der letzte Tag des Monats gesetzt
     * wird, selbst wenn kein &Uuml;berlauf vorliegt. </p>
     *
     * <p>Beispiel f&uuml;r Monate: </p>
     *
     * <pre>
     *  PlainDate date1 = PlainDate.of(2013, 2, 27);
     *  System.out.println(date1.plus(2, MONTHS.atEndOfMonth()));
     *  // Ausgabe: 2013-04-30
     *  PlainDate date2 = PlainDate.of(2013, 2, 28);
     *  System.out.println(date2.plus(2, MONTHS.atEndOfMonth()));
     *  // Ausgabe: 2013-04-30
     * </pre>
     *
     * <p>Notiz: Die Metrik zur Berechnung von Zeitabst&auml;nden bleibt
     * unver&auml;ndert erhalten. Eine Alternative, die nur dann zum Ende
     * des Monats springt, wenn das aktuelle Datum der letzte Tag des Monats
     * ist, ist mittels {@link #keepingEndOfMonth()} erh&auml;ltlich. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     */
    public IsoDateUnit atEndOfMonth() {

        return this.eof;

    }

    /**
     * <p>Defines a variation of this unit which always sets the resulting
     * date in additions and subtractions to the end of month if the original
     * date is the last day of month. </p>
     *
     * <p>Example for months: </p>
     *
     * <pre>
     *  PlainDate date1 = PlainDate.of(2013, 2, 27);
     *  System.out.println(date1.plus(2, MONTHS.keepingEndOfMonth()));
     *  // Ausgabe: 2013-04-27
     *  PlainDate date2 = PlainDate.of(2013, 2, 28);
     *  System.out.println(date2.plus(2, MONTHS.keepingEndOfMonth()));
     *  // Ausgabe: 2013-04-30
     * </pre>
     *
     * <p>Note: The metric for calculation of temporal distances remains
     * unaffected. An alternative which unconditionally jumps to the end
     * of month can be achieved by {@link #atEndOfMonth()}. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     * @since   2.3
     */
    /*[deutsch]
     * <p>Definiert eine Variante dieser Zeiteinheit, in der bei Additionen
     * und Subtraktionen grunds&auml;tzlich der letzte Tag des Monats gesetzt
     * wird, wenn das Ausgangsdatum bereits der letzte Tag des Monats ist. </p>
     *
     * <p>Beispiel f&uuml;r Monate: </p>
     *
     * <pre>
     *  PlainDate date1 = PlainDate.of(2013, 2, 27);
     *  System.out.println(date1.plus(2, MONTHS.keepingEndOfMonth()));
     *  // Ausgabe: 2013-04-27
     *  PlainDate date2 = PlainDate.of(2013, 2, 28);
     *  System.out.println(date2.plus(2, MONTHS.keepingEndOfMonth()));
     *  // Ausgabe: 2013-04-30
     * </pre>
     *
     * <p>Notiz: Die Metrik zur Berechnung von Zeitabst&auml;nden bleibt
     * unver&auml;ndert erhalten. Eine Alternative, die bedingungslos zum
     * Ende des Monats springt, ist mittels {@link #atEndOfMonth()}
     * erh&auml;ltlich. </p>
     *
     * @return  calendar unit with modified addition behaviour, but still
     *          the same metric
     * @since   2.3
     */
    public IsoDateUnit keepingEndOfMonth() {

        return this.kld;

    }

    /**
     * <p>Defines a special calendar unit for week-based years which are
     * not bound to the calendar year but to the week cycle of a year
     * preserving the day of week and (if possible) the week of year. </p>
     *
     * <p>Note: If the week of year is originally 53, but there is no such
     * value after addition or subtraction then the week of year will be
     * reduced to value 52. </p>
     *
     * <pre>
     *  PlainDate start = PlainDate.of(2000, 2, 29); // 2000-W09-2
     *  System.out.println(start.plus(14, CalendarUnit.weekBasedYears()));
     *  // Output: 2014-02-25 (= 2014-W09-2)
     * </pre>
     *
     * @return  calendar unit for week-based years
     */
    /*[deutsch]
     * <p>Definiert eine spezielle Zeiteinheit f&uuml;r wochenbasierte Jahre,
     * die an den Wochen-Zyklus gebunden sind. </p>
     *
     * <p>Notiz: Wenn die Kalenderwoche urspr&uuml;nglich den Wert 53 hat,
     * aber nach einer Addition oder Subtraktion dieser Wert nicht m&ouml;glich
     * ist, dann wird die Kalenderwoche auf den Wert 52 reduziert. </p>
     *
     * <pre>
     *  PlainDate start = PlainDate.of(2000, 2, 29); // 2000-W09-2
     *  System.out.println(start.plus(14, CalendarUnit.weekBasedYears()));
     *  // Ausgabe: 2014-02-25 (= 2014-W09-2)
     * </pre>
     *
     * @return  calendar unit for week-based years
     */
    public static IsoDateUnit weekBasedYears() {

        return YOWElement.YOWUnit.WEEK_BASED_YEARS;

    }

    //~ Innere Klassen ----------------------------------------------------

    /**
     * <p>Realisiert die Zeitarithmetik f&uuml;r eine kalendarische
     * Zeiteinheit. </p>
     *
     * @param   <T> generic type of calendrical context
     */
    static class Rule<T extends ChronoEntity<T>>
        implements UnitRule<T> {

        //~ Instanzvariablen ----------------------------------------------

        private final CalendarUnit unit;
        private final int policy;

        //~ Konstruktoren -------------------------------------------------

        /**
         * <p>Constructs a new rule for a calendar unit using the policy
         * {@code OverflowUnit.POLICY_PREVIOUS_VALID_DATE}. </p>
         *
         * @param   unit    calendar unit
         */
        Rule(CalendarUnit unit) {
            this(unit, OverflowUnit.POLICY_PREVIOUS_VALID_DATE);

        }

        /**
         * <p>Constructs a new rule for a calendar unit using the given
         * policy. </p>
         *
         * @param   unit    calendar unit as delegate
         * @param   policy  strategy for handling day overflow
         */
        Rule(
            CalendarUnit unit,
            int policy
        ) {
            super();

            this.unit = unit;
            this.policy = policy;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public T addTo(
            T context,
            long amount
        ) {

            PlainDate date = context.get(PlainDate.CALENDAR_DATE);
            date = PlainDate.doAdd(this.unit, date, amount, this.policy);
            return context.with(PlainDate.CALENDAR_DATE, date);

        }

        @Override
        public long between(T start, T end) {

            PlainDate d1 = start.get(PlainDate.CALENDAR_DATE);
            PlainDate d2 = end.get(PlainDate.CALENDAR_DATE);
            long amount;

            switch (this.unit) {
                case MILLENNIA:
                    amount = monthDelta(d1, d2) / 12000;
                    break;
                case CENTURIES:
                    amount = monthDelta(d1, d2) / 1200;
                    break;
                case DECADES:
                    amount = monthDelta(d1, d2) / 120;
                    break;
                case YEARS:
                    amount = monthDelta(d1, d2) / 12;
                    break;
                case QUARTERS:
                    amount = monthDelta(d1, d2) / 3;
                    break;
                case MONTHS:
                    amount = monthDelta(d1, d2);
                    break;
                case WEEKS:
                    amount = dayDelta(d1, d2) / 7;
                    break;
                case DAYS:
                    amount = dayDelta(d1, d2);
                    break;
                default:
                    throw new UnsupportedOperationException(this.unit.name());
            }

            if (
                (amount != 0)
                && start.contains(PlainTime.WALL_TIME)
                && end.contains(PlainTime.WALL_TIME)
            ) {

                PlainTime t1 = start.get(PlainTime.WALL_TIME);
                PlainTime t2 = end.get(PlainTime.WALL_TIME);

                if ((amount > 0) && t1.isAfter(t2)) {
                    amount--;
                } else if ((amount < 0) && t1.isBefore(t2)) {
                    amount++;
                }

            }

            return amount;

        }

        private static long monthDelta(
            PlainDate start,
            PlainDate end
        ) {

            long result = (end.getEpochMonths() - start.getEpochMonths());

            if (
                (result > 0)
                && (end.getDayOfMonth() < start.getDayOfMonth())
            ) {
                result--;
            } else if (
                (result < 0)
                && (end.getDayOfMonth() > start.getDayOfMonth())
            ) {
                result++;
            }

            return result;

        }

        private static long dayDelta(
            PlainDate start,
            PlainDate end
        ) {

            if (start.getYear() == end.getYear()) {
                return end.getDayOfYear() - start.getDayOfYear();
            }

            return end.getDaysSinceUTC() - start.getDaysSinceUTC();

        }

    }

}
