/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (UniversalTime.java) is part of project Time4J.
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

package net.time4j.scale;

import net.time4j.base.UnixTime;



/**
 * <p>Definiert eine Koordinate auf der Weltzeitlinie mit Hilfe der Anzahl der
 * Sekunden relativ zur UTC-Epoche [1972-01-01T00:00:00Z] in der UTC-Zeitzone
 * (Greenwich-Meridian) und einem in der letzten Sekunde enthaltenen
 * Nanosekundenbruchteil. </p>
 *
 * <p>Die Abk&uuml;rzung &quot;UTC&quot; steht f&uuml;r die Bezeichnung
 * &quot;Corrected Universal Time&quot;. Der Begriff bezieht sich auf die
 * Zeitzone UTC+00:00, bez&uuml;glich der alle Zeitangaben zu verstehen sind.
 * <strong>Dieses API verwendet als Ma&szlig;system eine Kombination aus
 * Sekunden (in einem long-Primitive gehalten) und Nanosekunden als Bruchteil
 * der letzten Sekunde (in einem int-Primitive gehalten).</strong> Im so
 * definierten Ma&szlig;system k&ouml;nnen alle Zeitpunkte vom Typ
 * {@code TimePoint} direkt miteinander verglichen werden, die zugleich
 * dieses Interface implementieren. Als Epoche wird der Zeitpunkt der
 * technischen Einf&uuml;hrung von UTC in Radiosignalen zum Datum 1972-01-01
 * festgelegt. Vor dieser Epoche ist die Sekunde einfach der 86400-te Teil
 * eines mittleren Sonnentags. Nach dem Epochenwechsel gilt die Atomuhrzeit
 * mit der Definition der Sekunde als SI-Sekunde basierend auf der Anzahl
 * von Schwingungen in einem C&auml;sium-Atom bei einem bestimmten Frequenz-
 * &Uuml;bergang. </p>
 *
 * <p>Im wesentlichen sind die Regeln der Weltzeit die des gregorianischen
 * Kalenders - mit der Ausnahme von Schaltsekunden. Der UTC-Standard und
 * die ISO8601-Norm zur Darstellung von Zeitangaben ber&uuml;cksichtigen
 * Schaltsekunden, die mit der Motivation eingef&uuml;hrt wurden, die Differenz
 * zwischen einer Atomuhr und der Sonnenzeit UT1 stets innerhalb von 0,9
 * Sekunden zu halten. Die Sonnenzeit UT1 ist wegen der mit der Erdrotation
 * verbundenen Anomalien eine gemittelte Zeit, in der jeder Tag immer aus
 * 86400 Sekunden besteht, die eine variable L&auml;nge haben und deshalb
 * keine SI-Sekunden bzw. Atomuhr-Sekunden darstellen. Dieses API liest die
 * UTC-Schaltsekunden aus einer Tabelle ein und definiert die Weltzeit in
 * SI-Sekunden seit dem Beginn der UTC-Epoche [1972-01-01T00:00:00Z] mitsamt
 * Nanosekundenanteil (meist nur auf Millisekunden genau). Mit Herausrechnen
 * der Schaltsekunden und Renormierung auf die POSIX-Epoche 1970-01-01 ergibt
 * sich die POSIX-Zeit, in der eine Minute immer 60 Sekunden hat (auf der Basis
 * 1 Kalendertag = 86400 Sekunden). </p>
 *
 * <p>Die so definierte UTC-Norm wurde offiziell erst am 1. Januar 1972
 * eingef&uuml;hrt. Davor gibt es keine Schaltsekunden. Das wiederum hat
 * zur Folge, da&szlig; alle historischen Zeitangaben, die vor dem Jahr 1972
 * liegen, von Time4J als UT1-Zeit interpretiert werden, also mit Bezug zum
 * mittleren Sonnentag auf dem Greenwich-Meridian. Entsprechend wechselt die
 * Definition der Sekunde mit Beginn der UTC-Epoche zur Atomzeit. Mit dem
 * Epochenwechsel &auml;ndert sich auch die Definition eines Kalendertages.
 * F&uuml;r alle UT1-Angaben vor dem Datum 1972-01-01 ist der Kalendertag
 * identisch mit dem mittleren Sonnentag, aus dem per Zeitgleichung der
 * wahre Sonnentag gewonnen werden kann. Ab dem Beginn der UTC-Epoche jedoch
 * ist ein Kalendertag stattdessen aus meist 86400 SI-Sekunden zusammengesetzt,
 * selten 86401 oder (bis jetzt theoretisch) 86399 SI-Sekunden. Diese Definition
 * eines Kalendertags ist bereits heute nicht mehr exakt gleich zum mittleren
 * Sonnentag. Die Schaltsekunden garantieren lediglich eine Ann&auml;herung des
 * Kalendertags an den mittleren Sonnentag innerhalb von 0,9 Sekunden. </p>
 *
 * <p>Sollte irgendwann einmal entschieden werden, gem&auml;&szlig; einem
 * Vorschlag aus den USA Schaltsekunden wieder abzuschaffen, w&uuml;rde dies
 * im Konzept der Weltzeitskala einen neuen Abschnitt nach UT1 und UTC mit
 * einem wahrscheinlich eigenst&auml;ndigen Namen einleiten. Das neue Segment
 * wird zwar weiterhin auf SI-Sekunden basieren, aber eben keine Schaltsekunden
 * mehr z&auml;hlen. Dieses Interface w&uuml;rde danach zu einer reinen
 * Atomuhrzeitskala ohne Bezug zum mittleren Sonnentag mutieren. Die
 * Abweichung des reinen Atomuhrzeit-Kalendertags vom mittleren Sonnentag
 * betr&auml;gt im Jahre 2013 bereits ca. 25 SI-Sekunden (so wie in der
 * POSIX-Zeit). Auf der ITU-Konferenz im Januar 2012 wurde geregelt, den
 * Vorschlag zur Abschaffung der Schaltsekunden erst 2015 zu entscheiden.
 * ITU ist eine internationale Organisation zur Regelung des Funkverkehrs
 * und verwaltet gegenw&auml;rtig den UTC-Standard. Time4J speichert also
 * Zeitangaben im Konzept dieses Interface wie folgt: </p>
 *
 * <p>&nbsp;</p>
 *
 * <table border="1">
 *  <tr>
 *      <th>Zeitabschnitt</th>
 *      <th>Name</th>
 *      <th>Sekundendefinition</th>
 *      <th>Details</th>
 *  </tr>
 *  <tr>
 *      <td>vor 1972-01-01T00:00:00Z</td>
 *      <td>UT1</td>
 *      <td>mittlere Solarsekunde</td>
 *      <td>Ein mittlerer Sonnentag entspricht als Kalendertag genau
 *      86400 Sekunden mit variabler L&auml;nge. </td>
 *  </tr>
 *  <tr>
 *      <td>1972-01-01T00:00:00Z bis Tag X</td>
 *      <td>UTC (mit Schaltsekunden)</td>
 *      <td>SI-Sekunde auf Atomuhrzeitbasis</td>
 *      <td>Ein Kalendertag setzt sich aus meist 86400 SI-Sekunden, selten
 *      auch aus 86401 oder 86399 SI-Sekunden zusammen. Der Mechanismus
 *      der Schaltsekunden sorgt daf&uuml;r, da&szlig; der Kalendertag
 *      innerhalb einer Differenz von 0,9 SI-Sekunden eng dem mittleren
 *      Sonnentag folgt. Wegen der Unm&ouml;glichkeit, die Erdrotation
 *      langfristig vorauszusagen, liegt der Tag X ca. 6 Monate nach dem
 *      aktuellen Tagesdatum oder der letzten Schaltsekunde in der
 *      Zukunft. Die Institution IERS ist daf&uuml;r verantwortlich,
 *      Schaltsekunden ca. 6 Monate im voraus anzuk&uuml;ndigen. Eine
 *      Sekundenpr&auml;zision ist in der Berechnung von Zeitdifferenzen
 *      nicht mehr gegegeben (Sch&auml;tzwert), wenn l&auml;nger als 6 Monate
 *      im voraus gerechnet wird. </td>
 *  </tr>
 *  <tr>
 *      <td>nach Tag X</td>
 *      <td>IT ??? (International Time ??? - ohne Schaltsekunden)</td>
 *      <td>SI-Sekunde auf Atomuhrzeitbasis</td>
 *      <td>Zuk&uuml;nftige Schaltsekunden sind hier nicht mehr definiert.
 *      Die Folge ist, da&szlig; der Kalendertag auf Basis von 86400
 *      SI-Sekunden immer mehr vom mittleren Sonnentag abweicht. </td>
 *  </tr>
 * </table>
 *
 * <p>Werden in Time4J Schaltsekunden per Konfiguration deaktiviert, dann
 * mutiert UTC mit Ausnahme des Versatz von zwei Jahren zwischen UNIX-
 * und UTC-Epoche zu POSIX. Berechnungen von Zeitdifferenzen in SI-Sekunden und
 * genauer haben dann ab 1972-01-01 einen Fehler, der zumindest in normalen
 * Business-Anwendungsf&auml;llen ignoriert werden kann (zum Beispiel sorgen
 * sich Banken bei der Berechnung von tagesgenauen Zinss&auml;tzen nicht um
 * Sekunden). </p>
 *
 * <p>Argumente f&uuml;r die Ber&uuml;cksichtigung von Schaltsekunden: </p>
 *
 * <ul><li>ASTRONOMIE: Die Differenz zwischen der Atomuhrzeit und der mittleren
 * Sonnenzeit UT1 ist ein physikalisches Faktum, das nicht einfach ignoriert
 * werden kann. Die UT1-Zeit ist im zivilen Alltag wichtiger, da Menschen
 * allgemein zur Mittagszeit den h&ouml;chsten Sonnenstand erwarten. Ohne den
 * UTC-Mechanismus der Schaltsekunden w&uuml;rde irgendwann 12 Uhr Mittag in
 * der Nacht auftreten, was f&uuml;r niemanden akzeptabel w&auml;re. </li>
 * <li>GENAUIGKEIT: Nur so werden sekundengenaue Differenzberechnungen zwischen
 * zwei Zeitpunkten in der N&auml;he von Schaltsekundenereignissen m&ouml;glich.
 * Alle heute in der Praxis verwendeten Uhren verwenden letztlich Atomuhren als
 * Referenzzeit und damit indirekt SI-Sekunden. Auch wenn sie selber in der
 * Regel nicht auf Atomuhrmechanismen basieren (Quartzuhr, Federwerk etc.),
 * so werden sie doch in irgendeiner Form an bestehende Atomuhren angepasst.
 * Dies ist auch deshalb nicht zu vernachl&auml;ssigen, weil das verwendete
 * Ma&szlig;system mathematisch sogar bis zur Nanosekundengenauigkeit geht.
 * Das ist die java-technische Begr&uuml;ndung, warum {@code UniversalTime} hier
 * UTC als universelle Zeitskala verwendet. Gleichzeitig tr&auml;gt die
 * Beschr&auml;nkung der Schaltsekunden auf den Zeitabschnitt ab dem Jahr 1972
 * der Tatsache Rechnung, da&szlig; vorher schon aus technisch-praktischen
 * Gr&uuml;nden nicht einmal von Sekundengenauigkeit gesprochen werden kann.
 * Selbst heute ist z.B. die Nanosekundengenauigkeit mit allen g&auml;ngigen
 * Computer-Uhren oder dem NTP-Protokoll nicht realisierbar und existiert hier
 * nur aus eher datenbanktechnischen Motiven (SQL-TIMESTAMP-Unterst&uuml;tzung,
 * Datenbanken generieren Nano-Zeitstempel oft als Prim&auml;rschl&uuml;ssel
 * mit Hilfe von Zufallsgeneratoren oder einem simplen Z&auml;hler). Aber es
 * gilt nach 1972 auch, da&szlig; z.B. relativ ungenaue Computer-Uhren notfalls
 * manuell an die UTC-Zeit angepasst werden und in der N&auml;he von
 * Schaltsekunden sehr wohl f&uuml;r das Thema in der Gr&ouml;&szlig;enordnung
 * von Sekunden ausreichend sensibel sind. </li>
 * <li>NORMEN / STANDARD: Ein anderer wichtiger Grund ist, da&szlig; UTC zur
 * Zeit der aktuelle Weltstandard im zivilen Alltag und in wichtigen technischen
 * Normen wie dem ISO-8601-Format und Internet-Protokollen wie RFC 3339 ist.
 * Dieses API hat das Ziel, auch in der Darstellung diesen Normen so genau wie
 * m&ouml;glich zu folgen. Schaltsekunden sind zwar selten, aber dennoch im
 * zivilen Alltag pr&auml;sent, wenn sie denn vorkommen. Sie werden immer
 * wieder in den Massenmedien der breiten &Ouml;ffentlichkeit vorgestellt. </p>
 * <li>SEKUNDEN-DEFINITION: UTC stellt als atomuhrzeitbasierte Skala sicher,
 * da&szlig; jede Sekunde physikalisch gleich lang ist, was bei der im zivilen
 * Alltagsleben gebr&auml;chlichen mittleren Sonnenzeit bzw. gregorianischen
 * Zeit UT1 nicht gilt. Varianten wie der abgelaufene Vorschlag UTC-SLS haben
 * noch nie irgendeine Rolle gespielt und versuchen, Schaltsekunden zu
 * verbergen, indem Schaltmillisekunden eingef&uuml;hrt werden, was die
 * Praktikabilit&auml;t nicht wirklich verbessert. Solche Vorschl&auml;ge
 * konterkarieren den Grundgedanken der UTC-Norm, Sekunden mit einheitlicher
 * L&auml;nge zu definieren. </li></ul>
 *
 * <p>Weiterf&uuml;hrende Links zur UTC-Definition: </p>
 *
 * <ul>
 *  <li><a href="http://en.wikipedia.org/wiki/Coordinated_Universal_Time">
 * Wikipedia</a></li>
 *  <li><a href="http://hpiers.obspm.fr/eoppc/bul/bulc/TimeSteps.history">
 * Schaltsekundenhistorie</a></li>
 *  <li><a href="http://hpiers.obspm.fr/eoppc/bul/bulc/bulletinc.dat">
 * Aktuelle Ank&uuml;ndigung von Schaltsekunden</a></li>
 * </ul>
 *
 * <p>Spezifikation: Implementierungen m&uuml;ssen unver&auml;nderlich
 * (<i>immutable</i>) sein. </p>
 *
 * @author  Meno Hochschild
 * @see     LeapSeconds
 */
public interface UniversalTime
    extends UnixTime {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Stellt diese Weltzeit als Sekundenwert in der angegebenen Zeitskala
     * dar. </p>
     *
     * <p>Die von {@code UnixTime} geeerbte Methode {@code getPosixTime()}
     * entspricht {@code getElapsedTime(TimeScale.POSIX)} und bezieht sich
     * auf die UNIX-Epoche 1970-01-01. Die Zeitskala UTC hingegen f&auml;ngt
     * zwei Jahre sp&auml;ter an und z&auml;hlt Schaltsekunden mit. </p>
     *
     * @param   scale       time scale reference
     * @return  elapsed seconds in given time scale
     * @throws  IllegalArgumentException if this instance is out of range
     *          for given time scale
     */
    long getElapsedTime(TimeScale scale);

    /**
     * <p>Stellt diese Weltzeit als Nanosekundenbruchteil in der angegebenen
     * Zeitskala dar. </p>
     *
     * <p>Die vom Super-Interface {@code UnixTime} geeerbte Methode gleichen
     * Namens ohne Argument entspricht exakt dieser Methode, wenn die Zeitskala
     * {@code POSIX} oder {@code UTC} ist. </p>
     *
     * @param   scale       time scale reference
     * @return  nanosecond fraction in given time scale
     * @throws  IllegalArgumentException if this instance is out of range
     *          for given time scale
     */
    int getNanosecond(TimeScale scale);

    /**
     * <p>Liegt dieser Zeitpunkt innerhalb einer positiven Schaltsekunde? </p>
     *
     * <p>Wurde die Unterst&uuml;tzung von UTC-Schaltsekunden per Konfiguration
     * abgeschaltet, dann liefert diese Methode immer {@code false}. </p>
     *
     * @return  {@code true} if this instance represents a positive
     *          leap second else {@code false}
     * @see     LeapSeconds#isEnabled()
     */
    boolean isLeapSecond();

}
