package net.time4j.calendar;

import java.util.Arrays;

import net.time4j.PlainDate;
import net.time4j.base.MathUtils;
import net.time4j.engine.EpochDays;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricDate;
import net.time4j.history.HistoricEra;


public enum HijriAlgorithm {
	
	EAST_ISLAMIC_CIVIL(new int[] {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29}, true),
	
	EAST_ISLAMIC_ASTRO(new int[] {2, 5, 7, 10, 13, 15, 18, 21, 24, 26, 29}, false),

	WEST_ISLAMIC_CIVIL(new int[] {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29}, true),
	
	WEST_ISLAMIC_ASTRO(new int[] {2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29}, false),
	
	FATIMID_CIVIL(new int[] {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29}, true),
	
	FATIMID_ASTRO(new int[] {2, 5, 8, 10, 13, 16, 19, 21, 24, 27, 29}, false),
	
	HABASH_AL_HASIB_CIVIL(new int[] {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30}, true),
	
	HABASH_AL_HASIB_ASTRO(new int[] {2, 5, 8, 11, 13, 16, 19, 21, 24, 27, 30}, false);
	
	private static final long LENGTH_OF_30_YEAR_CYCLE; 
	private static final long START_622_07_15;
	
	static {
		LENGTH_OF_30_YEAR_CYCLE = 30 * 354 + 11; 
		HistoricDate date = HistoricDate.of(HistoricEra.AD, 622, 7, 15); // Thursday epoch
		START_622_07_15 = ChronoHistory.PROLEPTIC_JULIAN.convert(date).get(EpochDays.UTC);
	}
	
	private final transient int[] intercalaries;
	private final transient boolean civil;
	
	private HijriAlgorithm(
		int[] intercalaries, 
		boolean civil
	) {
		this.intercalaries = intercalaries;
		this.civil = civil;
	}

	public static void main(String... args) {
		System.out.println(HijriAlgorithm.EAST_ISLAMIC_CIVIL.toGregorian(1436, 10, 18));
		System.out.println(HijriAlgorithm.EAST_ISLAMIC_ASTRO.toGregorian(1436, 10, 18));
		
		HijriAlgorithm.EAST_ISLAMIC_CIVIL.toHijri(PlainDate.of(2015, 8, 4));
		HijriAlgorithm.EAST_ISLAMIC_ASTRO.toHijri(PlainDate.of(2015, 8, 4));
	}
	
	private PlainDate toGregorian(int hyear, int hmonth, int hdom) {
		if (hyear < 1 || hmonth < 1 || hmonth > 12 || hdom < 1 || hdom > 30) {
			throw new IllegalArgumentException("Out of bounds.");
		}
		
		long days = ((hyear - 1) / 30) * LENGTH_OF_30_YEAR_CYCLE;
		int y = ((hyear - 1) % 30) + 1;
		
		for (int i = 1; i < y; i++) {
			if (Arrays.binarySearch(this.intercalaries, i) >= 0) {
				days += 355;
			} else {
				days += 354;
			}
		}
		
		for (int i = 1; i < hmonth; i++) {
			if ((i % 2) == 0) {
				days += 29;
			} else {
				days += 30;
			}
		}
		
		if (hdom == 30) {
			if (
				(hmonth == 12 && Arrays.binarySearch(this.intercalaries, y) < 0)
				|| ((hmonth % 2) == 0)
			) {
				throw new IllegalArgumentException("Invalid day-of-month: " + hdom);
			}
		}
		
		days += hdom;
		long start = START_622_07_15;
		
		if (this.civil) {
			start++;
		}
		
		return PlainDate.of(start + days - 1, EpochDays.UTC);
	}
	
	private void toHijri(PlainDate date) {
		long utc = date.get(EpochDays.UTC);
		long start = START_622_07_15;
		
		if (this.civil) {
			start++;
		}
		
		if (utc < start) {
			throw new IllegalArgumentException("Out of bounds: " + date);
		}
		
		long days = utc - start;
		
		int hyear = 1;
		int hmonth = 1;
		int hdom = 1;
		
		hyear += MathUtils.safeCast((days / LENGTH_OF_30_YEAR_CYCLE) * 30);
		int delta = (int) (days % LENGTH_OF_30_YEAR_CYCLE);
		
		for (int i = 1; i < 30; i++) {
			int ylen = 354;
			if (Arrays.binarySearch(this.intercalaries, i) >= 0) {
				ylen++;
			}
			if (delta > ylen) {
				delta -= ylen;
				hyear++;
			} else {
				break;
			}
		}
		
		for (int i = 1; i < 12; i++) {
			int mlen = 30;
			if ((i % 2) == 0) {
				mlen = 29;
			}
			if (delta > mlen) {
				delta -= mlen;
				hmonth++;
			} else {
				break;
			}
		}
		
		hdom += delta;
		
		System.out.println("Hijri=" + hyear + "-" + hmonth + "-" + hdom);		
	}
	
}
