package bd.org.quantum.hrm.report;

import bd.org.quantum.common.utils.Misc;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ReportType {
    EMPLOYEE_LIST,
    EMPLOYEE_PHONE_LIST,
    EMPLOYEE_LIST_DETAILS,
    EMPLOYEE_STATEMENT_MONTHLY,
    EMPLOYEE_STATEMENT_DESIGNATION_WISE,

    ATTENDANCE_SHEET,
    ATTENDANCE_SUMMARY,

    LEAVE_REPORT;

    public static Map<String, String> getEmployeeReportTypeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ReportType.EMPLOYEE_LIST.name(), Misc.getReadableName(ReportType.EMPLOYEE_LIST.name()));
        map.put(ReportType.EMPLOYEE_PHONE_LIST.name(), Misc.getReadableName(ReportType.EMPLOYEE_PHONE_LIST.name()));
        map.put(ReportType.EMPLOYEE_LIST_DETAILS.name(), Misc.getReadableName(ReportType.EMPLOYEE_LIST_DETAILS.name()));
        map.put(ReportType.EMPLOYEE_STATEMENT_MONTHLY.name(), Misc.getReadableName(ReportType.EMPLOYEE_STATEMENT_MONTHLY.name()));
        map.put(ReportType.EMPLOYEE_STATEMENT_DESIGNATION_WISE.name(), Misc.getReadableName(ReportType.EMPLOYEE_STATEMENT_DESIGNATION_WISE.name()));
        return map;
    }

    public static Map<String, String> getAttendanceReportTypeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ReportType.ATTENDANCE_SHEET.name(), Misc.getReadableName(ReportType.ATTENDANCE_SHEET.name()));
        map.put(ReportType.ATTENDANCE_SUMMARY.name(), Misc.getReadableName(ReportType.ATTENDANCE_SUMMARY.name()));
        return map;
    }

    public static Map<String, String> getLeaveReportTypeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ReportType.LEAVE_REPORT.name(), Misc.getReadableName(ReportType.LEAVE_REPORT.name()));
        return map;
    }
}
