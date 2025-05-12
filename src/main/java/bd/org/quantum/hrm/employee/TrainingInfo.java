package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.common.AuditData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "professional_info")
public class TrainingInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    private String courseName;
    private String contents;
    private String duration;
    private String completedYear;
    private String certification;

    @Column(length = 500)
    private String comments;
}
