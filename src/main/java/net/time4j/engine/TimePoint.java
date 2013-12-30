/*
 * -----------------------------------------------------------------------
 * Copyright © 2013 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimePoint.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.engine;

import java.io.Serializable;
import net.time4j.base.MathUtils;


/**
 * <p>Repr&auml;sentiert einen unver&auml;nderlichen Zeitpunkt auf einer in
 * die Zukunft gerichteten Zeitachse. </p>
 *
 * <h4>Chronologische Elementwerte anzeigen und &auml;ndern</h4>
 *
 * <p>Der Zeitwert setzt sich aus chronologischen Elementen zusammen. Diese
 * abstrakte Basisklasse delegiert die Zeitrechnung immer an die zugeh&ouml;rige
 * Zeitachse bzw. genauer an die ihr zugeordneten Regeln der Elemente und
 * Zeiteinheiten, mu&szlig; aber in den {@code ChronoEntity}-Methoden selbst
 * den Zustand definieren und auch das Serialisierungsverhalten festlegen. </p>
 *
 * <p>Da alle konkreten Implementierungen <i>immutable</i> sind und sein
 * m&uuml;ssen, sind Elementwerte nur dadurch &auml;nderbar, da&szlig; jeweils
 * eine neue Instanz mit ge&auml;nderten Elementwerten erzeugt wird. Das wird
 * unter anderem von allen {@code with()}-Methoden geleistet. </p>
 *
 * <h4>Zeitstrahl</h4>
 *
 * <p>Ist die referenzierte Zeitachse die UTC-Weltzeitlinie, wenn also
 * ein Zeitpunkt dann relativ zum Beginn der UTC-Epoche liegt (siehe
 * <a href="package-summary.html">Paketbeschreibung</a>), dann mu&szlig; eine
 * Implementierung auch das Interface {@link net.time4j.scale.UniversalTime}
 * implementieren. Sonst liegt eine lokale Zeitachse vor. Alle Zeiteinheiten
 * sind bez&uuml;glich der Zeitachse zu interpretieren. Zum Beispiel gelten
 * Sekundenzeiteinheiten auf einem lokalen Zeitstempel als lokale UT1-Sekunden
 * mit externem Zeitzonenkontext, auf einem {@code UniversalTime}-Objekt
 * vor 1972 als globale UT1-Sekunden und nach 1972 als atomare SI-Sekunden.
 * Deshalb sollten Anwendungen besondere Vorsicht walten lassen, wenn eine
 * Dauer von einer Zeitachse auf eine andere Zeitachse &uuml;bertragen wird.
 * Eine geeignete Konversionsmethode kann z.B. die Klasse {@code TimeScale}
 * bieten. </p>
 *
 * <h4>Sortierung</h4>
 *
 * <p>Wenn nicht ausdr&uuml;cklich anders dokumentiert, wird die Sortierung
 * von Zeitpunkten immer rein zeitlich definiert und konsistent mit
 * {@code equals()} sein. Im Zweifelsfall ist die Dokumentation der
 * konkreten Subklasse ma&szlig;geblich. Alternativ k&ouml;nnen Subklassen
 * auch das Interface {@code Temporal} implementieren, um eine rein
 * zeitliche Ordnung zu erm&ouml;glichen. </p>
 *
 * <h4>Addition (oder Subtraktion) einer Zeitspanne zu einem Zeitpunkt</h4>
 *
 * <p>Diese Operationen werden von allen {@code plus()}- und {@code minus()}-
 * Methoden geleistet. Eine Zeitspanne kann entweder nur eine einzelne
 * Zeiteinheit sein oder ist aus mehreren Zeiteinheiten zusammengesetzt. </p>
 *
 * <p>Wenn die angegebene Zeiteinheit bzw. Genauigkeit keine konstante
 * L&auml;nge definiert (zum Beispiel haben Monate in Tagen ausgedr&uuml;ckt
 * im allgemeinen unterschiedliche L&auml;ngen), dann kann das Ergebnis
 * einer Addition im Hinblick auf den erwarteten Elementwert {@code amount}
 * abweichen, siehe Regel 3. Bei mehrfachen Additionen hintereinander ist
 * Vorsicht angebracht, im Zweifelsfall sollte der Originalwert f&uuml;r
 * eine sp&auml;tere Addition gespeichert werden. Beispiel mit Additionen
 * von Monaten in einem oder in zwei Schritten (Pseudo-Code): </p>
 *
 * <ul>
 *  <li>[2011-08-31] + [P1M] = [2011-09-30]</li>
 *  <li>[2011-09-30] + [P1M] = [2011-10-30]</li>
 *  <li>[2011-08-31] + [P2M] = [2011-10-31]</li>
 * </ul>
 *
 * <h4>Differenz von Zeitpunkten</h4>
 *
 * <p>Die Differenz von Zeitpunkten resultiert jeweils in einer Zeitspanne.
 * Das Ergebnis kann entweder in nur einer Zeiteinheit ausgedr&uuml;ckt werden,
 * oder in mehreren Zeiteinheiten, die jede die Basiseinheit des entsprechenden
 * chronologischen Elements repr&auml;sentieren. In letzterem Fall ist auch
 * eine Metrik anzugeben. </p>
 *
 * <h4>Implementierungshinweise</h4>
 *
 * <ul>
 *  <li>Alle Subklassen m&uuml;ssen <i>final</i> und <i>immutable</i>
 *  sein. </li>
 *  <li>Es mu&szlig; dokumentiert werden, welche chronologischen Elemente
 *  unterst&uuml;tzt werden bzw. registriert sind. </li>
 *  <li>Zur Wahl des Zeiteinheitstyps U ist zu beachten, da&szlig; die
 *  Zeiteinheiten mit dem internen Zeitwertzustand zusammenpassen m&uuml;ssen,
 *  weil sonst die Berechnung der Zeitspanne zwischen zwei Zeitpunkten nicht
 *  vollst&auml;ndig m&ouml;glich sein kann. Zum Beispiel w&auml;re es ein
 *  Fehler, wenn die Zeiteinheiten maximal in Millisekunden genau sind, aber
 *  die konkreten Zeitpunkte sogar Nanosekunden enthalten. </li>
 *  <li>Die nat&uuml;rliche Ordnung sollte konsistent mit {@code equals()}
 *  sein. </li>
 * </ul>
 *
 * @param   <U> Typ der Zeiteinheiten (kompatibel zu {@link ChronoUnit})
 * @param   <T> reflexiver Selbstbezug
 * @author  Meno Hochschild
 * @see     Chronology
 * @see     TimeAxis
 * @see     Temporal
 */
public abstract class TimePoint<U, T extends TimePoint<U, T>>
    extends ChronoEntity<T>
    implements Comparable<T>, Serializable {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Vergleicht zwei Zeitpunkte bevorzugt nach ihrer Position auf der
     * gemeinsamen Zeitachse. </p>
     *
     * <p>Implementierungshinweis: Damit die nat&uuml;rliche Ordnung konsistent
     * mit {@code equals()} ist, m&uuml;ssen zum Vergleich alle internen
     * Zustandsattribute herangezogen werden, bevorzugt aber die Attribute,
     * die die zeitliche Position festlegen. </p>
     *
     * @param   timePoint   Vergleichszeitpunkt
     * @return  negativ, {@code 0} oder positiv, wenn dieser Zeitpunkt
     *          vor, gleich oder nach dem angegebenen Zeitpunkt liegt
     * @see     #equals(Object)
     */
    @Override
    public abstract int compareTo(T timePoint);

    /**
     * <p>Addiert die angegebene Zeitspanne zur Bezugszeit und liefert das
     * Additionsergebnis zur&uuml;ck. </p>
     *
     * <p>Delegiert an {@link TimeSpan#addTo(TimePoint)}. </p>
     *
     * @param   timeSpan    zu addierende Zeitspanne
     * @return  Additionsergebnis
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     #minus(TimeSpan)
     */
    public T plus(TimeSpan<? extends U> timeSpan) {

        return timeSpan.addTo(this.getContext());

    }

    /**
     * <p>Subtrahiert die angegebene Zeitspanne von der Bezugszeit und
     * liefert das Ergebnis zur&uuml;ck. </p>
     *
     * <p>Delegiert an {@link TimeSpan#subtractFrom(TimePoint)}. </p>
     *
     * @param   timeSpan    zu subtrahierende Zeitspanne
     * @return  Subtraktionsergebnis
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @see     #plus(TimeSpan)
     */
    public T minus(TimeSpan<? extends U> timeSpan) {

        return timeSpan.subtractFrom(this.getContext());

    }

    /**
     * <p>Addiert den angegebenen Betrag der entsprechenden Zeiteinheit
     * zu dieser Bezugszeit und liefert das Additionsergebnis zur&uuml;ck. </p>
     *
     * <p>&Auml;hnlich wie {@link #plus(TimeSpan)}, aber mit dem Unterschied,
     * da&szlig; die Zeitspanne in nur einer Zeiteinheit angegeben wird.
     * Beispiel in Pseudo-Code: </p>
     *
     * <ul>
     *  <li>[2011-05-31].plus(1, &lt;MONTHS&gt;) = [2011-06-30]</li>
     *  <li>[2011-05-31].plus(4, &lt;DAYS&gt;) = [2011-06-04]</li>
     *  <li>[2011-06-04].plus(-4, &lt;DAYS&gt;) = [2011-05-31]</li>
     *  <li>[2010-04-29].plus(397, &lt;DAYS&gt;) = [2011-05-31]</li>
     *  <li>[2010-04-29].plus(13, &lt;MONTHS&gt;) = [2011-05-29]</li>
     *  <li>[2010-04-29].plus(-2, &lt;MONTHS&gt;) = [2010-02-28]</li>
     *  <li>[2010-04-29].plus(1, &lt;YEARS&gt;) = [2011-04-29]</li>
     * </ul>
     *
     * @param   amount      zu addierender Betrag (darf negativ sein)
     * @param   unit        Zeiteinheit
     * @return  Additionsergebnis
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @throws  RuleNotFoundException wenn die Zeiteinheit nicht registriert
     *          ist und auch nicht {@link UnitRule.Source} implementiert
     * @see     #plus(TimeSpan)
     */
    public T plus(
        long amount,
        U unit
    ) {

        if (amount == 0) {
            return this.getContext();
        }

        return this.getRule(unit).addTo(this.getContext(), amount);

    }

    /**
     * <p>Subtrahiert den angegebenen Betrag der entsprechenden Zeiteinheit
     * von dieser Bezugszeit und liefert das Ergebnis zur&uuml;ck. </p>
     *
     * @param   amount      zu subtrahierender Betrag (darf negativ sein)
     * @param   unit        Zeiteinheit
     * @return  Subtraktionsergebnis
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @throws  RuleNotFoundException wenn die Zeiteinheit nicht registriert
     *          ist und auch nicht {@link UnitRule.Source} implementiert
     * @see     #plus(long, Object) plus(long, U)
     */
    public T minus(
        long amount,
        U unit
    ) {

        return this.plus(MathUtils.safeNegate(amount), unit);

    }

    /**
     * <p>Ermittelt die normalisierte Zeitspanne zwischen dieser Bezugszeit und
     * dem Endzeitpunkt in der angegebenen Metrik. </p>
     *
     * @param   end             Endzeitpunkt, zu dem die Differenz zu
     *                          ermitteln ist
     * @param   metric          Abstandsmetrik
     * @return  Zeitdifferenz als Zeitspanne
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     */
    public <P extends TimeSpan<?>> P until(
        T end,
        TimeMetric<? extends U, P> metric
    ) {

        return metric.between(this.getContext(), end);

    }

    /**
     * <p>Ermittelt den zeitlichen Abstand zwischen dieser Bezugszeit und dem
     * angegebenen Zeitpunkt nur in einer Zeiteinheit zur&uuml;ck. </p>
     *
     * <p>&Auml;hnlich wie {@link #until(TimePoint, TimeMetric)}, aber mit
     * dem Unterschied, da&szlig; die Zeitspanne nur in einer Zeiteinheit als
     * long-Primitive ermittelt wird. Es wird meist ein Subtraktionsrest
     * verbleiben, wenn es sich nicht um die kleinstm&ouml;gliche bzw. genaueste
     * Zeiteinheit handelt. Zeitpunkte, deren Elementwerte sich um weniger als
     * eine Einheit unterscheiden, gelten als gleich. Beispiele: </p>
     *
     * <ul>
     *  <li>[2011-05-31].until([2011-06-04], &lt;MONTHS&gt;) = 0</li>
     *  <li>[2011-05-31].until([2011-06-04], &lt;DAYS&gt;) = 4</li>
     *  <li>[2011-06-04].until([2011-05-31], &lt;DAYS&gt;) = -4</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;DAYS&gt;) = 397</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;MONTHS&gt;) = 13</li>
     *  <li>[2010-04-29].until([2011-05-31], &lt;YEARS&gt;) = 1</li>
     *  <li>[2010-05-31].until([2011-05-31], &lt;YEARS&gt;) = 1</li>
     *  <li>[2010-06-01].until([2011-05-31], &lt;YEARS&gt;) = 0</li>
     * </ul>
     *
     * @param   end     Endzeitpunkt, zu dem die Differenz zu ermitteln ist
     * @param   unit    Zeiteinheit, in der die Differenz zu messen ist
     * @return  Subtraktionsergebnis als Anzahl der angegebenen Zeiteinheit
     * @throws  ArithmeticException in &Uuml;berlaufsituationen
     * @throws  RuleNotFoundException wenn die Zeiteinheit nicht registriert
     *          ist und auch nicht {@link UnitRule.Source} implementiert
     * @see     #until(TimePoint, TimeMetric)
     */
    public long until(
        T end,
        U unit
    ) {

        return this.getRule(unit).between(this.getContext(), end);

    }

    /**
     * <p>Vergleicht den gesamten Zustand dieser Instanz mit dem des
     * angegebenen Objekts. </p>
     *
     * <p>Implementierungen werden &uuml;blicherweise ihren Zustand nur
     * auf Basis der zeitlichen Position definieren, da dies am ehesten
     * der Erwartungshaltung der Anwender entspricht. Ausnahmen sind
     * explizit zu dokumentieren und zu begr&uuml;nden. </p>
     *
     * @param   obj     Vergleichsobjekt
     * @return  Vergleichsergebnis
     * @see     #compareTo(TimePoint)
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * <p>Subklassen m&uuml;ssen diese Methode passend zum Verhalten
     * von {@code equals()} redefinieren. </p>
     *
     * @return  int
     */
    @Override
    public abstract int hashCode();

    /**
     * <p>Liefert eine vollst&auml;ndige Beschreibung des Zustands dieses
     * Zeitpunkts. </p>
     *
     * <p>Die textuelle Beschreibung folgt oft den Konventionen des
     * ISO-8601-Standards, indem zuerst die groben und dann die feineren
     * chronologischen Informationen mit h&ouml;herer Detailgenauigkeit folgen
     * (zum Beispiel die ISO-Notation YYYY-MM-DD). </p>
     *
     * @return  String
     */
    @Override
    public abstract String toString();

    /**
     * <p>Liefert die zugeh&ouml;rige Zeitachse, die alle notwendigen
     * chronologischen Regeln enth&auml;lt. </p>
     *
     * <p>Konkrete Subklassen m&uuml;ssen in einem <i>static initializer</i>
     * mit Hilfe von {@code TimeAxis.Builder} eine Zeitachse bauen, in
     * einer eigenen Konstanten halten und hier verf&uuml;gbar machen.
     * &Uuml;ber dieses Verfahren wird zugleich ein Basissatz von Elementen,
     * Zeiteinheiten und chronologischen Regeln vorinstalliert. Au&szlig;erdem
     * ist so eine 1:1-Relation zwischen einer {@code TimePoint}-Klasse und
     * der entsprechenden Zeitachse respektive Chronologie garantiert. </p>
     *
     * @return  chronologisches System als Zeitachse (immer vorhanden)
     * @see     TimeAxis.Builder
     */
    @Override
    public abstract TimeAxis<U, T> getChronology();

    /**
     * <p>Rollt das angegebene chronologische Element um eine Anzahl von
     * Werten innerhalb seines Wertbereichs zwischen Minimum und Maximum
     * (Odometer-Prinzip). </p>
     *
     * <p>&Auml;hnlich wie {@code plus(amount, unit)}, aber mit dem Unterschied,
     * da&szlig; erstens die Methode auf chronologischen Elementen statt
     * Zeiteinheiten definiert ist, zweitens mit gr&ouml;&szlig;eren
     * Zeiteinheiten verkn&uuml;pfte Elemente nicht beeinflusst werden
     * und drittens das Rollverhalten im Gegensatz zur Addition von den
     * Elementwertgrenzen abh&auml;ngt. Beispiele in Pseudo-Code: </p>
     *
     * <ul>
     *  <li>[2011-05-31].roll(&lt;MONTH_OF_YEAR&gt;, 1) = [2011-06-30]</li>
     *  <li>[2011-05-31].roll(&lt;DAY_OF_MONTH&gt;, 4) = [2011-05-04]</li>
     *  <li>[2011-06-04].roll(&lt;DAY_OF_MONTH&gt;, -4) = [2011-06-30]</li>
     *  <li>[2011-12-31].roll(&lt;DAY_OF_YEAR&gt;, 2) = [2011-01-02]</li>
     * </ul>
     *
     * @param   element     chronologisches Element mit einer definierten
     *                      Basiseinheit
     * @param   amount      Anzahl der zu addierenden Basiszeiteinheiten
     * @return  Rollergebnis
     * @throws  ChronoException wenn das angegebene Element keine
     *          definierte Basiseinheit in dieser Chronologie hat
     * @see     #plus(long, Object) plus(long, U)
     * @see     #minus(long, Object) minus(long, U)
     * @see     TimeAxis#getBaseUnit(ChronoElement)
     */
    public T roll(
        ChronoElement<?> element,
        long amount
    ) {

        if (element == null) {
            throw new NullPointerException("Missing chronological element.");
        }

        String name = element.name();

        if ( // Sonderfall
            name.equals("CLOCK_HOUR_OF_AMPM")
            || name.equals("CLOCK_HOUR_OF_DAY")
        ) {
            String test = name.substring(6);

            for (
                ChronoElement<?> e
                : this.getChronology().getRegisteredElements()
            ) {
                if (e.name().equals(test)) {
                    element = e;
                    break;
                }
            }
        }

        T context = this.getContext();
        U unit = this.getChronology().getBaseUnit(element);

        if (unit == null) {
            throw new ChronoException(
                "Base unit not defined for: " + element.name());
        } else if (amount == 0) {
            return context;
        }

        long value = amount;
        UnitRule<T> rule = this.getRule(unit);
        T min = this.withBounds(element, false);
        T max = this.withBounds(element, true);

        // Sonderfall zeitlich absteigende Reihenfolge (z.B. year-of-era)
        if (min.compareTo(max) > 0) {
            T temp = min;
            min = max;
            max = temp;
        }

        if (value < 0) {
            long diff = rule.between(min, context);
            if (value + diff < 0) {
                context = max;
                value = (value + diff + 1);
                if (value != 0) {
                    long size = rule.between(min, max) + 1;
                    value = (value % size);
                    if (value == 1 - size) {
                        return min;
                    }
                }
            } else if (value + diff == 0) {
                return min;
            }
        } else {
            long diff = rule.between(context, max);
            if (value > diff) {
                context = min;
                value = (value - diff - 1);
                if (value != 0) {
                    long size = rule.between(min, max) + 1;
                    value = (value % size);
                    if (value == size - 1) {
                        return max;
                    }
                }
            } else if (value == diff) {
                return max;
            }
        }

        return (
            (value == 0)
            ? context
            : rule.addTo(context, value)
        );

    }

    private <V> T withBounds(
        ChronoElement<V> element,
        boolean up
    ) {

        ElementRule<T, V> rule = this.getChronology().getRule(element);
        T context = this.getContext();

        if (up) {
            return rule.withValue(
                context,
                rule.getMaximum(context),
                element.isLenient());
        } else {
            return rule.withValue(
                context,
                rule.getMinimum(context),
                element.isLenient());
        }

    }

    // Einheitsregel
    private UnitRule<T> getRule(U unit) {

        if (unit == null) {
            throw new NullPointerException("Missing chronological unit.");
        }

        TimeAxis<U, T> axis = this.getChronology();

        if (axis.isRegistered(unit)) {
            return axis.getRule(unit);
        } else if (unit instanceof UnitRule.Source) {
            UnitRule<T> rule = UnitRule.Source.class.cast(unit).derive(axis);
            if (rule != null) {
                return rule;
            }
        }

        throw new RuleNotFoundException(axis, unit);

    }

}
