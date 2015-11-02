package tw.tasker.babysitter;

import org.junit.Test;

import java.util.Calendar;

import tw.tasker.babysitter.utils.DisplayUtils;

import static org.junit.Assert.assertEquals;

public class CalendarTest {

    @Test
    public void testCalendar() {
        Calendar calendar = Calendar.getInstance();
        System.out.print("calendar: " + calendar.toString());
        // calendar: java.util.GregorianCalendar[
        // time=1446445855770,
        // areFieldsSet=true,
        // areAllFieldsSet=true,
        // lenient=true,
        // zone=sun.util.calendar.ZoneInfo[
        // id="Asia/Taipei",
        // offset=28800000,
        // dstSavings=0,
        // useDaylight=false,
        // transitions=42,
        // lastRule=null],
        // firstDayOfWeek=1,
        // minimalDaysInFirstWeek=1,
        // ERA=1,
        // YEAR=2015,
        // MONTH=10,
        // WEEK_OF_YEAR=45,
        // WEEK_OF_MONTH=1,
        // DAY_OF_MONTH=2,
        // DAY_OF_YEAR=306,
        // DAY_OF_WEEK=2,
        // DAY_OF_WEEK_IN_MONTH=1,
        // AM_PM=1,
        // HOUR=2,
        // HOUR_OF_DAY=14,
        // MINUTE=30,
        // SECOND=55,
        // MILLISECOND=770,
        // ZONE_OFFSET=28800000,
        // DST_OFFSET=0]
    }

    @Test
    public void testGetCurrentAgeByBirthday()  {
        String ages = DisplayUtils.showBabyAgeByBirthday("10311");
        System.out.println(ages);
    }

    public static int perMonthDays(Calendar cal) {
        int maxDays = 0;
        int month = cal.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                maxDays = 31;
                break;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                maxDays = 30;
                break;
            case Calendar.FEBRUARY:
                if (isLeap(cal.get(Calendar.YEAR))) {
                    maxDays = 29;
                } else {
                    maxDays = 28;
                }
                break;
        }
        return maxDays;
    }

    public static boolean isLeap(int year) {
        boolean leap = false;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            leap = true;
        }
        return leap;
    }
}