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
public class EmployeeTrainingInfoChangeHistoryDto {

    @JsonIgnore
    private long employeeId;
    private String courseName;
    private String contents;
    private String duration;
    private String completedYear;
    private String certification;
    private String comments;


    public EmployeeTrainingInfoChangeHistoryDto(TrainingInfo training) {
        this.setEmployeeId(training.getEmployee().getId());
        this.courseName =  training.getCourseName() != null ? training.getCourseName() : "";
        this.contents =  training.getContents() != null ? training.getContents() : "";
        this.duration =  training.getDuration() != null ? training.getDuration() : "";
        this.completedYear =  training.getCompletedYear() != null ? training.getCompletedYear() : "";
        this.certification =  training.getCertification() != null ? training.getCertification() : "";
        this.comments =  training.getComments() != null ? training.getComments() : "";
    }
}