package bd.org.quantum.hrm.leave;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_update_request")
public class LeaveUpdateRequest extends AuditData {

    private Long applicationId;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateFrom;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateTo;

    @OneToMany(targetEntity = LeaveDetailsOld.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "request", referencedColumnName = "id")
    private List<LeaveDetailsOld> oldDetails;

    @OneToMany(targetEntity = LeaveDetailsUpdate.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "request", referencedColumnName = "id")
    private List<LeaveDetailsUpdate> updateDetails;

    private int duration;

    private String stage;

    @Column(length = 500)
    private String remarks;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate requestedOn;

    private Long requestedBy;
    private String requestedByName;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate authorizedOn;

    private Long authorizedBy;
    private String authorizedByName;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate approvedOn;

    private Long approvedBy;
    private String approvedByName;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate rejectedOn;

    private Long rejectedBy;
    private String rejectedByName;

    @Transient
    private LeaveApplication application;

    @Transient
    private String action;
}
