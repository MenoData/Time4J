
/**
 * <p>This package handles the UTC timeline (Corrected Universal Time) and
 * other time scales. </p>
 *
 * <p>Tools for conversion of time scales and calculation of UTC-leapseconds
 * are offered, see the classes {@code TimeScale} and {@code LeapSeconds}. </p>
 *
 * <p>Every chronology has its own elements, units and rules but all
 * systems where time points implement the interface {@code UniversalTime}
 * have the same common reference to UTC time scale. This way a common
 * storage model for different systems and timezones can be realized such
 * that all local timestamps can finally be stored as UTC (using timezones).
 * If as special case timezone rules possibly change (DST changes) and the
 * timestamps to be stored are in the future then the alternative would
 * be to save all temporal element values including the local time the timezone
 * and the daylight-saving-rule to be applied. </p>
 */
/*[deutsch]
 * <p>Behandelt die UTC-Zeitachse (Corrected Universal Time) und andere
 * Zeitskalen. </p>
 *
 * <p>Es werden Hilfen zum Konvertieren von Zeitskalen und
 * Ermitteln von UTC-Schaltsekunden angeboten, siehe die Klassen
 * {@code TimeScale} und {@code LeapSeconds}. </p>
 *
 * <p>Jedes Kalendersystem hat seine eigenen Elemente, Einheiten und Regeln,
 * aber alle Systeme, in denen Zeitpunkte das Interface {@code UniversalTime}
 * implementieren, besitzen einen gemeinsamen einheitlichen Bezug zur
 * UTC-Weltzeitlinie. Damit wird zugleich ein &uuml;ber verschiedene Systeme
 * und Zeitzonen hinweg konsistentes Speichermodell realisiert, indem alles als
 * UTC-Objekt gespeichert werden kann. F&uuml;r den Sonderfall von in der
 * Zukunft liegenden Zeitpunkten, wo &Auml;nderungen der Zeitzonenregeln
 * (insbesondere DST-Regeln zur Sommer- und Winterzeit) nicht ausgeschlossen
 * werden k&ouml;nnen, ist alternativ das komplette Speichern der kalendarischen
 * Zeitelementwerte mitsamt der Soll-Uhrzeit (lokale Zeit), der Zeitzone und
 * der in der Zeitzone enthaltenen DST-Regel sinnvoll. </p>
 */
package net.time4j.scale;
