package bd.org.quantum.hrm.attendance;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import bd.org.quantum.hrm.employee.Employee;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_attendance")
public class Attendance extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate attendanceDate;

    private int attendanceType;

    private String inTime;

    private String outTime;

    private int attendanceStatus;

    private String attendanceFrom;

    @Transient
    private int cl;

    @Transient
    private int yr;
}
