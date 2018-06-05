/**
 * <p>The main package contains four basic types of ISO-8601, namely
 * <code>PlainDate</code>, <code>PlainTime</code>, <code>PlainTimestamp</code>
 * and <code>Moment</code>. </p>
 *
 * <p>Time4J also uses some system properties for the purpose of internal
 * start configuration: </p>
 *
 * <ul>
 *  <li>net.time4j.format.iso.decimal.dot =&gt;
 *      controls formatting of decimal separator in ISO-8601-output
 *      (default is the comma)</li>
 *  <li>net.time4j.sql.utc.conversion =&gt;
 *      SQL-Konversion in <a href="sql/JDBCAdapter.html">JDBCAdapter</a></li>
 *  <li>net.time4j.systemclock.nanoTime =&gt;
 *      controls precision of <a href="SystemClock.html">SystemClock</a></li>
 *  <li>net.time4j.allow.system.tz.override =&gt;
 *      controls permit for overriding standard timezone</li>
 *  <li>net.time4j.scale.leapseconds.suppressed =&gt; see
 *      <a href="scale/LeapSeconds.html#SUPPRESS_UTC_LEAPSECONDS">LeapSeconds.SUPPRESS_UTC_LEAPSECONDS</a></li>
 *  <li>net.time4j.scale.leapseconds.final =&gt; see
 *      <a href="scale/LeapSeconds.html#FINAL_UTC_LEAPSECONDS">LeapSeconds.FINAL_UTC_LEAPSECONDS</a></li>
 *  <li>net.time4j.scale.leapseconds.path =&gt; see
 *      <a href="scale/LeapSeconds.html#PATH_TO_LEAPSECONDS">LeapSeconds.PATH_TO_LEAPSECONDS</a></li>
 *  <li>net.time4j.tz.repository.path =&gt;
 *      determines the path of the directory for the timezone data of
 *      Time4J, either absolute or relative to class path (default is
 *      the folder &quot;tzrepo&quot; in class path)</li>
 *  <li>net.time4j.tz.repository.version =&gt;
 *      preferred version for the timezone data of Time4J (example: 2015a),
 *      if specified then Time4J will look for a file with name
 *      &quot;tzdata{version}.repository&quot; in given directory path
 *      otherwise the name will not contain the version</li>
 *  <li>net.time4j.base.ResourceLoader =&gt; class name of external {@code ResourceLoader}-implementation</li>
 * </ul>
 */
/*[deutsch]
 * <p>Das Hauptpaket enth&auml;lt vier Grundtypen in ISO-8601,
 * n&auml;mlich <code>PlainDate</code>, <code>PlainTime</code>,
 * <code>PlainTimestamp</code> und <code>Moment</code>. </p>
 *
 * <p>Time4J verwendet auch einige System-Properties f&uuml;r den Zweck der
 * internen Startkonfiguration: </p>
 *
 * <ul>
 *  <li>net.time4j.format.iso.decimal.dot =&gt;
 *      steuert die Textausgabe des Dezimaltrennzeichens in ISO-8601 (Vorgabe
 *      ist das Komma)</li>
 *  <li>net.time4j.sql.utc.conversion =&gt;
 *      SQL-Konversion in <a href="sql/JDBCAdapter.html">JDBCAdapter</a></li>
 *  <li>net.time4j.systemclock.nanoTime =&gt;
 *      steuert die Genauigkeit von
 *      <a href="SystemClock.html">SystemClock</a></li>
 *  <li>net.time4j.allow.system.tz.override =&gt;
 *      steuert, ob das &Uuml;berschreiben der Standard-Zeitzone zul&auml;ssig
 *      ist</li>
 *  <li>net.time4j.scale.leapseconds.suppressed =&gt; siehe
 *      <a href="scale/LeapSeconds.html#SUPPRESS_UTC_LEAPSECONDS">LeapSeconds.SUPPRESS_UTC_LEAPSECONDS</a></li>
 *  <li>net.time4j.scale.leapseconds.final =&gt; siehe
 *      <a href="scale/LeapSeconds.html#FINAL_UTC_LEAPSECONDS">LeapSeconds.FINAL_UTC_LEAPSECONDS</a></li>
 *  <li>net.time4j.scale.leapseconds.path =&gt; siehe
 *      <a href="scale/LeapSeconds.html#PATH_TO_LEAPSECONDS">LeapSeconds.PATH_TO_LEAPSECONDS</a></li>
 *  <li>net.time4j.tz.repository.path =&gt;
 *      legt den Verzeichnispfad f&uuml;r die eigenen Zeitzonendaten
 *      fest, entweder absolut oder relativ zum Klassenpfad (Vorgabe ist
 *      &quot;tzrepo&quot; im Klassenpfad)</li>
 *  <li>net.time4j.tz.repository.version =&gt;
 *      bevorzugte Version f&uuml;r die eigenen Zeitzonendaten (z.B. 2015a),
 *      wenn angegeben wird Time4J im Verzeichnispfad nach einer Datei mit dem
 *      Namen &quot;tzdata{version}.repository&quot; suchen, ansonsten wird
 *      der Name der Datei nicht die Version enthalten</li>
 *  <li>net.time4j.base.ResourceLoader =&gt; Klassenname einer externen {@code ResourceLoader}-Implementierung</li>
 * </ul>
 */
package net.time4j;
