package bd.org.quantum.hrm.leave;

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
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leave_application")
public class LeaveApplication extends AuditData {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant")
    private Employee applicant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsible")
    private Employee responsible;

    @OneToMany(targetEntity = LeaveDetails.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "application", referencedColumnName = "id")
    private List<LeaveDetails> leaveDetails;

    @Column(length = 500)
    private String leaveReason;

    @Column(length = 500)
    private String remarks;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate applyDate;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate leaveFrom;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate leaveTo;

    private Long department;
    private Long branch;
    private int leaveDays;
    private String alternateContactName;
    private String alternateContactPhone;
    private String relationWith;
    private String stage;
    private Long appliedBy;
    private String appliedByName;
    private Long updatedBy;
    private String updatedByName;
    private Long authorizedBy;
    private String authorizedByName;
    private Long approvedBy;
    private String approvedByName;
    private Long confirmBy;
    private String confirmByName;
    @Transient
    private String action;
}


