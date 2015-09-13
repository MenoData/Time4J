/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2014 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeScale.java) is part of project Time4J.
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

package net.time4j.scale;


/**
 * <p>Defines some time scales for usage both in civil life and in science. </p>
 *
 * <p>Any conversion is usually not bijective. That means that for example
 * an UTC-timestamp can be mapped in a unique way to UT1, GPS or TAI (provided
 * that there are no negative leap seconds) but in reverse not. More important:
 * Conversions often happen in millisecond-precision or worse so they are
 * more or less like approximation procedures. </p>
 *
 * @author  Meno Hochschild
 */
/*[deutsch]
 * <p>Definiert verschiedene Zeitskalen sowohl zur Verwendung im zivilen
 * Alltag als auch in der Wissenschaft. </p>
 *
 * <p>Konvertierverfahren sind im allgemeinen nicht bijektiv. Das bedeutet,
 * da&szlig; z.B. zwar einem UTC-Zeitpunkt meist eindeutig ein UT1-, GPS- oder
 * TAI-Zeitpunkt zugeordnet werden kann (positive UTC-Schaltsekunden
 * vorausgesetzt), umgekehrt aber nicht. Noch wichtiger: Konversionen
 * erfolgen oft in Millisekundengenauigkeit (oder schlechter), sind
 * also grunds&auml;tzlich N&auml;herungsverfahren. </p>
 *
 * @author  Meno Hochschild
 */
public enum TimeScale {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * <p>Counts the seconds relative to UNIX-epoch 1970-01-01T00:00:00Z
     * which is two years before UTC-epoch. </p>
     *
     * <p>Leap seconds are not counted but ignored according to POSIX-standard.
     * Before the UTC-epoch 1972-01-01 the second is effectively defined as
     * the 86400th part of mean solar day (UT1). After this UTC-epoch the
     * POSIX-second is with the exception of leap second events equal to
     * the SI-second based on atomic clocks, but in long term effectively
     * similar to the UT1-second based on mean solar time. </p>
     *
     * <p>POSIX is more present in computer realms than UTC although there is
     * no precision in calculation of SI-seconds (a bug of UNIX-specification).
     * The definition of the reference timezone on zero meridian of Greenwich
     * is the same as in UTC. </p>
     *
     * <p>During a leap second the transformation of POSIX-time to an
     * UTC-timestamp is not defined. An old convention in the UNIX world
     * tries to reset the clock by one second AFTER the leap second, so
     * effectively mapping the leap second to the next day despite of
     * its obvious written form as last second of current day (see also
     * <a href="http://en.wikipedia.org/wiki/Unix_time">Wikipedia-page</a>).
     * The <a href="http://www.opengroup.org/onlinepubs/007904975/basedefs/xbd_chap04.html#tag_04_14">current description of POSIX-time</a>
     * explicitly states however that the relation between the current day
     * time and the current POSIX-value is not specified and dependent on
     * the implementation. Time4J maps the leap second to the current day
     * as the last second. This corresponds to an UNIX-variation where a
     * clock is reset at the begin of a leap second instead of at the end
     * of a leap second. </p>
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Sekunden relativ zur UNIX-Epoche 1970-01-01T00:00:00Z,
     * die zwei Jahre vor der UTC-Epoche liegt. </p>
     *
     * <p>Schaltsekunden werden nicht mitgez&auml;hlt, sondern gem&auml;&szlig;
     * dem POSIX-Standard ignoriert. Vor der UTC-Epoche 1972-01-01 wird die
     * Sekunde effektiv als der 86400-te Teil des mittleren Sonnentags
     * interpretiert (UT1). Danach ist die POSIX-Sekunde mit der Ausnahme von
     * Schaltsekunden im Kurzzeit-Bereich die SI-Sekunde auf Atomuhrzeitbasis,
     * auf lange Sicht aber effektiv als UT1-Sekunde auf Basis der mittleren
     * Sonnenzeit zu interpretieren. </p>
     *
     * <p>POSIX ist im Computer-Bereich pr&auml;senter als UTC, auch wenn von
     * einer Genauigkeit in der Berechnung von SI-Sekunden nicht mehr gesprochen
     * werden kann (ein Bug der UNIX-Spezifikation). Die Definition der
     * Referenzzeitzone auf dem Null-Meridian von Greenwich ist wie in UTC
     * gegeben. </p>
     *
     * <p>W&auml;hrend einer Schaltsekunde ist die Transformation einer
     * POSIX-Zeit zu einem UTC-Zeitstempel im Prinzip undefiniert.
     * Eine alte Konvention in der UNIX-Welt versucht jedoch, erst am Ende
     * einer Schaltsekunde die zugeh&ouml;rige Uhr um eine Sekunde
     * zur&uuml;ckzusetzen, rechnet also die Schaltsekunde selbst entgegen
     * seiner offensichtlichen Schreibform zum Folgetag (so auch gelistet im
     * <a href="http://en.wikipedia.org/wiki/Unix_time">Wikipedia-Artikel</a>).
     * Die <a href="http://www.opengroup.org/onlinepubs/007904975/basedefs/xbd_chap04.html#tag_04_14">aktuelle Beschreibung der POSIX-Zeit</a>
     * sagt jedoch ausdr&uuml;cklich, da&szlig; die Beziehung zwischen der
     * aktuellen Tageszeit und dem aktuellen POSIX-Wert unspezifiert und
     * implementierungsabh&auml;ngig ist. Time4J rechnet die Schaltsekunde
     * immer zu dem Tag, der sie als letzte Sekunde enth&auml;lt. Das
     * entspricht einer UNIX-Variante, in der eine Uhr zu Beginn statt
     * zum Ende einer Schaltsekunde zur&uuml;ckgesetzt wird. </p>
     */
    POSIX,

    /**
     * <p>Counts the seconds relative to UTC-epoch which started at
     * midnight on the calendar days 1972-01-01 (1972-01-01T00:00:00Z)
     * inclusive all leap seconds. </p>
     *
     * <p>Time4J handles all {@code UniversalTime}-timestamps before
     * the UTC-epoch as mean solar time (UT1). The second is therefore
     * defined as the 86400th part of the mean solar day before 1972.
     * After the UTC-epoch 1972-01-01 the second is always the
     * SI-second based on atomic clocks. </p>
     *
     * @see     UniversalTime
     */
    /*[deutsch]
     * <p>Z&auml;hlt die Sekunden relativ zur UTC-Epoche, die zu Mitternacht
     * des Datums 1972-01-01 begann (1972-01-01T00:00:00Z in der UTC-Zeitzone),
     * inklusive aller Schaltsekunden. </p>
     *
     * <p>Time4J behandelt in allen {@code UniversalTime}-Implementierungen
     * Zeitangaben vor der UTC-Epoche r&uuml;ckwirkend als mittlere Sonnenzeit
     * UT1. Vor der UTC-Epoche 1972-01-01 wird also die Sekunde als der 86400-te
     * Teil des mittleren Sonnentags definiert. Danach ist die Sekunde stets
     * die SI-Sekunde auf Atomuhrzeitbasis. </p>
     *
     * @see     UniversalTime
     */
    UTC,

    /**
     * <p>International atomic time which is based on the SI-seconds of an
     * atomic clock and presents a continuous scale relative to 1972-01-01. </p>
     *
     * <p>There is no second which is interpreted respective labelled as
     * leapsecond. Hence this scale is decoupled from civil day and only
     * usefule in a scientific context. As consequence the astronomic day has
     * no meaning on this scale. </p>
     *
     * <p>Strictly spoken, the scale definition of TAI is a statistical
     * approximation to the definition of an SI-second because the average of
     * around 250 atomic clocks worldwide is used. But the deviations are
     * in picoseconds or smaller which is not in the focus of this API. </p>
     *
     * <p>Although TAI knows historical ancestors already since 1958
     * Time4J only supports TAI from UTC-epoch 1972-01-01. First to note
     * the SI-second was introduced in year 1967. Second, the nowadays
     * used TAI-scale had got its name on a conference in year 1971, third
     * to note, the TAI-ancestors were still directly synchronized with
     * UT2 hence had still got a vague astronomical reference.. At the
     * calendar date 1972-01-01 the difference between TAI and UTC was
     * defined as exactly 10 seconds (TAI = UTC + 10). This difference is
     * fixed for all timestamps in epoch seconds because both TAI and UTC
     * counts in pure SI-seconds. But note: If TAI and UTC are resolved
     * to an element-oriented notation (YYYY-MM-DD HH:MM:SS) then the
     * difference between TAI and UTC increases with every inserted
     * leap second because of the different labelling. A TAI day does not
     * know leap seconds. In the year 2013 this difference between a TAI
     * day and an UTC day has increased to 35 seconds. </p>
     */
    /*[deutsch]
     * <p>Internationale Atomuhrzeit, die auf den SI-Sekunden einer Atomuhr
     * basiert und eine monoton fortlaufende Skala relativ zu 1972-01-01
     * darstellt. </p>
     *
     * <p>Es werden also keine Sekunden als Schaltsekunden interpretiert.
     * Somit ist diese Skala vom zivilen Alltag abgekoppelt und nur im
     * Wissenschaftsbetrieb von Bedeutung. Konsequent hat der astronomische
     * Tag in dieser Skala keine Bedeutung. </p>
     *
     * <p>Streng genommen ist auch das Skalenma&szlig; von TAI nur eine
     * statistische Ann&auml;herung an die Definition einer SI-Sekunde als
     * einer bestimmten Anzahl von C&auml;sium-Atomschwingungen, denn es wird
     * &uuml;ber ca. 250 Atomuhren weltweit gemittelt. Jedoch liegt die
     * Abweichung im Bereich von Pikosekunden, was nicht im Fokus dieses API
     * liegt. </p>
     *
     * <p>Obwohl Vorg&auml;nger von TAI schon seit 1958 bekannt sind, erlaubt
     * Time4J TAI-Angaben nur ab der UTC-Epoche 1972-01-01. Erstens wurde die
     * SI-Sekunde erst ab 1967 definiert, zweitens bekam die heutige TAI-Skala
     * ihren Namen auf einer Konferenz im Jahre 1971, drittens waren die
     * Vorg&auml;nger von TAI noch direkt mit UT2 synchronisiert, hatten
     * also noch einen etwas vagen astronomischen Bezug. Zum Datum 1972-01-01
     * wurde der Versatz zwischen TAI und UTC genau auf 10 Sekunden festgelegt
     * (TAI = UTC + 10). Diese Differenz gilt fest f&uuml;r alle Zeitangaben
     * in Epochensekunden, weil sowohl TAI als auch UTC in reinen SI-Sekunden
     * z&auml;hlen. Aber: Werden TAI und UTC zu Zeitstempeln in einer
     * feldorientierten Notation (YYYY-MM-DD HH:MM:SS) aufgel&ouml;st,
     * dann &auml;ndert sich der Versatz seit 1972 laufend mit jeder
     * eingef&uuml;gten Schaltsekunde, weil ein TAI-Tag keine Schaltsekunden
     * kennt. Im Jahr 2013 ist dieser Versatz zwischen TAI-Tag und UTC-Tag
     * auf 35 Sekunden angewachsen. </p>
     */
    TAI,

    /**
     * <p>Is used by the GPS-navigation system and counts SI-seconds relative
     * to the start of GPS. </p>
     *
     * <p>GPS was introduced on 6th of January 1980. All earlier timestamps
     * are not supported by Time4J. Between 1972 and 1980 there were 9
     * leap seconds therefore following relation holds leaving aside the
     * different epoch reference: GPS + delta = UTC - 9 = TAI - 19 where
     * <i>delta</i> stands for the POSIX-difference between 1972-01-01 and
     * 1980-01-06. </p>
     *
     * @see     #TAI
     */
    /*[deutsch]
     * <p>Wird vom GPS-Navigationssystem verwendet und wird relativ zum Start
     * von GPS in SI-Sekunden gez&auml;hlt. </p>
     *
     * <p>GPS wurde am 6. Januar 1980 eingef&uuml;hrt. Alle Zeitangaben davor
     * werden von Time4J mit einer Ausnahme quittiert. Zwischen 1972 und 1980
     * gab es 9 Schaltsekunden, deshalb gilt abgesehen vom unterschiedlichen
     * Epochenbezug folgende Relation: GPS + delta = UTC - 9 = TAI - 19, wo
     * <i>delta</i> f&uuml;r die POSIX-Differenz zwischen 1972-01-01 und
     * 1980-01-06 steht. </p>
     *
     * @see     #TAI
     */
    GPS;

//    /**
//     * <p>Die <i>Terrestrial Time</i> ist nicht an die Erdrotation gebunden
//     * und ist als dynamische Zeit aus Bewegungss&auml;tzen abgeleitet
//     * (theoretisches Ideal). </p>
//     *
//     * <p>Ab 1991 unter Ber&uuml;cksichtigung der H&ouml;he einer Atomuhr
//     * relativ zur Erdoberfl&auml;che (allgemeine Relativit&auml;tstheorie)
//     * neu definiert. Die Vorg&auml;nger ET (Ephemeris Time) und TDT
//     * (Terrestrial Dynamic Time) sind jedoch bis in den Millisekundenbereich
//     * hinein nahezu gleich. Time4J verwendet als N&auml;herung die Formel
//     * TT = TDT = ET = TAI + 32,184 Sekunden. </p>
//     *
//     * @see     #TAI
//     */
//    TT,
//
//    /**
//     * <p>Mittlere Sonnenzeit mit variabler erdrotationsgebundener Sekunde. </p>
//     *
//     * <p>UT1 wird h&auml;ufig mit UTC verwechselt, weil Schaltsekunden selten
//     * auftreten und im zivilen Alltag mit seinen meist deutlich geringeren
//     * Anforderungen an die Genauigkeit kaum wahrnehmbar sind. Die Abweichung
//     * von UT1 relativ zu TT (historisch eigentlich zu ET, Differenz TT - UT1)
//     * wird in der Astronomie als Delta-T bezeichnet und wird laufend vom IERS
//     * ver&ouml;ffentlicht. Time4J verwendet zum Absch&auml;tzen von Delta-T
//     * polynomiale Ausdr&uuml;cke, die auf den belgischen Astronomen Jean Meeus
//     * zur&uuml;ckgehen und kann so begrenzt zwischen UT1 und den anderen
//     * nicht an die Erdrotation gebundenen Zeitskalen umrechnen. </p>
//     */
//    UT1;

}
