## [v3.13] not yet released
### Added
- Fixed and flexible day periods with appropriate translations [#369]
- Minguo calendar (Taiwan) [#390]
- New getters for day-of-week and day-of-year in PlainDate [#416]
- Query parsed raw data for any registered elements [#415]
- New method CalendarText.getIsoInstance(Locale) [#410]
- New methods CalendarText.patternForXYZ(...) [#410]

### Fixed
- Formatting Ethiopian time with AM/PM-marker is not in western style [#413]
- Parsing of Ethiopic tabot names broken if not at end of text [#412]

### Deprecated
- EthiopianCalendar.getYearOfEra() deprecated and renamed to getYear() [#417]
- CalendarText.getGMTPrefix [#410]
- CalendarText.getFormatPatterns() [#410]
- CalendarText.getTimestampPattern [#410]

## [v3.12] published on 2015-11-30
### Added
- Support for Bengali [#409]
- Ethiopian time should support element PlainTime.COMPONENT [#408]

### Fixed
- HijriAlgorithm calendar variant broken [#407]
- Broken javadoc links to some serial forms [#406]
- British date format should be in order DMY [#403]

### Removed
- Tidy deprecated elements [#404]

## [v3.11] published on 2015-11-22
### Added
- Support for Sinhalese [#402]
- Add Ethiopian calendar and time [#389]
- Enable formatting/parsing moments using another calendar [#399]
- GeneralTimestamp should implement ChronoDisplay [#401]
- Add Coptic calendar [#388]
- Support for triennal julian leap years [#393]

### Fixed
- Ensure that parsing raw data does not resolve anything [#400]
- Compile problem with text elements in ChronoFormatter.Builder [#398]
- plus/minus(CalendarDays) does not document any exception [#397]
- Arithmetic overflow in ChronoHistory [#391]

### Changed (updated/removed/deprecated)
- New method ChronoMerger.getDefaultStartOfDay() [#399]
- Refactor handling of two exotic format attributes [#395]
- Hide internal format attributes of ChronoHistory [#394]

## [v3.10] published on 2015-10-22
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

### Changed
- Better translations for various countries (mainly arabic and french) [#383]
- Better translations for Spanish locales in Latin America [#368]
- Low-level interface ChronoMerger with new method [#377]
- Review plural rules [#366]
- Update i18n-data to CLDR-28 [#374]
- Change ISO-8601-fallback-mechanism to CLDR standard [#373]
- FormatPatternProvider.getDateTimePattern() with 3 args [#100]

### Removed
- FormatPatternProvider.DEFAULT [#100]

## [v3.9] published on 2015-10-01
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

## [v3.8] published on 2015-09-15
### Added
- Support for Malay language [#353]
- Unify CalendarVariant and Calendrical by new interface CalendarDate [#350]
- New XOR-operator for interval collections [#347]
- Make PatternType.CLDR useable for non-iso-dates [#348]
- New calendar-related class GeneralTimestamp [#340]
- New intersect-operator for interval collections [#345]

### Fixed
- Disable parsing of ambivalent text forms [#351]

### Changed
- Rename unit pattern resource file names [#352]
- CalendarVariant and Calendrical changed Temporal signature [#350]
- Two specialized now()-methods in ZonalClock changed the return type [#340]

## [v3.7] published on 2015-09-03
### Added
- New method IntervalCollection.getRange() [#344]
- PrettyTime.printRelativeOrDate() using PlainDate [#343]
- Moment-class with a new precision element [#342]
- Add plus/minus-operators for IntervalCollection [#339]

### Fixed
- Fix for generics-related problem in PatternType [#346]

### Changed
- IntervalCollection.union() should create a merged collection [#338]

## [v3.6] published on 2015-08-31
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

## [v3.5] published on 2015-08-18
### Added
- Umalqura calendar for Saudi-Arabia (experimental) [#313]
- Start-of-day-feature, relevant for current date in islamic calendar [#323]
- New pattern type for generic date chronologies [#324]
- New transformation methods for calendrical objects [#318]

### Deprecated
- Interface `ExtZoneProvider` no longer used

### Changed
- Narrow value-type of BasicElement to Comparable [#321]

### Removed
- Android-module dropped in favour of new stand-alone project Time4A [#326]

## [v3.4] published on 2015-07-06
### Added
- New pattern type CLDR_24 with support for ISO-hour in range 0-24 [#314]
- Convenient method `printRelativeInStdTimezone(moment)` for `PrettyTime` [#311]
- Introduce calendar families with day calculations [#315]

### Fixed
- Using PrettyTime on Android - null pointer exception [#316]
- Remove unnecessary warning message in ChronoFormatter [#312]

## [v3.3] published on 2015-06-26
### Added
- Generic clock-based creation of any `ChronoEntity` [#303]
- Add clock-methods `inPlatformView()` [#304]
- Register `PlainTime.PRECISION` in `PlainTimestamp` [#306]
- Improve adjacent digit parsing of 4-digit-years [#307]
- Language support for Irish (ga) [#309]

### Fixed
- Merger-factories for clock-based objects of basic types too lenient [#302]
- Don't cache system timezone in  SystemClock.inLocalView()  [#305]

## [v3.2] published on 2015-06-15
### Added
- Add Timezone-Change-Receiver for Android [#289]
- Monotonic clocks including support for real-time on Android [#295]
- Queries for dst+raw-offsets in `Timezone` [#288]
- Language support for Belorusian (be), Hebrew (he, iw), Bosnian (bs) [#290, #292, #294]

### Fixed
- Android does not accept `ResourceBundle.Control.getFormats()` [#291]
- `LeapSeconds.enhance(unixTime)` does not follow spec [#296]
- Version-compatibility-bug in tzdata-1.2-2015d [#298]

## [v3.1] published on 2015-05-25
### Added
- Add pattern-based factory methods to `ChronoFormatter` [#273]
- Handle min/max-date for gregorian cutover better [#274]
- Enhance `ZonalOffset.parse(...)` [#275]
- Add option for parsing an alternative literal char [#278]
- Add serialization feature to `ZonalDateTime` [#285]
- Complete localized era name support [#286]
- Support latin era names in non-English languages [#287]

### Fixed
- Sectional format attributes should always be explicit [#264]
- `ChronoFormatter.toFormat()` broken with some `FieldPosition`-args [#265]
- Javadoc-Fix for `ChronoFormatter.toFormat()` [#267]
- Define and document appropriate types for `TemporalFormatter` [#271]
- `Duration` cannot process -1sec + 999_999_999ns = -1ns [#272]
- Inner/nested formatters should accept trailing characters in parsing [#279]
- Ensure change of language without side effects [#280]
- Allow 5 symbols for narrow form of quarter-of-year [#281]
- Parsing timezone-IDs is sometimes broken [#284]

### Changed
- Hidden support for `ZonalDateTime` in `Platform.PATTERN` dropped [#271]

## [v3.0] published on 2015-04-30
- First release of version line v3.x, contains a pluggable format engine and historization features.
- The older version lines v1.x and v2.x are no longer supported and have reached end of life.
