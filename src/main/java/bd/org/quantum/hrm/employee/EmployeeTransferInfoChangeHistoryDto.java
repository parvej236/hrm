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
public class EmployeeTransferInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String transferDate;
    private String oldBranch;
    private String oldDepartment;
    private String newBranch;
    private String newDepartment;
    private String comments;

    public EmployeeTransferInfoChangeHistoryDto(TransferInfo transfer) {
        this.setEmployeeId(transfer.getEmployee().getId());
        this.transferDate = transfer.getTransferDate() != null ? String.valueOf(transfer.getTransferDate()) : "";
        this.oldBranch = transfer.getBranchFromName() != null ? transfer.getBranchFromName() : "";
        this.newBranch = transfer.getBranchToName() != null ? transfer.getBranchToName() : "";
        this.oldDepartment = transfer.getDepartmentFromName() != null ? transfer.getDepartmentFromName() : "";
        this.newDepartment = transfer.getDepartmentToName() != null ? transfer.getDepartmentToName() : "";
        this.comments = transfer.getComments() != null ? transfer.getComments() : "";
    }
}