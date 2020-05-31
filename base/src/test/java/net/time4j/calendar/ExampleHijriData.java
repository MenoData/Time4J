package net.time4j.calendar;

import net.time4j.PlainDate;

class ExampleHijriData
    implements HijriData {

    private static final int[][] LENGTHS_OF_MONTH = {
        {29, 30, 29, 29, 30, 29, 32, 29, 29, 30, 29, 29},
        {30, 29, 30, 29, 30, 29, 29, 30, 29, 30, 30, 29},
        {29, 30, 29, 31, 28, 29, 29, 30, 29, 30, 30, 30},
    };

    @Override
    public void prepare() {
        // nothing to do
    }

    @Override
    public String name() {
        return "hijri-data-test";
    }

    @Override
    public int minimumYear() {
        return 1449;
    }

    @Override
    public int maximumYear() {
        return 1451;
    }

    @Override
    public PlainDate firstGregorianDate() {
        return PlainDate.of(2028, 5, 20); // not used
    }

    @Override
    public int lengthOfMonth(int hijriYear, int hijriMonth) {
        return LENGTHS_OF_MONTH[hijriYear - 1449][hijriMonth - 1];
    }

}
