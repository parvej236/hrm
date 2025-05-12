package bd.org.quantum.hrm.biometric;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bio_attendance_log")
public class BioAttendanceLog extends AuditData {

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "employee")
//    private Employee employee;

    @Column(name = "employee")
    private Long employee;
    private String name;
    private String department;
    private String designation;

    @DateTimeFormat(pattern = DateUtils.YYYY_MM_DD_HH_MM_SS)
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MM_SS)
    private Date punchedAt;
    private String punchedArea;
    private String punchedDevice;
}
