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
public class EmployeeKhedmotaionInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String branch;
    private String department;
    private String description;

    public EmployeeKhedmotaionInfoChangeHistoryDto(KhedmotaionInfo khedmotaion) {
        this.setEmployeeId(khedmotaion.getEmployee().getId());
        this.branch = khedmotaion.getBranchName() != null ? khedmotaion.getBranchName() : "";
        this.department = khedmotaion.getDepartmentName() != null ? khedmotaion.getDepartmentName() : "";
        this.description = khedmotaion.getDescription() != null ? khedmotaion.getDescription() : "";
    }
}