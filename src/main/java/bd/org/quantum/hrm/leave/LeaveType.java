package bd.org.quantum.hrm.leave;

import bd.org.quantum.hrm.common.Gender;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum LeaveType {
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

    LeaveType(int value, String naturalName, String shortName, String leaveCount) {
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

    public static Map<Integer, String> getAllLeaveTypes() {
        Map<Integer, String> leaveType = new LinkedHashMap<>();
        for (LeaveType type : LeaveType.values()) {
            if (type.getValue() != LeaveType.MEDICAL_LEAVE.getValue()) {
                leaveType.put(type.getValue(), type.getNaturalName());
            }
        }
        return leaveType;
    }

    public static Map<Integer, String> getAllShortLeaveTypes() {
        Map<Integer, String> leaveType = new LinkedHashMap<>();
        for (LeaveType type : LeaveType.values()) {
            if (type.getValue() == LeaveType.SPECIAL_LEAVE.getValue()) {
                leaveType.put(type.getValue(), "Applied SL");
            } else {
                if (type.getValue() != LeaveType.MEDICAL_LEAVE.getValue()) {
                    leaveType.put(type.getValue(), type.getShortName());
                }
            }
        }
        return leaveType;
    }

    public static Map<Integer, String> getLeaveTypes(String gender) {
        Map<Integer, String> map = new LinkedHashMap<>();

        map.put(WEEKLY_PREPARATION_DAY.value, WEEKLY_PREPARATION_DAY.shortName);
        map.put(CASUAL_LEAVE.value, CASUAL_LEAVE.shortName);
        map.put(YEARLY_REJUVENATION.value, YEARLY_REJUVENATION.shortName);
        map.put(FESTIVAL_LEAVE.value, FESTIVAL_LEAVE.shortName);
        if (gender != null && gender.equals(Gender.FEMALE.getName())) {
            map.put(MATERNITY_LEAVE.value, MATERNITY_LEAVE.shortName);
        }
        map.put(SPECIAL_LEAVE.value, SPECIAL_LEAVE.shortName);
        map.put(WITHOUT_PAY_LEAVE.value, WITHOUT_PAY_LEAVE.shortName);

        return map;
    }
}
