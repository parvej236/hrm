package bd.org.quantum.hrm.attendance;

import bd.org.quantum.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class AttendanceDateStatus {
    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate attendanceDate;
    private long attendanceId;
    private Integer attendanceType;
    private int status;
    private int clBalance;
    private int yrBalance;
}
