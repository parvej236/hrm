package bd.org.quantum.hrm.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permissions {
    EMPLOYEE_VIEW("Can View"),
    EMPLOYEE_CREATE("Can Create"),
    EMPLOYEE_UPDATE("Can Update"),
    EMPLOYEE_UPDATE_ADVANCED("Advanced Update"),
    EMPLOYEE_REPORT_GENERAL("General Report"),
    EMPLOYEE_REPORT_ADVANCED("Advanced Report"),
    EMPLOYEE_DESIGNATION("Manage Designation"),
    EMPLOYEE_HR_ADMIN("HR Admin"),
    EMPLOYEE_HR_PERSONNEL("HR Personnel"),

    EMPLOYEE_BRANCH_ACCESS("Can Branch Access"),
    EMPLOYEE_BRANCH_INTER_ACCESS("Can Inter Branch Access"),
    EMPLOYEE_DEPARTMENT_INTER_ACCESS("Can Inter Department Access"),

    ID_CARD_ORDER("ID Card Order"),
    ID_CARD_PROCESS("ID Card Process"),

    ATTENDANCE_VIEW("Can View"),
    ATTENDANCE_CREATE("Can Create"),
    ATTENDANCE_UPDATE("Can Update"),
    ATTENDANCE_APPROVE("Can Approve"),
    ATTENDANCE_REPORT("Attendance Report"),

    LEAVE_VIEW("Can View"),
    LEAVE_APPLY("Can Apply"),
    LEAVE_LIST("Can View List"),
    LEAVE_AUTHORIZE("Can Authorize"),
    LEAVE_APPROVE("Can Approve"),
    LEAVE_CONFIRM("Can Confirm"),
    LEAVE_REPORT("Leave Report"),
    LEAVE_YR_CL("Manage YR CL");

    private String title;
}
