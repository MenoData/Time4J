
## [Unreleased] published on ?
- Print relative times with abbreviations [#327]

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
