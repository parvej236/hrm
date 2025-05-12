package bd.org.quantum.hrm.biometric;

import bd.org.quantum.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BioAttendanceSearchCriteria {
    private Long branch;
    private List<Long> branches;

    private Long department;
    private List<Long> departments;

    private String designation;

    private Long employee;
    private String employeeName;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate attendanceFrom;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate attendanceTo;
}
