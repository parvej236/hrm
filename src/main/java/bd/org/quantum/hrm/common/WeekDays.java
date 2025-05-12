package bd.org.quantum.hrm.common;

import java.util.LinkedHashMap;
import java.util.Map;

public enum WeekDays {
    SATURDAY("Saturday"),
    SUNDAY("Sunday"),
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday");

    private String day;

    WeekDays(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public static Map<String, String> getAllWeekDay() {
        Map<String, String> weekDayMap = new LinkedHashMap<>();
        for (WeekDays days : WeekDays.values()) {
            weekDayMap.put(days.getDay(), days.getDay());
        }
        return weekDayMap;
    }
}
