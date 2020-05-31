/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2020 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriData.java) is part of project Time4J.
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

package net.time4j.calendar;

import net.time4j.PlainDate;


/**
 * <p>Generic interface for loading and representing any local variant of Hijri data. </p>
 *
 * <p>Typical implementations might be based either on file resources or on SQL databases. A sensible approach
 * looks such that a database table has 12 columns where every cell contains the length of month in the associated
 * Hijri year (per row). </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
/*[deutsch]
 * <p>Allgemeine Schnittstelle zum Laden und zur Darstellung von lokalen Varianten des Hijri-Kalenders. </p>
 *
 * <p>Typische Implementierungen k&ouml;nnen entweder auf Dateiressourcen oder SQL-Datenbanktabellen beruhen.
 * Eine solche Tabelle hat sinnvollerweise f&uuml;r jedes Jahr als Datensatz 12 Spalten, die die Monatsl&auml;ngen
 * im jeweiligen Hijri-Jahr enthalten. </p>
 *
 * @author  Meno Hochschild
 * @since   5.6
 */
public interface HijriData {

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Initializes the data. </p>
     *
     * <p>This method will be called by Time4J every time when the data have to be registered.
     * A typical implementation might read a SQL database and load an internal array cache
     * in order to avoid excessive calls of database. </p>
     *
     * @throws  IllegalStateException if the initialization fails
     * @see     HijriCalendar#register(HijriData)
     */
    /*[deutsch]
     * <p>Initialisiert die Daten. </p>
     *
     * <p>Diese Methode wird von Time4J immer dann aufgerufen, wenn die Daten registriert werden m&uuml;ssen.
     * Eine typische Implementierung k&ouml;nnte hier eine SQL-Datenbanktabelle lesen und ein internes Array
     * f&uuml;llen, um wiederholte Datenbankzugriffe zu vermeiden. </p>
     *
     * @throws  IllegalStateException if the initialization fails
     * @see     HijriCalendar#register(HijriData)
     */
    void prepare();

    /**
     * <p>Obtains the name of the associated Hijri calendar variant. </p>
     *
     * <p>The full calendar type is created by prefixing the name with &quot;islamic-&quot;.
     * For example: The calendar of Saudi-Arabia with the name &quot;umalqura&quot; has the
     * calendar type and full variant name &quot;islamic-umalqura&quot;. </p>
     *
     * @return  String which must not start with prefix &quot;islamic&quot;
     */
    /*[deutsch]
     * <p>Liefert den Namen der verkn&uuml;pften Hijri-Kalendervariante. </p>
     *
     * <p>Der volle Kalendertyp und Variantenname entsteht dann dadurch, da&szlig; das Pr&auml;fix
     * &quot;islamic-&quot; vor den eigentlichen Namen gesetzt wird. Zum Beispiel hat der Kalender
     * von Saudi-Arabien mit dem Namen &quot;umalqura&quot; den Kalendertyp und vollen Variantennamen
     * &quot;islamic-umalqura&quot;. </p>
     *
     * @return  String which must not start with prefix &quot;islamic&quot;
     */
    String name();

    /**
     * <p>Determines the data version (optional). </p>
     *
     * <p>This method serves for data analysis only. </p>
     *
     * @return  version info (maybe empty if not relevant)
     */
    /*[deutsch]
     * <p>Bestimmt die Datenversion (optional). </p>
     *
     * <p>Diese Methode dient nur Analysezwecken. </p>
     *
     * @return  version info (maybe empty if not relevant)
     */
    default String version() {
        return "";
    }

    /**
     * <p>Obtains the minimum supported Hijri year. </p>
     *
     * @return  smallest supported Hijri year
     */
    /*[deutsch]
     * <p>Liefert das kleinste unterst&uuml;tzte Hijri-Jahr. </p>
     *
     * @return  smallest supported Hijri year
     */
    int minimumYear();

    /**
     * <p>Obtains the maximum supported Hijri year. </p>
     *
     * @return  biggest supported Hijri year
     */
    /*[deutsch]
     * <p>Liefert das gr&ouml;&szlig;te unterst&uuml;tzte Hijri-Jahr. </p>
     *
     * @return  biggest supported Hijri year
     */
    int maximumYear();

    /**
     * <p>Obtains the gregorian date of first day and month in minimum Hijri year. </p>
     *
     * @return  PlainDate
     */
    /*[deutsch]
     * <p>Liefert das gregorianische Datum des ersten Tags und ersten Monats im kleinsten Hijri-Jahr. </p>
     *
     * @return  PlainDate
     */
    PlainDate firstGregorianDate();

    /**
     * <p>Obtains the length of given month in days. </p>
     *
     * <p>The {@code hijriYear}-parameter is always in the range {@code minimumYear()}-{@code maximumYear()}. </p>
     *
     * @param   hijriYear   the Hijri year which contains the month in question
     * @param   hijriMonth  the Hijri month in the range 1-12
     * @return  length of month (usually 29 or 30)
     * @see     #minimumYear()
     * @see     #maximumYear()
     */
    /*[deutsch]
     * <p>Liefert die L&auml;nge des angegebenen Monats in Tagen. </p>
     *
     * <p>Der {@code hijriYear}-Parameter ist immer im Bereich {@code minimumYear()}-{@code maximumYear()}. </p>
     *
     * @param   hijriYear   the Hijri year which contains the month in question
     * @param   hijriMonth  the Hijri month in the range 1-12
     * @return  length of month (usually 29 or 30)
     * @see     #minimumYear()
     * @see     #maximumYear()
     */
    int lengthOfMonth(
        int hijriYear,
        int hijriMonth
    );

}
