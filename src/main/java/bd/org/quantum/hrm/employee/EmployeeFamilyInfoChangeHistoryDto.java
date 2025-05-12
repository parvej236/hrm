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
public class EmployeeFamilyInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String memberName;
    private String relation;
    private String birthDate;
    private String phone;
    private String comments;

    public EmployeeFamilyInfoChangeHistoryDto(FamilyInfo family) {
        this.setEmployeeId(family.getEmployee().getId());
        this.memberName = family.getName() != null ? family.getName() : "";
        this.relation = family.getRelation() != null ? family.getRelation() : "";
        this.birthDate = family.getBirthDate() != null ? String.valueOf(family.getBirthDate()) : "";
        this.phone = family.getPhone() != null ? family.getPhone() : "";
        this.comments = family.getComments() != null ? family.getComments() : "";
    }
}