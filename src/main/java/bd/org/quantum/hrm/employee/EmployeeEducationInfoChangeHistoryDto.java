package bd.org.quantum.hrm.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeEducationInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String examName;
    private String passingYear;
    private String result;
    private String institution;
    private String board;
    private String department;
    private String comments;

    public EmployeeEducationInfoChangeHistoryDto(EducationInfo education) {
        this.setEmployeeId(education.getEmployee().getId());
        this.examName = education.getExamName() != null ? education.getExamName() : "";
        this.passingYear = education.getPassingYear() != null ? education.getPassingYear() : "";
        this.result = education.getResult() != null ? education.getResult() : "";
        this.institution = education.getInstitution() != null ? education.getInstitution() : "";
        this.board = education.getBoard() != null ? education.getBoard() : "";
        this.department = education.getSubjectDepartment() != null ? education.getSubjectDepartment() : "";
        this.comments = education.getComments() != null ? education.getComments() : "";
    }
}