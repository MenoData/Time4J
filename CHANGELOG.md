## [v4.8] not yet released
### Added
- Add Coptic calendar [#388]
- Support for triennal julian leap years [#393]

### Fixed
- Compile problem with text elements in ChronoFormatter.Builder [#398]
- plus/minus(CalendarDays) does not document any exception [#397]
- Arithmetic overflow in ChronoHistory [#391]

### Changed (removed/deprecated)
- Refactor handling of two exotic format attributes [#395]
- Hide internal format attributes of ChronoHistory [#394]

## [v4.7] published on 2015-10-22
### Added
- New style-based factory methods for ChronoFormatter [#376], [#377]
- Repository for localized date-time-patterns [#100]
- Support for Amharic (am) [#387]
- Support for Swahili (sw) [#386]
- Support for Uzbek (uz), Turkmen (tk) and Kazakh (kk) [#385]
- Support for Armenian (hy), Azerbaijani (az) and Georgian (ka) [#384]
- Support for Filipino (Tagalog - fil or tl) [#380]
- Support for Icelandic (is) and Malta language (mt) [#375]

### Fixed
- Improved parsing of MSK timezone name [#381]
- Negative duration in Arabic sometimes without minus sign [#379]
- ChronoFormatter.format()-methods lack type safety [#378]

### Changed
- Better translations for various countries (mainly arabic and french) [#383]
- Better translations for Spanish locales in Latin America [#368]
- Simplify interface ChronoMerger by using Java-8 default methods [#382]
- Two ChronoFormatter.format()-methods renamed [#378]
- Low-level interface ChronoMerger with new method [#377]
- Review plural rules [#366]
- Update i18n-data to CLDR-28 [#374]
- Change ISO-8601-fallback-mechanism to CLDR standard [#373]
- FormatPatternProvider.getDateTimePattern() with 3 args [#100]

### Removed
- FormatPatternProvider.DEFAULT [#100]

## [v4.6] published on 2015-10-01
### Added
- Formatting/parsing of intervals based on custom interval patterns [#359]
- Canonicalization of interval boundaries [#365]
- New Persian calendar [#357]
- Support for Pashto language [#358]
- Support for Turkish islamic calendar (Diyanet) [#355]

### Fixed
- Non-overridden format attributes should be preserved [#364]
- Enable parsing of timestamps with 24:00 even in strict mode [#363]
- 2015-10-25T02:00+01:00[Europe/Berlin] cannot be parsed [#361]
- Wrong between-calculation with overflow units [#360]
- Pattern letter "L" causes exception for non-iso-dates [#354]

### Changed
- Behaviour of interval formatting/parsing changed [#359]
- Make serialization of HijriCalendar versionable [#356]

### Removed
- Interface ExtZoneProvider (no usage) [#371]

## [v4.5] published on 2015-09-15
### Added
- Support for Malay language [#353]
- Unify CalendarVariant and Calendrical by new interface CalendarDate [#350]
- New XOR-operator for interval collections [#347]
- Make PatternType.CLDR useable for non-iso-dates [#348]
- New calendar-related class GeneralTimestamp [#340]
- New intersect-operator for interval collections [#345]
- New method IntervalCollection.getRange() [#344]
- PrettyTime.printRelativeOrDate() using PlainDate [#343]
- Moment-class with a new precision element [#342]
- Add plus/minus-operators for IntervalCollection [#339]

### Fixed
- Disable parsing of ambivalent text forms [#351]

### Changed
- Rename unit pattern resource file names [#352]
- CalendarVariant and Calendrical changed Temporal-signature [#350]
- Two specialized now()-methods in ZonalClock changed the return type [#340]
- IntervalCollection.union() should create a merged collection [#338]

## [v4.4] published on 2015-08-31
### Added
- Support for algorithmic variants of islamic calendar [#334]
- Create direct option to determine length of Hijri month or year [#337]
- Add toDate() and toTime() in PlainTimestamp [#336]
- New method(s) for printing durations with 1 parameter only [#333]
- Create option to either print relative time or normal date-time [#328]
- Print relative times with abbreviations [#327]
- I18n-support for "yesterday", "today", "tomorrow" [#310]

### Fixed
- Printing relative time near a leap second incorrect [#332]
- Behaviour of timezone-offset-parser inconsistent with spec [#330]
- Time-axis-element not fully supported [#329]

### Changed
- ISO-formatters need to be strict by default [#331]

## [v4.3] published on 2015-08-12
### Added
- Umalqura calendar for Saudi-Arabia (experimental) [#313]
- Start-of-day-feature, relevant for current date in islamic calendar [#323]
- New pattern type for generic date chronologies [#324]
- New pattern type CLDR_24 with support for ISO-hour in range 0-24 [#314]
- Convenient method `printRelativeInStdTimezone(moment)` for `PrettyTime` [#311]
- New transformation methods for calendrical objects [#318]
- Introduce calendar families with day calculations [#315]

### Fixed
- Remove unnecessary warning message in ChronoFormatter [#312]

### Deprecated
- Interface `ExtZoneProvider` no longer used

### Changed
- Narrow value-type of BasicElement to Comparable [#321]

## [v4.2] published on 2015-06-26
### Added
- Generic clock-based creation of any `ChronoEntity` [#303]
- Add clock-methods `inPlatformView()` [#304]
- Register `PlainTime.PRECISION` in `PlainTimestamp` [#306]
- Improve adjacent digit parsing of 4-digit-years [#307]
- Language support for Irish (ga) [#309]

### Fixed
- Merger-factories for clock-based objects of basic types too lenient [#302]
- Don't cache system timezone in  SystemClock.inLocalView()  [#305]
- Threeten-pattern uu does not work [#308]

## [v4.1] published on 2015-06-11
### Added
- Monotonic clocks (main new feature) [#295]
- Add generic ordinal weekday-in-month [#297]
- Queries for dst+raw-offsets in `Timezone` [#288]
- Language support for Belorusian (be), Hebrew (he, iw), Bosnian (bs) [#290, #292, #294]

### Fixed
- `LeapSeconds.enhance(unixTime)` does not follow spec [#296]
- Version-compatibility-bug in tzdata-1.2-2015d [#298]

## [v4.0] published on 2015-05-25
- First release of version line v4.x, represents a fork of Time4J for the sake of interoperability with Java-8.
