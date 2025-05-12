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
public class EmployeeExtraCurricularInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String activitiesDescription;

    public EmployeeExtraCurricularInfoChangeHistoryDto(ExtraQualificationInfo extra) {
        this.setEmployeeId(extra.getEmployee().getId());
        this.activitiesDescription = extra.getDescription() != null ? extra.getDescription() : "";
    }
}