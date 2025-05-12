package bd.org.quantum.hrm.employee;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum EmployeeStatus {
    REGULAR("Regular"),
    IRREGULAR("Irregular"),
    MUSTER_ROLL("Muster Roll"),
    CONTRACTUAL("Contractual"),
    PART_TIME("Part-Time"),
    HONORARY("Honorary"),
    PROBATIONARY("Probationary"),
    INTERN("Intern"),
    RESIGNED("Resigned"),
    SUSPENDED("Suspended"),
    DISMISSED("Dismissed"),
    ABSENT("Absent"),
    DECEASED("Deceased");;

    private final String name;

    EmployeeStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static final String[] REGULAR_EMPLOYEE_STATUSES = {"Regular", "Irregular"};
    public static final String[] RUNNING_CORE_EMPLOYEE_STATUSES = {"Regular", "Irregular", "Muster Roll"};
    public static final String[] TEMPORARY_EMPLOYEE_STATUSES = {"Muster Roll", "Contractual", "Part-Time", "Probationary", "Intern"};
    public static final String[] RUNNING_ALL_EMPLOYEE_STATUSES = {"Regular", "Irregular", "Muster Roll", "Contractual", "Part-Time", "Honorary", "Probationary", "Intern"};
    public static final String[] EX_EMPLOYEE_STATUSES = {"Resigned", "Suspended", "Dismissed"};

    public static final String RUNNING_EMPLOYEE_STATUSES_STR = Arrays.toString(RUNNING_ALL_EMPLOYEE_STATUSES).replace("[","").replace("]","");

    public static boolean isExStatus(String emStatus){
        boolean result = false;
        for (String exStatus: EX_EMPLOYEE_STATUSES) {
            if (exStatus.equals(emStatus)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static Map<String, String> getCurrentStatus() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put(EmployeeStatus.REGULAR.getName(), EmployeeStatus.REGULAR.getName());
        status.put(EmployeeStatus.IRREGULAR.getName(), EmployeeStatus.IRREGULAR.getName());
        status.put(EmployeeStatus.MUSTER_ROLL.getName(), EmployeeStatus.MUSTER_ROLL.getName());
        return status;
    }

    public static Map<String, String> getStatusesForIdCard() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put(EmployeeStatus.REGULAR.getName(), EmployeeStatus.REGULAR.getName());
        status.put(EmployeeStatus.IRREGULAR.getName(), EmployeeStatus.IRREGULAR.getName());
        status.put(EmployeeStatus.MUSTER_ROLL.getName(), EmployeeStatus.MUSTER_ROLL.getName());
        status.put(EmployeeStatus.PART_TIME.getName(), EmployeeStatus.PART_TIME.getName());
        status.put(EmployeeStatus.HONORARY.getName(), EmployeeStatus.HONORARY.getName());
        status.put(EmployeeStatus.PROBATIONARY.getName(), EmployeeStatus.PROBATIONARY.getName());
        return status;
    }

    public static Map<String, String> getAllStatus() {
        Map<String, String> status = new LinkedHashMap<>();
        for (EmployeeStatus type : EmployeeStatus.values()) {
            status.put(type.getName(), type.getName());
        }
        return status;
    }
}
