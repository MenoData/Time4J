
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
 *      controls formatting of decimal separator in ISO-8601-output (default is the comma)</li>
 *  <li>net.time4j.sql.utc.conversion =&gt;
 *      SQL-conversion in <a href="TemporalType.html">TemporalType</a>-constants</li>
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
 *      steuert die Textausgabe des Dezimaltrennzeichens in ISO-8601 (Vorgabe ist das Komma)</li>
 *  <li>net.time4j.sql.utc.conversion =&gt;
 *      SQL-Konversion in <a href="TemporalType.html">TemporalType</a></li>
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
 * </ul>
 */
package net.time4j;
