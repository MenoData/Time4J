
/**
 * <p>Definiert die allgemeinen generischen Schemata und Schnittstellen
 * von kalendarischen Zeitsystemen. Anwender, die nur einen kurzen Einstieg
 * in Standardkalendersysteme suchen, seien auf das Hauptpaket
 * <a href="../package-summary.html">net.time4j</a> verwiesen. </p>
 *
 * <p>Ein chronologisches System enth&auml;lt chronologische Elemente und
 * als Zeitachse auch Zeiteinheiten. Den Elementen sind in einer konkreten
 * Zeitpunktinstanz Werte zugeordnet, die zusammen eine Koordinate auf einer
 * in die Zukunft gerichteten Zeitachse festlegen. Diese Zeitachse kann
 * z.B. die UTC-Weltzeitlinie sein. Allgemein ist eine Zeitachse mathematisch
 * gesehen eine Linie, auf der ein Zeitpunkt durch geeignete Koordinaten
 * beschrieben wird. Zur Chronologie geh&ouml;rt auch eine Skalierung der
 * Zeitachse mittels eines Systems aus fest vordefinierten Zeiteinheiten.
 * Diese Einheiten dienen vor allem der Zeitarithmetik und Dauerberechnung,
 * definieren also, wie L&auml;ngen auf der Zeitachse berechnet werden. </p>
 *
 * <p>Der wesentliche Unterschied zwischen Elementen und Zeiteinheiten ist
 * somit, da&szlig; Elemente unmittelbar zustandsbezogen sind und punktuellen
 * koordinatenbezogenen Charakter haben, w&auml;hrend Zeiteinheiten die
 * mathematische Dimension einer L&auml;nge bzw. Dauer eigen ist. </p>
 *
 * <p>Die vorhandenen Abstraktionsm&ouml;glichkeiten dieses Pakets erlauben
 * einerseits eine gro&szlig;e Typsicherheit und andererseits die notwendige
 * Flexibilit&auml;t, um beliebige Chronologien neu zu bauen. Konkrete
 * Implementierungen starten mit der Basisklasse {@code TimePoint} oder
 * der Datumsklasse {@code Calendrical} und registrieren dazu Elemente,
 * Zeiteinheiten und die entsprechenden Regeln in der zugeh&ouml;rigen
 * <code>Chronology</code>, die die chronologische Logik enth&auml;lt. </p>
 *
 * <p>Werte von Elementen werden meistens als Integerzahlen modelliert,
 * k&ouml;nnen aber bei Bedarf in anderen Typen definiert werden, z.B. als
 * Enums. Der Wertbereich eines int-Primitive ist in der Praxis keine reale
 * Einschr&auml;nkung, weil jede Chronologie gen&uuml;gend gro&szlig;e
 * Elemente (also mit kleiner Pr&auml;zision) definieren kann. Zum Beispiel
 * kennt eine GPS-Zeit auch ein Wochenelement. Die genaue Festlegung des
 * Wertbereichs ist der jeweiligen Elementdokumentation zu entnehmen. </p>
 */
package net.time4j.engine;
