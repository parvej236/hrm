package bd.org.quantum.hrm.employee;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "transfer_info")
public class TransferInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate transferDate;

    private Long branchFrom;
    private Long branchTo;
    private Long departmentFrom;
    private Long departmentTo;

    private String branchFromName;
    private String branchToName;
    private String departmentFromName;
    private String departmentToName;

    @Column(length = 500)
    private String comments;
}
