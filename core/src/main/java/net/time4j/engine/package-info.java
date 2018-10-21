/**
 * <p>Defines the common generic schemes and interfaces of chronological
 * systems. Users which only need an entry point to standard chronologies
 * should rather consult the main package
 * <a href="../package-summary.html">net.time4j</a>. </p>
 *
 * <p>Ein chronologisches System contains chronological elements and
 * (as time axis) also time units. Given a concrete time point on a
 * time axis the (primary) elements compose the coordinates of the
 * time point on this time axis which is directed into future. This
 * time axis might be the global UTC-timeline. In general a time axis
 * is mathematically spoken a line where a time point is described by
 * some suitable coordinates. This time axis is also scaled by mean of
 * a system of fixed predefined time units. These units mainly serve for
 * the time arithmetic and calculations of durations/distances on a
 * time axis. </p>
 *
 * <p>The main difference between elements and units is such that elements
 * directly refers to the state of a time point and are dimension-less like
 * coordinates on a time axis while time units have the dimension of a
 * length respective duration. </p>
 *
 * <p>The available abstractions of this package allow type-safety and also
 * the necessary flexibility in order to build new chronologies from the
 * scratch. Concrete implementations either start with the base class
 * {@code TimePoint} or with the date class {@code Calendrical} and register
 * elements, time units and calculation rules in the associated
 * <code>Chronology</code> which contains the chronological logic. </p>
 *
 * <p>Values of elements are usually modelled as integers but can also be
 * defined in other types like enums. The value range of an int-primitive
 * is usually no real limitation because each chronology can define
 * sufficiently large elements (with small precision). For example
 * a GPS-time also knows a week element. The detailed value range of
 * a chronological element is documented in the concrete element types. </p>
 */
/*[deutsch]
 * <p>Definiert die allgemeinen generischen Schemata und Schnittstellen
 * von chronologischen Zeitsystemen. Anwender, die nur einen kurzen Einstieg
 * in Standardchronologien suchen, seien auf das Hauptpaket
 * <a href="../package-summary.html">net.time4j</a> verwiesen. </p>
 *
 * <p>Ein chronologisches System enth&auml;lt chronologische Elemente und
 * (als Zeitachse) auch Zeiteinheiten. Den Elementen sind in einer konkreten
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
