package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.common.AuditData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "education_info")
public class EducationInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    private String examName;
    private String passingYear;
    private String result;
    private String institution;
    private String board;
    private String subjectDepartment;

    @Column(length = 500)
    private String comments;
}
