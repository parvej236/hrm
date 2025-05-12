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
public class EmployeeExperienceChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String designation;
    private String institution;
    private String duration;
    private String responsibility;
    private String comments;

    public EmployeeExperienceChangeHistoryDto(ExperienceInfo experience) {
        this.setEmployeeId(experience.getEmployee().getId());
        this.designation = experience.getDesignation() != null ? experience.getDesignation() : "";
        this.institution = experience.getInstitution() != null ? experience.getInstitution() : "";
        this.duration = experience.getDuration() != null ? experience.getDuration() : "";
        this.responsibility = experience.getResponsibility() != null ? experience.getResponsibility() : "";
        this.comments = experience.getComments() != null ? experience.getComments() : "";
    }
}