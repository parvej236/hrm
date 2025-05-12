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
public class EmployeeLanguageSkillChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String languageName;
    private String description;

    public EmployeeLanguageSkillChangeHistoryDto(LanguageSkillInfo language) {
        this.setEmployeeId(language.getEmployee().getId());
        this.languageName = language.getLanguageName() != null ? language.getLanguageName() : "";
        this.description = language.getDescription() != null ? language.getDescription() : "";
    }
}