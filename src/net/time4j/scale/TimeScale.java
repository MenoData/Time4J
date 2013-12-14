/*
 * -----------------------------------------------------------------------
 * Copyright © 2012 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (TimeScale.java) is part of project Time4J.
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

import java.math.BigDecimal;


/**
 * <p>Definiert verschiedene Zeitskalen sowohl zur Verwendung im zivilen
 * Alltag als auch in der Wissenschaft. </p>
 *
 * <p>Konvertierverfahren sind im allgemeinen nicht bijektiv. Das bedeutet,
 * da&szlig; z.B. zwar einem UTC-Zeitpunkt meist eindeutig ein UT1-, GPS- oder
 * TAI-Zeitpunkt zugeordnet werden kann (positive UTC-Schaltsekunden
 * vorausgesetzt), umgekehrt aber nicht. Noch wichtiger: Jede Konversion
 * erfolgt maximal in Millisekundengenauigkeit (oder schlechter), ist
 * also grunds&auml;tzlich ein N&auml;herungsverfahren. </p>
 *
 * @author  Meno Hochschild
 */
public enum TimeScale {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
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
     * POSIX-Zeit zu einem gregorianischen Zeitstempel im Prinzip undefiniert.
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
    POSIX() {
        @Override
        public long getElapsedTime(UniversalTime ut) {
            return ut.getPosixTime();
        }
        @Override
        public int getFractionalPart(UniversalTime ut) {
            return ut.getNanosecond();
        }
    },

    /**
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
    UTC() {
        @Override
        public long getElapsedTime(UniversalTime ut) {
            return ut.getEpochTime();
        }
        @Override
        public int getFractionalPart(UniversalTime ut) {
            return ut.getNanosecond();
        }
    },

    /**
     * <p>Internationale Atomuhrzeit, die ab 1971 den Namen TAI bekam und
     * auf den SI-Sekunden einer Atomuhr basiert. </p>
     *
     * <p>Es  werden also keine Sekunden als Schaltsekunden interpretiert.
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
     * betr&auml;gt der Versatz zwischen UTC und TAI fest 10 Sekunden
     * (UTC = TAI + 10) und &auml;ndert sich seitdem laufend mit jeder
     * eingef&uuml;gten Schaltsekunde. Im Jahr 2013 ist dieser Versatz
     * auf 35 Sekunden angewachsen. </p>
     */
    TAI() {
        @Override
        public long getElapsedTime(UniversalTime ut) {
            long elapsedTime =
                LeapSeconds.getInstance().strip(ut.getEpochTime())
                - UTC_OFFSET;
            if (elapsedTime < 0) {
                throw new IllegalArgumentException("TAI out of range: " + ut);
            } else {
                return (elapsedTime - 10);
            }
        }
        @Override
        public int getFractionalPart(UniversalTime ut) {
            if (ut.getEpochTime() < 0) {
                throw new IllegalArgumentException("TAI out of range: " + ut);
            } else {
                return ut.getNanosecond();
            }
        }
    },

    /**
     * <p>Wird vom GPS-Navigationssystem verwendet und unterscheidet sich
     * von der TAI-Skala nur um einen konstanten Versatz von 19 Sekunden
     * (GPS = TAI - 19). </p>
     *
     * <p>GPS wurde am 6. Januar 1980 eingef&uuml;hrt. Alle Zeitangaben
     * davor werden von Time4J mit einer Ausnahme quittiert. </p>
     *
     * @see     #TAI
     */
    GPS() {
        @Override
        public long getElapsedTime(UniversalTime ut) {
            long elapsedTime =
                LeapSeconds.getInstance().strip(ut.getEpochTime());
            if (elapsedTime < GPS_START) {
                throw new IllegalArgumentException(
                    "GPS not supported before 1980-01-06: " + ut);
            } else {
                return (elapsedTime - UTC_OFFSET + 9);
            }
        }
        @Override
        public int getFractionalPart(UniversalTime ut) {
            long elapsedTime =
                LeapSeconds.getInstance().strip(ut.getEpochTime());
            if (elapsedTime < GPS_START) {
                throw new IllegalArgumentException(
                    "GPS not supported before 1980-01-06: " + ut);
            } else {
                return ut.getNanosecond();
            }
        }
    };

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

    private static final int UTC_OFFSET = 2 * 365 * 86400;
    private static final long GPS_START = 315964800;

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Stellt die angegebene Weltzeit als Dezimalwert in dieser Zeitskala
     * dar. </p>
     *
     * @param   ut      Weltzeit (UT1 vor 1972, sonst UTC)
     * @return  Dezimalwert in dieser Zeitskala als Sekunden inkl. Bruchteil
     * @throws  IllegalArgumentException bei Wertbereichsverletzungen
     */
    public BigDecimal transform(UniversalTime ut) {

        BigDecimal elapsedTime =
            new BigDecimal(this.getElapsedTime(ut)).setScale(9);
        BigDecimal fraction = new BigDecimal(this.getFractionalPart(ut));
        return elapsedTime.add(fraction.movePointLeft(9));

    }

    /**
     * <p>Stellt die angegebene Weltzeit als Sekundenwert in dieser Zeitskala
     * dar. </p>
     *
     * @param   ut      Weltzeit (UT1 vor 1972, sonst UTC)
     * @return  Sekundenwert in dieser Zeitskala
     * @throws  IllegalArgumentException bei Wertbereichsverletzungen
     */
    public abstract long getElapsedTime(UniversalTime ut);

    /**
     * <p>Stellt die angegebene Weltzeit als Nanosekundenbruchteil in dieser
     * Zeitskala dar. </p>
     *
     * @param   ut      Weltzeit (UT1 vor 1972, sonst UTC)
     * @return  Nanosekundenbruchteil in dieser Zeitskala
     * @throws  IllegalArgumentException bei Wertbereichsverletzungen
     */
    public abstract int getFractionalPart(UniversalTime ut);

}
