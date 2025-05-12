package bd.org.quantum.hrm.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeJobStatusChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String statusDate;
    private String statusFrom;
    private String statusTo;
    private String statusRemarks;

    public EmployeeJobStatusChangeHistoryDto(StatusInfo status) {
        this.setEmployeeId(status.getEmployee().getId());
        this.statusDate = status.getDateFrom() != null ? String.valueOf(status.getDateFrom()) : "";
        this.statusFrom = EmployeeStatus.getAllStatus().get(status.getStatusFrom());
        this.statusTo = EmployeeStatus.getAllStatus().get(status.getStatusTo());
        this.statusRemarks = status.getComments() != null ? status.getComments() : "";
    }
}