package bd.org.quantum.hrm.attendance;

import bd.org.quantum.hrm.common.Department;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
public class AttendanceCriteria {
    private String employeeName;
    private String regCode;
    private String employeeId;
    private String primaryPhone;
    private long branch;
    private List<Long> branches;
    private boolean withSub;
    private long department;
    private List<Department> departments;
    private boolean includeDeptSub;
    private long designation;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    private String status;
    private List<String> statuses;
    private String gender;

    // Entry Form
    private List<AttendanceStatus> attendanceList;

    private String empDesignationStr;
    private String joinDate;
    private List<String> empIdList;
    private List<String> attendanceTypeList;
    private List<Integer> totalAttendance;
    private int total;

    private String errorMessage;
    private String action;
    private String reportType;
}
