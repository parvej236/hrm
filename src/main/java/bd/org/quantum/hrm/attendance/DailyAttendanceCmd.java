package bd.org.quantum.hrm.attendance;

import bd.org.quantum.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DailyAttendanceCmd {
    private String name;
    private String designation;
    private String inTime;
    private String outTime;
    private String title;

    private long branch;
    private long department;

    private String branchName;
    private String departmentName;

    private int timely;
    private int delay;
    private int movement;
    private int leave;
    private int absent;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate attendanceDate;

    private boolean includeDeptSub;
}
