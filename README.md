Time4J
======

Advanced date, time and interval library for Java

Motivation:
-----------

Time4J is thought as a complete and high-end replacement for old java classes around java.util.Date, java.util.Calendar and java.text.SimpleDateFormat. This project is also intended as first-class alternative to the popular libraries JodaTime and its successor JSR-310 (Threeten) since the target audience of Time4J will not only be business Java developers, but also developers with a more scientific background (for example extended time scale support including leap seconds or historically accurate dates).

Although the new JSR-310 (built in Java 8) is certainly a very useful library for many business developers it also has some severe and intentional limitations. Time4J intends to fill all gaps in the future so justifying its co-existence. The **interoperability with `java.time`-package** is supported by easily available conversion methods.

Current state and introduction:
-------------------------------

On 2022-06-20, the version v5.9.1 of Time4J has been finished and released. It requires at least Java-8. The older version lines v3.x and v4.x have reached end-of-life with the latest versions v3.50 and v4.38 where v3.x is based on Java 6+7. The previous version lines v1.x and v2.x are no longer recommended (due to several backward incompatibilities) and have reached end-of-life, too. Time4J is organized in modules. The module **time4j-base** is always necessary. Other modules are optional and include:

- **time4j-sqlxml** contains a simple adapter for the support of SQL-databases. 
- **time4j-tzdata** encapsulates the time zone repository (independent github-project starting with version 5.0-2018f)
- **time4j-ui** with JavaFX-features (includes a calendar picker)

For **Android support** please refer to the sister project [Time4A](https://github.com/MenoData/Time4A).

Standard use cases will be covered by the main package "net.time4j". It offers four basic temporal types.

- `PlainDate` = calendar date strictly following ISO-8601
- `PlainTime` = wall time (on an analogous clock) including 24:00-support and flexible dayperiods
- `PlainTimestamp` = local timestamp as composition of calendar date and wall time
- `Moment` = global timestamp which refers to true UTC standard including leapsecond-support

Here some examples as a flavour of how Time4J-code looks like (shown code valid for v5.0 or later):

```java
import net.time4j.*;
import net.time4j.format.TextWidth;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.olson.*;

import java.util.Locale;

import static net.time4j.CalendarUnit.MONTHS;
import static net.time4j.PlainDate.DAY_OF_MONTH;
import static net.time4j.PlainDate.DAY_OF_WEEK;
import static net.time4j.PlainTime.MINUTE_OF_HOUR;
import static net.time4j.Weekday.WEDNESDAY;

public class Demo {
  public static void main(String... args) {
	// What is the last day of overnext month?
	System.out.println(
		SystemClock.inLocalView().today().plus(2, MONTHS).with(DAY_OF_MONTH.maximized()));

	// When is next wednesday?
	PlainDate today = SystemClock.inLocalView().today();
	PlainDate nextWednesday = today.with(DAY_OF_WEEK.setToNext(WEDNESDAY));
	System.out.println(nextWednesday);

	// What is the current wall time rounded down to multiples of 5 minutes?
	PlainTimestamp currentLocalTimestamp = SystemClock.inZonalView(EUROPE.BERLIN).now();
	PlainTime roundedTime =
		currentLocalTimestamp.getWallTime() // T22:06:52,688
		.with(MINUTE_OF_HOUR.atFloor()) // T22:06
		.with(MINUTE_OF_HOUR.roundedDown(5)); // T22:05
	System.out.println("Rounded wall time: " + roundedTime);
	
	// Example for flexible dayperiods
	PlainTime eveningTime = PlainTime.of(20, 45);
	ChronoFormatter<PlainTime> formatter =
		ChronoFormatter.ofTimePattern("h:mm B", PatternType.CLDR, Locale.ENGLISH);
	System.out.println(
		"12-hour-format with dayperiod: " 
		+ formatter.format(eveningTime)); // 8:45 in the evening

	// How does last UTC-leapsecond look like in Japan?
	Moment leapsecondUTC =
		PlainDate.of(2012, Month.JUNE, 30)
		.at(PlainTime.midnightAtEndOfDay()) // 2012-06-30T24 => 2012-07-01T00
		.atUTC().minus(1, SI.SECONDS);
	System.out.println(leapsecondUTC); // 2012-06-30T23:59:60Z

	System.out.println(
		"Japan-Time: "
		+ ChronoFormatter.ofMomentPattern(
		     "uuuu-MM-dd'T'HH:mm:ssXX", 
		     PatternType.CLDR, 
		     Locale.ROOT, 
		     ASIA.TOKYO
		  ).format(leapsecondUTC)
	); // Japan-Time: 2012-07-01T08:59:60+0900

	// duration in seconds normalized to hours, minutes and seconds
	Duration<ClockUnit> dur = Duration.of(337540, ClockUnit.SECONDS).with(Duration.STD_CLOCK_PERIOD);

	// custom duration format => hh:mm:ss
	String s1 = Duration.Formatter.ofPattern(ClockUnit.class, "hh:mm:ss").format(dur);
	System.out.println(s1); // output: 93:45:40

	// localized duration format for french
	String s2 = PrettyTime.of(Locale.FRANCE).print(dur, TextWidth.WIDE);
	System.out.println(s2); // output: 93 heures, 45 minutes et 40 secondes
	
	// following code requires v4.20 (or later) and Java-8 using java.time.LocalDate
	ChronoFormatter<LocalDate> formatter2 =
	    ChronoFormatter.setUp(PlainDate.threeten(), new Locale("en", "SE"))
	        .addPattern("GGGG yyyy, MMMM ", PatternType.CLDR)
	        .addEnglishOrdinal(ChronoHistory.ofSweden().dayOfMonth())
	        .build();
	System.out.println(formatter2.format(LocalDate.of(1712, 3, 11)));
	// output: Anno Domini 1712, February 30th
  }
}
```

Design remarks:

a) **Type-safety**: Although Time4J is strongly generified users will not really use any generics in their application code as demonstrated in example code, but are more or less type-safe at compile-time. For example, it is impossible to add clock units to a calendar date. This is in contrast to JSR-310 which heavily relies on runtime exceptions. Otherwise Time4J shares the advantages like immutability and non-tolerant null-handling.

b) **Explicit**: In contrast to most other libraries Time4J does not like implicit defaults. Users have to explicitly specify what locale or time zone they want. And even if they want the default then they spell it so in methods like: `inLocalView()` or `localFormatter(...)` or `inStdTimezone()`. This philosophy is also the reason why the class `PlainDate` is missing a static method like `today()`. This method instead exists in the class `ZonalClock` making clear that you cannot achieve the current local date and time without specifying the time zone.

c) **Manipulations based on elements**: Time4J offers a lot of manipulations of date and time by an element-centric approach. Every basic type registers some elements (similar to fields in other libraries) which serve as access key to chronological partial data. These elements like "MINUTE_OF_HOUR" offer many different manipulation methods, called operators using the strategy pattern idea. With this design it is possible to manipulate a `PlainTime` in about 179 different ways. Another advantage of this design: Despite the size of features the count of methods in most classes is still not too big, `PlainTime` has less than 50 methods including the inherited methods from super classes.

d) **Temporal arithmetic**: Another way of manipulation is date/time-arithmetic along a time axis. All four basic types have their time axis. For example roughly spoken `Moment` is defined as an elapsed count of SI-seconds since UTC epoch while a calendar date (here: `PlainDate`) maps dates to julian days - another kind of time axis. The essentials of this time arithmetic are realized via the abstract super class `TimePoint`. So all four basic types inherit methods like `plus(n, units)`, `until(...)` etc for supporting adding, subtracting and evaluating durations. Multi-unit-durations are handled by the classes `Duration` and `MachineTime`.

e) **Global versus local**: Time4J rejects the design idea of JSR-310 to separate between "machine time" and "human time". This is considered as artificial. So all four basic types offer both aspects in one. For example a calendar date is simultaneously a human time consisting of several meaningful elements like year, month etc. and also a kind of machine or technical time counter because you can define a single incrementing number represented by julian days. In a similar way a UTC-moment has both a technical counter (the number of SI-seconds since UTC-epoch) AND a human representation visible in its canonical output produced by `toString()`-method (example: 2014-04-21T19:45:30Z). However, Time4J emphasizes the difference between local and global types. Conversion between these types always require a timezone or an offset.

f) **Internationalization**: Time4J defines its own i18n-resources for many languages (**95 languages in version 5.9**) in order to defend its i18n-behaviour against poor or insufficient platform resources (which only serve as fallback). Especially localized formatting of durations is not a supported feature on any platform, so Time4J fills an important gap.

g) **Powerful format engine**: The built-in format engine located in format/expert-package offers overwhelmingly many features, general interfaces for customization and outstanding parsing performance (better than in Joda-Time or JSR-310).

Support for alternative calendars:
----------------------------------

- Badi (Bahai)
- Chinese (since 1645)
- Coptic
- Dangi (old Korean)
- Ethiopian (including support for Ethiopian time)
- French revolutionary
- Hebrew (including support for Hebrew time)
- Hijri (Islamic) with a lot of customizable variants
- Hindu (based on algorithms by Dershowitz/Reingold)
- Historic christian (includes british, byzantine, swedish etc.)
- Indian national (Saka)
- Japanese (including lunisolar part since AD 701)
- Juche (North Korea)
- Julian
- Minguo (Taiwan)
- Persian (3000 years)
- ThaiSolar (Suriyakati), also valid before 1941
- Vietnamese

Plans for next releases:
------------------------

There are no fixed predictions when some features will be introduced in which release. However, you can follow the milestone page to get a rough estimation - see https://github.com/MenoData/Time4J/milestones. Time4J will be a long-term running project.

Downloads and Requirements:
---------------------------

You can find the latest downloads on the release page for running on the classpath. Alternatively you can use the maven central repository. Maven example for using the timezone repository and running on the module path in Java 9+:

Add these dependencies to your pom-file (typical setup):

```
<dependency>
  <groupId>net.time4j</groupId>
  <artifactId>time4j-base</artifactId>
  <version>5.9.1</version>
</dependency>
<dependency>
  <groupId>net.time4j</groupId>
  <artifactId>time4j-sqlxml</artifactId>
  <version>5.9.1</version>
</dependency>
<dependency>
  <groupId>net.time4j</groupId>
  <artifactId>time4j-tzdata</artifactId>
  <version>5.0-2022a</version>
</dependency>
```

Then make sure that the very first usage of Time4J-code starts with the following instruction:

```
TZDATA.init();
```

The last step is only necessary for module path environments (Java 9+) provided that the timezone repository should be used instead of platform zone rules. This special initialization can be left out if Time4J is run on the traditional classpath (for example Java 8). Users are also asked to set following system property
in OSGi-environments: &quot;-Dnet.time4j.base.useClassloaderOnly=true&quot;.

Please also read the installation notes on Time4J-tutorial.

Feedback:
---------

Feedback is welcome. You can best use following page for feedback: https://github.com/MenoData/Time4J/issues

Tutorials:
----------

English tutorials and code examples are presented on the website "http://www.time4j.net/".

Blog:
-----

If you are capable of German language you can also read my blog "http://www.menodata.de/blog/" where I sometimes post my views about Java and Time, for example my review of JSR-310.
