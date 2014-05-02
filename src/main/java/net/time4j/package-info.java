
/**
 * <p>Das Hauptpaket enth&auml;lt vier Grundtypen im ISO-8601-Kalendersystem,
 * n&auml;mlich <code>PlainDate</code>, <code>PlainTime</code>,
 * <code>PlainTimestamp</code> und <code>Moment</code>. </p>
 *
 * <p>Time4J verwendet auch einige System-Properties f&uuml;r den Zweck der
 * internen Startkonfiguration: </p>
 *
 * <ul>
 *  <li>net.time4j.format.iso.decimal.dot =&gt;
 *      controls formatting of decimal separator in ISO-8601-output</li>
 *  <li>net.time4j.sql.utc.conversion =&gt;
 *      SQL-conversion in <a href="TemporalTypes.html">TemporalTypes</a></li>
 *  <li>net.time4j.systemclock.nanoTime =&gt;
 *      controls precision of <a href="SystemClock.html">SystemClock</a></li>
 *  <li>net.time4j.allow.system.tz.override =&gt;
 *      controls permit for overriding standard timezone</li>
 *  <li>net.time4j.scale.leapseconds.suppressed =&gt; see
 *      <a href="scale/LeapSeconds.html#SUPPRESS_UTC_LEAPSECONDS">LeapSeconds.SUPPRESS_UTC_LEAPSECONDS</a></li>
 *  <li>net.time4j.scale.leapseconds.final =&gt; see
 *      <a href="scale/LeapSeconds.html#FINAL_UTC_LEAPSECONDS">LeapSeconds.FINAL_UTC_LEAPSECONDS</a></li>
 * </ul>
 */
package net.time4j;
