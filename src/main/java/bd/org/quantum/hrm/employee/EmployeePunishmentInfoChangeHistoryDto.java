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
public class EmployeePunishmentInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String showCauseDate;
    private String showCauseDescription;
    private String punishmentDate;
    private String punishmentDescription;

    public EmployeePunishmentInfoChangeHistoryDto(PunishmentInfo punishment) {
        this.setEmployeeId(punishment.getEmployee().getId());
        this.showCauseDate = punishment.getShowCauseDate() != null ? String.valueOf(punishment.getShowCauseDate()) : "";
        this.showCauseDescription = punishment.getShowCauseDescription() != null ? punishment.getShowCauseDescription() : "";
        this.punishmentDate = punishment.getActionDate() != null ? String.valueOf(punishment.getActionDate()) : "";
        this.punishmentDescription = punishment.getActionDescription() != null ? punishment.getActionDescription() : "";
    }
}