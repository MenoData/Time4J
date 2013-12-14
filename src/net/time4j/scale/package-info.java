
/**
 * <p>Behandelt die UTC-Zeitachse (Corrected Universal Time) und andere
 * Zeitskalen. </p>
 *
 * <p>Es werden Hilfen zum Konvertieren von Zeitskalen und
 * Ermitteln von UTC-Schaltsekunden angeboten, siehe die Klassen
 * {@code TimeScale} und {@code LeapSeconds}. </p>
 *
 * <p>Jedes Kalendersystem hat seine eigenen Felder und Regeln, aber alle
 * Systeme, in denen Zeitpunkte das Interface {@code UTC} implementieren,
 * besitzen einen gemeinsamen einheitlichen Bezug zur UTC-Weltzeitlinie.
 * Damit wird zugleich ein &uuml;ber verschiedene Systeme und Zeitzonen
 * hinweg konsistentes Speichermodell realisiert, indem alles als UTC-Objekt
 * gespeichert werden kann. F&uuml;r den Sonderfall von in der Zukunft
 * liegenden Zeitpunkten, wo &Auml;nderungen der Zeitzonenregeln (insbesondere
 * DST-Regeln zur Sommer- und Winterzeit) nicht ausgeschlossen werden
 * k&ouml;nnen, ist alternativ das komplette Speichern der kalendarischen
 * Zeitfeldwerte mitsamt der Soll-Uhrzeit (lokale Zeit), der Zeitzone und
 * der in der Zeitzone enthaltenen DST-Regel sinnvoll. </p>
 */
package net.time4j.scale;
