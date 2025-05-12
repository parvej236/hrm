package bd.org.quantum.hrm.leave;

import bd.org.quantum.common.utils.DateUtils;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leave_details_old")
public class LeaveDetailsOld {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column(length = 500)
    private String remarks;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateFrom;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateTo;

    private Integer days;
    private int leaveType;
}
