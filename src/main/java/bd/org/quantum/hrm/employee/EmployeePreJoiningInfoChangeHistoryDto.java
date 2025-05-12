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
public class EmployeePreJoiningInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String branch;
    private String department;
    private String description;

    public EmployeePreJoiningInfoChangeHistoryDto(PreJoiningInfo prejoin) {
        this.setEmployeeId(prejoin.getEmployee().getId());
        this.branch = prejoin.getBranchName() != null ? prejoin.getBranchName() : "";
        this.department = prejoin.getDepartmentName() != null ? prejoin.getDepartmentName() : "";
        this.description = prejoin.getDescription() != null ? prejoin.getDescription() : "";
    }
}