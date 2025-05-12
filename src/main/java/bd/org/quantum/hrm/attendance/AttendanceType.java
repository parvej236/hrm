package bd.org.quantum.hrm.attendance;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum AttendanceType {

    PRESENT(1,"Present", "P", "-"),
    WEEKLY_PREPARATION_DAY(3, "Weekly Preparation Day", "WPD", "-"),
    CASUAL_LEAVE(5, "Casual Leave", "CL", "-"),
    YEARLY_REJUVENATION(7, "Yearly Rejuvenation", "YR", "-"),
    MEDICAL_LEAVE(9, "Medical Leave", "ML", "-"),
    MATERNITY_LEAVE(11, "Maternity Leave", "Mt. L", "-"),
    FESTIVAL_LEAVE(13, "Festival Leave", "FL", "-"),
    SPECIAL_LEAVE(15, "Special Leave", "SL", "-"),
    WITHOUT_PAY_LEAVE(17, "Leave Without Pay", "LWP", "-"),
    ABSENT_LEAVE(19, "Absent", "A", "-"),
    OFFICE_DUTY(21,"Office Duty", "OD", "On duty out of desk"),
    TRAINING(23,"Training", "T", "-"),
    ON_TRAINING_PROGRAM(25, "On Training Program", "OTP", "Duty on Quantum Method Course"),
    NOT_APPLICABLE(27, "Not Applicable", "N/A", "-");

    private final int value;
    private final String naturalName;
    private final String shortName;
    private final String leaveCount;

    AttendanceType(int value, String naturalName, String shortName, String leaveCount) {
        this.value = value;
        this.naturalName = naturalName;
        this.shortName = shortName;
        this.leaveCount = leaveCount;
    }

    public static final int[] OD_OTP_TRAINING = {21, 23, 25};
    public static final int[] OTHER_LEAVE = {9, 11, 13, 15};

    public static final String OD_OTP_TRAINING_STR = Arrays.toString(OD_OTP_TRAINING).replace("[","").replace("]","");
    public static final String OTHER_LEAVE_STR = Arrays.toString(OTHER_LEAVE).replace("[","").replace("]","");

    public int getValue() {
        return value;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLeaveCount() {
        return leaveCount;
    }

    public static Map<Integer, String> getAllAttendanceTypes() {
        Map<Integer, String> attendanceType = new LinkedHashMap<Integer, String>();
        for (AttendanceType type : AttendanceType.values()) {
            if (type.getValue() != AttendanceType.MEDICAL_LEAVE.getValue()) {
                attendanceType.put(type.getValue(), type.getNaturalName());
            }
        }
        return attendanceType;
    }

    public static Map<Integer, String> getAllShortAttendanceTypes() {
        Map<Integer, String> attendanceType = new LinkedHashMap<Integer, String>();
        for (AttendanceType type : AttendanceType.values()) {
            if (type.getValue() != AttendanceType.MEDICAL_LEAVE.getValue()) {
                attendanceType.put(type.getValue(), type.getShortName());
            }
        }
        return attendanceType;
    }

    public static Map<Integer, String> getLeaveTypes( int gender) {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();

        map.put(WEEKLY_PREPARATION_DAY.value, WEEKLY_PREPARATION_DAY.shortName);
        map.put(CASUAL_LEAVE.value, CASUAL_LEAVE.shortName);
        map.put(YEARLY_REJUVENATION.value, YEARLY_REJUVENATION.shortName);
        map.put(FESTIVAL_LEAVE.value, FESTIVAL_LEAVE.shortName);
        if (gender == 2) {
            map.put(MATERNITY_LEAVE.value, MATERNITY_LEAVE.shortName);
        }
        map.put(SPECIAL_LEAVE.value, SPECIAL_LEAVE.shortName);
        map.put(WITHOUT_PAY_LEAVE.value, WITHOUT_PAY_LEAVE.shortName);

        return map;
    }

    public static Map<Integer, String> getAttendanceTypeMap() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (AttendanceType attendanceType : AttendanceType.values()) {
            map.put(attendanceType.getValue(), attendanceType.getShortName());
        }
        return map;
    }
}
